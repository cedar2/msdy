package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.RepManufactureStatusZaicha;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 生产状况-在产Mapper接口
 *
 * @author c
 * @date 2022-03-17
 */
public interface RepManufactureStatusZaichaMapper extends BaseMapper<RepManufactureStatusZaicha> {


    RepManufactureStatusZaicha selectRepManufactureStatusZaichaById(Long dataRecordSid);

    List<RepManufactureStatusZaicha> selectRepManufactureStatusZaichaList(RepManufactureStatusZaicha repManufactureStatusZaicha);

    /**
     * 从生产订单-产品明细对象的数据库表获取数据出来
     *
     * @param repManufactureStatusZaicha
     * @return int
     */
    List<RepManufactureStatusZaicha> getRepManufactureStatusZaichaList(RepManufactureStatusZaicha repManufactureStatusZaicha);

    /**
     * 添加多个
     *
     * @param list List RepManufactureStatusZaicha
     * @return int
     */
    int inserts(@Param("list") List<RepManufactureStatusZaicha> list);

}
