package com.platform.ems.mapper;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.math.BigDecimal;
import java.util.List;

import com.platform.ems.domain.ManWeekManufacturePlanItem;
import com.platform.ems.domain.dto.ManDayProgressMonthFormDay;
import com.platform.ems.domain.dto.ManDayProgressMonthForm;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManDayManufactureProgressItem;

/**
 * 生产进度日报-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-09
 */
public interface ManDayManufactureProgressItemMapper extends BaseMapper<ManDayManufactureProgressItem> {

    ManDayManufactureProgressItem selectManDayManufactureProgressItemById(Long dayManufactureProgressItemSid);

    BigDecimal getTotalCompetele(ManWeekManufacturePlanItem manWeekManufacturePlanItem);

    /**
     * 班组生产日报明细 添加行需要获取的分配量
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 生产进度日报-明细
     */
    @SqlParser(filter=true)
    List<ManDayManufactureProgressItem> getQuantityFenpei(ManDayManufactureProgressItem manDayManufactureProgressItem);

    /**
     * 查询生产进度日报-明细列表
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 生产进度日报-明细
     */
    @SqlParser(filter=true)
    List<ManDayManufactureProgressItem> selectManDayManufactureProgressItemList(ManDayManufactureProgressItem manDayManufactureProgressItem);

    /**
     * 查询生产进度日报-明细报表
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 生产进度日报-明细
     */
    @SqlParser(filter=true)
    List<ManDayManufactureProgressItem> selectManDayManufactureProgressItemForm(ManDayManufactureProgressItem manDayManufactureProgressItem);

    /**
     * 查询生产进度日报-明细报表获取总数
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 生产进度日报-明细
     */
    int selectCount(ManDayManufactureProgressItem manDayManufactureProgressItem);

    /**
     * 添加多个
     * @param list List ManDayManufactureProgressItem
     * @return int
     */
    int inserts(@Param("list") List<ManDayManufactureProgressItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity ManDayManufactureProgressItem
     * @return int
     */
    int updateAllById(ManDayManufactureProgressItem entity);

    /**
     * 更新多个
     * @param list List ManDayManufactureProgressItem
     * @return int
     */
    int updatesAllById(@Param("list") List<ManDayManufactureProgressItem> list);

    /**
     *  1》根据“所属年月+生产订单号+商品编码+所属生产工序”从“生产进度日报“中获取符合条件的生产进度明细数据（进度日报需为“已确认”状态），
     *      将明细行的”当天实际完成量“（quantity）累加得出
     */
    String getQuantity(ManDayManufactureProgressItem item);

    /**
     * 报表中心生产管理生产月进度
     * 根据查询条件，从“班组生产日报明细”数据库表中获取“是否标志阶段完成的工序”为“是”的班组生产日报明细数据及对应的生产订单数据
     *
     * @param manDayProgressMonthFormRequest 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    @SqlParser(filter=true)
    List<ManDayProgressMonthForm> selectManDayManufactureProgressMonthForm(ManDayProgressMonthForm manDayProgressMonthFormRequest);

    /**
     * 报表中心生产管理生产月进度
     * 根据查询条件，从“班组生产日报明细”数据库表中获取“是否标志阶段完成的工序”为“是”的班组生产日报明细数据及对应的生产订单数据
     *
     * @param manDayProgressMonthFormRequest 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    @SqlParser(filter=true)
    int countManDayManufactureProgressMonthForm(ManDayProgressMonthForm manDayProgressMonthFormRequest);

    /**
     * 报表中心生产管理生产月进度
     * 获取每日完成量
     *
     * @param manDayProgressMonthDayForm 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    List<ManDayProgressMonthFormDay> selectManDayManufactureProgressMonthDayList(ManDayProgressMonthFormDay manDayProgressMonthDayForm);

    /**
     *  获取 ：
     *    1》已完成量(工序)
     *      根据“工厂(工序)+班组+生产订单+生产订单工序明细sid”从“班组生产日报明细表“中（s_man_day_manufacture_progress_item）
     *      获取所有符合条件的明细行【仅获取”已确认“状态的”班组生产日报“】，然后将所有明细行的“当天实际完成量/收料量”（quantity）累加得出
     *    2》实裁量  根据“生产订单号”从“生产订单工序明细表”中获取“是否第一个工序”为“是”的工序的已完成量，已完成量计算逻辑参照第 1》点
     *    3》未完成量(计划)  = 计划产量(工序) - 已完成量(工序)
     *    4》未完成量(实裁)  = 实裁量 - 已完成量(工序)
     *
     * @param item 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    ManDayManufactureProgressItem getCompleteQuantity(ManDayManufactureProgressItem item);
}
