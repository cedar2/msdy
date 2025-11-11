package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.*;

/**
 * 事项清单Service接口
 *
 * @author platform
 * @date 2023-11-20
 */
public interface IPrjMatterListService extends IService<PrjMatterList> {

    /**
     * 查询事项清单
     *
     * @param matterListSid 事项清单ID
     * @return 事项清单
     */
    public PrjMatterList selectPrjMatterListById(Long matterListSid);

    /**
     * 查询事项清单列表
     *
     * @param prjMatterList 事项清单
     * @return 事项清单集合
     */
    public List<PrjMatterList> selectPrjMatterListList(PrjMatterList prjMatterList);

    /**
     * 新增事项清单
     *
     * @param prjMatterList 事项清单
     * @return 结果
     */
    public int insertPrjMatterList(PrjMatterList prjMatterList);

    /**
     * 修改事项清单
     *
     * @param prjMatterList 事项清单
     * @return 结果
     */
    public int updatePrjMatterList(PrjMatterList prjMatterList);

    /**
     * 变更事项清单
     *
     * @param prjMatterList 事项清单
     * @return 结果
     */
    public int changePrjMatterList(PrjMatterList prjMatterList);

    /**
     * 批量删除事项清单
     *
     * @param matterListSids 需要删除的事项清单ID
     * @return 结果
     */
    public int deletePrjMatterListByIds(List<Long> matterListSids);

    /**
     * 设置事项状态
     *
     * @param prjMatterList 入参
     * @return
     */
    public int setMatterStatus(PrjMatterList prjMatterList);

    /**
     * 分配事项处理人
     *
     * @param prjMatterList 入参
     * @return 出参
     */
    int setMatterHandler(PrjMatterList prjMatterList);

    /**
     * 设置日期
     *
     * @param prjMatterList 入参
     * @return
     */
    int setPlanDate(PrjMatterList prjMatterList);

    /**
     * 设置即将到期提醒天数
     *
     * @param prjMatterList 入参
     * @return
     */
    int setToexpireDays(PrjMatterList prjMatterList);

    /**
     * 设置待办提醒天数
     *
     * @param prjMatterList 入参
     * @return
     */
    int setTodoDays(PrjMatterList prjMatterList);

    /**
     * 设置优先级
     *
     * @param prjMatterList 入参
     * @return
     */
    int setPriority(PrjMatterList prjMatterList);

    /**
     * 事项进度跟踪报表
     *
     * @param query
     * @return
     */
    DataTotal<MatterTraceTableVo> matterTraceTable(PrjProjectQuery query);

    /**
     * 事项进度指标统计
     *
     * @param query
     * @return
     */
    TargetVo matterTraceTarget(PrjProjectQuery query);
}
