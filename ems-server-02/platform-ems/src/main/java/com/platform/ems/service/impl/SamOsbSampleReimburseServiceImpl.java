package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.SamOsbSampleReimburseReportRequert;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ISamOsbSampleReimburseService;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 外采样报销单-主Service业务层处理
 *
 * @author qhq
 * @date 2021-12-28
 */
@Service
@SuppressWarnings("all")
public class SamOsbSampleReimburseServiceImpl extends ServiceImpl<SamOsbSampleReimburseMapper,SamOsbSampleReimburse>  implements ISamOsbSampleReimburseService {
    @Autowired
    private SamOsbSampleReimburseMapper samOsbSampleReimburseMapper;
    @Autowired
    private SamOsbSampleReimburseItemMapper itemMapper;
    @Autowired
    private SamOsbSampleReimburseAttachMapper attachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private BasMaterialMapper materialMapper;

    private static final String TITLE = "外采样报销单-主";
    /**
     * 查询外采样报销单-主
     *
     * @param reimburseSid 外采样报销单-主ID
     * @return 外采样报销单-主
     */
    @Override
    public SamOsbSampleReimburse selectSamOsbSampleReimburseById(Long reimburseSid) {
        SamOsbSampleReimburse samOsbSampleReimburse = samOsbSampleReimburseMapper.selectSamOsbSampleReimburseById(reimburseSid);
        SamOsbSampleReimburseItem itemRequest = new SamOsbSampleReimburseItem();
        itemRequest.setReimburseSid(reimburseSid);
        List<SamOsbSampleReimburseItem> itemList = itemMapper.selectSamOsbSampleReimburseItemList(itemRequest);
        itemList.stream().sorted(Comparator.comparing(SamOsbSampleReimburseItem::getItemNum)).collect(Collectors.toList());
        samOsbSampleReimburse.setItemList(itemList);
        SamOsbSampleReimburseAttach attachRequest = new SamOsbSampleReimburseAttach();
        attachRequest.setReimburseSid(reimburseSid);
        List<SamOsbSampleReimburseAttach> attachList = attachMapper.selectSamOsbSampleReimburseAttachList(attachRequest);
        samOsbSampleReimburse.setAttachList(attachList);
        MongodbUtil.find(samOsbSampleReimburse);
        return  samOsbSampleReimburse;
    }

    /**
     * 查询外采样报销单-主列表
     *
     * @param samOsbSampleReimburse 外采样报销单-主
     * @return 外采样报销单-主
     */
    @Override
    public List<SamOsbSampleReimburse> selectSamOsbSampleReimburseList(SamOsbSampleReimburse samOsbSampleReimburse) {
        return samOsbSampleReimburseMapper.selectSamOsbSampleReimburseList(samOsbSampleReimburse);
    }

