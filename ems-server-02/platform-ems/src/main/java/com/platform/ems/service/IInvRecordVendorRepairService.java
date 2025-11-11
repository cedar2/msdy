package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.InvRecordVendorRepair;
import com.platform.ems.domain.InvRecordVendorRepairItem;
import com.platform.ems.domain.dto.request.InvRecordVendorRepairRequest;
import com.platform.ems.domain.dto.response.InvRecordVendorRepairResponse;

/**
 * 供应商返修台账Service接口
 * 
 * @author linhongwei
 * @date 2021-10-27
 */
public interface IInvRecordVendorRepairService extends IService<InvRecordVendorRepair>{
    /**
     * 查询供应商返修台账
     * 
     * @param vendorRepairSid 供应商返修台账ID
     * @return 供应商返修台账
     */
    public InvRecordVendorRepair selectInvRecordVendorRepairById(Long vendorRepairSid);

    public List<InvRecordVendorRepairItem> sort(List<InvRecordVendorRepairItem> items, String type);
    public int judgeRepeat(List<InvRecordVendorRepair> list);

    /**
     * 查询供应商返修台账列表
     * 
     * @param invRecordVendorRepair 供应商返修台账
     * @return 供应商返修台账集合
     */
    public List<InvRecordVendorRepair> selectInvRecordVendorRepairList(InvRecordVendorRepair invRecordVendorRepair);

    public List<InvRecordVendorRepairResponse> report(InvRecordVendorRepairRequest invRecordVendorRepair);
    /**
     * 新增供应商返修台账
     * 
     * @param invRecordVendorRepair 供应商返修台账
     * @return 结果
     */
    public int insertInvRecordVendorRepair(InvRecordVendorRepair invRecordVendorRepair);

    /**
     * 修改供应商返修台账
     * 
     * @param invRecordVendorRepair 供应商返修台账
     * @return 结果
     */
    public int updateInvRecordVendorRepair(InvRecordVendorRepair invRecordVendorRepair);

    /**
     * 变更供应商返修台账
     *
     * @param invRecordVendorRepair 供应商返修台账
     * @return 结果
     */
    public int changeInvRecordVendorRepair(InvRecordVendorRepair invRecordVendorRepair);

    /**
     * 批量删除供应商返修台账
     * 
     * @param vendorRepairSids 需要删除的供应商返修台账ID
     * @return 结果
     */
    public int deleteInvRecordVendorRepairByIds(List<Long> vendorRepairSids);

    /**
    * 启用/停用
    * @param invRecordVendorRepair
    * @return
    */
    int changeStatus(InvRecordVendorRepair invRecordVendorRepair);

    /**
     * 更改确认状态
     * @param invRecordVendorRepair
     * @return
     */
    int check(InvRecordVendorRepair invRecordVendorRepair);

}
