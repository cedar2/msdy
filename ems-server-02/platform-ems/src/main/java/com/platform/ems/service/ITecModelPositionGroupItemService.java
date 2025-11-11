package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecModelPositionGroupItem;

/**
 * 版型部位组明细Service接口
 * 
 * @author linhongwei
 * @date 2021-06-02
 */
public interface ITecModelPositionGroupItemService extends IService<TecModelPositionGroupItem>{
    /**
     * 查询版型部位组明细
     * 
     * @param groupItemSid 版型部位组明细ID
     * @return 版型部位组明细
     */
    public TecModelPositionGroupItem selectTecModelPositionGroupItemById(Long groupItemSid);

    /**
     * 查询版型部位组明细列表
     * 
     * @param tecModelPositionGroupItem 版型部位组明细
     * @return 版型部位组明细集合
     */
    public List<TecModelPositionGroupItem> selectTecModelPositionGroupItemList(TecModelPositionGroupItem tecModelPositionGroupItem);

    /**
     * 新增版型部位组明细
     * 
     * @param tecModelPositionGroupItem 版型部位组明细
     * @return 结果
     */
    public int insertTecModelPositionGroupItem(TecModelPositionGroupItem tecModelPositionGroupItem);

    /**
     * 修改版型部位组明细
     * 
     * @param tecModelPositionGroupItem 版型部位组明细
     * @return 结果
     */
    public int updateTecModelPositionGroupItem(TecModelPositionGroupItem tecModelPositionGroupItem);

    /**
     * 变更版型部位组明细
     *
     * @param tecModelPositionGroupItem 版型部位组明细
     * @return 结果
     */
    public int changeTecModelPositionGroupItem(TecModelPositionGroupItem tecModelPositionGroupItem);

    /**
     * 批量删除版型部位组明细
     * 
     * @param groupItemSids 需要删除的版型部位组明细ID
     * @return 结果
     */
    public int deleteTecModelPositionGroupItemByIds(List<Long> groupItemSids);

    /**
    * 启用/停用
    * @param tecModelPositionGroupItem
    * @return
    */
    int changeStatus(TecModelPositionGroupItem tecModelPositionGroupItem);

    /**
     * 更改确认状态
     * @param tecModelPositionGroupItem
     * @return
     */
    int check(TecModelPositionGroupItem tecModelPositionGroupItem);

    /**
     * 查询版型部位组明细报表
     *
     * @param tecModelPositionGroupItem 版型部位组明细
     * @return 版型部位组明细集合
     */
    List<TecModelPositionGroupItem> getReportForm(TecModelPositionGroupItem tecModelPositionGroupItem);

}
