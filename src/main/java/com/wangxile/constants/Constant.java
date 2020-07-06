/*
 * 文件名：Constant.java 版权：Copyright by www.huawei.com 描述： 修改人：ZTE 修改时间：2019年3月8日 跟踪单号： 修改单号： 修改内容：
 */

package com.wangxile.constants;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/6/9 0009 10:40
 */

public interface Constant {
    /**
     * UTF-8编码
     */
    String CHARSET_UTF8 = "UTF-8";

    /******** 在properties文件中配置信息 **************/
    String MAPPER_LOCATION = "mapper.location";

    String DB_DRIVER_CONF = "db.driver";

    String DB_URL_CONF = "db.url";

    String DB_USERNAME_CONF = "db.username";

    String db_PASSWORD = "db.password";

    /************ mapper xml  ****************/

    /**
     * mapper文件后缀
     */
    String MAPPER_FILE_SUFFIX = ".xml";

    String XML_ROOT_LABEL = "mapper";

    String XML_ELEMENT_ID = "id";

    String XML_SELECT_NAMESPACE = "namespace";

    String XML_SELECT_RESULTTYPE = "resultType";

    /**
     * SQL类型枚举，如select、insert、update
     */
    public enum SqlType {
        SELECT("select"),
        INSERT("insert"),
        UPDATE("update"),
        DEFAULT("default");

        private String value;

        private SqlType(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }

}
