package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasPlantAddr;

/**
 * 工厂-联系方式信息Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-27
 */
public interface BasPlantAddrMapper  extends BaseMapper<BasPlantAddr> {


    BasPlantAddr selectBasPlantAddrById(Long plantContactSid);

    List<BasPlantAddr> selectBasPlantAddrList(BasPlantAddr basPlantAddr);

    /**
     * 添加多个
     * @param list List BasPlantAddr
     * @return int
     */
    int inserts(@Param("list") List<BasPlantAddr> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasPlantAddr
    * @return int
    */
    int updateAllById(BasPlantAddr entity);

    /**
     * 更新多个
     * @param list List BasPlantAddr
     * @return int
     */
    int updatesAllById(@Param("list") List<BasPlantAddr> list);


    void deleteBasPlantAddrByIds(@Param("array")String[] plantSids);
}
