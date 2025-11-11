package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeSampleLendreturn;

/**
 * 单据类型_样品借还单Service接口
 * 
 * @author linhongwei
 * @date 2022-01-24
 */
public interface IConDocTypeSampleLendreturnService extends IService<ConDocTypeSampleLendreturn>{
    /**
     * 查询单据类型_样品借还单
     * 
     * @param sid 单据类型_样品借还单ID
     * @return 单据类型_样品借还单
     */
    public ConDocTypeSampleLendreturn selectConDocTypeSampleLendreturnById(Long sid);

    /**
     * 查询单据类型_样品借还单列表
     * 
     * @param conDocTypeSampleLendreturn 单据类型_样品借还单
     * @return 单据类型_样品借还单集合
     */
    public List<ConDocTypeSampleLendreturn> selectConDocTypeSampleLendreturnList(ConDocTypeSampleLendreturn conDocTypeSampleLendreturn);

    /**
     * 新增单据类型_样品借还单
     * 
     * @param conDocTypeSampleLendreturn 单据类型_样品借还单
     * @return 结果
     */
    public int insertConDocTypeSampleLendreturn(ConDocTypeSampleLendreturn conDocTypeSampleLendreturn);

    /**
     * 修改单据类型_样品借还单
     * 
     * @param conDocTypeSampleLendreturn 单据类型_样品借还单
     * @return 结果
     */
    public int updateConDocTypeSampleLendreturn(ConDocTypeSampleLendreturn conDocTypeSampleLendreturn);

    /**
     * 变更单据类型_样品借还单
     *
     * @param conDocTypeSampleLendreturn 单据类型_样品借还单
     * @return 结果
     */
    public int changeConDocTypeSampleLendreturn(ConDocTypeSampleLendreturn conDocTypeSampleLendreturn);

    /**
     * 批量删除单据类型_样品借还单
     * 
     * @param sids 需要删除的单据类型_样品借还单ID
     * @return 结果
     */
    public int deleteConDocTypeSampleLendreturnByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conDocTypeSampleLendreturn
    * @return
    */
    int changeStatus(ConDocTypeSampleLendreturn conDocTypeSampleLendreturn);

    /**
     * 更改确认状态
     * @param conDocTypeSampleLendreturn
     * @return
     */
    int check(ConDocTypeSampleLendreturn conDocTypeSampleLendreturn);

}
