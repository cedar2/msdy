package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ManProcess;
import com.platform.ems.domain.ManProcessRoute;
import com.platform.ems.domain.ManProcessRouteItem;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.domain.dto.request.ManProcessRouteActionRequest;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IManProcessRouteService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工艺路线Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-26
 */
@Service
@SuppressWarnings("all")
public class ManProcessRouteServiceImpl extends ServiceImpl<ManProcessRouteMapper, ManProcessRoute> implements IManProcessRouteService {
    @Autowired
    private ManProcessRouteMapper manProcessRouteMapper;
    @Autowired
    private ManProcessRouteItemMapper manProcessRouteItemMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private ManProcessMapper manProcessMapper;
    @Autowired
    private ManWorkCenterMapper manWorkCenterMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    private static final String TITLE = "工艺路线";


    /**
     * 查询工艺路线
     *
     * @param processRouteSid 工艺路线ID
     * @return 工艺路线
     */
    @Override
    public ManProcessRoute selectManProcessRouteById(Long processRouteSid) {
        //主表
        ManProcessRoute manProcessRoute = manProcessRouteMapper.selectManProcessRouteById(processRouteSid);
        if (manProcessRoute != null) {
            //明细表
            List<ManProcessRouteItem> manProcessRouteItems = manProcessRouteItemMapper.selectManProcessRouteItemList(new ManProcessRouteItem().setProcessRouteSid(processRouteSid));
            manProcessRouteItems = manProcessRouteItems.stream().sorted(Comparator.comparing(ManProcessRouteItem::getSerialNum, Comparator.nullsLast(BigDecimal::compareTo))
                    .thenComparing(ManProcessRouteItem::getProcessName, Collator.getInstance(Locale.CHINA))).collect(Collectors.toList());
            manProcessRoute.setListManProcessRouteItem(manProcessRouteItems);
        }
        MongodbUtil.find(manProcessRoute);
        return manProcessRoute;
    }

    @Override
    public  List<ManProcessRouteItem> monthGetManProcess(Long processRouteSid) {
            List<ManProcessRouteItem> manProcessRouteItems = manProcessRouteItemMapper.selectManProcessRouteItemList(new ManProcessRouteItem().setProcessRouteSid(processRouteSid));
            manProcessRouteItems = manProcessRouteItems.stream().sorted(Comparator.comparing(ManProcessRouteItem::getSerialNum, Comparator.nullsLast(BigDecimal::compareTo)))
                   .collect(Collectors.toList());
        return manProcessRouteItems;
    }
    /**
     * 查询工艺路线列表
     *
     * @param manProcessRoute 工艺路线
     * @return 工艺路线
     */
    @Override
    public List<ManProcessRoute> selectManProcessRouteList(ManProcessRoute manProcessRoute) {
        List<ManProcessRoute> manProcessRoutes = manProcessRouteMapper.selectManProcessRouteList(manProcessRoute);
        return manProcessRoutes;
    }

    /**
     * 新增工艺路线
     *
     * @param manProcessRoute 工艺路线
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManProcessRoute(ManProcessRoute manProcessRoute) {
        List<ManProcessRoute> processRouteCodeList = manProcessRouteMapper.selectList(new QueryWrapper<ManProcessRoute>().lambda()
                .eq(ManProcessRoute::getProcessRouteCode, manProcessRoute.getProcessRouteCode()));
        if (processRouteCodeList.size() > 0) {
            throw new BaseException("工艺路线编码已存在！");
        }
        List<ManProcessRoute> processRouteNameList = manProcessRouteMapper.selectList(new QueryWrapper<ManProcessRoute>().lambda()
                .eq(ManProcessRoute::getProcessRouteName, manProcessRoute.getProcessRouteName()));
        if (processRouteNameList.size() > 0) {
            throw new BaseException("工艺路线名称已存在！");
        }
        setConfirmInfo(manProcessRoute);
        int row = manProcessRouteMapper.insert(manProcessRoute);
        Long processRouteSid = manProcessRoute.getProcessRouteSid();
        List<ManProcessRouteItem> listManProcessRouteItem = manProcessRoute.getListManProcessRouteItem();
        if (CollectionUtils.isNotEmpty(listManProcessRouteItem)) {
            addManProcessRouteItem(manProcessRoute, listManProcessRouteItem);
        }
        ManProcessRoute processRoute = manProcessRouteMapper.selectManProcessRouteById(manProcessRoute.getProcessRouteSid());
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(manProcessRoute.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_OUTSOURCE_DELIVERY_NOTE)
                    .setDocumentSid(manProcessRoute.getProcessRouteSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("工艺路线" + processRoute.getProcessRouteCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(processRoute.getProcessRouteCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(manProcessRoute);
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbDeal.insert(manProcessRoute.getProcessRouteSid(), manProcessRoute.getHandleStatus(), msgList, TITLE, null);
        return row;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(ManProcessRoute manProcessRoute) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, manProcessRoute.getProcessRouteSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, manProcessRoute.getProcessRouteSid()));
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ManProcessRoute o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            List<ManProcessRouteItem> manProcessRouteItemList = o.getListManProcessRouteItem();
            if (CollectionUtil.isEmpty(manProcessRouteItemList)) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 校验名称是否重复
     */
    private void checkNameUnique(ManProcessRoute manProcessRoute) {
        List<ManProcessRoute> processRouteNameList = manProcessRouteMapper.selectList(new QueryWrapper<ManProcessRoute>().lambda()
                .eq(ManProcessRoute::getProcessRouteName, manProcessRoute.getProcessRouteName()));
        if (CollectionUtils.isNotEmpty(processRouteNameList)) {
            processRouteNameList.forEach(o -> {
                if (!manProcessRoute.getProcessRouteSid().equals(o.getProcessRouteSid())) {
                    throw new BaseException("工艺路线名称已存在！");
                }
            });
        }
    }

