package com.wangxile.spring.framework.orm.core.common;

import java.io.Serializable;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/3 0003 13:47
 * ResultMsg 类主要是为统一返回结果做的顶层设计，主要包括状态码、结果说明内容和返回数据
 */
public class ResultMsg<T> implements Serializable {
    /**
     * 状态码，系统的返回码
     */
    private int status;

    /**
     * 状态码的解释
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    public ResultMsg() {
    }

    public ResultMsg(int status) {
        this.status = status;
    }

    public ResultMsg(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ResultMsg(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public ResultMsg(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
