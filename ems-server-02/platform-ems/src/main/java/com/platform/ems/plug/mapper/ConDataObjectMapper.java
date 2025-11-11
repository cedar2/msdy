package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDataObject;

/**
 * 数据对象Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConDataObjectMapper  extends BaseMapper<ConDataObject> {


    ConDataObject selectConDataObjectById(Long sid);

    List<ConDataObject> selectConDataObjectList(ConDataObject conDataObject);

    /**
     * 添加多个
     * @param list List ConDataObject
     * @return int
     */
    int inserts(@Param("list") List<ConDataObject> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDataObject
    * @return int
    */
    int updateAllById(ConDataObject entity);

    /**
     * 更新多个
     * @param list List ConDataObject
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDataObject> list);


}
