package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.request.InvRecordVendorRepairRequest;
import com.platform.ems.domain.dto.response.InvRecordVendorRepairResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvRecordVendorRepairItem;

/**
 * 供应商返修台账-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-10-27
 */
public interface InvRecordVendorRepairItemMapper  extends BaseMapper<InvRecordVendorRepairItem> {

    List<InvRecordVendorRepairResponse> report(InvRecordVendorRepairRequest invRecordVendorRepairRequest);
    InvRecordVendorRepairItem selectInvRecordVendorRepairItemById(Long vendorRepairItemSid);

    List<InvRecordVendorRepairItem> selectInvRecordVendorRepairItemList(InvRecordVendorRepairItem invRecordVendorRepairItem);

    /**
     * 添加多个
     * @param list List InvRecordVendorRepairItem
     * @return int
     */
    int inserts(@Param("list") List<InvRecordVendorRepairItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvRecordVendorRepairItem
    * @return int
    */
    int updateAllById(InvRecordVendorRepairItem entity);

    /**
     * 更新多个
     * @param list List InvRecordVendorRepairItem
     * @return int
     */
    int updatesAllById(@Param("list") List<InvRecordVendorRepairItem> list);


}
