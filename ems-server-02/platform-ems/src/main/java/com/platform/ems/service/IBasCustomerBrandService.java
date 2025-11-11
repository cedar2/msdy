package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasCustomerBrand;

/**
 * 客户-客方品牌信息Service接口
 * 
 * @author qhq
 * @date 2021-03-24
 */
public interface IBasCustomerBrandService extends IService<BasCustomerBrand>{
    /**
     * 查询客户-客方品牌信息
     * 
     * @param clientId 客户-客方品牌信息ID
     * @return 客户-客方品牌信息
     */
    public BasCustomerBrand selectBasCustomerBrandById(String customerBrandSid);

    /**
     * 查询客户-客方品牌信息列表
     * 
     * @param basCustomerBrand 客户-客方品牌信息
     * @return 客户-客方品牌信息集合
     */
    public List<BasCustomerBrand> selectBasCustomerBrandList(BasCustomerBrand basCustomerBrand);

    /**
     * 新增客户-客方品牌信息
     * 
     * @param basCustomerBrand 客户-客方品牌信息
     * @return 结果
     */
    public int insertBasCustomerBrand(BasCustomerBrand basCustomerBrand);

    /**
     * 修改客户-客方品牌信息
     * 
     * @param basCustomerBrand 客户-客方品牌信息
     * @return 结果
     */
    public int updateBasCustomerBrand(BasCustomerBrand basCustomerBrand);

    /**
     * 批量删除客户-客方品牌信息
     * 
     * @param clientIds 需要删除的客户-客方品牌信息ID
     * @return 结果
     */
    public int deleteBasCustomerBrandByIds(List<String>  customerBrandSids);

}
