package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.experimental.util.UniqueCheckUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.SysAuthorityField;
import com.platform.ems.domain.SysAuthorityObject;
import com.platform.ems.domain.SysAuthorityObjectField;
import com.platform.ems.mapper.SysAuthorityFieldMapper;
import com.platform.ems.mapper.SysAuthorityObjectFieldMapper;
import com.platform.ems.mapper.SysAuthorityObjectMapper;
import com.platform.ems.service.ISysAuthorityObjectService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.platform.common.redis.thread.ApiThreadLocalUtil.getLoginUserClientId;
import static com.platform.common.redis.thread.ApiThreadLocalUtil.getLoginUserUserName;

/**
 * 权限对象Service业务层处理
 *
 * @author straw
 * @date 2023-01-16
 */
@Service
@Slf4j
public class SysAuthorityObjectServiceImpl extends ServiceImpl<SysAuthorityObjectMapper, SysAuthorityObject> implements ISysAuthorityObjectService {
    final
    SysAuthorityObjectMapper objectMapper;

    final
    SysAuthorityFieldMapper fieldMapper;

    final
    SysAuthorityObjectFieldMapper ofMapper;


    private static final String TITLE = "权限对象";

    public SysAuthorityObjectServiceImpl(SysAuthorityObjectMapper objectMapper,
                                         SysAuthorityFieldMapper fieldMapper,
                                         SysAuthorityObjectFieldMapper ofMapper) {
        this.objectMapper = objectMapper;
        this.fieldMapper = fieldMapper;
        this.ofMapper = ofMapper;
    }

    /**
     * 查询权限对象
     *
     * @param authorityObjectSid 权限对象ID
     * @return 权限对象
     */
    @Override
    public SysAuthorityObject selectSysAuthorityObjectById(Long authorityObjectSid) {
        SysAuthorityObject object = objectMapper.selectSysAuthorityObjectById(authorityObjectSid);
        MongodbUtil.find(object);
        return object;
    }

    private static LambdaQueryWrapper<SysAuthorityObjectField> wrapperForObjectField(Long sid) {
        return new LambdaQueryWrapper<SysAuthorityObjectField>()
                .in(SysAuthorityObjectField::getAuthorityObjectSid, sid);
    }

    /**
     * 查询权限对象列表
     *
     * @param object 权限对象
     * @return 权限对象
     */
    @Override
    public List<SysAuthorityObject> selectSysAuthorityObjectList(SysAuthorityObject object) {
        return objectMapper.selectSysAuthorityObjectList(object);
    }

