package com.platform.ems.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.HrOtherPersonnelCertificate;
import com.platform.ems.domain.HrOtherPersonnelCertificateAttach;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.HrOtherPersonnelCertificateAttachMapper;
import com.platform.ems.mapper.HrOtherPersonnelCertificateMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.service.IHrOtherPersonnelCertificateService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 其它人事证明
 *
 * @author xfzz
 * @date 2024/5/8
 */
@Service
public class HrOtherPersonnelCertificateServiceImpl extends ServiceImpl<HrOtherPersonnelCertificateMapper, HrOtherPersonnelCertificate> implements IHrOtherPersonnelCertificateService {

    @Autowired
    private HrOtherPersonnelCertificateMapper hrOtherPersonnelCertificateMapper;
    @Autowired
    private HrOtherPersonnelCertificateAttachMapper hrOtherPersonnelCertificateAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    private static final String TITLE = "其它人事证明";

    /**
     * 查询其它人事证明
     *
     * @param otherPersonnelCertificateSid 其它人事证明ID
     * @return 其它人事证明
     */
    @Override
    public HrOtherPersonnelCertificate selectHrOtherPersonnelCertificateById(Long otherPersonnelCertificateSid) {
        HrOtherPersonnelCertificate hrOtherPersonnelCertificate = hrOtherPersonnelCertificateMapper.selectHrOtherPersonnelCertificateById(otherPersonnelCertificateSid);
        List<HrOtherPersonnelCertificateAttach> attachList = hrOtherPersonnelCertificateAttachMapper.selectHrOtherPersonnelCertificateAttachList(new HrOtherPersonnelCertificateAttach().setOtherPersonnelCertificateSid(otherPersonnelCertificateSid));
        hrOtherPersonnelCertificate.setAttachmentList(attachList);
        MongodbUtil.find(hrOtherPersonnelCertificate);
        return hrOtherPersonnelCertificate;
    }

    /**
     * 查询其它人事证明列表
     *
     * @param hrOtherPersonnelCertificate 其它人事证明
     * @return 其它人事证明
     */
    @Override
    public List<HrOtherPersonnelCertificate> selectHrOtherPersonnelCertificateList(HrOtherPersonnelCertificate hrOtherPersonnelCertificate) {
        List<HrOtherPersonnelCertificate> list = hrOtherPersonnelCertificateMapper.selectHrOtherPersonnelCertificateList(hrOtherPersonnelCertificate);
        return list;
    }

