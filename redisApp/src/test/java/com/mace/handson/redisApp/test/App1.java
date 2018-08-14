package com.mace.handson.redisApp.test;

import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * description: 获取 加密后的密码
 * <br />
 * Created by mace on 10:22 2018/8/1.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class App1 {

    @Autowired
    private StringEncryptor stringEncryptor;


    @Test
    public void test1(){


        String encrypt = stringEncryptor.encrypt("liuye0425@+.");

        System.out.printf("加密后的密码: %s",encrypt);

        System.out.println("解密后的密码: "+stringEncryptor.decrypt(encrypt));
    }
}
