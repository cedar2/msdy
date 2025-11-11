package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.DevMakeSampleForm;
import com.platform.ems.domain.DevSampleReviewForm;
import com.platform.ems.domain.DevSampleReviewFormAttach;
import com.platform.ems.mapper.DevSampleReviewFormAttachMapper;
import com.platform.ems.mapper.DevSampleReviewFormMapper;
import com.platform.ems.service.IDevSampleReviewFormService;
import com.platform.ems.util.MongodbUtil;
import com.platform.api.service.RemoteSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 样品评审单Service业务层处理
 *
 * @author linhongwei
 * @date 2022-03-23
 */
@Service
@SuppressWarnings("all")
public class DevSampleReviewFormServiceImpl extends ServiceImpl<DevSampleReviewFormMapper, DevSampleReviewForm> implements IDevSampleReviewFormService {
    @Autowired
    private DevSampleReviewFormMapper devSampleReviewFormMapper;
    @Autowired
    private DevSampleReviewFormAttachMapper attachMapper;
    @Autowired
    private RemoteSystemService remoteSystemService;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "样品评审单";

    /**
     * 查询样品评审单
     *
     * @param sampleReviewFormSid 样品评审单ID
     * @return 样品评审单
     */
    @Override
    public DevSampleReviewForm selectDevSampleReviewFormById(Long sampleReviewFormSid) {
        DevSampleReviewForm devSampleReviewForm = devSampleReviewFormMapper.selectDevSampleReviewFormById(sampleReviewFormSid);
        if (devSampleReviewForm == null) {
            return null;
        }
        List<DevSampleReviewFormAttach> attachList =
                attachMapper.selectDevSampleReviewFormAttachList(new DevSampleReviewFormAttach().setSampleReviewFormSid(sampleReviewFormSid));
        devSampleReviewForm.setAttachList(attachList);
        MongodbUtil.find(devSampleReviewForm);
        return devSampleReviewForm;
    }

    /**
     * 查询样品评审单列表
     *
     * @param devSampleReviewForm 样品评审单
     * @return 样品评审单
     */
    @Override
    public List<DevSampleReviewForm> selectDevSampleReviewFormList(DevSampleReviewForm devSampleReviewForm) {
        return devSampleReviewFormMapper.selectDevSampleReviewFormList(devSampleReviewForm);
    }

    /**
     * 新增样品评审单
     * 需要注意编码重复校验
     *
     * @param devSampleReviewForm 样品评审单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DevSampleReviewForm insertDevSampleReviewForm(DevSampleReviewForm devSampleReviewForm) {
        List<DevSampleReviewForm> reviewFormList = getDevSampleReviewForms(devSampleReviewForm);
        if (CollUtil.isNotEmpty(reviewFormList)) {
            //样品已存在相同评审阶段评审单
            devSampleReviewForm = reviewFormList.get(0);
            devSampleReviewForm.setIsRepetition(ConstantsEms.YES_OR_NO_Y);
        } else {
            //不存在则新增
            int row = devSampleReviewFormMapper.insert(devSampleReviewForm);
            if (row > 0) {
                List<DevSampleReviewFormAttach> attachList = devSampleReviewForm.getAttachList();
                if (CollUtil.isNotEmpty(attachList)) {
                    addDevSampleReviewFormAttach(devSampleReviewForm, attachList);
                }
                devSampleReviewForm = devSampleReviewFormMapper.selectDevSampleReviewFormById(devSampleReviewForm.getSampleReviewFormSid());
                devSampleReviewForm.setIsRepetition(ConstantsEms.YES_OR_NO_N);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(devSampleReviewForm.getSampleReviewFormSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            }
        }
        return devSampleReviewForm;
    }

    private List<DevSampleReviewForm> getDevSampleReviewForms(DevSampleReviewForm devSampleReviewForm) {
        return devSampleReviewFormMapper.selectList(new QueryWrapper<DevSampleReviewForm>().lambda()
                .eq(DevSampleReviewForm::getProductSid, devSampleReviewForm.getProductSid())
                .eq(DevSampleReviewForm::getReviewStage, devSampleReviewForm.getReviewStage()));
    }

    /**
     * 样品评审单-附件
     */
    private void addDevSampleReviewFormAttach(DevSampleReviewForm devSampleReviewForm, List<DevSampleReviewFormAttach> attachList) {
        deleteAttach(devSampleReviewForm);
        attachList.forEach(o -> {
            o.setSampleReviewFormSid(devSampleReviewForm.getSampleReviewFormSid());
        });
        attachMapper.inserts(attachList);
    }

