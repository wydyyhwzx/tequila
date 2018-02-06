package com.tequila.domain;

/**
 * Created by wangyudong on 2018/1/31.
 * 微信文章
 */
public class WechartArticle {
    private String url;
    private String tittle;
    private String author;      //作者
    private String time;
    private String nickName;  //公众号昵称
    private String content;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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
                ", author='" + author + '\'' +
                ", time='" + time + '\'' +
                ", nickName='" + nickName + '\'' +
                ", content='" + content + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
