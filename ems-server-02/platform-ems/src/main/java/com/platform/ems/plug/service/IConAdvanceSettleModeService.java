package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConAdvanceSettleMode;

/**
 * 预收款/预付款付款方式Service接口
 *
 * @author linhongwei
 * @date 2021-05-20
 */
public interface IConAdvanceSettleModeService extends IService<ConAdvanceSettleMode>{
    /**
     * 查询预收款/预付款付款方式
     *
     * @param sid 预收款/预付款付款方式ID
     * @return 预收款/预付款付款方式
     */
    public ConAdvanceSettleMode selectConAdvanceSettleModeById(Long sid);

    /**
     * 查询预收款/预付款付款方式列表
     *
     * @param conAdvanceSettleMode 预收款/预付款付款方式
     * @return 预收款/预付款付款方式集合
     */
    public List<ConAdvanceSettleMode> selectConAdvanceSettleModeList(ConAdvanceSettleMode conAdvanceSettleMode);

    /**
     * 新增预收款/预付款付款方式
     *
     * @param conAdvanceSettleMode 预收款/预付款付款方式
     * @return 结果
     */
    public int insertConAdvanceSettleMode(ConAdvanceSettleMode conAdvanceSettleMode);

    /**
     * 修改预收款/预付款付款方式
     *
     * @param conAdvanceSettleMode 预收款/预付款付款方式
     * @return 结果
     */
    public int updateConAdvanceSettleMode(ConAdvanceSettleMode conAdvanceSettleMode);

    /**
     * 变更预收款/预付款付款方式
     *
     * @param conAdvanceSettleMode 预收款/预付款付款方式
     * @return 结果
     */
    public int changeConAdvanceSettleMode(ConAdvanceSettleMode conAdvanceSettleMode);

    /**
     * 批量删除预收款/预付款付款方式
     *
     * @param sids 需要删除的预收款/预付款付款方式ID
     * @return 结果
     */
    public int deleteConAdvanceSettleModeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conAdvanceSettleMode
    * @return
    */
    int changeStatus(ConAdvanceSettleMode conAdvanceSettleMode);

    /**
     * 更改确认状态
     * @param conAdvanceSettleMode
     * @return
     */
    int check(ConAdvanceSettleMode conAdvanceSettleMode);

    /**  获取下拉列表 */
    List<ConAdvanceSettleMode> getConAdvanceSettleModeList();
}
