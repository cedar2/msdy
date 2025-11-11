package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConReceivePartnerType;

/**
 * 收货方类型Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConReceivePartnerTypeService extends IService<ConReceivePartnerType>{
    /**
     * 查询收货方类型
     * 
     * @param sid 收货方类型ID
     * @return 收货方类型
     */
    public ConReceivePartnerType selectConReceivePartnerTypeById(Long sid);

    public List<ConReceivePartnerType> getList();

    /**
     * 查询收货方类型列表
     * 
     * @param conReceivePartnerType 收货方类型
     * @return 收货方类型集合
     */
    public List<ConReceivePartnerType> selectConReceivePartnerTypeList(ConReceivePartnerType conReceivePartnerType);

    /**
     * 新增收货方类型
     * 
     * @param conReceivePartnerType 收货方类型
     * @return 结果
     */
    public int insertConReceivePartnerType(ConReceivePartnerType conReceivePartnerType);

    /**
     * 修改收货方类型
     * 
     * @param conReceivePartnerType 收货方类型
     * @return 结果
     */
    public int updateConReceivePartnerType(ConReceivePartnerType conReceivePartnerType);

    /**
     * 变更收货方类型
     *
     * @param conReceivePartnerType 收货方类型
     * @return 结果
     */
    public int changeConReceivePartnerType(ConReceivePartnerType conReceivePartnerType);

    /**
     * 批量删除收货方类型
     * 
     * @param sids 需要删除的收货方类型ID
     * @return 结果
     */
    public int deleteConReceivePartnerTypeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conReceivePartnerType
    * @return
    */
    int changeStatus(ConReceivePartnerType conReceivePartnerType);

    /**
     * 更改确认状态
     * @param conReceivePartnerType
     * @return
     */
    int check(ConReceivePartnerType conReceivePartnerType);

}
