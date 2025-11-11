package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConStorageUnitSizeType;

/**
 * 托盘规格类型Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConStorageUnitSizeTypeMapper  extends BaseMapper<ConStorageUnitSizeType> {


    ConStorageUnitSizeType selectConStorageUnitSizeTypeById(Long sid);

    List<ConStorageUnitSizeType> selectConStorageUnitSizeTypeList(ConStorageUnitSizeType conStorageUnitSizeType);

    /**
     * 添加多个
     * @param list List ConStorageUnitSizeType
     * @return int
     */
    int inserts(@Param("list") List<ConStorageUnitSizeType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConStorageUnitSizeType
    * @return int
    */
    int updateAllById(ConStorageUnitSizeType entity);

    /**
     * 更新多个
     * @param list List ConStorageUnitSizeType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConStorageUnitSizeType> list);


}
