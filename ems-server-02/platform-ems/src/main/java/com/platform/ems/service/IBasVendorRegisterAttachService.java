package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorRegisterAttach;

/**
 * 供应商注册-附件Service接口
 *
 * @author chenkw
 * @date 2022-02-21
 */
public interface IBasVendorRegisterAttachService extends IService<BasVendorRegisterAttach> {
    /**
     * 查询供应商注册-附件
     *
     * @param vendorRegisterAttachSid 供应商注册-附件ID
     * @return 供应商注册-附件
     */
    public BasVendorRegisterAttach selectBasVendorRegisterAttachById(Long vendorRegisterAttachSid);

    /**
     * 查询供应商注册-附件列表
     *
     * @param basVendorRegisterAttach 供应商注册-附件
     * @return 供应商注册-附件集合
     */
    public List<BasVendorRegisterAttach> selectBasVendorRegisterAttachList(BasVendorRegisterAttach basVendorRegisterAttach);

    /**
     * 新增供应商注册-附件
     *
     * @param basVendorRegisterAttach 供应商注册-附件
     * @return 结果
     */
    public int insertBasVendorRegisterAttach(BasVendorRegisterAttach basVendorRegisterAttach);

    /**
     * 修改供应商注册-附件
     *
     * @param basVendorRegisterAttach 供应商注册-附件
     * @return 结果
     */
    public int updateBasVendorRegisterAttach(BasVendorRegisterAttach basVendorRegisterAttach);

    /**
     * 变更供应商注册-附件
     *
     * @param basVendorRegisterAttach 供应商注册-附件
     * @return 结果
     */
    public int changeBasVendorRegisterAttach(BasVendorRegisterAttach basVendorRegisterAttach);

    /**
     * 批量删除供应商注册-附件
     *
     * @param vendorRegisterAttachSids 需要删除的供应商注册-附件ID
     * @return 结果
     */
    public int deleteBasVendorRegisterAttachByIds(List<Long> vendorRegisterAttachSids);

    /**
     * 查询主表下的附件清单信息
     *
     * @param vendorRegisterSid 供应商注册ID
     * @return 供应商注册-附件
     */
    public List<BasVendorRegisterAttach> selectBasVendorRegisterAttachListById(Long vendorRegisterSid);

    /**
     * 由主表批量新增供应商注册-附件
     *
     * @param basVendorRegisterAttachList List 供应商注册-附件
     * @return 结果
     */
    public int insertBasVendorRegisterAttach(List<BasVendorRegisterAttach> basVendorRegisterAttachList, Long vendorRegisterSid);

    /**
     * 批量修改供应商注册-附件
     *
     * @param basVendorRegisterAttachList 供应商注册-附件
     * @return 结果
     */
    public int updateBasVendorRegisterAttach(List<BasVendorRegisterAttach> basVendorRegisterAttachList);

    /**
     * 批量修改（删除/新建/更改）供应商注册-附件
     *
     * @param list    供应商注册-附件 (原先的)
     * @param request 供应商注册-附件 (更新后)
     * @return 结果
     */
    public int updateBasVendorRegisterAttach(List<BasVendorRegisterAttach> list, List<BasVendorRegisterAttach> request, Long vendorRegisterSid);

    /**
     * 由主表批量删除供应商注册-附件
     *
     * @param vendorRegisterSids 需要删除的供应商注册IDs
     * @return 结果
     */
    public int deleteBasVendorRegisterAttachListByIds(List<Long> vendorRegisterSids);
}
