package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.experimental.util.UniqueCheckUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.SysAuthorityField;
import com.platform.ems.mapper.SysAuthorityFieldMapper;
import com.platform.ems.service.ISysAuthorityFieldService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.platform.common.redis.thread.ApiThreadLocalUtil.getLoginUserClientId;
import static com.platform.common.redis.thread.ApiThreadLocalUtil.getLoginUserUserName;

/**
 * 权限字段Service业务层处理
 *
 * @author linxq
 * @date 2023-01-12
 */
@Service
@SuppressWarnings("all")
public class SysAuthorityFieldServiceImpl extends ServiceImpl<SysAuthorityFieldMapper, SysAuthorityField> implements ISysAuthorityFieldService {
    @Autowired
    private SysAuthorityFieldMapper sysAuthorityFieldMapper;

    private static final String TITLE = "权限字段";

    /**
     * 查询权限字段列表
     *
     * @param sysAuthorityField 权限字段
     * @return 权限字段
     */
    @Override
    public List<SysAuthorityField> selectSysAuthorityFieldList(SysAuthorityField sysAuthorityField) {
        return sysAuthorityFieldMapper.selectSysAuthorityFieldList(sysAuthorityField);
    }

    /**
     * 查询权限字段
     *
     * @param authorityFieldSid 权限字段ID
     * @return 权限字段
     */
    @Override
    public SysAuthorityField selectSysAuthorityFieldById(Long authorityFieldSid) {
        SysAuthorityField sysAuthorityField = sysAuthorityFieldMapper.selectSysAuthorityFieldById(authorityFieldSid);
        MongodbUtil.find(sysAuthorityField);
        return sysAuthorityField;
    }

    /**
     * 新增权限字段
     * 需要注意编码重复校验
     *
     * @param field 权限字段
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysAuthorityField(SysAuthorityField field) {
        checkAuthorityFieldExistForClient(field);
        setHandleStatusInfoWhenNew(field);

        int row = sysAuthorityFieldMapper.insert(field);
        if (row > 0) {
            // 插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new SysAuthorityField(), field);
            MongodbDeal.insert(field.getAuthorityFieldSid(),
                               field.getHandleStatus(),
                               msgList,
                               TITLE,
                               null);
        }
        return row;
    }

    private void checkAuthorityFieldExistForClient(SysAuthorityField field) {
        Wrapper<SysAuthorityField> queryWrapper =
                new LambdaQueryWrapper<SysAuthorityField>()
                        .eq((SysAuthorityField::getFieldName), field.getFieldName())
                        .in(SysAuthorityField::getClientId, Arrays.asList(
                                getLoginUserClientId(), "10000"
                        ));
        UniqueCheckUtil.checkUnique(field,
                                    () -> this.sysAuthorityFieldMapper.selectList(queryWrapper),
                                    SysAuthorityField::getAuthorityFieldSid,
                                    "权限字段名称已存在");
    }

    /**
     * 修改权限字段
     *
     * @param field 权限字段
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysAuthorityField(SysAuthorityField field) {
        checkAuthorityFieldExistForClient(field);
        setHandleStatusInfoWhenUpdate(field);

        SysAuthorityField original = sysAuthorityFieldMapper.selectSysAuthorityFieldById(field.getAuthorityFieldSid());
        int row = sysAuthorityFieldMapper.updateById(field);
        if (row > 0) {
            // 插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, field);
            MongodbDeal.update(field.getAuthorityFieldSid(),
                               original.getHandleStatus(),
                               field.getHandleStatus(),
                               msgList,
                               TITLE,
                               null);
        }
        return row;
    }

    /**
     * 变更权限字段
     *
     * @param field 权限字段
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysAuthorityField(SysAuthorityField field) {
        checkAuthorityFieldExistForClient(field);
        setHandleStatusInfoWhenUpdate(field);

        SysAuthorityField response = sysAuthorityFieldMapper.selectSysAuthorityFieldById(field.getAuthorityFieldSid());
        int row = sysAuthorityFieldMapper.updateAllById(field);
        if (row > 0) {
            // 插入日志
            MongodbUtil.insertUserLog(field.getAuthorityFieldSid(),
                                      BusinessType.CHANGE.getValue(),
                                      response,
                                      field,
                                      TITLE);
        }
        return row;
    }

    /**
     * 批量删除权限字段
     *
     * @param authorityFieldSidList 需要删除的权限字段ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysAuthorityFieldByIds(List<Long> authorityFieldSidList) {
        List<SysAuthorityField> list = sysAuthorityFieldMapper.selectList(new QueryWrapper<SysAuthorityField>()
                                                                                  .lambda().in(SysAuthorityField::getAuthorityFieldSid,
                                                                                               authorityFieldSidList));
        int row = sysAuthorityFieldMapper.deleteBatchIds(authorityFieldSidList);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new SysAuthorityField());
                MongodbUtil.insertUserLog(o.getAuthorityFieldSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param sysAuthorityField
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(SysAuthorityField sysAuthorityField) {
        int row = 0;
        Long[] sids = sysAuthorityField.getAuthorityFieldSidList();
        if (sids != null && sids.length > 0) {
            row = sysAuthorityFieldMapper.update(null,
                                                 new UpdateWrapper<SysAuthorityField>().lambda()
                                                                                       .set(SysAuthorityField::getStatus,
                                                                                            sysAuthorityField.getStatus())
                                                                                       .in(SysAuthorityField::getAuthorityFieldSid,
                                                                                           sids)
            );
            if (row == 0) {
                throw new CustomException("更改状态失败,请联系管理员");
            }
            for (Long id : sids) {
                // 插入日志
                MongodbDeal.status(id, sysAuthorityField.getStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param sysAuthorityField
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(SysAuthorityField sysAuthorityField) {
        int row = 0;
        Long[] sids = sysAuthorityField.getAuthorityFieldSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<SysAuthorityField> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(SysAuthorityField::getAuthorityFieldSid, sids);
            updateWrapper.set(SysAuthorityField::getHandleStatus, sysAuthorityField.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(sysAuthorityField.getHandleStatus())) {
                updateWrapper.set(SysAuthorityField::getConfirmDate, new Date());
                updateWrapper.set(SysAuthorityField::getConfirmerAccount, getLoginUserUserName());
            }
            row = sysAuthorityFieldMapper.update(null, updateWrapper);
            if (row > 0) {
                for (Long id : sids) {
                    // 插入日志
                    MongodbDeal.check(id, sysAuthorityField.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

}
