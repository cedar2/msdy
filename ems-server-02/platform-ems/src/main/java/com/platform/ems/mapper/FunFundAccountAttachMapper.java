package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FunFundAccountAttach;

/**
 * 资金账户信息-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-03-01
 */
public interface FunFundAccountAttachMapper extends BaseMapper<FunFundAccountAttach> {

    FunFundAccountAttach selectFunFundAccountAttachById(Long fundAccountAttachSid);

    List<FunFundAccountAttach> selectFunFundAccountAttachList(FunFundAccountAttach funFundAccountAttach);

    /**
     * 添加多个
     *
     * @param list List FunFundAccountAttach
     * @return int
     */
    int inserts(@Param("list") List<FunFundAccountAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity FunFundAccountAttach
     * @return int
     */
    int updateAllById(FunFundAccountAttach entity);

    /**
     * 更新多个
     *
     * @param list List FunFundAccountAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<FunFundAccountAttach> list);


}
