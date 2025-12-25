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

    /** 是否成功 */
    private boolean success;

    /** 返回的数据 */
    private T data;

    /** 返回的信息 */
    private String message;

    // --- Static factory helpers ---
    public static <T> R<T> ok() {
        return new R<>(true, null, "success");
    }

    public static <T> R<T> ok(T data) {
        return new R<>(true, data, "success");
    }

    public static <T> R<T> ok(T data, String message) {
        return new R<>(true, data, message);
    }

    public static <T> R<T> fail() {
        return new R<>(false, null, "error");
    }

    public static <T> R<T> fail(String message) {
        return new R<>(false, null, message);
    }

    public static <T> R<T> fail(T data, String message) {
        return new R<>(false, data, message);
    }

    // --- Fluent helpers (custom names) ---
    public R<T> message(String message) {
        this.message = message;
        return this;
    }

    public R<T> data(T data) {
        this.data = data;
        return this;
    }

    public R<T> success(boolean success) {
        this.success = success;
        return this;
    }

}
