package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConBuStockBusinessFlag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 业务标识_其它出入库Mapper接口
 *
 * @author wangp
 * @date 2022-10-09
 */
public interface ConBuStockBusinessFlagMapper extends BaseMapper<ConBuStockBusinessFlag> {


    ConBuStockBusinessFlag selectConBuStockBusinessFlagById(Long sid);

    List<ConBuStockBusinessFlag> selectConBuStockBusinessFlagList(ConBuStockBusinessFlag conBuStockBusinessFlag);

    /**
     * 业务标识_其它出入库列表下拉框
     *
     * @param conBuStockBusinessFlag 业务标识_其它出入库
     * @return 业务标识_其它出入库集合
     */
    List<ConBuStockBusinessFlag> getConBuStockBusinessFlagList(ConBuStockBusinessFlag conBuStockBusinessFlag);

    /**
     * 添加多个
     *
     * @param list List ConBuStockBusinessFlag
     * @return int
     */
    int inserts(@Param("list") List<ConBuStockBusinessFlag> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConBuStockBusinessFlag
     * @return int
     */
    int updateAllById(ConBuStockBusinessFlag entity);

    /**
     * 更新多个
     *
     * @param list List ConBuStockBusinessFlag
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuStockBusinessFlag> list);


}
