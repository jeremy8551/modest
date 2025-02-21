package cn.org.expect.ssm.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import cn.org.expect.springboot.starter.configuration.ModestProperties;
import cn.org.expect.ssm.dao.UserInfoMapper;
import cn.org.expect.ssm.entity.UserInfo;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.IO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HelpController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private ScriptEngine engine;

    @Autowired
    ModestProperties modestProperties;

    @RequestMapping("/help")
    public String help() throws ScriptException {
        engine.eval("echo 测试脚本引擎输出");

        UserInfo user = new UserInfo();
        user.setUserId("userID001");
        user.setUsername("可发人员");
        user.setCountry("中国");
        user.setProvince("北京");
        user.setCity("北京");
        user.setPostalCode("100222");
        user.setFirstName("ming");
        user.setLastName("xing");
        user.setBirthdate(LocalDate.now());
        user.setEmail("xxxxxx@qq.com");
        user.setBuildType("self");
        user.setAddress("朝阳区");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setStatus("00");
        user.setPrivilege("");
        user.setPhoneNumber("1850000000");
        user.setPasswordHash("xxxxxxxxx");
        user.setHeadimgurl("https://xxxxx/xxxx/xx.gif");

        log.info("insertObj: {}", user);
        log.info("删除用户个数: {}", this.userInfoMapper.deleteById(user));
        log.info("插入用户个数: {}", this.userInfoMapper.insert(user));

        UserInfo queryObj = this.userInfoMapper.selectById(user.getUserId());
        Assert.notNull(queryObj, "UserInfo not exists!");
        log.info("queryObj: {}", queryObj);

        Ensure.equals(IO.getByteArrayLength(), 1024 * 1024);
        Ensure.equals(IO.getCharArrayLength(), 1024 * 1024);

        Assert.isTrue(!modestProperties.getLog().isPrintTrace(), "ModestProperties not correct!");
        return "help";
    }
}
