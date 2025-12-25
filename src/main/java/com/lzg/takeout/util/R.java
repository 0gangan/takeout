package com.lzg.takeout.util;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

/*
返回类型的同意定义
  1. 成功与否的标识
  2. 返回的数据
  3. 返回的信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private int code;

    /** 返回的数据 */
    private T data;

    /** 返回的信息 */
    private String message;

    // --- Static factory helpers ---
    public static <T> R<T> ok() {
        return new R<>(200, null, "success");
    }

    public static <T> R<T> ok(T data) {
        return new R<>(200, data, "success");
    }

    public static <T> R<T> ok(T data, String message) {
        return new R<>(200, data, message);
    }

    public static <T> R<T> fail() {
        return new R<>(500, null, "error");
    }

    public static <T> R<T> fail(String message) {
        return new R<>(500, null, message);
    }

    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, null, message);
    }

    public static <T> R<T> fail(T data, int code, String message) {
        return new R<>(code, data, message);
    }

    // --- Fluent helpers ---
    public R<T> code(int code) {
        this.code = code;
        return this;
    }

    public R<T> message(String message) {
        this.message = message;
        return this;
    }

    public R<T> data(T data) {
        this.data = data;
        return this;
    }

}
