package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.ems.domain.FinPayBill;
import com.platform.ems.domain.base.EmsResultEntity;

import java.util.List;

/**
 * 付款单Service接口
 *
 * @author linhongwei
 * @date 2021-04-21
 */
public interface IFinPayBillService extends IService<FinPayBill> {

    /**
     * 查询付款单
     */
    public FinPayBill selectFinPayBillById(Long payBillSid);

    /**
     * 查询付款单列表
     */
    public List<FinPayBill> selectFinPayBillList(FinPayBill finPayBill);

    /**
     * 获取租户默认配置
     */
    public SysDefaultSettingClient getClientSetting();

    /**
     * 新建页面基本信息页签待核销金额
     */
    public void countBaseDai(FinPayBill finPayBill, SysDefaultSettingClient settingClient);

    /**
     * 新增付款单
     */
    public int insertFinPayBill(FinPayBill finPayBill);

    /**
     * 修改付款单
     */
    public int updateFinPayBill(FinPayBill finPayBill);

    /**
     * 变更付款单
     */
    public int changeFinPayBill(FinPayBill finPayBill);

    /**
     * 批量删除付款单
     */
    public int deleteFinPayBillByIds(List<Long> payBillSids);

    /**
     * 提交时校验
     */
    EmsResultEntity submitVerify(FinPayBill finPayBill);

    /**
     * 更改确认状态
     */
    int check(FinPayBill finPayBill);

    /**
     * 到账
     */
    int receipt(FinPayBill finPayBill);

    /**
     * 撤回保存
     */
    int revocation(FinPayBill finPayBill);

    /**
     * 设置是否有票
     */
    public int setIsyoupiao(FinPayBill finPayBill);

    /**
     * 更新发票台账 查询
     */
    FinPayBill invoiceList(FinPayBill finPayBill);

    /**
     * 更新发票台账
     */
    int invoiceUpdate(FinPayBill finPayBill);
}
