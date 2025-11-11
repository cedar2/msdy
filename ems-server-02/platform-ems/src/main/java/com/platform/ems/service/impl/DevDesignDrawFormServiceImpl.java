package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.DevDesignDrawForm;
import com.platform.ems.domain.DevDesignDrawFormAttach;
import com.platform.ems.enums.FormType;
import com.platform.ems.mapper.DevDesignDrawFormAttachMapper;
import com.platform.ems.mapper.DevDesignDrawFormMapper;
import com.platform.ems.service.IBasMaterialService;
import com.platform.ems.service.IDevDesignDrawFormService;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.flowable.domain.vo.FormParameter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 图稿批复单Service业务层处理
 *
 * @author qhq
 * @date 2021-11-05
 */
@Service
@SuppressWarnings("all")
public class DevDesignDrawFormServiceImpl extends ServiceImpl<DevDesignDrawFormMapper,DevDesignDrawForm>  implements IDevDesignDrawFormService {
    @Autowired
    private DevDesignDrawFormMapper devDesignDrawFormMapper;
    @Autowired
    private DevDesignDrawFormAttachMapper devDesignDrawFormAttachMapper;
    @Autowired
    private IBasMaterialService materialService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IWorkFlowService workFlowService;


    private static final String TITLE = "图稿批复单";
    /**
     * 查询图稿批复单
     *
     * @param designDrawFormSid 图稿批复单ID
     * @return 图稿批复单
     */
    @Override
    public DevDesignDrawForm selectDevDesignDrawFormById(Long designDrawFormSid) {
        DevDesignDrawForm devDesignDrawForm = devDesignDrawFormMapper.selectDevDesignDrawFormById(designDrawFormSid);
        if(devDesignDrawForm==null){
            return devDesignDrawForm;
        }
        List<DevDesignDrawFormAttach> ath =
                devDesignDrawFormAttachMapper.selectDevDesignDrawFormAttachList(new DevDesignDrawFormAttach().setDesignDrawFormSid(designDrawFormSid));
        BasMaterial material = materialService.selectBasMaterialById(devDesignDrawForm.getProductSid());
        devDesignDrawForm.setMaterial(material);
        MongodbUtil.find(devDesignDrawForm);
        devDesignDrawForm.setAthList(ath);
        return  devDesignDrawForm;
    }

    /**
     * 查询图稿批复单列表
     *
     * @param devDesignDrawForm 图稿批复单
     * @return 图稿批复单
     */
    @Override
    public List<DevDesignDrawForm> selectDevDesignDrawFormList(DevDesignDrawForm devDesignDrawForm) {
        List<DevDesignDrawForm> response = new ArrayList<>();
        List<DevDesignDrawForm> formList = devDesignDrawFormMapper.selectDevDesignDrawFormList(devDesignDrawForm);
        BasMaterial bm = new BasMaterial();
        bm.setSampleCodeSelf(devDesignDrawForm.getSampleCodeSelf());
        bm.setProductSeasonSidList(devDesignDrawForm.getProductSeasonSidList());
        bm.setSeasonList(devDesignDrawForm.getSeasonList());
        bm.setDesignerAccountList(devDesignDrawForm.getDesignerAccountList());
        bm.setModelTypeList(devDesignDrawForm.getModelTypeList());
        List<BasMaterial> bmList = materialService.selectBasMaterialList(bm);
        if(bmList!=null||bmList.size()>0){
            for(DevDesignDrawForm form : formList){
                for(BasMaterial m : bmList){
                    if(m.getMaterialSid().equals(form.getProductSid())){
                        form.setMaterial(m);
                        response.add(form);
                        break;
                    }else{
                        continue;
                    }
                }
            }
            return response;
        }else{
            return formList;
        }
    }

