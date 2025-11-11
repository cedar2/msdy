package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypePayBill;
import com.platform.ems.plug.domain.ConRemainSettleMode;

/**
 * 尾款结算方式Service接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConRemainSettleModeService extends IService<ConRemainSettleMode>{
    /**
     * 查询尾款结算方式
     *
     * @param sid 尾款结算方式ID
     * @return 尾款结算方式
     */
    public ConRemainSettleMode selectConRemainSettleModeById(Long sid);

    /**
     * 查询尾款结算方式列表
     *
     * @param conRemainSettleMode 尾款结算方式
     * @return 尾款结算方式集合
     */
    public List<ConRemainSettleMode> selectConRemainSettleModeList(ConRemainSettleMode conRemainSettleMode);

    /**
     * 新增尾款结算方式
     *
     * @param conRemainSettleMode 尾款结算方式
     * @return 结果
     */
    public int insertConRemainSettleMode(ConRemainSettleMode conRemainSettleMode);

    /**
     * 修改尾款结算方式
     *
     * @param conRemainSettleMode 尾款结算方式
     * @return 结果
     */
    public int updateConRemainSettleMode(ConRemainSettleMode conRemainSettleMode);

    /**
     * 变更尾款结算方式
     *
     * @param conRemainSettleMode 尾款结算方式
     * @return 结果
     */
    public int changeConRemainSettleMode(ConRemainSettleMode conRemainSettleMode);

    /**
     * 批量删除尾款结算方式
     *
     * @param sids 需要删除的尾款结算方式ID
     * @return 结果
     */
    public int deleteConRemainSettleModeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conRemainSettleMode
    * @return
    */
    int changeStatus(ConRemainSettleMode conRemainSettleMode);

    /**
     * 更改确认状态
     * @param conRemainSettleMode
     * @return
     */
    int check(ConRemainSettleMode conRemainSettleMode);

    /**  获取下拉列表 */
    List<ConRemainSettleMode> getConRemainSettleModeList();
}
