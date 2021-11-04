package net.zpavelocity.springboot.test.bean;

public class Result<T> {
    private String code;
    private String msg;
    private T data;

    public String getCode() {
        return code;
    }

    public Result setCode(String code) {
        this.code = code;
        return this;

    }

    public String getMsg() {
        return msg;
    }

    public Result setMsg(String msg) {
        this.msg = msg;
        return this;

    }

    public T getData() {
        return data;
    }

    public Result setData(T data) {
        this.data = data;
        return this;
    }
}
