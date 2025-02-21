package cn.org.expect.ssm.service.impl;

import cn.org.expect.ssm.dao.UserOperationMapper;
import cn.org.expect.ssm.entity.UserOperation;
import cn.org.expect.ssm.service.IUserOperationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户操作记录表 服务实现类
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Service
public class UserOperationServiceImpl extends ServiceImpl<UserOperationMapper, UserOperation> implements IUserOperationService {
}