    /**
     * 更新行号
     */
    public void setSerialNum(List<ManProcessRouteItem> routeItemList){
        routeItemList = routeItemList.stream().sorted(Comparator.comparing(ManProcessRouteItem::getProcessName, Collator.getInstance(Locale.CHINA))).collect(Collectors.toList());
        BigDecimal num = BigDecimal.ZERO;
        for (ManProcessRouteItem item : routeItemList) {
            item.setSerialNum(num);
            num = num.add(BigDecimal.ONE);
        }
    }

    /**
     * 工作中心-工序对象
     */
    private void addManProcessRouteItem(ManProcessRoute manProcessRoute, List<ManProcessRouteItem> routeItemList) {
        routeItemList.forEach(o -> {
            o.setQuantityReferProcessCode(getQuantityReferProcessCode(o));
            o.setProcessRouteSid(manProcessRoute.getProcessRouteSid());
        });
        manProcessRouteItemMapper.inserts(routeItemList);
    }

    private void deleteManProcessRouteItem(ManProcessRoute manProcessRoute) {
        manProcessRouteItemMapper.delete(
                new UpdateWrapper<ManProcessRouteItem>()
                        .lambda()
                        .eq(ManProcessRouteItem::getProcessRouteSid, manProcessRoute.getProcessRouteSid())
        );
    }

