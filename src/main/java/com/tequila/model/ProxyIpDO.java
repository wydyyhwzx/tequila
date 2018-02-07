package com.tequila.model;

/**
 * Created by wangyudong on 2018/2/7.
 */
public class ProxyIpDO {
    private String ip;
    private int port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProxyIpDO proxyIpDO = (ProxyIpDO) o;

        if (port != proxyIpDO.port) return false;
        return ip.equals(proxyIpDO.ip);
    }

    @Override
    public int hashCode() {
        int result = 31 * ip.hashCode() + port;
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
