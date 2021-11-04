package net.zpavelocity.springboot.common;

import lombok.Data;

@Data
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(1, msg, null);
    }

    public static <T> Result<T> ok(String msg) {
        return new Result<>(0, msg, null);
    }

    public static <T> Result<T> data(T data) {
        return new Result<>(0, "成功", data);
    }
}
