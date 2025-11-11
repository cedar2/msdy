package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConStorageUnitType;

/**
 * 托盘类型Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConStorageUnitTypeMapper  extends BaseMapper<ConStorageUnitType> {


    ConStorageUnitType selectConStorageUnitTypeById(Long sid);

    List<ConStorageUnitType> selectConStorageUnitTypeList(ConStorageUnitType conStorageUnitType);

    /**
     * 添加多个
     * @param list List ConStorageUnitType
     * @return int
     */
    int inserts(@Param("list") List<ConStorageUnitType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConStorageUnitType
    * @return int
    */
    int updateAllById(ConStorageUnitType entity);

    /**
     * 更新多个
     * @param list List ConStorageUnitType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConStorageUnitType> list);


}