    /**
     * 新增其它人事证明
     * 需要注意编码重复校验
     *
     * @param hrOtherPersonnelCertificate 其它人事证明
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertHrOtherPersonnelCertificate(HrOtherPersonnelCertificate hrOtherPersonnelCertificate) {
        // 写默认值
        hrOtherPersonnelCertificate.setSignInStatus("WQS");
        hrOtherPersonnelCertificate.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(hrOtherPersonnelCertificate.getHandleStatus())) {
            hrOtherPersonnelCertificate.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = hrOtherPersonnelCertificateMapper.insert(hrOtherPersonnelCertificate);
        if (row > 0) {
            if (CollectionUtil.isNotEmpty(hrOtherPersonnelCertificate.getAttachmentList())){
                hrOtherPersonnelCertificate.getAttachmentList().forEach(item->{
                    item.setOtherPersonnelCertificateSid(hrOtherPersonnelCertificate.getOtherPersonnelCertificateSid ());
                });
                hrOtherPersonnelCertificateAttachMapper.inserts(hrOtherPersonnelCertificate.getAttachmentList());
            }
            //插入日志
            List<OperMsg> msgList;
            msgList = BeanUtils.eq(new HrOtherPersonnelCertificate(), hrOtherPersonnelCertificate);
            MongodbDeal.insert(hrOtherPersonnelCertificate.getOtherPersonnelCertificateSid (), hrOtherPersonnelCertificate.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更其它人事证明
     *
     * @param hrOtherPersonnelCertificate 其它人事证明
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeHrOtherPersonnelCertificate(HrOtherPersonnelCertificate hrOtherPersonnelCertificate) {
        // 写默认值OtherPersonnelCertificateSid
        HrOtherPersonnelCertificate response = hrOtherPersonnelCertificateMapper.selectHrOtherPersonnelCertificateById(hrOtherPersonnelCertificate.getOtherPersonnelCertificateSid ());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, hrOtherPersonnelCertificate);
        if (CollectionUtil.isNotEmpty(msgList)) {
            hrOtherPersonnelCertificate.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = hrOtherPersonnelCertificateMapper.updateAllById(hrOtherPersonnelCertificate);
        if (row > 0) {
            addAttach(hrOtherPersonnelCertificate);
            if (HandleStatus.SUBMIT.getCode().equals(hrOtherPersonnelCertificate.getHandleStatus())){
                // 附件清单
                updateAttach(hrOtherPersonnelCertificate);
                //插入日志
                MongodbUtil.insertUserLog(hrOtherPersonnelCertificate.getOtherPersonnelCertificateSid (), BusinessType.CHANGE.getValue(), response, hrOtherPersonnelCertificate, TITLE);

            }
        }
        return row;
    }

    /**
     * 批量删除其它人事证明
     *
     * @param otherPersonnelCertificateSids 需要删除的其它人事证明ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteHrOtherPersonnelCertificateByIds(List<Long> otherPersonnelCertificateSids) {
        List<HrOtherPersonnelCertificate> list = hrOtherPersonnelCertificateMapper.selectList(new QueryWrapper<HrOtherPersonnelCertificate>()
                .lambda().in(HrOtherPersonnelCertificate::getOtherPersonnelCertificateSid, otherPersonnelCertificateSids));
        int row = hrOtherPersonnelCertificateMapper.deleteBatchIds(otherPersonnelCertificateSids);
        if (row > 0) {
            // 删除附件
            hrOtherPersonnelCertificateAttachMapper.delete(new QueryWrapper<HrOtherPersonnelCertificateAttach>().lambda()
                    .in(HrOtherPersonnelCertificateAttach::getOtherPersonnelCertificateSid, otherPersonnelCertificateSids));
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new HrOtherPersonnelCertificate());
                MongodbUtil.insertUserLog(o.getOtherPersonnelCertificateSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param hrOtherPersonnelCertificate
     * @return
     */
    @Override
    public int check(HrOtherPersonnelCertificate hrOtherPersonnelCertificate) {
        int row = 0;
        Long[] sids = hrOtherPersonnelCertificate.getOtherPersonnelCertificateSidList();
        if (sids != null && sids.length > 0) {
            row = hrOtherPersonnelCertificateMapper.update(null, new UpdateWrapper<HrOtherPersonnelCertificate>().lambda()
                    .set(HrOtherPersonnelCertificate::getHandleStatus, hrOtherPersonnelCertificate.getHandleStatus())
                    .set(HrOtherPersonnelCertificate::getConfirmDate, new Date())
                    .set(HrOtherPersonnelCertificate::getConfirmerAccount,ApiThreadLocalUtil.get().getUsername())
                    .set(HrOtherPersonnelCertificate::getUpdateDate, new Date())
                    .set(HrOtherPersonnelCertificate::getUpdaterAccount, ApiThreadLocalUtil.get().getUsername())
                    .in(HrOtherPersonnelCertificate::getOtherPersonnelCertificateSid , sids));
            //删除代办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid,sids)
                    .eq(SysTodoTask::getTaskCategory,ConstantsEms.TODO_TASK_DB));
            //插入日志
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, hrOtherPersonnelCertificate.getHandleStatus(), null, TITLE, null);
            }
            //如果是审批通过
            if (ConstantsEms.CHECK_STATUS.equals(hrOtherPersonnelCertificate.getHandleStatus())){
                List<HrOtherPersonnelCertificate> recordList = hrOtherPersonnelCertificateMapper.selectList(new QueryWrapper<HrOtherPersonnelCertificate>().lambda()
                        .in(HrOtherPersonnelCertificate::getOtherPersonnelCertificateSid ,sids));
                if (CollectionUtil.isNotEmpty(recordList)){
                    recordList.forEach(record->{

                    });
                }
            }
        }
        return row;
    }
    /**
     * 其它人事证明签收
     */
    @Override

    @Transactional(rollbackFor = Exception.class)
    public int signHrOtherPersonnelCertificateById(HrOtherPersonnelCertificate hrOtherPersonnelCertificate) {
        LambdaUpdateWrapper<HrOtherPersonnelCertificate> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(HrOtherPersonnelCertificate::getOtherPersonnelCertificateSid ,hrOtherPersonnelCertificate.getOtherPersonnelCertificateSids())
                .set(HrOtherPersonnelCertificate::getSignInStatus, hrOtherPersonnelCertificate.getSignInStatus());
        int row = hrOtherPersonnelCertificateMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 处理附件
     *
     * @param hrOtherPersonnelCertificate
     * @return
     */
    public void addAttach(HrOtherPersonnelCertificate hrOtherPersonnelCertificate){
        hrOtherPersonnelCertificateAttachMapper.delete(new QueryWrapper<HrOtherPersonnelCertificateAttach>().lambda()
                .eq(HrOtherPersonnelCertificateAttach::getOtherPersonnelCertificateSid ,hrOtherPersonnelCertificate.getOtherPersonnelCertificateSid ()));
        if (CollectionUtil.isNotEmpty(hrOtherPersonnelCertificate.getAttachmentList())){
            hrOtherPersonnelCertificate.getAttachmentList().forEach(item->{
                item.setOtherPersonnelCertificateSid (hrOtherPersonnelCertificate.getOtherPersonnelCertificateSid ());
            });
            hrOtherPersonnelCertificateAttachMapper.inserts(hrOtherPersonnelCertificate.getAttachmentList());
        }
    }

    /**
     * 批量修改附件信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAttach(HrOtherPersonnelCertificate record) {
        // 先删后加
        hrOtherPersonnelCertificateAttachMapper.delete(new QueryWrapper<HrOtherPersonnelCertificateAttach>().lambda()
                .eq(HrOtherPersonnelCertificateAttach::getOtherPersonnelCertificateSid , record.getOtherPersonnelCertificateSid ()));
        if (CollectionUtil.isNotEmpty(record.getAttachmentList())) {
            record.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getOtherPersonnelCertificateSid () == null) {
                    att.setClientId(ApiThreadLocalUtil.get().getSysUser().getClientId())
                            .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    att.setOtherPersonnelCertificateSid (record.getOtherPersonnelCertificateSid ());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            hrOtherPersonnelCertificateAttachMapper.inserts(record.getAttachmentList());
        }
    }


}
