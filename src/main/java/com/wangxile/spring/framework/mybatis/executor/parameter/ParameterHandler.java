package com.wangxile.spring.framework.mybatis.executor.parameter;


import java.sql.PreparedStatement;


public interface ParameterHandler {

    /**
     * 设置参数
     *
     * @param paramPreparedStatement
     * @see
     */
    void setParameters(PreparedStatement paramPreparedStatement);
}
