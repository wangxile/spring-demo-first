package com.wangxile.spring.framework.mybatis.executor.resultset;


import java.sql.ResultSet;
import java.util.List;


public interface ResultSetHandler {

    /**
     * 处理查询结果
     *
     * @param resultSet
     * @return
     * @see
     */
    <E> List<E> handleResultSets(ResultSet resultSet);

}
