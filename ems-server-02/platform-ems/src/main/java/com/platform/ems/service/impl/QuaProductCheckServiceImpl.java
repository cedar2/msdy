package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.QuaProductCheck;
import com.platform.ems.domain.QuaProductCheckAttach;
import com.platform.ems.domain.QuaProductCheckItem;
import com.platform.ems.domain.QuaProductCheckProducts;
import com.platform.ems.mapper.QuaProductCheckAttachMapper;
import com.platform.ems.mapper.QuaProductCheckItemMapper;
import com.platform.ems.mapper.QuaProductCheckMapper;
import com.platform.ems.mapper.QuaProductCheckProductsMapper;
import com.platform.ems.service.IQuaProductCheckService;
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
 * 成衣检测单-主Service业务层处理
 *
 * @author linhongwei
 * @date 2022-04-13
 */
@Service
@SuppressWarnings("all")
public class QuaProductCheckServiceImpl extends ServiceImpl<QuaProductCheckMapper, QuaProductCheck> implements IQuaProductCheckService {
    @Autowired
    private QuaProductCheckMapper quaProductCheckMapper;
    @Autowired
    private QuaProductCheckProductsMapper quaProductCheckProductsMapper;
    @Autowired
    private QuaProductCheckItemMapper quaProductCheckItemMapper;
    @Autowired
    private QuaProductCheckAttachMapper quaProductCheckAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "成衣检测单-主";

    /**
     * 查询成衣检测单-主
     *
     * @param productCheckSid 成衣检测单-主ID
     * @return 成衣检测单-主
     */
    @Override
    public QuaProductCheck selectQuaProductCheckById(Long productCheckSid) {
        QuaProductCheck quaProductCheck = quaProductCheckMapper.selectQuaProductCheckById(productCheckSid);
        if (quaProductCheck == null) {
            return null;
        }
        //成衣检测单-款明细
        List<QuaProductCheckProducts> productList =
                quaProductCheckProductsMapper.selectQuaProductCheckProductsList(new QuaProductCheckProducts().setProductCheckSid(productCheckSid));
        //成衣检测单-检测项目
        List<QuaProductCheckItem> itemList =
                quaProductCheckItemMapper.selectQuaProductCheckItemList(new QuaProductCheckItem().setProductCheckSid(productCheckSid));
        //成衣检测单-附件
        List<QuaProductCheckAttach> attachList =
                quaProductCheckAttachMapper.selectQuaProductCheckAttachList(new QuaProductCheckAttach().setProductCheckSid(productCheckSid));
        quaProductCheck.setProductList(productList);
        quaProductCheck.setItemList(itemList);
        quaProductCheck.setAttachList(attachList);
        MongodbUtil.find(quaProductCheck);
        return quaProductCheck;
    }

    /**
     * 查询成衣检测单-主列表
     *
     * @param quaProductCheck 成衣检测单-主
     * @return 成衣检测单-主
     */
    @Override
    public List<QuaProductCheck> selectQuaProductCheckList(QuaProductCheck quaProductCheck) {
        return quaProductCheckMapper.selectQuaProductCheckList(quaProductCheck);
    }

    /**
     * 新增成衣检测单-主
     * 需要注意编码重复校验
     *
     * @param quaProductCheck 成衣检测单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaProductCheck(QuaProductCheck quaProductCheck) {
        //设置确认信息
        setConfirmInfo(quaProductCheck);
        int row = quaProductCheckMapper.insert(quaProductCheck);
        if (row > 0) {
            //成衣检测单-款明细
            List<QuaProductCheckProducts> productList = quaProductCheck.getProductList();
            if (CollUtil.isNotEmpty(productList)) {
                addProduct(quaProductCheck, productList);
            }
            //成衣检测单-检测项目
            List<QuaProductCheckItem> itemList = quaProductCheck.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                addItem(quaProductCheck, itemList);
            }
            //成衣检测单-附件
            List<QuaProductCheckAttach> attachList = quaProductCheck.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                addAttach(quaProductCheck, attachList);
            }

            //待办通知
            QuaProductCheck productCheck = new QuaProductCheck();
            productCheck = quaProductCheckMapper.selectQuaProductCheckById(quaProductCheck.getProductCheckSid());
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(quaProductCheck.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.CYJCD)
                        .setDocumentSid(quaProductCheck.getProductCheckSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("成衣检测单" + productCheck.getProductCheckCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(productCheck.getProductCheckCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(quaProductCheck.getProductCheckSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(QuaProductCheck o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 成衣检测单-款明细
     */
    private void addProduct(QuaProductCheck quaProductCheck, List<QuaProductCheckProducts> productList) {
        deleteProduct(quaProductCheck);
        productList.forEach(o -> {
            o.setProductCheckSid(quaProductCheck.getProductCheckSid());
        });
        quaProductCheckProductsMapper.inserts(productList);
    }

