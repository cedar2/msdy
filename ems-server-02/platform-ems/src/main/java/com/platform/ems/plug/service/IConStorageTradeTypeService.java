package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConStorageTradeType;

/**
 * 交易类型Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConStorageTradeTypeService extends IService<ConStorageTradeType>{
    /**
     * 查询交易类型
     * 
     * @param sid 交易类型ID
     * @return 交易类型
     */
    public ConStorageTradeType selectConStorageTradeTypeById(Long sid);

    /**
     * 查询交易类型列表
     * 
     * @param conStorageTradeType 交易类型
     * @return 交易类型集合
     */
    public List<ConStorageTradeType> selectConStorageTradeTypeList(ConStorageTradeType conStorageTradeType);

    /**
     * 新增交易类型
     * 
     * @param conStorageTradeType 交易类型
     * @return 结果
     */
    public int insertConStorageTradeType(ConStorageTradeType conStorageTradeType);

    /**
     * 修改交易类型
     * 
     * @param conStorageTradeType 交易类型
     * @return 结果
     */
    public int updateConStorageTradeType(ConStorageTradeType conStorageTradeType);

    /**
     * 变更交易类型
     *
     * @param conStorageTradeType 交易类型
     * @return 结果
     */
    public int changeConStorageTradeType(ConStorageTradeType conStorageTradeType);

    /**
     * 批量删除交易类型
     * 
     * @param sids 需要删除的交易类型ID
     * @return 结果
     */
    public int deleteConStorageTradeTypeByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conStorageTradeType
    * @return
    */
    int changeStatus(ConStorageTradeType conStorageTradeType);

    /**
     * 更改确认状态
     * @param conStorageTradeType
     * @return
     */
    int check(ConStorageTradeType conStorageTradeType);

}
