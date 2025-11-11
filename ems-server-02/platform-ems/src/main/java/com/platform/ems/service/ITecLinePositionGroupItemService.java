package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecLinePositionGroupItem;

/**
 * 线部位组明细Service接口
 * 
 * @author linhongwei
 * @date 2021-08-19
 */
public interface ITecLinePositionGroupItemService extends IService<TecLinePositionGroupItem>{
    /**
     * 查询线部位组明细
     * 
     * @param groupItemSid 线部位组明细ID
     * @return 线部位组明细
     */
    public TecLinePositionGroupItem selectTecLinePositionGroupItemById(Long groupItemSid);

    /**
     * 查询线部位组明细列表
     * 
     * @param tecLinePositionGroupItem 线部位组明细
     * @return 线部位组明细集合
     */
    public List<TecLinePositionGroupItem> selectTecLinePositionGroupItemList(TecLinePositionGroupItem tecLinePositionGroupItem);

    /**
     * 新增线部位组明细
     * 
     * @param tecLinePositionGroupItem 线部位组明细
     * @return 结果
     */
    public int insertTecLinePositionGroupItem(TecLinePositionGroupItem tecLinePositionGroupItem);

    /**
     * 修改线部位组明细
     * 
     * @param tecLinePositionGroupItem 线部位组明细
     * @return 结果
     */
    public int updateTecLinePositionGroupItem(TecLinePositionGroupItem tecLinePositionGroupItem);

    /**
     * 变更线部位组明细
     *
     * @param tecLinePositionGroupItem 线部位组明细
     * @return 结果
     */
    public int changeTecLinePositionGroupItem(TecLinePositionGroupItem tecLinePositionGroupItem);

    /**
     * 批量删除线部位组明细
     * 
     * @param groupItemSids 需要删除的线部位组明细ID
     * @return 结果
     */
    public int deleteTecLinePositionGroupItemByIds(List<Long> groupItemSids);

}
