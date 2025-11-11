package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.HrIncomeCertificate;
import com.platform.ems.domain.HrIncomeCertificateAttach;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.HrIncomeCertificateAttachMapper;
import com.platform.ems.mapper.HrIncomeCertificateMapper;
import com.platform.ems.service.IHrIncomeCertificateService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 收入证明
 *
 * @author xfzz
 * @date 2024/5/8
 */
@Service
public class HrIncomeCertificateServiceImpl extends ServiceImpl<HrIncomeCertificateMapper, HrIncomeCertificate> implements IHrIncomeCertificateService {

    @Autowired
    private HrIncomeCertificateMapper hrIncomeCertificateMapper;
    @Autowired
    private HrIncomeCertificateAttachMapper hrIncomeCertificateAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    private static final String TITLE = "收入证明";

    /**
     * 查询收入证明
     *
     * @param incomeCertificateSid 收入证明ID
     * @return 收入证明
     */
    @Override
    public HrIncomeCertificate selectHrIncomeCertificateById(Long incomeCertificateSid) {
        HrIncomeCertificate hrIncomeCertificate = hrIncomeCertificateMapper.selectHrIncomeCertificateById(incomeCertificateSid);
        List<HrIncomeCertificateAttach> attachList = hrIncomeCertificateAttachMapper.selectHrIncomeCertificateAttachList(new HrIncomeCertificateAttach().setIncomeCertificateSid(incomeCertificateSid));
        hrIncomeCertificate.setAttachmentList(attachList);
        MongodbUtil.find(hrIncomeCertificate);
        return hrIncomeCertificate;
    }

    /**
     * 查询收入证明列表
     *
     * @param hrIncomeCertificate 收入证明
     * @return 收入证明
     */
    @Override
    public List<HrIncomeCertificate> selectHrIncomeCertificateList(HrIncomeCertificate hrIncomeCertificate) {
        List<HrIncomeCertificate> list = hrIncomeCertificateMapper.selectHrIncomeCertificateList(hrIncomeCertificate);
        return list;
    }

