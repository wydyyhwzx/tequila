package com.tequila.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Date;

/**
 * Created by wangyudong on 2018/1/10.
 */
public class UserDO {
    private Integer id;
    /*
    *  用户名
    * */
    private String name;
    /*
    *  真实名称
    * */
    private String realName;
    /*
    *  手机号
    * */
    private String phone;
    /*
    *  邮箱
    * */
    private String mail;
    /*
    *  密码
    * */
    private String password;
    /*
    *  认证状态
    * */
    private Integer verifyStatus;
    /*
    *  会员类型
    * */
    private Integer memberType;
    /*
    *  证件类型
    * */
    private Integer cardType;
    /*
    *  证件号
    * */
    private String cardId;
    /*
    *  自媒体类型
    * */
    private Integer mediaType;
    /*
    *  自媒体ID
    * */
    private String mediaId;
    /*
    *  登录认证token
    * */
    private String token;
    /*
    *  登录认证token到期时间
    * */
    private Date tokenExpire;
    /*
    *  用户头像
    * */
    private String profile;
    /*
    *  扩展字段，json格式
    * */
    private String extend;
    @JsonIgnore
    private ObjectNode extendJson;
    private Date gmtCreate;
    private Date gmtModified;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(Integer verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public Integer getMemberType() {
        return memberType;
    }

    public void setMemberType(Integer memberType) {
        this.memberType = memberType;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public Integer getMediaType() {
        return mediaType;
    }

    public void setMediaType(Integer mediaType) {
        this.mediaType = mediaType;
    }
    @JsonIgnore
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    @JsonIgnore
    public Date getTokenExpire() {
        return tokenExpire;
    }

    public void setTokenExpire(Date tokenExpire) {
        this.tokenExpire = tokenExpire;
    }
    @JsonIgnore
    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public ObjectNode extendJsonVaule() {
        return this.extendJson;
    }

    public void transformExtendToJson(ObjectNode extendJson) {
        this.extendJson = extendJson;
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
