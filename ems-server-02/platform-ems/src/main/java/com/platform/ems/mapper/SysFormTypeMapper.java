package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.SysFormType;

/**
 * 系统单据定义Mapper接口
 * 
 * @author qhq
 * @date 2021-09-06
 */
public interface SysFormTypeMapper  extends BaseMapper<SysFormType> {


    SysFormType selectSysFormTypeById(Long id);

    List<SysFormType> selectSysFormTypeList(SysFormType sysFormType);

    /**
     * 添加多个
     * @param list List SysFormType
     * @return int
     */
    int inserts(@Param("list") List<SysFormType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SysFormType
    * @return int
    */
    int updateAllById(SysFormType entity);

    /**
     * 更新多个
     * @param list List SysFormType
     * @return int
     */
    int updatesAllById(@Param("list") List<SysFormType> list);


}
