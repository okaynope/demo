//package com.demo.controller;
//
//import com.demo.entity.DiscussPost;
//import com.demo.entity.Page;
//import com.demo.entity.User;
//import com.demo.service.DiscussPostService;
//import com.demo.service.UserService;
//import org.apache.ibatis.annotations.Mapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Controller
//public class HomeController {
//
//    @Autowired
//    private DiscussPostService discussPostService;
//
//    @Autowired
//    private UserService userService;
//
//    @RequestMapping(path = "/index", method = RequestMethod.GET)
////    @ResponseBody
//    public String getIndexPage(Model model, Page page) {
//        page.setRows(discussPostService.findDiscussPostRows(0));
//        page.setPath("/index");
//
//        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getCurrent(), page.getLimit());
//        List<Map<String, Object>> discussPosts = new ArrayList<>();
//        if (list != null) {
//            for (DiscussPost post : list) {
//                Map<String, Object> map = new HashMap<>();
//                map.put("post", post);
//                User user = userService.findUserById(post.getUserId());
//                map.put("user", user);
//                discussPosts.add(map);
//                System.out.println(user.toString());
//            }
//        }
//        model.addAttribute("discussPosts", discussPosts);
//        return "/index";
//    }
//
//}
