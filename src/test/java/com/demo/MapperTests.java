package com.demo;

import com.demo.dao.DiscussPostMapper;
import com.demo.dao.LoginTicketMapper;
import com.demo.dao.MessageMapper;
import com.demo.dao.UserMapper;
import com.demo.entity.DiscussPost;
import com.demo.entity.LoginTicket;
import com.demo.entity.Message;
import com.demo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class MapperTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectPosts() {
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(19975);
            System.out.println(discussPost);
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

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setLoginStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSULoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectLoginTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectLoginTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testMessage() {
        List<Message> list = messageMapper.selectConversations(299, 0, 10);
        List<Message> list1 = messageMapper.selectLetters("308_309", 0, 10);
    }
}
