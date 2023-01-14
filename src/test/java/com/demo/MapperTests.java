package com.demo;

import com.demo.dao.DiscussPostMapper;
import com.demo.dao.UserMapper;
import com.demo.entity.DiscussPost;
import com.demo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MapperTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for(DiscussPost post : discussPosts){
            System.out.println();
        }
    }

    @Test
    public void testSelectPostRows() {
        int discussPosts = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(discussPosts);
    }

    @Test
    public void testSelectUser() {
        User user = userMapper.selectByName("12");
        System.out.println(user);
    }
}
