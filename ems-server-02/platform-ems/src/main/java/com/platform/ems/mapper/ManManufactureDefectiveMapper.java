package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManManufactureDefective;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 生产次品台账Mapper接口
 *
 * @author c
 * @date 2022-03-02
 */
public interface ManManufactureDefectiveMapper extends BaseMapper<ManManufactureDefective> {


    ManManufactureDefective selectManManufactureDefectiveById(Long manufactureDefectiveSid);

    List<ManManufactureDefective> selectManManufactureDefectiveList(ManManufactureDefective manManufactureDefective);

    /**
     * 添加多个
     *
     * @param list List ManManufactureDefective
     * @return int
     */
    int inserts(@Param("list") List<ManManufactureDefective> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManManufactureDefective
     * @return int
     */
    int updateAllById(ManManufactureDefective entity);

    /**
     * 更新多个
     *
     * @param list List ManManufactureDefective
     * @return int
     */
    int updatesAllById(@Param("list") List<ManManufactureDefective> list);


}
