package com.tequila.domain;

/**
 * Created by wangyudong on 2018/1/31.
 * 微信文章
 */
public class WechartArticle {
    /*
    * 详情页地址
    * */
    private String url;
    /*
    * 标题
    * */
    private String tittle;
    /*
    * 发布时间
    * */
    private String time;
    /*
    * 公众号昵称
    * */
    private String nickName;
    /*
    * 内容概述
    * */
    private String content;
    /*
    * 首图，可能没有
    * */
    private String img;

    public WechartArticle() {

    }

    public WechartArticle(String url, String tittle) {
        this.url = url;
        this.tittle = tittle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "WechartArticle{" +
                "url='" + url + '\'' +
                ", tittle='" + tittle + '\'' +
                ", time='" + time + '\'' +
                ", nickName='" + nickName + '\'' +
                ", content='" + content + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
