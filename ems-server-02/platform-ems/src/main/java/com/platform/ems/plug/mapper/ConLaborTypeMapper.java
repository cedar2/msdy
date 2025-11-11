package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConLaborType;
import org.apache.ibatis.annotations.Select;

/**
 * 工价类型Mapper接口
 *
 * @author chenkw
 * @date 2021-06-10
 */
public interface ConLaborTypeMapper  extends BaseMapper<ConLaborType> {


    ConLaborType selectConLaborTypeById(Long laborTypeSid);

    List<ConLaborType> selectConLaborTypeList(ConLaborType conLaborType);

    /**
     * 添加多个
     * @param list List ConLaborType
     * @return int
     */
    int inserts(@Param("list") List<ConLaborType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConLaborType
    * @return int
    */
    int updateAllById(ConLaborType entity);

    /**
     * 更新多个
     * @param list List ConLaborType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConLaborType> list);

    /**
     * 下拉框列表
     */
    List<ConLaborType> getConLaborTypeList();

    @Select("select labor_type_code from s_con_labor_type where labor_type_sid = ${laborTypeSid}")
    String selectConLaborTypeCodeBySid(@Param("laborTypeSid") Long laborTypeSid);

}
