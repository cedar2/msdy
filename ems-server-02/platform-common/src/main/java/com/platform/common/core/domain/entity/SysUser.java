package com.platform.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.common.core.domain.model.SysRoleDataAuthFieldValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * 用户信息对象 sys_user
 *
 * @author qhq
 * @date 2021-09-13
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "sys_user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysUser extends EmsBaseEntity {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "sid数组")
    @TableField(exist = false)
    private Long[] userIdList;

    /**
     * 用户账号
     */
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    @Excel(name = "用户账号")
    @ApiModelProperty(value = "用户账号")
    private String userName;

    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "角色ID")
    private Long roleId;

    @TableField(exist = false)
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    /**
     * 用户昵称
     */
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    @Excel(name = "用户昵称")
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    /**
     * 用户类型（00系统用户）
     */
    @Excel(name = "用户类型（00系统用户）")
    @ApiModelProperty(value = "用户类型（00系统用户）")
    private String userType;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    @Excel(name = "用户邮箱")
    @ApiModelProperty(value = "用户邮箱")
    private String email;

    /**
     * 手机号码
     */
    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    @Excel(name = "手机号码")
    @ApiModelProperty(value = "手机号码")
    private String phonenumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    @Excel(name = "用户性别（0男 1女 2未知）")
    @ApiModelProperty(value = "用户性别（0男 1女 2未知）")
    private String sex;

    /**
     * 头像地址
     */
    @Excel(name = "头像地址")
    @ApiModelProperty(value = "头像地址")
    private String avatar;

    /**
     * 密码
     */
    @Excel(name = "密码")
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    @Excel(name = "帐号状态（0正常 1停用）", dictType = "s_valid_flag")
    @ApiModelProperty(value = "帐号状态（0正常 1停用）")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    /**
     * 最后登录IP
     */
    @Excel(name = "最后登录IP")
    @ApiModelProperty(value = "最后登录IP")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "最后登录时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "最后登录时间")
    private Date loginDate;

    /**
     * 租户id
     */
    @Excel(name = "租户id")
    @ApiModelProperty(value = "租户id")
    private String clientId;

    /**
     * 账号类型
     */
    @Excel(name = "账号类型")
    @ApiModelProperty(value = "账号类型")
    private String accountType;

    /**
     * 关联供应商
     */
    @Excel(name = "关联供应商")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联供应商")
    private Long vendorSid;

    /**
     * 关联客户
     */
    @Excel(name = "关联客户")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联客户")
    private Long customerSid;

    /**
     * 关联员工
     */
    @Excel(name = "关联员工")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "关联员工")
    private Long staffSid;

    /**
     * 固定电话
     */
    @Excel(name = "固定电话")
    @ApiModelProperty(value = "固定电话")
    private String telephone;

    /**
     * 传真
     */
    @Excel(name = "传真")
    @ApiModelProperty(value = "传真")
    private String fax;

    /**
     * 联系地址
     */
    @Excel(name = "联系地址")
    @ApiModelProperty(value = "联系地址")
    private String address;

    /**
     * 操作代理人
     */
    @Excel(name = "操作代理人")
    @ApiModelProperty(value = "操作代理人")
    private String opeAgentId;

    /**
     * 操作代理有效期(起)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "操作代理有效期(起)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "操作代理有效期(起)")
    private Date opeAgentVDateS;

    /**
     * 操作代理有效期(止)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @Excel(name = "操作代理有效期(止)", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty(value = "操作代理有效期(止)")
    private Date opeAgentVDateE;

    /**
     * 是否管理员帐号
     */
    @Excel(name = "是否管理员帐号")
    @ApiModelProperty(value = "是否管理员帐号")
    private String isAdministrator;

    /**
     * 部门ID
     */
    @Excel(name = "部门ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "部门ID")
    private Long deptId;

    /**
     * 微信openId
     */
    @Excel(name = "微信openId")
    @ApiModelProperty(value = "微信openId")
    private String wechatOpenid;

    /**
     * 企业微信openId
     */
    @Excel(name = "企业微信openId")
    @ApiModelProperty(value = "企业微信openId")
    private String workWechatOpenid;

    /**
     * 钉钉id
     */
    @Excel(name = "钉钉id")
    @ApiModelProperty(value = "钉钉id")
    private String dingtalkOpenid;

    /**
     * 验证码
     */
    @TableField(exist = false)
    private String code;

    /**
     * 唯一标识
     */
    @TableField(exist = false)
    private String uuid;

    /**
     * 通过岗位查询员工关联对应的用户
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "通过岗位查询员工关联对应的用户岗位sid")
    private Long[] positionSidList;

    /**
     * 关联员工所在的岗位
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "关联员工所在的岗位")
    private Long positionSid;

    /**
     * 飞书appId
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "飞书appId")
    private String feishuAppId;

    /**
     * 飞书的appSecret
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "飞书的appSecret")
    private String feishuAppSecret;

    /**
     * 飞书的open_id
     * */
    @ApiModelProperty(value = "飞书的openid")
    public String feishuOpenId;

    @Excel(name = "公众号openid")
    private String wxGzhOpenid;

    /**
     * 简道云帐号
     * */
    @ApiModelProperty(value = "简道云帐号")
    public String jiandaoyunUsername;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更改人")
    private String updateBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更改时间")
    private Date updateTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "员工存在重复校验是否继续")
    private String staffVerifyContinue;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作角色")
    private List<SysUserRole> userRoleList;

    @TableField(exist = false)
    @ApiModelProperty(value = "数据角色")
    private List<SysUserDataRole> userDataRoleList;

    ////////////////////////////////////

    @TableField(exist = false)
    private String[] accountTypeArray;

    /**
     *  云智算调用要拿的token */
    @TableField(exist = false)
    private String accessToken;

    @TableField(exist = false)
    private String nickUserName;

    @TableField(exist = false)
    @Excel(name = "员工")
    private String staffName;

    @Excel(name = "供应商")
    @TableField(exist = false)
    private String vendorName;

    @Excel(name = "客户")
    @TableField(exist = false)
    private String customerName;

    @Excel(name = "创建人")
    @TableField(exist = false)
    private String createByName;

    @TableField(exist = false)
    private String accountTypeName;

    /**
     * 盐加密
     */
    @TableField(exist = false)
    private String salt;

    private String wxXcxOpenid;

    @TableField(exist = false)
    private Long vendorCode;

    @TableField(exist = false)
    private String vendorShortName;

    @TableField(exist = false)
    private String shortName;

    @TableField(exist = false)
    private String staffCode;

    /** 系统默认设置_租户级对象 */
    @TableField(exist = false)
    private SysDefaultSettingClient client;

    /** 数据角色权限字段 */
    @TableField(exist = false)
    private List<SysRoleDataAuthFieldValue> fieldValueList;

    /**
     * 部门对象
     */
    @TableField(exist = false)
    private SysDept dept;

    /**
     * 角色对象
     */
    @TableField(exist = false)
    private List<SysRole> roles;

    /**
     * 角色组
     */
    @TableField(exist = false)
    private Long[] roleIds;

    /**
     * 岗位组
     */
    @TableField(exist = false)
    private Long[] postIds;

    @TableField(exist = false)
    private String newpassword;

    @TableField(exist = false)
    private String returnEmail;

    /**
     * 1-钉钉、2-企微、3-微信公众号
     */
    @TableField(exist = false)
    private String type;

    /**
     * 钉钉公匙
     */
    @TableField(exist = false)
    private String dingtalkAppkey;

    /**
     * 钉钉私匙
     */
    @TableField(exist = false)
    private String dingtalkAppsecret;

    /**
     * 钉钉Agentid
     */
    @TableField(exist = false)
    private String dingtalkAgentid;

    /**
     * 企微公匙
     */
    @TableField(exist = false)
    private String workWechatAppkey;

    /**
     * 企微私匙
     */
    @TableField(exist = false)
    private String workWechatAppsecret;

    /**
     * 企微Agentid
     */
    @TableField(exist = false)
    private String workWechatAgentid;

    /**
     * 微信公众号公匙
     */
    @TableField(exist = false)
    private String wxGzhAppkey;

    /**
     * 微信公众号私匙
     */
    @TableField(exist = false)
    private String wxGzhAppsecret;

    /**
     * 移动端消息推送人
     */
    @TableField(exist = false)
    private String touser;

    /**
     * 企微
     */
    @TableField(exist = false)
    private String workWechatFlag;

    /**
     * 公众号
     */
    @TableField(exist = false)
    private String wxGzhFlag;

    /**
     * 小程序
     */
    @TableField(exist = false)
    private String wxXcxFlag;

    /**
     * 钉钉
     */
    @TableField(exist = false)
    private String dingtalkFlag;

    /**
     * 是否业财一体化
     */
    @TableField(exist = false)
    private String clientType;

    @TableField(exist = false)
    @ApiModelProperty(value = "行业领域编码")
    private String industryField;

    /**
     * 是否业财一体化
     */
    @TableField(exist = false)
    private String isBusinessFinance;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商填写要求(其它入库)（数据字典的键值或配置档案的编码）")
    private String vendorEnterRequestInStockOther;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户填写要求(其它入库)（数据字典的键值或配置档案的编码）")
    private String customerEnterRequestInStockOther;

    @TableField(exist = false)
    @ApiModelProperty(value = "供应商填写要求(其它出库)（数据字典的键值或配置档案的编码）")
    private String vendorEnterRequestOutStockOther;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户填写要求(其它出库)（数据字典的键值或配置档案的编码）")
    private String customerEnterRequestOutStockOther;

    @TableField(exist = false)
    @ApiModelProperty(value = "价格填写要求(其它入库)（数据字典的键值或配置档案的编码）")
    private String priceEnterRequestInStockOther;

    @TableField(exist = false)
    @ApiModelProperty(value = "价格填写要求(其它出库)（数据字典的键值或配置档案的编码）")
    private String priceEnterRequestOutStockOther;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品SKU编码(ERP)录入方式(项目)")
    private String erpMaterialSkuEnterModeProject;

    @TableField(exist = false)
    @ApiModelProperty(value = "商品款号/SPU号录入方式(项目)")
    private String productCodeEnterModeProject;

    /**
     * 盘点是否启用审批
     */
    @TableField(exist = false)
    private String isPandianApproval;

    /**
     * 是否显示价格-销售价
     */
    @TableField(exist = false)
    private String isViewPrice;

    /**
     * 是否显示价格-采购价
     */
    @TableField(exist = false)
    private String isViewPricePur;

    /**
     * 长时间未操作限时
     */
    @TableField(exist = false)
    private Long logonTimeout;

    public SysUser() {

    }

    public SysUser(Long userId) {
        this.userId = userId;
    }


    public boolean isAdmin() {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

}
