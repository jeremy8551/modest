package cn.org.expect.ssm.service.impl;

import cn.org.expect.ssm.dao.RoleApiResponseMapper;
import cn.org.expect.ssm.entity.RoleApiResponse;
import cn.org.expect.ssm.service.IRoleApiResponseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * API响应处理表 服务实现类
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Service
public class RoleApiResponseServiceImpl extends ServiceImpl<RoleApiResponseMapper, RoleApiResponse> implements IRoleApiResponseService {
}
