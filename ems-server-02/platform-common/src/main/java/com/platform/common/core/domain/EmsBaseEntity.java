package com.platform.common.core.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.core.domain.document.UserOperLog;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ems模块基类
 *
 * @author cwp
 * @date 2021-05-06
 */
@Data
@Accessors(chain = true)
@ApiModel
public class EmsBaseEntity implements Serializable {

    @TableField(exist = false)
    @ApiModelProperty(value ="创建日期开始时间")
    private String beginTime;

    @TableField(exist = false)
    @ApiModelProperty(value ="创建日期结束时间")
    private String endTime;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageNum;

    @TableField(exist = false)
    @ApiModelProperty(value ="每页个数")
    private Integer pageSize;

    @TableField(exist = false)
    @ApiModelProperty(value ="分页起始数")
    private Integer pageBegin;

    @ApiModelProperty(value ="备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value ="二维码：如果二维码要在前端显示需要转成Base64")
    private String qrCode;

    @TableField(exist = false)
    @ApiModelProperty(value ="租户LOGO地址")
    private String logoPicturePath;

    @TableField(exist = false)
    @ApiModelProperty(value ="操作日志列表")
    private List<UserOperLog> operLogList;

    @TableField(exist = false)
    @ApiModelProperty(value = "操作日志最近更改人")
    private String operLogRecentName;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "操作日志最近更改时间")
    private Date operLogRecentDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人userId")
    private Long creatorAccountId;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批节点名称")
    private String approvalNode;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批人ID")
    private String approvalUserId;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批人ID（多选）")
    private String[] approvalUserIdList;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前审批人")
    private String approvalUserName;

    @TableField(exist = false)
    @ApiModelProperty(value ="审批意见")
    private String comment;

    @TableField(exist = false)
    @ApiModelProperty(value = "提交人")
    private String submitUserName;

    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "提交日期")
    private Date submitDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "指示灯：空白-1红色0绿色1橙黄2蓝色3")
    private String light;

    @TableField(exist = false)
    @ApiModelProperty(value ="是否继续操作")
    private Boolean isContinue;

    @TableField(exist = false)
    @ApiModelProperty(value ="数据权限过滤参数")
    private Map<String, Object> params;

    public Integer getPageBegin() {
        if (pageSize != null && pageNum != null){
            return pageSize*(pageNum-1);
        }else {
            return pageBegin;
        }
    }

    public void setPageBegin(Integer pageBegin) {
        if (pageSize != null && pageNum != null){
            this.pageBegin = this.pageSize*(this.pageNum-1);
        }else {
            this.pageBegin = pageBegin;
        }
    }

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    /**
     * 搜索值
     */
    @TableField(exist = false)
    private String searchValue;

    /**
     * 创建者
     */
    @TableField(exist = false)
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date createTime;

    /**
     * 更新者
     *
     */
    @TableField(exist = false)
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date updateTime;

}