    /**
     * 新增权限对象
     * 需要注意编码重复校验
     *
     * @param object 权限对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysAuthorityObject(SysAuthorityObject object) {
        checkAuthorityObjectExistForClient(object);

        setHandleStatusInfoWhenNew(object);

        int row = objectMapper.insert(object);

        if (row <= 0) {
            return row;
        }

        // 操作关系表
        createEntityThenInsert(object);

        // 插入日志
        List<OperMsg> msgList = BeanUtils.eq(new SysAuthorityObject(), object);
        MongodbDeal.insert(object.getAuthorityObjectSid(),
                           object.getHandleStatus(),
                           msgList,
                           TITLE,
                           null);
        return row;
    }

    private void createEntityThenInsert(SysAuthorityObject object) {
        for (Long fieldSid : object.getAuthorityFieldSidList()) {
            SysAuthorityObjectField of = new SysAuthorityObjectField()
                    .setClientId(getLoginUserClientId())
                    .setAuthorityObjectSid(object.getAuthorityObjectSid())
                    .setAuthorityFieldSid(fieldSid);
            // of是明细的实体类，设置of的创建人信息
            of.setClientId(getLoginUserClientId());
            of.setCreateDate(new Date());
            of.setCreatorAccount(getLoginUserUserName());
            ofMapper.insert(of);
        }
    }

    /**
     * 修改权限对象
     *
     * @param object 权限对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysAuthorityObject(SysAuthorityObject object) {
        checkAuthorityObjectExistForClient(object);
        setHandleStatusInfoWhenUpdate(object);

        SysAuthorityObject original = objectMapper.selectSysAuthorityObjectById(object.getAuthorityObjectSid());
        updateObjectFieldTable(object);

        int row = objectMapper.updateById(object);
        if (row > 0) {
            // 插入日志
            List<OperMsg> msgList = BeanUtils.eq(original, object);
            MongodbDeal.update(object.getAuthorityObjectSid(),
                               original.getHandleStatus(),
                               object.getHandleStatus(),
                               msgList,
                               TITLE,
                               null);
        }
        return row;
    }

    /**
     * 变更权限对象
     *
     * @param object 权限对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysAuthorityObject(SysAuthorityObject object) {
        setHandleStatusInfoWhenUpdate(object);

        SysAuthorityObject response = objectMapper.selectSysAuthorityObjectById(object.getAuthorityObjectSid());
        updateObjectFieldTable(object);

        int row = objectMapper.updateAllById(object);
        if (row > 0) {
            // 插入日志
            MongodbUtil.insertUserLog(object.getAuthorityObjectSid(),
                                      BusinessType.CHANGE.getValue(),
                                      response,
                                      object,
                                      TITLE);
        }
        return row;
    }

    /**
     * 批量删除权限对象
     *
     * @param authorityObjectSids 需要删除的权限对象ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysAuthorityObjectByIds(List<Long> authorityObjectSids) {
        List<SysAuthorityObject> list = objectMapper.selectList(new QueryWrapper<SysAuthorityObject>()
                                                                        .lambda().in(SysAuthorityObject::getAuthorityObjectSid,
                                                                                     authorityObjectSids));
        int row = objectMapper.deleteBatchIds(authorityObjectSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = BeanUtils.eq(o, new SysAuthorityObject());
                MongodbUtil.insertUserLog(o.getAuthorityObjectSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param object
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(SysAuthorityObject object) {
        int row = 0;
        Long[] sids = object.getAuthorityObjectSidList();
        if (sids != null && sids.length > 0) {
            row = objectMapper.update(null,
                                      new UpdateWrapper<SysAuthorityObject>().lambda().set(
                                                                                     SysAuthorityObject::getStatus,
                                                                                     object.getStatus())
                                                                             .in(SysAuthorityObject::getAuthorityObjectSid,
                                                                                 (Object[]) sids));
            if (row == 0) {
                throw new CustomException("更改状态失败,请联系管理员");
            }
            for (Long id : sids) {
                // 插入日志
                String remark = object.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbDeal.status(id, object.getStatus(), null, TITLE, remark);
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param object
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(SysAuthorityObject object) {
        int row = 0;
        Long[] sids = object.getAuthorityObjectSidList();

        if (ArrayUtil.isEmpty(sids)) {
            return row;
        }

        for (Long sid : sids) {
            // 要求这些sid的权限对象的权限字段不能为空
            List<SysAuthorityObjectField> list = ofMapper.selectList(wrapperForObjectField(sid));
            if (CollectionUtil.isEmpty(list)) {
                throw new CheckedException("存在权限对象的权限字段为空！");
            }
        }

        LambdaUpdateWrapper<SysAuthorityObject> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SysAuthorityObject::getAuthorityObjectSid, sids);
        updateWrapper.set(SysAuthorityObject::getHandleStatus, object.getHandleStatus());
        if (ConstantsEms.CHECK_STATUS.equals(object.getHandleStatus())) {
            updateWrapper.set(SysAuthorityObject::getConfirmDate, new Date());
            updateWrapper.set(SysAuthorityObject::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        row = objectMapper.update(null, updateWrapper);
        if (row > 0) {
            for (Long id : sids) {
                // 插入日志
                MongodbDeal.check(id, object.getHandleStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    private void checkAuthorityObjectExistForClient(SysAuthorityObject object) {
        Wrapper<SysAuthorityObject> queryWrapper =
                new LambdaQueryWrapper<SysAuthorityObject>()
                        .eq((SysAuthorityObject::getObjectName), object.getObjectName())
                        .in(SysAuthorityObject::getClientId, Arrays.asList(
                                getLoginUserClientId(), "10000"
                        ));
        UniqueCheckUtil.checkUnique(object,
                                    () -> this.objectMapper.selectList(queryWrapper),
                                    SysAuthorityObject::getAuthorityObjectSid,
                                    "权限对象名称已存在");
    }


    public void updateObjectFieldTable(SysAuthorityObject obj) {
        ofMapper.delete(new LambdaQueryWrapper<SysAuthorityObjectField>()
                .eq(SysAuthorityObjectField::getAuthorityObjectSid,
                        obj.getAuthorityObjectSid()));
        if (CollectionUtil.isNotEmpty(obj.getSysAuthorityFieldList())) {
            obj.getSysAuthorityFieldList().forEach(att -> {
                // 如果是新的
                if (att.getAuthorityObjectFieldSid() == null) {
                    att.setAuthorityObjectSid(obj.getAuthorityObjectSid());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            ofMapper.inserts(obj.getSysAuthorityFieldList());
        }


    }

}
