package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConExceptionReasonSampleLendreturn;

/**
 * 异常明细配置Service接口
 * 
 * @author yangqz
 * @date 2022-04-25
 */
public interface IConExceptionReasonSampleLendreturnService extends IService<ConExceptionReasonSampleLendreturn>{
    /**
     * 查询异常明细配置
     * 
     * @param sid 异常明细配置ID
     * @return 异常明细配置
     */
    public ConExceptionReasonSampleLendreturn selectConExceptionReasonSampleLendreturnById(Long sid);

    /**
     * 查询异常明细配置列表
     * 
     * @param conExceptionReasonSampleLendreturn 异常明细配置
     * @return 异常明细配置集合
     */
    public List<ConExceptionReasonSampleLendreturn> selectConExceptionReasonSampleLendreturnList(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn);

    /**
     * 新增异常明细配置
     * 
     * @param conExceptionReasonSampleLendreturn 异常明细配置
     * @return 结果
     */
    public int insertConExceptionReasonSampleLendreturn(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn);
    /**
     * 查询异常明细配置下拉列表
     *
     */
    public List<ConExceptionReasonSampleLendreturn> getList(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn);
    /**
     * 修改异常明细配置
     * 
     * @param conExceptionReasonSampleLendreturn 异常明细配置
     * @return 结果
     */
    public int updateConExceptionReasonSampleLendreturn(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn);

    /**
     * 变更异常明细配置
     *
     * @param conExceptionReasonSampleLendreturn 异常明细配置
     * @return 结果
     */
    public int changeConExceptionReasonSampleLendreturn(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn);

    /**
     * 批量删除异常明细配置
     * 
     * @param sids 需要删除的异常明细配置ID
     * @return 结果
     */
    public int deleteConExceptionReasonSampleLendreturnByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conExceptionReasonSampleLendreturn
    * @return
    */
    int changeStatus(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn);

    /**
     * 更改确认状态
     * @param conExceptionReasonSampleLendreturn
     * @return
     */
    int check(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn);

}
