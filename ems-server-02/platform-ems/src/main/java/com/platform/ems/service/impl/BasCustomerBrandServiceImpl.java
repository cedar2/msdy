package com.platform.ems.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CheckedException;
import com.platform.ems.domain.BasCustomer;
import com.platform.ems.domain.BasCustomerBrand;
import com.platform.ems.mapper.BasCustomerBrandMapper;
import com.platform.ems.mapper.BasCustomerMapper;
import com.platform.ems.service.IBasCustomerBrandService;

/**
 * 客户-客方品牌信息Service业务层处理
 *
 * @author qhq
 * @date 2021-03-24
 */
@Service
@SuppressWarnings("all")
public class BasCustomerBrandServiceImpl extends ServiceImpl<BasCustomerBrandMapper,BasCustomerBrand>  implements IBasCustomerBrandService {

    @Autowired
    private BasCustomerBrandMapper basCustomerBrandMapper;

    @Autowired
    private BasCustomerMapper basCustomerMapper;

    /**
     * 查询客户-客方品牌信息
     *
     * @param clientId 客户-客方品牌信息ID
     * @return 客户-客方品牌信息
     */
    @Override
    public BasCustomerBrand selectBasCustomerBrandById(String customerBrandSid) {
        return basCustomerBrandMapper.selectBasCustomerBrandById(customerBrandSid);
    }

    /**
     * 查询客户-客方品牌信息列表
     *
     * @param basCustomerBrand 客户-客方品牌信息
     * @return 客户-客方品牌信息
     */
    @Override
    public List<BasCustomerBrand> selectBasCustomerBrandList(BasCustomerBrand basCustomerBrand) {
        return basCustomerBrandMapper.selectBasCustomerBrandByCustomerSid(basCustomerBrand.getCustomerSid());
    }

    /**
     * 新增客户-客方品牌信息
     * 需要注意编码重复校验
     * @param basCustomerBrand 客户-客方品牌信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasCustomerBrand(BasCustomerBrand basCustomerBrand) {
        return basCustomerBrandMapper.insert(basCustomerBrand);
    }

    /**
     * 修改客户-客方品牌信息
     *
     * @param basCustomerBrand 客户-客方品牌信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasCustomerBrand(BasCustomerBrand basCustomerBrand) {
        return basCustomerBrandMapper.updateById(basCustomerBrand);
    }

    /**
     * 批量删除客户-客方品牌信息
     *
     * @param clientIds 需要删除的客户-客方品牌信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasCustomerBrandByIds(List<String> clientIds) {
        return basCustomerBrandMapper.deleteBatchIds(clientIds);
    }


}
