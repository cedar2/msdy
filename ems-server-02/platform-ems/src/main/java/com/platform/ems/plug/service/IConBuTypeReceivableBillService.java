package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypePayBill;
import com.platform.ems.plug.domain.ConBuTypeReceivableBill;

/**
 * 业务类型_收款单Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeReceivableBillService extends IService<ConBuTypeReceivableBill>{
    /**
     * 查询业务类型_收款单
     *
     * @param sid 业务类型_收款单ID
     * @return 业务类型_收款单
     */
    public ConBuTypeReceivableBill selectConBuTypeReceivableBillById(Long sid);

    /**
     * 查询业务类型_收款单列表
     *
     * @param conBuTypeReceivableBill 业务类型_收款单
     * @return 业务类型_收款单集合
     */
    public List<ConBuTypeReceivableBill> selectConBuTypeReceivableBillList(ConBuTypeReceivableBill conBuTypeReceivableBill);

    /**
     * 新增业务类型_收款单
     *
     * @param conBuTypeReceivableBill 业务类型_收款单
     * @return 结果
     */
    public int insertConBuTypeReceivableBill(ConBuTypeReceivableBill conBuTypeReceivableBill);

    /**
     * 修改业务类型_收款单
     *
     * @param conBuTypeReceivableBill 业务类型_收款单
     * @return 结果
     */
    public int updateConBuTypeReceivableBill(ConBuTypeReceivableBill conBuTypeReceivableBill);

    /**
     * 变更业务类型_收款单
     *
     * @param conBuTypeReceivableBill 业务类型_收款单
     * @return 结果
     */
    public int changeConBuTypeReceivableBill(ConBuTypeReceivableBill conBuTypeReceivableBill);

    /**
     * 批量删除业务类型_收款单
     *
     * @param sids 需要删除的业务类型_收款单ID
     * @return 结果
     */
    public int deleteConBuTypeReceivableBillByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeReceivableBill
    * @return
    */
    int changeStatus(ConBuTypeReceivableBill conBuTypeReceivableBill);

    /**
     * 更改确认状态
     * @param conBuTypeReceivableBill
     * @return
     */
    int check(ConBuTypeReceivableBill conBuTypeReceivableBill);

    /**  获取下拉列表 */
    List<ConBuTypeReceivableBill> getConBuTypeReceivableBillList();
}
