package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Pinzhen Chen
 * @Date 2021/2/4 16:40
 */
@Data
public class PurPurchasePriceAttachmentResponse implements Serializable {

    /** 系统ID-采购价信息附件信息 */
    private String purchasePriceAttachmentSid;

    /** 系统ID-物料采购价信息 */
    @Excel(name = "系统ID-物料采购价信息")
    private String purchasePriceInforSid;

    /** 附件类型编码 */
    @Excel(name = "附件类型编码")
    private String fileType;

    /** 附件名称 */
    @Excel(name = "附件名称")
    private String fileName;

    /** 附件路径 */
    @Excel(name = "附件路径")
    private String filePath;

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


}
