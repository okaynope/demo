package com.demo.dao;

import com.demo.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateDiscussCommentCount(int id, int commentCount);

    int updateType(int id, int discussPostType);

    int updateStatus(int id, int discussPostStatus);

    int updateScore(int id, Double score);

}
