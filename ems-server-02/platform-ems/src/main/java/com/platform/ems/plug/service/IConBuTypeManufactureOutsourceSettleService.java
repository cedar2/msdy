package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeManufactureOutsourceSettle;

import java.util.List;

/**
 * 业务类型_外发加工费结算单Service接口
 *
 * @author c
 * @date 2021-11-25
 */
public interface IConBuTypeManufactureOutsourceSettleService extends IService<ConBuTypeManufactureOutsourceSettle> {
    /**
     * 查询业务类型_外发加工费结算单
     *
     * @param sid 业务类型_外发加工费结算单ID
     * @return 业务类型_外发加工费结算单
     */
    public ConBuTypeManufactureOutsourceSettle selectConBuTypeManOutsourceSettleById(Long sid);

    /**
     * 查询业务类型_外发加工费结算单列表
     *
     * @param outsourceSettle 业务类型_外发加工费结算单
     * @return 业务类型_外发加工费结算单集合
     */
    public List<ConBuTypeManufactureOutsourceSettle> selectConBuTypeManOutsourceSettleList(ConBuTypeManufactureOutsourceSettle outsourceSettle);

    /**
     * 新增业务类型_外发加工费结算单
     *
     * @param outsourceSettle 业务类型_外发加工费结算单
     * @return 结果
     */
    public int insertConBuTypeManOutsourceSettle(ConBuTypeManufactureOutsourceSettle outsourceSettle);

    /**
     * 修改业务类型_外发加工费结算单
     *
     * @param outsourceSettle 业务类型_外发加工费结算单
     * @return 结果
     */
    public int updateConBuTypeManOutsourceSettle(ConBuTypeManufactureOutsourceSettle outsourceSettle);

    /**
     * 变更业务类型_外发加工费结算单
     *
     * @param outsourceSettle 业务类型_外发加工费结算单
     * @return 结果
     */
    public int changeConBuTypeManOutsourceSettle(ConBuTypeManufactureOutsourceSettle outsourceSettle);

    /**
     * 批量删除业务类型_外发加工费结算单
     *
     * @param sids 需要删除的业务类型_外发加工费结算单ID
     * @return 结果
     */
    public int deleteConBuTypeManOutsourceSettleByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param outsourceSettle
     * @return
     */
    int changeStatus(ConBuTypeManufactureOutsourceSettle outsourceSettle);

    /**
     * 更改确认状态
     *
     * @param outsourceSettle
     * @return
     */
    int check(ConBuTypeManufactureOutsourceSettle outsourceSettle);

    List<ConBuTypeManufactureOutsourceSettle> getOutsourceSettleList(ConBuTypeManufactureOutsourceSettle outsourceSettle);
}
