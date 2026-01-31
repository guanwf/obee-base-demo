package com.obee.base.support;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.obee.base.enums.RowStatus;
import com.obee.base.model.BaseEntity;
import com.obee.base.service.BaseSingleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/23 22:05
 */
@Slf4j
public abstract class BaseSingleServiceImpl<T extends BaseEntity>
//        extends ServiceImpl<M, T>
        implements BaseSingleService<T> {

    // ================== 扩展钩子 (Hooks) ==================
    // 采用 protected 允许子类覆盖，但对外不可见

    protected void beforeCreate(T entity) {
        log.info("BaseSingle.beforeAdd");
    }

    protected void afterCreate(T entity) {
        log.info("BaseSingle.afterCreate");
    }

    protected void beforeModify(T entity) {
    }

    protected void afterModify(T entity) {
    }

    protected void beforeRemove(Long id) {
    }

    protected void afterRemove(Long id) {
    }

    // ================== 接口实现 ==================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(T entity) {
        beforeCreate(entity);
        // super.save() 是 ServiceImpl 提供的方法
        boolean result = true;//super.save(entity);
        if (result) {
            afterCreate(entity);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean modify(T entity) {
        beforeModify(entity);
        // super.updateById() 是 ServiceImpl 提供的方法
        boolean result = true;//super.updateById(entity);
        if (result) {
            afterModify(entity);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean remove(Long id) {
        beforeRemove(id);
        boolean result = true;//super.removeById(id);
        if (result) {
            afterRemove(id);
        }
        return result;
    }

    @Override
    public T get(Long id) {
        return null;//super.getById(id);
    }

    @Override
    public IPage<T> page(long current, long size, T condition) {
        Page<T> page = new Page<>(current, size);
        QueryWrapper<T> wrapper = new QueryWrapper<>(condition);
        // 默认倒序，防止全表无序
        wrapper.orderByDesc("create_time");
        return null;//super.page(page, wrapper);
    }

    @Override
    public List<T> list(T condition) {
        QueryWrapper<T> wrapper = new QueryWrapper<>(condition);
        wrapper.orderByDesc("create_time");
        return null;//super.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启事务
    public boolean save(T[] rows) {
        if (rows == null || rows.length == 0) {
            return false;
        }

        // 1. 准备数据容器
        List<T> addList = new ArrayList<>();
        List<T> updateList = new ArrayList<>();
        List<T> deleteList = new ArrayList<>();

        try {
            // === 生命周期：全局保存前置 ===
            this.beforeBatchSave(rows);

            // 2. 数据分类 (校验并填充默认状态)
            for (T row : rows) {
                RowStatus status = row.getRowStatus();
                // 容错：如果未传状态，根据ID判断
                if (status == null) {
                    status = (row.getId() == null) ? RowStatus.NEW : RowStatus.UPDATED;
                    row.setRowStatus(status);
                }

                switch (status) {
                    case NEW:
                        addList.add(row);
                        break;
                    case UPDATED:
                        updateList.add(row);
                        break;
                    case DELETED:
                        deleteList.add(row);
                        break;
                    default:
                        break;
                }
            }

            // 3. 执行处理 (建议顺序：删 -> 改 -> 增，避免唯一索引冲突)

            // --- 处理删除 ---
            if (!deleteList.isEmpty()) {
                for (T row : deleteList) {
                    this.beforeDelete(row); // 钩子
//                    this.doDelete(row);     // 实际DB操作
                    this.afterDelete(row);  // 钩子
                }
            }

            // --- 处理修改 ---
            if (!updateList.isEmpty()) {
                for (T row : updateList) {
                    this.beforeUpdate(row);
//                    this.doUpdate(row);
                    this.afterUpdate(row);
                }
            }

            // --- 处理新增 ---
            if (!addList.isEmpty()) {
                for (T row : addList) {
                    this.beforeAdd(row);
//                    this.doAdd(row);
                    this.afterAdd(row);
                }
            }

            // === 生命周期：全局保存后置 ===
            this.afterBatchSave(rows, addList, updateList, deleteList);

            return true;

        } catch (Exception e) {
            // === 生命周期：异常处理 ===
            this.onException(e, rows);
            // 重要：必须抛出异常，Spring事务管理器才能捕获并回滚！
            // 如果这里吞掉了异常，事务将提交，导致数据不一致。
            throw e;
        }
    }

    // ==========================================
    // 核心数据库操作 (由子类实现，或在此处注入DAO通用实现)
    // ==========================================
//    protected abstract void doAdd(T entity);
//
//    protected abstract void doUpdate(T entity);
//
//    protected abstract void doDelete(T entity);

    // ==========================================
    // 扩展钩子方法 (子类按需重写，默认空实现)
    // ==========================================

    /**
     * 批处理开始前（例如：权限校验、参数整体清洗）
     */
    protected void beforeBatchSave(T[] allRows) {
    }

    /**
     * 批处理结束后（例如：记录操作日志、发送MQ消息、刷新缓存）
     */
    protected void afterBatchSave(T[] allRows, List<T> adds, List<T> updates, List<T> deletes) {
    }

    /**
     * 单条新增前（例如：填充创建时间、生成编码、校验必填项）
     */
    protected void beforeAdd(T entity) {
    }

    protected void afterAdd(T entity) {
    }

    /**
     * 单条修改前（例如：校验数据是否存在、记录变更前快照）
     */
    protected void beforeUpdate(T entity) {
    }

    protected void afterUpdate(T entity) {
    }

    /**
     * 单条删除前（例如：校验是否被引用，禁止删除）
     */
    protected void beforeDelete(T entity) {
    }

    protected void afterDelete(T entity) {
    }

    /**
     * 异常回调（例如：记录错误日志、发送告警）
     */
    protected void onException(Exception e, T[] allRows) {
        log.error("Batch save failed. Total rows: {}", allRows.length, e);
    }

}
