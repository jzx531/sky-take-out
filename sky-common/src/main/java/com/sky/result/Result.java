package com.sky.result;

import lombok.Data;
import java.io.Serializable;

/**
 *后端统一返回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {
    private Integer code;//编码，1成功，2失败
    private String msg;//错误信息
    private T data;//返回数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 1;
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.code = 1;
        result.data = data;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<T>();
        result.code = 0;
        result.msg = msg;
        return result;
    }
}
