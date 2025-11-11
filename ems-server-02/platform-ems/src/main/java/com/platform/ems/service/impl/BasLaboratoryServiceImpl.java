package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IBasLaboratoryService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 实验室档案Service业务层处理
 *
 * @author c
 * @date 2022-03-31
 */
@Service
@SuppressWarnings("all")
public class BasLaboratoryServiceImpl extends ServiceImpl<BasLaboratoryMapper, BasLaboratory> implements IBasLaboratoryService {
    @Autowired
    private BasLaboratoryMapper basLaboratoryMapper;
    @Autowired
    private BasLaboratoryAddrMapper basLaboratoryAddrMapper;
    @Autowired
    private BasLaboratoryAttachMapper basLaboratoryAttachMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "实验室档案";

    /**
     * 查询实验室档案
     *
     * @param laboratorySid 实验室档案ID
     * @return 实验室档案
     */
    @Override
    public BasLaboratory selectBasLaboratoryById(Long laboratorySid) {
        BasLaboratory basLaboratory = basLaboratoryMapper.selectBasLaboratoryById(laboratorySid);
        if (basLaboratory == null) {
            return null;
        }
        //实验室-联系方式信息
        List<BasLaboratoryAddr> addrList =
                basLaboratoryAddrMapper.selectBasLaboratoryAddrList(new BasLaboratoryAddr().setLaboratorySid(laboratorySid));
        //实验室-附件
        List<BasLaboratoryAttach> attachList =
                basLaboratoryAttachMapper.selectBasLaboratoryAttachList(new BasLaboratoryAttach().setLaboratorySid(laboratorySid));
        basLaboratory.setAddrList(addrList);
        basLaboratory.setAttachList(attachList);
        //操作日志
        MongodbUtil.find(basLaboratory);
        return basLaboratory;
    }

    /**
     * 查询实验室档案列表
     *
     * @param basLaboratory 实验室档案
     * @return 实验室档案
     */
    @Override
    public List<BasLaboratory> selectBasLaboratoryList(BasLaboratory basLaboratory) {
        return basLaboratoryMapper.selectBasLaboratoryList(basLaboratory);
    }

    /**
     * 新增实验室档案
     * 需要注意编码重复校验
     *
     * @param basLaboratory 实验室档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasLaboratory(BasLaboratory basLaboratory) {
        List<BasLaboratory> list = basLaboratoryMapper.selectList(new QueryWrapper<BasLaboratory>().lambda()
                .eq(BasLaboratory::getLaboratoryName,basLaboratory.getLaboratoryName()));
        if (CollectionUtil.isNotEmpty(list)){
            throw new BaseException("实验室名称已存在");
        }
        //设置确认信息
        setConfirmInfo(basLaboratory);
        basLaboratory.setVendorCode(getVendorCode(basLaboratory));
        int row = basLaboratoryMapper.insert(basLaboratory);
        if (row > 0) {
            //实验室-联系方式信息
            List<BasLaboratoryAddr> addrList = basLaboratory.getAddrList();
            if (CollUtil.isNotEmpty(addrList)) {
                addBasLaboratoryAddr(basLaboratory, addrList);
            }
            //实验室-附件
            List<BasLaboratoryAttach> attachList = basLaboratory.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                addBasLaboratoryAttach(basLaboratory, attachList);
            }

            //待办通知
            BasLaboratory laboratory = new BasLaboratory();
            laboratory = basLaboratoryMapper.selectBasLaboratoryById(basLaboratory.getLaboratorySid());
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(basLaboratory.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.SYS)
                        .setDocumentSid(basLaboratory.getLaboratorySid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("实验室" + laboratory.getLaboratoryCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(laboratory.getLaboratoryCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(basLaboratory.getLaboratorySid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    private List<BasLaboratory> getBasLaboratories(BasLaboratory basLaboratory) {
        return basLaboratoryMapper.selectBasLaboratoryList(new BasLaboratory().setLaboratoryName(basLaboratory.getLaboratoryName()));
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(BasLaboratory o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 获取供应商编码
     */
    private String getVendorCode(BasLaboratory basLaboratory){
        String vendorCode = null;
        if (basLaboratory.getVendorSid() != null){
            BasVendor vendor = basVendorMapper.selectById(basLaboratory.getVendorSid());
            if (vendor != null){
                vendorCode = vendor.getVendorCode().toString();
            }
        }
        return vendorCode;
    }

