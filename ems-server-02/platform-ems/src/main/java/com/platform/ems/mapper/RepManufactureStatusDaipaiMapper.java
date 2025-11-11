package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.RepManufactureStatusDaipai;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 生产状况-待排产Mapper接口
 *
 * @author c
 * @date 2022-03-17
 */
public interface RepManufactureStatusDaipaiMapper extends BaseMapper<RepManufactureStatusDaipai> {


    RepManufactureStatusDaipai selectRepManufactureStatusDaipaiById(Long dataRecordSid);

    List<RepManufactureStatusDaipai> selectRepManufactureStatusDaipaiList(RepManufactureStatusDaipai repManufactureStatusDaipai);

    /**
     * 从销售订单排产进度/待排产销售订单明细的数据库表获取数据出来
     *
     * @param repManufactureStatusDaipai
     * @return int
     */
    List<RepManufactureStatusDaipai> getRepManufactureStatusDaipaiList(RepManufactureStatusDaipai repManufactureStatusDaipai);

    /**
     * 添加多个
     *
     * @param list List RepManufactureStatusDaipai
     * @return int
     */
    int inserts(@Param("list") List<RepManufactureStatusDaipai> list);

}
