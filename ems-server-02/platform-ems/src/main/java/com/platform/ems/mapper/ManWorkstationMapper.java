package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManWorkstation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工位档案Mapper接口
 *
 * @author Straw
 * @date 2023-03-31
 */
public interface ManWorkstationMapper extends BaseMapper<ManWorkstation> {


    ManWorkstation selectManWorkstationById(Long workstationSid);

    List<ManWorkstation> selectManWorkstationList(ManWorkstation manWorkstation);

    /**
     * 添加多个
     *
     * @param list List ManWorkstation
     * @return int
     */
    int inserts(@Param("list") List<ManWorkstation> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManWorkstation
     * @return int
     */
    int updateAllById(ManWorkstation entity);

    /**
     * 更新多个
     *
     * @param list List ManWorkstation
     * @return int
     */
    int updatesAllById(@Param("list") List<ManWorkstation> list);


}
