package com.wangxile.spring.framework.orm.core.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/3 0003 14:35
 * <p>
 * 如果用 QueryRule 类来构建查询条件，用户在做条件查询时不需要手写 SQL ，实现业务代码与sql解耦
 */
public final class QueryRule implements Serializable {

    public static final int ASC_ORDER = 101;
    public static final int DESC_ORDER = 102;
    public static final int LIKE = 1;
    public static final int IN = 2;
    public static final int NOT_IN = 3;
    public static final int BETWEEN = 4;
    public static final int EQ = 5;
    public static final int NOT_EQ = 6;
    public static final int GT = 7;
    public static final int GE = 8;
    public static final int LT = 9;
    public static final int LE = 10;
    public static final int IS_NULL = 11;
    public static final int IS_NOT_NULL = 12;
    public static final int IS_EMPTY = 13;
    public static final int IS_NOT_EMPTY = 14;
    public static final int AND = 201;
    public static final int OR = 202;

    private List<Rule> ruleList = new ArrayList<Rule>();
    private List<QueryRule> queryRuleList = new ArrayList<QueryRule>();
    private String propertyName;

    private QueryRule() {
    }

    public QueryRule(String propertyName) {
        this.propertyName = propertyName;
    }

    public QueryRule addAscOrder(String propertyName) {
        this.ruleList.add(new Rule(ASC_ORDER, propertyName));
        return this;
    }

    public QueryRule addDescOrder(String propertyName) {
        this.ruleList.add(new Rule(DESC_ORDER, propertyName));
        return this;
    }

    public QueryRule andIsNull(String propertyName) {
        this.ruleList.add(new Rule(IS_NULL, propertyName).setAndOr(AND));
        return this;
    }

    public QueryRule andIsNotNull(String propertyName) {
        this.ruleList.add(new Rule(IS_NOT_NULL, propertyName).setAndOr(AND));
        return this;
    }

    public QueryRule andIsEmpty(String propertyName) {
        this.ruleList.add(new Rule(IS_EMPTY, propertyName).setAndOr(AND));
        return this;
    }

    public QueryRule andIsNotEmpty(String propertyName) {
        this.ruleList.add(new Rule(IS_NOT_EMPTY, propertyName).setAndOr(AND));
        return this;
    }

    public QueryRule andLike(String propertyName, Object value) {
        this.ruleList.add(new Rule(LIKE, propertyName, new Object[]{value}).setAndOr(AND));
        return this;
    }

    public QueryRule andEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(EQ, propertyName, new Object[]{value}).setAndOr(AND));
        return this;
    }

    public QueryRule andBetween(String propertyName, Object... values) {
        this.ruleList.add(new Rule(BETWEEN, propertyName, values).setAndOr(AND));
        return this;
    }

    public QueryRule andIn(String propertyName, List<Object> values) {
        this.ruleList.add(new Rule(IN, propertyName, new Object[]{values}).setAndOr(AND));
        return this;
    }

    public QueryRule andIn(String propertyName, Object... values) {
        this.ruleList.add(new Rule(IN, propertyName, values).setAndOr(AND));
        return this;
    }

    public QueryRule andNotIn(String propertyName, List<Object> values) {
        this.ruleList.add(new Rule(NOT_IN, propertyName, new Object[]{values}).setAndOr(AND));
        return this;
    }

    public QueryRule andNotIn(String propertyName, Object... values) {
        this.ruleList.add(new Rule(NOT_IN, propertyName, values).setAndOr(AND));
        return this;
    }

    public QueryRule andNotEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(NOT_EQ, propertyName, new Object[]{value}).setAndOr(AND));
        return this;
    }

    public QueryRule andGreaterThan(String propertyName, Object value) {
        this.ruleList.add(new Rule(GT, propertyName, new Object[]{value}).setAndOr(AND));
        return this;
    }

    public QueryRule andGreaterEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(GE, propertyName, new Object[]{value}).setAndOr(AND));
        return this;
    }

    public QueryRule andLessThan(String propertyName, Object value) {
        this.ruleList.add(new Rule(LT, propertyName, new Object[]{value}).setAndOr(AND));
        return this;
    }

    public QueryRule andLessEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(LE, propertyName, new Object[]{value}).setAndOr(AND));
        return this;
    }

    public List<Rule> getRuleList() {
        return ruleList;
    }

    public List<QueryRule> getQueryRuleList() {
        return queryRuleList;
    }

    public String getPropertyName() {
        return propertyName;
    }

    protected class Rule implements Serializable {
        /**
         * 规则类型
         */
        private int type;

        private String propertyName;

        private Object[] values;

        private int andOr = AND;

        public Rule(int type, String propertyName) {
            this.propertyName = propertyName;
            this.type = type;
        }

        public Rule(int type, String propertyName, Object[] paramArrayOfObject) {
            this.propertyName = propertyName;
            this.type = type;
            this.values = paramArrayOfObject;
        }

        public Rule setAndOr(int andOr) {
            this.andOr = andOr;
            return this;
        }

        public int getAndOr() {
            return andOr;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public Object[] getValues() {
            return values;
        }

        public void setValues(Object[] values) {
            this.values = values;
        }
    }
}


