package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvRecordCustomerRepair;

/**
 * 客户返修台账Mapper接口
 * 
 * @author linhongwei
 * @date 2021-10-27
 */
public interface InvRecordCustomerRepairMapper  extends BaseMapper<InvRecordCustomerRepair> {


    InvRecordCustomerRepair selectInvRecordCustomerRepairById(Long customerRepairSid);
    List<InvRecordCustomerRepair> getItemList(InvRecordCustomerRepair invRecordCustomerRepair);
    List<InvRecordCustomerRepair> selectInvRecordCustomerRepairList(InvRecordCustomerRepair invRecordCustomerRepair);

    /**
     * 添加多个
     * @param list List InvRecordCustomerRepair
     * @return int
     */
    int inserts(@Param("list") List<InvRecordCustomerRepair> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvRecordCustomerRepair
    * @return int
    */
    int updateAllById(InvRecordCustomerRepair entity);

    /**
     * 更新多个
     * @param list List InvRecordCustomerRepair
     * @return int
     */
    int updatesAllById(@Param("list") List<InvRecordCustomerRepair> list);


}
