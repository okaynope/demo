package com.demo.entity;


import java.util.Date;

public class Message {

  private int id;
  private int fromId;
  private int toId;
  private String conversationId;
  private String content;

  @Override
  public String toString() {
    return "Message{" +
            "id=" + id +
            ", fromId=" + fromId +
            ", toId=" + toId +
            ", conversationId='" + conversationId + '\'' +
            ", content='" + content + '\'' +
            ", messageStatus=" + messageStatus +
            ", createTime=" + createTime +
            '}';
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getFromId() {
    return fromId;
  }

  public void setFromId(int fromId) {
    this.fromId = fromId;
  }

  public int getToId() {
    return toId;
  }

  public void setToId(int toId) {
    this.toId = toId;
  }

  public String getConversationId() {
    return conversationId;
  }

  public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getMessageStatus() {
    return messageStatus;
  }

  public void setMessageStatus(int messageStatus) {
    this.messageStatus = messageStatus;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  private int messageStatus;
  private Date createTime;

}
