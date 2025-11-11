package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel
public class ListSeasonRequest implements Serializable {
    private Integer pageNum;
    private Integer pageSize;

    private String productSeasonSid;
    /** 产品季编码 */
    private String productSeasonCode;

    /** 产品季名称 */
    private String productSeasonName;

    /** 年份（年份的编码） */
    private String year;

    /** 季度（季度的编码） */
    private String seasonCode;

    /** 公司（公司档案的sid） */
    private String companySid;

    /** 产品季所属阶段编码 */
    private String productSeasonStage;

    /** 启用/停用状态 */
    private String status;

    /** 备注 */
    private String remark;

    /** 处理状态 */
    private String handleStatus;

    /** 创建人账号 */
    private String creatorAccount;

    /** 创建时间开始 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createDateStart;

    /** 创建时间结束 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createDateEnd;


}
