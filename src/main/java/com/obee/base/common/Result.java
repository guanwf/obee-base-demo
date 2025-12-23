package com.obee.base.common;

import lombok.Data;

/**
 * @description:
 * @author: Guanwf
 * @date: 2025/12/23 22:09
 */
@Data
public class Result<T> {
    private String code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode("0");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode("0");
        result.setMessage(msg);
        return result;
    }

}
