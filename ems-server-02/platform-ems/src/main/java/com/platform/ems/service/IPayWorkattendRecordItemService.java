package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PayWorkattendRecordItem;

import java.util.List;

/**
 * 考勤信息-明细Service接口
 *
 * @author linhongwei
 * @date 2021-09-14
 */
public interface IPayWorkattendRecordItemService extends IService<PayWorkattendRecordItem> {
    /**
     * 查询考勤信息-明细
     *
     * @param recordItemSid 考勤信息-明细ID
     * @return 考勤信息-明细
     */
    public PayWorkattendRecordItem selectPayWorkattendRecordItemById(Long recordItemSid);

    /**
     * 查询考勤信息-明细列表
     *
     * @param payWorkattendRecordItem 考勤信息-明细
     * @return 考勤信息-明细集合
     */
    public List<PayWorkattendRecordItem> selectPayWorkattendRecordItemList(PayWorkattendRecordItem payWorkattendRecordItem);

    /**
     * 新增考勤信息-明细
     *
     * @param payWorkattendRecordItem 考勤信息-明细
     * @return 结果
     */
    public int insertPayWorkattendRecordItem(PayWorkattendRecordItem payWorkattendRecordItem);

    /**
     * 修改考勤信息-明细
     *
     * @param payWorkattendRecordItem 考勤信息-明细
     * @return 结果
     */
    public int updatePayWorkattendRecordItem(PayWorkattendRecordItem payWorkattendRecordItem);

    /**
     * 变更考勤信息-明细
     *
     * @param payWorkattendRecordItem 考勤信息-明细
     * @return 结果
     */
    public int changePayWorkattendRecordItem(PayWorkattendRecordItem payWorkattendRecordItem);

    /**
     * 批量删除考勤信息-明细
     *
     * @param recordItemSids 需要删除的考勤信息-明细ID
     * @return 结果
     */
    public int deletePayWorkattendRecordItemByIds(List<Long> recordItemSids);

}
