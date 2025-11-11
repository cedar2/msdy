package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvRecordCustomerRepairAttach;

/**
 * 客户返修台账-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-10-27
 */
public interface InvRecordCustomerRepairAttachMapper  extends BaseMapper<InvRecordCustomerRepairAttach> {


    InvRecordCustomerRepairAttach selectInvRecordCustomerRepairAttachById(Long repairAttachmentSid);

    List<InvRecordCustomerRepairAttach> selectInvRecordCustomerRepairAttachList(InvRecordCustomerRepairAttach invRecordCustomerRepairAttach);

    /**
     * 添加多个
     * @param list List InvRecordCustomerRepairAttach
     * @return int
     */
    int inserts(@Param("list") List<InvRecordCustomerRepairAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvRecordCustomerRepairAttach
    * @return int
    */
    int updateAllById(InvRecordCustomerRepairAttach entity);

    /**
     * 更新多个
     * @param list List InvRecordCustomerRepairAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<InvRecordCustomerRepairAttach> list);


}
