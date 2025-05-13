package com.demo.dao;

import com.demo.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

  // 查询当前用户会话列表(每个会话只返回最新一条消息)
  List<Message> selectConversations(int userId, int offset, int limit);

  // 查询当前用户会话数
  int selectConversationCount(int userId);

  // 查询私信列表
  List<Message> selectLetters(String conversationId, int offset, int limit);

  // 查询单个会话消息数
  int selectLetterCount(String conversationId);

  // 查询未读私信数
  int selectLetterUnreadCount(int userId, String conversationId);

  // 新增消息
  int insertMessage(Message message);

  // 修改消息状态
  int updateStatus(List<Integer> ids, int messageStatus);

  // 查询某主题下最新消息通知
  Message selectLatestNotice(int userId, String topic);

  // 查询某主题所包含的通知数量
  int selectNoticeCount(int userId, String topic);

  // 查询未读通知的消息数量
  int selectNoticeUnreadCount(int userId, String topic);

  // 查询某个主题所包含的通知列表
  List<Message> selectNotices(int userId, String topic, int offset, int limit);

}
