package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.RepManufactureStatusCipin;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 生产状况-次品Mapper接口
 *
 * @author c
 * @date 2022-03-17
 */
public interface RepManufactureStatusCipinMapper extends BaseMapper<RepManufactureStatusCipin> {


    RepManufactureStatusCipin selectRepManufactureStatusCipinById(Long dataRecordSid);

    List<RepManufactureStatusCipin> selectRepManufactureStatusCipinList(RepManufactureStatusCipin repManufactureStatusCipin);

    /**
     * 从生产次品台账对象的数据库表获取数据出来
     *
     * @param repManufactureStatusCipin
     * @return int
     */
    List<RepManufactureStatusCipin> getRepManufactureStatusCipinList(RepManufactureStatusCipin repManufactureStatusCipin);

    /**
     * 添加多个
     *
     * @param list List RepManufactureStatusCipin
     * @return int
     */
    int inserts(@Param("list") List<RepManufactureStatusCipin> list);

}
