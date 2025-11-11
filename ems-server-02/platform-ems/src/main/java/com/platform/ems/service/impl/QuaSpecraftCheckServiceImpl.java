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
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IQuaSpecraftCheckService;
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
 * 特殊工艺检测单-主Service业务层处理
 *
 * @author linhongwei
 * @date 2022-04-12
 */
@Service
@SuppressWarnings("all")
public class QuaSpecraftCheckServiceImpl extends ServiceImpl<QuaSpecraftCheckMapper, QuaSpecraftCheck> implements IQuaSpecraftCheckService {
    @Autowired
    private QuaSpecraftCheckMapper quaSpecraftCheckMapper;
    @Autowired
    private QuaSpecraftCheckProductsMapper quaSpecraftCheckProductsMapper;
    @Autowired
    private QuaSpecraftCheckItemMapper quaSpecraftCheckItemMapper;
    @Autowired
    private QuaSpecraftCheckAttachMapper quaSpecraftCheckAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "特殊工艺检测单-主";

    /**
     * 查询特殊工艺检测单-主
     *
     * @param specraftCheckSid 特殊工艺检测单-主ID
     * @return 特殊工艺检测单-主
     */
    @Override
    public QuaSpecraftCheck selectQuaSpecraftCheckById(Long specraftCheckSid) {
        QuaSpecraftCheck quaSpecraftCheck = quaSpecraftCheckMapper.selectQuaSpecraftCheckById(specraftCheckSid);
        if (quaSpecraftCheck == null) {
            return null;
        }
        //特殊工艺检测单-款明细
        List<QuaSpecraftCheckProducts> productList =
                quaSpecraftCheckProductsMapper.selectQuaSpecraftCheckProductsList(new QuaSpecraftCheckProducts().setSpecraftCheckSid(specraftCheckSid));
        //特殊工艺检测单-检测项目
        List<QuaSpecraftCheckItem> itemList =
                quaSpecraftCheckItemMapper.selectQuaSpecraftCheckItemList(new QuaSpecraftCheckItem().setSpecraftCheckSid(specraftCheckSid));
        //特殊工艺检测单-附件
        List<QuaSpecraftCheckAttach> attachList =
                quaSpecraftCheckAttachMapper.selectQuaSpecraftCheckAttachList(new QuaSpecraftCheckAttach().setSpecraftCheckSid(specraftCheckSid));
        quaSpecraftCheck.setProductList(productList);
        quaSpecraftCheck.setItemList(itemList);
        quaSpecraftCheck.setAttachList(attachList);
        MongodbUtil.find(quaSpecraftCheck);
        return quaSpecraftCheck;
    }

    /**
     * 查询特殊工艺检测单-主列表
     *
     * @param quaSpecraftCheck 特殊工艺检测单-主
     * @return 特殊工艺检测单-主
     */
    @Override
    public List<QuaSpecraftCheck> selectQuaSpecraftCheckList(QuaSpecraftCheck quaSpecraftCheck) {
        return quaSpecraftCheckMapper.selectQuaSpecraftCheckList(quaSpecraftCheck);
    }

    /**
     * 新增特殊工艺检测单-主
     * 需要注意编码重复校验
     *
     * @param quaSpecraftCheck 特殊工艺检测单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaSpecraftCheck(QuaSpecraftCheck quaSpecraftCheck) {
        //设置确认信息
        setConfirmInfo(quaSpecraftCheck);
        int row = quaSpecraftCheckMapper.insert(quaSpecraftCheck);
        if (row > 0) {
            //特殊工艺检测单-款明细
            List<QuaSpecraftCheckProducts> productList = quaSpecraftCheck.getProductList();
            if (CollUtil.isNotEmpty(productList)) {
                addProduct(quaSpecraftCheck, productList);
            }
            //特殊工艺检测单-检测项目
            List<QuaSpecraftCheckItem> itemList = quaSpecraftCheck.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                addItem(quaSpecraftCheck, itemList);
            }
            //特殊工艺检测单-附件
            List<QuaSpecraftCheckAttach> attachList = quaSpecraftCheck.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                addAttach(quaSpecraftCheck, attachList);
            }

            //待办通知
            QuaSpecraftCheck specraftCheck = new QuaSpecraftCheck();
            specraftCheck = quaSpecraftCheckMapper.selectQuaSpecraftCheckById(quaSpecraftCheck.getSpecraftCheckSid());
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(quaSpecraftCheck.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TSGYJCD)
                        .setDocumentSid(quaSpecraftCheck.getSpecraftCheckSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("特殊工艺检测单" + specraftCheck.getSpecraftCheckCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(specraftCheck.getSpecraftCheckCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(quaSpecraftCheck.getSpecraftCheckSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(QuaSpecraftCheck o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 特殊工艺检测单-款明细
     */
    private void addProduct(QuaSpecraftCheck quaSpecraftCheck, List<QuaSpecraftCheckProducts> productList) {
        deleteProduct(quaSpecraftCheck);
        productList.forEach(o -> {
            o.setSpecraftCheckSid(quaSpecraftCheck.getSpecraftCheckSid());
        });
        quaSpecraftCheckProductsMapper.inserts(productList);
    }

