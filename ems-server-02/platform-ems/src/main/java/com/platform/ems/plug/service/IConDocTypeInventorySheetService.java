package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeInventorySheet;

/**
 * 单据类型(盘点单)Service接口
 *
 * @author chenkw
 * @date 2021-08-11
 */
public interface IConDocTypeInventorySheetService extends IService<ConDocTypeInventorySheet>{
    /**
     * 查询单据类型(盘点单)
     *
     * @param sid 单据类型(盘点单)ID
     * @return 单据类型(盘点单)
     */
    public ConDocTypeInventorySheet selectConDocTypeInventorySheetById(Long sid);

    /**
     * 查询单据类型(盘点单)列表
     *
     * @param conDocTypeInventorySheet 单据类型(盘点单)
     * @return 单据类型(盘点单)集合
     */
    public List<ConDocTypeInventorySheet> selectConDocTypeInventorySheetList(ConDocTypeInventorySheet conDocTypeInventorySheet);

    /**
     * 新增单据类型(盘点单)
     *
     * @param conDocTypeInventorySheet 单据类型(盘点单)
     * @return 结果
     */
    public int insertConDocTypeInventorySheet(ConDocTypeInventorySheet conDocTypeInventorySheet);

    /**
     * 修改单据类型(盘点单)
     *
     * @param conDocTypeInventorySheet 单据类型(盘点单)
     * @return 结果
     */
    public int updateConDocTypeInventorySheet(ConDocTypeInventorySheet conDocTypeInventorySheet);

    /**
     * 变更单据类型(盘点单)
     *
     * @param conDocTypeInventorySheet 单据类型(盘点单)
     * @return 结果
     */
    public int changeConDocTypeInventorySheet(ConDocTypeInventorySheet conDocTypeInventorySheet);

    /**
     * 批量删除单据类型(盘点单)
     *
     * @param sids 需要删除的单据类型(盘点单)ID
     * @return 结果
     */
    public int deleteConDocTypeInventorySheetByIds(List<Long>  sids);

    /**
     * 启用/停用
     * @param conDocTypeInventorySheet
     * @return
     */
    int changeStatus(ConDocTypeInventorySheet conDocTypeInventorySheet);

    /**
     * 更改确认状态
     * @param conDocTypeInventorySheet
     * @return
     */
    int check(ConDocTypeInventorySheet conDocTypeInventorySheet);

}
