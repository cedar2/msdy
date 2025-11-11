package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.SysFormProcess;

/**
 * 单据关联流程实例Mapper接口
 * 
 * @author qhq
 * @date 2021-09-06
 */
public interface SysFormProcessMapper  extends BaseMapper<SysFormProcess> {


    SysFormProcess selectSysFormProcessById(Long id);

    List<SysFormProcess> selectSysFormProcessList(SysFormProcess sysFormProcess);

    int insertSysFormProcess(SysFormProcess formProcess);
    
    /**
     * 添加多个
     * @param list List SysFormProcess
     * @return int
     */
    int inserts(@Param("list") List<SysFormProcess> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SysFormProcess
    * @return int
    */
    int updateAllById(SysFormProcess entity);

    /**
     * 更新多个
     * @param list List SysFormProcess
     * @return int
     */
    int updatesAllById(@Param("list") List<SysFormProcess> list);


}
