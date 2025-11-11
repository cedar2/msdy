package com.platform.system.api.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.BaseEntity;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysDept;
import com.platform.common.core.domain.entity.SysRole;
import com.platform.common.core.domain.model.SysRoleDataAuthFieldValue;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * 用户对象 sys_user
 *
 * @author platform
 */
@SuppressWarnings("ALL")
@TableName(value = "sys_user")
public class SysUserExport extends BaseEntity {

    /* 简道云帐号 */
    public String jiandaoyunUsername;

    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private String[] accountTypeArray;

    /* 云智算调用要拿的token */
    @TableField(exist = false)
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 用户账号
     */
    @Excel(name = "用户账号")
    private String userName;

    /**
     * 用户昵称
     */
    @Excel(name = "用户昵称")
    private String nickName;

    @TableField(exist = false)
    private String nickUserName;

    /**
     * 账号类型
     */
    @Excel(name = "用户类型", dictType = "s_user_type")
    private String userType;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    /** 账号类型 */
    @Excel(name = "账号类型" , dictType = "s_user_account_type")
    private String accountType;

    @TableField(exist = false)
    @Excel(name = "员工")
    private String staffName;

    @TableField(exist = false)
    @Excel(name = "员工存在重复校验是否继续")
    private String staffVerifyContinue;

    @Excel(name = "供应商")
    @TableField(exist = false)
    private String vendorName;

    @Excel(name = "客户")
    @TableField(exist = false)
    private String customerName;

    @Excel(name = "租户ID")
    private String clientId;

    @Excel(name = "企微openid")
    private String workWechatOpenid;

    @Excel(name = "钉钉openid")
    private String dingtalkOpenid;

    @Excel(name = "飞书openid")
    public String feishuOpenId;

    @Excel(name = "公众号openid")
    private String wxGzhOpenid;

    @Excel(name = "手机号码")
    private String phonenumber;

    /**
     * 用户ID
     */
    @Excel(name = "用户编码", cellType = Excel.ColumnType.NUMERIC, prompt = "用户编号")
    private Long userId;

