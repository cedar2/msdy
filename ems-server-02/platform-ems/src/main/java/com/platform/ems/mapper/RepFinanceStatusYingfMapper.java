package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepFinanceStatusYingf;

/**
 * 财务状况-供应商-应付Mapper接口
 *
 * @author chenkw
 * @date 2022-02-25
 */
public interface RepFinanceStatusYingfMapper extends BaseMapper<RepFinanceStatusYingf> {


    RepFinanceStatusYingf selectRepFinanceStatusYingfById(Long dataRecordSid);

    List<RepFinanceStatusYingf> selectRepFinanceStatusYingfList(RepFinanceStatusYingf repFinanceStatusYingf);

    /**
     * 从应付流水数据库表获取数据出来
     *
     * @param repFinanceStatusYingf
     * @return int
     */
    List<RepFinanceStatusYingf> getRepFinanceStatusYingfList(RepFinanceStatusYingf repFinanceStatusYingf);

    /**
     * 添加多个
     *
     * @param list List RepFinanceStatusYingf
     * @return int
     */
    int inserts(@Param("list") List<RepFinanceStatusYingf> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepFinanceStatusYingf
     * @return int
     */
    int updateAllById(RepFinanceStatusYingf entity);

    /**
     * 更新多个
     *
     * @param list List RepFinanceStatusYingf
     * @return int
     */
    int updatesAllById(@Param("list") List<RepFinanceStatusYingf> list);


}
