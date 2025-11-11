package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurRecordVendorConsign;

/**
 * s_pur_record_vendor_consignService接口
 * 
 * @author linhongwei
 * @date 2021-06-23
 */
public interface IPurRecordVendorConsignService extends IService<PurRecordVendorConsign>{
    /**
     * 查询s_pur_record_vendor_consign
     * 
     * @param recordVendorConsignSid s_pur_record_vendor_consignID
     * @return s_pur_record_vendor_consign
     */
    public PurRecordVendorConsign selectPurRecordVendorConsignById(Long recordVendorConsignSid);

    /**
     * 查询s_pur_record_vendor_consign列表
     * 
     * @param purRecordVendorConsign s_pur_record_vendor_consign
     * @return s_pur_record_vendor_consign集合
     */
    public List<PurRecordVendorConsign> selectPurRecordVendorConsignList(PurRecordVendorConsign purRecordVendorConsign);

    /**
     * 新增s_pur_record_vendor_consign
     * 
     * @param purRecordVendorConsign s_pur_record_vendor_consign
     * @return 结果
     */
    public int insertPurRecordVendorConsign(PurRecordVendorConsign purRecordVendorConsign);

    /**
     * 修改s_pur_record_vendor_consign
     * 
     * @param purRecordVendorConsign s_pur_record_vendor_consign
     * @return 结果
     */
    public int updatePurRecordVendorConsign(PurRecordVendorConsign purRecordVendorConsign);

    /**
     * 变更s_pur_record_vendor_consign
     *
     * @param purRecordVendorConsign s_pur_record_vendor_consign
     * @return 结果
     */
    public int changePurRecordVendorConsign(PurRecordVendorConsign purRecordVendorConsign);

    /**
     * 批量删除s_pur_record_vendor_consign
     * 
     * @param recordVendorConsignSids 需要删除的s_pur_record_vendor_consignID
     * @return 结果
     */
    public int deletePurRecordVendorConsignByIds(List<Long> recordVendorConsignSids);

    /**
    * 启用/停用
    * @param purRecordVendorConsign
    * @return
    */
    int changeStatus(PurRecordVendorConsign purRecordVendorConsign);

    /**
     * 更改确认状态
     * @param purRecordVendorConsign
     * @return
     */
    int check(PurRecordVendorConsign purRecordVendorConsign);

}
