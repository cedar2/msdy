package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import com.platform.common.core.domain.BaseEntity;
import com.platform.ems.domain.BasMaterialCertificateAttachment;
import com.platform.ems.domain.BasMaterialCertificateFieldValue;
import com.platform.ems.domain.BasMaterialSkuComponent;
import com.platform.ems.domain.BasMaterialSkuDown;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 商品合格证洗唛信息对象 s_bas_material_certificate
 *
 * @author linhongwei
 * @date 2021-03-04
 */
@Data
public class BasMaterialCertificateResponse implements Serializable {
    /** 客户端口号 */
    private String clientId;

    /** 系统ID-商品合格证洗唛信息 */
    @Excel(name = "系统ID-商品合格证洗唛信息")
    private String materialCertificateSid;

    /** 系统ID-商品档案 */
    private String materialSid;

    /** 处理状态 */
    @Excel(name = "处理状态")
    private String handleStatus;

    /** 商品编码 */
    @Excel(name = "商品编码", readConverterExp = "商=品/服务")
    private String materialCode;

    /** 商品名称 */
    @Excel(name = "商品名称", readConverterExp = "商=品/服务")
    private String materialName;

    /** 图片路径 */
    @Excel(name = "图片路径")
    private String picturePath;

    /** 合格证洗唛类型编码 */
    @Excel(name = "合格证洗唛类型编码")
    private String materialCertificateType;

    /** 号型版型编码 */
    @Excel(name = "号型版型编码")
    private String sizePatternType;

    /** 建议零售价（元） */
    @Excel(name = "建议零售价", readConverterExp = "元=")
    private Long suggestedPrice;

    /** 等级编码 */
    @Excel(name = "等级编码")
    private String grade;

    /** 执行标准编码 */
    @Excel(name = "执行标准编码")
    private String executiveStandard;

    /** 执行标准编码（套装下装） */
    @Excel(name = "执行标准编码", readConverterExp = "套=装下装")
    private String executiveStandardBottoms;

    /** 安全类别编码 */
    @Excel(name = "安全类别编码")
    private String safeCategory;

    /** 产地 */
    @Excel(name = "产地")
    private String productPlace;

    /** 检验员 */
    @Excel(name = "检验员")
    private String checker;

    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生产日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date productDate;

    /** 制造商 */
    @Excel(name = "制造商")
    private Long manufacturer;

    /** 检测成分 */
    @Excel(name = "检测成分")
    private String detectComposition;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    /** 创建人账号 */
    @Excel(name = "创建人账号")
    private String creatorAccount;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    /** 更新人账号 */
    @Excel(name = "更新人账号")
    private String updaterAccount;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    /** 确认人账号 */
    @Excel(name = "确认人账号")
    private String confirmerAccount;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmDate;

    /** 数据源系统 */
    @Excel(name = "数据源系统")
    private String dataSourceSys;
}
