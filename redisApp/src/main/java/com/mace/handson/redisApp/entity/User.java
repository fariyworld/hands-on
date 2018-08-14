package com.mace.handson.redisApp.entity;


import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;



public class User implements Serializable {


    private static final long serialVersionUID = 1329761320652933551L;
    /**
     * 用户表id
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码，MD5加密
     */
    @JSONField(serialize = false)
    private String password;

    /**
     * 
     */
    private String email;

    /**
     * 
     */
    private String phone;

    /**
     * 找回密码问题
     */
    private String question;

    /**
     * 找回密码答案
     */
    private String answer;

    /**
     * 角色0-管理员,1-普通用户
     */
    private Integer role;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 最后一次更新时间
     */
    private Date update_time;

    /**
     *
     * @mbggenerated
     */
    public User(Integer id, String username, String password, String email, String phone, String question, String answer, Integer role, Date create_time, Date update_time) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.question = question;
        this.answer = answer;
        this.role = role;
        this.create_time = create_time;
        this.update_time = update_time;
    }

    /**
     *
     * @mbggenerated
     */
    public User() {
        super();
    }

    /**
     * 用户表id
     * @return id 用户表id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 用户表id
     * @param id 用户表id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 用户名
     * @return username 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    /**
     * 用户密码，MD5加密
     * @return password 用户密码，MD5加密
     */
    public String getPassword() {
        return password;
    }

    /**
     * 用户密码，MD5加密
     * @param password 用户密码，MD5加密
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * 
     * @return email 
     */
    public String getEmail() {
        return email;
    }

    /**
     * 
     * @param email 
     */
    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    /**
     * 
     * @return phone 
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 
     * @param phone 
     */
    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    /**
     * 找回密码问题
     * @return question 找回密码问题
     */
    public String getQuestion() {
        return question;
    }

    /**
     * 找回密码问题
     * @param question 找回密码问题
     */
    public void setQuestion(String question) {
        this.question = question == null ? null : question.trim();
    }

    /**
     * 找回密码答案
     * @return answer 找回密码答案
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * 找回密码答案
     * @param answer 找回密码答案
     */
    public void setAnswer(String answer) {
        this.answer = answer == null ? null : answer.trim();
    }

    /**
     * 角色0-管理员,1-普通用户
     * @return role 角色0-管理员,1-普通用户
     */
    public Integer getRole() {
        return role;
    }

    /**
     * 角色0-管理员,1-普通用户
     * @param role 角色0-管理员,1-普通用户
     */
    public void setRole(Integer role) {
        this.role = role;
    }

    /**
     * 创建时间
     * @return create_time 创建时间
     */
    public Date getCreate_time() {
        return create_time;
    }

    /**
     * 创建时间
     * @param create_time 创建时间
     */
    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    /**
     * 最后一次更新时间
     * @return update_time 最后一次更新时间
     */
    public Date getUpdate_time() {
        return update_time;
    }

    /**
     * 最后一次更新时间
     * @param update_time 最后一次更新时间
     */
    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    /**
     *
     * @mbggenerated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", username=").append(username);
        sb.append(", password=").append(password);
        sb.append(", email=").append(email);
        sb.append(", phone=").append(phone);
        sb.append(", question=").append(question);
        sb.append(", answer=").append(answer);
        sb.append(", role=").append(role);
        sb.append(", create_time=").append(create_time);
        sb.append(", update_time=").append(update_time);
//        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}