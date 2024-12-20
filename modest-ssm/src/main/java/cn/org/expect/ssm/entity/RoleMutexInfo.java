package cn.org.expect.ssm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 角色互斥表
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Data
@TableName("ROLE_MUTEX_INFO")
@Schema(name = "RoleMutexInfo", description = "角色互斥表")
public class RoleMutexInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一编号")
    @TableId("ID")
    private String id;

    @Schema(description = "角色编号")
    @TableField("ROLE_ID")
    private String roleId;

    @Schema(description = "互斥角色编号")
    @TableField("MUTEX_ROLE")
    private String mutexRole;
}