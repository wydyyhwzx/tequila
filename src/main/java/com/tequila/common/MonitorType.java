package com.tequila.common;

/**
 * Created by wangyudong on 2018/3/12.
 */
public enum MonitorType {
    NO(0, "未监控"),
    YES(1, "已监控");

    private int code;
    private String desc;

    private MonitorType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
