package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasGoodsShelf;
import com.platform.ems.domain.InvInventoryDocumentItem;

import java.util.List;

/**
 * 货架档案Service接口
 *
 * @author straw
 * @date 2023-02-02
 */
public interface IBasGoodsShelfService extends IService<BasGoodsShelf> {
    /**
     * 查询货架档案
     *
     * @param goodsShelfSid 货架档案ID
     * @return 货架档案
     */
    public BasGoodsShelf selectBasGoodsShelfById(Long goodsShelfSid);

    /**
     * 查询货架档案列表
     *
     * @param basGoodsShelf 货架档案
     * @return 货架档案集合
     */
    public List<BasGoodsShelf> selectBasGoodsShelfList(BasGoodsShelf basGoodsShelf);

    /**
     * 新增货架档案
     *
     * @param basGoodsShelf 货架档案
     * @return 结果
     */
    public int insertBasGoodsShelf(BasGoodsShelf basGoodsShelf);

    /**
     * 修改货架档案
     *
     * @param basGoodsShelf 货架档案
     * @return 结果
     */
    public int updateBasGoodsShelf(BasGoodsShelf basGoodsShelf);

    /**
     * 变更货架档案
     *
     * @param basGoodsShelf 货架档案
     * @return 结果
     */
    public int changeBasGoodsShelf(BasGoodsShelf basGoodsShelf);

    /**
     * 批量删除货架档案
     *
     * @param goodsShelfSids 需要删除的货架档案ID
     * @return 结果
     */
    public int deleteBasGoodsShelfByIds(List<Long> goodsShelfSids);

    /**
     * 启用/停用
     *
     * @param basGoodsShelf
     * @return
     */
    int changeStatus(BasGoodsShelf basGoodsShelf);

    /**
     * 更改确认状态
     *
     * @param basGoodsShelf
     * @return
     */
    int check(BasGoodsShelf basGoodsShelf);

    /**
     * 根据物料分类和仓库和库位获取货架编号多值用分号隔开
     * @param request
     * @return
     */
    List<InvInventoryDocumentItem> getCodes(List<InvInventoryDocumentItem> request);
}
