package com.platform.ems.domain;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.EmsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 工厂档案对象 s_bas_plant
 *
 * @author linhongwei
 * @date 2021-03-15
 */
@Data
@Accessors(chain = true)
@ApiModel
@TableName(value = "s_bas_plant")
public class BasPlant extends EmsBaseEntity {

    /**
     * 客户端口号
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "客户端口号")
    private String clientId;

    /**
     * 系统ID-工厂档案
     */
    @TableId
    @ApiModelProperty(value = "系统ID-工厂档案")
    private String plantSid;

    public void setPlantCode(String plantCode) {
        if (StrUtil.isNotBlank(plantCode)) {
            plantCode = plantCode.replaceAll("\\s*", "");
        }
        this.plantCode = plantCode;
    }

    public void setPlantName(String plantName) {
        if (StrUtil.isNotBlank(plantName)) {
            plantName = plantName.trim();
        }
        this.plantName = plantName;
    }

    public void setShortName(String shortName) {
        if (StrUtil.isNotBlank(shortName)) {
            shortName = shortName.trim();
        }
        this.shortName = shortName;
    }

    /**
     * 工厂编码
     */
    @Excel(name = "工厂编码")
    @ApiModelProperty(value = "工厂编码")
    private String plantCode;

    /**
     * 工厂名称
     */
    @Excel(name = "工厂名称")
    @ApiModelProperty(value = "工厂名称")
    private String plantName;

    /**
     * 工厂简称
     */
    @Excel(name = "工厂简称")
    @ApiModelProperty(value = "工厂简称")
    private String shortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂简称")
    private String plantShortName;

    @TableField(exist = false)
    @ApiModelProperty(value = "工厂编码")
    private String plantCodeName;

    @Excel(name = "经营类别", dictType = "s_operate_type_plant")
    @ApiModelProperty(value = "经营类别")
    private String operateCategory;

    /**
     * 工厂简介
     */
    @ApiModelProperty(value = "工厂简介")
    private String briefIntroduction;

    /**
     * 是否外协厂
     */
    @Excel(name = "是否外协厂", dictType = "sys_yes_no")
    @ApiModelProperty(value = "是否外协厂")
    private String isSubcontract;

    /**
     * 启用/停用状态
     */
    @Excel(name = "启用/停用", dictType = "s_valid_flag")
    @ApiModelProperty(value = "启用/停用状态")
    private String status;

    @ApiModelProperty(value = "停用说明")
    private String disableRemark;

    /**
     * 处理状态
     */
    @Excel(name = "处理状态", dictType = "s_handle_status")
    @ApiModelProperty(value = "处理状态")
    private String handleStatus;

    /**
     * 厂房属性
     */
    @Excel(name = "厂房属性", dictType = "s_plant_attribute")
    @ApiModelProperty(value = "厂房属性")
    private String plantAttribute;

    /**
     * 所属公司
     */
    @ApiModelProperty(value = "所属公司")
    private String companySid;

    /**
     * 所属公司名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属公司名称")
    private String companyName;

    @TableField(exist = false)
    @Excel(name = "所属公司")
    @ApiModelProperty(value = "所属公司简称")
    private String companyShortName;

    /**
     * 公司代码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    @Excel(name = "厂长")
    @TableField(exist = false)
    @ApiModelProperty(value = "厂长")
    private String plantDirectorName;

    /**
     * 所属供应商
     */
    @ApiModelProperty(value = "所属供应商")
    private String vendorSid;

    /**
     * 供应商名称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商名称")
    private String vendorName;

    @TableField(exist = false)
    @Excel(name = "所属供应商")
    @ApiModelProperty(value = "供应商简称")
    private String vendorShortName;

    /**
     * 供应商编码
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "供应商编码")
    private String vendorCode;

    /**
     * 所属国家
     */
    @ApiModelProperty(value = "所属国家")
    private String country;

    /**
     * 所属省份
     */
    @ApiModelProperty(value = "所属省份")
    private String province;

    /**
     * 所属城市
     */
    @ApiModelProperty(value = "所属城市")
    private String city;

    /**
     * 厂区面积（平方米）
     */
    @ApiModelProperty(value = "厂区面积（平方米）")
    private BigDecimal plantArea;

    /**
     * 厂房面积（平方米）
     */
    @ApiModelProperty(value = "厂房面积（平方米）")
    private BigDecimal factoryArea;