    /**
     * 新增图稿批复单
     * 需要注意编码重复校验
     * @param devDesignDrawForm 图稿批复单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult insertDevDesignDrawForm(DevDesignDrawForm devDesignDrawForm) {
        if(null==devDesignDrawForm.getDesignDrawFormSid()){
            List<DevDesignDrawFormAttach> athList = devDesignDrawForm.getAthList();
//            if(CollectionUtils.isEmpty(athList)){
//                return AjaxResult.error("附件不能为空！");
//            }
            int row = devDesignDrawFormMapper.insert(devDesignDrawForm);
            if(row>0){
                if(athList!=null&&athList.size()>0){
                    for(DevDesignDrawFormAttach ath : athList){
                        ath.setDesignDrawFormSid(devDesignDrawForm.getDesignDrawFormSid());
                        devDesignDrawFormAttachMapper.insert(ath);
                    }
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(devDesignDrawForm.getDesignDrawFormSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
                Submit submit = new Submit();
                submit.setStartUserId(String.valueOf(ApiThreadLocalUtil.get().getUserid()));
                submit.setFormType(FormType.TGPF.getCode());
                List<FormParameter> formParameters = new ArrayList<>();
                FormParameter formParameter = new FormParameter();
                formParameter.setFormId(String.valueOf(devDesignDrawForm.getDesignDrawFormSid()));
                formParameter.setFormCode(String.valueOf(devDesignDrawForm.getDesignDrawFormCode()));
                formParameter.setParentId(String.valueOf(devDesignDrawForm.getDesignDrawFormSid()));
                formParameters.add(formParameter);
                submit.setFormParameters(formParameters);

                return workFlowService.submitByItem(submit);
            }
        }else{
//            if(!attachmentIsExist(devDesignDrawForm.getDesignDrawFormSid())){
//                return AjaxResult.error("附件不能为空！");
//            }
            devDesignDrawFormMapper.updateById(devDesignDrawForm);
            List<DevDesignDrawFormAttach> athList = devDesignDrawForm.getAthList();
            if (CollUtil.isNotEmpty(athList)) {
                for(DevDesignDrawFormAttach ath : athList){
                    deleteAttach(devDesignDrawForm);
                    ath.setDesignDrawFormSid(devDesignDrawForm.getDesignDrawFormSid());
                    devDesignDrawFormAttachMapper.insert(ath);
                }
            } else {
                deleteAttach(devDesignDrawForm);
            }
            Submit submit = new Submit();
            submit.setStartUserId(String.valueOf(ApiThreadLocalUtil.get().getUserid()));
            submit.setFormType(FormType.TGPF.getCode());
            List<FormParameter> formParameters = new ArrayList<>();
            FormParameter formParameter = new FormParameter();
            formParameter.setFormId(String.valueOf(devDesignDrawForm.getDesignDrawFormSid()));
            formParameter.setFormCode(String.valueOf(devDesignDrawForm.getDesignDrawFormCode()));
            formParameter.setParentId(String.valueOf(devDesignDrawForm.getDesignDrawFormSid()));
            formParameters.add(formParameter);
            submit.setFormParameters(formParameters);

            return workFlowService.submitByItem(submit);
        }
        return AjaxResult.error("提交失败！");
    }

    private void deleteAttach(DevDesignDrawForm devDesignDrawForm) {
        devDesignDrawFormAttachMapper.delete(new UpdateWrapper<DevDesignDrawFormAttach>().lambda()
                .eq(DevDesignDrawFormAttach::getDesignDrawFormSid, devDesignDrawForm.getDesignDrawFormSid()));
    }

    /**
     * 修改图稿批复单
     *
     * @param devDesignDrawForm 图稿批复单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDevDesignDrawForm(DevDesignDrawForm devDesignDrawForm) {
        DevDesignDrawForm response = devDesignDrawFormMapper.selectDevDesignDrawFormById(devDesignDrawForm.getDesignDrawFormSid());
        QueryWrapper<DevDesignDrawFormAttach> attachQueryWrapper = new QueryWrapper<>();
        attachQueryWrapper.eq("design_draw_form_sid",devDesignDrawForm.getDesignDrawFormSid());
        devDesignDrawFormAttachMapper.delete(attachQueryWrapper);
        List<DevDesignDrawFormAttach> athList = devDesignDrawForm.getAthList();
        if(athList!=null&&athList.size()>0){
            for(DevDesignDrawFormAttach ath : athList){
                ath.setDesignDrawFormSid(devDesignDrawForm.getDesignDrawFormSid());
                devDesignDrawFormAttachMapper.insert(ath);
            }
        }
        int row=devDesignDrawFormMapper.updateById(devDesignDrawForm);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(devDesignDrawForm.getDesignDrawFormSid(), BusinessType.UPDATE.ordinal(), response,devDesignDrawForm,TITLE);
        }
        return row;
    }

    public int updateStatus(DevDesignDrawForm devDesignDrawForm){
        int row = devDesignDrawFormMapper.updateById(devDesignDrawForm);
        return row;
    }

    /**
     * 变更图稿批复单
     *
     * @param devDesignDrawForm 图稿批复单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDevDesignDrawForm(DevDesignDrawForm devDesignDrawForm) {
        DevDesignDrawForm response = devDesignDrawFormMapper.selectDevDesignDrawFormById(devDesignDrawForm.getDesignDrawFormSid());
        QueryWrapper<DevDesignDrawFormAttach> attachQueryWrapper = new QueryWrapper<>();
        attachQueryWrapper.eq("design_draw_form_sid",devDesignDrawForm.getDesignDrawFormSid());
        devDesignDrawFormAttachMapper.delete(attachQueryWrapper);
        List<DevDesignDrawFormAttach> athList = devDesignDrawForm.getAthList();
        if(athList!=null&&athList.size()>0){
            for(DevDesignDrawFormAttach ath : athList){
                ath.setDesignDrawFormSid(devDesignDrawForm.getDesignDrawFormSid());
                devDesignDrawFormAttachMapper.insert(ath);
            }
        }
                                                                    int row=devDesignDrawFormMapper.updateAllById(devDesignDrawForm);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(devDesignDrawForm.getDesignDrawFormSid(), BusinessType.CHANGE.ordinal(), response,devDesignDrawForm,TITLE);
        }
        return row;
    }

    /**
     * 批量删除图稿批复单
     *
     * @param designDrawFormSids 需要删除的图稿批复单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDevDesignDrawFormByIds(List<Long> designDrawFormSids) {
        for(Long sid : designDrawFormSids){
            QueryWrapper<DevDesignDrawFormAttach> attachQueryWrapper = new QueryWrapper<>();
            attachQueryWrapper.eq("design_draw_form_sid",sid);
            devDesignDrawFormAttachMapper.delete(attachQueryWrapper);
            devDesignDrawFormMapper.deleteById(sid);
        }
        return 1;
    }


    /**
     *更改确认状态
     * @param devDesignDrawForm
     * @return
     */
    @Override
    public int check(DevDesignDrawForm devDesignDrawForm){
        int row=0;
        Long[] sids=devDesignDrawForm.getDesignDrawFormSidList();
        if(sids!=null&&sids.length>0){
            row=devDesignDrawFormMapper.update(null,new UpdateWrapper<DevDesignDrawForm>().lambda().set(DevDesignDrawForm::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .set(DevDesignDrawForm::getConfirmDate,devDesignDrawForm.getConfirmDate())
                    .set(DevDesignDrawForm::getConfirmerAccount,devDesignDrawForm.getConfirmerAccount())
                    .in(DevDesignDrawForm::getDesignDrawFormSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 校验单据是否存在附件
     * @param sid
     * @return
     */
    @Override
    public boolean attachmentIsExist(Long sid){
        QueryWrapper<DevDesignDrawFormAttach> attachQueryWrapper = new QueryWrapper<>();
        attachQueryWrapper.eq("design_draw_form_sid",sid);
        List<DevDesignDrawFormAttach> athList = devDesignDrawFormAttachMapper.selectList(attachQueryWrapper);
        if(CollectionUtils.isNotEmpty(athList)){
            return true;
        }
        return false;
    }

    /**
     * 是否已创建图稿批复
     */
    @Override
    public DevDesignDrawForm verify(Long productSid) {
        List<DevDesignDrawForm> list = devDesignDrawFormMapper.selectList(new QueryWrapper<DevDesignDrawForm>().lambda()
                .eq(DevDesignDrawForm::getProductSid, productSid));
        DevDesignDrawForm designDrawForm = new DevDesignDrawForm();
        if (CollUtil.isNotEmpty(list)) {
            for (DevDesignDrawForm devDesignDrawForm : list) {
                designDrawForm = selectDevDesignDrawFormById(devDesignDrawForm.getDesignDrawFormSid());
            }
        }
        return designDrawForm;
    }
}
