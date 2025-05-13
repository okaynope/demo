package com.demo.util;

import com.demo.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，代替session对象
 */
@Component
public class HostHolder {

    private ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    private int unreadCount = 0;
    private int noticeUnreadCount = 0;
    private int letterUnreadCount = 0;

    public int getNoticeUnreadCount() {
        return noticeUnreadCount;
    }

    public void setNoticeUnreadCount(int noticeUnreadCount) {
        this.noticeUnreadCount = noticeUnreadCount;
    }

    public int getLetterUnreadCount() {
        return letterUnreadCount;
    }

    public void setLetterUnreadCount(int letterUnreadCount) {
        this.letterUnreadCount = letterUnreadCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public User getUserThreadLocal() {
        return userThreadLocal.get();
    }

    public void setUserThreadLocal(User user) {
        userThreadLocal.set(user);
    }

    public void clear() {
        userThreadLocal.remove();
    }
}
