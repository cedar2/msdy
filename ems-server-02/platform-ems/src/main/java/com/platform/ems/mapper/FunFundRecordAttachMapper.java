package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FunFundRecordAttach;

/**
 * 资金流水-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-03-01
 */
public interface FunFundRecordAttachMapper extends BaseMapper<FunFundRecordAttach> {


    FunFundRecordAttach selectFunFundRecordAttachById(Long fundRecordAttachSid);

    List<FunFundRecordAttach> selectFunFundRecordAttachList(FunFundRecordAttach funFundRecordAttach);

    /**
     * 添加多个
     *
     * @param list List FunFundRecordAttach
     * @return int
     */
    int inserts(@Param("list") List<FunFundRecordAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FunFundRecordAttach
     * @return int
     */
    int updateAllById(FunFundRecordAttach entity);

    /**
     * 更新多个
     *
     * @param list List FunFundRecordAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FunFundRecordAttach> list);


}
