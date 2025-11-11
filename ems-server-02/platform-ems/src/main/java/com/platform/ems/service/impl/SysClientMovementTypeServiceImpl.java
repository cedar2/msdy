package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.plug.domain.ConMovementType;
import com.platform.ems.plug.mapper.ConMovementTypeMapper;
import com.platform.ems.util.MongodbDeal;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.mapper.SysClientMovementTypeMapper;
import com.platform.ems.domain.SysClientMovementType;
import com.platform.ems.service.ISysClientMovementTypeService;

/**
 * 作业类型_租户级Service业务层处理
 *
 * @author chenkw
 * @date 2022-06-17
 */
@Service
@SuppressWarnings("all")
public class SysClientMovementTypeServiceImpl extends ServiceImpl<SysClientMovementTypeMapper, SysClientMovementType> implements ISysClientMovementTypeService {
    @Autowired
    private SysClientMovementTypeMapper sysClientMovementTypeMapper;
    @Autowired
    private ConMovementTypeMapper conMovementTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "作业类型_租户级";

    /**
     * 查询作业类型_租户级
     *
     * @param clientMovementTypeSid 作业类型_租户级ID
     * @return 作业类型_租户级
     */
    @Override
    public SysClientMovementType selectSysClientMovementTypeById(Long clientMovementTypeSid) {
        SysClientMovementType sysClientMovementType = sysClientMovementTypeMapper.selectSysClientMovementTypeById(clientMovementTypeSid);
        MongodbUtil.find(sysClientMovementType);
        return sysClientMovementType;
    }

    /**
     * 查询作业类型_租户级列表
     *
     * @param sysClientMovementType 作业类型_租户级
     * @return 作业类型_租户级
     */
    @Override
    public List<SysClientMovementType> selectSysClientMovementTypeList(SysClientMovementType sysClientMovementType) {
        return sysClientMovementTypeMapper.selectSysClientMovementTypeList(sysClientMovementType);
    }

    /**
     * 设置确认人
     */
    private void setConfirm(SysClientMovementType sysClientMovementType){
        if (ConstantsEms.CHECK_STATUS.equals(sysClientMovementType.getHandleStatus())){
            sysClientMovementType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername())
                    .setConfirmDate(new Date());
        }
    }

    /**
     * 获取移动类型作业类型的编码
     */
    private String getMovementTypeCode(SysClientMovementType sysClientMovementType){
        String code = null;
        if (sysClientMovementType.getMovementTypeSid() != null){
            ConMovementType movementType = conMovementTypeMapper.selectById(sysClientMovementType.getMovementTypeSid());
            if (movementType != null){
                code = movementType.getCode();
            }
        }
        return code;
    }

    /**
     * 唯一性校验
     */
    private void checkUnique(SysClientMovementType sysClientMovementType){
        QueryWrapper<SysClientMovementType> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysClientMovementType::getClientId, sysClientMovementType.getClientId());
        queryWrapper.lambda().eq(SysClientMovementType::getMovementTypeSid, sysClientMovementType.getMovementTypeSid());
        if (sysClientMovementType.getClientMovementTypeSid() != null){
            queryWrapper.lambda().ne(SysClientMovementType::getClientMovementTypeSid, sysClientMovementType.getClientMovementTypeSid());
        }
        SysClientMovementType type = new SysClientMovementType();
        try {
            type = sysClientMovementTypeMapper.selectOne(queryWrapper);
        }catch (Exception e){
            throw new BaseException("该配置已重复存在多个，请核实！");
        }
        if (type != null){
            throw new BaseException("该配置已存在！");
        }
    }

    /**
     * 新增作业类型_租户级
     * 需要注意编码重复校验
     *
     * @param sysClientMovementType 作业类型_租户级
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysClientMovementType(SysClientMovementType sysClientMovementType) {
        setConfirm(sysClientMovementType);
        checkUnique(sysClientMovementType);
        sysClientMovementType.setMovementTypeCode(getMovementTypeCode(sysClientMovementType));
        int row = sysClientMovementTypeMapper.insert(sysClientMovementType);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(sysClientMovementType.getClientMovementTypeSid(), sysClientMovementType.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改作业类型_租户级
     *
     * @param sysClientMovementType 作业类型_租户级
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysClientMovementType(SysClientMovementType sysClientMovementType) {
        checkUnique(sysClientMovementType);
        SysClientMovementType response = sysClientMovementTypeMapper.selectSysClientMovementTypeById(sysClientMovementType.getClientMovementTypeSid());
        sysClientMovementType.setMovementTypeCode(getMovementTypeCode(sysClientMovementType));
        int row = sysClientMovementTypeMapper.updateById(sysClientMovementType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(sysClientMovementType.getClientMovementTypeSid(), BusinessType.UPDATE.getValue(), response, sysClientMovementType, TITLE);
        }
        return row;
    }

    /**
     * 变更作业类型_租户级
     *
     * @param sysClientMovementType 作业类型_租户级
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysClientMovementType(SysClientMovementType sysClientMovementType) {
        checkUnique(sysClientMovementType);
        SysClientMovementType response = sysClientMovementTypeMapper.selectSysClientMovementTypeById(sysClientMovementType.getClientMovementTypeSid());
        sysClientMovementType.setMovementTypeCode(getMovementTypeCode(sysClientMovementType));
        int row = sysClientMovementTypeMapper.updateAllById(sysClientMovementType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(sysClientMovementType.getClientMovementTypeSid(), BusinessType.CHANGE.getValue(), response, sysClientMovementType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除作业类型_租户级
     *
     * @param clientMovementTypeSids 需要删除的作业类型_租户级ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysClientMovementTypeByIds(List<Long> clientMovementTypeSids) {
        return sysClientMovementTypeMapper.deleteBatchIds(clientMovementTypeSids);
    }

    /**
     * 更改确认状态
     *
     * @param sysClientMovementType
     * @return
     */
    @Override
    public int check(SysClientMovementType sysClientMovementType) {
        int row = 0;
        Long[] sids = sysClientMovementType.getClientMovementTypeSidList();
        if (sids != null && sids.length > 0) {
            UpdateWrapper<SysClientMovementType> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .set(SysClientMovementType::getHandleStatus, sysClientMovementType.getHandleStatus())
                    .in(SysClientMovementType::getClientMovementTypeSid, sids);
            if (ConstantsEms.CHECK_STATUS.equals(sysClientMovementType.getHandleStatus())){
                updateWrapper.lambda().set(SysClientMovementType::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
                updateWrapper.lambda().set(SysClientMovementType::getConfirmDate, new Date());
            }
            row = sysClientMovementTypeMapper.update(null, updateWrapper);
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.check(id, sysClientMovementType.getHandleStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }


}
