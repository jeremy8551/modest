package cn.org.expect.ssm.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author jeremy8551@qq.com
 * @since 2024-09-07
 */
@Data
@TableName("USER_INFO")
@Schema(name = "UserInfo", description = "用户信息表")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户编号")
    @TableId("USER_ID")
    private String userId;

    @Schema(description = "账号登陆方式")
    @TableField("BUILD_TYPE")
    private String buildType;

    @Schema(description = "微信昵称")
    @TableField("USERNAME")
    private String username;

    @Schema(description = "微信头像地址")
    @TableField("headimgurl")
    private String headimgurl;

    @Schema(description = "微信权限")
    @TableField("privilege")
    private String privilege;

    @Schema(description = "邮箱地址")
    @TableField("EMAIL")
    private String email;

    @Schema(description = "登陆密码")
    @TableField("PASSWORD_HASH")
    private String passwordHash;

    @Schema(description = "名")
    @TableField("FIRST_NAME")
    private String firstName;

    @Schema(description = "姓")
    @TableField("LAST_NAME")
    private String lastName;

    @Schema(description = "生日")
    @TableField("BIRTHDATE")
    private LocalDate birthdate;

    @Schema(description = "性别")
    @TableField("GENDER")
    private String gender;

    @Schema(description = "手机号")
    @TableField("PHONE_NUMBER")
    private String phoneNumber;

    @Schema(description = "居住地址")
    @TableField("ADDRESS")
    private String address;

    @Schema(description = "省份")
    @TableField("PROVINCE")
    private String province;

    @Schema(description = "城市")
    @TableField("CITY")
    private String city;

    @Schema(description = "国家")
    @TableField("COUNTRY")
    private String country;

    @Schema(description = "邮编")
    @TableField("POSTAL_CODE")
    private String postalCode;

    @Schema(description = "用户状态")
    @TableField("STATUS")
    private String status;

    @Schema(description = "创建时间")
    @TableField("CREATE_TIME")
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间")
    @TableField("UPDATE_TIME")
    private LocalDateTime updateTime;
}
