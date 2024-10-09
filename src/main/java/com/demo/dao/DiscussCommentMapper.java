package com.demo.dao;

import com.demo.entity.DiscussComment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DiscussCommentMapper {

    List<DiscussComment> selectDiscussCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectDiscussCommentByEntity(int entityType, int entityId);

    int insertDiscussComment(DiscussComment discussComment);

    DiscussComment selectDiscussCommentById(int id);

    int selectDiscussCommentByUserId(int userId);

    List<DiscussComment> selectDiscussCommentByUserIds(int userId, int limit, int offset);

}
