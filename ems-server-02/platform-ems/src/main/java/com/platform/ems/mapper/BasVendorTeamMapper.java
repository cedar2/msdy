package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorTeam;

/**
 * 供应商的人员信息Mapper接口
 *
 * @author chenkw
 * @date 2022-01-06
 */
public interface BasVendorTeamMapper extends BaseMapper<BasVendorTeam> {


    BasVendorTeam selectBasVendorTeamById(Long vendorTeamSid);

    List<BasVendorTeam> selectBasVendorTeamList(BasVendorTeam basVendorTeam);

    /**
     * 添加多个
     *
     * @param list List BasVendorTeam
     * @return int
     */
    int inserts(@Param("list") List<BasVendorTeam> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasVendorTeam
     * @return int
     */
    int updateAllById(BasVendorTeam entity);

    /**
     * 更新多个
     *
     * @param list List BasVendorTeam
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorTeam> list);


}
