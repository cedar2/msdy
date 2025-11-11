package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuStockBusinessFlag;

import java.util.List;

/**
 * 业务标识_其它出入库Service接口
 *
 * @author wangp
 * @date 2022-10-09
 */
public interface IConBuStockBusinessFlagService extends IService<ConBuStockBusinessFlag> {
    /**
     * 查询业务标识_其它出入库
     *
     * @param sid 业务标识_其它出入库ID
     * @return 业务标识_其它出入库
     */
    public ConBuStockBusinessFlag selectConBuStockBusinessFlagById(Long sid);

    /**
     * 查询业务标识_其它出入库列表
     *
     * @param conBuStockBusinessFlag 业务标识_其它出入库
     * @return 业务标识_其它出入库集合
     */
    public List<ConBuStockBusinessFlag> selectConBuStockBusinessFlagList(ConBuStockBusinessFlag conBuStockBusinessFlag);

    /**
     * 业务标识_其它出入库列表下拉框
     *
     * @param conBuStockBusinessFlag 业务标识_其它出入库
     * @return 业务标识_其它出入库集合
     */
    public List<ConBuStockBusinessFlag> getConBuStockBusinessFlagList(ConBuStockBusinessFlag conBuStockBusinessFlag);

    /**
     * 新增业务标识_其它出入库
     *
     * @param conBuStockBusinessFlag 业务标识_其它出入库
     * @return 结果
     */
    public int insertConBuStockBusinessFlag(ConBuStockBusinessFlag conBuStockBusinessFlag);

    /**
     * 修改业务标识_其它出入库
     *
     * @param conBuStockBusinessFlag 业务标识_其它出入库
     * @return 结果
     */
    public int updateConBuStockBusinessFlag(ConBuStockBusinessFlag conBuStockBusinessFlag);

    /**
     * 变更业务标识_其它出入库
     *
     * @param conBuStockBusinessFlag 业务标识_其它出入库
     * @return 结果
     */
    public int changeConBuStockBusinessFlag(ConBuStockBusinessFlag conBuStockBusinessFlag);

    /**
     * 批量删除业务标识_其它出入库
     *
     * @param sids 需要删除的业务标识_其它出入库ID
     * @return 结果
     */
    public int deleteConBuStockBusinessFlagByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conBuStockBusinessFlag
     * @return
     */
    int changeStatus(ConBuStockBusinessFlag conBuStockBusinessFlag);

    /**
     * 更改确认状态
     *
     * @param conBuStockBusinessFlag
     * @return
     */
    int check(ConBuStockBusinessFlag conBuStockBusinessFlag);

}
