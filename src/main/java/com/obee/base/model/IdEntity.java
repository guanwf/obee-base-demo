package com.obee.base.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/23 21:46
 *
 * Level 1: 仅包含主键ID
 *
 */
@Data
public class IdEntity implements Serializable {
    @TableId(type = IdType.ASSIGN_ID) // 雪花算法ID
    private Long id;
}
