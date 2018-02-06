package com.tequila.common;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangyudong on 2018/1/31.
 */

public class HtmlUtils {
    private static Logger logger = LoggerFactory.getLogger(HtmlUtils.class);
    private static final String UTF8 = "utf-8";
    private static List<IpPort> proxy = Lists.newCopyOnWriteArrayList();
    private static AtomicInteger index = new AtomicInteger(0);
    private static Map<IpPort,Integer> failSize = new ConcurrentHashMap<>();
    private static final int maxFail = 30;
    private static final int maxFailSize = 1000;


    static {
        List<IpPort> temp = new ArrayList<>();
        temp.add(new IpPort("219.149.46.151", 3129));
        temp.add(new IpPort("101.4.136.34", 81));
        temp.add(new IpPort("116.199.115.78", 82));
        temp.add(new IpPort("14.215.177.73", 80));
        temp.add(new IpPort("121.8.98.197", 80));
        temp.add(new IpPort("121.8.98.198", 80));
        temp.add(new IpPort("47.52.222.165", 80));
        temp.add(new IpPort("113.207.42.103", 80));
        temp.add(new IpPort("121.40.108.76", 80));
        temp.add(new IpPort("39.137.83.131", 8080));
        temp.add(new IpPort("118.114.77.47", 8080));
        temp.add(new IpPort("39.134.240.135", 8080));
        temp.add(new IpPort("61.134.29.13", 8080));
        temp.add(new IpPort("180.118.242.42", 61234));
        proxy.addAll(temp);
    }

    public static String getHtml(String url, Map<String,String> parameters) throws IOException {
        String html = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();// 创建httpClient对象
        HttpGet httpget = new HttpGet(composeTargetUrl(url, parameters));

        IpPort ipPort = null;
        RequestConfig.Builder builder = RequestConfig.custom();
        if (proxy.size() > 0) {
            int i = index.get() % proxy.size();
            index.set(i + 1);
            ipPort = proxy.get(i);
            HttpHost httpProxy = new HttpHost(ipPort.getIp(), ipPort.getPort());
            builder.setProxy(httpProxy);
        }
        RequestConfig requestConfig = builder
                .setSocketTimeout(3000)
                .setConnectTimeout(3000)
                .setConnectionRequestTimeout(3000)
                .build();

        httpget.setConfig(requestConfig);
        httpget.setHeader("Referer", "http://weixin.sogou.com/weixin?oq=&query=%E6%9D%8E%E5%AE%87%E6%98%A5&_sug_type_=&sut=596&lkt=1%2C1517560265583%2C1517560265583&s_from=input&ri=0&_sug_=n&type=2&sst0=1517560265687&page=2&ie=utf8&w=01015002&dr=1");
        httpget.setHeader("Host", "weixin.sogou.com");
        httpget.setHeader("Pragma", "no-cache");
        httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpget.setHeader("Accept-Language:", "zh-cn");
        httpget.setHeader("Accept-Encoding:", "gzip, deflate");
        httpget.setHeader("Cache-Control:", "no-cache");
        httpget.setHeader("Upgrade-Insecure-Requests:", "1");
        httpget.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/604.5.6 (KHTML, like Gecko) Version/11.0.3 Safari/604.5.6");
        httpget.setHeader("Cookie", "SUID=D02F0E7A232C940A000000005A6064D2; JSESSIONID=aaamdiu-7Q0_scZArLCew; ABTEST=6|1516266706|v1; weixinIndexVisited=1; sct=1; SNUID=CA3415611A1F7F0BEEAEB6181B9372ED; IPLOC=CN1100; SUID=D02F0E7A541C940A000000005A6064D2; SUV=000D174E7A0E2FD05A6064D29C9B8679");

        InputStream in = null;
        try {
            CloseableHttpResponse responce = httpClient.execute(httpget);
            int code = responce.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                HttpEntity entity = responce.getEntity();
                if (entity != null) {
                    in = entity.getContent();
                    html = IOUtils.toString(in, UTF8);
                }
            }
        } catch (Throwable e) {
            if (null == ipPort) {
                logger.error("[HtmlUtils] Throwable.", e);
            } else {
                logger.error("[HtmlUtils] Throwable.IpPort:" + ipPort, e);
                int size = 1;
                if (failSize.containsKey(ipPort)) {
                    size = failSize.get(ipPort) + 1;
                }
                failSize.put(ipPort, size);
                if (size > maxFail) {
                    setProxy(ipPort.getIp(), ipPort.getPort(), 1);
                }
                if (failSize.size() > maxFailSize)
                    failSize.clear();
            }
        } finally {
            // 释放连接
            httpClient.close();
            IOUtils.closeQuietly(in);
        }
        return html;
    }

    public static boolean setProxy(String ip, int port, int type) {
        logger.info("[HtmlUtils] setProxy,ip:{},port:{},type:{}", ip, port, type);

        if (type == 0) {
            index.set(0);
            return proxy.add(new IpPort(ip, port));
        }

        if (type == 1) {
            IpPort ipPort = new IpPort(ip, port);
            index.set(0);
            failSize.remove(ipPort);
            return proxy.remove(ipPort);
        }

        return false;
    }

    private static String composeTargetUrl(String url, Map<String,String> parameters) {
        if (null == parameters || parameters.size() == 0)
            return url;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url);

        if (stringBuilder.lastIndexOf("?") < 0) {
            stringBuilder.append("?");
        }

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String encode;
            if (StringUtils.isNotBlank(entry.getValue())) {
                try {
                    encode = URLEncoder.encode(entry.getValue(), UTF8);
                } catch (UnsupportedEncodingException e) {
                    encode = entry.getValue();
                }
            } else {
                encode = "";
            }

            stringBuilder.append(entry.getKey()).append("=").append(encode).append("&");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

}

class IpPort {
    private String ip;
    private int port;

    IpPort(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

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

        IpPort ipPort = (IpPort) o;

        if (port != ipPort.port) return false;
        return ip.equals(ipPort.ip);
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
