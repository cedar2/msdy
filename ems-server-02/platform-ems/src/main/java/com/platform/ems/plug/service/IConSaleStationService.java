package com.platform.ems.plug.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConSaleStation;

/**
 * 销售站点Service接口
 *
 * @author chenkw
 * @date 2023-01-02
 */
public interface IConSaleStationService extends IService<ConSaleStation> {
    /**
     * 查询销售站点
     *
     * @param sid 销售站点ID
     * @return 销售站点
     */
    public ConSaleStation selectConSaleStationById(Long sid);

    /**
     * 查询销售站点列表
     *
     * @param conSaleStation 销售站点
     * @return 销售站点集合
     */
    public List<ConSaleStation> selectConSaleStationList(ConSaleStation conSaleStation);

    /**
     * 新增销售站点
     *
     * @param conSaleStation 销售站点
     * @return 结果
     */
    public int insertConSaleStation(ConSaleStation conSaleStation);

    /**
     * 修改销售站点
     *
     * @param conSaleStation 销售站点
     * @return 结果
     */
    public int updateConSaleStation(ConSaleStation conSaleStation);

    /**
     * 变更销售站点
     *
     * @param conSaleStation 销售站点
     * @return 结果
     */
    public int changeConSaleStation(ConSaleStation conSaleStation);

    /**
     * 批量删除销售站点
     *
     * @param sids 需要删除的销售站点ID
     * @return 结果
     */
    public int deleteConSaleStationByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conSaleStation
     * @return
     */
    int changeStatus(ConSaleStation conSaleStation);

    /**
     * 更改确认状态
     *
     * @param conSaleStation
     * @return
     */
    int check(ConSaleStation conSaleStation);

}
