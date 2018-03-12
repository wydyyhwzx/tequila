package com.tequila.model;

import java.util.Date;

/**
 * Created by wangyudong on 2018/3/12.
 */
public class HistoryDO {
    private Integer id;
    /*
    *  用户ID
    * */
    private Integer userId;
    /*
    *  搜索关键词
    * */
    private String keyword;
    /*
    *  用户文章URL
    * */
    private String url;
    /*
    *  是否监控，0：未监控，1：已监控
    * */
    private Integer monitor;
    /*
    *  扩展字段，json格式
    * */
    private String extend;
    private Date gmtCreate;
    private Date gmtModified;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getMonitor() {
        return monitor;
    }

    public void setMonitor(Integer monitor) {
        this.monitor = monitor;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}
