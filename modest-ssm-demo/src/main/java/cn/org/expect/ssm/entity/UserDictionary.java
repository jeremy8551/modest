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
 * 数据字典信息表
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Data
@TableName("USER_DICTIONARY")
@Schema(name = "UserDictionary", description = "数据字典信息表")
public class UserDictionary implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一编号")
    @TableId("ID")
    private String id;

    @Schema(description = "字典编号")
    @TableField("DICT_ID")
    private String dictId;

    @Schema(description = "字典名")
    @TableField("DICT_NAME")
    private String dictName;

    @Schema(description = "选项编号")
    @TableField("OPTION_KEY")
    private String optionKey;

    @Schema(description = "选项名称")
    @TableField("OPTION_NAME")
    private String optionName;

    @Schema(description = "创建时间")
    @TableField("CREATE_TIME")
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间")
    @TableField("UPDATE_TIME")
    private LocalDateTime updateTime;
}
