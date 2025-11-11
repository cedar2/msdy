package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SysClientMovementType;

/**
 * 作业类型_租户级Service接口
 *
 * @author chenkw
 * @date 2022-06-17
 */
public interface ISysClientMovementTypeService extends IService<SysClientMovementType> {
    /**
     * 查询作业类型_租户级
     *
     * @param clientMovementTypeSid 作业类型_租户级ID
     * @return 作业类型_租户级
     */
    public SysClientMovementType selectSysClientMovementTypeById(Long clientMovementTypeSid);

    /**
     * 查询作业类型_租户级列表
     *
     * @param sysClientMovementType 作业类型_租户级
     * @return 作业类型_租户级集合
     */
    public List<SysClientMovementType> selectSysClientMovementTypeList(SysClientMovementType sysClientMovementType);

    /**
     * 新增作业类型_租户级
     *
     * @param sysClientMovementType 作业类型_租户级
     * @return 结果
     */
    public int insertSysClientMovementType(SysClientMovementType sysClientMovementType);

    /**
     * 修改作业类型_租户级
     *
     * @param sysClientMovementType 作业类型_租户级
     * @return 结果
     */
    public int updateSysClientMovementType(SysClientMovementType sysClientMovementType);

    /**
     * 变更作业类型_租户级
     *
     * @param sysClientMovementType 作业类型_租户级
     * @return 结果
     */
    public int changeSysClientMovementType(SysClientMovementType sysClientMovementType);

    /**
     * 批量删除作业类型_租户级
     *
     * @param clientMovementTypeSids 需要删除的作业类型_租户级ID
     * @return 结果
     */
    public int deleteSysClientMovementTypeByIds(List<Long> clientMovementTypeSids);

    /**
     * 更改确认状态
     *
     * @param sysClientMovementType
     * @return
     */
    int check(SysClientMovementType sysClientMovementType);

}
