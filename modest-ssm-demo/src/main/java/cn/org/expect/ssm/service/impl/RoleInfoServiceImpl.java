package cn.org.expect.ssm.service.impl;

import cn.org.expect.ssm.dao.RoleInfoMapper;
import cn.org.expect.ssm.entity.RoleInfo;
import cn.org.expect.ssm.service.IRoleInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色信息表 服务实现类
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Service
public class RoleInfoServiceImpl extends ServiceImpl<RoleInfoMapper, RoleInfo> implements IRoleInfoService {
}
