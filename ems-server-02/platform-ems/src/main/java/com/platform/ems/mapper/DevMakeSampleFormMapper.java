package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.DevMakeSampleForm;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 打样准许单Mapper接口
 *
 * @author linhongwei
 * @date 2022-03-24
 */
public interface DevMakeSampleFormMapper extends BaseMapper<DevMakeSampleForm> {


    DevMakeSampleForm selectDevMakeSampleFormById(Long makeSampleFormSid);

    List<DevMakeSampleForm> selectDevMakeSampleFormList(DevMakeSampleForm devMakeSampleForm);

    /**
     * 添加多个
     *
     * @param list List DevMakeSampleForm
     * @return int
     */
    int inserts(@Param("list") List<DevMakeSampleForm> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity DevMakeSampleForm
     * @return int
     */
    int updateAllById(DevMakeSampleForm entity);

    /**
     * 更新多个
     *
     * @param list List DevMakeSampleForm
     * @return int
     */
    int updatesAllById(@Param("list") List<DevMakeSampleForm> list);


    int updateHandleStatus(DevMakeSampleForm devMakeSampleForm);
}
