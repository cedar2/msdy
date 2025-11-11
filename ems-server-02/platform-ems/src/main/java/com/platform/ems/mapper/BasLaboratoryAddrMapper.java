package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasLaboratoryAddr;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 实验室-联系方式信息Mapper接口
 *
 * @author c
 * @date 2022-03-31
 */
public interface BasLaboratoryAddrMapper extends BaseMapper<BasLaboratoryAddr> {


    BasLaboratoryAddr selectBasLaboratoryAddrById(Long laboratoryContactSid);

    List<BasLaboratoryAddr> selectBasLaboratoryAddrList(BasLaboratoryAddr basLaboratoryAddr);

    /**
     * 添加多个
     *
     * @param list List BasLaboratoryAddr
     * @return int
     */
    int inserts(@Param("list") List<BasLaboratoryAddr> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasLaboratoryAddr
     * @return int
     */
    int updateAllById(BasLaboratoryAddr entity);

    /**
     * 更新多个
     *
     * @param list List BasLaboratoryAddr
     * @return int
     */
    int updatesAllById(@Param("list") List<BasLaboratoryAddr> list);


}
