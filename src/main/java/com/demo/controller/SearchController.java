package com.demo.controller;

import com.demo.entity.DiscussComment;
import com.demo.entity.DiscussPost;
import com.demo.entity.Page;
import com.demo.service.ElasticsearchService;
import com.demo.service.LikeService;
import com.demo.service.UserService;
import com.demo.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) throws IOException {
        // 搜索帖子
        List<Object> list = elasticsearchService.searchDiscussPost(keyword, page.getCurrent()-1, page.getLimit());
        List<DiscussPost> posts = (List<DiscussPost>) list.get(1);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        int searchCount = (int) list.get(0);
        if (list != null) {
            for (DiscussPost post : posts) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                map.put("post", post);
                // 作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        // 分页
        page.setRows(searchCount);
        page.setPath("/search?keyword=" + keyword);
        return "/site/search";
    }
}
