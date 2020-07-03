package com.wangxile.spring.framework.orm.core.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangqi
 * @version 1.0
 * @date 2020/7/3 0003 13:21
 * 分页对象 包含当前页数据及分页信息，如总记录数
 */
public class Page<T> implements Serializable {

    private static final Integer DEFAULT_PAGE_SIZE = 20;

    /**
     * 每页的记录数
     */
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 当前页第一条数据在 List 中的位置，从 0 开始
     */
    private Long start;

    /**
     * 当前页中存放的记录，类型一般为 List
     */
    private List<T> rows;

    /**
     * 总记录数
     */
    private Long total;

    public Page(Long start, Long total, Integer pageSize, List<T> rows) {
        this.pageSize = pageSize;
        this.start = start;
        this.rows = rows;
        this.total = total;
    }

    /**
     * 构造空页
     */
    public Page() {
        this(0L, 0L, DEFAULT_PAGE_SIZE, new ArrayList<T>());
    }

    /**
     * 取总页数
     *
     * @return
     */
    public long getTotalPageCount() {
        if (total % pageSize == 0) {
            return total / pageSize;
        } else {
            return total / pageSize + 1;
        }
    }

    /**
     * 获取当前页面
     *
     * @return
     */
    public long getPageNo() {
        return start / pageSize + 1;
    }

    /**
     * 判断该页是否有下一页
     *
     * @return
     */
    public boolean hasNextPage() {
        return this.getPageNo() < this.getTotalPageCount() - 1;
    }

    /**
     * 判断该页是否有上一页
     *
     * @return
     */
    public boolean hasPreviousPage() {
        return this.getPageNo() > 1;
    }

    /**
     * 获取任意一页第一条数据在数据集中的位置，每页条数使用默认值
     *
     * @return
     */
    protected static int getStartOfPage(int pageNo) {
        return getStartOfPage(pageNo, DEFAULT_PAGE_SIZE);
    }

    /**
     * 获取任意一页第一条数据在数据集中的位置
     *
     * @return
     */
    protected static int getStartOfPage(int pageNo, int pageSize) {
        return (pageNo - 1) * pageSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