    /**
     * 特殊工艺检测单-检测项目
     */
    private void addItem(QuaSpecraftCheck quaSpecraftCheck, List<QuaSpecraftCheckItem> itemList) {
        deleteItem(quaSpecraftCheck);
        itemList.forEach(o -> {
            o.setSpecraftCheckSid(quaSpecraftCheck.getSpecraftCheckSid());
        });
        quaSpecraftCheckItemMapper.inserts(itemList);
    }

    /**
     * 特殊工艺检测单-附件
     */
    private void addAttach(QuaSpecraftCheck quaSpecraftCheck, List<QuaSpecraftCheckAttach> attachList) {
        deleteAttach(quaSpecraftCheck);
        attachList.forEach(o -> {
            o.setSpecraftCheckSid(quaSpecraftCheck.getSpecraftCheckSid());
        });
        quaSpecraftCheckAttachMapper.inserts(attachList);
    }

    /**
     * 删除款明细
     */
    private void deleteProduct(QuaSpecraftCheck quaSpecraftCheck) {
        quaSpecraftCheckProductsMapper.delete(
                new UpdateWrapper<QuaSpecraftCheckProducts>()
                        .lambda()
                        .eq(QuaSpecraftCheckProducts::getSpecraftCheckSid, quaSpecraftCheck.getSpecraftCheckSid())
        );
    }

    /**
     * 删除检测项目
     */
    private void deleteItem(QuaSpecraftCheck quaSpecraftCheck) {
        quaSpecraftCheckItemMapper.delete(
                new UpdateWrapper<QuaSpecraftCheckItem>()
                        .lambda()
                        .eq(QuaSpecraftCheckItem::getSpecraftCheckSid, quaSpecraftCheck.getSpecraftCheckSid())
        );
    }

    /**
     * 删除附件
     */
    private void deleteAttach(QuaSpecraftCheck quaSpecraftCheck) {
        quaSpecraftCheckAttachMapper.delete(
                new UpdateWrapper<QuaSpecraftCheckAttach>()
                        .lambda()
                        .eq(QuaSpecraftCheckAttach::getSpecraftCheckSid, quaSpecraftCheck.getSpecraftCheckSid())
        );
    }

