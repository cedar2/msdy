package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.InvRecordVendorRepairItem;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvRecordVendorRepair;

/**
 * 供应商返修台账Mapper接口
 * 
 * @author linhongwei
 * @date 2021-10-27
 */
public interface InvRecordVendorRepairMapper  extends BaseMapper<InvRecordVendorRepair> {


    InvRecordVendorRepair selectInvRecordVendorRepairById(Long vendorRepairSid);

    List<InvRecordVendorRepair> getItemList(InvRecordVendorRepair invRecordVendorRepair);

    List<InvRecordVendorRepair> selectInvRecordVendorRepairList(InvRecordVendorRepair invRecordVendorRepair);

    /**
     * 添加多个
     * @param list List InvRecordVendorRepair
     * @return int
     */
    int inserts(@Param("list") List<InvRecordVendorRepair> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvRecordVendorRepair
    * @return int
    */
    int updateAllById(InvRecordVendorRepair entity);

    /**
     * 更新多个
     * @param list List InvRecordVendorRepair
     * @return int
     */
    int updatesAllById(@Param("list") List<InvRecordVendorRepair> list);


}
