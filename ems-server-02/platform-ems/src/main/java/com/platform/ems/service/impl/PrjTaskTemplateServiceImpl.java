package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.PrjTask;
import com.platform.ems.domain.PrjTaskTemplate;
import com.platform.ems.domain.PrjTaskTemplateItem;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.domain.dto.request.form.PrjTaskTemplateFormRequest;
import com.platform.ems.domain.dto.response.form.PrjTaskTemplateFormResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.PrjTaskMapper;
import com.platform.ems.mapper.PrjTaskTemplateItemMapper;
import com.platform.ems.mapper.PrjTaskTemplateMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.service.IPrjTaskTemplateItemService;
import com.platform.ems.service.IPrjTaskTemplateService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 项目任务模板Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-07
 */
@Service
@SuppressWarnings("all")
public class PrjTaskTemplateServiceImpl extends ServiceImpl<PrjTaskTemplateMapper, PrjTaskTemplate> implements IPrjTaskTemplateService {
    @Autowired
    private PrjTaskTemplateMapper prjTaskTemplateMapper;
    @Autowired
    private PrjTaskTemplateItemMapper prjTaskTemplateItemMapper;
    @Autowired
    private IPrjTaskTemplateItemService prjTaskTemplateItemService;
    @Autowired
    private PrjTaskMapper prjTaskMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    private static final String TITLE = "项目任务模板";

