package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasCustomerTag;
import com.platform.ems.domain.BasCustomerTagItem;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.mapper.BasCustomerTagItemMapper;
import com.platform.ems.mapper.BasCustomerTagMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.service.IBasCustomerTagService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 客户标签(分组)Service业务层处理
 *
 * @author c
 * @date 2022-03-30
 */
@Service
@SuppressWarnings("all")
public class BasCustomerTagServiceImpl extends ServiceImpl<BasCustomerTagMapper, BasCustomerTag> implements IBasCustomerTagService {
    @Autowired
    private BasCustomerTagMapper basCustomerTagMapper;
    @Autowired
    private BasCustomerTagItemMapper basCustomerTagItemMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "客户标签(分组)";

    /**
     * 查询客户标签(分组)
     *
     * @param customerTagSid 客户标签(分组)ID
     * @return 客户标签(分组)
     */
    @Override
    public BasCustomerTag selectBasCustomerTagById(Long customerTagSid) {
        BasCustomerTag basCustomerTag = basCustomerTagMapper.selectBasCustomerTagById(customerTagSid);
        if (basCustomerTag == null) {
            return null;
        }
        List<BasCustomerTagItem> itemList =
                basCustomerTagItemMapper.selectBasCustomerTagItemList(new BasCustomerTagItem().setCustomerTagSid(customerTagSid));
        basCustomerTag.setItemList(itemList);
        MongodbUtil.find(basCustomerTag);
        return basCustomerTag;
    }

    /**
     * 查询客户标签(分组)列表
     *
     * @param basCustomerTag 客户标签(分组)
     * @return 客户标签(分组)
     */
    @Override
    public List<BasCustomerTag> selectBasCustomerTagList(BasCustomerTag basCustomerTag) {
        return basCustomerTagMapper.selectBasCustomerTagList(basCustomerTag);
    }

    /**
     * 新增客户标签(分组)
     * 需要注意编码重复校验
     *
     * @param basCustomerTag 客户标签(分组)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasCustomerTag(BasCustomerTag basCustomerTag) {
        List<BasCustomerTag> list = selectBasCustomerTagList(new BasCustomerTag().setCustomerTagName(basCustomerTag.getCustomerTagName()));
        if (CollUtil.isNotEmpty(list)) {
            throw new BaseException("客户分组名称已存在");
        }
        //设置确认信息
        setConfirmInfo(basCustomerTag);
        int row = basCustomerTagMapper.insert(basCustomerTag);
        if (row > 0) {
            //客户分组-明细
            List<BasCustomerTagItem> itemList = basCustomerTag.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                addBasCustomerTagItem(basCustomerTag, itemList);
            }
            //待办通知
            BasCustomerTag customerTag = new BasCustomerTag();
            customerTag = basCustomerTagMapper.selectBasCustomerTagById(basCustomerTag.getCustomerTagSid());
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(basCustomerTag.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.KHFZ)
                        .setDocumentSid(basCustomerTag.getCustomerTagSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("客户分组" + customerTag.getCustomerTagCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(customerTag.getCustomerTagCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(basCustomerTag.getCustomerTagSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(BasCustomerTag o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            if (CollUtil.isEmpty(o.getItemList())) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 供应商分组-明细
     */
    private void addBasCustomerTagItem(BasCustomerTag basCustomerTag, List<BasCustomerTagItem> itemList) {
        deleteItem(basCustomerTag);
        itemList.forEach(o -> {
            o.setCustomerTagSid(basCustomerTag.getCustomerTagSid());
        });
        basCustomerTagItemMapper.inserts(itemList);
    }

