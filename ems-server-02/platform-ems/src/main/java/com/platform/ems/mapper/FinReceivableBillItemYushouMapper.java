package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinReceivableBillItemYushou;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收款单-核销预收明细表Mapper接口
 *
 * @author platform
 * @date 2024-03-12
 */
public interface FinReceivableBillItemYushouMapper extends BaseMapper<FinReceivableBillItemYushou> {

    /**
     * 查询详情
     *
     * @param receivableBillItemYushouSid 单据sid
     * @return FinReceivableBillItemYushou
     */
    FinReceivableBillItemYushou selectFinReceivableBillItemYushouById(Long receivableBillItemYushouSid);

    /**
     * 查询列表
     *
     * @param finReceivableBillItemYushou FinReceivableBillItemYushou
     * @return List
     */
    List<FinReceivableBillItemYushou> selectFinReceivableBillItemYushouList(FinReceivableBillItemYushou finReceivableBillItemYushou);

    /**
     * 添加多个
     *
     * @param list List FinReceivableBillItemYushou
     * @return int
     */
    int inserts(@Param("list") List<FinReceivableBillItemYushou> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FinReceivableBillItemYushou
     * @return int
     */
    int updateAllById(FinReceivableBillItemYushou entity);

    /**
     * 更新多个
     *
     * @param list List FinReceivableBillItemYushou
     * @return int
     */
    int updatesAllById(@Param("list") List<FinReceivableBillItemYushou> list);

}
