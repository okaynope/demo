package com.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Document(indexName="discusspost", shards = 6, replicas = 3)
public class DiscussPost {

    @Id
    private int id;

    @Field(type = FieldType.Integer, name = "userId")
    private int userId;

    // 词条
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", name = "title")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", name = "content")
    private String content;

    @Field(type = FieldType.Integer, name = "discussPostType")
    private int discussPostType;

    @Field(type = FieldType.Integer, name = "discussPostStatus")
    private int discussPostStatus;

    @Field(type = FieldType.Date, name = "createTime")
    private Date createTime;

    @Field(type = FieldType.Integer, name = "commentCount")
    private int commentCount;

    @Field(type = FieldType.Double, name = "score")
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
