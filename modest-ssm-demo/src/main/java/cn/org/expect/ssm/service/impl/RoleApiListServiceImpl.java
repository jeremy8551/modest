package cn.org.expect.ssm.service.impl;

import cn.org.expect.ssm.dao.RoleApiListMapper;
import cn.org.expect.ssm.entity.RoleApiList;
import cn.org.expect.ssm.service.IRoleApiListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * API权限表 服务实现类
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Service
public class RoleApiListServiceImpl extends ServiceImpl<RoleApiListMapper, RoleApiList> implements IRoleApiListService {
}
