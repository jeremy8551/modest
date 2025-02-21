package cn.org.expect.ssm.service.impl;

import cn.org.expect.ssm.dao.RoleMutexInfoMapper;
import cn.org.expect.ssm.entity.RoleMutexInfo;
import cn.org.expect.ssm.service.IRoleMutexInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色互斥表 服务实现类
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Service
public class RoleMutexInfoServiceImpl extends ServiceImpl<RoleMutexInfoMapper, RoleMutexInfo> implements IRoleMutexInfoService {
}
