package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeSaleContract;

/**
 * 业务类型_销售合同信息Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeSaleContractService extends IService<ConBuTypeSaleContract>{
    /**
     * 查询业务类型_销售合同信息
     * 
     * @param sid 业务类型_销售合同信息ID
     * @return 业务类型_销售合同信息
     */
    public ConBuTypeSaleContract selectConBuTypeSaleContractById(Long sid);

    /**
     * 查询业务类型_销售合同信息列表
     * 
     * @param conBuTypeSaleContract 业务类型_销售合同信息
     * @return 业务类型_销售合同信息集合
     */
    public List<ConBuTypeSaleContract> selectConBuTypeSaleContractList(ConBuTypeSaleContract conBuTypeSaleContract);

    /**
     * 新增业务类型_销售合同信息
     * 
     * @param conBuTypeSaleContract 业务类型_销售合同信息
     * @return 结果
     */
    public int insertConBuTypeSaleContract(ConBuTypeSaleContract conBuTypeSaleContract);

    /**
     * 修改业务类型_销售合同信息
     * 
     * @param conBuTypeSaleContract 业务类型_销售合同信息
     * @return 结果
     */
    public int updateConBuTypeSaleContract(ConBuTypeSaleContract conBuTypeSaleContract);

    /**
     * 变更业务类型_销售合同信息
     *
     * @param conBuTypeSaleContract 业务类型_销售合同信息
     * @return 结果
     */
    public int changeConBuTypeSaleContract(ConBuTypeSaleContract conBuTypeSaleContract);

    /**
     * 批量删除业务类型_销售合同信息
     * 
     * @param sids 需要删除的业务类型_销售合同信息ID
     * @return 结果
     */
    public int deleteConBuTypeSaleContractByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeSaleContract
    * @return
    */
    int changeStatus(ConBuTypeSaleContract conBuTypeSaleContract);

    /**
     * 更改确认状态
     * @param conBuTypeSaleContract
     * @return
     */
    int check(ConBuTypeSaleContract conBuTypeSaleContract);

}
