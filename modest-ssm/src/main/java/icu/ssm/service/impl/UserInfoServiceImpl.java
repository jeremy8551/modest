package icu.ssm.service.impl;

import icu.ssm.entity.UserInfo;
import icu.ssm.dao.UserInfoMapper;
import icu.ssm.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}
