package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecBomHead;
import com.platform.ems.domain.TecBomItem;
import com.platform.ems.domain.TecBomItemReport;
import com.platform.ems.domain.dto.request.InvFundRequest;
import com.platform.ems.domain.dto.request.TecBomHeadReportRequest;
import com.platform.ems.domain.dto.response.InvFundResponse;
import com.platform.ems.domain.dto.response.TecBomHeadMaterialReportResponse;
import com.platform.ems.domain.dto.response.TecBomHeadReportResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物料清单（BOM）组件清单Mapper接口
 *
 * @author qhq
 * @date 2021-03-15
 */
public interface TecBomItemMapper  extends BaseMapper<TecBomItem> {


    TecBomItem selectTecBomItemById(Long clientId);

    List<Long> judgeBomAndMaterial(TecBomItem tecBomItem);

    List<TecBomItem> selectTecBomItemList(TecBomItem tecBomItem);

    List<TecBomItem> exChangeMaterial(TecBomItem tecBomItem);

    List<InvFundResponse> getFund(InvFundRequest invFundRequest);
    List<InvFundResponse> getFundSku2(InvFundRequest invFundRequest);

    /**
     * 添加多个
     * @param list List TecBomItem
     * @return int
     */
    int inserts(@Param("list") List<TecBomItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity TecBomItem
     * @return int
     */
    int updateAllById(TecBomItem entity);

    /**
     * 更新多个
     * @param list List TecBomItem
     * @return int
     */
    int updatesAllById(@Param("list") List<TecBomItem> list);

    /**
     * 根据bomSid查询关联数据
     * @param bomSid
     * @return
     */
    List<TecBomItem> selectBomItemByBomSid(Long bomSid);

    /**
     * 根据bomid删除相关数据
     */
    int deleteItemByBomId(Long bomId);

    /**
     * 物料需求报表
     * @return
     */
    List<TecBomItem> getMaterialRequireList(TecBomHead tecBomHead);

    /**
     * 物料需求报表2
     * @return
     */
    List<TecBomItem> getMaterialRequireList2(TecBomHead tecBomHead);

    List<TecBomHeadReportResponse> report(TecBomHeadReportRequest tecBomHead);

    List<TecBomHeadMaterialReportResponse> reportMaterial(TecBomHeadReportRequest tecBomHead);
}
