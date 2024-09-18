package icu.ssm.service.impl;

import icu.ssm.entity.UserOperation;
import icu.ssm.dao.UserOperationMapper;
import icu.ssm.service.IUserOperationService;
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
