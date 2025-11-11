package com.platform.ems.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.common.annotation.Excel;
import com.platform.ems.domain.TecBomAttachment;
import com.platform.ems.domain.TecBomHead;
import com.platform.ems.domain.TecBomItem;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: Hu JJ
 * @date: 2021-02-03
 */
public class TecBomHeadRequest implements Serializable {
    /**
     * 创建时间开始
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间开始", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDateBegin;

    /**
     * 创建时间结束
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间结束", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDateEnd;

    /**
     * 物料档案（商品/服务）编码
     */
    private String materialCode;

    /**
     * BOM组件清单集合
     */
    private List<TecBomItem> bomItems;

    /**
     * BOM附件集合
     */
    private List<TecBomAttachment> bomAttachments;

    /**
     * 物料档案ids
     */
    private String[] materialSids;



}