    /**
     * 删除附件
     */
    private void deleteAttach(DevSampleReviewForm devSampleReviewForm) {
        attachMapper.delete(new UpdateWrapper<DevSampleReviewFormAttach>().lambda()
                .eq(DevSampleReviewFormAttach::getSampleReviewFormSid, devSampleReviewForm.getSampleReviewFormSid()));
    }

    /**
     * 修改样品评审单
     *
     * @param devSampleReviewForm 样品评审单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DevSampleReviewForm updateDevSampleReviewForm(DevSampleReviewForm devSampleReviewForm) {
        List<DevSampleReviewForm> reviewFormList = getDevSampleReviewForms(devSampleReviewForm);
        if (CollUtil.isNotEmpty(reviewFormList)) {
            DevSampleReviewForm form = reviewFormList.get(0);
            //样品已存在相同评审阶段评审单
            if (!form.getSampleReviewFormSid().equals(devSampleReviewForm.getSampleReviewFormSid())) {
                form.setIsRepetition(ConstantsEms.YES_OR_NO_Y);
                devSampleReviewForm = form;
            } else {
                updateBills(devSampleReviewForm);
            }
        } else {
            updateBills(devSampleReviewForm);
        }
        return devSampleReviewForm;
    }

    /**
     * 修改样品评审单
     */
    private void updateBills(DevSampleReviewForm devSampleReviewForm) {
        DevSampleReviewForm response = devSampleReviewFormMapper.selectDevSampleReviewFormById(devSampleReviewForm.getSampleReviewFormSid());
        int row = devSampleReviewFormMapper.updateAllById(devSampleReviewForm);
        if (row > 0) {
            devSampleReviewForm.setIsRepetition(ConstantsEms.YES_OR_NO_N);
            List<DevSampleReviewFormAttach> attachList = devSampleReviewForm.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                attachList.forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addDevSampleReviewFormAttach(devSampleReviewForm, attachList);
            } else {
                deleteAttach(devSampleReviewForm);
            }
            //插入日志
            MongodbUtil.insertUserLog(devSampleReviewForm.getSampleReviewFormSid(), BusinessType.UPDATE.getValue(), response, devSampleReviewForm, TITLE);
        }
    }

    /**
     * 变更样品评审单
     *
     * @param devSampleReviewForm 样品评审单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDevSampleReviewForm(DevSampleReviewForm devSampleReviewForm) {
        DevSampleReviewForm response = devSampleReviewFormMapper.selectDevSampleReviewFormById(devSampleReviewForm.getSampleReviewFormSid());
        int row = devSampleReviewFormMapper.updateAllById(devSampleReviewForm);
        if (row > 0) {
            List<DevSampleReviewFormAttach> attachList = devSampleReviewForm.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                attachList.forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addDevSampleReviewFormAttach(devSampleReviewForm, attachList);
            } else {
                deleteAttach(devSampleReviewForm);
            }
            //插入日志
            MongodbUtil.insertUserLog(devSampleReviewForm.getSampleReviewFormSid(), BusinessType.CHANGE.getValue(), response, devSampleReviewForm, TITLE);
        }
        return row;
    }

    /**
     * 批量删除样品评审单
     *
     * @param sampleReviewFormSids 需要删除的样品评审单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDevSampleReviewFormByIds(List<Long> sampleReviewFormSids) {
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(ConstantsEms.SAVA_STATUS);
        handleStatusList.add(ConstantsEms.BACK_STATUS);
        Integer count = devSampleReviewFormMapper.selectCount(new QueryWrapper<DevSampleReviewForm>().lambda()
                .in(DevSampleReviewForm::getHandleStatus, handleStatusList)
                .in(DevSampleReviewForm::getSampleReviewFormSid, sampleReviewFormSids));
        if (count != sampleReviewFormSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT_APPROVE);
        }
        attachMapper.delete(new UpdateWrapper<DevSampleReviewFormAttach>().lambda()
                .in(DevSampleReviewFormAttach::getSampleReviewFormSid, sampleReviewFormSids));
        return devSampleReviewFormMapper.deleteBatchIds(sampleReviewFormSids);
    }

    /**
     * 更改确认状态
     *
     * @param devSampleReviewForm
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(DevSampleReviewForm devSampleReviewForm) {
        int row = 0;
        Long[] sids = devSampleReviewForm.getSampleReviewFormSidList();
        if (sids != null && sids.length > 0) {
            row = devSampleReviewFormMapper.update(null, new UpdateWrapper<DevSampleReviewForm>().lambda()
                    .set(DevSampleReviewForm::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(DevSampleReviewForm::getConfirmDate, new Date())
                    .set(DevSampleReviewForm::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .in(DevSampleReviewForm::getSampleReviewFormSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    @Override
    public int updateHandleStatus(DevSampleReviewForm devSampleReviewForm) {
        return devSampleReviewFormMapper.updateHandleStatus(devSampleReviewForm);
    }
}
