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
import com.platform.ems.domain.BasVendorTag;
import com.platform.ems.domain.BasVendorTagItem;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.mapper.BasVendorTagItemMapper;
import com.platform.ems.mapper.BasVendorTagMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.service.IBasVendorTagService;
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
 * 供应商标签(分组)Service业务层处理
 *
 * @author c
 * @date 2022-03-30
 */
@Service
@SuppressWarnings("all")
public class BasVendorTagServiceImpl extends ServiceImpl<BasVendorTagMapper, BasVendorTag> implements IBasVendorTagService {
    @Autowired
    private BasVendorTagMapper basVendorTagMapper;
    @Autowired
    private BasVendorTagItemMapper basVendorTagItemMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商标签(分组)";

    /**
     * 查询供应商标签(分组)
     *
     * @param vendorTagSid 供应商标签(分组)ID
     * @return 供应商标签(分组)
     */
    @Override
    public BasVendorTag selectBasVendorTagById(Long vendorTagSid) {
        BasVendorTag basVendorTag = basVendorTagMapper.selectBasVendorTagById(vendorTagSid);
        if (basVendorTag == null) {
            return null;
        }
        List<BasVendorTagItem> itemList =
                basVendorTagItemMapper.selectBasVendorTagItemList(new BasVendorTagItem().setVendorTagSid(vendorTagSid));
        basVendorTag.setItemList(itemList);
        MongodbUtil.find(basVendorTag);
        return basVendorTag;
    }

    /**
     * 查询供应商标签(分组)列表
     *
     * @param basVendorTag 供应商标签(分组)
     * @return 供应商标签(分组)
     */
    @Override
    public List<BasVendorTag> selectBasVendorTagList(BasVendorTag basVendorTag) {
        return basVendorTagMapper.selectBasVendorTagList(basVendorTag);
    }

