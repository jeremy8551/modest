package cn.org.expect.ssm.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 角色信息表
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Data
@TableName("ROLE_INFO")
@Schema(name = "RoleInfo", description = "角色信息表")
public class RoleInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色编号")
    @TableId("ROLE_ID")
    private String roleId;

    @Schema(description = "角色名")
    @TableField("ROLE_NAME")
    private String roleName;

    @Schema(description = "角色说明")
    @TableField("MEMO")
    private String memo;

    @Schema(description = "角色数量")
    @TableField("ROLE_NUMBER")
    private Integer roleNumber;

    @Schema(description = "状态")
    @TableField("ROLE_STATUS")
    private String roleStatus;
}
