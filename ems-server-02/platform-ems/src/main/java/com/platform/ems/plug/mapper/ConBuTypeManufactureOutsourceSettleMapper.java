package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConBuTypeManufactureOutsourceSettle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 业务类型_外发加工费结算单Mapper接口
 *
 * @author c
 * @date 2021-11-25
 */
public interface ConBuTypeManufactureOutsourceSettleMapper extends BaseMapper<ConBuTypeManufactureOutsourceSettle> {

    ConBuTypeManufactureOutsourceSettle selectConBuTypeManOutsourceSettleById(Long sid);

    List<ConBuTypeManufactureOutsourceSettle> selectConBuTypeManOutsourceSettleList(ConBuTypeManufactureOutsourceSettle outsourceSettle);

    /**
     * 添加多个
     *
     * @param list List ConBuTypeManufactureOutsourceSettle
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeManufactureOutsourceSettle> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConBuTypeManufactureOutsourceSettle
     * @return int
     */
    int updateAllById(ConBuTypeManufactureOutsourceSettle entity);

    /**
     * 更新多个
     *
     * @param list List ConBuTypeManufactureOutsourceSettle
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeManufactureOutsourceSettle> list);


    List<ConBuTypeManufactureOutsourceSettle> getOutsourceSettleList(ConBuTypeManufactureOutsourceSettle outsourceSettle);
}
