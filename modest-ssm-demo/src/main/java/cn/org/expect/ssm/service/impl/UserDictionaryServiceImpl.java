package cn.org.expect.ssm.service.impl;

import cn.org.expect.ssm.dao.UserDictionaryMapper;
import cn.org.expect.ssm.entity.UserDictionary;
import cn.org.expect.ssm.service.IUserDictionaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 数据字典信息表 服务实现类
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Service
public class UserDictionaryServiceImpl extends ServiceImpl<UserDictionaryMapper, UserDictionary> implements IUserDictionaryService {
}
