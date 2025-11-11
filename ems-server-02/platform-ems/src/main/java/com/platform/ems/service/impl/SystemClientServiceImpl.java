package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.service.ISystemClientService;
import com.platform.ems.util.MongodbUtil;
import com.platform.api.service.RemoteUserService;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.system.mapper.SysClientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 租户信息Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-30
 */
@Service
@SuppressWarnings("all")
public class SystemClientServiceImpl extends ServiceImpl<SysClientMapper, SysClient> implements ISystemClientService {
    @Autowired
    private SysClientMapper sysClientMapper;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "租户信息";

    /**
     * 查询租户信息
     *
     * @param clientId 租户信息ID
     * @return 租户信息
     */
    @Override
    public SysClient selectSysClientById(String clientId) {
        SysClient sysClient = sysClientMapper.selectSysClientById(clientId);
        MongodbUtil.findString(sysClient);
        return sysClient;
    }

    /**
     * 查询租户信息列表
     *
     * @param sysClient 租户信息
     * @return 租户信息
     */
    @Override
    public List<SysClient> selectSysClientList(SysClient sysClient) {
        return sysClientMapper.selectSysClientList(sysClient);
    }

    /**
     * 新增租户信息
     * 需要注意编码重复校验
     *
     * @param sysClient 租户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysClient(SysClient sysClient) {
        List<SysClient> clientIds = sysClientMapper.selectList(new QueryWrapper<SysClient>().lambda()
                .eq(SysClient::getClientId, sysClient.getClientId()));
        if (CollectionUtil.isNotEmpty(clientIds)) {
            throw new BaseException("租户ID已存在");
        }
        List<SysClient> clientCodes = sysClientMapper.selectList(new QueryWrapper<SysClient>().lambda()
                .eq(SysClient::getClientCode, sysClient.getClientCode()));
        if (CollectionUtil.isNotEmpty(clientCodes)) {
            throw new BaseException("租户编码已存在");
        }
        List<SysClient> clientNames = sysClientMapper.selectList(new QueryWrapper<SysClient>().lambda()
                .eq(SysClient::getClientName, sysClient.getClientName()));
        if (CollectionUtil.isNotEmpty(clientNames)) {
            throw new BaseException("租户名称已存在");
        }
        setConfirmInfo(sysClient);
        int row = sysClientMapper.insert(sysClient);
        if (row > 0) {
            //自动注册系统账号
            SysUser sysUser = new SysUser();
            sysUser.setClientId(sysClient.getClientId());//租户id
            sysUser.setUserName(sysClient.getClientId());//租户账号
            sysUser.setNickName(sysClient.getClientId());//租户昵称
            sysUser.setSex("2");
            sysUser.setDeptId(100L);
            sysUser.setPassword("123456");
            sysUser.setStatus("0");
            sysUser.setRemark("租户管理员账号,租户id:" + sysClient.getClientId());
            R result = remoteUserService.autoRegister(sysUser);
            if (result.getCode() != 200) {
                throw new BaseException(result.getMsg());
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(Long.parseLong(sysClient.getClientId()), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(SysClient o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改租户信息
     *
     * @param sysClient 租户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysClient(SysClient sysClient) {
        checkNameUnique(sysClient);
        setConfirmInfo(sysClient);
        SysClient response = sysClientMapper.selectSysClientById(sysClient.getClientId());
        int row = sysClientMapper.updateAllById(sysClient);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(Long.parseLong(sysClient.getClientId()), BusinessType.UPDATE.getValue(), response, sysClient, TITLE);
        }
        return row;
    }

    /**
     * 校验名称是否重复
     */
    private void checkNameUnique(SysClient sysClient) {
        List<SysClient> clientNames = sysClientMapper.selectList(new QueryWrapper<SysClient>().lambda()
                .eq(SysClient::getClientName, sysClient.getClientName()));
        if (CollectionUtil.isNotEmpty(clientNames)) {
            clientNames.forEach(o ->{
                if (!o.getClientId().equals(sysClient.getClientId())) {
                    throw new BaseException("租户名称已存在");
                }
            });
        }
    }

