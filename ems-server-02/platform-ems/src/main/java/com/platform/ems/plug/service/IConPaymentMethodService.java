package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConMaterialType;
import com.platform.ems.plug.domain.ConPaymentMethod;

/**
 * 支付方式Service接口
 *
 * @author linhongwei
 * @date 2021-05-20
 */
public interface IConPaymentMethodService extends IService<ConPaymentMethod>{
    /**
     * 查询支付方式
     *
     * @param sid 支付方式ID
     * @return 支付方式
     */
    public ConPaymentMethod selectConPaymentMethodById(Long sid);

    /**
     * 查询支付方式列表
     *
     * @param conPaymentMethod 支付方式
     * @return 支付方式集合
     */
    public List<ConPaymentMethod> selectConPaymentMethodList(ConPaymentMethod conPaymentMethod);

    /**
     * 新增支付方式
     *
     * @param conPaymentMethod 支付方式
     * @return 结果
     */
    public int insertConPaymentMethod(ConPaymentMethod conPaymentMethod);

    /**
     * 修改支付方式
     *
     * @param conPaymentMethod 支付方式
     * @return 结果
     */
    public int updateConPaymentMethod(ConPaymentMethod conPaymentMethod);

    /**
     * 变更支付方式
     *
     * @param conPaymentMethod 支付方式
     * @return 结果
     */
    public int changeConPaymentMethod(ConPaymentMethod conPaymentMethod);

    /**
     * 批量删除支付方式
     *
     * @param sids 需要删除的支付方式ID
     * @return 结果
     */
    public int deleteConPaymentMethodByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conPaymentMethod
    * @return
    */
    int changeStatus(ConPaymentMethod conPaymentMethod);

    /**
     * 更改确认状态
     * @param conPaymentMethod
     * @return
     */
    int check(ConPaymentMethod conPaymentMethod);

    /**  获取下拉列表 */
    List<ConPaymentMethod> getConPaymentMethodList(ConPaymentMethod conPaymentMethod);
}
