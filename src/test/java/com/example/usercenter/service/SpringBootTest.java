package com.example.usercenter.service;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@org.springframework.boot.test.context.SpringBootTest
public class SpringBootTest {

    @Test
    public void testSecret() throws NoSuchAlgorithmException {
        String s = DigestUtils.md5DigestAsHex(("ymy" + "mypassword").getBytes());
        System.out.println(s);
    }
}
