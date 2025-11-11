package com.platform.flowable.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CheckedException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.flowable.domain.SysForm;
import com.platform.flowable.service.ISysDeployFormService;
import com.platform.system.domain.SysDeployForm;
import com.platform.system.domain.SysProcessTaskConfig;
import com.platform.system.mapper.SysDeployFormMapper;
import com.platform.system.mapper.SysProcessTaskConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 单与流程实例关联Service业务层处理
 *
 * @author qhq
 * @date 2021-09-03
 */
@Service
@SuppressWarnings("all")
public class SysDeployFormServiceImpl extends ServiceImpl<SysDeployFormMapper, SysDeployForm> implements ISysDeployFormService {
    @Autowired
    private SysDeployFormMapper sysDeployFormMapper;
    @Autowired
    private SysProcessTaskConfigMapper sysProcessTaskConfigMapper;


    private static final String TITLE = "单与流程实例关联";

    /**
     * 查询单与流程实例关联
     *
     * @param formProcessRelatSid 单与流程实例关联ID
     * @return 单与流程实例关联
     */
    @Override
    public SysDeployForm selectSysDeployFormById(Long formProcessRelatSid) {
        SysDeployForm sysDeployForm = sysDeployFormMapper.selectSysDeployFormById(formProcessRelatSid);
        return sysDeployForm;
    }

    /**
     * 查询单与流程实例关联列表
     *
     * @param sysDeployForm 单与流程实例关联
     * @return 单与流程实例关联
     */
    @Override
    public List<SysDeployForm> selectSysDeployFormList(SysDeployForm sysDeployForm) {
        return sysDeployFormMapper.selectSysDeployFormList(sysDeployForm);
    }

    /**
     * 新增单与流程实例关联
     * 需要注意编码重复校验
     *
     * @param sysDeployForm 单与流程实例关联
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysDeployForm(SysDeployForm sysDeployForm) {
        String clientId = sysDeployForm.getClientId();
        if (StrUtil.isEmpty(clientId)) {
            throw new CheckedException("保存单与流程实例关联失败：未获取到租户id");
        } else {
            /**
             * 无奈之举，如果找到了更好的绕开租户拦截器的方式，再说
             */
            LoginUser loginUser = new LoginUser();
            SysUser sysUser = new SysUser();
            sysUser.setClientId(clientId);
            loginUser.setSysUser(sysUser);
            ApiThreadLocalUtil.set(loginUser);
        }

        try {
            sysDeployFormMapper.delete(
                    new QueryWrapper<SysDeployForm>()
                            .lambda()
                            .eq(SysDeployForm::getKey, sysDeployForm.getKey())
                            .eq(SysDeployForm::getClientId, clientId)
            );
            int row = sysDeployFormMapper.insert(sysDeployForm);
            if (row <= 0) {
                return row;
            }

            List<SysProcessTaskConfig> taskConfigList = sysDeployForm.getTaskConfigList();
            if (CollectionUtil.isNotEmpty(taskConfigList)) {
                sysProcessTaskConfigMapper.delete(
                        new QueryWrapper<SysProcessTaskConfig>()
                                .lambda()
                                .eq(SysProcessTaskConfig::getProcessKey,
                                        sysDeployForm.getKey())
                                .eq(SysProcessTaskConfig::getClientId,
                                        clientId)
                );
                sysProcessTaskConfigMapper.inserts(taskConfigList);
            }
            return row;
        } finally {
            ApiThreadLocalUtil.unset();
        }
    }

    /**
     * 修改单与流程实例关联
     *
     * @param sysDeployForm 单与流程实例关联
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysDeployForm(SysDeployForm sysDeployForm) {
        SysDeployForm response = sysDeployFormMapper.selectSysDeployFormById(sysDeployForm.getFormProcessRelatSid());
        int row = sysDeployFormMapper.updateById(sysDeployForm);
        return row;
    }

    /**
     * 变更单与流程实例关联
     *
     * @param sysDeployForm 单与流程实例关联
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysDeployForm(SysDeployForm sysDeployForm) {
        SysDeployForm response = sysDeployFormMapper.selectSysDeployFormById(sysDeployForm.getFormProcessRelatSid());
        int row = sysDeployFormMapper.updateAllById(sysDeployForm);
        return row;
    }
}
