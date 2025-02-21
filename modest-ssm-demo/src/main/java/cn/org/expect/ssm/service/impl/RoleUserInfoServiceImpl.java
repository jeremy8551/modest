package cn.org.expect.ssm.service.impl;

import cn.org.expect.ssm.dao.RoleUserInfoMapper;
import cn.org.expect.ssm.entity.RoleUserInfo;
import cn.org.expect.ssm.service.IRoleUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色成员信息表 服务实现类
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Service
public class RoleUserInfoServiceImpl extends ServiceImpl<RoleUserInfoMapper, RoleUserInfo> implements IRoleUserInfoService {
}
