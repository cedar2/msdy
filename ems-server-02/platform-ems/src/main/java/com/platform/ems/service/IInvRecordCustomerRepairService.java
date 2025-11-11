package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvRecordCustomerRepair;
import com.platform.ems.domain.InvRecordCustomerRepairItem;
import com.platform.ems.domain.dto.request.InvRecordCustomerRepairRequest;
import com.platform.ems.domain.dto.response.InvRecordCustomerRepairResponse;

/**
 * 客户返修台账Service接口
 * 
 * @author linhongwei
 * @date 2021-10-27
 */
public interface IInvRecordCustomerRepairService extends IService<InvRecordCustomerRepair>{
    /**
     * 查询客户返修台账
     * 
     * @param customerRepairSid 客户返修台账ID
     * @return 客户返修台账
     */
    public InvRecordCustomerRepair selectInvRecordCustomerRepairById(Long customerRepairSid);

    public List<InvRecordCustomerRepairItem> sort(List<InvRecordCustomerRepairItem> items, String type);
    /**
     * 查询客户返修台账列表
     * 
     * @param invRecordCustomerRepair 客户返修台账
     * @return 客户返修台账集合
     */
    public List<InvRecordCustomerRepair> selectInvRecordCustomerRepairList(InvRecordCustomerRepair invRecordCustomerRepair);

    public List<InvRecordCustomerRepairResponse> report(InvRecordCustomerRepairRequest invRecordCustomerRepair);
    /**
     * 新增客户返修台账
     * 
     * @param invRecordCustomerRepair 客户返修台账
     * @return 结果
     */
    public int insertInvRecordCustomerRepair(InvRecordCustomerRepair invRecordCustomerRepair);

    /**
     * 修改客户返修台账
     * 
     * @param invRecordCustomerRepair 客户返修台账
     * @return 结果
     */
    public int updateInvRecordCustomerRepair(InvRecordCustomerRepair invRecordCustomerRepair);

    /**
     * 变更客户返修台账
     *
     * @param invRecordCustomerRepair 客户返修台账
     * @return 结果
     */
    public int changeInvRecordCustomerRepair(InvRecordCustomerRepair invRecordCustomerRepair);

    /**
     * 批量删除客户返修台账
     * 
     * @param customerRepairSids 需要删除的客户返修台账ID
     * @return 结果
     */
    public int deleteInvRecordCustomerRepairByIds(List<Long> customerRepairSids);

    /**
    * 启用/停用
    * @param invRecordCustomerRepair
    * @return
    */
    int changeStatus(InvRecordCustomerRepair invRecordCustomerRepair);

    /**
     * 更改确认状态
     * @param invRecordCustomerRepair
     * @return
     */
    int check(InvRecordCustomerRepair invRecordCustomerRepair);

    public int judgeRepeat(List<InvRecordCustomerRepair> list);

}
