package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBomType;

/**
 * BOM类型Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBomTypeMapper  extends BaseMapper<ConBomType> {


    ConBomType selectConBomTypeById(Long sid);

    List<ConBomType> selectConBomTypeList(ConBomType conBomType);

    /**
     * 添加多个
     * @param list List ConBomType
     * @return int
     */
    int inserts(@Param("list") List<ConBomType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBomType
    * @return int
    */
    int updateAllById(ConBomType entity);

    /**
     * 更新多个
     * @param list List ConBomType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBomType> list);


}
