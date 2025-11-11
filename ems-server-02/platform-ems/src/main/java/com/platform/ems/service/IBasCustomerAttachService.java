package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasCustomerAttach;

/**
 * 客户档案-附件Service接口
 *
 * @author chenkw
 * @date 2021-09-15
 */
public interface IBasCustomerAttachService extends IService<BasCustomerAttach>{
    /**
     * 查询客户档案-附件
     *
     * @param attachmentSid 客户档案-附件ID
     * @return 客户档案-附件
     */
    public BasCustomerAttach selectBasCustomerAttachById(Long attachmentSid);

    /**
     * 查询客户档案-附件列表
     *
     * @param basCustomerAttach 客户档案-附件
     * @return 客户档案-附件集合
     */
    public List<BasCustomerAttach> selectBasCustomerAttachList(BasCustomerAttach basCustomerAttach);

    /**
     * 新增客户档案-附件
     *
     * @param basCustomerAttach 客户档案-附件
     * @return 结果
     */
    public int insertBasCustomerAttach(BasCustomerAttach basCustomerAttach);

    /**
     * 修改客户档案-附件
     *
     * @param basCustomerAttach 客户档案-附件
     * @return 结果
     */
    public int updateBasCustomerAttach(BasCustomerAttach basCustomerAttach);

    /**
     * 变更客户档案-附件
     *
     * @param basCustomerAttach 客户档案-附件
     * @return 结果
     */
    public int changeBasCustomerAttach(BasCustomerAttach basCustomerAttach);

    /**
     * 批量删除客户档案-附件
     *
     * @param attachmentSids 需要删除的客户档案-附件ID
     * @return 结果
     */
    public int deleteBasCustomerAttachByIds(List<Long>  attachmentSids);

}