    /**
     * 修改工艺路线
     *
     * @param manProcessRoute 工艺路线
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManProcessRoute(ManProcessRoute manProcessRoute) {
        //名称校验
        checkNameUnique(manProcessRoute);
        //设置确认信息
        setConfirmInfo(manProcessRoute);
        ManProcessRoute response = manProcessRouteMapper.selectManProcessRouteById(manProcessRoute.getProcessRouteSid());
        manProcessRoute.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = manProcessRouteMapper.updateAllById(manProcessRoute);
        //工艺路线-工序
        List<ManProcessRouteItem> routeItemList = manProcessRoute.getListManProcessRouteItem();
        operateItem(manProcessRoute, routeItemList);
        if (!ConstantsEms.SAVA_STATUS.equals(manProcessRoute.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(manProcessRoute);
        }
        //插入日志
        List<OperMsg> msgList = BeanUtils.eq(response, manProcessRoute);
        MongodbDeal.update(manProcessRoute.getProcessRouteSid(), response.getHandleStatus(), manProcessRoute.getHandleStatus(), msgList, TITLE, null);
        return row;
    }

    /**
     * 工艺路线-工序
     */
    private void operateItem(ManProcessRoute manProcessRoute, List<ManProcessRouteItem> routeItemList) {
        if (CollectionUtils.isNotEmpty(routeItemList)) {
            //新增
            List<ManProcessRouteItem> addList = routeItemList.stream().filter(o -> o.getProcessRouteProcessSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)){
                addManProcessRouteItem(manProcessRoute, addList);
            }
            //编辑
            List<ManProcessRouteItem> editList = routeItemList.stream().filter(o -> o.getProcessRouteProcessSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)){
                editList.forEach(o ->{
                    o.setQuantityReferProcessCode(getQuantityReferProcessCode(o));
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    manProcessRouteItemMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ManProcessRouteItem> itemList = manProcessRouteItemMapper.selectList(new QueryWrapper<ManProcessRouteItem>().lambda()
                    .eq(ManProcessRouteItem::getProcessRouteSid, manProcessRoute.getProcessRouteSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ManProcessRouteItem::getProcessRouteProcessSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = routeItemList.stream().map(ManProcessRouteItem::getProcessRouteProcessSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)){
                manProcessRouteItemMapper.deleteBatchIds(result);
            }
        } else {
            deleteManProcessRouteItem(manProcessRoute);
        }
    }

    /**
     * 变更工艺路线
     *
     * @param manProcessRoute 工艺路线
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int change(ManProcessRoute manProcessRoute) {
        //名称校验
        checkNameUnique(manProcessRoute);
        //设置确认信息
        setConfirmInfo(manProcessRoute);
        ManProcessRoute response = manProcessRouteMapper.selectManProcessRouteById(manProcessRoute.getProcessRouteSid());
        manProcessRoute.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = manProcessRouteMapper.updateAllById(manProcessRoute);
        List<ManProcessRouteItem> listManProcessRouteItem = manProcessRoute.getListManProcessRouteItem();
        //工艺路线-工序
        operateItem(manProcessRoute, listManProcessRouteItem);
        //插入日志
        List<OperMsg> msgList = BeanUtils.eq(response, manProcessRoute);
        MongodbDeal.update(manProcessRoute.getProcessRouteSid(), response.getHandleStatus(), manProcessRoute.getHandleStatus(), msgList, TITLE, null);
        return row;
    }

    /**
     * 获得完成量校验参考工序code
     *
     * @param manProcessRouteItem ManProcessRouteItem
     * @return 结果
     */
    private String getQuantityReferProcessCode(ManProcessRouteItem manProcessRouteItem){
        String processCode = null;
        if (manProcessRouteItem.getQuantityReferProcessSid() != null && StrUtil.isBlank(manProcessRouteItem.getQuantityTypeReferProcess())){
            throw new BaseException("“工序列表“页签中，参考工序所引用数量类型不能为空");
        }
        ManProcess manProcess = manProcessMapper.selectById(manProcessRouteItem.getQuantityReferProcessSid());
        if (manProcess != null){
            processCode = manProcess.getProcessCode();
        }
        return processCode;
    }

    /**
     * 批量删除工艺路线
     *
     * @param processRouteSids 需要删除的工艺路线ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManProcessRouteByIds(List<Long> processRouteSids) {
        Integer count = manProcessRouteMapper.selectCount(new QueryWrapper<ManProcessRoute>().lambda()
                .eq(ManProcessRoute::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(ManProcessRoute::getProcessRouteSid, processRouteSids));
        if (count != processRouteSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        ManProcessRoute manProcessRoute = new ManProcessRoute();
        processRouteSids.forEach(processRouteSid -> {
            manProcessRoute.setProcessRouteSid(processRouteSid);
            //校验是否存在待办
            checkTodoExist(manProcessRoute);
        });
        manProcessRouteItemMapper.delete(new UpdateWrapper<ManProcessRouteItem>().lambda()
                .in(ManProcessRouteItem::getProcessRouteSid, processRouteSids));
        return manProcessRouteMapper.deleteBatchIds(processRouteSids);
    }

    /**
     * 批量确认工艺路线
     *
     * @param
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(ManProcessRouteActionRequest action) {
        List<Long> processRouteSids = action.getProcessRouteSids();
        Integer count = manProcessRouteMapper.selectCount(new QueryWrapper<ManProcessRoute>().lambda()
                .eq(ManProcessRoute::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(ManProcessRoute::getProcessRouteSid, processRouteSids));
        if (count != processRouteSids.size()) {
            throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
        }
        ManProcessRoute manProcessRoute = new ManProcessRoute();
        for (Long id : processRouteSids) {
            List<ManProcessRouteItem> itemList = manProcessRouteItemMapper.selectList(new QueryWrapper<ManProcessRouteItem>().lambda()
                    .eq(ManProcessRouteItem::getProcessRouteSid, id));
            if (CollectionUtil.isEmpty(itemList)) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            manProcessRoute.setProcessRouteSid(id);
            //校验是否存在待办
            checkTodoExist(manProcessRoute);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.check(id, action.getHandleStatus(), msgList, TITLE, null);
        }
        manProcessRouteMapper.update(null, new UpdateWrapper<ManProcessRoute>().lambda()
                .set(ManProcessRoute::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .set(ManProcessRoute::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                .set(ManProcessRoute::getConfirmDate, new Date())
                .in(ManProcessRoute::getProcessRouteSid, processRouteSids));
        return processRouteSids.size();
    }

    /**
     * 启用/停用 工艺路线
     *
     * @param
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int status(ManProcessRouteActionRequest action) {
        List<Long> processRouteSids = action.getProcessRouteSids();
        String status = action.getStatus();
        manProcessRouteMapper.update(null, new UpdateWrapper<ManProcessRoute>().lambda()
                .set(ManProcessRoute::getStatus, status).in(ManProcessRoute::getProcessRouteSid, processRouteSids));
        for (Long id : processRouteSids) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.status(id, status, msgList, TITLE, null);
        }
        return processRouteSids.size();
    }

    /**
     * 款项类别下拉框列表
     */
    @Override
    public List<ManProcessRoute> getManProcessRouteList(ManProcessRoute manProcessRoute) {
        return manProcessRouteMapper.getManProcessRouteList(manProcessRoute);
    }
}
