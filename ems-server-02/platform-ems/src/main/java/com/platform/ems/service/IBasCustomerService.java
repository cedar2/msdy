package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasCustomer;
import com.platform.ems.domain.BasCustomerAddr;
import com.platform.ems.domain.BasCustomerBrandMark;

/**
 * 客户档案Service接口
 *
 * @author qhq
 * @date 2021-03-22
 */
public interface IBasCustomerService extends IService<BasCustomer>{
    /**
     * 查询客户档案
     *
     * @param customerSid 客户档案ID
     * @return 客户档案
     */
    public BasCustomer selectBasCustomerById(Long customerSid);

    /**
     * 查询客户档案列表
     *
     * @param basCustomer 客户档案
     * @return 客户档案集合
     */
    public List<BasCustomer> selectBasCustomerList(BasCustomer basCustomer);

    /**
     * 新增客户档案
     *
     * @param basCustomer 客户档案
     * @return 结果
     */
    public int insertBasCustomer(BasCustomer basCustomer);

    /**
     * 修改客户档案
     *
     * @param basCustomer 客户档案
     * @return 结果
     */
    public int updateBasCustomer(BasCustomer basCustomer);

    /**
     * 批量删除客户档案
     *
     * @param customerSids 需要删除的客户档案ID
     * @return 结果
     */
    public int deleteBasCustomerByIds(List<Long>  customerSids);

	public List<BasCustomer> getCustomerList(BasCustomer basCustomer);

    List<BasCustomerBrandMark> getCustomerBrandMarkList(Long brandSid);


	public int editStatus(BasCustomer basCustomer);

	public int editHandleStatus(BasCustomer basCustomer);

    /**
     * 设置我方跟单员
     * @param basCustomer
     * @return
     */
    public int setOperator(BasCustomer basCustomer);

    /**
     * 设置供方业务员
     * @param basCustomer
     * @return
     */
    public int setOperatorCustomer(BasCustomer basCustomer);

    /**
     * 设置合作状态
     * @param basCustomer
     * @return
     */
    public int setCooperate(BasCustomer basCustomer);

    /**
     * 查询客户档案联系人列表
     *
     * @param addr 客户档案
     * @return 客户档案集合
     */
    public List<BasCustomerAddr> selectBasCustomerAddrList(BasCustomerAddr addr);
}
