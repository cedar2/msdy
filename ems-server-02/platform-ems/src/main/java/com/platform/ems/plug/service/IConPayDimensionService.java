package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConPayDimension;

/**
 * 付款维度Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConPayDimensionService extends IService<ConPayDimension>{
    /**
     * 查询付款维度
     * 
     * @param sid 付款维度ID
     * @return 付款维度
     */
    public ConPayDimension selectConPayDimensionById(Long sid);

    /**
     * 查询付款维度列表
     * 
     * @param conPayDimension 付款维度
     * @return 付款维度集合
     */
    public List<ConPayDimension> selectConPayDimensionList(ConPayDimension conPayDimension);

    /**
     * 新增付款维度
     * 
     * @param conPayDimension 付款维度
     * @return 结果
     */
    public int insertConPayDimension(ConPayDimension conPayDimension);

    /**
     * 修改付款维度
     * 
     * @param conPayDimension 付款维度
     * @return 结果
     */
    public int updateConPayDimension(ConPayDimension conPayDimension);

    /**
     * 变更付款维度
     *
     * @param conPayDimension 付款维度
     * @return 结果
     */
    public int changeConPayDimension(ConPayDimension conPayDimension);

    /**
     * 批量删除付款维度
     * 
     * @param sids 需要删除的付款维度ID
     * @return 结果
     */
    public int deleteConPayDimensionByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conPayDimension
    * @return
    */
    int changeStatus(ConPayDimension conPayDimension);

    /**
     * 更改确认状态
     * @param conPayDimension
     * @return
     */
    int check(ConPayDimension conPayDimension);

}
