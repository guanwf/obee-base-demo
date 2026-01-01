package com.obee.base.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/29 22:16
 */
public enum RowStatus {
    /**
     * 新增 (ID为空)
     */
    NEW(1),
    /**
     * 修改 (ID不为空)
     */
    UPDATED(2),
    /**
     * 删除 (ID不为空)
     */
    DELETED(3),
    /**
     * 无变化 (后端忽略)
     */
    UNCHANGED(0);

    @EnumValue // MP写入数据库时的值 (虽然这个字段不存库)
    private final int code;

    RowStatus(int code) {
        this.code = code;
    }

    @JsonValue // 前端传参可以使用数字或字符串
    public int getCode() {
        return code;
    }
}
