package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypePayBill;
import com.platform.ems.plug.domain.ConDocTypeReceivableBill;

/**
 * 单据类型_收款单Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeReceivableBillService extends IService<ConDocTypeReceivableBill>{

    /**
     * 查询单据类型_收款单
     */
    public ConDocTypeReceivableBill selectConDocTypeReceivableBillById(Long sid);

    /**
     * 查询单据类型_收款单列表
     */
    public List<ConDocTypeReceivableBill> selectConDocTypeReceivableBillList(ConDocTypeReceivableBill conDocTypeReceivableBill);

    /**
     * 新增单据类型_收款单
     *
     * @param conDocTypeReceivableBill 单据类型_收款单
     * @return 结果
     */
    public int insertConDocTypeReceivableBill(ConDocTypeReceivableBill conDocTypeReceivableBill);

    /**
     * 修改单据类型_收款单
     */
    public int updateConDocTypeReceivableBill(ConDocTypeReceivableBill conDocTypeReceivableBill);

    /**
     * 变更单据类型_收款单
     */
    public int changeConDocTypeReceivableBill(ConDocTypeReceivableBill conDocTypeReceivableBill);

    /**
     * 批量删除单据类型_收款单
     */
    public int deleteConDocTypeReceivableBillByIds(List<Long>  sids);

    /**
     * 启用/停用
     */
    int changeStatus(ConDocTypeReceivableBill conDocTypeReceivableBill);

    /**
     * 更改确认状态
     */
    int check(ConDocTypeReceivableBill conDocTypeReceivableBill);

    /**
     *  获取下拉列表
     */
    List<ConDocTypeReceivableBill> getConDocTypeReceivableBillList(ConDocTypeReceivableBill conDocType);
}
