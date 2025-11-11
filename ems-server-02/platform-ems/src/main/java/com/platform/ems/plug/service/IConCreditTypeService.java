package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConCreditType;

/**
 * 信用类型Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConCreditTypeService extends IService<ConCreditType>{
    /**
     * 查询信用类型
     * 
     * @param sid 信用类型ID
     * @return 信用类型
     */
    public ConCreditType selectConCreditTypeById(Long sid);

    /**
     * 查询信用类型列表
     * 
     * @param conCreditType 信用类型
     * @return 信用类型集合
     */
    public List<ConCreditType> selectConCreditTypeList(ConCreditType conCreditType);

    /**
     * 新增信用类型
     * 
     * @param conCreditType 信用类型
     * @return 结果
     */
    public int insertConCreditType(ConCreditType conCreditType);

    /**
     * 修改信用类型
     * 
     * @param conCreditType 信用类型
     * @return 结果
     */
    public int updateConCreditType(ConCreditType conCreditType);

    /**
     * 变更信用类型
     *
     * @param conCreditType 信用类型
     * @return 结果
     */
    public int changeConCreditType(ConCreditType conCreditType);

    /**
     * 批量删除信用类型
     * 
     * @param sids 需要删除的信用类型ID
     * @return 结果
     */
    public int deleteConCreditTypeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conCreditType
    * @return
    */
    int changeStatus(ConCreditType conCreditType);

    /**
     * 更改确认状态
     * @param conCreditType
     * @return
     */
    int check(ConCreditType conCreditType);

}
