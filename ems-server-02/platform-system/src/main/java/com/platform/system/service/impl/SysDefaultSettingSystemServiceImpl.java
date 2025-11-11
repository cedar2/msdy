package com.platform.system.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysDefaultSettingSystem;
import com.platform.common.exception.base.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.system.mapper.SysDefaultSettingSystemMapper;
import com.platform.system.service.ISysDefaultSettingSystemService;

/**
 * 系统默认设置_系统级Service业务层处理
 *
 * @author chenkw
 * @date 2022-04-22
 */
@Service
@SuppressWarnings("all")
public class SysDefaultSettingSystemServiceImpl extends ServiceImpl<SysDefaultSettingSystemMapper, SysDefaultSettingSystem> implements ISysDefaultSettingSystemService {
    @Autowired
    private SysDefaultSettingSystemMapper sysDefaultSettingSystemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "系统默认设置_系统级";

    /**
     * 查询系统默认设置_系统级
     *
     * @param clientId 系统默认设置_系统级ID
     * @return 系统默认设置_系统级
     */
    @Override
    public SysDefaultSettingSystem selectSysDefaultSettingSystemById(String clientId) {
        SysDefaultSettingSystem sysDefaultSettingSystem = null;
        try {
            sysDefaultSettingSystem = sysDefaultSettingSystemMapper.selectSysDefaultSettingSystemById(clientId);
        }catch (Exception e){
            List<SysDefaultSettingSystem> sysDefaultSettingSystemList = sysDefaultSettingSystemMapper.selectList(new QueryWrapper<SysDefaultSettingSystem>().lambda()
                    .eq(SysDefaultSettingSystem::getClientId,"10000"));
            if (CollectionUtil.isNotEmpty(sysDefaultSettingSystemList)){
                sysDefaultSettingSystemList = sysDefaultSettingSystemList.stream().sorted(new Comparator<SysDefaultSettingSystem>() {
                    @Override
                    public int compare(SysDefaultSettingSystem o1, SysDefaultSettingSystem o2) {
                        try {
                            //Date d1 = DateUtil.convertStringToDate(o1.getCreateDate(), "yyyy-MM-dd HH:mm:ss");
                            //Date d2 = DateUtil.convertStringToDate(o2.getCreateDate(), "yyyy-MM-dd HH:mm:ss");
                            //正序
                            //return d1.compareTo(d2);
                            //倒序
                            return o2.getCreateDate().compareTo(o1.getCreateDate());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                }).collect(Collectors.toList());
                return sysDefaultSettingSystemList.get(0);
            }else {
                throw new BaseException(e.getMessage());
            }
        }
        return sysDefaultSettingSystem;
    }

    /**
     * 查询系统默认设置_系统级列表
     *
     * @param sysDefaultSettingSystem 系统默认设置_系统级
     * @return 系统默认设置_系统级
     */
    @Override
    public List<SysDefaultSettingSystem> selectSysDefaultSettingSystemList(SysDefaultSettingSystem sysDefaultSettingSystem) {
        return sysDefaultSettingSystemMapper.selectSysDefaultSettingSystemList(sysDefaultSettingSystem);
    }

    /**
     * 新增系统默认设置_系统级
     * 需要注意编码重复校验
     *
     * @param sysDefaultSettingSystem 系统默认设置_系统级
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysDefaultSettingSystem(SysDefaultSettingSystem sysDefaultSettingSystem) {
        int row = 0;
        if ("DLBS".equals(sysDefaultSettingSystem.getDeploymentMode()) && sysDefaultSettingSystem.getSysClientId() == null){
            throw new BaseException("独立部署时，系统级对应的租户ID不能为空！");
        }
        SysDefaultSettingSystem response = null;
        try {
            response = sysDefaultSettingSystemMapper.selectOne(new QueryWrapper<SysDefaultSettingSystem>()
                    .lambda().eq(SysDefaultSettingSystem::getClientId,sysDefaultSettingSystem.getClientId()));

        }catch (Exception e){
            throw new BaseException("该系统存在重复配置数据，请联系管理员处理！");
        }
        if (response != null){
            throw new BaseException("该系统配置已存在，请联系管理员处理！");
        }else {
            row = sysDefaultSettingSystemMapper.insert(sysDefaultSettingSystem);
        }
        return row;
    }

    /**
     * 修改系统默认设置_系统级
     *
     * @param sysDefaultSettingSystem 系统默认设置_系统级
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysDefaultSettingSystem(SysDefaultSettingSystem sysDefaultSettingSystem) {
        if ("DLBS".equals(sysDefaultSettingSystem.getDeploymentMode()) && sysDefaultSettingSystem.getSysClientId() == null){
            throw new BaseException("独立部署时，系统级对应的租户ID不能为空！");
        }
        SysDefaultSettingSystem response = sysDefaultSettingSystemMapper.selectSysDefaultSettingSystemById(sysDefaultSettingSystem.getClientId());
        int row = sysDefaultSettingSystemMapper.updateAllById(sysDefaultSettingSystem);
        return row;
    }

    /**
     * 变更系统默认设置_系统级
     *
     * @param sysDefaultSettingSystem 系统默认设置_系统级
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysDefaultSettingSystem(SysDefaultSettingSystem sysDefaultSettingSystem) {
        if ("DLBS".equals(sysDefaultSettingSystem.getDeploymentMode()) && sysDefaultSettingSystem.getSysClientId() == null){
            throw new BaseException("独立部署时，系统级对应的租户ID不能为空！");
        }
        SysDefaultSettingSystem response = sysDefaultSettingSystemMapper.selectSysDefaultSettingSystemById(sysDefaultSettingSystem.getClientId());
        int row = sysDefaultSettingSystemMapper.updateAllById(sysDefaultSettingSystem);
        return row;
    }

    /**
     * 批量删除系统默认设置_系统级
     *
     * @param clientIds 需要删除的系统默认设置_系统级ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysDefaultSettingSystemByIds(List<String> clientIds) {
        return sysDefaultSettingSystemMapper.deleteBatchIds(clientIds);
    }


    /**
     * 更改确认状态
     *
     * @param sysDefaultSettingSystem
     * @return
     */
    @Override
    public int check(SysDefaultSettingSystem sysDefaultSettingSystem) {
        int row = 0;
        String[] sids = sysDefaultSettingSystem.getClientIdList();
        if (sids != null && sids.length > 0) {
            row = sysDefaultSettingSystemMapper.update(null, new UpdateWrapper<SysDefaultSettingSystem>()
                    .lambda().set(SysDefaultSettingSystem::getHandleStatus, sysDefaultSettingSystem.getHandleStatus())
                    .in(SysDefaultSettingSystem::getClientId, sids));
        }
        return row;
    }


}
