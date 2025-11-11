package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendor;
import com.platform.ems.domain.BasVendorAddr;
import org.springframework.web.multipart.MultipartFile;

/**
 * 供应商档案Service接口
 *
 * @author qhq
 * @date 2021-03-12
 */
public interface IBasVendorService extends IService<BasVendor>{
    /**
     * 查询供应商档案
     *
     * @param sid 供应商档案ID
     * @return 供应商档案
     */
    public BasVendor selectBasVendorBySid(Long sid);

    /**
     * 查询供应商档案列表
     *
     * @param basVendor 供应商档案
     * @return 供应商档案集合
     */
    public List<BasVendor> selectBasVendorList(BasVendor basVendor);

    /**
     * 新增供应商档案
     *
     * @param basVendor 供应商档案
     * @return 结果
     */
    public int insertBasVendor(BasVendor basVendor);


    public int importData(MultipartFile file);
    /**
     * 修改供应商档案
     *
     * @param basVendor 供应商档案
     * @return 结果
     */
    public int updateBasVendor(BasVendor basVendor);

    /**
     * 批量删除供应商档案
     *
     * @param vendorSids 需要删除的供应商档案ID
     * @return 结果
     */
    public int deleteBasVendorByIds(List<Long>  vendorSids);

    /**
     * 删除供应商档案信息
     *
     * @param vendorSid 供应商档案ID
     * @return 结果
     */
    public int deleteBasVendorById(Long vendorSid);

    /**
     * 查询供应商档案sid、名称及简称，用于下拉框
     */
    public List<BasVendor> getVendorList(BasVendor basVendor);

    /**
     * 修改状态
     * @param basVendor
     * @return
     */
    public int editStatus(BasVendor basVendor);

    /**
     * 修改状态
     * @param basVendor
     * @return
     */
    public int editHandleStatus(BasVendor basVendor);

    /**
     * 设置我方跟单员
     * @param basVendor
     * @return
     */
    public int setOperator(BasVendor basVendor);

    /**
     * 设置供方业务员
     * @param basVendor
     * @return
     */
    public int setOperatorVendor(BasVendor basVendor);

    /**
     * 设置合作状态
     * @param basVendor
     * @return
     */
    public int setCooperate(BasVendor basVendor);

    /**
     * 根据id查名称
     * @param sid
     * @return
     */
    public String getCompanyNameBySid(Long sid);

    /**
     * 根据id查名称
     * @param sid
     * @return
     */
    public String getCustomerNameBySid(Long sid);

    /**
     * 根据id查名称
     * @param sid
     * @return
     */
    public String getVendorNameBySid(Long sid);

    /**
     * 查询供应商档案联系人列表
     *
     * @param addr 供应商档案
     * @return 供应商档案集合
     */
    public List<BasVendorAddr> selectBasVendorAddrList(BasVendorAddr addr);
}
