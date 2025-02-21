package cn.org.expect.ssm.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * API数据范围表
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Data
@TableName("ROLE_API_SCOPE")
@Schema(name = "RoleApiScope", description = "API数据范围表")
public class RoleApiScope implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一编号")
    @TableId("ID")
    private String id;

    @Schema(description = "角色编号")
    @TableField("ROLE_ID")
    private String roleId;

    @Schema(description = "API信息")
    @TableField("API_URL")
    private String apiUrl;

    @Schema(description = "范围编号")
    @TableField("API_SCOPE")
    private String apiScope;

    @Schema(description = "范围格式")
    @TableField("API_SCOPE_VALUE")
    private String apiScopeValue;
}