    /**
     * 修改特殊工艺检测单-主
     *
     * @param quaSpecraftCheck 特殊工艺检测单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaSpecraftCheck(QuaSpecraftCheck quaSpecraftCheck) {
        QuaSpecraftCheck response = quaSpecraftCheckMapper.selectQuaSpecraftCheckById(quaSpecraftCheck.getSpecraftCheckSid());
        //设置确认信息
        setConfirmInfo(quaSpecraftCheck);
        quaSpecraftCheck.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        int row = quaSpecraftCheckMapper.updateAllById(quaSpecraftCheck);
        if (row > 0) {
            //特殊工艺检测单-款明细
            List<QuaSpecraftCheckProducts> productList = quaSpecraftCheck.getProductList();
            if (CollUtil.isNotEmpty(productList)) {
                productList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addProduct(quaSpecraftCheck, productList);
            } else {
                deleteProduct(quaSpecraftCheck);
            }
            //特殊工艺检测单-检测项目
            List<QuaSpecraftCheckItem> itemList = quaSpecraftCheck.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                itemList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addItem(quaSpecraftCheck, itemList);
            } else {
                deleteItem(quaSpecraftCheck);
            }
            //特殊工艺检测单-附件
            List<QuaSpecraftCheckAttach> attachList = quaSpecraftCheck.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                attachList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addAttach(quaSpecraftCheck, attachList);
            } else {
                deleteAttach(quaSpecraftCheck);
            }

            if (!ConstantsEms.SAVA_STATUS.equals(quaSpecraftCheck.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(quaSpecraftCheck);
            }
            //插入日志
            MongodbUtil.insertUserLog(quaSpecraftCheck.getSpecraftCheckSid(), BusinessType.UPDATE.getValue(), response, quaSpecraftCheck, TITLE);
        }
        return row;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(QuaSpecraftCheck quaSpecraftCheck) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, quaSpecraftCheck.getSpecraftCheckSid()));
        if (CollUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, quaSpecraftCheck.getSpecraftCheckSid()));
        }
    }

    /**
     * 变更特殊工艺检测单-主
     *
     * @param quaSpecraftCheck 特殊工艺检测单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaSpecraftCheck(QuaSpecraftCheck quaSpecraftCheck) {
        QuaSpecraftCheck response = quaSpecraftCheckMapper.selectQuaSpecraftCheckById(quaSpecraftCheck.getSpecraftCheckSid());
        //设置确认信息
        setConfirmInfo(quaSpecraftCheck);
        quaSpecraftCheck.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        int row = quaSpecraftCheckMapper.updateAllById(quaSpecraftCheck);
        if (row > 0) {
            //特殊工艺检测单-款明细
            List<QuaSpecraftCheckProducts> productList = quaSpecraftCheck.getProductList();
            if (CollUtil.isNotEmpty(productList)) {
                productList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addProduct(quaSpecraftCheck, productList);
            } else {
                deleteProduct(quaSpecraftCheck);
            }
            //特殊工艺检测单-检测项目
            List<QuaSpecraftCheckItem> itemList = quaSpecraftCheck.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                itemList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addItem(quaSpecraftCheck, itemList);
            } else {
                deleteItem(quaSpecraftCheck);
            }
            //特殊工艺检测单-附件
            List<QuaSpecraftCheckAttach> attachList = quaSpecraftCheck.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                attachList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addAttach(quaSpecraftCheck, attachList);
            } else {
                deleteAttach(quaSpecraftCheck);
            }
            //插入日志
            MongodbUtil.insertUserLog(quaSpecraftCheck.getSpecraftCheckSid(), BusinessType.CHANGE.getValue(), response, quaSpecraftCheck, TITLE);
        }
        return row;
    }

    /**
     * 批量删除特殊工艺检测单-主
     *
     * @param specraftCheckSids 需要删除的特殊工艺检测单-主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaSpecraftCheckByIds(List<Long> specraftCheckSids) {
        //删除校验
        Integer count = quaSpecraftCheckMapper.selectCount(new QueryWrapper<QuaSpecraftCheck>().lambda()
                .eq(QuaSpecraftCheck::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(QuaSpecraftCheck::getSpecraftCheckSid, specraftCheckSids));
        if (count != specraftCheckSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        QuaSpecraftCheck quaSpecraftCheck = new QuaSpecraftCheck();
        specraftCheckSids.forEach(specraftCheckSid -> {
            quaSpecraftCheck.setSpecraftCheckSid(specraftCheckSid);
            //校验是否存在待办
            checkTodoExist(quaSpecraftCheck);
        });
        //删除-款明细
        quaSpecraftCheckProductsMapper.delete(new UpdateWrapper<QuaSpecraftCheckProducts>().lambda()
                .in(QuaSpecraftCheckProducts::getSpecraftCheckSid, specraftCheckSids));
        //删除-检测项目
        quaSpecraftCheckItemMapper.delete(new UpdateWrapper<QuaSpecraftCheckItem>().lambda()
                .in(QuaSpecraftCheckItem::getSpecraftCheckSid, specraftCheckSids));
        //删除-附件
        quaSpecraftCheckAttachMapper.delete(new UpdateWrapper<QuaSpecraftCheckAttach>().lambda()
                .in(QuaSpecraftCheckAttach::getSpecraftCheckSid, specraftCheckSids));
        return quaSpecraftCheckMapper.deleteBatchIds(specraftCheckSids);
    }

    /**
     * 更改确认状态
     *
     * @param quaSpecraftCheck
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(QuaSpecraftCheck quaSpecraftCheck) {
        int row = 0;
        Long[] sids = quaSpecraftCheck.getSpecraftCheckSidList();
        if (sids != null && sids.length > 0) {
            row = quaSpecraftCheckMapper.update(null, new UpdateWrapper<QuaSpecraftCheck>().lambda()
                    .set(QuaSpecraftCheck::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(QuaSpecraftCheck::getConfirmDate, new Date())
                    .set(QuaSpecraftCheck::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .in(QuaSpecraftCheck::getSpecraftCheckSid, sids));
            for (Long id : sids) {
                //校验是否存在待办
                quaSpecraftCheck.setSpecraftCheckSid(id);
                checkTodoExist(quaSpecraftCheck);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
