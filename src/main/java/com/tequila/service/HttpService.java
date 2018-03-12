package com.tequila.service;

import com.google.common.collect.Sets;
import com.tequila.common.TequilaException;
import com.tequila.mapper.ProxyIpMapper;
import com.tequila.model.ProxyIpDO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangyudong on 2018/1/31.
 */
@Component
public class HttpService {
    private static Logger logger = LoggerFactory.getLogger(HttpService.class);
    private static final String UTF8 = "utf-8";
    private static final int maxFail = 30;
    private static final int maxFailSize = 1000;

    private Set<ProxyIpDO> proxy = Sets.newConcurrentHashSet();
    private AtomicInteger index = new AtomicInteger(0);
    private Map<ProxyIpDO,Integer> failSize = new ConcurrentHashMap<>();
    private Map<String,String> headeres = new ConcurrentHashMap<>();
    private RequestConfig.Builder builder;

    @Autowired
    private ProxyIpMapper proxyIpMapper;

    @PostConstruct
    public void init() {
        List<ProxyIpDO> temp = proxyIpMapper.all();
        logger.info("[HttpService] proxy db size:" + temp.size());
        if (null != temp && temp.size() > 0) {
            proxy.addAll(temp);
        }
        logger.info("[HttpService] proxy size:" + proxy.size());

        headeres.put("Referer", "http://weixin.sogou.com/weixin?oq=&query=%E6%9D%8E%E5%AE%87%E6%98%A5&_sug_type_=&sut=596&lkt=1%2C1517560265583%2C1517560265583&s_from=input&ri=0&_sug_=n&type=2&sst0=1517560265687&page=2&ie=utf8&w=01015002&dr=1");
        headeres.put("Host", "weixin.sogou.com");
        headeres.put("Pragma", "no-cache");
        headeres.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headeres.put("Accept-Language:", "zh-cn");
        headeres.put("Accept-Encoding:", "gzip, deflate");
        headeres.put("Cache-Control:", "no-cache");
        headeres.put("Upgrade-Insecure-Requests:", "1");
        headeres.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/604.5.6 (KHTML, like Gecko) Version/11.0.3 Safari/604.5.6");
        headeres.put("Cookie", "SUID=D02F0E7A232C940A000000005A6064D2; JSESSIONID=aaamdiu-7Q0_scZArLCew; ABTEST=6|1516266706|v1; weixinIndexVisited=1; sct=1; SNUID=CA3415611A1F7F0BEEAEB6181B9372ED; IPLOC=CN1100; SUID=D02F0E7A541C940A000000005A6064D2; SUV=000D174E7A0E2FD05A6064D29C9B8679");

        builder = RequestConfig.custom();
        builder.setSocketTimeout(3000).setConnectTimeout(3000).setConnectionRequestTimeout(3000).setCookieSpec(CookieSpecs.IGNORE_COOKIES);
    }

    public String getArticleList(String url, Map<String,String> parameters) throws IOException {
        String html;
        HttpGet httpget = new HttpGet(composeTargetUrl(url, parameters));
        ProxyIpDO proxyIpDO = buildProxy();
        RequestConfig requestConfig = builder.build();
        httpget.setConfig(requestConfig);

        if (headeres.size() > 0) {
            for (Map.Entry<String,String> entry : headeres.entrySet()) {
                httpget.setHeader(entry.getKey(), entry.getValue());
            }
        }

        html = execute(httpget, proxyIpDO);
        if (StringUtils.isNotBlank(html) && html.contains("用户您好，您的访问过于频繁，为确认本次访问为正常用户行为，需要您协助验证")) {
            if (null == proxyIpDO) {
                logger.warn("[HttpService] Request Limit");
            } else {
                logger.warn("[HttpService] Request Limit. ProxyIpDO:{}", proxyIpDO.toString());
            }
            html = "";
        }
        return html;
    }

    public String getArticle(String url) throws IOException {
        HttpGet httpget = new HttpGet(url);
        ProxyIpDO proxyIpDO = buildProxy();
        RequestConfig requestConfig = builder.build();
        httpget.setConfig(requestConfig);

        return execute(httpget, proxyIpDO);
    }

    private String execute(HttpGet httpget, ProxyIpDO proxyIpDO) throws IOException{
        CloseableHttpClient httpClient = HttpClients.createDefault();// 创建httpClient对象
        InputStream in = null;
        String html = "";
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
            if (null == proxyIpDO) {
                logger.error("[HttpService] Throwable.", e);
            } else {
                logger.error("[HttpService] Throwable.ProxyIpDO:" + proxyIpDO, e);
                int size = 1;
                if (failSize.containsKey(proxyIpDO)) {
                    size = failSize.get(proxyIpDO) + 1;
                }
                failSize.put(proxyIpDO, size);
                if (size > maxFail) {
                    setProxy(proxyIpDO.getIp() + ":" + proxyIpDO.getPort(), 1);
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

    private ProxyIpDO buildProxy() {
        ProxyIpDO proxyIpDO = null;
        if (proxy.size() > 0) {
            int i = index.get() % proxy.size();
            index.set(i + 1);
            proxyIpDO = proxy.toArray(new ProxyIpDO[0])[i];
            HttpHost httpProxy = new HttpHost(proxyIpDO.getIp(), proxyIpDO.getPort());
            builder.setProxy(httpProxy);
        } else {
            builder.setProxy(null);
        }

        return proxyIpDO;
    }

    public boolean setProxy(String ipPort, int type) {
        logger.info("[HttpService] setProxy,ipPort:{},type:{}", ipPort, type);

        String[] spilt = ipPort.split(":");
        ProxyIpDO proxyIpDO = new ProxyIpDO();
        proxyIpDO.setIp(spilt[0]);
        proxyIpDO.setPort(Integer.parseInt(spilt[1]));

        if (type == 0) {
            proxyIpMapper.insert(proxyIpDO);
            index.set(0);
            return proxy.add(proxyIpDO);
        }

        if (type == 1) {
            proxyIpMapper.delete(proxyIpDO);
            index.set(0);
            failSize.remove(proxyIpDO);
            return proxy.remove(proxyIpDO);
        }

        return false;
    }

    public boolean reloadProxy() {
        logger.info("[HttpService] reloadProxy");
        List<ProxyIpDO> temp = proxyIpMapper.all();
        logger.info("[HttpService] proxy db size:" + temp.size());
        index.set(0);
        failSize.clear();
        proxy.clear();
        if (null != temp && temp.size() > 0) {
            proxy.addAll(temp);
            logger.info("[HttpService] proxy size:" + proxy.size());
        }

        return true;
    }

    public boolean setSougouHeader(String name, String value) {
        if (StringUtils.isBlank(value)) {
            headeres.remove(name);
        } else {
            headeres.put(name, value);
        }

        return true;
    }

    private String composeTargetUrl(String url, Map<String,String> parameters) {
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
