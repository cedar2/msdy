package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasStorehouseLocation;

/**
 * 仓库-库位信息Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-17
 */
public interface BasStorehouseLocationMapper  extends BaseMapper<BasStorehouseLocation> {


    BasStorehouseLocation selectBasStorehouseLocationById(String clientId);

    List<BasStorehouseLocation> selectBasStorehouseLocationList(BasStorehouseLocation basStorehouseLocation);

    /**
     * 添加多个
     * @param list List BasStorehouseLocation
     * @return int
     */
    int inserts(@Param("list") List<BasStorehouseLocation> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasStorehouseLocation
    * @return int
    */
    int updateAllById(BasStorehouseLocation entity);

    /**
     * 更新多个
     * @param list List BasStorehouseLocation
     * @return int
     */
    int updatesAllById(@Param("list") List<BasStorehouseLocation> list);


    int deleteBasStorehouseLocationByIds(@Param("array")String[] storehouseSids);
}
