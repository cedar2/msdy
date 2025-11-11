package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepFinanceStatusYings;

/**
 * 财务状况-客户-应收Mapper接口
 *
 * @author chenkw
 * @date 2022-02-25
 */
public interface RepFinanceStatusYingsMapper extends BaseMapper<RepFinanceStatusYings> {


    RepFinanceStatusYings selectRepFinanceStatusYingsById(Long dataRecordSid);

    List<RepFinanceStatusYings> selectRepFinanceStatusYingsList(RepFinanceStatusYings repFinanceStatusYings);

    /**
     * 从应收流水数据库表获取数据出来
     *
     * @param repFinanceStatusYings
     * @return int
     */
    List<RepFinanceStatusYings> getRepFinanceStatusYingsList(RepFinanceStatusYings repFinanceStatusYings);

    /**
     * 添加多个
     *
     * @param list List RepFinanceStatusYings
     * @return int
     */
    int inserts(@Param("list") List<RepFinanceStatusYings> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepFinanceStatusYings
     * @return int
     */
    int updateAllById(RepFinanceStatusYings entity);

    /**
     * 更新多个
     *
     * @param list List RepFinanceStatusYings
     * @return int
     */
    int updatesAllById(@Param("list") List<RepFinanceStatusYings> list);


}
