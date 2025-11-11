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
import com.platform.ems.domain.HrDimissionCertificate;
import com.platform.ems.domain.HrDimissionCertificateAttach;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.HrDimissionCertificateAttachMapper;
import com.platform.ems.mapper.HrDimissionCertificateMapper;
import com.platform.ems.service.IHrDimissionCertificateService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 离职证明
 *
 * @author xfzz
 * @date 2024/5/8
 */
@Service
public class HrDimissionCertificateServiceImpl extends ServiceImpl<HrDimissionCertificateMapper, HrDimissionCertificate> implements IHrDimissionCertificateService {

    @Autowired
    private HrDimissionCertificateMapper hrDimissionCertificateMapper;
    @Autowired
    private HrDimissionCertificateAttachMapper hrDimissionCertificateAttachMapper;
    @Resource
    private SysTodoTaskMapper sysTodoTaskMapper;

    private static final String TITLE = "离职证明";

    /**
     * 查询离职证明
     *
     * @param dimissionCertificateSid 离职证明ID
     * @return 离职证明
     */
    @Override
    public HrDimissionCertificate selectHrDimissionCertificateById(Long dimissionCertificateSid) {
        HrDimissionCertificate hrDimissionCertificate = hrDimissionCertificateMapper.selectHrDimissionCertificateById(dimissionCertificateSid);
        List<HrDimissionCertificateAttach> attachList = hrDimissionCertificateAttachMapper.selectHrDimissionCertificateAttachList(new HrDimissionCertificateAttach().setDimissionCertificateSid(dimissionCertificateSid));
        hrDimissionCertificate.setAttachmentList(attachList);
        MongodbUtil.find(hrDimissionCertificate);
        return hrDimissionCertificate;
    }

    /**
     * 查询离职证明列表
     *
     * @param hrDimissionCertificate 离职证明
     * @return 离职证明
     */
    @Override
    public List<HrDimissionCertificate> selectHrDimissionCertificateList(HrDimissionCertificate hrDimissionCertificate) {
        List<HrDimissionCertificate> list = hrDimissionCertificateMapper.selectHrDimissionCertificateList(hrDimissionCertificate);
        return list;
    }