    /**
     * 实验室-联系方式信息
     */
    private void addBasLaboratoryAddr(BasLaboratory basLaboratory, List<BasLaboratoryAddr> addrList) {
        deleteAddr(basLaboratory);
        addrList.forEach(o -> {
            o.setLaboratorySid(basLaboratory.getLaboratorySid());
        });
        basLaboratoryAddrMapper.inserts(addrList);
    }

    /**
     * 实验室-附件
     */
    private void addBasLaboratoryAttach(BasLaboratory basLaboratory, List<BasLaboratoryAttach> attachList) {
        deleteAttach(basLaboratory);
        attachList.forEach(o -> {
            o.setLaboratorySid(basLaboratory.getLaboratorySid());
        });
        basLaboratoryAttachMapper.inserts(attachList);
    }

    /**
     * 删除联系方式信息
     */
    private void deleteAddr(BasLaboratory basLaboratory) {
        basLaboratoryAddrMapper.delete(
                new UpdateWrapper<BasLaboratoryAddr>()
                        .lambda()
                        .eq(BasLaboratoryAddr::getLaboratorySid, basLaboratory.getLaboratorySid())
        );
    }

    /**
     * 删除附件
     */
    private void deleteAttach(BasLaboratory basLaboratory) {
        basLaboratoryAttachMapper.delete(
                new UpdateWrapper<BasLaboratoryAttach>()
                        .lambda()
                        .eq(BasLaboratoryAttach::getLaboratorySid, basLaboratory.getLaboratorySid())
        );
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(BasLaboratory basLaboratory) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, basLaboratory.getLaboratorySid()));
        if (CollUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, basLaboratory.getLaboratorySid()));
        }
    }

    /**
     * 修改实验室档案
     *
     * @param basLaboratory 实验室档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasLaboratory(BasLaboratory basLaboratory) {
        //校验名称是否重复
        checkNameUnique(basLaboratory);
        //设置确认信息
        setConfirmInfo(basLaboratory);
        basLaboratory.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        basLaboratory.setVendorCode(getVendorCode(basLaboratory));
        BasLaboratory response = basLaboratoryMapper.selectBasLaboratoryById(basLaboratory.getLaboratorySid());
        int row = basLaboratoryMapper.updateAllById(basLaboratory);
        if (row > 0) {
            //实验室-联系方式信息
            List<BasLaboratoryAddr> addrList = basLaboratory.getAddrList();
            if (CollUtil.isNotEmpty(addrList)) {
                addBasLaboratoryAddr(basLaboratory, addrList);
            } else {
                deleteAddr(basLaboratory);
            }
            //实验室-附件
            List<BasLaboratoryAttach> attachList = basLaboratory.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                addBasLaboratoryAttach(basLaboratory, attachList);
            } else {
                deleteAttach(basLaboratory);
            }

            if (!ConstantsEms.SAVA_STATUS.equals(basLaboratory.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(basLaboratory);
            }
            //插入日志
            MongodbUtil.insertUserLog(basLaboratory.getLaboratorySid(), BusinessType.UPDATE.getValue(), response, basLaboratory, TITLE);
        }
        return row;
    }

    /**
     * 校验名称是否重复
     */
    private void checkNameUnique(BasLaboratory basLaboratory) {
        List<BasLaboratory> list = basLaboratoryMapper.selectList(new QueryWrapper<BasLaboratory>().lambda()
                .eq(BasLaboratory::getLaboratoryName,basLaboratory.getLaboratoryName())
                .ne(BasLaboratory::getLaboratorySid,basLaboratory.getLaboratorySid()));
        if (CollectionUtil.isNotEmpty(list)){
            throw new BaseException("实验室名称已存在");
        }
    }

    /**
     * 变更实验室档案
     *
     * @param basLaboratory 实验室档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasLaboratory(BasLaboratory basLaboratory) {
        //校验名称是否重复
        checkNameUnique(basLaboratory);
        //设置确认信息
        setConfirmInfo(basLaboratory);
        basLaboratory.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        basLaboratory.setVendorCode(getVendorCode(basLaboratory));
        BasLaboratory response = basLaboratoryMapper.selectBasLaboratoryById(basLaboratory.getLaboratorySid());
        int row = basLaboratoryMapper.updateAllById(basLaboratory);
        if (row > 0) {
            //实验室-联系方式信息
            List<BasLaboratoryAddr> addrList = basLaboratory.getAddrList();
            if (CollUtil.isNotEmpty(addrList)) {
                addBasLaboratoryAddr(basLaboratory, addrList);
            } else {
                deleteAddr(basLaboratory);
            }
            //实验室-附件
            List<BasLaboratoryAttach> attachList = basLaboratory.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                addBasLaboratoryAttach(basLaboratory, attachList);
            } else {
                deleteAttach(basLaboratory);
            }
            //插入日志
            MongodbUtil.insertUserLog(basLaboratory.getLaboratorySid(), BusinessType.CHANGE.getValue(), response, basLaboratory, TITLE);
        }
        return row;
    }

    /**
     * 批量删除实验室档案
     *
     * @param laboratorySids 需要删除的实验室档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasLaboratoryByIds(List<Long> laboratorySids) {
        Integer count = basLaboratoryMapper.selectCount(new QueryWrapper<BasLaboratory>().lambda()
                .eq(BasLaboratory::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(BasLaboratory::getLaboratorySid, laboratorySids));
        if (count != laboratorySids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        BasLaboratory basLaboratory = new BasLaboratory();
        laboratorySids.forEach(laboratorySid -> {
            basLaboratory.setLaboratorySid(laboratorySid);
            //校验是否存在待办
            checkTodoExist(basLaboratory);
        });
        //删除实验室-联系方式信息
        basLaboratoryAddrMapper.delete(new UpdateWrapper<BasLaboratoryAddr>().lambda()
                .in(BasLaboratoryAddr::getLaboratorySid, laboratorySids));
        //删除实验室-附件
        basLaboratoryAttachMapper.delete(new UpdateWrapper<BasLaboratoryAttach>().lambda()
                .in(BasLaboratoryAttach::getLaboratorySid, laboratorySids));
        return basLaboratoryMapper.deleteBatchIds(laboratorySids);
    }

    /**
     * 启用/停用
     *
     * @param basLaboratory
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(BasLaboratory basLaboratory) {
        Long[] sids = basLaboratory.getLaboratorySidList();
        basLaboratoryMapper.update(null, new UpdateWrapper<BasLaboratory>().lambda()
                .set(BasLaboratory::getStatus, basLaboratory.getStatus())
                .in(BasLaboratory::getLaboratorySid, sids));
        for (Long id : sids) {
            //插入日志
            String remark = StrUtil.isEmpty(basLaboratory.getDisableRemark()) ? null : basLaboratory.getDisableRemark();
            MongodbDeal.status(basLaboratory.getLaboratorySid(), basLaboratory.getStatus(), null, TITLE, remark);
        }
        return sids.length;
    }


    /**
     * 更改确认状态
     *
     * @param basLaboratory
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(BasLaboratory basLaboratory) {
        Long[] sids = basLaboratory.getLaboratorySidList();
        basLaboratoryMapper.update(null, new UpdateWrapper<BasLaboratory>().lambda()
                .set(BasLaboratory::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .set(BasLaboratory::getConfirmDate, new Date())
                .set(BasLaboratory::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                .in(BasLaboratory::getLaboratorySid, sids));
        for (Long id : sids) {
            //校验是否存在待办
            basLaboratory.setLaboratorySid(id);
            checkTodoExist(basLaboratory);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
        }
        return sids.length;
    }

    /**
     * 下拉框
     *
     * @param basLaboratory
     * @return
     */
    @Override
    public List<BasLaboratory> getList(BasLaboratory basLaboratory){
        return basLaboratoryMapper.getList(basLaboratory);
    }
}
