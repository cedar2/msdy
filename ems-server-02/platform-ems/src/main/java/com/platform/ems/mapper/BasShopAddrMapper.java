package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasShopAddr;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 店铺-联系方式信息Mapper接口
 *
 * @author c
 * @date 2022-03-31
 */
public interface BasShopAddrMapper extends BaseMapper<BasShopAddr> {


    BasShopAddr selectBasShopAddrById(Long shopContactSid);

    List<BasShopAddr> selectBasShopAddrList(BasShopAddr basShopAddr);

    /**
     * 添加多个
     *
     * @param list List BasShopAddr
     * @return int
     */
    int inserts(@Param("list") List<BasShopAddr> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasShopAddr
     * @return int
     */
    int updateAllById(BasShopAddr entity);

    /**
     * 更新多个
     *
     * @param list List BasShopAddr
     * @return int
     */
    int updatesAllById(@Param("list") List<BasShopAddr> list);


}
