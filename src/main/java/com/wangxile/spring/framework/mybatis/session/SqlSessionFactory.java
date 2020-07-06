package com.wangxile.spring.framework.mybatis.session;


public interface SqlSessionFactory {

    /**
     * 开启数据库会话
     *
     * @return
     * @see
     */
    SqlSession openSession();

}
