package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.SysRoleAuthorityFieldValueMapper;
import com.platform.ems.domain.SysRoleAuthorityFieldValue;
import com.platform.ems.service.ISysRoleAuthorityFieldValueService;

/**
 * 角色信息-权限字段值Service业务层处理
 * 
 * @author chenkw
 * @date 2021-12-28
 */
@Service
@SuppressWarnings("all")
public class SysRoleAuthorityFieldValueServiceImpl extends ServiceImpl<SysRoleAuthorityFieldValueMapper,SysRoleAuthorityFieldValue>  implements ISysRoleAuthorityFieldValueService {
    @Autowired
    private SysRoleAuthorityFieldValueMapper sysRoleAuthorityFieldValueMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "角色信息-权限字段值";
    /**
     * 查询角色信息-权限字段值
     * 
     * @param roleAuthorityFieldValueSid 角色信息-权限字段值ID
     * @return 角色信息-权限字段值
     */
    @Override
    public SysRoleAuthorityFieldValue selectSysRoleAuthorityFieldValueById(Long roleAuthorityFieldValueSid) {
        SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue = sysRoleAuthorityFieldValueMapper.selectSysRoleAuthorityFieldValueById(roleAuthorityFieldValueSid);
        return  sysRoleAuthorityFieldValue;
    }

    /**
     * 查询角色信息-权限字段值列表
     * 
     * @param sysRoleAuthorityFieldValue 角色信息-权限字段值
     * @return 角色信息-权限字段值
     */
    @Override
    public List<SysRoleAuthorityFieldValue> selectSysRoleAuthorityFieldValueList(SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue) {
        return sysRoleAuthorityFieldValueMapper.selectSysRoleAuthorityFieldValueList(sysRoleAuthorityFieldValue);
    }

    /**
     * 查询角色信息-权限字段值列表（有关联其他表的比较完整查询）
     *
     * @param sysRoleAuthorityFieldValue 角色信息-权限字段值
     * @return 角色信息-权限字段值
     */
    @Override
    public List<SysRoleAuthorityFieldValue> selectMoreRoleAuthorityFieldValueList(SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue) {
        return sysRoleAuthorityFieldValueMapper.selectMoreRoleAuthorityFieldValueList(sysRoleAuthorityFieldValue);
    }

    /**
     * 新增角色信息-权限字段值
     * 需要注意编码重复校验
     * @param sysRoleAuthorityFieldValue 角色信息-权限字段值
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysRoleAuthorityFieldValue(SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue) {
        int row= sysRoleAuthorityFieldValueMapper.insert(sysRoleAuthorityFieldValue);
        return row;
    }

    /**
     * 修改角色信息-权限字段值
     * 
     * @param sysRoleAuthorityFieldValue 角色信息-权限字段值
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysRoleAuthorityFieldValue(SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue) {
        SysRoleAuthorityFieldValue response = sysRoleAuthorityFieldValueMapper.selectSysRoleAuthorityFieldValueById(sysRoleAuthorityFieldValue.getRoleAuthorityFieldValueSid());
        int row=sysRoleAuthorityFieldValueMapper.updateById(sysRoleAuthorityFieldValue);
        return row;
    }

    /**
     * 变更角色信息-权限字段值
     *
     * @param sysRoleAuthorityFieldValue 角色信息-权限字段值
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysRoleAuthorityFieldValue(SysRoleAuthorityFieldValue sysRoleAuthorityFieldValue) {
        SysRoleAuthorityFieldValue response = sysRoleAuthorityFieldValueMapper.selectSysRoleAuthorityFieldValueById(sysRoleAuthorityFieldValue.getRoleAuthorityFieldValueSid());
        int row=sysRoleAuthorityFieldValueMapper.updateAllById(sysRoleAuthorityFieldValue);
        return row;
    }

    /**
     * 批量删除角色信息-权限字段值
     * 
     * @param roleAuthorityFieldValueSids 需要删除的角色信息-权限字段值ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysRoleAuthorityFieldValueByIds(List<Long> roleAuthorityFieldValueSids) {
        return sysRoleAuthorityFieldValueMapper.deleteBatchIds(roleAuthorityFieldValueSids);
    }
}
