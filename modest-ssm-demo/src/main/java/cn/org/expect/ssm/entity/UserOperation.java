package cn.org.expect.ssm.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 用户操作记录表
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Data
@TableName("USER_OPERATION")
@Schema(name = "UserOperation", description = "用户操作记录表")
public class UserOperation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一编号")
    @TableId("ID")
    private String id;

    @Schema(description = "用户编号")
    @TableField("USER_ID")
    private String userId;

    @Schema(description = "创建时间")
    @TableField("CREATE_TIME")
    private LocalDateTime createTime;

    @Schema(description = "操作编号")
    @TableField("OPER_ID")
    private String operId;

    @Schema(description = "操作名称")
    @TableField("OPER_NAME")
    private String operName;

    @Schema(description = "操作结果")
    @TableField("OPER_RESULT")
    private String operResult;

    @Schema(description = "异常信息")
    @TableField("OPER_ERROR")
    private String operError;

    @Schema(description = "操作IP地址")
    @TableField("OPER_IP")
    private String operIp;

    @Schema(description = "操作应用编号")
    @TableField("OPER_APP_ID")
    private String operAppId;

    @Schema(description = "操作应用名称")
    @TableField("OPER_APP_NAME")
    private String operAppName;

    @Schema(description = "角色编号")
    @TableField("ROLE_ID")
    private String roleId;

    @Schema(description = "角色名称")
    @TableField("ROLE_NAME")
    private String roleName;
}
