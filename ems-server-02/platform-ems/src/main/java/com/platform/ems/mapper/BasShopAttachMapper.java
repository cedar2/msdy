package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasShopAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 店铺-附件Mapper接口
 *
 * @author c
 * @date 2022-03-31
 */
public interface BasShopAttachMapper extends BaseMapper<BasShopAttach> {


    BasShopAttach selectBasShopAttachById(Long attachmentSid);

    List<BasShopAttach> selectBasShopAttachList(BasShopAttach basShopAttach);

    /**
     * 添加多个
     *
     * @param list List BasShopAttach
     * @return int
     */
    int inserts(@Param("list") List<BasShopAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasShopAttach
     * @return int
     */
    int updateAllById(BasShopAttach entity);

    /**
     * 更新多个
     *
     * @param list List BasShopAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<BasShopAttach> list);


}
