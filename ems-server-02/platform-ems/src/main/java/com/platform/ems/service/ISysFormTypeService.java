package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SysFormType;

/**
 * 系统单据定义Service接口
 * 
 * @author qhq
 * @date 2021-09-06
 */
public interface ISysFormTypeService extends IService<SysFormType>{
    /**
     * 查询系统单据定义
     * 
     * @param id 系统单据定义ID
     * @return 系统单据定义
     */
    public SysFormType selectSysFormTypeById(Long id);

    /**
     * 查询系统单据定义列表
     * 
     * @param sysFormType 系统单据定义
     * @return 系统单据定义集合
     */
    public List<SysFormType> selectSysFormTypeList(SysFormType sysFormType);

    /**
     * 新增系统单据定义
     * 
     * @param sysFormType 系统单据定义
     * @return 结果
     */
    public int insertSysFormType(SysFormType sysFormType);

    /**
     * 修改系统单据定义
     * 
     * @param sysFormType 系统单据定义
     * @return 结果
     */
    public int updateSysFormType(SysFormType sysFormType);

    /**
     * 变更系统单据定义
     *
     * @param sysFormType 系统单据定义
     * @return 结果
     */
    public int changeSysFormType(SysFormType sysFormType);

    /**
     * 批量删除系统单据定义
     * 
     * @param ids 需要删除的系统单据定义ID
     * @return 结果
     */
    public int deleteSysFormTypeByIds(List<Long>  ids);

}