    /**
     * 新增外采样报销单-主
     * 需要注意编码重复校验
     * @param samOsbSampleReimburse 外采样报销单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSamOsbSampleReimburse(SamOsbSampleReimburse samOsbSampleReimburse) {
        int row = samOsbSampleReimburseMapper.insert(samOsbSampleReimburse);
        if(row>0){
            List<SamOsbSampleReimburseItem> itemList = samOsbSampleReimburse.getItemList();
            int num = 0;
            if(itemList!=null&&itemList.size()>0){
                for(SamOsbSampleReimburseItem item : itemList){
                    num++;
                    item.setReimburseSid(samOsbSampleReimburse.getReimburseSid());
                    item.setItemNum(Integer.toUnsignedLong(num));
                    itemMapper.insert(item);
                }
            }
            List<SamOsbSampleReimburseAttach> athList = samOsbSampleReimburse.getAttachList();
            if(athList!=null&&athList.size()>0){
                for(SamOsbSampleReimburseAttach ath : athList){
                    ath.setReimburseSid(samOsbSampleReimburse.getReimburseSid());
                    attachMapper.insert(ath);
                }
            }

            samOsbSampleReimburse = samOsbSampleReimburseMapper.selectSamOsbSampleReimburseById(samOsbSampleReimburse.getReimburseSid());
            //记录待办
            todoTask(samOsbSampleReimburse);
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(samOsbSampleReimburse.getReimburseSid(), BusinessType.INSERT.getValue(), msgList,TITLE);

        }
        return row;
    }

    /**
     * 记录待办
     */
    private void todoTask(SamOsbSampleReimburse samOsbSampleReimburse) {
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(samOsbSampleReimburse.getHandleStatus()) || ConstantsEms.BACK_STATUS.equals(samOsbSampleReimburse.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.WCYBXD)
                    .setDocumentSid(samOsbSampleReimburse.getReimburseSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollUtil.isEmpty(sysTodoTaskList)) {
                if (ConstantsEms.SAVA_STATUS.equals(samOsbSampleReimburse.getHandleStatus())) {
                    sysTodoTask.setTitle("外采样报销单" + samOsbSampleReimburse.getReimburseCode() + "当前是保存状态，请及时处理！");
                } else {
                    sysTodoTask.setTitle("外采样报销单" + samOsbSampleReimburse.getReimburseCode() + "当前是已退回状态，请及时处理！");
                }
                sysTodoTask.setDocumentCode(String.valueOf(samOsbSampleReimburse.getReimburseCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(samOsbSampleReimburse);
        }
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(SamOsbSampleReimburse samOsbSampleReimburse) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, samOsbSampleReimburse.getReimburseSid()));
        if (CollUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, samOsbSampleReimburse.getReimburseSid()));
        }
    }

    /**
     * 修改外采样报销单-主
     *
     * @param samOsbSampleReimburse 外采样报销单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSamOsbSampleReimburse(SamOsbSampleReimburse samOsbSampleReimburse) {
        samOsbSampleReimburse.setUpdateDate(new Date());
        samOsbSampleReimburse.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        int row = samOsbSampleReimburseMapper.updateById(samOsbSampleReimburse);
        if(row>0){
            List<SamOsbSampleReimburseItem> itemList = samOsbSampleReimburse.getItemList();
            List<SamOsbSampleReimburseItem> addList = new ArrayList<>();
            List<Integer> itemNumList = new ArrayList<>();
            for(int i=1;i<itemList.size()+1;i++){
                itemNumList.add(i);
            }
            QueryWrapper<SamOsbSampleReimburseItem> itemqw = new QueryWrapper<>();
            itemqw.eq("reimburse_sid",samOsbSampleReimburse.getReimburseSid());
            itemMapper.delete(itemqw);
            //修改
            for(SamOsbSampleReimburseItem item : itemList){
                if(item.getReimburseItemSid()!=null){
                    item.setUpdateDate(new Date());
                    item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    itemMapper.insert(item);
                    itemNumList.remove((Integer) item.getItemNum().intValue());
                }else{
                    addList.add(item);
                }
            }
            //新 增
            for(SamOsbSampleReimburseItem item : addList){
                item.setCreateDate(new Date());
                item.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                item.setReimburseSid(samOsbSampleReimburse.getReimburseSid());
                Long minNum = Long.parseLong(Collections.min(itemNumList).toString());
                item.setItemNum(minNum);
                itemMapper.insert(item);
                itemNumList.remove((Integer)minNum.intValue());
            }
            QueryWrapper<SamOsbSampleReimburseAttach> athqw = new QueryWrapper<>();
            athqw.eq("reimburse_sid",samOsbSampleReimburse.getReimburseSid());
            attachMapper.delete(athqw);
            List<SamOsbSampleReimburseAttach> athList = samOsbSampleReimburse.getAttachList();
            for(SamOsbSampleReimburseAttach ath : athList){
                ath.setReimburseSid(samOsbSampleReimburse.getReimburseSid());
                attachMapper.insert(ath);
            }
        }
        //插入日志
        List<OperMsg> msgList=new ArrayList<>();
        MongodbUtil.insertUserLog(samOsbSampleReimburse.getReimburseSid(), BusinessType.UPDATE.getValue(), msgList,TITLE);
        return row;
    }

    /**
     * 变更外采样报销单-主
     *
     * @param samOsbSampleReimburse 外采样报销单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSamOsbSampleReimburse(SamOsbSampleReimburse samOsbSampleReimburse) {
        samOsbSampleReimburse.setUpdateDate(new Date());
        samOsbSampleReimburse.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        int row = samOsbSampleReimburseMapper.updateById(samOsbSampleReimburse);
        if(row>0){
            List<SamOsbSampleReimburseItem> itemList = samOsbSampleReimburse.getItemList();
            List<SamOsbSampleReimburseItem> addList = new ArrayList<>();
            List<Integer> itemNumList = new ArrayList<>();
            for(int i=0;i<itemList.size();i++){
                itemNumList.add(i);
            }
            //修改
            for(SamOsbSampleReimburseItem item : itemList){
                if(item.getReimburseItemSid()!=null){
                    item.setUpdateDate(new Date());
                    item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    itemMapper.updateById(item);
                    itemNumList.remove(item.getItemNum());
                }else{
                    addList.add(item);
                }
            }
            //新 增
            for(SamOsbSampleReimburseItem item : addList){
                item.setCreateDate(new Date());
                item.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                item.setReimburseSid(samOsbSampleReimburse.getReimburseSid());
                Long minNum = Long.parseLong(Collections.min(itemNumList).toString());
                System.out.println(itemNumList.toString()+"  剩余最小值："+minNum);
                item.setItemNum(minNum);
                itemMapper.insert(item);
                itemNumList.remove(minNum);
            }
            QueryWrapper<SamOsbSampleReimburseAttach> athqw = new QueryWrapper<>();
            athqw.eq("reimburse_sid",samOsbSampleReimburse.getReimburseSid());
            attachMapper.delete(athqw);
            List<SamOsbSampleReimburseAttach> athList = samOsbSampleReimburse.getAttachList();
            for(SamOsbSampleReimburseAttach ath : athList){
                ath.setReimburseSid(samOsbSampleReimburse.getReimburseSid());
                attachMapper.insert(ath);
            }
        }
        //插入日志
        List<OperMsg> msgList=new ArrayList<>();
        MongodbUtil.insertUserLog(samOsbSampleReimburse.getReimburseSid(), BusinessType.CHANGE.getValue(), msgList,TITLE);
        return row;
    }

    /**
     * 批量删除外采样报销单-主
     *
     * @param reimburseSids 需要删除的外采样报销单-主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSamOsbSampleReimburseByIds(List<Long> reimburseSids) {
        SamOsbSampleReimburse samOsbSampleReimburse = new SamOsbSampleReimburse();
        reimburseSids.forEach(sid -> {
            QueryWrapper<SamOsbSampleReimburseItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("reimburse_sid", sid);
            itemMapper.delete(itemWrapper);
            QueryWrapper<SamOsbSampleReimburseAttach> athQueryWrapper = new QueryWrapper<>();
            athQueryWrapper.eq("reimburse_sid", sid);
            attachMapper.delete(athQueryWrapper);
            samOsbSampleReimburseMapper.deleteById(sid);
            samOsbSampleReimburse.setReimburseSid(sid);
            //校验是否存在待办
            checkTodoExist(samOsbSampleReimburse);
        });
        return 1;

    }


    /**
     *更改确认状态
     * @param samOsbSampleReimburse
     * @return
     */
    @Override
    public int check(SamOsbSampleReimburse samOsbSampleReimburse){
        int row=0;
        Long[] sids=samOsbSampleReimburse.getReimburseSidList();
        if(sids!=null&&sids.length>0){
            row=samOsbSampleReimburseMapper.update(null,new UpdateWrapper<SamOsbSampleReimburse>()
                    .lambda()
                    .set(SamOsbSampleReimburse::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .set(SamOsbSampleReimburse::getConfirmDate,new Date())
                    .set(SamOsbSampleReimburse::getConfirmerAccount,ApiThreadLocalUtil.get().getUsername())
                    .in(SamOsbSampleReimburse::getReimburseSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    @Override
    public List<SamOsbSampleReimburseReportRequert> selectReport (SamOsbSampleReimburseReportRequert requert) {
        return samOsbSampleReimburseMapper.selectReport(requert);
    }

    @Override
    public int submitItem(Long formSid){
        try {
            SamOsbSampleReimburse sam = new SamOsbSampleReimburse();
            sam.setHandleStatus( HandleStatus.SUBMIT.getCode());
            sam.setReimburseSid(formSid);
            UpdateWrapper<SamOsbSampleReimburse> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("reimburse_sid",formSid);
            samOsbSampleReimburseMapper.update(sam,updateWrapper);
            sam = selectSamOsbSampleReimburseById(formSid);
            for(SamOsbSampleReimburseItem item : sam.getItemList()){
                BasMaterial material = new BasMaterial();
                material.setReimburseStatus("BXZ");
                UpdateWrapper<BasMaterial> updateWrapper2 = new UpdateWrapper<>();
                updateWrapper2.eq("material_sid",item.getSampleSid());
                materialMapper.update(material,updateWrapper2);
            }
            //校验是否存在待办
            checkTodoExist(sam);
            return 1;
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public int over(Long formSid){
        try {
            SamOsbSampleReimburse sam = new SamOsbSampleReimburse();
            sam.setHandleStatus( HandleStatus.CONFIRMED.getCode());
            UpdateWrapper<SamOsbSampleReimburse> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("reimburse_sid",formSid);
            samOsbSampleReimburseMapper.update(sam,updateWrapper);
            sam = selectSamOsbSampleReimburseById(formSid);
            for(SamOsbSampleReimburseItem item : sam.getItemList()){
                BasMaterial material = new BasMaterial();
                material.setReimburseStatus("YBX");
                UpdateWrapper<BasMaterial> updateWrapper2 = new UpdateWrapper<>();
                updateWrapper2.eq("material_sid",item.getSampleSid());
                materialMapper.update(material,updateWrapper2);
            }
            return 1;
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public int returned(Long formSid){
        try {
            SamOsbSampleReimburse sam = new SamOsbSampleReimburse();
            sam.setHandleStatus( HandleStatus.RETURNED.getCode());
            UpdateWrapper<SamOsbSampleReimburse> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("reimburse_sid",formSid);
            samOsbSampleReimburseMapper.update(sam,updateWrapper);
            sam = selectSamOsbSampleReimburseById(formSid);
            for(SamOsbSampleReimburseItem item : sam.getItemList()){
                BasMaterial material = new BasMaterial();
                material.setReimburseStatus("WBX");
                UpdateWrapper<BasMaterial> updateWrapper2 = new UpdateWrapper<>();
                updateWrapper2.eq("material_sid",item.getSampleSid());
                materialMapper.update(material,updateWrapper2);
            }
            //记录待办
            todoTask(sam);
            return 1;
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public boolean itemValidation(Long sid){
        try {
            SamOsbSampleReimburse sam = selectSamOsbSampleReimburseById(sid);
            if(sam.getItemList()!=null && sam.getItemList().size()>0){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public SamOsbSampleReimburse isCreate(List<Long> materialSidList){
        List<SamOsbSampleReimburseItem> itemList = itemMapper.selectSamOsbSampleReimburseItemList(new SamOsbSampleReimburseItem().setMaterialSidList(materialSidList));
        SamOsbSampleReimburse samOsbSampleReimburse = new SamOsbSampleReimburse();
        if (CollUtil.isNotEmpty(itemList)) {
            //样品名称
            List<String> materialNames = itemList.stream().map(SamOsbSampleReimburseItem::getMaterialName).collect(Collectors.toList());
            //外采样报销单号
            List<Long> reimburseCodes = itemList.stream().map(SamOsbSampleReimburseItem::getReimburseCode).collect(Collectors.toList());
            samOsbSampleReimburse.setFlag(ConstantsEms.YES);
            samOsbSampleReimburse.setHint("外采样" + materialNames.toString() + "已存在报销单，请核实！");
        } else {
            samOsbSampleReimburse.setFlag(ConstantsEms.NO);
        }
        return samOsbSampleReimburse;
    }

    @Override
    public String wbx(Long sid){
        List<String> tips = new ArrayList<>();
        SamOsbSampleReimburse sam = selectSamOsbSampleReimburseById(sid);
        List<SamOsbSampleReimburseItem> itemList = sam.getItemList();
        for (SamOsbSampleReimburseItem item : itemList) {
            if(!item.getReimburseStatus().equals("WBX")){
                tips.add(item.getMaterialName());
            }
        }
        if(tips.size()>0){
            return "提示：外采样"+tips.toString()+"已存在报销单，请核实！";
        }else{
            return "";
        }
    }
}
