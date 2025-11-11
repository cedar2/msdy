package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypePayBill;

/**
 * 单据类型_付款单Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypePayBillService extends IService<ConDocTypePayBill>{

    /**
     * 查询单据类型_付款单
     */
    public ConDocTypePayBill selectConDocTypePayBillById(Long sid);

    /**
     * 查询单据类型_付款单列表
     */
    public List<ConDocTypePayBill> selectConDocTypePayBillList(ConDocTypePayBill conDocTypePayBill);

    /**
     * 新增单据类型_付款单
     */
    public int insertConDocTypePayBill(ConDocTypePayBill conDocTypePayBill);

    /**
     * 修改单据类型_付款单
     */
    public int updateConDocTypePayBill(ConDocTypePayBill conDocTypePayBill);

    /**
     * 变更单据类型_付款单
     */
    public int changeConDocTypePayBill(ConDocTypePayBill conDocTypePayBill);

    /**
     * 批量删除单据类型_付款单
     */
    public int deleteConDocTypePayBillByIds(List<Long>  sids);

    /**
     * 启用/停用
     */
    int changeStatus(ConDocTypePayBill conDocTypePayBill);

    /**
     * 更改确认状态
     */
    int check(ConDocTypePayBill conDocTypePayBill);

    /**
     * 获取下拉列表
     */
    List<ConDocTypePayBill> getConDocTypePayBillList(ConDocTypePayBill conDocType);
}
