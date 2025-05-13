package com.demo.service;

import com.demo.dao.DiscussCommentMapper;
import com.demo.dao.DiscussPostMapper;
import com.demo.entity.DiscussComment;
import com.demo.entity.DiscussPost;
import com.demo.util.CommunityConstant;
import com.demo.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussCommentService implements CommunityConstant {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussCommentMapper discussCommentMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussComment> findDiscussCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return discussCommentMapper.selectDiscussCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findDiscussCommentByEntity(int entityType, int entityId) {
        return discussCommentMapper.selectDiscussCommentByEntity(entityType, entityId);
    }

    public int findDiscussCommentByUserId(int userId) {
        return discussCommentMapper.selectDiscussCommentByUserId(userId);
    }

    public List<DiscussComment> findDiscussCommentByUserIds(int userId, int offset, int limit) {
        return discussCommentMapper.selectDiscussCommentByUserIds(userId, limit, offset);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public int addDiscussComment(DiscussComment discussComment) {
        if (discussComment == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 添加评论
        discussComment.setContent(HtmlUtils.htmlEscape(discussComment.getContent()));
        discussComment.setContent(sensitiveFilter.filter(discussComment.getContent()));
        int rows = discussCommentMapper.insertDiscussComment(discussComment);

        // 更新帖子评论数量
        if (discussComment.getEntityType() == ENTITY_TYPE_POST) {
            int count = discussCommentMapper.selectDiscussCommentByEntity(discussComment.getEntityType(), discussComment.getEntityId());
            discussPostMapper.updateDiscussCommentCount(discussComment.getEntityId(), count);
        }
        return rows;
    }

    public DiscussComment findDiscussCommentById(int id) {
        return discussCommentMapper.selectDiscussCommentById(id);
    }

}
