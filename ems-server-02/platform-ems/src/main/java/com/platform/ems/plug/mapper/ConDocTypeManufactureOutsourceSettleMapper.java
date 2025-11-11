package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConDocTypeManufactureOutsourceSettle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 单据类型_外发加工费结算单Mapper接口
 *
 * @author c
 * @date 2021-11-25
 */
public interface ConDocTypeManufactureOutsourceSettleMapper extends BaseMapper<ConDocTypeManufactureOutsourceSettle> {


    ConDocTypeManufactureOutsourceSettle selectConDocTypeManOutsourceSettleById(Long sid);

    List<ConDocTypeManufactureOutsourceSettle> selectConDocTypeManOutsourceSettleList(ConDocTypeManufactureOutsourceSettle outsourceSettle);

    /**
     * 添加多个
     *
     * @param list List ConDocTypeManufactureOutsourceSettle
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeManufactureOutsourceSettle> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConDocTypeManufactureOutsourceSettle
     * @return int
     */
    int updateAllById(ConDocTypeManufactureOutsourceSettle entity);

    /**
     * 更新多个
     *
     * @param list List ConDocTypeManufactureOutsourceSettle
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeManufactureOutsourceSettle> list);


    List<ConDocTypeManufactureOutsourceSettle> getDocOutsourceSettleList(ConDocTypeManufactureOutsourceSettle outsourceSettle);
}
