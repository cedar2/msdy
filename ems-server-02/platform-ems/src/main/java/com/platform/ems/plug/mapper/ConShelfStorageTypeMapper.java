package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConShelfStorageType;

/**
 * 货架存储类型Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConShelfStorageTypeMapper  extends BaseMapper<ConShelfStorageType> {


    ConShelfStorageType selectConShelfStorageTypeById(Long sid);

    List<ConShelfStorageType> selectConShelfStorageTypeList(ConShelfStorageType conShelfStorageType);

    /**
     * 添加多个
     * @param list List ConShelfStorageType
     * @return int
     */
    int inserts(@Param("list") List<ConShelfStorageType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConShelfStorageType
    * @return int
    */
    int updateAllById(ConShelfStorageType entity);

    /**
     * 更新多个
     * @param list List ConShelfStorageType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConShelfStorageType> list);


}
