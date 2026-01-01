package com.obee.base.support;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.obee.base.enums.RowStatus;
import com.obee.base.model.*;
import com.obee.base.service.BaseDynamicService;
import com.obee.base.service.BaseSingleService;
import com.obee.base.service.ChildDataHook;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils; // Spring 反射工具

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/29 22:06
 */
@Slf4j
public abstract class BaseDynamicServiceImpl<M extends BaseEntity>
        extends BaseSingleServiceImpl<M>
        implements BaseDynamicService<M> {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationContext applicationContext; // 用于获取 Bean

    // 缓存解析后的处理器
    private final Map<String, ChildHandler> childHandlerMap = new HashMap<>();

    /**
     * 钩子注册表
     * Key: 子表标识 (如 "items")
     * Value: ChildDataHook 实现
     */
    private final Map<String, ChildDataHook<?>> hookRegistry = new HashMap<>();

    /**
     * 【供子类调用】注册子表钩子
     * 通常在 @PostConstruct 或 构造函数 中调用
     */
    protected <E> void registerChildHook(String key, ChildDataHook<E> hook) {
        hookRegistry.put(key, hook);
    }

    /**
     * 初始化：解析注解，组装处理器
     */
    @PostConstruct
    public void initChildHandlers() {
        // 1. 获取类上的注解 (支持代理类)
        ModuleInfo info = AnnotationUtils.findAnnotation(this.getClass(), ModuleInfo.class);
        if (info == null || info.children().length == 0) {
            return;
        }

        // 2. 遍历注解配置
        for (ChildModule childConfig : info.children()) {
            try {
                // 从 Spring 容器获取 Service Bean
                BaseSingleService<?> serviceBean = applicationContext.getBean(childConfig.service());

                // 构建 Handler
                ChildHandler handler = new ChildHandler(
                        childConfig.entity(),
                        serviceBean,
                        childConfig.fkField()
                );

                childHandlerMap.put(childConfig.key(), handler);
                log.info("动态加载子表配置: Key={}, Entity={}, Service={}",childConfig.key(), childConfig.entity().getSimpleName(), childConfig.service().getSimpleName());

            } catch (Exception e) {
                log.error("加载子表配置失败: Key=" + childConfig.key(), e);
                // 根据需要决定是否抛出异常中断启动
            }
        }
    }

    /**
     * 对外暴露的动态保存接口
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateDynamic(UniversalDTO<M> dto) {
        // 1. 保存主表
//        if (!this.modify(dto.getMaster())) return false;

        Map<String, List<JsonNode>> slavesMap = dto.getSlaves();
        if (slavesMap == null || slavesMap.isEmpty()) return true;

        // 2. 遍历前端传来的 Map
        for (Map.Entry<String, List<JsonNode>> entry : slavesMap.entrySet()) {
            String key = entry.getKey();
            List<JsonNode> nodes = entry.getValue();

            // 直接从缓存取 Handler，无需子类实现 getChildHandlers()
            ChildHandler handler = childHandlerMap.get(key);

            if (handler != null) {
//                processOneChildTable2(dto.getMaster().getId(), nodes, handler);
                processOneChildTable(dto.getMaster().getId(), nodes, handler,key);
            } else {
                log.warn("收到未配置的子表Key: {}, 请检查 @ModuleInfo 配置", key);
            }
        }
        return true;
    }

    /**
     * 通用单表处理逻辑 (反射版)
     */
    private void processOneChildTable2(Long masterId, List<JsonNode> nodes, ChildHandler handler) {
        if (nodes == null || nodes.isEmpty()) return;

        List<Long> deleteIds = new ArrayList<>();
        List<BaseEntity> saveOrUpdateList = new ArrayList<>();

        // 获取 MP 的表元数据，用于将 Java字段名(orderId) 转为 DB列名(order_id)
        TableInfo tableInfo = TableInfoHelper.getTableInfo(handler.entityClass());
        String fkColumnName = null;
        if (tableInfo != null) {
            // 这里简单假设 fkFieldName 直接对应属性名，实际可以用 tableInfo 去查
            fkColumnName = StringUtils.camelToUnderline(handler.fkFieldName());
        }

        for (JsonNode node : nodes) {
            try {
                // A. 反序列化
                BaseEntity entity = objectMapper.treeToValue(node, handler.entityClass());

                // B. 动态设置外键 (核心反射逻辑)
                // 利用 Spring 的 BeanUtils 查找 setter 方法
                Method setter = BeanUtils.getPropertyDescriptor(handler.entityClass(), handler.fkFieldName()).getWriteMethod();
                if (setter != null) {
                    setter.invoke(entity, masterId);
                } else {
                    log.error("在实体 {} 中找不到外键字段 {} 的Setter方法", handler.entityClass().getName(), handler.fkFieldName());
                }

                // C. 分流
                if (entity.getRowStatus() == RowStatus.DELETED) {
                    if (entity.getId() != null) deleteIds.add(entity.getId());
                } else if (entity.getRowStatus() != RowStatus.UNCHANGED) {
                    saveOrUpdateList.add(entity);
                }

            } catch (Exception e) {
                throw new RuntimeException("处理子表数据异常: " + handler.entityClass().getName(), e);
            }
        }

        // D. 批量执行
        if (!deleteIds.isEmpty()) {
            QueryWrapper<BaseEntity> qw = new QueryWrapper<>();
            qw.in("id", deleteIds);
            // 安全防御：如果能解析出数据库列名，就加上 master_id 限制
            if (fkColumnName != null) {
                qw.eq(fkColumnName, masterId);
            }
            log.info("remove>{}", qw);
            //handler.service().remove(qw);
        }

        if (!saveOrUpdateList.isEmpty()) {
            log.info("saveOrUpdateBatch>{}", saveOrUpdateList);
//            handler.service().saveOrUpdateBatch(saveOrUpdateList);
        }
    }

    private <E extends BaseEntity> void processOneChildTable(Long masterId, List<JsonNode> nodes, ChildHandler handler, String key) {
        if (nodes == null || nodes.isEmpty()) return;

        ChildDataHook<E> hook = (ChildDataHook<E>) hookRegistry.get(key);
        BaseSingleService<E> service = handler.service();

        // 临时容器
        List<Long> idsToDelete = new ArrayList<>();
        List<E> entitiesToInsert = new ArrayList<>();
        List<E> entitiesToUpdate = new ArrayList<>();

        // ==========================================
        // 1. 解析 & 前置钩子 (Before Hooks)
        // ==========================================
        for (JsonNode node : nodes) {
            try {
                E entity = objectMapper.treeToValue(node, (Class<E>) handler.entityClass());

                // 反射设置外键 (这里假设已有缓存好的 Setter MethodHandle，性能极高)
                // handler.fkSetter().invoke(entity, masterId);
                // 为演示简单，这里假装调用了 setter
//                invokeFkSetter(entity, masterId, handler);

                switch (entity.getRowStatus()) {
                    case DELETED -> {
                        if (entity.getId() != null) {
                            if (hook != null) hook.beforeDelete(entity.getId());
                            idsToDelete.add(entity.getId());
                        }
                    }
                    case NEW -> {
                        if (hook != null) hook.beforeAdd(entity);
                        entitiesToInsert.add(entity);
                    }
                    case UPDATED -> {
                        if (hook != null) hook.beforeUpdate(entity);
                        entitiesToUpdate.add(entity);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("处理子表数据异常: " + key, e);
            }
        }

        // ==========================================
        // 2. 执行数据库操作 (Batch DB Operations)
        // ==========================================

//        // A. 批量删除
//        if (!idsToDelete.isEmpty()) {
//            service.removeByIds(idsToDelete);
//        }
//
//        // B. 批量新增 (MyBatis-Plus 会自动回填 ID)
//        if (!entitiesToInsert.isEmpty()) {
//            service.saveBatch(entitiesToInsert);
//        }
//
//        // C. 批量更新
//        if (!entitiesToUpdate.isEmpty()) {
//            service.updateBatchById(entitiesToUpdate);
//        }

        // ==========================================
        // 3. 后置钩子 (After Hooks)
        // ==========================================
        if (hook != null) {
            // A. Delete After
            for (Long id : idsToDelete) {
                hook.afterDelete(id);
            }
            // B. Add After (此时 entity.getId() 已经有值了)
            for (E entity : entitiesToInsert) {
                hook.afterAdd(entity);
            }
            // C. Update After
            for (E entity : entitiesToUpdate) {
                hook.afterUpdate(entity);
            }
        }
    }


    public void check(String sheetId){

    }

}