    /**
     * 成衣检测单-检测项目
     */
    private void addItem(QuaProductCheck quaProductCheck, List<QuaProductCheckItem> itemList) {
        deleteItem(quaProductCheck);
        itemList.forEach(o -> {
            o.setProductCheckSid(quaProductCheck.getProductCheckSid());
        });
        quaProductCheckItemMapper.inserts(itemList);
    }

    /**
     * 成衣检测单-附件
     */
    private void addAttach(QuaProductCheck quaProductCheck, List<QuaProductCheckAttach> attachList) {
        deleteAttach(quaProductCheck);
        attachList.forEach(o -> {
            o.setProductCheckSid(quaProductCheck.getProductCheckSid());
        });
        quaProductCheckAttachMapper.inserts(attachList);
    }

    /**
     * 删除款明细
     */
    private void deleteProduct(QuaProductCheck quaProductCheck) {
        quaProductCheckProductsMapper.delete(
                new UpdateWrapper<QuaProductCheckProducts>()
                        .lambda()
                        .eq(QuaProductCheckProducts::getProductCheckSid, quaProductCheck.getProductCheckSid())
        );
    }

    /**
     * 删除检测项目
     */
    private void deleteItem(QuaProductCheck quaProductCheck) {
        quaProductCheckItemMapper.delete(
                new UpdateWrapper<QuaProductCheckItem>()
                        .lambda()
                        .eq(QuaProductCheckItem::getProductCheckSid, quaProductCheck.getProductCheckSid())
        );
    }

    /**
     * 删除附件
     */
    private void deleteAttach(QuaProductCheck quaProductCheck) {
        quaProductCheckAttachMapper.delete(
                new UpdateWrapper<QuaProductCheckAttach>()
                        .lambda()
                        .eq(QuaProductCheckAttach::getProductCheckSid, quaProductCheck.getProductCheckSid())
        );
    }

