package com.obee.base.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/29 22:11
 *
 *  * 通用动态 DTO
 *  * @param <M> 主表实体类型
 */
@Data
public class UniversalDTO<M> {
    /**
     * 主表数据
     */
    private M master;

    /**
     * 子表数据容器
     * Key: 子表标识 (如 "items", "pays")
     * Value: 尚未解析的 JSON 节点列表 (包含 rowStatus, id, 以及业务字段)
     *
     * 为什么用 JsonNode？
     * 因为在 Controller 层反序列化时，我们还不知道 "items" 对应哪个 Java Class。
     */
    private Map<String, List<JsonNode>> slaves;
}