    /**
     * 查询项目任务模板
     *
     * @param taskTemplateSid 项目任务模板ID
     * @return 项目任务模板
     */
    @Override
    public PrjTaskTemplate selectPrjTaskTemplateById(Long taskTemplateSid) {
        PrjTaskTemplate prjTaskTemplate = prjTaskTemplateMapper.selectPrjTaskTemplateById(taskTemplateSid);
        // 查询行数量
        Long[] sids = new Long[]{prjTaskTemplate.getTaskTemplateSid()};
        HashMap<Long, PrjTaskTemplate> map = prjTaskTemplateItemMapper.selectItemCountGroupByTemplateSid(
                new PrjTaskTemplateItem().setTaskTemplateSidList(sids));
        if (map.containsKey(prjTaskTemplate.getTaskTemplateSid())) {
            prjTaskTemplate.setItemCount(map.get(prjTaskTemplate.getTaskTemplateSid()).getItemCount());
        }
        // 明细列表
        prjTaskTemplate.setTaskTemplateItemList(new ArrayList<>());
        List<PrjTaskTemplateItem> itemList = prjTaskTemplateItemService.selectPrjTaskTemplateItemListById(taskTemplateSid);
        if (CollectionUtil.isNotEmpty(itemList)) {
            // 排序
            itemList = itemList.stream().sorted(Comparator.comparing(PrjTaskTemplateItem::getSort, Comparator.nullsFirst(BigDecimal::compareTo))
                    .thenComparing(PrjTaskTemplateItem::getTaskName, Comparator.nullsFirst(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
            ).collect(toList());
            // 明细前置节点处理
            for (int i = 0; i < itemList.size(); i++) {
                if (StrUtil.isNotBlank(itemList.get(i).getPreTask())) {
                    String[] preTaskList = itemList.get(i).getPreTask().split(";");
                    itemList.get(i).setPreTaskList(preTaskList);
                    // 前置任务节点
                    String preTaskName = "";
                    if (ArrayUtil.isNotEmpty(preTaskList)) {
                        List<PrjTask> taskList= prjTaskMapper.selectPrjTaskList(new PrjTask().setTaskCodeList(preTaskList));
                        if (CollectionUtil.isNotEmpty(taskList)) {
                            for (int j = 0; j < taskList.size(); j++) {
                                preTaskName = preTaskName + taskList.get(j).getTaskName() + ";";
                            }
                        }
                    }
                    if (StrUtil.isNotBlank(preTaskName)) {
                        preTaskName = preTaskName.substring(0, preTaskName.length()-1);
                        itemList.get(i).setPreTaskName(preTaskName);
                    }
                }
            }
            prjTaskTemplate.setTaskTemplateItemList(itemList);
        }
        MongodbUtil.find(prjTaskTemplate);
        return prjTaskTemplate;
    }

    /**
     * 查询项目任务模板列表
     *
     * @param prjTaskTemplate 项目任务模板
     * @return 项目任务模板
     */
    @Override
    public List<PrjTaskTemplate> selectPrjTaskTemplateList(PrjTaskTemplate prjTaskTemplate) {
        List<PrjTaskTemplate> response = prjTaskTemplateMapper.selectPrjTaskTemplateList(prjTaskTemplate);
        if (CollectionUtil.isNotEmpty(response)) {
            // 查询行数量
            Long[] sids = response.stream().map(PrjTaskTemplate::getTaskTemplateSid).toArray(Long[]::new);
            HashMap<Long, PrjTaskTemplate> map = prjTaskTemplateItemMapper.selectItemCountGroupByTemplateSid(
                    new PrjTaskTemplateItem().setTaskTemplateSidList(sids));
            for (PrjTaskTemplate taskTemplate : response) {
                if (map.containsKey(taskTemplate.getTaskTemplateSid())) {
                    taskTemplate.setItemCount(map.get(taskTemplate.getTaskTemplateSid()).getItemCount());
                }
            }
        }
        return response;
    }

    /**
     * 新增项目任务模板
     * 需要注意编码重复校验
     *
     * @param prjTaskTemplate 项目任务模板
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPrjTaskTemplate(PrjTaskTemplate prjTaskTemplate) {
        // 校验名称重复
        judgeName(prjTaskTemplate);
        // 其它校验
        judge(prjTaskTemplate);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(prjTaskTemplate.getHandleStatus())) {
            prjTaskTemplate.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 数据字段处理
        setData(prjTaskTemplate);
        int row = prjTaskTemplateMapper.insert(prjTaskTemplate);
        if (row > 0) {
            // 主要获取编码
            PrjTaskTemplate original = prjTaskTemplateMapper.selectById(prjTaskTemplate.getTaskTemplateSid());
            prjTaskTemplate.setTaskTemplateCode(original.getTaskTemplateCode());
            // 写入明细
            if (CollectionUtil.isNotEmpty(prjTaskTemplate.getTaskTemplateItemList())) {
                prjTaskTemplateItemService.insertPrjTaskTemplateItemList(prjTaskTemplate);
            }
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(prjTaskTemplate.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_PRJ_TASK_TEMPLATE)
                        .setDocumentSid(prjTaskTemplate.getTaskTemplateSid());
                sysTodoTask.setTitle("项目任务模板" + original.getTaskTemplateName() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(original.getTaskTemplateCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new PrjTaskTemplate(), prjTaskTemplate);
            MongodbDeal.insert(prjTaskTemplate.getTaskTemplateSid(), prjTaskTemplate.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 数据字段处理
     *
     * @param prjTaskTemplate 项目任务模板
     * @return 结果
     */
    private void setData(PrjTaskTemplate prjTaskTemplate) {
        // 明细行处理
        if (CollectionUtil.isNotEmpty(prjTaskTemplate.getTaskTemplateItemList())) {
            prjTaskTemplate.getTaskTemplateItemList().forEach(item->{
                // 前置节点处理
                item.setPreTask(null);
                if (ArrayUtil.isNotEmpty(item.getPreTaskList())) {
                    String preTask = "";
                    for (int i = 0; i < item.getPreTaskList().length; i++) {
                        preTask = preTask + item.getPreTaskList()[i] + ";";
                    }
                    item.setPreTask(preTask);
                }
            });
        }
    }

    /**
     * 校验
     *
     * @param prjTaskTemplate 项目任务模板
     * @return 结果
     */
    private void judge(PrjTaskTemplate prjTaskTemplate) {
        // 确认状态明细不能为空
        if (ConstantsEms.CHECK_STATUS.equals(prjTaskTemplate.getHandleStatus())
                && CollectionUtil.isEmpty(prjTaskTemplate.getTaskTemplateItemList())) {
            throw new BaseException("任务明细不能为空！");
        }
        // 校验明细
        if (CollectionUtil.isNotEmpty(prjTaskTemplate.getTaskTemplateItemList())) {
            // 校验任务明细的序号不允许重复
            List<PrjTaskTemplateItem> haveSort = prjTaskTemplate.getTaskTemplateItemList().stream()
                    .filter(o->o.getSort() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(haveSort)) {
                Map<BigDecimal, PrjTaskTemplateItem> map = new HashMap<>();
                map = haveSort.stream().collect(Collectors.toMap(PrjTaskTemplateItem::getSort, Function.identity(), (t1,t2) -> t1));
                if (map.size() != haveSort.size()) {
                    throw new BaseException("任务明细的序号不允许重复！");
                }
            }
            // 校验明细的关联业务单据不能重复
            List<PrjTaskTemplateItem> temp2 = prjTaskTemplate.getTaskTemplateItemList()
                    .stream().filter(o->StrUtil.isNotBlank(o.getRelateBusinessFormCode())).collect(toList());
            Map<String, PrjTaskTemplateItem> map2 = temp2.stream().collect(Collectors.toMap(PrjTaskTemplateItem::getRelateBusinessFormCode, Function.identity(), (t1,t2) -> t1));
            if (map2.size() != temp2.size()) {
                throw new BaseException("存在重复关联业务单据的任务明细！");
            }
        }
    }

    /**
     * 校验名称不能重复
     *
     * @param prjTaskTemplate 项目任务模板
     * @return 结果
     */
    private void judgeName(PrjTaskTemplate prjTaskTemplate) {
        QueryWrapper<PrjTaskTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PrjTaskTemplate::getTaskTemplateName, prjTaskTemplate.getTaskTemplateName());
        if (prjTaskTemplate.getTaskTemplateSid() != null) {
            queryWrapper.lambda().ne(PrjTaskTemplate::getTaskTemplateSid, prjTaskTemplate.getTaskTemplateSid());
        }
        List<PrjTaskTemplate> templateNameList = prjTaskTemplateMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(templateNameList)) {
            throw new BaseException("任务模板名称已存在！");
        }
    }

