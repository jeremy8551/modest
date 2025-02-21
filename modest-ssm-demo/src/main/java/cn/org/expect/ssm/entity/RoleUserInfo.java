package cn.org.expect.ssm.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 角色成员信息表
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Data
@TableName("ROLE_USER_INFO")
@Schema(name = "RoleUserInfo", description = "角色成员信息表")
public class RoleUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一编号")
    @TableId("ID")
    private String id;

    @Schema(description = "角色编号")
    @TableField("ROLE_ID")
    private String roleId;

    @Schema(description = "成员类型")
    @TableField("ROLE_TYPE")
    private String roleType;

    @Schema(description = "用户编号或岗位编号")
    @TableField("ROLE_MEMBER")
    private String roleMember;
}
