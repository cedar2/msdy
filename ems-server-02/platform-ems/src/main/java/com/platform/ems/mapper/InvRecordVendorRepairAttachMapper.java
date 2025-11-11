package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvRecordVendorRepairAttach;

/**
 * 供应商返修台账-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-10-27
 */
public interface InvRecordVendorRepairAttachMapper  extends BaseMapper<InvRecordVendorRepairAttach> {


    InvRecordVendorRepairAttach selectInvRecordVendorRepairAttachById(Long repairAttachmentSid);

    List<InvRecordVendorRepairAttach> selectInvRecordVendorRepairAttachList(InvRecordVendorRepairAttach invRecordVendorRepairAttach);

    /**
     * 添加多个
     * @param list List InvRecordVendorRepairAttach
     * @return int
     */
    int inserts(@Param("list") List<InvRecordVendorRepairAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvRecordVendorRepairAttach
    * @return int
    */
    int updateAllById(InvRecordVendorRepairAttach entity);

    /**
     * 更新多个
     * @param list List InvRecordVendorRepairAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<InvRecordVendorRepairAttach> list);


}
