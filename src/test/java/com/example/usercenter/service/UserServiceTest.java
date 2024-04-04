package com.example.usercenter.service;
import java.util.Date;
import java.util.List;

import com.example.usercenter.model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户测试
 *
 * 编写用户测试类主义测试类所在包最好与主文件所在包一致
 */
@SpringBootTest
class UserServiceTest {
    @Resource
    UserService userService;

    //junit5引入的包是jupiter
    @Test
    public void testAdd() {
        User user = new User();
        user.setUsername("镜流");
        user.setUserAccount("ymy123");
        user.setAvatarUrl("https://pic.rmb.bdstatic.com/bjh/bb67772f8a0b1dcbbf074e7e841dc8e86798.png@h_1280");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("1231231");
        user.setEmail("76587@qq.com");
        boolean save = userService.save(user);
        if(save) System.out.println("保存成功！！");
    }

    @Test
    public void testSelect() {
        List<User> users = userService.list();
        users.forEach(user -> System.out.println(user));
    }

    @Test
    void userRegister() {
        //对编写的筛选条件进行测试
        String userAccount = "y";
        String password = "quan123+++";
        String checkPassword = "quan123+++";
        String planetCode = "12345";
        long result = userService.userRegister(userAccount, password, checkPassword,planetCode);
        Assertions.assertNotEquals(-1,result);
    }
}