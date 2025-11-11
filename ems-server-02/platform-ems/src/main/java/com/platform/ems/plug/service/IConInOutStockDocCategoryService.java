package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConInOutStockDocCategory;
/**
 * 出入库对应的单据类别Service接口
 *
 * @author linhongwei
 * @date 2021-06-15
 */
public interface IConInOutStockDocCategoryService extends IService<ConInOutStockDocCategory> {
    List<ConInOutStockDocCategory>  getList();
    List<ConInOutStockDocCategory>  getListCategory(String movementTypeCode);
    /**
     * 查询出入库对应的单据类别
     *
     * @param sid 出入库对应的单据类别ID
     * @return 出入库对应的单据类别
     */
    public ConInOutStockDocCategory selectConInOutStockDocCategoryById(Long sid);

    /**
     * 查询出入库对应的单据类别列表
     *
     * @param conInOutStockDocCategory 出入库对应的单据类别
     * @return 出入库对应的单据类别集合
     */
    public List<ConInOutStockDocCategory> selectConInOutStockDocCategoryList(ConInOutStockDocCategory conInOutStockDocCategory);

    /**
     * 新增出入库对应的单据类别
     *
     * @param conInOutStockDocCategory 出入库对应的单据类别
     * @return 结果
     */
    public int insertConInOutStockDocCategory(ConInOutStockDocCategory conInOutStockDocCategory);

    /**
     * 修改出入库对应的单据类别
     *
     * @param conInOutStockDocCategory 出入库对应的单据类别
     * @return 结果
     */
    public int updateConInOutStockDocCategory(ConInOutStockDocCategory conInOutStockDocCategory);

    /**
     * 变更出入库对应的单据类别
     *
     * @param conInOutStockDocCategory 出入库对应的单据类别
     * @return 结果
     */
    public int changeConInOutStockDocCategory(ConInOutStockDocCategory conInOutStockDocCategory);

    /**
     * 批量删除出入库对应的单据类别
     *
     * @param sids 需要删除的出入库对应的单据类别ID
     * @return 结果
     */
    public int deleteConInOutStockDocCategoryByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conInOutStockDocCategory
    * @return
    */
    int changeStatus(ConInOutStockDocCategory conInOutStockDocCategory);

    /**
     * 更改确认状态
     * @param conInOutStockDocCategory
     * @return
     */
    int check(ConInOutStockDocCategory conInOutStockDocCategory);

}
