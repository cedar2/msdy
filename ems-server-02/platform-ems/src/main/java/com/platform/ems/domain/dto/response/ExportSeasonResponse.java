package com.platform.ems.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;

import java.io.Serializable;
import java.util.Date;


/**
 * 产品季档案对象 s_bas_product_season
 *
 * @author ruoyi
 * @date 2021-01-21
 */
public class ExportSeasonResponse implements Serializable
{

    /** 客户端口号 */
    @Excel(name = "客户端口号")
    private String clientId;

    /** 系统ID-产品季档案 */
    @Excel(name = "系统ID-产品季档案")
    private String productSeasonSid;

    /** 产品季编码 */
    @Excel(name = "产品季编码")
    private String productSeasonCode;

    /** 产品季名称 */
    @Excel(name = "产品季名称")
    private String productSeasonName;

    /** 年份（年份的编码） */
    @Excel(name = "年份")
    private String year;

    /** 季度（季度的编码） */
    @Excel(name = "季度")
    private String seasonCode;

    /** 公司（公司档案的sid） */
    @Excel(name = "公司")
    private String companySid;

    /** 产品季所属阶段编码 */
    @Excel(name = "产品季所属阶段编码")
    private String seasonStage;

    /** 启用/停用状态 */
    @Excel(name = "启用/停用状态")
    private String status;

    /** 处理状态 */
    @Excel(name = "处理状态")
    private String handleStatus;

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

    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    public String getClientId()
    {
        return clientId;
    }
    public void setProductSeasonSid(String productSeasonSid)
    {
        this.productSeasonSid = productSeasonSid;
    }

    public String getProductSeasonSid()
    {
        return productSeasonSid;
    }
    public void setProductSeasonCode(String productSeasonCode)
    {
        this.productSeasonCode = productSeasonCode;
    }

    public String getProductSeasonCode()
    {
        return productSeasonCode;
    }
    public void setProductSeasonName(String productSeasonName)
    {
        this.productSeasonName = productSeasonName;
    }

    public String getProductSeasonName()
    {
        return productSeasonName;
    }
    public void setYear(String year)
    {
        this.year = year;
    }

    public String getYear()
    {
        return year;
    }
    public void setSeasonCode(String seasonCode)
    {
        this.seasonCode = seasonCode;
    }

    public String getSeasonCode()
    {
        return seasonCode;
    }
    public void setCompanySid(String companySid)
    {
        this.companySid = companySid;
    }

    public String getCompanySid()
    {
        return companySid;
    }
    public void setSeasonStage(String seasonStage)
    {
        this.seasonStage = seasonStage;
    }

    public String getSeasonStage()
    {
        return seasonStage;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setHandleStatus(String handleStatus)
    {
        this.handleStatus = handleStatus;
    }

    public String getHandleStatus()
    {
        return handleStatus;
    }
    public void setCreatorAccount(String creatorAccount)
    {
        this.creatorAccount = creatorAccount;
    }

    public String getCreatorAccount()
    {
        return creatorAccount;
    }
    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }

    public Date getCreateDate()
    {
        return createDate;
    }
    public void setUpdaterAccount(String updaterAccount)
    {
        this.updaterAccount = updaterAccount;
    }

    public String getUpdaterAccount()
    {
        return updaterAccount;
    }
    public void setUpdateDate(Date updateDate)
    {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate()
    {
        return updateDate;
    }
    public void setConfirmerAccount(String confirmerAccount)
    {
        this.confirmerAccount = confirmerAccount;
    }

    public String getConfirmerAccount()
    {
        return confirmerAccount;
    }
    public void setConfirmDate(Date confirmDate)
    {
        this.confirmDate = confirmDate;
    }

    public Date getConfirmDate()
    {
        return confirmDate;
    }
    public void setDataSourceSys(String dataSourceSys)
    {
        this.dataSourceSys = dataSourceSys;
    }

    public String getDataSourceSys()
    {
        return dataSourceSys;
    }

    @Override
    public String toString() {
        return "ExportSeasonResponse{" +
                "clientId='" + clientId + '\'' +
                ", productSeasonSid='" + productSeasonSid + '\'' +
                ", productSeasonCode='" + productSeasonCode + '\'' +
                ", productSeasonName='" + productSeasonName + '\'' +
                ", year='" + year + '\'' +
                ", seasonCode='" + seasonCode + '\'' +
                ", companySid='" + companySid + '\'' +
                ", seasonStage='" + seasonStage + '\'' +
                ", status='" + status + '\'' +
                ", handleStatus='" + handleStatus + '\'' +
                ", creatorAccount='" + creatorAccount + '\'' +
                ", createDate=" + createDate +
                ", updaterAccount='" + updaterAccount + '\'' +
                ", updateDate=" + updateDate +
                ", confirmerAccount='" + confirmerAccount + '\'' +
                ", confirmDate=" + confirmDate +
                ", dataSourceSys='" + dataSourceSys + '\'' +
                '}';
    }

    public ExportSeasonResponse(String clientId, String productSeasonSid, String productSeasonCode, String productSeasonName, String year, String seasonCode, String companySid, String seasonStage, String status, String handleStatus, String remark, String creatorAccount, Date createDate, String updaterAccount, Date updateDate, String confirmerAccount, Date confirmDate, String dataSourceSys) {
        this.clientId = clientId;
        this.productSeasonSid = productSeasonSid;
        this.productSeasonCode = productSeasonCode;
        this.productSeasonName = productSeasonName;
        this.year = year;
        this.seasonCode = seasonCode;
        this.companySid = companySid;
        this.seasonStage = seasonStage;
        this.status = status;
        this.handleStatus = handleStatus;
        this.remark = remark;
        this.creatorAccount = creatorAccount;
        this.createDate = createDate;
        this.updaterAccount = updaterAccount;
        this.updateDate = updateDate;
        this.confirmerAccount = confirmerAccount;
        this.confirmDate = confirmDate;
        this.dataSourceSys = dataSourceSys;
    }

    public ExportSeasonResponse() {
    }
}
