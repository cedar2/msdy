package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepFinanceStatusDaiyf;

/**
 * 财务状况-供应商-待预付Mapper接口
 *
 * @author chenkw
 * @date 2022-02-25
 */
public interface RepFinanceStatusDaiyfMapper extends BaseMapper<RepFinanceStatusDaiyf> {


    RepFinanceStatusDaiyf selectRepFinanceStatusDaiyfById(Long dataRecordSid);

    List<RepFinanceStatusDaiyf> selectRepFinanceStatusDaiyfList(RepFinanceStatusDaiyf repFinanceStatusDaiyf);

    /**
     * 从预付台账数据库表获取数据出来
     *
     * @param repFinanceStatusDaiyf
     * @return int
     */
    List<RepFinanceStatusDaiyf> getRepFinanceStatusDaiyfList(RepFinanceStatusDaiyf repFinanceStatusDaiyf);

    /**
     * 添加多个
     *
     * @param list List RepFinanceStatusDaiyf
     * @return int
     */
    int inserts(@Param("list") List<RepFinanceStatusDaiyf> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepFinanceStatusDaiyf
     * @return int
     */
    int updateAllById(RepFinanceStatusDaiyf entity);

    /**
     * 更新多个
     *
     * @param list List RepFinanceStatusDaiyf
     * @return int
     */
    int updatesAllById(@Param("list") List<RepFinanceStatusDaiyf> list);


}
