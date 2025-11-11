package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypePurchaseContract;

/**
 * 业务类型_采购合同信息Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypePurchaseContractService extends IService<ConBuTypePurchaseContract>{
    /**
     * 查询业务类型_采购合同信息
     * 
     * @param sid 业务类型_采购合同信息ID
     * @return 业务类型_采购合同信息
     */
    public ConBuTypePurchaseContract selectConBuTypePurchaseContractById(Long sid);

    /**
     * 查询业务类型_采购合同信息列表
     * 
     * @param conBuTypePurchaseContract 业务类型_采购合同信息
     * @return 业务类型_采购合同信息集合
     */
    public List<ConBuTypePurchaseContract> selectConBuTypePurchaseContractList(ConBuTypePurchaseContract conBuTypePurchaseContract);

    /**
     * 新增业务类型_采购合同信息
     * 
     * @param conBuTypePurchaseContract 业务类型_采购合同信息
     * @return 结果
     */
    public int insertConBuTypePurchaseContract(ConBuTypePurchaseContract conBuTypePurchaseContract);

    /**
     * 修改业务类型_采购合同信息
     * 
     * @param conBuTypePurchaseContract 业务类型_采购合同信息
     * @return 结果
     */
    public int updateConBuTypePurchaseContract(ConBuTypePurchaseContract conBuTypePurchaseContract);

    /**
     * 变更业务类型_采购合同信息
     *
     * @param conBuTypePurchaseContract 业务类型_采购合同信息
     * @return 结果
     */
    public int changeConBuTypePurchaseContract(ConBuTypePurchaseContract conBuTypePurchaseContract);

    /**
     * 批量删除业务类型_采购合同信息
     * 
     * @param sids 需要删除的业务类型_采购合同信息ID
     * @return 结果
     */
    public int deleteConBuTypePurchaseContractByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypePurchaseContract
    * @return
    */
    int changeStatus(ConBuTypePurchaseContract conBuTypePurchaseContract);

    /**
     * 更改确认状态
     * @param conBuTypePurchaseContract
     * @return
     */
    int check(ConBuTypePurchaseContract conBuTypePurchaseContract);

}
