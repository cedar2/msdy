package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysUserDataRole;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.SysDataRoleAuthorityFieldValueMapper;
import com.platform.ems.mapper.SysDataRoleAuthorityObjectMapper;
import com.platform.system.mapper.SysUserDataRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.SysRoleDataMapper;
import com.platform.ems.service.ISysRoleDataService;

/**
 * 数据角色Service业务层处理
 *
 * @author chenkw
 * @date 2023-05-16
 */
@Service
@SuppressWarnings("all")
public class SysRoleDataServiceImpl extends ServiceImpl<SysRoleDataMapper, SysRoleData> implements ISysRoleDataService {
    @Autowired
    private SysRoleDataMapper sysRoleDataMapper;
    @Autowired
    private SysDataRoleAuthorityObjectMapper authorityObjectMapper;
    @Autowired
    private SysDataRoleAuthorityFieldValueMapper authorityFieldValueMapper;
    @Autowired
    private SysUserDataRoleMapper userDataRoleMapper;

    private static final String TITLE = "数据角色";

    /**
     * 查询数据角色
     *
     * @param roleDataSid 数据角色ID
     * @return 数据角色
     */
    @Override
    public SysRoleData selectSysRoleDataById(Long roleDataSid) {
        SysRoleData sysRoleData = sysRoleDataMapper.selectSysRoleDataById(roleDataSid);
        if (sysRoleData != null) {
            // 权限对象
            List<SysDataRoleAuthorityObject> objectList = authorityObjectMapper.selectSysDataRoleAuthorityObjectList(
                    new SysDataRoleAuthorityObject().setRoleDataSid(roleDataSid));
            if (CollectionUtil.isNotEmpty(objectList)) {
                Long[] objectSidS = objectList.stream().map(SysDataRoleAuthorityObject::getDataRoleAuthorityObjectSid).toArray(Long[]::new);
                // 权限对象字段值
                List<SysDataRoleAuthorityFieldValue> fieldValueList = authorityFieldValueMapper.selectSysDataRoleAuthorityFieldValueList(
                        new SysDataRoleAuthorityFieldValue().setDataRoleAuthorityObjectSidList(objectSidS));
                if (CollectionUtil.isNotEmpty(fieldValueList)) {
                    Map<Long, List<SysDataRoleAuthorityFieldValue>> fieldMap = fieldValueList.stream()
                            .collect(Collectors.groupingBy(e -> e.getDataRoleAuthorityObjectSid()));
                    objectList.forEach(item->{
                        if (fieldMap.containsKey(item.getDataRoleAuthorityObjectSid())) {
                            List<SysDataRoleAuthorityFieldValue> detailList = fieldMap.get(item.getDataRoleAuthorityObjectSid());
                            // 组合的字符串
                            String fields = "";
                            for (SysDataRoleAuthorityFieldValue detail : detailList) {
                                String fieldName = detail.getFieldName() == null ? "" : detail.getFieldName();
                                String fieldValue = detail.getAuthorityFieldValue() == null ? "" : detail.getAuthorityFieldValue();
                                fields = fields + fieldName + "-" + fieldValue + "；";
                            }
                            if (StrUtil.isNotBlank(fields)) {
                                fields = fields.substring(0, fields.length() - 1);
                            }
                            item.setFieldValueString(fields);
                            item.setFieldValueList(detailList);
                        }
                    });
                }
            }
            sysRoleData.setObjectList(objectList);
            // 用户列表
            List<SysUserDataRole> userDataRoleList = userDataRoleMapper.selectSysUserDataRoleList(new SysUserDataRole()
                    .setRoleDataSid(roleDataSid));
            sysRoleData.setUserDataRoleList(userDataRoleList);
            // 操作日志
            MongodbUtil.find(sysRoleData);
        }
        return sysRoleData;
    }

    /**
     * 查询数据角色列表
     *
     * @param sysRoleData 数据角色
     * @return 数据角色
     */
    @Override
    public List<SysRoleData> selectSysRoleDataList(SysRoleData sysRoleData) {
        return sysRoleDataMapper.selectSysRoleDataList(sysRoleData);
    }

