package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.request.InvOwnerMaterialSettleRequest;
import com.platform.ems.domain.dto.response.InvOwnerMaterialSettleReportResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvOwnerMaterialSettleItem;

/**
 * 甲供料结算单-明细Mapper接口
 * 
 * @author c
 * @date 2021-09-13
 */
public interface InvOwnerMaterialSettleItemMapper  extends BaseMapper<InvOwnerMaterialSettleItem> {

    List<InvOwnerMaterialSettleReportResponse> getReport(InvOwnerMaterialSettleRequest InvOwnerMaterialSettleRequest);
    List<InvOwnerMaterialSettleItem> selectInvOwnerMaterialSettleItemById(Long settleSid);

    List<InvOwnerMaterialSettleItem> selectInvOwnerMaterialSettleItemList(InvOwnerMaterialSettleItem invOwnerMaterialSettleItem);

    /**
     * 添加多个
     * @param list List InvOwnerMaterialSettleItem
     * @return int
     */
    int inserts(@Param("list") List<InvOwnerMaterialSettleItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvOwnerMaterialSettleItem
    * @return int
    */
    int updateAllById(InvOwnerMaterialSettleItem entity);

    /**
     * 更新多个
     * @param list List InvOwnerMaterialSettleItem
     * @return int
     */
    int updatesAllById(@Param("list") List<InvOwnerMaterialSettleItem> list);


}
