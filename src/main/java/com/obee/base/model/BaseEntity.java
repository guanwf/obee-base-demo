package com.obee.base.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.obee.base.enums.RowStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/23 21:54
 *
 * Level 3: 包含审计字段（通用基类）
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseEntity extends TenantEntity{

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifyTime;

    @TableField(fill = FieldFill.INSERT)
    private String creater;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String modifyer;

    /**
     * 备注
     */
    private String remark;

    /**
     * 逻辑删除 (0:未删除, 1:已删除)
     */
    @TableLogic
    private Integer deleted;

    /**
     * 行状态标记
     * 注：@TableField(exist = false) 表示该字段不映射到数据库表
     * 默认值为 UNCHANGED，兼容旧接口
     */
    @TableField(exist = false)
    private RowStatus rowStatus = RowStatus.UNCHANGED;

}
