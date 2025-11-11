package com.platform.ems.service;

import java.io.IOException;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManDayManufactureProgress;
import com.platform.ems.domain.ManDayManufactureProgressDetail;
import com.platform.ems.domain.ManDayManufactureProgressItem;
import com.platform.ems.domain.PayProcessStepCompleteItem;
import com.platform.ems.domain.dto.ManDayProgressDetailTable;
import com.platform.ems.domain.dto.ManDayProgressMonthForm;
import com.platform.ems.domain.dto.ManDayProgressMonthFormData;

import javax.servlet.http.HttpServletResponse;

/**
 * 生产进度日报-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-06-09
 */
public interface IManDayManufactureProgressItemService extends IService<ManDayManufactureProgressItem>{
    /**
     * 查询生产进度日报-明细
     * 
     * @param dayManufactureProgressItemSid 生产进度日报-明细ID
     * @return 生产进度日报-明细
     */
    ManDayManufactureProgressItem selectManDayManufactureProgressItemById(Long dayManufactureProgressItemSid);

    /**
     * 查询生产进度日报-明细列表
     * 
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    List<ManDayManufactureProgressItem> selectManDayManufactureProgressItemList(ManDayManufactureProgressItem manDayManufactureProgressItem);

    /**
     * 查询生产进度日报-明细报表
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    List<ManDayManufactureProgressItem> selectManDayManufactureProgressItemForm(ManDayManufactureProgressItem manDayManufactureProgressItem);

    /**
     * 查询生产进度日报-明细报表 的总数 用来分页
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    int selectCount(ManDayManufactureProgressItem manDayManufactureProgressItem);

    /**
     * 新增生产进度日报-明细
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 结果
     */
    int insertManDayManufactureProgressItem(ManDayManufactureProgressItem manDayManufactureProgressItem);

    /**
     * 批量新增生产进度日报-明细
     *
     * @param manDayManufactureProgressItemList 生产进度日报-明细
     * @return 结果
     */
    int insertManDayManufactureProgressItem(List<ManDayManufactureProgressItem> manDayManufactureProgressItemList);

    /**
     * 修改生产进度日报-明细
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 结果
     */
    int updateManDayManufactureProgressItem(ManDayManufactureProgressItem manDayManufactureProgressItem);

    /**
     * 变更生产进度日报-明细
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 结果
     */
    int changeManDayManufactureProgressItem(ManDayManufactureProgressItem manDayManufactureProgressItem);

    /**
     * 批量删除生产进度日报-明细
     *
     * @param dayManufactureProgressItemSids 需要删除的生产进度日报-明细ID
     * @return 结果
     */
    int deleteManDayManufactureProgressItemByIds(List<Long> dayManufactureProgressItemSids);

    /**
     *  1》根据“所属年月+生产订单号+商品编码+所属生产工序”从“生产进度日报“中获取符合条件的生产进度明细数据（进度日报需为“已确认”状态），
     *      将明细行的”当天实际完成量“（quantity）累加得出
     */
    PayProcessStepCompleteItem getQuantity(ManDayManufactureProgressItem item);

    /**
     *  班组生产日报 “工序进度”页签，新增4个清单列：实裁量、已完成量(工序)、未完成量(计划)、未完成量(实裁)，不可编辑，放置于“完成量(首批)”清单列后
     *    1》已完成量(工序)
     *      根据“工厂(工序)+班组+生产订单+生产订单工序明细sid”从“班组生产日报明细表“中（s_man_day_manufacture_progress_item）
     *      获取所有符合条件的明细行【仅获取”已确认“状态的”班组生产日报“】，然后将所有明细行的“当天实际完成量/收料量”（quantity）累加得出
     *    2》实裁量  根据“生产订单号”从“生产订单工序明细表”中获取“是否第一个工序”为“是”的工序的已完成量，已完成量计算逻辑参照第 1》点
     *    3》未完成量(计划)  = 计划产量(工序) - 已完成量(工序)
     *    4》未完成量(实裁)  = 实裁量 - 已完成量(工序)
     */
    List<ManDayManufactureProgressItem> getCompleteQuantity(ManDayManufactureProgress manDayManufactureProgress);

    /**
     * 报表中心生产管理生产月进度
     * 根据查询条件，从“班组生产日报明细”数据库表中获取“是否标志阶段完成的工序”为“是”的班组生产日报明细数据及对应的生产订单数据
     *
     * @param manDayProgressMonthFormRequest 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    ManDayProgressMonthFormData selectManDayManufactureProgressMonthForm(ManDayProgressMonthForm manDayProgressMonthFormRequest);

    /**
     * 导出报表中心生产管理生产月进度
     * 根据查询条件，从“班组生产日报明细”数据库表中获取“是否标志阶段完成的工序”为“是”的班组生产日报明细数据及对应的生产订单数据
     *
     * @param data 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    void exportFormMonth(HttpServletResponse response, ManDayProgressMonthFormData data) throws IOException;

    /**
     * 报表中心生产管理生产日进度
     * 根据查询条件，从“班组生产日报明细”数据库表中获取“是否标志阶段完成的工序”为“是”的班组生产日报明细数据及对应的生产订单数据
     *
     * @param manDayProgressMonthFormRequest 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    ManDayProgressMonthFormData selectManDayManufactureProgressDayForm(ManDayProgressMonthForm manDayProgressMonthFormRequest);

    /**
     * 导出报表中心生产管理生产日进度
     * 根据查询条件，从“班组生产日报明细”数据库表中获取“是否标志阶段完成的工序”为“是”的班组生产日报明细数据及对应的生产订单数据
     *
     * @param manDayProgressMonthFormRequest 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    void exportFormDay(HttpServletResponse response, ManDayProgressMonthForm manDayProgressMonthFormRequest) throws IOException;

    /**
     * 勾选明细进入尺码完工明细 默认带出 生产订单 根据外层所选择明细行的“生产订单号+商品编码+颜色”，从生产订单产品明细表中，自动带出商品的颜色&尺码明细
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 生产进度日报-明细
     */
    List<ManDayManufactureProgressDetail> getManDayManufactureProgressDetail(ManDayManufactureProgressItem manDayManufactureProgressItem);

    /**
     * 生产日进度报表查看详情的 行转列
     *
     * @param manDayProgressMonthForm 生产日进度报表 行数据
     * @return 生产进度日报-明细
     */
    ManDayProgressDetailTable getManDayManufactureProgressDetailTable(ManDayProgressMonthForm manDayProgressMonthForm);
}