    /**
     * 新增收入证明
     * 需要注意编码重复校验
     *
     * @param hrIncomeCertificate 收入证明
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertHrIncomeCertificate(HrIncomeCertificate hrIncomeCertificate) {
        // 写默认值
        hrIncomeCertificate.setSignInStatus("WQS");
        hrIncomeCertificate.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(hrIncomeCertificate.getHandleStatus())) {
            hrIncomeCertificate.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = hrIncomeCertificateMapper.insert(hrIncomeCertificate);
        if (row > 0) {
            if (CollectionUtil.isNotEmpty(hrIncomeCertificate.getAttachmentList())){
                hrIncomeCertificate.getAttachmentList().forEach(item->{
                    item.setIncomeCertificateSid (hrIncomeCertificate.getIncomeCertificateSid ());
                });
                hrIncomeCertificateAttachMapper.inserts(hrIncomeCertificate.getAttachmentList());
            }
            //插入日志
            List<OperMsg> msgList;
            msgList = BeanUtils.eq(new HrIncomeCertificate(), hrIncomeCertificate);
            MongodbDeal.insert(hrIncomeCertificate.getIncomeCertificateSid (), hrIncomeCertificate.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更收入证明
     *
     * @param hrIncomeCertificate 收入证明
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeHrIncomeCertificate(HrIncomeCertificate hrIncomeCertificate) {
        // 写默认值IncomeCertificateSid
        HrIncomeCertificate response = hrIncomeCertificateMapper.selectHrIncomeCertificateById(hrIncomeCertificate.getIncomeCertificateSid ());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, hrIncomeCertificate);
        if (CollectionUtil.isNotEmpty(msgList)) {
            hrIncomeCertificate.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = hrIncomeCertificateMapper.updateAllById(hrIncomeCertificate);
        if (row > 0) {
            addAttach(hrIncomeCertificate);
            if (HandleStatus.SUBMIT.getCode().equals(hrIncomeCertificate.getHandleStatus())){
                // 附件清单
                updateAttach(hrIncomeCertificate);
                //插入日志
                MongodbUtil.insertUserLog(hrIncomeCertificate.getIncomeCertificateSid (), BusinessType.CHANGE.getValue(), response, hrIncomeCertificate, TITLE);

            }
        }
        return row;
    }

    /**
     * 批量删除收入证明
     *
     * @param incomeCertificateSids 需要删除的收入证明ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteHrIncomeCertificateByIds(List<Long> incomeCertificateSids) {
        List<HrIncomeCertificate> list = hrIncomeCertificateMapper.selectList(new QueryWrapper<HrIncomeCertificate>()
                .lambda().in(HrIncomeCertificate::getIncomeCertificateSid, incomeCertificateSids));
        int row = hrIncomeCertificateMapper.deleteBatchIds(incomeCertificateSids);
        if (row > 0) {
            // 删除附件
            hrIncomeCertificateAttachMapper.delete(new QueryWrapper<HrIncomeCertificateAttach>().lambda()
                    .in(HrIncomeCertificateAttach::getIncomeCertificateSid, incomeCertificateSids));
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new HrIncomeCertificate());
                MongodbUtil.insertUserLog(o.getIncomeCertificateSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param hrIncomeCertificate
     * @return
     */
    @Override
    public int check(HrIncomeCertificate hrIncomeCertificate) {
        int row = 0;
        Long[] sids = hrIncomeCertificate.getIncomeCertificateSidList();
        if (sids != null && sids.length > 0) {
            row = hrIncomeCertificateMapper.update(null, new UpdateWrapper<HrIncomeCertificate>().lambda()
                    .set(HrIncomeCertificate::getHandleStatus, hrIncomeCertificate.getHandleStatus())
                    .set(HrIncomeCertificate::getConfirmDate, new Date())
                    .set(HrIncomeCertificate::getConfirmerAccount,ApiThreadLocalUtil.get().getUsername())
                    .set(HrIncomeCertificate::getUpdateDate, new Date())
                    .set(HrIncomeCertificate::getUpdaterAccount, ApiThreadLocalUtil.get().getUsername())
                    .in(HrIncomeCertificate::getIncomeCertificateSid , sids));
            //删除代办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid,sids)
                    .eq(SysTodoTask::getTaskCategory,ConstantsEms.TODO_TASK_DB));
            //插入日志
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, hrIncomeCertificate.getHandleStatus(), null, TITLE, null);
            }
            //如果是审批通过
            if (ConstantsEms.CHECK_STATUS.equals(hrIncomeCertificate.getHandleStatus())){
                List<HrIncomeCertificate> recordList = hrIncomeCertificateMapper.selectList(new QueryWrapper<HrIncomeCertificate>().lambda()
                        .in(HrIncomeCertificate::getIncomeCertificateSid ,sids));
                if (CollectionUtil.isNotEmpty(recordList)){
                    recordList.forEach(record->{

                    });
                }
            }
        }
        return row;
    }
    /**
     * 收入证明签收
     */
    @Override

    @Transactional(rollbackFor = Exception.class)
    public int signHrIncomeCertificateById(HrIncomeCertificate hrIncomeCertificate) {
        LambdaUpdateWrapper<HrIncomeCertificate> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(HrIncomeCertificate::getIncomeCertificateSid ,hrIncomeCertificate.getIncomeCertificateSids())
                .set(HrIncomeCertificate::getSignInStatus, hrIncomeCertificate.getSignInStatus());
        int row = hrIncomeCertificateMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 处理附件
     *
     * @param hrIncomeCertificate
     * @return
     */
    public void addAttach(HrIncomeCertificate hrIncomeCertificate){
        hrIncomeCertificateAttachMapper.delete(new QueryWrapper<HrIncomeCertificateAttach>().lambda()
                .eq(HrIncomeCertificateAttach::getIncomeCertificateSid ,hrIncomeCertificate.getIncomeCertificateSid ()));
        if (CollectionUtil.isNotEmpty(hrIncomeCertificate.getAttachmentList())){
            hrIncomeCertificate.getAttachmentList().forEach(item->{
                item.setIncomeCertificateSid (hrIncomeCertificate.getIncomeCertificateSid ());
            });
            hrIncomeCertificateAttachMapper.inserts(hrIncomeCertificate.getAttachmentList());
        }
    }

    /**
     * 批量修改附件信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAttach(HrIncomeCertificate record) {
        // 先删后加
        hrIncomeCertificateAttachMapper.delete(new QueryWrapper<HrIncomeCertificateAttach>().lambda()
                .eq(HrIncomeCertificateAttach::getIncomeCertificateSid , record.getIncomeCertificateSid ()));
        if (CollectionUtil.isNotEmpty(record.getAttachmentList())) {
            record.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getIncomeCertificateSid () == null) {
                    att.setClientId(ApiThreadLocalUtil.get().getSysUser().getClientId())
                            .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    att.setIncomeCertificateSid (record.getIncomeCertificateSid ());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            hrIncomeCertificateAttachMapper.inserts(record.getAttachmentList());
        }
    }


}