    /**
     * 外协工厂性质
     */
    @Excel(name = "外协厂性质", dictType = "s_outsource_attribute")
    @ApiModelProperty(value = "外协工厂性质")
    private String subcontractAttribute;

    /**
     * 与本部同市
     */
    @Excel(name = "与本部同市", dictType = "sys_yes_no")
    @ApiModelProperty(value = "与本部同市")
    private String isSameCity;

    /**
     * 厂址
     */
    @Excel(name = "厂址")
    @ApiModelProperty(value = "厂址")
    private String address;

    /**
     * 总人数
     */
    @ApiModelProperty(value = "总人数")
    private Long allStaffNum;

    /**
     * 开发部人数
     */
    @ApiModelProperty(value = "开发部人数")
    private Long devStaffNum;

    /**
     * 品管部人数
     */
    @ApiModelProperty(value = "品管部人数")
    private Long qualityStaffNum;

    /**
     * 生产人数
     */
    @ApiModelProperty(value = "生产人数")
    private Long prodStaffNum;

    /**
     * 直接工人数
     */
    @ApiModelProperty(value = "直接工人数")
    private Long ownWorkersNum;

    /**
     * 管理人员数
     */
    @ApiModelProperty(value = "管理人员数")
    private Long managerNum;

    /**
     * 月平均工时（小时）
     */
    @ApiModelProperty(value = "月平均工时（小时）")
    private Long permonthWorkHours;

    /**
     * 月平均工资（元）
     */
    @ApiModelProperty(value = "月平均工资（元）")
    private BigDecimal permonthWage;

    /**
     * 月平均工资（币种）
     */
    @ApiModelProperty(value = "月平均工资（币种）")
    private String averageWageCurrency;

    /**
     * 是否安排住宿
     */
    @ApiModelProperty(value = "是否安排住宿")
    private String isLodging;

    /**
     * 是否有餐补
     */
    @ApiModelProperty(value = "是否有餐补")
    private String isMealAllowance;

    /**
     * 工厂外景照
     */
    @ApiModelProperty(value = "工厂外景照")
    private String picturePath;

    @ApiModelProperty(value = "厂长")
    private String plantDirector;

    @TableField(exist = false)
    @ApiModelProperty(value = "厂长")
    private String plantDirectorList;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人")
    private String creatorAccount;

    @Excel(name = "创建人")
    @TableField(exist = false)
    @ApiModelProperty(value = "创建人")
    private String creatorAccountName;

    /**
     * 创建时间
     */
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    /**
     * 更新人账号
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccount;

    @Excel(name = "更改人")
    @TableField(exist = false)
    @ApiModelProperty(value = "更新人账号")
    private String updaterAccountName;

    /**
     * 更新时间
     */
    @Excel(name = "更改日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    /**
     * 确认人账号
     */
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccount;

    @Excel(name = "确认人")
    @TableField(exist = false)
    @ApiModelProperty(value = "确认人账号")
    private String confirmerAccountName;

    /**
     * 确认时间
     */
    @Excel(name = "确认日期", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "确认时间")
    private Date confirmDate;

    /**
     * 数据源系统
     */
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "数据源系统")
    private String dataSourceSys;


    /**
     * 是否外协厂list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否外协厂list")
    private String[] isSubcontractList;

    /**
     * 处理状态list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "处理状态list")
    private String[] handleStatusList;

    /**
     * 外协工厂性质list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "外协工厂性质list")
    private String[] subcontractAttributeList;

    /**
     * 所属公司list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "所属公司list")
    private String[] companySidList;

    /**
     * 厂房属性list
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "厂房属性list")
    private String[] plantAttributeList;

    /**
     * 系统ID-工厂档案
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "系统ID-工厂档案")
    private String[] plantSidList;

    /**
     * 工厂-联系方式信息对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂-联系方式信息对象")
    private List<BasPlantAddr> basPlantAddrList;

    /**
     * 工厂-擅长品类信息对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂-擅长品类信息对象")
    private List<BasPlantCategory> basPlantCategoryList;

    /**
     * 工厂-生产线信息对象
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "工厂-生产线信息对象")
    private List<BasPlantProdLine> basPlantProdLineList;

    @ApiModelProperty(value = "附件清单")
    @TableField(exist = false)
    private List<BasPlantAttach> basPlantAttachList;
}
