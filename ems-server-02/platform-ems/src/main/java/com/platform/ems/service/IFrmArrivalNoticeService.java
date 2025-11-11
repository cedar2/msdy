package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FrmArrivalNotice;
import com.platform.ems.domain.base.EmsResultEntity;

/**
 * 到货通知单Service接口
 *
 * @author chenkw
 * @date 2022-12-13
 */
public interface IFrmArrivalNoticeService extends IService<FrmArrivalNotice> {
    /**
     * 查询到货通知单
     *
     * @param arrivalNoticeSid 到货通知单ID
     * @return 到货通知单
     */
    public FrmArrivalNotice selectFrmArrivalNoticeById(Long arrivalNoticeSid);

    /**
     * 查询到货通知单列表
     *
     * @param frmArrivalNotice 到货通知单
     * @return 到货通知单集合
     */
    public List<FrmArrivalNotice> selectFrmArrivalNoticeList(FrmArrivalNotice frmArrivalNotice);

    /**
     * 新增到货通知单
     *
     * @param frmArrivalNotice 到货通知单
     * @return 结果
     */
    public int insertFrmArrivalNotice(FrmArrivalNotice frmArrivalNotice);

    /**
     * 修改到货通知单
     *
     * @param frmArrivalNotice 到货通知单
     * @return 结果
     */
    public int updateFrmArrivalNotice(FrmArrivalNotice frmArrivalNotice);

    /**
     * 变更到货通知单
     *
     * @param frmArrivalNotice 到货通知单
     * @return 结果
     */
    public int changeFrmArrivalNotice(FrmArrivalNotice frmArrivalNotice);

    /**
     * 批量删除到货通知单
     *
     * @param arrivalNoticeSids 需要删除的到货通知单ID
     * @return 结果
     */
    public int deleteFrmArrivalNoticeByIds(List<Long> arrivalNoticeSids);

    /**
     * 提交前校验
     *
     * @param frmArrivalNotice 新品试销计划单
     * @return 结果
     */
    public EmsResultEntity submitVerify(FrmArrivalNotice frmArrivalNotice);

    /**
     * 更改确认状态
     *
     * @param frmArrivalNotice
     * @return
     */
    int check(FrmArrivalNotice frmArrivalNotice);

}