    /**
     * 新增数据角色 校验编码
     *
     * @param sysRoleData 数据角色
     * @return 结果
     */
    private void judgeCode(SysRoleData sysRoleData) {
        QueryWrapper<SysRoleData> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRoleData::getRoleDataCode, sysRoleData.getRoleDataCode());
        if (sysRoleData.getRoleDataSid() != null) {
            queryWrapper.lambda().ne(SysRoleData::getRoleDataSid, sysRoleData.getRoleDataSid());
        }
        List<SysRoleData> nameList = sysRoleDataMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException("数据角色编码已存在");
        }
    }

    /**
     * 数据角色 校验名称
     *
     * @param sysRoleData 数据角色
     * @return 结果
     */
    private void judgeName(SysRoleData sysRoleData) {
        QueryWrapper<SysRoleData> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRoleData::getRoleDataName, sysRoleData.getRoleDataName());
        if (sysRoleData.getRoleDataSid() != null) {
            queryWrapper.lambda().ne(SysRoleData::getRoleDataSid, sysRoleData.getRoleDataSid());
        }
        List<SysRoleData> nameList = sysRoleDataMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException("数据角色名称已存在");
        }
    }

    /**
     * 数据角色 校验权限字符
     *
     * @param sysRoleData 数据角色
     * @return 结果
     */
    private void judgeAccess(SysRoleData sysRoleData) {
        QueryWrapper<SysRoleData> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysRoleData::getAccessText, sysRoleData.getAccessText());
        if (sysRoleData.getRoleDataSid() != null) {
            queryWrapper.lambda().ne(SysRoleData::getRoleDataSid, sysRoleData.getRoleDataSid());
        }
        List<SysRoleData> nameList = sysRoleDataMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException("权限字符已存在");
        }
    }

    /**
     * 新增数据角色
     * 需要注意编码重复校验
     *
     * @param sysRoleData 数据角色
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysRoleData(SysRoleData sysRoleData) {
        // 校验编码是否已存在
        judgeCode(sysRoleData);
        // 校验名称是否已存在
        judgeName(sysRoleData);
        // 校验权限字符是否已存在
        judgeAccess(sysRoleData);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(sysRoleData.getHandleStatus())) {
            if (CollectionUtil.isEmpty(sysRoleData.getObjectList())) {
                throw new BaseException("数据权限页签，明细不能为空");
            }
            sysRoleData.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = sysRoleDataMapper.insert(sysRoleData);
        if (row > 0) {
            SysRoleData response = sysRoleDataMapper.selectSysRoleDataById(sysRoleData.getRoleDataSid());
            // 权限对象
            if (CollectionUtil.isNotEmpty(sysRoleData.getObjectList())) {
                List<SysDataRoleAuthorityObject> objectList = sysRoleData.getObjectList();
                List<SysDataRoleAuthorityFieldValue> fieldValueList = new ArrayList<>();
                objectList.forEach(item->{
                    item.setRoleDataSid(response.getRoleDataSid());
                    item.setRoleDataCode(response.getRoleDataCode())
                            .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    Long sid = IdWorker.getId();
                    item.setDataRoleAuthorityObjectSid(sid).setClientId(ApiThreadLocalUtil.get().getClientId());
                    if (CollectionUtil.isNotEmpty(item.getFieldValueList())) {
                        item.getFieldValueList().forEach(field->{
                            field.setDataRoleAuthorityObjectSid(sid)
                                    .setClientId(ApiThreadLocalUtil.get().getClientId())
                                    .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                        });
                        fieldValueList.addAll(item.getFieldValueList());
                    }
                });
                authorityObjectMapper.inserts(objectList);
                if (CollectionUtil.isNotEmpty(fieldValueList)) {
                    authorityFieldValueMapper.inserts(fieldValueList);
                }
            }
            // 用户信息
            if (CollectionUtil.isNotEmpty(sysRoleData.getUserDataRoleList())) {
                List<SysUserDataRole> userDataRoleList = sysRoleData.getUserDataRoleList();
                for (SysUserDataRole user : userDataRoleList) {
                    user.setRoleDataSid(response.getRoleDataSid()).setRoleDataCode(response.getRoleDataCode())
                            .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                }
                userDataRoleMapper.inserts(userDataRoleList);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new SysRoleData(), sysRoleData);
            MongodbDeal.insert(sysRoleData.getRoleDataSid(), sysRoleData.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改数据角色
     *
     * @param sysRoleData 数据角色
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysRoleData(SysRoleData sysRoleData) {
        // 校验名称是否已存在
        judgeName(sysRoleData);
        // 校验权限字符是否已存在
        judgeAccess(sysRoleData);
        // 原数据
        SysRoleData original = sysRoleDataMapper.selectSysRoleDataById(sysRoleData.getRoleDataSid());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(sysRoleData.getHandleStatus())) {
            sysRoleData.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, sysRoleData);
        if (CollectionUtil.isNotEmpty(msgList)) {
            sysRoleData.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = sysRoleDataMapper.updateAllById(sysRoleData);
        if (row > 0) {
            this.updateObjectAndFieldList(sysRoleData);
            // 用户列表
            userDataRoleMapper.delete(new QueryWrapper<SysUserDataRole>().lambda().eq(SysUserDataRole::getRoleDataSid, sysRoleData.getRoleDataSid()));
            if (CollectionUtil.isNotEmpty(sysRoleData.getUserDataRoleList())) {
                List<SysUserDataRole> userDataRoleList = sysRoleData.getUserDataRoleList();
                for (SysUserDataRole user : userDataRoleList) {
                    if (user.getUserDataRoleSid() == null) {
                        user.setRoleDataSid(sysRoleData.getRoleDataSid()).setRoleDataCode(sysRoleData.getRoleDataCode())
                                .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                                .setUpdateDate(null).setUpdaterAccount(null);
                    }
                    user.setRoleDataCode(sysRoleData.getRoleDataCode());
                }
                userDataRoleMapper.inserts(userDataRoleList);
            }
            //插入日志
            MongodbDeal.update(sysRoleData.getRoleDataSid(), original.getHandleStatus(), sysRoleData.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更数据角色
     *
     * @param sysRoleData 数据角色
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysRoleData(SysRoleData sysRoleData) {
        // 校验名称是否已存在
        judgeName(sysRoleData);
        // 校验权限字符是否已存在
        judgeAccess(sysRoleData);
        // 原数据
        SysRoleData response = sysRoleDataMapper.selectSysRoleDataById(sysRoleData.getRoleDataSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, sysRoleData);
        if (CollectionUtil.isNotEmpty(msgList)) {
            sysRoleData.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = sysRoleDataMapper.updateAllById(sysRoleData);
        if (row > 0) {
            this.updateObjectAndFieldList(sysRoleData);
            // 用户列表
            userDataRoleMapper.delete(new QueryWrapper<SysUserDataRole>().lambda().eq(SysUserDataRole::getRoleDataSid, sysRoleData.getRoleDataSid()));
            if (CollectionUtil.isNotEmpty(sysRoleData.getUserDataRoleList())) {
                List<SysUserDataRole> userDataRoleList = sysRoleData.getUserDataRoleList();
                for (SysUserDataRole user : userDataRoleList) {
                    if (user.getUserDataRoleSid() == null) {
                        user.setRoleDataSid(sysRoleData.getRoleDataSid()).setRoleDataCode(sysRoleData.getRoleDataCode())
                                .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                                .setUpdateDate(null).setUpdaterAccount(null);
                    }
                    user.setRoleDataCode(sysRoleData.getRoleDataCode());
                }
                userDataRoleMapper.inserts(userDataRoleList);
            }
            //插入日志
            MongodbUtil.insertUserLog(sysRoleData.getRoleDataSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改权限对象明细和权限对象值
     * @param sysRoleData
     */
    private void updateObjectAndFieldList(SysRoleData sysRoleData) {
        // 权限对象
        List<SysDataRoleAuthorityObject> itemList = sysRoleData.getObjectList();
        // 删除挂载的明细
        List<SysDataRoleAuthorityObject> objectList = authorityObjectMapper.selectList(new QueryWrapper<SysDataRoleAuthorityObject>()
                .lambda().eq(SysDataRoleAuthorityObject::getRoleDataSid, sysRoleData.getRoleDataSid()));
        if (CollectionUtil.isNotEmpty(objectList)) {
            List<Long> objectSidList = objectList.stream().map(SysDataRoleAuthorityObject::getDataRoleAuthorityObjectSid).collect(Collectors.toList());
            authorityObjectMapper.deleteBatchIds(objectSidList);
            authorityFieldValueMapper.delete(new QueryWrapper<SysDataRoleAuthorityFieldValue>().lambda()
                    .in(SysDataRoleAuthorityFieldValue::getDataRoleAuthorityObjectSid, objectSidList));
        }
        if (CollectionUtil.isNotEmpty(itemList)) {
            List<SysDataRoleAuthorityFieldValue> fieldValueList = new ArrayList<>();
            itemList.forEach(item -> {
                item.setClientId(ApiThreadLocalUtil.get().getClientId());
                item.setRoleDataSid(sysRoleData.getRoleDataSid());
                item.setRoleDataCode(sysRoleData.getRoleDataCode());
                Long sid = item.getDataRoleAuthorityObjectSid() == null ? IdWorker.getId() : item.getDataRoleAuthorityObjectSid();
                if (item.getDataRoleAuthorityObjectSid() == null) {
                    item.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                            .setUpdateDate(null).setUpdaterAccount(null);
                }
                item.setDataRoleAuthorityObjectSid(sid);
                if (CollectionUtil.isNotEmpty(item.getFieldValueList())) {
                    item.getFieldValueList().forEach(field -> {
                        field.setDataRoleAuthorityObjectSid(sid).setClientId(ApiThreadLocalUtil.get().getClientId());
                        if (field.getDataRoleAuthorityFieldValueSid() == null) {
                            field.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                                    .setUpdateDate(null).setUpdaterAccount(null);
                        }
                    });
                    fieldValueList.addAll(item.getFieldValueList());
                }
            });
            authorityObjectMapper.inserts(itemList);
            if (CollectionUtil.isNotEmpty(fieldValueList)) {
                authorityFieldValueMapper.inserts(fieldValueList);
            }
        }
    }

    /**
     * 批量删除数据角色
     *
     * @param roleDataSids 需要删除的数据角色ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysRoleDataByIds(List<Long> roleDataSids) {
        int row = 0;
        List<SysRoleData> list = sysRoleDataMapper.selectList(new QueryWrapper<SysRoleData>()
                .lambda().in(SysRoleData::getRoleDataSid, roleDataSids));
        if (CollectionUtil.isNotEmpty(list)) {
            row = sysRoleDataMapper.deleteBatchIds(roleDataSids);
            // 删除挂载的明细
            List<SysDataRoleAuthorityObject> objectList = authorityObjectMapper.selectList(new QueryWrapper<SysDataRoleAuthorityObject>()
                    .lambda().in(SysDataRoleAuthorityObject::getRoleDataSid, roleDataSids));
            if (CollectionUtil.isNotEmpty(objectList)) {
                List<Long> objectSidList = objectList.stream().map(SysDataRoleAuthorityObject::getDataRoleAuthorityObjectSid).collect(Collectors.toList());
                authorityObjectMapper.deleteBatchIds(objectSidList);
                authorityFieldValueMapper.delete(new QueryWrapper<SysDataRoleAuthorityFieldValue>().lambda()
                        .in(SysDataRoleAuthorityFieldValue::getDataRoleAuthorityObjectSid, objectSidList));
            }
            // 删除用户关联
            userDataRoleMapper.delete(new QueryWrapper<SysUserDataRole>().lambda().in(SysUserDataRole::getRoleDataSid, roleDataSids));
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new SysRoleData());
                MongodbUtil.insertUserLog(o.getRoleDataSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param sysRoleData
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(SysRoleData sysRoleData) {
        int row = 0;
        Long[] sids = sysRoleData.getRoleDataSidList();
        if (sids != null && sids.length > 0) {
            List<SysRoleData> list = sysRoleDataMapper.selectList(new QueryWrapper<SysRoleData>().lambda()
                    .eq(SysRoleData::getRoleDataSid, sids)
                    .ne(SysRoleData::getStatus, sysRoleData.getStatus()));
            if (CollectionUtil.isNotEmpty(list)) {
                sids = list.stream().map(SysRoleData::getRoleDataSid).toArray(Long[]::new);
                row = sysRoleDataMapper.update(null, new UpdateWrapper<SysRoleData>().lambda()
                        .set(SysRoleData::getStatus, sysRoleData.getStatus())
                        .in(SysRoleData::getRoleDataSid, sids));
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.status(id, sysRoleData.getStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param sysRoleData
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(SysRoleData sysRoleData) {
        int row = 0;
        Long[] sids = sysRoleData.getRoleDataSidList();
        if (sids != null && sids.length > 0) {
            List<SysRoleData> list = sysRoleDataMapper.selectList(new QueryWrapper<SysRoleData>().lambda()
                    .eq(SysRoleData::getRoleDataSid, sids)
                    .ne(SysRoleData::getHandleStatus, sysRoleData.getHandleStatus()));
            if (CollectionUtil.isNotEmpty(list)) {
                sids = list.stream().map(SysRoleData::getRoleDataSid).toArray(Long[]::new);
                LambdaUpdateWrapper<SysRoleData> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(SysRoleData::getRoleDataSid, sids);
                updateWrapper.set(SysRoleData::getHandleStatus, sysRoleData.getHandleStatus());
                if (ConstantsEms.CHECK_STATUS.equals(sysRoleData.getHandleStatus())) {
                    updateWrapper.set(SysRoleData::getConfirmDate, new Date());
                    updateWrapper.set(SysRoleData::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
                }
                row = sysRoleDataMapper.update(null, updateWrapper);
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, sysRoleData.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

}
