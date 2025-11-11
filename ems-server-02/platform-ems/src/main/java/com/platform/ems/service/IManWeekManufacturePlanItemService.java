package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManWeekManufacturePlanItem;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 生产周计划-明细Service接口
 *
 * @author hjj
 * @date 2021-07-16
 */
public interface IManWeekManufacturePlanItemService extends IService<ManWeekManufacturePlanItem> {
    /**
     * 查询生产周计划-明细
     *
     * @param weekManufacturePlanItemSid 生产周计划-明细ID
     * @return 生产周计划-明细
     */
    public ManWeekManufacturePlanItem selectManWeekManufacturePlanItemById(Long weekManufacturePlanItemSid);

    /**
     * 查询生产周计划-明细列表
     *
     * @param manWeekManufacturePlanItem 生产周计划-明细
     * @return 生产周计划-明细集合
     */
    public List<ManWeekManufacturePlanItem> selectManWeekManufacturePlanItemList(ManWeekManufacturePlanItem manWeekManufacturePlanItem);

    /**
     * 新增生产周计划-明细
     *
     * @param manWeekManufacturePlanItem 生产周计划-明细
     * @return 结果
     */
    public int insertManWeekManufacturePlanItem(ManWeekManufacturePlanItem manWeekManufacturePlanItem);

    /**
     * 修改生产周计划-明细
     *
     * @param manWeekManufacturePlanItem 生产周计划-明细
     * @return 结果
     */
    public int updateManWeekManufacturePlanItem(ManWeekManufacturePlanItem manWeekManufacturePlanItem);

    /**
     * 变更生产周计划-明细
     *
     * @param manWeekManufacturePlanItem 生产周计划-明细
     * @return 结果
     */
    public int changeManWeekManufacturePlanItem(ManWeekManufacturePlanItem manWeekManufacturePlanItem);

    /**
     * 批量删除生产周计划-明细
     *
     * @param weekManufacturePlanItemSids 需要删除的生产周计划-明细ID
     * @return 结果
     */
    public int deleteManWeekManufacturePlanItemByIds(List<Long> weekManufacturePlanItemSids);
    /**
     * 导出生产周计划-明细
     *
     */
    public void exportReport(HttpServletResponse response, List<ManWeekManufacturePlanItem> list , Date dateStart);
    /**
     * 生产周计划明细报表
     */
    List<ManWeekManufacturePlanItem> getItemList(ManWeekManufacturePlanItem manWeekManufacturePlanItem);
}
