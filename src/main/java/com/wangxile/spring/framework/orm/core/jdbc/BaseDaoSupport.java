package com.wangxile.spring.framework.orm.core.jdbc;

import com.wangxile.spring.framework.orm.core.framework.EntityOperation;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @Author:wangqi
 * @Description:
 * @Date:Created in 2020/7/5
 * @Modified by:
 * <p>
 * 主要是对 JdbcTemplate 的包装
 */
public abstract class BaseDaoSupport<T extends Serializable, PK extends Serializable> implements BaseDao<T, PK> {

    private String tableName = "";

    private JdbcTemplate jdbcTemplateWrite;
    private JdbcTemplate jdbcTemplateReadOnly;

    private DataSource dataSourceReadOnly;
    private DataSource dataSourceWrite;

    private EntityOperation<T> op;

    protected BaseDaoSupport() {
        Class<T> entityClass = GenericsUtils.getSuperClassGenricType(getClass(), 0);
        op = new EntityOperation<T>(entityClass, this.getPKColumn());
        this.setTableName(op.tableName);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        if (StringUtils.isEmpty(tableName)) {
            this.tableName = op.tableName;
        } else {
            this.tableName = tableName;
        }
    }

    protected abstract String getPKColumn();

    protected abstract void setDataSoure(DataSource dataSource);

    public JdbcTemplate getJdbcTemplateWrite() {
        return jdbcTemplateWrite;
    }

    public void setJdbcTemplateWrite(JdbcTemplate jdbcTemplateWrite) {
        this.jdbcTemplateWrite = jdbcTemplateWrite;
    }

    public JdbcTemplate getJdbcTemplateReadOnly() {
        return jdbcTemplateReadOnly;
    }

    public void setJdbcTemplateReadOnly(JdbcTemplate jdbcTemplateReadOnly) {
        this.jdbcTemplateReadOnly = jdbcTemplateReadOnly;
    }

    public DataSource getDataSourceReadOnly() {
        return dataSourceReadOnly;
    }

    public void setDataSourceReadOnly(DataSource dataSourceReadOnly) {
        this.dataSourceReadOnly = dataSourceReadOnly;
        jdbcTemplateReadOnly = new JdbcTemplate(dataSourceReadOnly);
    }

    public DataSource getDataSourceWrite() {
        return dataSourceWrite;
    }

    public void setDataSourceWrite(DataSource dataSourceWrite) {
        this.dataSourceWrite = dataSourceWrite;
        jdbcTemplateWrite = new JdbcTemplate(dataSourceWrite);
    }
}
