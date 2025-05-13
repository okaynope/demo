package com.demo.controller;

import com.demo.entity.*;
import com.demo.event.EventProducer;
import com.demo.service.DiscussCommentService;
import com.demo.service.DiscussPostService;
import com.demo.service.LikeService;
import com.demo.service.UserService;
import com.demo.util.CommunityConstant;
import com.demo.util.CommunityUtil;
import com.demo.util.HostHolder;
import com.demo.util.RedisKeyUtil;
import org.aspectj.apache.bcel.generic.MethodGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussCommentService discussCommentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUserThreadLocal();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还未登录");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        // 发帖事件触发
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(discussPost.getId());
        eventProducer.fireEvent(event);

        // 计算贴子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, discussPost.getId());

        return CommunityUtil.getJSONString(0, "发布成功");
    }

    /**
     * 评论：给帖子的评论
     * 回复：给评论的评论
     * 评论列表
     *
     * @param discussPostId 帖子id
     * @param model         返回信息
     * @param page          分页信息
     * @return
     */
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);

        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        // 点赞数
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);
        // 点赞状态
        int likeStatus = hostHolder.getUserThreadLocal() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUserThreadLocal().getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus", likeStatus);

        // 分页
        page.setLimit(5);
        page.setRows(discussCommentService.findDiscussCommentByEntity(ENTITY_TYPE_POST, post.getId()));
        page.setPath("/discuss/detail/" + discussPostId);

        List<DiscussComment> discussCommentsByEntity = discussCommentService.findDiscussCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussCommentVoList = new ArrayList<>();
        if (discussCommentsByEntity != null) {
            for (DiscussComment discussComment :
                    discussCommentsByEntity) {
                // 评论VO
                Map<String, Object> discussCommentVo = new HashMap<>();
                // 评论
                discussCommentVo.put("discussComment", discussComment);
                // 评论作者
                discussCommentVo.put("user", userService.findUserById(discussComment.getUserId()));
                // 点赞数
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, discussComment.getId());
                discussCommentVo.put("likeCount", likeCount);
                // 点赞状态
                likeStatus = hostHolder.getUserThreadLocal() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUserThreadLocal().getId(), ENTITY_TYPE_COMMENT, discussComment.getId());
                discussCommentVo.put("likeStatus", likeStatus);
                // 回复列表
                List<DiscussComment> replyList = discussCommentService.findDiscussCommentsByEntity(
                        ENTITY_TYPE_COMMENT, discussComment.getId(), 0, Integer.MAX_VALUE);
                // 回复VO列表
                List<Map<String, Object>> replyVOList = new ArrayList<>();
                if (replyList != null) {
                    for (DiscussComment reply :
                            replyList) {
                        Map<String, Object> replyVO = new HashMap<>();
                        // 回复
                        replyVO.put("reply", reply);
                        // 回复作者
                        replyVO.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVO.put("target", target);
                        // 点赞数
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeCount", likeCount);
                        // 点赞状态
                        likeStatus = hostHolder.getUserThreadLocal() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUserThreadLocal().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVO.put("likeStatus", likeStatus);

                        replyVOList.add(replyVO);
                    }
                }
                discussCommentVo.put("replys", replyVOList);
                // 回复数
                int replyCount = discussCommentService.findDiscussCommentByEntity(
                        ENTITY_TYPE_COMMENT, discussComment.getId());
                discussCommentVo.put("replyCount", replyCount);

                discussCommentVoList.add(discussCommentVo);
            }
            model.addAttribute("comments", discussCommentVoList);
        }
        return "/site/discuss-detail";
    }

    // 置顶
    @RequestMapping(path = "/top", method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id) {
        DiscussPost discussPost = discussPostService.findDiscussPostById(id);
        int type = discussPost.getDiscussPostType()^1;
        discussPostService.updateType(id, type);

        // 事件发帖触发
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUserThreadLocal().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

    // 加精
    @RequestMapping(path = "/wonderful", method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id) {
        DiscussPost discussPost = discussPostService.findDiscussPostById(id);
        int status = discussPost.getDiscussPostStatus()^1;
        discussPostService.updateStatus(id, status);

        // 事件发帖触发
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUserThreadLocal().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        // 计算贴子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, id);

        return CommunityUtil.getJSONString(0);
    }

    // 删除
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id) {
        discussPostService.updateStatus(id, 2);

        // 事件删帖触发
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUserThreadLocal().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }
}