    /**
     * 修改项目任务模板
     *
     * @param prjTaskTemplate 项目任务模板
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePrjTaskTemplate(PrjTaskTemplate prjTaskTemplate) {
        PrjTaskTemplate original = prjTaskTemplateMapper.selectPrjTaskTemplateById(prjTaskTemplate.getTaskTemplateSid());
        // 校验名称不能重复
        if (!prjTaskTemplate.getTaskTemplateName().equals(original.getTaskTemplateName())) {
            judgeName(prjTaskTemplate);
        }
        // 其它校验
        judge(prjTaskTemplate);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(prjTaskTemplate.getHandleStatus())) {
            prjTaskTemplate.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 数据字段处理
        setData(prjTaskTemplate);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, prjTaskTemplate);
        if (CollectionUtil.isNotEmpty(msgList)) {
            prjTaskTemplate.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = prjTaskTemplateMapper.updateAllById(prjTaskTemplate);
        if (row > 0) {
            // 修改明细
            prjTaskTemplateItemService.updatePrjTaskTemplateItemList(prjTaskTemplate);
            // 不是保存状态删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(prjTaskTemplate.getHandleStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, prjTaskTemplate.getTaskTemplateSid())
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_TASK_TEMPLATE));
            }
            //插入日志
            MongodbDeal.update(prjTaskTemplate.getTaskTemplateSid(), original.getHandleStatus(), prjTaskTemplate.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更项目任务模板
     *
     * @param prjTaskTemplate 项目任务模板
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePrjTaskTemplate(PrjTaskTemplate prjTaskTemplate) {
        PrjTaskTemplate response = prjTaskTemplateMapper.selectPrjTaskTemplateById(prjTaskTemplate.getTaskTemplateSid());
        // 校验名称不能重复
        if (!prjTaskTemplate.getTaskTemplateName().equals(response.getTaskTemplateName())) {
            judgeName(prjTaskTemplate);
        }
        // 其它校验
        judge(prjTaskTemplate);
        // 数据字段处理
        setData(prjTaskTemplate);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, prjTaskTemplate);
        if (CollectionUtil.isNotEmpty(msgList)) {
            prjTaskTemplate.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = prjTaskTemplateMapper.updateAllById(prjTaskTemplate);
        if (row > 0) {
            // 修改明细
            prjTaskTemplateItemService.updatePrjTaskTemplateItemList(prjTaskTemplate);
            //插入日志
            MongodbUtil.insertUserLog(prjTaskTemplate.getTaskTemplateSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除项目任务模板
     *
     * @param taskTemplateSids 需要删除的项目任务模板ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePrjTaskTemplateByIds(List<Long> taskTemplateSids) {
        List<PrjTaskTemplate> list = prjTaskTemplateMapper.selectList(new QueryWrapper<PrjTaskTemplate>()
                .lambda().in(PrjTaskTemplate::getTaskTemplateSid, taskTemplateSids));
        // 删除校验
        list = list.stream().filter(o-> !HandleStatus.SAVE.getCode().equals(o.getHandleStatus()) && !HandleStatus.RETURNED.getCode().equals(o.getHandleStatus()))
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BaseException("只有保存或者已退回状态才允许删除！");
        }
        int row = prjTaskTemplateMapper.deleteBatchIds(taskTemplateSids);
        if (row > 0) {
            // 删除明细
            prjTaskTemplateItemService.deletePrjTaskTemplateItemByTemplete(taskTemplateSids);
            // 删除待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, taskTemplateSids)
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_TASK_TEMPLATE));
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new PrjTaskTemplate());
                MongodbUtil.insertUserLog(o.getTaskTemplateSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param prjTaskTemplate
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(PrjTaskTemplate prjTaskTemplate) {
        int row = 0;
        Long[] sids = prjTaskTemplate.getTaskTemplateSidList();
        if (sids != null && sids.length > 0) {
            row = prjTaskTemplateMapper.update(null, new UpdateWrapper<PrjTaskTemplate>().lambda().set(PrjTaskTemplate::getStatus, prjTaskTemplate.getStatus())
                    .in(PrjTaskTemplate::getTaskTemplateSid, sids));
            if (row == 0) {
                throw new BaseException("更改状态失败,请联系管理员");
            }
            for (Long id : sids) {
                //插入日志
                MongodbDeal.status(id, prjTaskTemplate.getStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param prjTaskTemplate
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(PrjTaskTemplate prjTaskTemplate) {
        int row = 0;
        Long[] sids = prjTaskTemplate.getTaskTemplateSidList();
        if (sids != null && sids.length > 0) {
            // 校验
            // 是否没有明细
            boolean noItem = true;
            List<PrjTaskTemplateItem> itemList = prjTaskTemplateItemMapper.selectList(new QueryWrapper<PrjTaskTemplateItem>()
                    .lambda().in(PrjTaskTemplateItem::getTaskTemplateSid, sids));
            if (CollectionUtil.isNotEmpty(itemList)) {
                Long[] itemSids = itemList.stream().map(PrjTaskTemplateItem::getTaskTemplateSid).distinct().toArray(Long[]::new);
                Set<Long> set = Arrays.stream(itemSids).collect(Collectors.toSet());
                int length = 0; length = set.size(); CollectionUtil.addAll(set, sids);
                if (length == set.size()) {
                    noItem = false;
                }
            }
            if (noItem) {
                throw new BaseException("存在任务明细为空的任务模板，确认失败！");
            }
            // 更新
            LambdaUpdateWrapper<PrjTaskTemplate> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(PrjTaskTemplate::getTaskTemplateSid, sids);
            updateWrapper.set(PrjTaskTemplate::getHandleStatus, prjTaskTemplate.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(prjTaskTemplate.getHandleStatus())) {
                updateWrapper.set(PrjTaskTemplate::getConfirmDate, new Date());
                updateWrapper.set(PrjTaskTemplate::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = prjTaskTemplateMapper.update(null, updateWrapper);
            if (row > 0) {
                // 删除待办
                if (ConstantsEms.CHECK_STATUS.equals(prjTaskTemplate.getHandleStatus())) {
                    sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                            .in(SysTodoTask::getDocumentSid, sids)
                            .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                            .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PRJ_TASK_TEMPLATE));
                }
                for (Long id : sids) {
                    // 插入日志
                    MongodbDeal.check(id, prjTaskTemplate.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

    @Override
    public List<PrjTaskTemplateFormResponse> selectPrjTaskTemplateForm(PrjTaskTemplateFormRequest prjTaskTemplate) {
        return prjTaskTemplateMapper.selectPrjTaskTemplateForm(prjTaskTemplate);
    }

}
