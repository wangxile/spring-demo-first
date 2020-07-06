package com.wangxile.dao;


import com.wangxile.bean.User;

import java.util.List;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/9 0009 10:40
 */

public interface UserMapper {

    /**
     * 获取单个user
     *
     * @param id
     * @return
     * @see
     */
    User getUser(String id);

    /**
     * 获取所有用户
     *
     * @return
     * @see
     */
    List<User> getAll();

    /**
     * 更新用户（功能未完成）
     *
     * @param id
     */
    void updateUser(String id);
}