    /**
     * 修改成衣检测单-主
     *
     * @param quaProductCheck 成衣检测单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaProductCheck(QuaProductCheck quaProductCheck) {
        QuaProductCheck response = quaProductCheckMapper.selectQuaProductCheckById(quaProductCheck.getProductCheckSid());
        //设置确认信息
        setConfirmInfo(quaProductCheck);
        quaProductCheck.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        int row = quaProductCheckMapper.updateAllById(quaProductCheck);
        if (row > 0) {
            //成衣检测单-款明细
            List<QuaProductCheckProducts> productList = quaProductCheck.getProductList();
            if (CollUtil.isNotEmpty(productList)) {
                productList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addProduct(quaProductCheck, productList);
            } else {
                deleteProduct(quaProductCheck);
            }
            //成衣检测单-检测项目
            List<QuaProductCheckItem> itemList = quaProductCheck.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                itemList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addItem(quaProductCheck, itemList);
            } else {
                deleteItem(quaProductCheck);
            }
            //成衣检测单-附件
            List<QuaProductCheckAttach> attachList = quaProductCheck.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                attachList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addAttach(quaProductCheck, attachList);
            } else {
                deleteAttach(quaProductCheck);
            }

            if (!ConstantsEms.SAVA_STATUS.equals(quaProductCheck.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(quaProductCheck);
            }
            //插入日志
            MongodbUtil.insertUserLog(quaProductCheck.getProductCheckSid(), BusinessType.UPDATE.getValue(), response, quaProductCheck, TITLE);
        }
        return row;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(QuaProductCheck quaProductCheck) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, quaProductCheck.getPreProductCheckSid()));
        if (CollUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, quaProductCheck.getPreProductCheckSid()));
        }
    }

    /**
     * 变更成衣检测单-主
     *
     * @param quaProductCheck 成衣检测单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaProductCheck(QuaProductCheck quaProductCheck) {
        QuaProductCheck response = quaProductCheckMapper.selectQuaProductCheckById(quaProductCheck.getProductCheckSid());
        //设置确认信息
        setConfirmInfo(quaProductCheck);
        quaProductCheck.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        int row = quaProductCheckMapper.updateAllById(quaProductCheck);
        if (row > 0) {
            //成衣检测单-款明细
            List<QuaProductCheckProducts> productList = quaProductCheck.getProductList();
            if (CollUtil.isNotEmpty(productList)) {
                productList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addProduct(quaProductCheck, productList);
            } else {
                deleteProduct(quaProductCheck);
            }
            //成衣检测单-检测项目
            List<QuaProductCheckItem> itemList = quaProductCheck.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                itemList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addItem(quaProductCheck, itemList);
            } else {
                deleteItem(quaProductCheck);
            }
            //成衣检测单-附件
            List<QuaProductCheckAttach> attachList = quaProductCheck.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                attachList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addAttach(quaProductCheck, attachList);
            } else {
                deleteAttach(quaProductCheck);
            }
            //插入日志
            MongodbUtil.insertUserLog(quaProductCheck.getProductCheckSid(), BusinessType.CHANGE.getValue(), response, quaProductCheck, TITLE);
        }
        return row;
    }

    /**
     * 批量删除成衣检测单-主
     *
     * @param productCheckSids 需要删除的成衣检测单-主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaProductCheckByIds(List<Long> productCheckSids) {
        //删除校验
        Integer count = quaProductCheckMapper.selectCount(new QueryWrapper<QuaProductCheck>().lambda()
                .eq(QuaProductCheck::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(QuaProductCheck::getProductCheckSid, productCheckSids));
        if (count != productCheckSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        QuaProductCheck quaProductCheck = new QuaProductCheck();
        productCheckSids.forEach(productCheckSid -> {
            quaProductCheck.setPreProductCheckSid(productCheckSid);
            //校验是否存在待办
            checkTodoExist(quaProductCheck);
        });
        //删除-款明细
        quaProductCheckProductsMapper.delete(new UpdateWrapper<QuaProductCheckProducts>().lambda()
                .in(QuaProductCheckProducts::getProductCheckSid, productCheckSids));
        //删除-检测项目
        quaProductCheckItemMapper.delete(new UpdateWrapper<QuaProductCheckItem>().lambda()
                .in(QuaProductCheckItem::getProductCheckSid, productCheckSids));
        //删除-附件
        quaProductCheckAttachMapper.delete(new UpdateWrapper<QuaProductCheckAttach>().lambda()
                .in(QuaProductCheckAttach::getProductCheckSid, productCheckSids));
        return quaProductCheckMapper.deleteBatchIds(productCheckSids);
    }

    /**
     * 更改确认状态
     *
     * @param quaProductCheck
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(QuaProductCheck quaProductCheck) {
        int row = 0;
        Long[] sids = quaProductCheck.getProductCheckSidList();
        if (sids != null && sids.length > 0) {
            row = quaProductCheckMapper.update(null, new UpdateWrapper<QuaProductCheck>().lambda()
                    .set(QuaProductCheck::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(QuaProductCheck::getConfirmDate, new Date())
                    .set(QuaProductCheck::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .in(QuaProductCheck::getProductCheckSid, sids));
            for (Long id : sids) {
                //校验是否存在待办
                quaProductCheck.setProductCheckSid(id);
                checkTodoExist(quaProductCheck);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
