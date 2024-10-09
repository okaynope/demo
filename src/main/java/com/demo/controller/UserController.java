package com.demo.controller;

import com.demo.annotation.LoginRequired;
import com.demo.entity.DiscussComment;
import com.demo.entity.DiscussPost;
import com.demo.entity.Page;
import com.demo.entity.User;
import com.demo.service.*;
import com.demo.util.CommunityConstant;
import com.demo.util.CommunityUtil;
import com.demo.util.HostHolder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${communtiy.path.upload}")
    private String uploadPath;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    @Value("${communtiy.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private DiscussCommentService discussCommentService;

    @LoginRequired
    @RequestMapping(path = "/user/setting", method = RequestMethod.GET)
    public String getSettingPage(Model model) {
        // 上传文件名称
        String fileName = CommunityUtil.generateUUID();
        // 设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(0));
        // 生成上传凭证
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);
        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);
        return "/site/setting";
    }

    // 更新头像路径
    @RequestMapping(path = "/user/header/url", method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return CommunityUtil.getJSONString(1, "文件名不能为空");
        }

        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUserThreadLocal().getId(), url);
        return CommunityUtil.getJSONString(0);
    }

    // 废弃
    @LoginRequired
    @RequestMapping(path = "/user/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确");
            return "/site/setting";
        }

        fileName = CommunityUtil.generateUUID() + suffix;

        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常", e);
        }

        User user = hostHolder.getUserThreadLocal();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    // 废弃
    @RequestMapping(path = "/user/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        fileName = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());

        }

    }

    @RequestMapping(path = "/user/repassword", method = RequestMethod.POST)
    public String rePassword(String oldPassword, String newPassword, Model model) {
        User user = hostHolder.getUserThreadLocal();
        Map<String, Object> map = userService.updatePassword(newPassword, oldPassword, user);
        if (map.containsKey("acess")) {
            return "redirect:/logout";
        } else {
            model.addAttribute("oldMsg", map.get("oldMsg"));
            model.addAttribute("newMsg", map.get("newMsg"));
            return "/site/setting";
        }
    }

    // 个人主页
    @RequestMapping(path = "/user/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }

        // 用户
        model.addAttribute("user", user);
        // 用户获赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUserThreadLocal() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUserThreadLocal().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

    // 个人帖子
    @RequestMapping(path = "/user/mypost/{userId}", method = RequestMethod.GET)
    public String getMyPost(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.findUserById(userId);
        int mypostCount = discussPostService.findDiscussPostRows(userId);

        page.setRows(mypostCount);
        page.setPath("/user/mypost/" + userId);

        List<DiscussPost> mypostList = discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit(), 0);
        List<Map<String, Object>> mypostListVO = new ArrayList<>();
        if (mypostList != null) {
            for (DiscussPost post : mypostList) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                mypostListVO.add(map);
            }
        }
        model.addAttribute("mypostCount", mypostCount);
        model.addAttribute("user", user);
        model.addAttribute("myposts", mypostListVO);
        return "/site/my-post";
    }

    // 个人回复
    @RequestMapping(path = "/user/myreply/{userId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.findUserById(userId);

        // 回复数
        int replyCount = discussCommentService.findDiscussCommentByUserId(userId);
        // 分页
        page.setRows(replyCount);
        page.setPath("/user/myreply/" + userId);

        List<DiscussComment> myreplyList = discussCommentService.findDiscussCommentByUserIds(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> myreplys = new ArrayList<>();
        if (myreplyList != null) {
            for (DiscussComment myreply : myreplyList) {
                // 信息
                Map<String, Object> myreplyVO = new HashMap<>();
                if(myreply.getEntityType() == 1) {
                    // 帖子
                    DiscussPost post = discussPostService.findDiscussPostById(myreply.getEntityId());
                    myreplyVO.put("post", post);
                    // 评论
                    myreplyVO.put("myreply", myreply);
                } else if (myreply.getEntityType() == 2) {
                    // 帖子
                    DiscussPost post = discussPostService.findDiscussPostById(discussCommentService.findDiscussCommentById(myreply.getEntityId()).getEntityId());
                    myreplyVO.put("post", post);
                    // 评论
                    myreplyVO.put("myreply", myreply);
                }
                // 回复用户
                myreplyVO.put("touser", userService.findUserById(myreply.getTargetId()) != null ? userService.findUserById(myreply.getTargetId()) : null);
                myreplys.add(myreplyVO);
            }
        }
        model.addAttribute("user", user);
        model.addAttribute("myreplyCount", replyCount);
        model.addAttribute("myreplys", myreplys);
        return "/site/my-reply";
    }
}
