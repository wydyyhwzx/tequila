package com.tequila.service;

import java.io.IOException;
import java.util.*;

import com.tequila.common.HtmlUtils;
import com.tequila.domain.WechartArticle;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wangyudong on 2018/1/31.
 * 搜狗微信
 */
public class SougouWechartService {
    private static Logger logger = LoggerFactory.getLogger(SougouWechartService.class);
    private static final String sogouUrl = "http://weixin.sogou.com/weixin";
    private static final int maxRetryNum = 3;


    /**
     * 获取搜狗微信文章列表
     *
     * @param query
     *            查询参数
     * @param page
     *            当前页数
     * @param retryNum
     *            重试次数
     * @return 包含标题和ul的集合
     * @throws IOException
     */
    public List<WechartArticle> getArticleList(String query, int page, Map<String,String> parameters, int retryNum) throws IOException {
        if (retryNum > maxRetryNum) {
            logger.info("[SougouWechartService] getArticleList retryNum > {}, query:{}, page:{}", maxRetryNum, query, page);
            return new ArrayList<>(0);
        }

        if (null == parameters) {
            parameters = createParamters(query, page);
        }

        HtmlUtils htmlUtil = new HtmlUtils();
        String sougouWechatHtml = htmlUtil.getHtml(sogouUrl, parameters);
        Document sougouWechatDoc = Jsoup.parse(sougouWechatHtml);

        Element newsList = sougouWechatDoc.selectFirst(".news-box ul.news-list");
        if (null == newsList) {
            return getArticleList(query, page, parameters, retryNum + 1);
        }
        List<WechartArticle> sougouWechartList = new ArrayList<>();
        for (Element li : newsList.children()) {
            WechartArticle article = new WechartArticle();
            Element img = li.selectFirst(".img-box a img");
            if (null != img) {
                article.setImg(img.attr("src"));
            }
            Element txtBox = li.selectFirst("div.txt-box");
            Element title = txtBox.selectFirst("h3 a");
            article.setUrl(title.attr("href"));
            article.setTittle(title.text());
            Element desc = txtBox.selectFirst("p.txt-info");
            article.setContent(desc.text());
            Element sp = txtBox.selectFirst("div.s-p");
            article.setNickName(sp.selectFirst("a.account").text());
            String time = sp.selectFirst(".s2").html();
            time = time.substring(time.indexOf("('") + 2, time.indexOf("')"));
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(Long.parseLong(time) * 1000);
            article.setTime(String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE)));
            sougouWechartList.add(article);
        }
        return sougouWechartList;
    }

    /**
     * 获取微信文章
     *
     * @param url
     *            请求地址
     * @return 微信文章
     * @throws IOException
     */
    public WechartArticle getWechartArticle(String url) throws IOException {
        HtmlUtils htmlUtil = new HtmlUtils();
        String wechartArticleHtml = htmlUtil.getHtml(url, null);

        Document wechartArticlDoc = Jsoup.parse(wechartArticleHtml);
        Elements tittle = wechartArticlDoc.select(".rich_media_title");
        Element author = wechartArticlDoc.select(".rich_media_meta.rich_media_meta_text").get(1);
        Elements time = wechartArticlDoc.select(".rich_media_meta.rich_media_meta_text#post-date");
        Elements content = wechartArticlDoc.select(".rich_media_content#js_content");
        String alterContent = content.html().replace("data-src", "src");// 将属性data-src替换为src，否则图片不能正常显示
        Elements nickName = wechartArticlDoc.select(".rich_media_meta.rich_media_meta_text.rich_media_meta_nickname");

        WechartArticle wechartArticle = new WechartArticle(url, tittle.text());
        wechartArticle.setAuthor(author.text());
        wechartArticle.setTime(time.text());
        wechartArticle.setNickName(nickName.text());
        wechartArticle.setContent(alterContent);
        return wechartArticle;
    }

    private Map<String,String> createParamters(String query, int page) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("query", query);
        parameters.put("page", String.valueOf(page));
        parameters.put("s_from", "input");
        parameters.put("_sug_", "y");
        parameters.put("type", "2");
        parameters.put("ie", "utf8");
        parameters.put("dr", "1");
        parameters.put("ri", "0");
        parameters.put("oq", "");
        parameters.put("_sug_type_", "");
        parameters.put("sut", "1356");
        parameters.put("lkt", "1,1517820477620,1517820477620");
        parameters.put("sst0", String.valueOf(new Date().getTime()));
        parameters.put("w", "01019900");
        return parameters;
    }

    public static void main(String[] args) throws Exception{
        SougouWechartService sougouWechartService = new SougouWechartService();
        /*List<WechartArticle> list = sougouWechartService.getArticleList("张靓颖", 1, null, 0);
        for (WechartArticle wechartArticle: list) {
            System.out.println(wechartArticle.toString());
            System.out.println();
        }*/
        WechartArticle article = sougouWechartService.getWechartArticle("http://mp.weixin.qq.com/s?src=3&timestamp=1517821753&ver=1&signature=lgOWtvIEX71GseKByfGlshoPv16Fde6gdE168eUn65v9M7-bDqDzoyKMjD7NquJfXtLyJtXvS9RuplDHiiz9Y92kXN0dgg6R2dOFscpAatojucaYrAWCyMIlpCZ0bi8*4qzJiQd4fIr4O3VPxqeqO5cDLByDYcITgUPVCFHQRQM=");
        System.out.println(article.toString());
    }
}