    /**
     * 新增离职证明
     * 需要注意编码重复校验
     *
     * @param hrDimissionCertificate 离职证明
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertHrDimissionCertificate(HrDimissionCertificate hrDimissionCertificate) {
        // 写默认值
        hrDimissionCertificate.setUpdaterAccount(null).setUpdateDate(null);
        hrDimissionCertificate.setSignInStatus("WQS");
        hrDimissionCertificate.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(hrDimissionCertificate.getHandleStatus())) {
            hrDimissionCertificate.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = hrDimissionCertificateMapper.insert(hrDimissionCertificate);
        if (row > 0) {
            if (CollectionUtil.isNotEmpty(hrDimissionCertificate.getAttachmentList())){
                hrDimissionCertificate.getAttachmentList().forEach(item->{
                    item.setDimissionCertificateSid (hrDimissionCertificate.getDimissionCertificateSid ());
                });
                hrDimissionCertificateAttachMapper.inserts(hrDimissionCertificate.getAttachmentList());
            }
            //插入日志
            List<OperMsg> msgList;
            msgList = BeanUtils.eq(new HrDimissionCertificate(), hrDimissionCertificate);
            MongodbDeal.insert(hrDimissionCertificate.getDimissionCertificateSid (), hrDimissionCertificate.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更离职证明
     *
     * @param hrDimissionCertificate 离职证明
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeHrDimissionCertificate(HrDimissionCertificate hrDimissionCertificate) {
        // 写默认值DimissionCertificateSid
        HrDimissionCertificate response = hrDimissionCertificateMapper.selectHrDimissionCertificateById(hrDimissionCertificate.getDimissionCertificateSid ());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, hrDimissionCertificate);
        if (CollectionUtil.isNotEmpty(msgList)) {
            hrDimissionCertificate.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            hrDimissionCertificate.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = hrDimissionCertificateMapper.updateAllById(hrDimissionCertificate);
        if (row > 0) {
            addAttach(hrDimissionCertificate);
            if (HandleStatus.SUBMIT.getCode().equals(hrDimissionCertificate.getHandleStatus())){
                // 附件清单
                updateAttach(hrDimissionCertificate);
                //插入日志
                MongodbUtil.insertUserLog(hrDimissionCertificate.getDimissionCertificateSid (), BusinessType.CHANGE.getValue(), response, hrDimissionCertificate, TITLE);

            }
        }
        return row;
    }

    /**
     * 批量删除离职证明
     *
     * @param dimissionCertificateSids 需要删除的离职证明ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteHrDimissionCertificateByIds(List<Long> dimissionCertificateSids) {
        List<HrDimissionCertificate> list = hrDimissionCertificateMapper.selectList(new QueryWrapper<HrDimissionCertificate>()
                .lambda().in(HrDimissionCertificate::getDimissionCertificateSid, dimissionCertificateSids));
        int row = hrDimissionCertificateMapper.deleteBatchIds(dimissionCertificateSids);
        if (row > 0) {
            // 删除附件
            hrDimissionCertificateAttachMapper.delete(new QueryWrapper<HrDimissionCertificateAttach>().lambda()
                    .in(HrDimissionCertificateAttach::getDimissionCertificateSid, dimissionCertificateSids));
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new HrDimissionCertificate());
                MongodbUtil.insertUserLog(o.getDimissionCertificateSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param hrDimissionCertificate
     * @return
     */
    @Override
    public int check(HrDimissionCertificate hrDimissionCertificate) {
        int row = 0;
        Long[] sids = hrDimissionCertificate.getDimissionCertificateSidList();
        if (sids != null && sids.length > 0) {
            row = hrDimissionCertificateMapper.update(null, new UpdateWrapper<HrDimissionCertificate>().lambda()
                    .set(HrDimissionCertificate::getHandleStatus, hrDimissionCertificate.getHandleStatus())
                    .set(HrDimissionCertificate::getConfirmDate, new Date())
                    .set(HrDimissionCertificate::getConfirmerAccount,ApiThreadLocalUtil.get().getUsername())
                    .set(HrDimissionCertificate::getUpdateDate, new Date())
                    .set(HrDimissionCertificate::getUpdaterAccount, ApiThreadLocalUtil.get().getUsername())
                    .in(HrDimissionCertificate::getDimissionCertificateSid , sids));
            //删除代办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid,sids)
                    .eq(SysTodoTask::getTaskCategory,ConstantsEms.TODO_TASK_DB));
            //插入日志
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, hrDimissionCertificate.getHandleStatus(), null, TITLE, null);
            }
            //如果是审批通过
            if (ConstantsEms.CHECK_STATUS.equals(hrDimissionCertificate.getHandleStatus())){
                List<HrDimissionCertificate> recordList = hrDimissionCertificateMapper.selectList(new QueryWrapper<HrDimissionCertificate>().lambda()
                        .in(HrDimissionCertificate::getDimissionCertificateSid ,sids));
                if (CollectionUtil.isNotEmpty(recordList)){
                    recordList.forEach(record->{

                    });
                }
            }
        }
        return row;
    }
    /**
     * 离职证明签收
     */
    @Override

    @Transactional(rollbackFor = Exception.class)
    public int signHrDimissionCertificateById(HrDimissionCertificate hrDimissionCertificate) {
        LambdaUpdateWrapper<HrDimissionCertificate> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(HrDimissionCertificate::getDimissionCertificateSid ,hrDimissionCertificate.getDimissionCertificateSids())
                .set(HrDimissionCertificate::getSignInStatus, hrDimissionCertificate.getSignInStatus());
        int row = hrDimissionCertificateMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 处理附件
     *
     * @param hrDimissionCertificate
     * @return
     */
    public void addAttach(HrDimissionCertificate hrDimissionCertificate){
        hrDimissionCertificateAttachMapper.delete(new QueryWrapper<HrDimissionCertificateAttach>().lambda()
                .eq(HrDimissionCertificateAttach::getDimissionCertificateSid ,hrDimissionCertificate.getDimissionCertificateSid ()));
        if (CollectionUtil.isNotEmpty(hrDimissionCertificate.getAttachmentList())){
            hrDimissionCertificate.getAttachmentList().forEach(item->{
                item.setDimissionCertificateSid (hrDimissionCertificate.getDimissionCertificateSid ());
            });
            hrDimissionCertificateAttachMapper.inserts(hrDimissionCertificate.getAttachmentList());
        }
    }

    /**
     * 批量修改附件信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAttach(HrDimissionCertificate record) {
        // 先删后加
        hrDimissionCertificateAttachMapper.delete(new QueryWrapper<HrDimissionCertificateAttach>().lambda()
                .eq(HrDimissionCertificateAttach::getDimissionCertificateSid , record.getDimissionCertificateSid ()));
        if (CollectionUtil.isNotEmpty(record.getAttachmentList())) {
            record.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getDimissionCertificateSid () == null) {
                    att.setClientId(ApiThreadLocalUtil.get().getSysUser().getClientId())
                            .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    att.setDimissionCertificateSid (record.getDimissionCertificateSid ());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            hrDimissionCertificateAttachMapper.inserts(record.getAttachmentList());
        }
    }


}
