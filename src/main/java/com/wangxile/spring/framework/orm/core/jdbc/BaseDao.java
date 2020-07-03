package com.wangxile.spring.framework.orm.core.jdbc;

import com.wangxile.spring.framework.orm.core.common.Page;
import com.wangxile.spring.framework.orm.core.framework.QueryRule;

import java.util.List;
import java.util.Map;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/3 0003 13:57
 * 基础类
 */
public interface BaseDao<T, PK> {

    /**
     * 获取列表
     *
     * @param queryRule 查询条件
     * @return
     * @throws Exception
     */
    List<T> select(QueryRule queryRule) throws Exception;

    /**
     * 分页
     *
     * @param queryRule
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    Page<?> select(QueryRule queryRule, int pageNo, int pageSize) throws Exception;

    /**
     * 根据sql获取列表
     *
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> selectBySql(String sql, Object... args) throws Exception;

    /**
     * 根据sql获取分页
     *
     * @param sql
     * @param param
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    Page<Map<String, Object>> selectBySql(String sql, Object[] param, int pageNo, int pageSize) throws Exception;

    /**
     * 删除一条记录
     *
     * @param entity
     * @return
     * @throws Exception
     */
    int delete(T entity) throws Exception;

    /**
     * 批量删除
     *
     * @param list
     * @return
     * @throws Exception
     */
    int deleteAll(List<T> list) throws Exception;

    /**
     * 插入记录，并返回主键
     *
     * @param entity
     * @return
     * @throws Exception
     */
    PK insertAndReturnId(T entity) throws Exception;

    /**
     * 插入一条记录自增ID
     *
     * @param entity
     * @return
     * @throws Exception
     */
    boolean insert(T entity) throws Exception;

    /**
     * 批量插入
     *
     * @param list
     * @return
     * @throws Exception
     */
    int insertAll(List<T> list) throws Exception;


    /**
     * 修改一条记录
     *
     * @param entity
     * @return
     * @throws Exception
     */
    boolean update(T entity) throws Exception;
}
