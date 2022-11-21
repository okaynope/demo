package com.demo.entity;

import java.util.Date;

public class DiscussPost {

    private int id;
    private int userId;
    private String title;
    private String content;
    private int discussPostType;
    private int discussPostStatus;
    private Date createTime;
    private int commentCount;
    private double score;

    public DiscussPost() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getDiscussPostType() {
        return discussPostType;
    }

    public void setDiscussPostType(int discussPostType) {
        this.discussPostType = discussPostType;
    }

    public int getDiscussPostStatus() {
        return discussPostStatus;
    }

    public void setDiscussPostStatus(int discussPostStatus) {
        this.discussPostStatus = discussPostStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "DiscussPost{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", discussPostType=" + discussPostType +
                ", discussPostStatus=" + discussPostStatus +
                ", createTime=" + createTime +
                ", commentCount=" + commentCount +
                ", score=" + score +
                '}';
    }
}
