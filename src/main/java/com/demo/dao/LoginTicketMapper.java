package com.demo.dao;

import com.demo.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated
public interface LoginTicketMapper {

//    @Insert({
//            "insert into login_ticket(user_id,ticket,login_status,expired) ", "values(#{userId}, #{ticket}, #{loginStatus}, #{expired})"
//    })
//    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

//    @Select({
//            "select * from user_id,ticket,login_status,expired ",
//            "from login_ticket where ticket=${ticket}"
//    })
    LoginTicket selectLoginTicket(String ticket);

//    @Update({
//            "update login_ticket set login_status=#{loginStatus} where ticket=#{ticket}"
//    })
    int updateStatus(String ticket, int loginStatus);

}
