package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManManufactureOrderProcess;
import com.platform.ems.domain.ManWorkCenter;
import com.platform.ems.domain.dto.request.ManWorkCenterReportRequest;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 工作中心/班组Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-26
 */
public interface ManWorkCenterMapper extends BaseMapper<ManWorkCenter> {


    ManWorkCenter selectManWorkCenterById(Long workCenterSid);

    List<ManWorkCenter> selectManWorkCenterList(ManWorkCenter manWorkCenter);

    @MapKey("workCenterSid")
    Map<Long, ManWorkCenter> selectManWorkCenterProcessNameList(ManWorkCenter manWorkCenter);

    List<ManManufactureOrderProcess> selectManWorkCenterReportList(ManWorkCenterReportRequest manWorkCenter);

    /**
     * 添加多个
     *
     * @param list List ManWorkCenter
     * @return int
     */
    int inserts(@Param("list") List<ManWorkCenter> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManWorkCenter
     * @return int
     */
    int updateAllById(ManWorkCenter entity);

    /**
     * 更新多个
     *
     * @param list List ManWorkCenter
     * @return int
     */
    int updatesAllById(@Param("list") List<ManWorkCenter> list);

    /**
     * 档案列表
     *
     * @param
     * @return int
     */
    List<ManWorkCenter> getList();

    List<ManWorkCenter> getWorkCenterList(ManWorkCenter manWorkCenter);
}
