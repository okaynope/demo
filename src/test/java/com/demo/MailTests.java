package com.demo;

import com.demo.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail() {
        mailClient.sendMail("3328568099@qq.com", "TEST", "Welcome.");
    }

    @Test
    public void testThmlMail() {
        Context context = new Context();
        context.setVariable("username", "sunday");

        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);

        mailClient.sendMail("3328568099@qq.com", "THML", process);
    }

}
