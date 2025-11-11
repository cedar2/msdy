package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorRegister;

/**
 * 供应商注册-基础Service接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface IBasVendorRegisterService extends IService<BasVendorRegister>{
    /**
     * 查询供应商注册-基础
     * 
     * @param vendorRegisterSid 供应商注册-基础ID
     * @return 供应商注册-基础
     */
    public BasVendorRegister selectBasVendorRegisterById(Long vendorRegisterSid);

    /**
     * 查询供应商注册-基础
     *
     * @param basVendorRegister 供应商注册-基础（流水号，注册码）
     * @return 供应商注册-基础
     */
    public BasVendorRegister selectBasVendorRegisterByCode(BasVendorRegister basVendorRegister);

    /**
     * 查询供应商注册-基础列表
     * 
     * @param basVendorRegister 供应商注册-基础
     * @return 供应商注册-基础集合
     */
    public List<BasVendorRegister> selectBasVendorRegisterList(BasVendorRegister basVendorRegister);

    /**
     * 新增供应商注册-基础
     * 
     * @param basVendorRegister 供应商注册-基础
     * @return 结果
     */
    public BasVendorRegister insertBasVendorRegister(BasVendorRegister basVendorRegister);

    /**
     * 修改供应商注册-基础
     * 
     * @param basVendorRegister 供应商注册-基础
     * @return 结果
     */
    public int updateBasVendorRegister(BasVendorRegister basVendorRegister);

    /**
     * 变更供应商注册-基础
     *
     * @param basVendorRegister 供应商注册-基础
     * @return 结果
     */
    public int changeBasVendorRegister(BasVendorRegister basVendorRegister);

    /**
     * 批量删除供应商注册-基础
     * 
     * @param vendorRegisterSids 需要删除的供应商注册-基础ID
     * @return 结果
     */
    public int deleteBasVendorRegisterByIds(List<Long>  vendorRegisterSids);

    /**
     * 更改确认状态
     * @param basVendorRegister
     * @return
     */
    int check(BasVendorRegister basVendorRegister);

}
