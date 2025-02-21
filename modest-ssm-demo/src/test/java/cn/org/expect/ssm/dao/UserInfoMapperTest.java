package cn.org.expect.ssm.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;

import cn.org.expect.ssm.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class UserInfoMapperTest {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @DirtiesContext
    @Test
    public void testInsert() {
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

        log.info("删除用户个数: " + this.userInfoMapper.deleteById(user));
        log.info("插入用户个数: " + this.userInfoMapper.insert(user));
        Assert.notNull(this.userInfoMapper.selectById(user.getUserId()), "UserInfo not exists!");
    }
}
