package com.wangxile.spring.framework.jdbc;

import com.wangxile.spring.framework.annotation.Column;
import com.wangxile.spring.framework.annotation.Table;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/3 0003 10:14
 * <p>
 * 通过反射和注解编写，通用select demo
 */
public class demo {

    public static void main(String[] args) {
        Member condition = new Member();
        condition.setName("Tom");
        condition.setAge(19);
        List<?> result = select(condition);
        System.out.println(Arrays.toString(result.toArray()));
    }

    private static List<?> select(Object condition) {
        List<Object> result = new ArrayList<>();
        Class<?> entityClass = condition.getClass();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            //1. 加载驱动类
            Class.forName("com.mysql.jdbc.Driver");
            //2. 建立连接
            con = DriverManager
                    .getConnection("jdbc:mysql://127.0.0.1 3306/demo?characterEncoding=UTF-8&rewriteBatchedStatements=true",
                            " root", "123456");
            //根据表中的行名找对应的属性名
            Map<String, String> columnMapper = new HashMap<String, String>();
            //据类的属性名找对应表中的行名
            Map<String, String> fieldMapper = new HashMap<String, String>();
            Field[] fields = entityClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (field.isAnnotationPresent(Column.class)) {
                    //判断是否有注解
                    Column column = field.getAnnotation(Column.class);
                    String columnName = column.name();
                    columnMapper.put(columnName, fieldName);
                    fieldMapper.put(fieldName, columnName);
                } else {
                    //默认就是字段名，属性名一致
                    columnMapper.put(fieldName, fieldName);
                    fieldMapper.put(fieldName, fieldName);
                }
            }

            //3. 创建语句集
            Table table = entityClass.getAnnotation(Table.class);
            String sql = "select * from" + table.name();
            StringBuilder where = new StringBuilder("where 1=1 ");
            for (Field field : fields) {
                Object value = field.get(condition);
                if (Objects.nonNull(value)) {
                    if (String.class == field.getType()) {
                        where.append(" and ").append(fieldMapper.get(field.getName()))
                                .append(" = '").append(value).append("'");
                    } else {
                        where.append(" and ").append(fieldMapper.get(field.getName()))
                                .append(" = ").append(value);
                    }
                }
            }
            pstm = con.prepareStatement(sql + where.toString());

            //4. 执行语句集
            rs = pstm.executeQuery();
            //保存了处理真正数值以外的所有附加信息
            int columnCounts = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Object instance = entityClass.newInstance();
                for (int i = 0; i < columnCounts; i++) {
                    //实体类属性名 ，对应数据库表的字段名
                    //可以通过反射机制拿到实体类的所有字段
                    //从rs中取得当前这个游标下的类名
                    String columnName = rs.getMetaData().getColumnName(i);
                    Field field = entityClass.getDeclaredField(columnMapper.get(columnName));
                    field.setAccessible(true);
                    field.set(instance, rs.getObject(columnName));
                }
                result.add(instance);
            }
            //5 获取结果集
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //6. 关闭结果集、关闭语句集、关闭连接
            try {
                rs.close();
                pstm.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
