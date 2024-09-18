package icu.ssm.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import cn.org.expect.springboot.starter.EasyetlProperties;
import icu.ssm.dao.UserInfoMapper;
import icu.ssm.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelpController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private ScriptEngine engine;

    @Autowired
    EasyetlProperties easyetlProperties;

    @RequestMapping("/help")
    public String help() throws ScriptException {
        engine.eval("echo 测试脚本引擎输出");

        UserInfo user = new UserInfo();
        user.setUserId("userID001");
        user.setUsername("测试人员");
        user.setCountry("中国");
        user.setProvince("北京");
        user.setCity("北京");
        user.setPostalCode("100222");
        user.setFirstName("ming");
        user.setLastName("xing");
        user.setBirthdate(LocalDate.now());
        user.setEmail("xxxxxx@qq.com");
        user.setBuildType("self");
        user.setAddress("朝阳区光辉里");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setStatus("00");
        user.setPrivilege("");
        user.setPhoneNumber("1850000000");
        user.setPasswordHash("xxxxxxxxx");
        user.setHeadimgurl("https://xxxxx/xxxx/xx.gif");

        System.out.println("insertObj: " + user);
        System.out.println("删除用户个数: " + this.userInfoMapper.deleteById(user));
        System.out.println("插入用户个数: " + this.userInfoMapper.insert(user));

        UserInfo queryObj = this.userInfoMapper.selectById(user.getUserId());
        Assert.notNull(queryObj, "UserInfo not exists!");
        System.out.println("queryObj: " + queryObj);

        Assert.isTrue(!easyetlProperties.getLog().isPrintTrace(), "easyetlProperties not correct!");
        return "help";
    }

}
