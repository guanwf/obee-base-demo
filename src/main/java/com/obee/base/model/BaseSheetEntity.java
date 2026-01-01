package com.obee.base.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/30 11:36
 */
@Data
public class BaseSheetEntity extends TenantEntity{
    private String sheetId;
    private String creater;
    private LocalDateTime createDate;
    private Integer flag;
}
