package com.demo.dao;

import com.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateUserStatus(int id, int userStatus);

    int updatePassword(int id, int password);

    int updateHeader(int id, int headerUrl);

}
