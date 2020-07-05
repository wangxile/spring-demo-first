package com.wangxile.spring.framework.orm.core.framework;


import com.wangxile.spring.framework.jdbc.Column;
import com.wangxile.spring.framework.jdbc.Entity;
import com.wangxile.spring.framework.jdbc.Id;
import com.wangxile.spring.framework.jdbc.Table;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


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

    public Map<String, PropertyMapping> mappings;

    public RowMapper<T> rowMapper;

    public String tableName;

    public String allColumn = "*";

    public Field pkField;

    public EntityOperation(Class<T> clazz, String pk) throws Exception {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new Exception("类错误,未包含注解");
        }
        this.entityClass = clazz;
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null) {
            this.tableName = table.name();
        } else {
            this.tableName = entityClass.getSimpleName();
        }
        Map<String, Method> getterMap = ClassMappings.findPublicGetters(entityClass);
        Map<String, Method> setterMap = ClassMappings.findPublicSetters(entityClass);
        Field[] fields = ClassMappings.findFields(entityClass);
        fillPkFieldAndAllColumn(pk, fields);
        this.mappings = getPropertyMappings(getterMap, setterMap, fields);
        this.allColumn = this.mappings.keySet().toString().replace("[", "").replace("]", "").replaceAll(" ", "");
        this.rowMapper = createRowMapper();
    }

    Map<String, PropertyMapping> getPropertyMappings(Map<String, Method> getters, Map<String, Method> setters,
                                                     Field[] fields) {
        Map<String, PropertyMapping> mappings = new HashMap<String, PropertyMapping>();
        String name;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }
            name = field.getName();
            if (name.startsWith("is")) {
                name = name.substring(2);
            }
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            Method setter = setters.get(name);
            Method getter = getters.get(name);
            if (getter == null || setter == null) {
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            if (column == null) {
                mappings.put(field.getName(), new PropertyMapping(getter, setter, field));
            } else {
                mappings.put(column.name(), new PropertyMapping(getter, setter, field));
            }
        }
        return mappings;
    }

    RowMapper<T> createRowMapper() {
        return new RowMapper<T>() {
            public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                try {
                    T t = entityClass.newInstance();
                    ResultSetMetaData meta = rs.getMetaData();
                    int columns = meta.getColumnCount();
                    String columnName;
                    for (int i = 1; i <= columns; i++) {
                        Object value = rs.getObject(i);
                        columnName = meta.getColumnName(i);
                        fillBeanFieldValue(t, columnName, value);
                    }
                    return t;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    protected void fillBeanFieldValue(T t, String columnName, Object value) {
        if (value != null) {
            PropertyMapping pm = mappings.get(columnName);
            if (pm != null) {
                try {
                    pm.set(t, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void fillPkFieldAndAllColumn(String pk, Field[] fields) {
        //设定主键
        try {
            if (!StringUtils.isEmpty(pk)) {
                pkField = entityClass.getDeclaredField(pk);
                pkField.setAccessible(true);
            }
        } catch (Exception e) {
            //没找到主键列， 主键列名必须与属性名相同
            e.printStackTrace();
        }
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (StringUtils.isEmpty(pk)) {
                Id id = f.getAnnotation(Id.class);
                if (id != null) {
                    pkField = f;
                    break;
                }
            }
        }
    }

    public T parse(ResultSet rs) {
        T t = null;
        if (null == rs) {
            return null;
        }
        Object value = null;
        try {
            t = (T) entityClass.newInstance();
            for (String columnName : mappings.keySet()) {
                value = rs.getObject(columnName);
                fillBeanFieldValue(t, columnName, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public Map<String, Object> parse(T t) {
        Map<String, Object> map = new TreeMap<String, Object>();
        try {
            for (String columnName : mappings.keySet()) {
                Object value = null;
                value = mappings.get(columnName).getter.invoke(t);
                if (value == null) {
                    continue;
                }
                map.put(columnName, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public void println(T t) {
        try {
            for (String columnName : mappings.keySet()) {
                Object value = mappings.get(columnName).getter.invoke(t);
                if (value == null) {
                    continue;
                }
                System.out.println(columnName + "=" + value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PropertyMapping {
        boolean insertable;
        boolean updatable;
        String columnName;
        boolean id;
        Method getter;
        Method setter;
        Class enumClass;
        String fieldName;

        public PropertyMapping(Method getter, Method setter, Field field) {
            this.getter = getter;
            this.setter = setter;
            this.enumClass = getter.getReturnType().isEnum() ? getter.getReturnType() : null;
            Column column = field.getAnnotation(Column.class);
            this.insertable = column == null || column.insertable();
            this.updatable = column == null || column.updatable();
            this.columnName = column == null ? ClassMappings.getGetterName(getter)
                    : ("".equals(column.name()) ? ClassMappings.getGetterName(getter) : column.name());
            this.id = field.isAnnotationPresent(Id.class);
            this.fieldName = field.getName();
        }

        Object get(Object target) throws Exception {
            Object r = getter.invoke(target);
            return enumClass == null ? r : Enum.valueOf(enumClass, (String) r);
        }

        void set(Object target, Object value) throws Exception {
            if (enumClass != null && value != null) {
                value = Enum.valueOf(enumClass, (String) value);
            }
            try {
                if (value != null) {
                    setter.invoke(target, setter.getParameterTypes()[0].cast(value));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public EntityOperation(Class<T> entityClass, Field pkField) {
        this.entityClass = entityClass;
        this.pkField = pkField;
    }
}
