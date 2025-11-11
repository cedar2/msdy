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
import com.platform.ems.domain.DevMakeSampleFormAttach;
import com.platform.ems.mapper.DevMakeSampleFormAttachMapper;
import com.platform.ems.mapper.DevMakeSampleFormMapper;
import com.platform.ems.service.IDevMakeSampleFormService;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.workflow.service.IWorkFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 打样准许单Service业务层处理
 *
 * @author linhongwei
 * @date 2022-03-24
 */
@Service
@SuppressWarnings("all")
public class DevMakeSampleFormServiceImpl extends ServiceImpl<DevMakeSampleFormMapper, DevMakeSampleForm> implements IDevMakeSampleFormService {
    @Autowired
    private DevMakeSampleFormMapper devMakeSampleFormMapper;
    @Autowired
    private DevMakeSampleFormAttachMapper attachMapper;
    @Autowired
    private IWorkFlowService workFlowService;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "打样准许单";

    /**
     * 查询打样准许单
     *
     * @param makeSampleFormSid 打样准许单ID
     * @return 打样准许单
     */
    @Override
    public DevMakeSampleForm selectDevMakeSampleFormById(Long makeSampleFormSid) {
        DevMakeSampleForm devMakeSampleForm = devMakeSampleFormMapper.selectDevMakeSampleFormById(makeSampleFormSid);
        if (devMakeSampleForm == null) {
            return null;
        }
        List<DevMakeSampleFormAttach> attachList =
                attachMapper.selectDevMakeSampleFormAttachList(new DevMakeSampleFormAttach().setMakeSampleFormSid(makeSampleFormSid));
        devMakeSampleForm.setAttachList(attachList);
        MongodbUtil.find(devMakeSampleForm);
        return devMakeSampleForm;
    }

    /**
     * 查询打样准许单列表
     *
     * @param devMakeSampleForm 打样准许单
     * @return 打样准许单
     */
    @Override
    public List<DevMakeSampleForm> selectDevMakeSampleFormList(DevMakeSampleForm devMakeSampleForm) {
        return devMakeSampleFormMapper.selectDevMakeSampleFormList(devMakeSampleForm);
    }

    /**
     * 新增打样准许单
     * 需要注意编码重复校验
     *
     * @param devMakeSampleForm 打样准许单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DevMakeSampleForm insertDevMakeSampleForm(DevMakeSampleForm devMakeSampleForm) {
        List<DevMakeSampleForm> list = devMakeSampleFormMapper.selectList(new QueryWrapper<DevMakeSampleForm>().lambda()
                .eq(DevMakeSampleForm::getProductSid, devMakeSampleForm.getProductSid()));
        if (CollUtil.isNotEmpty(list)) {
            throw new BaseException("样品已存在打样准许单，请核实！");
        }
        devMakeSampleFormMapper.insert(devMakeSampleForm);
        List<DevMakeSampleFormAttach> attachList = devMakeSampleForm.getAttachList();
        if (CollUtil.isNotEmpty(attachList)) {
            addDevMakeSampleFormAttach(devMakeSampleForm, attachList);
        }
        devMakeSampleForm = devMakeSampleFormMapper.selectDevMakeSampleFormById(devMakeSampleForm.getMakeSampleFormSid());
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbUtil.insertUserLog(devMakeSampleForm.getMakeSampleFormSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        return devMakeSampleForm;
    }

    /**
     * 打样准许单-附件
     */
    private void addDevMakeSampleFormAttach(DevMakeSampleForm devMakeSampleForm, List<DevMakeSampleFormAttach> attachList) {
        deleteAttach(devMakeSampleForm);
        attachList.forEach(o -> {
            o.setMakeSampleFormSid(devMakeSampleForm.getMakeSampleFormSid());
        });
        attachMapper.inserts(attachList);
    }

    /**
     * 删除附件
     */
    private void deleteAttach(DevMakeSampleForm devMakeSampleForm) {
        attachMapper.delete(new UpdateWrapper<DevMakeSampleFormAttach>().lambda()
                .eq(DevMakeSampleFormAttach::getMakeSampleFormSid, devMakeSampleForm.getMakeSampleFormSid()));
    }

