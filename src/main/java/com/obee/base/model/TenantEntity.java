package com.obee.base.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/23 21:54
 *
 * Level 2: 包含租户ID
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TenantEntity extends IdEntity{
    private Long tid;
}
