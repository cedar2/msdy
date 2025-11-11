package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.request.InvRecordCustomerRepairRequest;
import com.platform.ems.domain.dto.request.InvRecordVendorRepairRequest;
import com.platform.ems.domain.dto.response.InvRecordCustomerRepairResponse;
import com.platform.ems.domain.dto.response.InvRecordVendorRepairResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvRecordCustomerRepairItem;

/**
 * 客户返修台账-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-10-27
 */
public interface InvRecordCustomerRepairItemMapper  extends BaseMapper<InvRecordCustomerRepairItem> {

    List<InvRecordCustomerRepairResponse> report(InvRecordCustomerRepairRequest invRecordCustomerRepairRequest);
    InvRecordCustomerRepairItem selectInvRecordCustomerRepairItemById(Long customerRepairItemSid);

    List<InvRecordCustomerRepairItem> selectInvRecordCustomerRepairItemList(InvRecordCustomerRepairItem invRecordCustomerRepairItem);

    /**
     * 添加多个
     * @param list List InvRecordCustomerRepairItem
     * @return int
     */
    int inserts(@Param("list") List<InvRecordCustomerRepairItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvRecordCustomerRepairItem
    * @return int
    */
    int updateAllById(InvRecordCustomerRepairItem entity);

    /**
     * 更新多个
     * @param list List InvRecordCustomerRepairItem
     * @return int
     */
    int updatesAllById(@Param("list") List<InvRecordCustomerRepairItem> list);


}
