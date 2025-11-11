package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureOutsourceSettle;
import com.platform.ems.domain.base.EmsResultEntity;

import java.util.List;

/**
 * 外发加工费结算单Service接口
 *
 * @author linhongwei
 * @date 2021-06-10
 */
public interface IManManufactureOutsourceSettleService extends IService<ManManufactureOutsourceSettle> {
    /**
     * 查询外发加工费结算单
     *
     * @param manufactureOutsourceSettleSid 外发加工费结算单ID
     * @return 外发加工费结算单
     */
    public ManManufactureOutsourceSettle selectManManufactureOutsourceSettleById(Long manufactureOutsourceSettleSid);

    /**
     * 查询外发加工费结算单列表
     *
     * @param manManufactureOutsourceSettle 外发加工费结算单
     * @return 外发加工费结算单集合
     */
    public List<ManManufactureOutsourceSettle> selectManManufactureOutsourceSettleList(ManManufactureOutsourceSettle manManufactureOutsourceSettle);

    /**
     * 新增外发加工费结算单
     *
     * @param manManufactureOutsourceSettle 外发加工费结算单
     * @return 结果
     */
    public int insertManManufactureOutsourceSettle(ManManufactureOutsourceSettle manManufactureOutsourceSettle);

    /**
     * 修改外发加工费结算单
     *
     * @param manManufactureOutsourceSettle 外发加工费结算单
     * @return 结果
     */
    public int updateManManufactureOutsourceSettle(ManManufactureOutsourceSettle manManufactureOutsourceSettle);

    /**
     * 变更外发加工费结算单
     *
     * @param manManufactureOutsourceSettle 外发加工费结算单
     * @return 结果
     */
    public int changeManManufactureOutsourceSettle(ManManufactureOutsourceSettle manManufactureOutsourceSettle);

    /**
     * 批量删除外发加工费结算单
     *
     * @param manufactureOutsourceSettleSids 需要删除的外发加工费结算单ID
     * @return 结果
     */
    public int deleteManManufactureOutsourceSettleByIds(List<Long> manufactureOutsourceSettleSids);

    /**
     * 更改确认状态
     *
     * @param manManufactureOutsourceSettle
     * @return
     */
    int check(ManManufactureOutsourceSettle manManufactureOutsourceSettle);

    /**
     * 更改确认状态
     *
     * @param manManufactureOutsourceSettle
     * @return
     */
    int approval(ManManufactureOutsourceSettle manManufactureOutsourceSettle);

    EmsResultEntity verify(ManManufactureOutsourceSettle settle);

    /**
     * 作废外发加工费结算单
     */
    int cancellationManufactureOutsourceSettleById(Long manufactureOutsourceSettleSid);

    /**
     * 获取明细的加工采购价
     * @param settle
     * @return
     */
    ManManufactureOutsourceSettle itemGetPrice(ManManufactureOutsourceSettle settle, boolean flag);
}
