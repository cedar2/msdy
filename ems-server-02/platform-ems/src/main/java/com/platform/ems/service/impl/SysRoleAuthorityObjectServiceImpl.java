package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.SysRoleAuthorityObjectMapper;
import com.platform.ems.domain.SysRoleAuthorityObject;
import com.platform.ems.service.ISysRoleAuthorityObjectService;

/**
 * 角色信息-权限对象Service业务层处理
 * 
 * @author chenkw
 * @date 2021-12-28
 */
@Service
@SuppressWarnings("all")
public class SysRoleAuthorityObjectServiceImpl extends ServiceImpl<SysRoleAuthorityObjectMapper,SysRoleAuthorityObject>  implements ISysRoleAuthorityObjectService {
    @Autowired
    private SysRoleAuthorityObjectMapper sysRoleAuthorityObjectMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "角色信息-权限对象";
    /**
     * 查询角色信息-权限对象
     * 
     * @param roleAuthorityObjectSid 角色信息-权限对象ID
     * @return 角色信息-权限对象
     */
    @Override
    public SysRoleAuthorityObject selectSysRoleAuthorityObjectById(Long roleAuthorityObjectSid) {
        SysRoleAuthorityObject sysRoleAuthorityObject = sysRoleAuthorityObjectMapper.selectSysRoleAuthorityObjectById(roleAuthorityObjectSid);
        return  sysRoleAuthorityObject;
    }

    /**
     * 查询角色信息-权限对象列表
     * 
     * @param sysRoleAuthorityObject 角色信息-权限对象
     * @return 角色信息-权限对象
     */
    @Override
    public List<SysRoleAuthorityObject> selectSysRoleAuthorityObjectList(SysRoleAuthorityObject sysRoleAuthorityObject) {
        return sysRoleAuthorityObjectMapper.selectSysRoleAuthorityObjectList(sysRoleAuthorityObject);
    }

    /**
     * 新增角色信息-权限对象
     * 需要注意编码重复校验
     * @param sysRoleAuthorityObject 角色信息-权限对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysRoleAuthorityObject(SysRoleAuthorityObject sysRoleAuthorityObject) {
        int row= sysRoleAuthorityObjectMapper.insert(sysRoleAuthorityObject);
        return row;
    }

    /**
     * 修改角色信息-权限对象
     * 
     * @param sysRoleAuthorityObject 角色信息-权限对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysRoleAuthorityObject(SysRoleAuthorityObject sysRoleAuthorityObject) {
        SysRoleAuthorityObject response = sysRoleAuthorityObjectMapper.selectSysRoleAuthorityObjectById(sysRoleAuthorityObject.getRoleAuthorityObjectSid());
        int row=sysRoleAuthorityObjectMapper.updateById(sysRoleAuthorityObject);
        return row;
    }

    /**
     * 变更角色信息-权限对象
     *
     * @param sysRoleAuthorityObject 角色信息-权限对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysRoleAuthorityObject(SysRoleAuthorityObject sysRoleAuthorityObject) {
        SysRoleAuthorityObject response = sysRoleAuthorityObjectMapper.selectSysRoleAuthorityObjectById(sysRoleAuthorityObject.getRoleAuthorityObjectSid());
        int row=sysRoleAuthorityObjectMapper.updateAllById(sysRoleAuthorityObject);
        return row;
    }

    /**
     * 批量删除角色信息-权限对象
     * 
     * @param roleAuthorityObjectSids 需要删除的角色信息-权限对象ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysRoleAuthorityObjectByIds(List<Long> roleAuthorityObjectSids) {
        return sysRoleAuthorityObjectMapper.deleteBatchIds(roleAuthorityObjectSids);
    }

}
