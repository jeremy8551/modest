package cn.org.expect.ssm.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 角色继承表
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Data
@TableName("ROLE_EXTEND_INFO")
@Schema(name = "RoleExtendInfo", description = "角色继承表")
public class RoleExtendInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色编号")
    @TableId("ROLE_ID")
    private String roleId;

    @Schema(description = "继承角色编号")
    @TableField("PARENT_ROLE")
    private String parentRole;
}