    /**
     * 新增供应商标签(分组)
     * 需要注意编码重复校验
     *
     * @param basVendorTag 供应商标签(分组)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorTag(BasVendorTag basVendorTag) {
        List<BasVendorTag> list = selectBasVendorTagList(new BasVendorTag().setVendorTagName(basVendorTag.getVendorTagName()));
        if (CollUtil.isNotEmpty(list)) {
            throw new BaseException("供应商分组名称已存在");
        }
        //设置确认信息
        setConfirmInfo(basVendorTag);
        int row = basVendorTagMapper.insert(basVendorTag);
        if (row > 0) {
            //供应商分组-明细
            List<BasVendorTagItem> itemList = basVendorTag.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                addBasVendorTagItem(basVendorTag, itemList);
            }
            //待办通知
            BasVendorTag vendorTag = new BasVendorTag();
            vendorTag = basVendorTagMapper.selectBasVendorTagById(basVendorTag.getVendorTagSid());
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(basVendorTag.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.GYSFZ)
                        .setDocumentSid(basVendorTag.getVendorTagSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("供应商分组" + vendorTag.getVendorTagCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(vendorTag.getVendorTagCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(basVendorTag.getVendorTagSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(BasVendorTag o) {
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
    private void addBasVendorTagItem(BasVendorTag basVendorTag, List<BasVendorTagItem> itemList) {
        deleteItem(basVendorTag);
        itemList.forEach(o -> {
            o.setVendorTagSid(basVendorTag.getVendorTagSid());
        });
        basVendorTagItemMapper.inserts(itemList);
    }

    /**
     * 删除明细
     */
    private void deleteItem(BasVendorTag basVendorTag) {
        basVendorTagItemMapper.delete(
                new UpdateWrapper<BasVendorTagItem>()
                        .lambda()
                        .eq(BasVendorTagItem::getVendorTagSid, basVendorTag.getVendorTagSid())
        );
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(BasVendorTag basVendorTag) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, basVendorTag.getVendorTagSid()));
        if (CollUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, basVendorTag.getVendorTagSid()));
        }
    }


    /**
     * 修改供应商标签(分组)
     *
     * @param basVendorTag 供应商标签(分组)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorTag(BasVendorTag basVendorTag) {
        //校验名称是否重复
        checkNameUnique(basVendorTag);
        BasVendorTag response = basVendorTagMapper.selectBasVendorTagById(basVendorTag.getVendorTagSid());
        //设置确认信息
        setConfirmInfo(basVendorTag);
        int row = basVendorTagMapper.updateById(basVendorTag);
        if (row > 0) {
            //供应商分组-明细
            List<BasVendorTagItem> itemList = basVendorTag.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                itemList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addBasVendorTagItem(basVendorTag, itemList);
            } else {
                deleteItem(basVendorTag);
            }

            if (!ConstantsEms.SAVA_STATUS.equals(basVendorTag.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(basVendorTag);
            }
            //插入日志
            MongodbUtil.insertUserLog(basVendorTag.getVendorTagSid(), BusinessType.UPDATE.getValue(), response, basVendorTag, TITLE);
        }
        return row;
    }

    /**
     * 校验名称是否重复
     */
    private void checkNameUnique(BasVendorTag basVendorTag) {
        List<BasVendorTag> list = selectBasVendorTagList(new BasVendorTag().setVendorTagName(basVendorTag.getVendorTagName()));
        if (CollUtil.isNotEmpty(list)) {
            list.forEach(o -> {
                if (!o.getVendorTagSid().equals(basVendorTag.getVendorTagSid())) {
                    throw new BaseException("供应商分组名称已存在");
                }
            });
        }
    }

    /**
     * 变更供应商标签(分组)
     *
     * @param basVendorTag 供应商标签(分组)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorTag(BasVendorTag basVendorTag) {
        //校验名称是否重复
        checkNameUnique(basVendorTag);
        BasVendorTag response = basVendorTagMapper.selectBasVendorTagById(basVendorTag.getVendorTagSid());
        //设置确认信息
        setConfirmInfo(basVendorTag);
        int row = basVendorTagMapper.updateAllById(basVendorTag);
        if (row > 0) {
            //供应商分组-明细
            List<BasVendorTagItem> itemList = basVendorTag.getItemList();
            if (CollUtil.isNotEmpty(itemList)) {
                itemList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                });
                addBasVendorTagItem(basVendorTag, itemList);
            } else {
                deleteItem(basVendorTag);
            }
            //插入日志
            MongodbUtil.insertUserLog(basVendorTag.getVendorTagSid(), BusinessType.CHANGE.getValue(), response, basVendorTag, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商标签(分组)
     *
     * @param vendorTagSids 需要删除的供应商标签(分组)ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorTagByIds(List<Long> vendorTagSids) {
        Integer count = basVendorTagMapper.selectCount(new QueryWrapper<BasVendorTag>().lambda()
                .eq(BasVendorTag::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(BasVendorTag::getVendorTagSid, vendorTagSids));
        if (count != vendorTagSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        BasVendorTag basVendorTag = new BasVendorTag();
        vendorTagSids.forEach(vendorTagSid -> {
            basVendorTag.setVendorTagSid(vendorTagSid);
            //校验是否存在待办
            checkTodoExist(basVendorTag);
        });
        basVendorTagItemMapper.delete(new UpdateWrapper<BasVendorTagItem>().lambda().in(BasVendorTagItem::getVendorTagSid, vendorTagSids));
        return basVendorTagMapper.deleteBatchIds(vendorTagSids);
    }

    /**
     * 启用/停用
     *
     * @param basVendorTag
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(BasVendorTag basVendorTag) {
        Long[] sids = basVendorTag.getVendorTagSidList();
        for (Long id : sids) {
            basVendorTag.setVendorTagSid(id);
            basVendorTagMapper.updateById(basVendorTag);
            //插入日志
            String remark = StrUtil.isEmpty(basVendorTag.getDisableRemark()) ? null : basVendorTag.getDisableRemark();
            MongodbDeal.status(basVendorTag.getVendorTagSid(), basVendorTag.getStatus(), null, TITLE, remark);
        }
        return sids.length;
    }


    /**
     * 更改确认状态
     *
     * @param basVendorTag
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(BasVendorTag basVendorTag) {
        Long[] sids = basVendorTag.getVendorTagSidList();
        List<String> codeList = new ArrayList<>();
        for (Long sid : sids) {
            BasVendorTag vendorTag = selectBasVendorTagById(sid);
            if (CollUtil.isEmpty(vendorTag.getItemList())) {
                codeList.add(vendorTag.getVendorTagCode());
            }
        }
        if (CollUtil.isNotEmpty(codeList)) {
            throw new BaseException("供应商分组" + codeList + "的明细行为空，无法确认！");
        }
        basVendorTagMapper.update(null, new UpdateWrapper<BasVendorTag>().lambda()
                .set(BasVendorTag::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .set(BasVendorTag::getConfirmDate, new Date())
                .set(BasVendorTag::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                .in(BasVendorTag::getVendorTagSid, sids));
        for (Long id : sids) {
            //校验是否存在待办
            basVendorTag.setVendorTagSid(id);
            checkTodoExist(basVendorTag);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
        }
        return sids.length;
    }
}
