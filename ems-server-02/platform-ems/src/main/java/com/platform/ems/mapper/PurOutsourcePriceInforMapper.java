package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurOutsourcePriceInfor;

/**
 * 加工采购价格记录主(报价/核价/议价)Mapper接口
 *
 * @author linhongwei
 * @date 2022-04-01
 */
public interface PurOutsourcePriceInforMapper extends BaseMapper<PurOutsourcePriceInfor> {


    PurOutsourcePriceInfor selectPurOutsourcePriceInforById(Long outsourcePriceInforSid);

    List<PurOutsourcePriceInfor> selectPurOutsourcePriceInforList(PurOutsourcePriceInfor purOutsourcePriceInfor);

    /**
     * 添加多个
     *
     * @param list List PurOutsourcePriceInfor
     * @return int
     */
    int inserts(@Param("list") List<PurOutsourcePriceInfor> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PurOutsourcePriceInfor
     * @return int
     */
    int updateAllById(PurOutsourcePriceInfor entity);

    /**
     * 更新多个
     *
     * @param list List PurOutsourcePriceInfor
     * @return int
     */
    int updatesAllById(@Param("list") List<PurOutsourcePriceInfor> list);


}