    /**
     * 修改打样准许单
     *
     * @param devMakeSampleForm 打样准许单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDevMakeSampleForm(DevMakeSampleForm devMakeSampleForm) {
        DevMakeSampleForm response = devMakeSampleFormMapper.selectDevMakeSampleFormById(devMakeSampleForm.getMakeSampleFormSid());
        devMakeSampleForm.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        int row = devMakeSampleFormMapper.updateAllById(devMakeSampleForm);
        if (row > 0) {
            List<DevMakeSampleFormAttach> attachList = devMakeSampleForm.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                attachList.forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addDevMakeSampleFormAttach(devMakeSampleForm, attachList);
            } else {
                deleteAttach(devMakeSampleForm);
            }
            /*Submit submit = new Submit();
            submit.setStartUserId(String.valueOf(ApiThreadLocalUtil.get().getUserid()));
            submit.setFormType(FormType.TGPF.getCode());
            List<FormParameter> formParameters = new ArrayList<>();
            FormParameter formParameter = new FormParameter();
            formParameter.setFormId(String.valueOf(devMakeSampleForm.getMakeSampleFormSid()));
            formParameter.setFormCode(String.valueOf(devMakeSampleForm.getMakeSampleFormCode()));
            formParameter.setParentId(String.valueOf(devMakeSampleForm.getMakeSampleFormSid()));
            formParameters.add(formParameter);
            submit.setFormParameters(formParameters);
            workFlowService.submitByItem(submit);*/
            //插入日志
            MongodbUtil.insertUserLog(devMakeSampleForm.getMakeSampleFormSid(), BusinessType.UPDATE.getValue(), response, devMakeSampleForm, TITLE);
        }
        return row;
    }

    /**
     * 变更打样准许单
     *
     * @param devMakeSampleForm 打样准许单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDevMakeSampleForm(DevMakeSampleForm devMakeSampleForm) {
        DevMakeSampleForm response = devMakeSampleFormMapper.selectDevMakeSampleFormById(devMakeSampleForm.getMakeSampleFormSid());
        int row = devMakeSampleFormMapper.updateAllById(devMakeSampleForm);
        if (row > 0) {
            List<DevMakeSampleFormAttach> attachList = devMakeSampleForm.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                attachList.forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addDevMakeSampleFormAttach(devMakeSampleForm, attachList);
            } else {
                deleteAttach(devMakeSampleForm);
            }
            //插入日志
            MongodbUtil.insertUserLog(devMakeSampleForm.getMakeSampleFormSid(), BusinessType.CHANGE.getValue(), response, devMakeSampleForm, TITLE);
        }
        return row;
    }

    /**
     * 批量删除打样准许单
     *
     * @param makeSampleFormSids 需要删除的打样准许单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDevMakeSampleFormByIds(List<Long> makeSampleFormSids) {
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(ConstantsEms.SAVA_STATUS);
        handleStatusList.add(ConstantsEms.BACK_STATUS);
        Integer count = devMakeSampleFormMapper.selectCount(new QueryWrapper<DevMakeSampleForm>().lambda()
                .in(DevMakeSampleForm::getHandleStatus, handleStatusList)
                .in(DevMakeSampleForm::getMakeSampleFormSid, makeSampleFormSids));
        if (count != makeSampleFormSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT_APPROVE);
        }
        attachMapper.delete(new UpdateWrapper<DevMakeSampleFormAttach>().lambda()
                .in(DevMakeSampleFormAttach::getMakeSampleFormSid, makeSampleFormSids));
        return devMakeSampleFormMapper.deleteBatchIds(makeSampleFormSids);
    }

    /**
     * 更改确认状态
     *
     * @param devMakeSampleForm
     * @return
     */
    @Override
    public int check(DevMakeSampleForm devMakeSampleForm) {
        int row = 0;
        Long[] sids = devMakeSampleForm.getMakeSampleFormSidList();
        if (sids != null && sids.length > 0) {
            row = devMakeSampleFormMapper.update(null, new UpdateWrapper<DevMakeSampleForm>().lambda()
                    .set(DevMakeSampleForm::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(DevMakeSampleForm::getConfirmDate, new Date())
                    .set(DevMakeSampleForm::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .in(DevMakeSampleForm::getMakeSampleFormSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    @Override
    public int updateHandleStatus(DevMakeSampleForm devMakeSampleForm) {
        return devMakeSampleFormMapper.updateHandleStatus(devMakeSampleForm);
    }
}
