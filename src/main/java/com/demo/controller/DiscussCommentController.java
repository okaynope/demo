package com.demo.controller;

import com.demo.entity.DiscussComment;
import com.demo.entity.DiscussPost;
import com.demo.entity.Event;
import com.demo.event.EventConsumer;
import com.demo.event.EventProducer;
import com.demo.service.DiscussCommentService;
import com.demo.service.DiscussPostService;
import com.demo.util.CommunityConstant;
import com.demo.util.HostHolder;
import com.demo.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;


@Controller
@RequestMapping("/comment")
public class DiscussCommentController implements CommunityConstant {

    @Autowired
    private DiscussCommentService discussCommentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addDiscussComment(@PathVariable("discussPostId") int discussPostId, DiscussComment discussComment, DiscussComment dis) {
        discussComment.setUserId(hostHolder.getUserThreadLocal().getId());
        discussComment.setCreateTime(new Date());
        discussComment.setCommentStatus(0);
        discussCommentService.addDiscussComment(discussComment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUserThreadLocal().getId())
                .setEntityType(discussComment.getEntityType())
                .setEntityId(discussComment.getEntityId())
                .setData("postId", discussPostId);
        if (discussComment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(discussComment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (discussComment.getEntityType() == ENTITY_TYPE_COMMENT) {
            DiscussComment target = discussCommentService.findDiscussCommentById(discussComment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        if (discussComment.getEntityType() == ENTITY_TYPE_POST) {
            // 触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(hostHolder.getUserThreadLocal().getId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);

            // 计算贴子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