    /**
     * 变更租户信息
     *
     * @param sysClient 租户信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSysClient(SysClient sysClient) {
        checkNameUnique(sysClient);
        setConfirmInfo(sysClient);
        sysClient.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        SysClient response = sysClientMapper.selectSysClientById(sysClient.getClientId());
        int row = sysClientMapper.updateAllById(sysClient);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(Long.parseLong(sysClient.getClientId()), BusinessType.CHANGE.getValue(), response, sysClient, TITLE);
        }
        return row;
    }

    /**
     * 批量删除租户信息
     *
     * @param clientIds 需要删除的租户信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSysClientByIds(List<String> clientIds) {
        Integer count = sysClientMapper.selectCount(new QueryWrapper<SysClient>().lambda()
                .eq(SysClient::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(SysClient::getClientId, clientIds));
        if (count != clientIds.size()){
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        return sysClientMapper.deleteBatchIds(clientIds);
    }

    /**
     * 启用/停用
     *
     * @param sysClient
     * @return
     */
    @Override
    public int changeStatus(SysClient sysClient) {
        int row = 0;
        String[] sids = sysClient.getClientIdList();
        if (sids != null && sids.length > 0) {
            row = sysClientMapper.update(null, new UpdateWrapper<SysClient>().lambda().set(SysClient::getStatus, sysClient.getStatus())
                    .in(SysClient::getClientId, sids));
            for (String id : sids) {
                sysClient.setClientId(id);
                row = sysClientMapper.updateById(sysClient);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = sysClient.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(Long.parseLong(id), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param sysClient
     * @return
     */
    @Override
    public int check(SysClient sysClient) {
        int row = 0;
        String[] sids = sysClient.getClientIdList();
        if (sids != null && sids.length > 0) {
            Integer count = sysClientMapper.selectCount(new QueryWrapper<SysClient>().lambda()
                    .eq(SysClient::getHandleStatus, ConstantsEms.SAVA_STATUS)
                    .in(SysClient::getClientId, sids));
            if (count != sids.length){
                throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
            }
            row = sysClientMapper.update(null, new UpdateWrapper<SysClient>().lambda().set(SysClient::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(SysClient::getClientId, sids));
            for (String id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(Long.parseLong(id), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    @Override
    public List<SysClient> getList(SysClient sysClient) {
        return sysClientMapper.getList(sysClient);
    }

    @Override
    public int setDianqian(SysClient client) {
        int row = 0;
        SysClient sysClient = sysClientMapper.selectOne(new QueryWrapper<SysClient>().lambda()
                .eq(SysClient::getClientId, client.getClientId()));
        StringBuilder changeInfo = new StringBuilder();
        if (client.getClientId() != null && sysClient != null) {
            String oldData = "";
            String newData = "";
            if(Objects.nonNull(sysClient.getLicenseDianqianNum()) || Objects.nonNull(client.getLicenseDianqianNum())) {
                oldData = "";
                newData = "";
                if (sysClient.getLicenseDianqianNum() == null) {
                    newData = String.valueOf(client.getLicenseDianqianNum());
                } else if (client.getLicenseDianqianNum() == null) {
                    oldData = String.valueOf(sysClient.getLicenseDianqianNum());
                } else if (sysClient.getLicenseDianqianNum().compareTo(client.getLicenseDianqianNum()) != 0) {
                    newData = String.valueOf(client.getLicenseDianqianNum());
                    oldData = String.valueOf(sysClient.getLicenseDianqianNum());
                }
                if ( !oldData.equals("") || !newData.equals("")) {
                    changeInfo.append("授权电签数变更，原值：").append(oldData).append("，新值：").append(newData).append("；");
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if((client.getDianqianStartDate() != null && !client.getDianqianStartDate().equals("")) ||
                    (sysClient.getDianqianStartDate() != null && !sysClient.getDianqianStartDate().equals(""))) {
                oldData = "";
                newData = "";
                if (sysClient.getDianqianStartDate() == null) {
                    newData = sdf.format(client.getDianqianStartDate());
                }else if(client.getDianqianStartDate() == null) {
                    oldData = sdf.format(sysClient.getDianqianStartDate());
                }
                else if (!sysClient.getDianqianStartDate().equals(client.getDianqianStartDate())) {
                    newData = sdf.format(client.getDianqianStartDate());
                    oldData = sdf.format(sysClient.getDianqianStartDate());
                }
                if(!oldData.equals("")|| !newData.equals("")) {
                    changeInfo.append("电签数有效期(起)变更，原值：").append(oldData).append("，新值：").append(newData).append("；");
                }
            }
            if((client.getDianqianEndDate() != null && !client.getDianqianEndDate().equals("")) ||
                    (sysClient.getDianqianEndDate() != null && !sysClient.getDianqianEndDate().equals(""))) {
                oldData = "";
                newData = "";
                if (sysClient.getDianqianEndDate() == null) {
                    newData = sdf.format(client.getDianqianEndDate());
                }else if(client.getDianqianEndDate() == null) {
                    oldData = sdf.format(sysClient.getDianqianEndDate());
                }
                else if (!sysClient.getDianqianEndDate().equals(client.getDianqianEndDate())) {
                    newData = sdf.format(client.getDianqianEndDate());
                    oldData = sdf.format(sysClient.getDianqianEndDate());
                }
                if(!oldData.equals("") || !newData.equals("")){
                    changeInfo.append("电签数有效期(至)变更，原值：").append(oldData).append("，新值：").append(newData).append("；");
                }
            }

            if(Objects.nonNull(sysClient.getUseDianqianNum()) || Objects.nonNull(client.getUseDianqianNum())){
                oldData = "";
                newData = "";
                if (sysClient.getUseDianqianNum() == null) {
                    newData = String.valueOf(client.getUseDianqianNum());
                }else if(client.getUseDianqianNum() == null) {
                    oldData = String.valueOf(sysClient.getUseDianqianNum());
                }
                else if (sysClient.getUseDianqianNum().compareTo(client.getUseDianqianNum()) != 0) {
                    newData = String.valueOf(client.getUseDianqianNum());
                    oldData = String.valueOf(sysClient.getUseDianqianNum());
                }
                if(!oldData.equals("") || !newData.equals("")){
                    changeInfo.append("当期已使用电签数变更，原值：").append(oldData).append("，新值：").append(newData).append("；");
                }
            }

            sysClientMapper.update(null, new UpdateWrapper<SysClient>().lambda()
                    .eq(SysClient::getClientId, client.getClientId())
                    .set(SysClient::getLicenseDianqianNum,client.getLicenseDianqianNum())
                    .set(SysClient::getDianqianStartDate,client.getDianqianStartDate())
                    .set(SysClient::getDianqianEndDate,client.getDianqianEndDate())
                    .set(SysClient::getUseDianqianNum,client.getUseDianqianNum()));
            row = 1;
        }
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(Long.valueOf(client.getClientId()), BusinessType.CHANGE.getValue(), sysClient, client, TITLE, changeInfo.toString());
        }
        return row;
    }

}
