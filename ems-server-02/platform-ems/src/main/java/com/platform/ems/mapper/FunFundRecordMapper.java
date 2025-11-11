package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FunFundRecord;

/**
 * 资金流水Mapper接口
 *
 * @author chenkw
 * @date 2022-03-01
 */
public interface FunFundRecordMapper extends BaseMapper<FunFundRecord> {


    FunFundRecord selectFunFundRecordById(Long fundRecordSid);

    List<FunFundRecord> selectFunFundRecordList(FunFundRecord funFundRecord);

    /**
     * 添加多个
     *
     * @param list List FunFundRecord
     * @return int
     */
    int inserts(@Param("list") List<FunFundRecord> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FunFundRecord
     * @return int
     */
    int updateAllById(FunFundRecord entity);

    /**
     * 更新多个
     *
     * @param list List FunFundRecord
     * @return int
     */
    int updatesAllById(@Param("list") List<FunFundRecord> list);


}
