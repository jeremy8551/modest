package cn.org.expect.ssm.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * API响应处理表
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Data
@TableName("ROLE_API_RESPONSE")
@Schema(name = "RoleApiResponse", description = "API响应处理表")
public class RoleApiResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一编号")
    @TableId("ID")
    private String id;

    @Schema(description = "角色编号")
    @TableField("ROLE")
    private String role;

    @Schema(description = "API信息")
    @TableField("API_URL")
    private String apiUrl;

    @Schema(description = "处理规则类的全名")
    @TableField("CLASS_NAME")
    private String className;

    @Schema(description = "序号")
    @TableField("ORDER")
    private Integer order;
}
