package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepFinanceStatus;

/**
 * 财务状况Mapper接口
 *
 * @author chenkw
 * @date 2022-02-25
 */
public interface RepFinanceStatusMapper extends BaseMapper<RepFinanceStatus> {


    RepFinanceStatus selectRepFinanceStatusById(Long dataRecordSid);

    List<RepFinanceStatus> selectRepFinanceStatusList(RepFinanceStatus repFinanceStatus);

    /**
     * 添加多个
     *
     * @param list List RepFinanceStatus
     * @return int
     */
    int inserts(@Param("list") List<RepFinanceStatus> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepFinanceStatus
     * @return int
     */
    int updateAllById(RepFinanceStatus entity);

    /**
     * 更新多个
     *
     * @param list List RepFinanceStatus
     * @return int
     */
    int updatesAllById(@Param("list") List<RepFinanceStatus> list);


}
