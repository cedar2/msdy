package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypePayBill;

/**
 * 业务类型_付款单Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypePayBillService extends IService<ConBuTypePayBill>{
    /**
     * 查询业务类型_付款单
     *
     * @param sid 业务类型_付款单ID
     * @return 业务类型_付款单
     */
    public ConBuTypePayBill selectConBuTypePayBillById(Long sid);

    /**
     * 查询业务类型_付款单列表
     *
     * @param conBuTypePayBill 业务类型_付款单
     * @return 业务类型_付款单集合
     */
    public List<ConBuTypePayBill> selectConBuTypePayBillList(ConBuTypePayBill conBuTypePayBill);

    /**
     * 新增业务类型_付款单
     *
     * @param conBuTypePayBill 业务类型_付款单
     * @return 结果
     */
    public int insertConBuTypePayBill(ConBuTypePayBill conBuTypePayBill);

    /**
     * 修改业务类型_付款单
     *
     * @param conBuTypePayBill 业务类型_付款单
     * @return 结果
     */
    public int updateConBuTypePayBill(ConBuTypePayBill conBuTypePayBill);

    /**
     * 变更业务类型_付款单
     *
     * @param conBuTypePayBill 业务类型_付款单
     * @return 结果
     */
    public int changeConBuTypePayBill(ConBuTypePayBill conBuTypePayBill);

    /**
     * 批量删除业务类型_付款单
     *
     * @param sids 需要删除的业务类型_付款单ID
     * @return 结果
     */
    public int deleteConBuTypePayBillByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypePayBill
    * @return
    */
    int changeStatus(ConBuTypePayBill conBuTypePayBill);

    /**
     * 更改确认状态
     * @param conBuTypePayBill
     * @return
     */
    int check(ConBuTypePayBill conBuTypePayBill);

    /**  获取下拉列表 */
    List<ConBuTypePayBill> getConBuTypePayBillList();
}
