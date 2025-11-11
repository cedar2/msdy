package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasSkuGroup;
import com.platform.ems.domain.BasSkuGroupItem;

/**
 * SKU组档案Service接口
 *
 * @author linhongwei
 * @date 2021-03-22
 */
public interface IBasSkuGroupService extends IService<BasSkuGroup>{
    /**
     * 查询SKU组档案
     *
     * @param skuGroupSid SKU组档案ID
     * @return SKU组档案
     */
    public BasSkuGroup selectBasSkuGroupById(Long skuGroupSid);

    /**
     * 查询SKU组档案列表
     *
     * @param basSkuGroup SKU组档案
     * @return SKU组档案集合
     */
    public List<BasSkuGroup> selectBasSkuGroupList(BasSkuGroup basSkuGroup);

    /**
     * 新增SKU组档案
     *
     * @param basSkuGroup SKU组档案
     * @return 结果
     */
    public int insertBasSkuGroup(BasSkuGroup basSkuGroup);

    /**
     * 修改SKU组档案
     *
     * @param basSkuGroup SKU组档案
     * @return 结果
     */
    public int updateBasSkuGroup(BasSkuGroup basSkuGroup);

    /**
     * 批量删除SKU组档案
     *
     * @param skuGroupSids 需要删除的SKU组档案ID
     * @return 结果
     */
    public int deleteBasSkuGroupByIds(List<Long> skuGroupSids);

    /**
     * 批量删除SKU组档案详情里的明细前的校验
     *
     * @param skuGroupItemSidList 需要删除的SKU组档案明细的SID
     * @return 结果
     */
    public void deleteBasSkuGroupItemByIdsCheck(List<Long> skuGroupItemSidList);

    /**
     * 查询SKU组明细报表
     *
     * @param basSkuGroupItem SKU组明细档案
     * @return SKU组档案集合
     */
    List<BasSkuGroupItem> getReportForm(BasSkuGroupItem basSkuGroupItem);

    List<BasSkuGroup> getList(BasSkuGroup basSkuGroup);

    List<BasSkuGroupItem> getDetail(Long skuGroupSid);

    int changeStatus(BasSkuGroup basSkuGroup);

    int check(BasSkuGroup basSkuGroup);


}
