package com.platform.common.core.domain.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * 用户操作日志
 * @author c
 */
@Document
@Data
public class UserOperLog {

    @Id
    private String id;

    @Indexed
    private Long sid;

    @Indexed
    private String title;

    /**
     * 操作类型在枚举中的序号
     */
    private Integer businessType;

    /**
     * 操作类型在枚举中的序号
     */
    private String businessTypeValue;

    @Indexed
    private String operName;

    private String nickName;

    private String before;

    private String after;

    private String remark;

    /**
     * 操作时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss:sss")
    private Date operTime;

    private List<OperMsg> msgList;

    private int  itemNum;

    /**
     * 审批意见
     */
    private String comment;
}

