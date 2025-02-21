package cn.org.expect.ssm.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 角色约束表
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Data
@TableName("ROLE_CONDITION_INFO")
@Schema(name = "RoleConditionInfo", description = "角色约束表")
public class RoleConditionInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一编号")
    @TableId("ID")
    private String id;

    @Schema(description = "角色编号")
    @TableField("ROLE_ID")
    private String roleId;

    @Schema(description = "先决条件角色编号")
    @TableField("CONDITION_ROLE")
    private String conditionRole;
}
