package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConLocationType;

/**
 * 库位类型Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConLocationTypeMapper  extends BaseMapper<ConLocationType> {


    ConLocationType selectConLocationTypeById(Long sid);

    List<ConLocationType> selectConLocationTypeList(ConLocationType conLocationType);

    /**
     * 添加多个
     * @param list List ConLocationType
     * @return int
     */
    int inserts(@Param("list") List<ConLocationType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConLocationType
    * @return int
    */
    int updateAllById(ConLocationType entity);

    /**
     * 更新多个
     * @param list List ConLocationType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConLocationType> list);


}
