package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.ems.domain.FinReceivableBill;
import com.platform.ems.domain.base.EmsResultEntity;

import java.util.List;

/**
 * 收款单Service接口
 *
 * @author linhongwei
 * @date 2021-04-22
 */
public interface IFinReceivableBillService extends IService<FinReceivableBill> {

    /**
     * 查询收款单
     *
     * @param receivableBillSid 收款单ID
     * @return 收款单
     */
    public FinReceivableBill selectFinReceivableBillById(Long receivableBillSid);

    /**
     * 查询收款单列表
     *
     * @param finReceivableBill 收款单
     * @return 收款单集合
     */
    public List<FinReceivableBill> selectFinReceivableBillList(FinReceivableBill finReceivableBill);

    /**
     * 获取租户默认配置
     * @return
     */
    public SysDefaultSettingClient getClientSetting();

    /**
     * 新建页面基本信息页签待核销金额
     *
     * @param finReceivableBill 收款单
     * @return 收款单集合
     */
    public void countBaseDai(FinReceivableBill finReceivableBill, SysDefaultSettingClient settingClient);

    /**
     * 新增收款单
     *
     * @param finReceivableBill 收款单
     * @return 结果
     */
    public int insertFinReceivableBill(FinReceivableBill finReceivableBill);

    /**
     * 修改收款单
     *
     * @param finReceivableBill 收款单
     * @return 结果
     */
    public int updateFinReceivableBill(FinReceivableBill finReceivableBill);

    /**
     * 变更收款单
     *
     * @param finReceivableBill 收款单
     * @return 结果
     */
    public int changeFinReceivableBill(FinReceivableBill finReceivableBill);

    /**
     * 批量删除收款单
     *
     * @param receivableBillSids 需要删除的收款单ID
     * @return 结果
     */
    public int deleteFinReceivableBillByIds(List<Long> receivableBillSids);

    /**
     * 提交时校验
     */
    EmsResultEntity submitVerify(FinReceivableBill finReceivableBill);

    /**
     * 更改确认状态
     *
     * @param finReceivableBill 请求参数
     * @return
     */
    int check(FinReceivableBill finReceivableBill);

    /**
     * 到账
     */
    int receipt(FinReceivableBill finReceivableBill);

    /**
     * 撤回保存
     */
    int revocation(FinReceivableBill finReceivableBill);

    /**
     * 设置是否有票
     */
    public int setIsyoupiao(FinReceivableBill finReceivableBill);

    /**
     * 更新发票台账 查询
     */
    FinReceivableBill invoiceList(FinReceivableBill finReceivableBill);

    /**
     * 更新发票台账
     */
    int invoiceUpdate(FinReceivableBill finReceivableBill);
}
