package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.BasStorehouseLocation;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasStorehouse;

/**
 * 仓库档案Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-17
 */
public interface BasStorehouseMapper  extends BaseMapper<BasStorehouse> {


    BasStorehouse selectBasStorehouseById(Long storehouseSid);

    List<BasStorehouse> selectBasStorehouseList(BasStorehouse basStorehouse);

    /**
     * 添加多个
     * @param list List BasStorehouse
     * @return int
     */
    int inserts(@Param("list") List<BasStorehouse> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasStorehouse
    * @return int
    */
    int updateAllById(BasStorehouse entity);

    /**
     * 更新多个
     * @param list List BasStorehouse
     * @return int
     */
    int updatesAllById(@Param("list") List<BasStorehouse> list);

    /**
     * 验证仓库编码是否重复
     * @param plantCode 工厂编码
     * @return int
     */
    int checkCodeUnique(String plantCode);

    int checkNameUnique(String storehouseName);

    /**
     * 批量删除仓库档案
     *
     * @param storehouseSids 需要删除的仓库档案ID
     * @return 结果
     */
    int deleteBasStorehouseByIds(String[] storehouseSids);

    int countByDomain(BasStorehouse params);

    /**
     * 批量确认或启用/停用仓库档案
     *
     * @param basStorehouse 仓库档案IDS、确认状态、启用/停用状态
     * @return 结果
     */
    int confirm(BasStorehouse basStorehouse);

    /**
     * 仓库档案下拉框列表
     */
    List<BasStorehouse> getStorehouseList(BasStorehouse params);

    /**
     * 获取仓库下库位列表
     *
     * @param storehouseSid 仓库档案ID
     * @return 结果
     */
    List<BasStorehouseLocation> getStorehouseLocationListById(Long storehouseSid);

    /**
     * 获取仓库下库位列表
     *
     * @param params 仓库档案ID
     * @return 结果
     */
    List<BasStorehouseLocation> getLocationList(BasStorehouse params);
}