    public List<Long> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Long> userIdList) {
        this.userIdList = userIdList;
    }

    @TableField(exist = false)
    private List<Long> userIdList;

    /**
     * 帐号状态（0正常 1停用）
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    @Excel(name = "创建人")
    @TableField(exist = false)
    private String createByName;

    private String createBy;

    @Excel(name = "创建时间" , dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private Date createTime;

//    -----------------

    @TableField(exist = false)
    private String accountTypeName;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */


    /**
     * 用户性别
     */
    private String sex;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 盐加密
     */
    @TableField(exist = false)
    private String salt;



    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    private String wechatOpenid;

    private String wxXcxOpenid;

    /**
     * 关联供应商
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long vendorSid;

    @TableField(exist = false)
    private Long vendorCode;

    @TableField(exist = false)
    private String vendorShortName;

    @TableField(exist = false)
    private String shortName;

    /** 关联客户 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerSid;

    @TableField(exist = false)
    private String staffCode;

    /** 关联员工 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long staffSid;

    /** 系统默认设置_租户级对象 */
    @TableField(exist = false)
    private SysDefaultSettingClient client;

    /** 数据角色权限字段 */
    @TableField(exist = false)
    private List<SysRoleDataAuthFieldValue> fieldValueList;

    /**
     * 是否租户管理员 1是 0否
     */
    private Integer isAdministrator;
    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginDate;

    /**
     * 部门对象
     */
    @TableField(exist = false)
    //@Excels({ @Excel(name = "部门名称", targetAttr = "deptName", type = Type.EXPORT), @Excel(name = "部门负责人", targetAttr = "leader", type = Type.EXPORT) })
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

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    @TableField(exist = false)
    @ApiModelProperty(value = "行业领域编码")
    private String industryField;

    public String getIndustryField() {
        return industryField;
    }

    public void setIndustryField(String industryField) {
        this.industryField = industryField;
    }

    /**
     * 是否业财一体化
     */
    @TableField(exist = false)
    private String isBusinessFinance;

    public String getIsBusinessFinance() {
        return isBusinessFinance;
    }

    public void setIsBusinessFinance(String isBusinessFinance) {
        this.isBusinessFinance = isBusinessFinance;
    }

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

    public String getErpMaterialSkuEnterModeProject() {
        return erpMaterialSkuEnterModeProject;
    }

    public void setErpMaterialSkuEnterModeProject(String erpMaterialSkuEnterModeProject) {
        this.erpMaterialSkuEnterModeProject = erpMaterialSkuEnterModeProject;
    }

    public String getProductCodeEnterModeProject() {
        return productCodeEnterModeProject;
    }

    public void setProductCodeEnterModeProject(String productCodeEnterModeProject) {
        this.productCodeEnterModeProject = productCodeEnterModeProject;
    }

    public String getVendorEnterRequestInStockOther() {
        return vendorEnterRequestInStockOther;
    }

    public void setVendorEnterRequestInStockOther(String vendorEnterRequestInStockOther) {
        this.vendorEnterRequestInStockOther = vendorEnterRequestInStockOther;
    }

    public String getCustomerEnterRequestInStockOther() {
        return customerEnterRequestInStockOther;
    }

    public void setCustomerEnterRequestInStockOther(String customerEnterRequestInStockOther) {
        this.customerEnterRequestInStockOther = customerEnterRequestInStockOther;
    }

    public String getVendorEnterRequestOutStockOther() {
        return vendorEnterRequestOutStockOther;
    }

    public void setVendorEnterRequestOutStockOther(String vendorEnterRequestOutStockOther) {
        this.vendorEnterRequestOutStockOther = vendorEnterRequestOutStockOther;
    }

    public String getCustomerEnterRequestOutStockOther() {
        return customerEnterRequestOutStockOther;
    }

    public void setCustomerEnterRequestOutStockOther(String customerEnterRequestOutStockOther) {
        this.customerEnterRequestOutStockOther = customerEnterRequestOutStockOther;
    }

    public String getPriceEnterRequestInStockOther() {
        return priceEnterRequestInStockOther;
    }

    public void setPriceEnterRequestInStockOther(String priceEnterRequestInStockOther) {
        this.priceEnterRequestInStockOther = priceEnterRequestInStockOther;
    }

    public String getPriceEnterRequestOutStockOther() {
        return priceEnterRequestOutStockOther;
    }

    public void setPriceEnterRequestOutStockOther(String priceEnterRequestOutStockOther) {
        this.priceEnterRequestOutStockOther = priceEnterRequestOutStockOther;
    }

    /**
     * 盘点是否启用审批
     */
    @TableField(exist = false)
    private String isPandianApproval;

    public String getIsPandianApproval() {
        return isPandianApproval;
    }

    public void setIsPandianApproval(String isPandianApproval) {
        this.isPandianApproval = isPandianApproval;
    }

    /**
     * 是否显示价格-销售价
     */
    @TableField(exist = false)
    private String isViewPrice;

    public String getIsViewPrice() {
        return isViewPrice;
    }
    public void setIsViewPrice(String isViewPrice) {
        this.isViewPrice = isViewPrice;
    }
    /**
     * 是否显示价格-采购价
     */
    @TableField(exist = false)
    private String isViewPricePur;
    public String getIsViewPricePur() {
        return isViewPricePur;
    }

    public void setIsViewPricePur(String isViewPricePur) {
        this.isViewPricePur = isViewPricePur;
    }

    /**
     * 长时间未操作限时
     */
    @TableField(exist = false)
    private Long logonTimeout;

    public Long getLogonTimeout() {
        return logonTimeout;
    }

    public void setLogonTimeout(Long logonTimeout) {
        this.logonTimeout = logonTimeout;
    }

    public SysUserExport() {

    }

    public Integer getIsAdministrator() {
        return isAdministrator;
    }

    public void setIsAdministrator(Integer isAdministrator) {
        this.isAdministrator = isAdministrator;
    }

    public String getWechatOpenid() {
        return wechatOpenid;
    }

    public void setWechatOpenid(String wechatOpenid) {
        this.wechatOpenid = wechatOpenid;
    }

    public String getWorkWechatOpenid() {
        return workWechatOpenid;
    }

    public void setWorkWechatOpenid(String workWechatOpenid) {
        this.workWechatOpenid = workWechatOpenid;
    }

    public void setWorkWechatAgentid(String workWechatAgentid) {
        this.workWechatAgentid = workWechatAgentid;
    }

    public String getWorkWechatAgentid() {
        return workWechatAgentid;
    }

    public String getDingtalkOpenid() {
        return dingtalkOpenid;
    }

    public void setDingtalkOpenid(String dingtalkOpenid) {
        this.dingtalkOpenid = dingtalkOpenid;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public SysUserExport(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickUserName() {
        return nickUserName;
    }

    public void setNickUserName(String nickUserName) {
        this.nickUserName = nickUserName;
    }

    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public SysDept getDept() {
        return dept;
    }

    public void setDept(SysDept dept) {
        this.dept = dept;
    }

    public SysDefaultSettingClient getClient() {
        return client;
    }

    public void setClient(SysDefaultSettingClient client) {
        this.client = client;
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

    public List<SysRoleDataAuthFieldValue> getFieldValueList() {
        return fieldValueList;
    }

    public void setFieldValueList(List<SysRoleDataAuthFieldValue> fieldValueList) {
        this.fieldValueList = fieldValueList;
    }

    public Long[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds) {
        this.roleIds = roleIds;
    }

    public Long[] getPostIds() {
        return postIds;
    }

    public void setPostIds(Long[] postIds) {
        this.postIds = postIds;
    }

    public Long getVendorSid() {
        return vendorSid;
    }

    public void setVendorSid(Long vendorSid) {
        this.vendorSid = vendorSid;
    }

    public Long getCustomerSid() {
        return customerSid;
    }

    public void setCustomerSid(Long customerSid) {
        this.customerSid = customerSid;
    }

    public Long getStaffSid() {
        return staffSid;
    }

    public void setStaffSid(Long staffSid) {
        this.staffSid = staffSid;
    }

    public String getStaffVerifyContinue() {
        return staffVerifyContinue;
    }

    public void setStaffVerifyContinue(String staffVerifyContinue) {
        this.staffVerifyContinue = staffVerifyContinue;
    }

    public String getReturnEmail() {
        return returnEmail;
    }

    public void setReturnEmail(String returnEmail) {
        this.returnEmail = returnEmail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getWxGzhOpenid() {
        return wxGzhOpenid;
    }

    public void setWxGzhOpenid(String wxGzhOpenid) {
        this.wxGzhOpenid = wxGzhOpenid;
    }

    public String getWxXcxOpenid() {
        return wxXcxOpenid;
    }

    public void setWxXcxOpenid(String wxXcxOpenid) {
        this.wxXcxOpenid = wxXcxOpenid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(Long vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorShortName() {
        return vendorShortName;
    }

    public void setVendorShortName(String vendorShortName) {
        this.vendorShortName = vendorShortName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getDingtalkAppkey() {
        return dingtalkAppkey;
    }

    public void setDingtalkAppkey(String dingtalkAppkey) {
        this.dingtalkAppkey = dingtalkAppkey;
    }

    public String getDingtalkAppsecret() {
        return dingtalkAppsecret;
    }

    public void setDingtalkAppsecret(String dingtalkAppsecret) {
        this.dingtalkAppsecret = dingtalkAppsecret;
    }

    public String getDingtalkAgentid() {
        return dingtalkAgentid;
    }

    public void setDingtalkAgentid(String dingtalkAgentid) {
        this.dingtalkAgentid = dingtalkAgentid;
    }

    public String getWorkWechatAppkey() {
        return workWechatAppkey;
    }

    public void setWorkWechatAppkey(String workWechatAppkey) {
        this.workWechatAppkey = workWechatAppkey;
    }

    public String getWorkWechatAppsecret() {
        return workWechatAppsecret;
    }

    public void setWorkWechatAppsecret(String workWechatAppsecret) {
        this.workWechatAppsecret = workWechatAppsecret;
    }

    public String getWxGzhAppkey() {
        return wxGzhAppkey;
    }

    public void setWxGzhAppkey(String wxGzhAppkey) {
        this.wxGzhAppkey = wxGzhAppkey;
    }

    public String getWxGzhAppsecret() {
        return wxGzhAppsecret;
    }

    public void setWxGzhAppsecret(String wxGzhAppsecret) {
        this.wxGzhAppsecret = wxGzhAppsecret;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("userId", getUserId()).append("deptId", getDeptId()).append("userName", getUserName()).append("nickName", getNickName())
                .append("email", getEmail()).append("phonenumber", getPhonenumber()).append("sex", getSex()).append("avatar", getAvatar()).append("password", getPassword()).append("salt", getSalt())
                .append("status", getStatus()).append("delFlag", getDelFlag()).append("loginIp", getLoginIp()).append("loginDate", getLoginDate()).append("createBy", getCreateBy())
                .append("createTime", getCreateTime()).append("updateBy", getUpdateBy()).append("updateTime", getUpdateTime()).append("remark", getRemark()).append("dept", getDept()).toString();
    }

    public String getAccountTypeName() {
        return accountTypeName;
    }

    public void setAccountTypeName(String accountTypeName) {
        this.accountTypeName = accountTypeName;
    }

    public String[] getAccountTypeArray() {
        return accountTypeArray;
    }

    public void setAccountTypeArray(String[] accountTypeArray) {
        this.accountTypeArray = accountTypeArray;
    }

    @Override
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getWorkWechatFlag() {
        return workWechatFlag;
    }

    public void setWorkWechatFlag(String workWechatFlag) {
        this.workWechatFlag = workWechatFlag;
    }

    public String getWxGzhFlag() {
        return wxGzhFlag;
    }

    public void setWxGzhFlag(String wxGzhFlag) {
        this.wxGzhFlag = wxGzhFlag;
    }

    public String getWxXcxFlag() {
        return wxXcxFlag;
    }

    public void setWxXcxFlag(String wxXcxFlag) {
        this.wxXcxFlag = wxXcxFlag;
    }

    public String getDingtalkFlag() {
        return dingtalkFlag;
    }

    public void setDingtalkFlag(String dingtalkFlag) {
        this.dingtalkFlag = dingtalkFlag;
    }
    public String getFeishuOpenId() {
        return feishuOpenId;
    }

    public void setFeishuOpenId(String feishuOpenId) {
        this.feishuOpenId = feishuOpenId;
    }
}
