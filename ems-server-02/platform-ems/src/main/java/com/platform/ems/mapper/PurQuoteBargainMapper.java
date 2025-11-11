package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.PurQuoteBargain;
import org.apache.ibatis.annotations.Param;

/**
 * 报议价单主(报价/核价/议价)Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-26
 */
public interface PurQuoteBargainMapper extends BaseMapper<PurQuoteBargain> {


    PurQuoteBargain selectPurRequestQuotationById(Long requestQuotationSid);

    List<PurQuoteBargain> selectPurRequestQuotationList(PurQuoteBargain purQuoteBargain);

    /**
     * 添加多个
     *
     * @param list List PurQuoteBargain
     * @return int
     */
    int inserts(@Param("list") List<PurQuoteBargain> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PurQuoteBargain
     * @return int
     */
    int updateAllById(PurQuoteBargain entity);

    /**
     * 更新多个
     *
     * @param list List PurQuoteBargain
     * @return int
     */
    int updatesAllById(@Param("list") List<PurQuoteBargain> list);

}
