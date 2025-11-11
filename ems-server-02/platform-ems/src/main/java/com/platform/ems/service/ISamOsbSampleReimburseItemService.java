package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SamOsbSampleReimburseItem;

import java.util.List;

/**
 * 外采样报销单-明细Service接口
 *
 * @author qhq
 * @date 2021-12-28
 */
public interface ISamOsbSampleReimburseItemService extends IService<SamOsbSampleReimburseItem>{
    /**
     * 查询外采样报销单-明细
     *
     * @param reimburseItemSid 外采样报销单-明细ID
     * @return 外采样报销单-明细
     */
    public SamOsbSampleReimburseItem selectSamOsbSampleReimburseItemById (Long reimburseItemSid);

    /**
     * 查询外采样报销单-明细列表
     *
     * @param samOsbSampleReimburseItem 外采样报销单-明细
     * @return 外采样报销单-明细集合
     */
    public List<SamOsbSampleReimburseItem> selectSamOsbSampleReimburseItemList (SamOsbSampleReimburseItem samOsbSampleReimburseItem);

    /**
     * 新增外采样报销单-明细
     *
     * @param samOsbSampleReimburseItem 外采样报销单-明细
     * @return 结果
     */
    public int insertSamOsbSampleReimburseItem (SamOsbSampleReimburseItem samOsbSampleReimburseItem);

    /**
     * 修改外采样报销单-明细
     *
     * @param samOsbSampleReimburseItem 外采样报销单-明细
     * @return 结果
     */
    public int updateSamOsbSampleReimburseItem (SamOsbSampleReimburseItem samOsbSampleReimburseItem);

    /**
     * 变更外采样报销单-明细
     *
     * @param samOsbSampleReimburseItem 外采样报销单-明细
     * @return 结果
     */
    public int changeSamOsbSampleReimburseItem (SamOsbSampleReimburseItem samOsbSampleReimburseItem);

    /**
     * 批量删除外采样报销单-明细
     *
     * @param reimburseItemSids 需要删除的外采样报销单-明细ID
     * @return 结果
     */
    public int deleteSamOsbSampleReimburseItemByIds (List<Long> reimburseItemSids);

}
