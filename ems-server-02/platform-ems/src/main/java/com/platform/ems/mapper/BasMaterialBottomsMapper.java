package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasMaterialBottoms;
import com.platform.ems.domain.dto.request.MaterialBottomsListRequest;
import com.platform.ems.domain.dto.response.MaterialBottomsListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品-上下装尺码对照Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-14
 */
public interface BasMaterialBottomsMapper extends BaseMapper<BasMaterialBottoms> {

    /**
     * 根据条件查询
     * @param request List BasMaterialBottoms
     * @return int
     */
    List<BasMaterialBottoms> selectBasMaterialBottomsList(BasMaterialBottoms request);

    /**
     * 添加多个
     * @param list List BasMaterialBottoms
     * @return int
     */
    int inserts(@Param("list") List<BasMaterialBottoms> list);


    /**
     * 更新多个
     * @param list List BasMaterialBottoms
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterialBottoms> list);

    int updateAllById(BasMaterialBottoms basMaterialBottoms);

}
