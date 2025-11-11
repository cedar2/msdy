package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaRawmatCheckItem;

import java.util.List;

/**
 * 面辅料检测单-检测项目Service接口
 *
 * @author linhongwei
 * @date 2022-04-11
 */
public interface IQuaRawmatCheckItemService extends IService<QuaRawmatCheckItem> {
    /**
     * 查询面辅料检测单-检测项目
     *
     * @param rawmatCheckItemSid 面辅料检测单-检测项目ID
     * @return 面辅料检测单-检测项目
     */
    public QuaRawmatCheckItem selectQuaRawmatCheckItemById(Long rawmatCheckItemSid);

    /**
     * 查询面辅料检测单-检测项目列表
     *
     * @param quaRawmatCheckItem 面辅料检测单-检测项目
     * @return 面辅料检测单-检测项目集合
     */
    public List<QuaRawmatCheckItem> selectQuaRawmatCheckItemList(QuaRawmatCheckItem quaRawmatCheckItem);

    /**
     * 新增面辅料检测单-检测项目
     *
     * @param quaRawmatCheckItem 面辅料检测单-检测项目
     * @return 结果
     */
    public int insertQuaRawmatCheckItem(QuaRawmatCheckItem quaRawmatCheckItem);

    /**
     * 修改面辅料检测单-检测项目
     *
     * @param quaRawmatCheckItem 面辅料检测单-检测项目
     * @return 结果
     */
    public int updateQuaRawmatCheckItem(QuaRawmatCheckItem quaRawmatCheckItem);

    /**
     * 变更面辅料检测单-检测项目
     *
     * @param quaRawmatCheckItem 面辅料检测单-检测项目
     * @return 结果
     */
    public int changeQuaRawmatCheckItem(QuaRawmatCheckItem quaRawmatCheckItem);

    /**
     * 批量删除面辅料检测单-检测项目
     *
     * @param rawmatCheckItemSids 需要删除的面辅料检测单-检测项目ID
     * @return 结果
     */
    public int deleteQuaRawmatCheckItemByIds(List<Long> rawmatCheckItemSids);

}
