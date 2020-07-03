package com.wangxile.spring.framework.orm.core.framework;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;


/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/3 0003 17:10
 * <p>
 * 主要实现数据库表结构和对象类结构的映射关系
 */
public class EntityOperation<T> {

    private static final Logger logger = LoggerFactory.getLogger(EntityOperation.class);

    /**
     * 泛型实体 Class 对象
     */
    public Class<T> entityClass = null;

    //  public final Map<String, PropertyMapping> mappings;

    //   public final RowMapper<T> rowMapper;

    public String tableName;

    public String allColumn = "*";

    public Field pkField;

    public EntityOperation(Class<T> entityClass, Field pkField) {
        this.entityClass = entityClass;
        this.pkField = pkField;
    }
}
