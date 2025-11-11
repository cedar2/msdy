package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDataObjectFileType;

/**
 * 数据对象&附件类型对照Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConDataObjectFileTypeMapper  extends BaseMapper<ConDataObjectFileType> {


    ConDataObjectFileType selectConDataObjectFileTypeById(Long sid);

    List<ConDataObjectFileType> selectConDataObjectFileTypeList(ConDataObjectFileType conDataObjectFileType);

    /**
     * 添加多个
     * @param list List ConDataObjectFileType
     * @return int
     */
    int inserts(@Param("list") List<ConDataObjectFileType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDataObjectFileType
    * @return int
    */
    int updateAllById(ConDataObjectFileType entity);

    /**
     * 更新多个
     * @param list List ConDataObjectFileType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDataObjectFileType> list);


}
