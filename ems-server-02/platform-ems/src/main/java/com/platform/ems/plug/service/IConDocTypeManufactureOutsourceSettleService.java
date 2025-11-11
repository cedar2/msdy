package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeManufactureOutsourceSettle;

import java.util.List;

/**
 * 单据类型_外发加工费结算单Service接口
 *
 * @author c
 * @date 2021-11-25
 */
public interface IConDocTypeManufactureOutsourceSettleService extends IService<ConDocTypeManufactureOutsourceSettle> {
    /**
     * 查询单据类型_外发加工费结算单
     *
     * @param sid 单据类型_外发加工费结算单ID
     * @return 单据类型_外发加工费结算单
     */
    public ConDocTypeManufactureOutsourceSettle selectConDocTypeManOutsourceSettleById(Long sid);

    /**
     * 查询单据类型_外发加工费结算单列表
     *
     * @param outsourceSettle 单据类型_外发加工费结算单
     * @return 单据类型_外发加工费结算单集合
     */
    public List<ConDocTypeManufactureOutsourceSettle> selectConDocTypeManOutsourceSettleList(ConDocTypeManufactureOutsourceSettle outsourceSettle);

    /**
     * 新增单据类型_外发加工费结算单
     *
     * @param outsourceSettle 单据类型_外发加工费结算单
     * @return 结果
     */
    public int insertConDocTypeManOutsourceSettle(ConDocTypeManufactureOutsourceSettle outsourceSettle);

    /**
     * 修改单据类型_外发加工费结算单
     *
     * @param outsourceSettle 单据类型_外发加工费结算单
     * @return 结果
     */
    public int updateConDocTypeManOutsourceSettle(ConDocTypeManufactureOutsourceSettle outsourceSettle);

    /**
     * 变更单据类型_外发加工费结算单
     *
     * @param outsourceSettle 单据类型_外发加工费结算单
     * @return 结果
     */
    public int changeConDocTypeManOutsourceSettle(ConDocTypeManufactureOutsourceSettle outsourceSettle);

    /**
     * 批量删除单据类型_外发加工费结算单
     *
     * @param sids 需要删除的单据类型_外发加工费结算单ID
     * @return 结果
     */
    public int deleteConDocTypeManOutsourceSettleByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param outsourceSettle
     * @return
     */
    int changeStatus(ConDocTypeManufactureOutsourceSettle outsourceSettle);

    /**
     * 更改确认状态
     *
     * @param outsourceSettle
     * @return
     */
    int check(ConDocTypeManufactureOutsourceSettle outsourceSettle);

    List<ConDocTypeManufactureOutsourceSettle> getDocOutsourceSettleList(ConDocTypeManufactureOutsourceSettle outsourceSettle);
}