    /**
     * 删除明细
     */
    private void deleteItem(BasCustomerTag basCustomerTag) {
        basCustomerTagItemMapper.delete(
                new UpdateWrapper<BasCustomerTagItem>()
                        .lambda()
                        .eq(BasCustomerTagItem::getCustomerTagSid, basCustomerTag.getCustomerTagSid())
        );
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(BasCustomerTag basCustomerTag) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, basCustomerTag.getCustomerTagSid()));
        if (CollUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, basCustomerTag.getCustomerTagSid()));
        }
    }

    /**
     * 修改客户标签(分组)
     *
     * @param basCustomerTag 客户标签(分组)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasCustomerTag(BasCustomerTag basCustomerTag) {
        //校验名称是否重复
        checkNameUnique(basCustomerTag);
        BasCustomerTag response = basCustomerTagMapper.selectBasCustomerTagById(basCustomerTag.getCustomerTagSid());
        //设置确认信息
        setConfirmInfo(basCustomerTag);
        int row = basCustomerTagMapper.updateById(basCustomerTag);
        if (row > 0) {
            //客户分组-明细
            List<BasCustomerTagItem> itemList = basCustomerTag.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                itemList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addBasCustomerTagItem(basCustomerTag, itemList);
            } else {
                deleteItem(basCustomerTag);
            }

            if (!ConstantsEms.SAVA_STATUS.equals(basCustomerTag.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(basCustomerTag);
            }
            //插入日志
            MongodbUtil.insertUserLog(basCustomerTag.getCustomerTagSid(), BusinessType.UPDATE.getValue(), response, basCustomerTag, TITLE);
        }
        return row;
    }

    /**
     * 校验名称是否重复
     */
    private void checkNameUnique(BasCustomerTag basCustomerTag) {
        List<BasCustomerTag> list = selectBasCustomerTagList(new BasCustomerTag().setCustomerTagName(basCustomerTag.getCustomerTagName()));
        if (CollUtil.isNotEmpty(list)) {
            list.forEach(o -> {
                if (!o.getCustomerTagSid().equals(basCustomerTag.getCustomerTagSid())) {
                    throw new BaseException("客户分组名称已存在");
                }
            });
        }
    }

    /**
     * 变更客户标签(分组)
     *
     * @param basCustomerTag 客户标签(分组)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasCustomerTag(BasCustomerTag basCustomerTag) {
        //校验名称是否重复
        checkNameUnique(basCustomerTag);
        BasCustomerTag response = basCustomerTagMapper.selectBasCustomerTagById(basCustomerTag.getCustomerTagSid());
        //设置确认信息
        setConfirmInfo(basCustomerTag);
        int row = basCustomerTagMapper.updateAllById(basCustomerTag);
        if (row > 0) {
            //客户分组-明细
            List<BasCustomerTagItem> itemList = basCustomerTag.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                itemList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addBasCustomerTagItem(basCustomerTag, itemList);
            } else {
                deleteItem(basCustomerTag);
            }
            //插入日志
            MongodbUtil.insertUserLog(basCustomerTag.getCustomerTagSid(), BusinessType.CHANGE.getValue(), response, basCustomerTag, TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户标签(分组)
     *
     * @param customerTagSids 需要删除的客户标签(分组)ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasCustomerTagByIds(List<Long> customerTagSids) {
        Integer count = basCustomerTagMapper.selectCount(new QueryWrapper<BasCustomerTag>().lambda()
                .eq(BasCustomerTag::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(BasCustomerTag::getCustomerTagSid, customerTagSids));
        if (count != customerTagSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        BasCustomerTag basCustomerTag = new BasCustomerTag();
        customerTagSids.forEach(customerTagSid -> {
            basCustomerTag.setCustomerTagSid(customerTagSid);
            //校验是否存在待办
            checkTodoExist(basCustomerTag);
        });
        basCustomerTagItemMapper.delete(new UpdateWrapper<BasCustomerTagItem>().lambda().in(BasCustomerTagItem::getCustomerTagSid, customerTagSids));
        return basCustomerTagMapper.deleteBatchIds(customerTagSids);
    }

    /**
     * 启用/停用
     *
     * @param basCustomerTag
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(BasCustomerTag basCustomerTag) {
        Long[] sids = basCustomerTag.getCustomerTagSidList();
        for (Long id : sids) {
            basCustomerTag.setCustomerTagSid(id);
            basCustomerTagMapper.updateById(basCustomerTag);
            //插入日志
            String remark = StrUtil.isEmpty(basCustomerTag.getDisableRemark()) ? null : basCustomerTag.getDisableRemark();
            MongodbDeal.status(basCustomerTag.getCustomerTagSid(), basCustomerTag.getStatus(), null, TITLE, remark);
        }
        return sids.length;
    }


    /**
     * 更改确认状态
     *
     * @param basCustomerTag
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(BasCustomerTag basCustomerTag) {
        Long[] sids = basCustomerTag.getCustomerTagSidList();
        List<String> codeList = new ArrayList<>();
        for (Long sid : sids) {
            BasCustomerTag customerTag = selectBasCustomerTagById(sid);
            if (CollUtil.isEmpty(customerTag.getItemList())) {
                codeList.add(customerTag.getCustomerTagCode());
            }
        }
        if (CollUtil.isNotEmpty(codeList)) {
            throw new BaseException("客户分组" + codeList + "的明细行为空，无法确认！");
        }
        basCustomerTagMapper.update(null, new UpdateWrapper<BasCustomerTag>().lambda()
                .set(BasCustomerTag::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .set(BasCustomerTag::getConfirmDate, new Date())
                .set(BasCustomerTag::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                .in(BasCustomerTag::getCustomerTagSid, sids));
        for (Long id : sids) {
            //校验是否存在待办
            basCustomerTag.setCustomerTagSid(id);
            checkTodoExist(basCustomerTag);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
        }
        return sids.length;
    }


}
