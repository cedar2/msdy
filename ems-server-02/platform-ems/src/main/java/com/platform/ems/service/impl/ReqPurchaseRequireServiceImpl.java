package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.constant.ConstantsTask;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConBuTypePurchaseRequire;
import com.platform.ems.plug.domain.ConDocTypePurchaseRequire;
import com.platform.ems.plug.mapper.ConBuTypePurchaseRequireMapper;
import com.platform.ems.plug.mapper.ConDocTypePurchaseRequireMapper;
import com.platform.ems.service.IReqPurchaseRequireService;
import com.platform.ems.service.ISysTodoTaskService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 申请单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-06
 */
@Service
@SuppressWarnings("all")
public class ReqPurchaseRequireServiceImpl extends ServiceImpl<ReqPurchaseRequireMapper, ReqPurchaseRequire> implements IReqPurchaseRequireService {
    @Autowired
    private ReqPurchaseRequireMapper reqPurchaseRequireMapper;
    @Autowired
    private ReqPurchaseRequireItemMapper reqPurchaseRequireItemMapper;
    @Autowired
    private ReqPurchaseRequireAttachmentMapper reqPurchaseRequireAttachmentMapper;
    @Autowired
    private ConDocTypePurchaseRequireMapper conDocTypePurchaseRequireMapper;
    @Autowired
    private ConBuTypePurchaseRequireMapper conBuTypePurchaseRequireMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private BasDepartmentMapper basDepartmentMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SysDefaultSettingClientMapper defaultSettingClientMapper;
    @Autowired
    private ISysTodoTaskService sysTodoTaskService;
    @Autowired
    private IWorkFlowService workFlowService;
    @Autowired
    private RemoteMenuService remoteMenuService;

    private static final String TITLE = "申请单";

    /**
     * 查询申请单
     *
     * @param purchaseRequireSid 申请单ID
     * @return 申请单
     */
    @Override
    public ReqPurchaseRequire selectReqPurchaseRequireById(Long purchaseRequireSid) {
        ReqPurchaseRequire reqPurchaseRequire = reqPurchaseRequireMapper.selectReqPurchaseRequireById(purchaseRequireSid);
        if (reqPurchaseRequire == null) {
            return null;
        }
        //申请单明细对象
        ReqPurchaseRequireItem reqPurchaseRequireItem = new ReqPurchaseRequireItem();
        reqPurchaseRequireItem.setPurchaseRequireSid(purchaseRequireSid);
        List<ReqPurchaseRequireItem> reqPurchaseRequireItemList = reqPurchaseRequireItemMapper.selectReqPurchaseRequireItemList(reqPurchaseRequireItem);
        //申请单附件对象
        ReqPurchaseRequireAttachment reqRequireDocAttachment = new ReqPurchaseRequireAttachment();
        reqRequireDocAttachment.setPurchaseRequireSid(purchaseRequireSid);
        List<ReqPurchaseRequireAttachment> reqPurchaseRequireAttachmentList = reqPurchaseRequireAttachmentMapper.selectReqPurchaseRequireAttachmentList(reqRequireDocAttachment);

        reqPurchaseRequire.setReqPurchaseRequireItemList(reqPurchaseRequireItemList);
        reqPurchaseRequire.setAttachmentList(reqPurchaseRequireAttachmentList);
        MongodbUtil.find(reqPurchaseRequire);
        return reqPurchaseRequire;
    }

    /**
     * 查询申请单列表
     *
     * @param reqPurchaseRequire 申请单
     * @return 申请单
     */
    @Override
    public List<ReqPurchaseRequire> selectReqPurchaseRequireList(ReqPurchaseRequire reqPurchaseRequire) {
        return reqPurchaseRequireMapper.selectReqPurchaseRequireList(reqPurchaseRequire);
    }

    /**
     * 新增申请单
     * 需要注意编码重复校验
     *
     * @param reqPurchaseRequire 申请单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertReqPurchaseRequire(ReqPurchaseRequire reqPurchaseRequire) {
        String handleStatus = "";
        handleStatus = reqPurchaseRequire.getHandleStatus();
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        if (ConstantsEms.SUBMIT_STATUS.equals(reqPurchaseRequire.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowPurchaseRequire())) {
                flag = false;
                remark = "提交并确认";
                reqPurchaseRequire.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        setConfirmInfo(reqPurchaseRequire);
        reqPurchaseRequireMapper.insert(reqPurchaseRequire);
        //申请单明细对象
        List<ReqPurchaseRequireItem> reqPurchaseRequireItemList = reqPurchaseRequire.getReqPurchaseRequireItemList();
        if (CollectionUtils.isNotEmpty(reqPurchaseRequireItemList)) {
            addReqPurchaseRequireItem(reqPurchaseRequire, reqPurchaseRequireItemList);
        }
        //申请单附件对象
        List<ReqPurchaseRequireAttachment> reqPurchaseRequireAttachmentList = reqPurchaseRequire.getAttachmentList();
        if (CollectionUtils.isNotEmpty(reqPurchaseRequireAttachmentList)) {
            addReqPurchaseRequireAttachment(reqPurchaseRequire, reqPurchaseRequireAttachmentList);
        }
        ReqPurchaseRequire purchaseRequire = reqPurchaseRequireMapper.selectReqPurchaseRequireById(reqPurchaseRequire.getPurchaseRequireSid());
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(reqPurchaseRequire.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsTable.TABLE_PURCHASE_REQUIRE)
                    .setDocumentSid(reqPurchaseRequire.getPurchaseRequireSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("采购申请单" + purchaseRequire.getPurchaseRequireCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(purchaseRequire.getPurchaseRequireCode())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                // 获取菜单id
                SysMenu menu = new SysMenu();
                menu.setMenuName(ConstantsWorkbench.TODO_PUR_REQUIRE_INFO);
                menu = remoteMenuService.getInfoByName(menu).getData();
                if (menu != null && menu.getMenuId() != null) {
                    sysTodoTask.setMenuId(menu.getMenuId());
                }
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        }             // 提交启动审批
        else if (ConstantsEms.SUBMIT_STATUS.equals(purchaseRequire.getHandleStatus())) {
            // 启用审批流程
            if (flag) {
                this.submit(purchaseRequire);
            }
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        msgList = BeanUtils.eq(new ReqPurchaseRequire(), reqPurchaseRequire);
        if (ConstantsEms.CHECK_STATUS.equals(reqPurchaseRequire.getHandleStatus())) {
            reqPurchaseRequire.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
        }
        if (BusinessType.IMPORT.getValue().equals(reqPurchaseRequire.getImportType())) {
            MongodbDeal.insert(reqPurchaseRequire.getPurchaseRequireSid(), reqPurchaseRequire.getHandleStatus(), msgList, TITLE, remark, BusinessType.IMPORT.getValue());
        }
        else {
            MongodbDeal.insert(reqPurchaseRequire.getPurchaseRequireSid(), reqPurchaseRequire.getHandleStatus(), msgList, TITLE, remark);
        }
        return 1;
    }

    /**
     * 需求单明细对象
     */
    private void addReqPurchaseRequireItem(ReqPurchaseRequire reqPurchaseRequire, List<ReqPurchaseRequireItem> reqPurchaseRequireItemList) {
        int i = 1;
        Integer maxItemNum = reqPurchaseRequire.getMaxItemNum();
        if (maxItemNum != null) {
            i = maxItemNum + i;
        }
        for (ReqPurchaseRequireItem o : reqPurchaseRequireItemList) {
            o.setPurchaseRequireSid(reqPurchaseRequire.getPurchaseRequireSid());
            o.setItemNum(i);
            i++;
        }
        reqPurchaseRequireItemMapper.inserts(reqPurchaseRequireItemList);
    }

    private void deleteItem(ReqPurchaseRequire reqPurchaseRequire) {
        reqPurchaseRequireItemMapper.delete(
                new UpdateWrapper<ReqPurchaseRequireItem>()
                        .lambda()
                        .eq(ReqPurchaseRequireItem::getPurchaseRequireSid, reqPurchaseRequire.getPurchaseRequireSid())
        );
    }

    /**
     * 需求单附件对象
     */
    private void addReqPurchaseRequireAttachment(ReqPurchaseRequire reqPurchaseRequire, List<ReqPurchaseRequireAttachment> reqPurchaseRequireAttachmentList) {
//        deleteAttach(reqPurchaseRequire);
        reqPurchaseRequireAttachmentList.forEach(o -> {
            o.setPurchaseRequireSid(reqPurchaseRequire.getPurchaseRequireSid());
            reqPurchaseRequireAttachmentMapper.insert(o);
        });
    }

    private void deleteAttach(ReqPurchaseRequire reqPurchaseRequire) {
        reqPurchaseRequireAttachmentMapper.delete(
                new UpdateWrapper<ReqPurchaseRequireAttachment>()
                        .lambda()
                        .eq(ReqPurchaseRequireAttachment::getPurchaseRequireSid, reqPurchaseRequire.getPurchaseRequireSid())
        );
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ReqPurchaseRequire o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(ReqPurchaseRequire reqPurchaseRequire) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, reqPurchaseRequire.getPurchaseRequireSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, reqPurchaseRequire.getPurchaseRequireSid()));
        }
    }

    /**
     * 修改申请单
     *
     * @param reqPurchaseRequire 申请单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateReqPurchaseRequire(ReqPurchaseRequire reqPurchaseRequire) {
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        String handleStatus = "";
        handleStatus = reqPurchaseRequire.getHandleStatus();
        if (ConstantsEms.SUBMIT_STATUS.equals(reqPurchaseRequire.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowPurchaseRequire())) {
                flag = false;
                remark = "提交并确认";
                reqPurchaseRequire.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        setConfirmInfo(reqPurchaseRequire);
        ReqPurchaseRequire response = reqPurchaseRequireMapper.selectReqPurchaseRequireById(reqPurchaseRequire.getPurchaseRequireSid());
        reqPurchaseRequire.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        reqPurchaseRequireMapper.updateAllById(reqPurchaseRequire);
        //申请单明细对象
        List<ReqPurchaseRequireItem> requireItemList = reqPurchaseRequire.getReqPurchaseRequireItemList();
        operateItem(reqPurchaseRequire, requireItemList);
        //申请单附件对象
        List<ReqPurchaseRequireAttachment> requireAttachList = reqPurchaseRequire.getAttachmentList();
        operateAttachment(reqPurchaseRequire, requireAttachList);
        // 不是保存状态删除待办
        Long[] sids = new Long[]{reqPurchaseRequire.getPurchaseRequireSid()};
        if (!ConstantsEms.SAVA_STATUS.equals(reqPurchaseRequire.getHandleStatus())) {
            sysTodoTaskService.deleteSysTodoTaskList(sids,
                    reqPurchaseRequire.getHandleStatus(), ConstantsTable.TABLE_PURCHASE_REQUIRE);
        }
        // 提交启动审批
        if (ConstantsEms.SUBMIT_STATUS.equals(reqPurchaseRequire.getHandleStatus())) {
            // 启用审批流程
            if (flag) {
                this.submit(reqPurchaseRequire);
            }
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        msgList = BeanUtils.eq(response, reqPurchaseRequire);
        if (ConstantsEms.CHECK_STATUS.equals(reqPurchaseRequire.getHandleStatus())) {
            reqPurchaseRequire.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
        }
        MongodbDeal.update(reqPurchaseRequire.getPurchaseRequireSid(), response.getHandleStatus(), reqPurchaseRequire.getHandleStatus(), msgList, TITLE, remark);
        return 1;
    }

    /**
     * 申请单-明细
     */
    private void operateItem(ReqPurchaseRequire reqPurchaseRequire, List<ReqPurchaseRequireItem> requireItemList) {
        if (CollectionUtil.isNotEmpty(requireItemList)) {
            //最大行号
            List<Integer> itemNums = requireItemList.stream().filter(o -> o.getItemNum() != null).map(ReqPurchaseRequireItem::getItemNum).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(itemNums)) {
                Integer maxItemNum = itemNums.stream().max(Comparator.comparingLong(Integer::intValue)).get();
                reqPurchaseRequire.setMaxItemNum(maxItemNum);
            }
            //新增
            List<ReqPurchaseRequireItem> addList = requireItemList.stream().filter(o -> o.getPurchaseRequireItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addReqPurchaseRequireItem(reqPurchaseRequire, addList);
            }
            //编辑
            List<ReqPurchaseRequireItem> editList = requireItemList.stream().filter(o -> o.getPurchaseRequireItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    reqPurchaseRequireItemMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ReqPurchaseRequireItem> itemList = reqPurchaseRequireItemMapper.selectList(new QueryWrapper<ReqPurchaseRequireItem>().lambda()
                    .eq(ReqPurchaseRequireItem::getPurchaseRequireSid, reqPurchaseRequire.getPurchaseRequireSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ReqPurchaseRequireItem::getPurchaseRequireItemSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = requireItemList.stream().map(ReqPurchaseRequireItem::getPurchaseRequireItemSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                reqPurchaseRequireItemMapper.deleteBatchIds(result);
            }
        } else {
            deleteItem(reqPurchaseRequire);
        }
    }

    /**
     * 申请单-附件
     */
    private void operateAttachment(ReqPurchaseRequire reqPurchaseRequire, List<ReqPurchaseRequireAttachment> requireAttachList) {
        if (CollectionUtil.isNotEmpty(requireAttachList)) {
            //新增
            List<ReqPurchaseRequireAttachment> addList = requireAttachList.stream().filter(o -> o.getPurchaseRequireAttachmentSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addReqPurchaseRequireAttachment(reqPurchaseRequire, addList);
            }
            //编辑
            List<ReqPurchaseRequireAttachment> editList = requireAttachList.stream().filter(o -> o.getPurchaseRequireAttachmentSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    reqPurchaseRequireAttachmentMapper.updateAllById(o);
                });
            }
            //原有数据
            List<ReqPurchaseRequireAttachment> itemList =
                    reqPurchaseRequireAttachmentMapper.selectList(new QueryWrapper<ReqPurchaseRequireAttachment>().lambda()
                            .eq(ReqPurchaseRequireAttachment::getPurchaseRequireSid, reqPurchaseRequire.getPurchaseRequireSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(ReqPurchaseRequireAttachment::getPurchaseRequireAttachmentSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = requireAttachList.stream().map(ReqPurchaseRequireAttachment::getPurchaseRequireAttachmentSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                reqPurchaseRequireAttachmentMapper.deleteBatchIds(result);
            }
        } else {
            deleteAttach(reqPurchaseRequire);
        }
    }

    /**
     * 批量删除申请单
     *
     * @param purchaseRequireSids 需要删除的申请单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteReqPurchaseRequireByIds(Long[] purchaseRequireSids) {
        // 删除申请单
        reqPurchaseRequireMapper.deleteReqPurchaseRequireByIds(purchaseRequireSids);
        // 删除申请单明细
        reqPurchaseRequireItemMapper.deleteReqPurchaseRequireItemByIds(purchaseRequireSids);
        // 删除申请单附件
        reqPurchaseRequireAttachmentMapper.deleteReqPurchaseRequireAttachmentByIds(purchaseRequireSids);
        // 删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, purchaseRequireSids));
        return purchaseRequireSids.length;
    }

    /**
     * 作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancellationByIds(ReqPurchaseRequire reqPurchaseRequire) {
        int row = 1;
        if (ArrayUtil.isEmpty(reqPurchaseRequire.getPurchaseRequireSidList())) {
            return 0;
        }
        List<ReqPurchaseRequire> reqPurchaseRequires = reqPurchaseRequireMapper.selectList(new QueryWrapper<ReqPurchaseRequire>().lambda()
                .in(ReqPurchaseRequire::getPurchaseRequireSid, reqPurchaseRequire.getPurchaseRequireSidList())
                .eq(ReqPurchaseRequire::getHandleStatus, HandleStatus.CONFIRMED.getCode()));
        if (CollectionUtil.isEmpty(reqPurchaseRequires) || reqPurchaseRequire.getPurchaseRequireSidList().length != reqPurchaseRequires.size()) {
            throw new BaseException("所选数据非'已确认'状态，无法作废！");
        }
        if (StrUtil.isBlank(reqPurchaseRequire.getCancelRemark())) {
            throw new BaseException("作废说明不允许为空！");
        }
        reqPurchaseRequire.setHandleStatus(HandleStatus.INVALID.getCode());
        row = updateHandle(reqPurchaseRequire);
        // 操作日志
        for (ReqPurchaseRequire purchaseRequire : reqPurchaseRequires) {
            MongodbUtil.insertUserLog(purchaseRequire.getPurchaseRequireSid(), BusinessType.CANCEL.getValue(), null, TITLE, reqPurchaseRequire.getCancelRemark());
        }
        return row;
    }

    /**
     * 作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int closeByIds(ReqPurchaseRequire reqPurchaseRequire) {
        int row = 1;
        if (ArrayUtil.isEmpty(reqPurchaseRequire.getPurchaseRequireSidList())) {
            return 0;
        }
        List<ReqPurchaseRequire> reqPurchaseRequires = reqPurchaseRequireMapper.selectList(new QueryWrapper<ReqPurchaseRequire>().lambda()
                .in(ReqPurchaseRequire::getPurchaseRequireSid, reqPurchaseRequire.getPurchaseRequireSidList())
                .ne(ReqPurchaseRequire::getHandleStatus, HandleStatus.CLOSED.getCode()));
        if (CollectionUtil.isNotEmpty(reqPurchaseRequires)) {
            reqPurchaseRequire.setHandleStatus(HandleStatus.CLOSED.getCode());
            row = updateHandle(reqPurchaseRequire);
            // 操作日志
            for (ReqPurchaseRequire purchaseRequire : reqPurchaseRequires) {
                MongodbUtil.insertUserLog(purchaseRequire.getPurchaseRequireSid(), BusinessType.CLOSE.getValue(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 申购单提交
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(ReqPurchaseRequire require) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("formId", require.getPurchaseRequireSid());
        variables.put("formCode", require.getPurchaseRequireCode());
        variables.put("formType", FormType.PurchaseRequire.getCode());
        variables.put("startUserId", ApiThreadLocalUtil.get().getSysUser().getUserId());
        try {
            AjaxResult result = workFlowService.submitOnly(variables);
        } catch (BaseException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 更改处理状态
     *
     * @param sids, handleStatus
     * @return
     */
    private int updateHandle(ReqPurchaseRequire reqPurchaseRequire) {
        Long[] sids = reqPurchaseRequire.getPurchaseRequireSidList();
        String handleStatus = reqPurchaseRequire.getHandleStatus();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }
        LambdaUpdateWrapper<ReqPurchaseRequire> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ReqPurchaseRequire::getPurchaseRequireSid, sids);
        updateWrapper.set(ReqPurchaseRequire::getHandleStatus, handleStatus);
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            updateWrapper.set(ReqPurchaseRequire::getConfirmDate, new Date());
            updateWrapper.set(ReqPurchaseRequire::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        return reqPurchaseRequireMapper.update(null, updateWrapper);
    }

    /**
     * 申请单确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ReqPurchaseRequire reqPurchaseRequire) {
        int row = 0;
        Long[] sids = reqPurchaseRequire.getPurchaseRequireSidList();
        // 处理状态
        String handleStatus = "";
        handleStatus = reqPurchaseRequire.getHandleStatus();
        if (StrUtil.isNotBlank(reqPurchaseRequire.getBusinessType())) {
            handleStatus = ConstantsTask.backHandleByBusiness(reqPurchaseRequire.getBusinessType());
        }
        if (sids != null && sids.length > 0) {
            row = sids.length;
            // 获取数据
            List<ReqPurchaseRequire> list = reqPurchaseRequireMapper.selectReqPurchaseRequireList(new ReqPurchaseRequire().setPurchaseRequireSidList(reqPurchaseRequire.getPurchaseRequireSidList()));
            // 删除待办
            if (ConstantsEms.CHECK_STATUS.equals(handleStatus) || HandleStatus.RETURNED.getCode().equals(handleStatus)
                    || ConstantsEms.SUBMIT_STATUS.equals(handleStatus)) {
                sysTodoTaskService.deleteSysTodoTaskList(sids, handleStatus,
                        ConstantsTable.TABLE_PURCHASE_REQUIRE);
            }
            // 提交
            if (BusinessType.SUBMIT.getValue().equals(reqPurchaseRequire.getBusinessType())) {
                // 判断是否配置需要审批
                SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
                // 不启用审批流程
                if (settingClient != null && !ConstantsEms.YES.equals(settingClient.getIsWorkflowPurchaseRequire())) {
                    // 修改处理状态
                    ReqPurchaseRequire require = new ReqPurchaseRequire();
                    require.setPurchaseRequireSidList(sids).setHandleStatus(ConstantsEms.CHECK_STATUS);
                    this.updateHandle(require);
                    //插入日志
                    for (int i = 0; i < list.size(); i++) {
                        MongodbUtil.insertUserLog(list.get(i).getPurchaseRequireSid(),
                                BusinessType.SUBMIT.getValue(), null, TITLE, "提交并确认");
                    }
                    return row;
                }
                // 修改处理状态
                ReqPurchaseRequire require = new ReqPurchaseRequire();
                require.setPurchaseRequireSidList(sids).setHandleStatus(ConstantsEms.SUBMIT_STATUS);
                this.updateHandle(require);
                // 开启工作流
                for (int i = 0; i < list.size(); i++) {
                    this.submit(list.get(i));
                    //插入日志
                    MongodbUtil.insertUserLog(list.get(i).getPurchaseRequireSid(),
                            BusinessType.SUBMIT.getValue(), null, TITLE, null);
                }
            }
            // 审批
            if (BusinessType.APPROVED.getValue().equals(reqPurchaseRequire.getBusinessType())) {
                Map<String, List<ReqPurchaseRequire>> map = list.stream()
                        .collect(Collectors.groupingBy(o -> String.valueOf(o.getApprovalUserId())));
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                if (map != null) {
                    for (String key : map.keySet()) {
                        if (key == null || (!key.equals(userId.toString()) && !key.startsWith(userId.toString()+",")
                                && key.indexOf(","+userId.toString()) == -1 && key.indexOf(", "+userId.toString()) == -1)) {
                            throw new BaseException("您不是当前审批节点处理人，无法点击此按钮！");
                        }
                    }
                }
                // 审批意见
                String comment = "";
                for (int i = 0; i < list.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setBusinessKey(list.get(i).getPurchaseRequireSid().toString());
                    taskVo.setFormId(list.get(i).getPurchaseRequireSid());
                    taskVo.setFormCode(list.get(i).getPurchaseRequireCode().toString());
                    taskVo.setFormType(FormType.PurchaseRequire.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(reqPurchaseRequire.getComment());
                    try {
                        SysFormProcess process = workFlowService.approvalOnly(taskVo);
                        if ("2".equals(process.getFormStatus())) {
                            // 修改处理状态
                            ReqPurchaseRequire require = new ReqPurchaseRequire();
                            require.setPurchaseRequireSidList(new Long[]{list.get(i).getPurchaseRequireSid()}).setHandleStatus(ConstantsEms.CHECK_STATUS);
                            this.updateHandle(require);
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(list.get(i).getPurchaseRequireSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, comment);
                }
            }
            // 审批驳回
            else if (BusinessType.DISAPPROVED.getValue().equals(reqPurchaseRequire.getBusinessType())) {
                Map<String, List<ReqPurchaseRequire>> map = list.stream()
                        .collect(Collectors.groupingBy(o -> String.valueOf(o.getApprovalUserId())));
                Long userId = ApiThreadLocalUtil.get().getSysUser().getUserId();
                if (map != null) {
                    for (String key : map.keySet()) {
                        if (key == null || (!key.equals(userId.toString()) && !key.startsWith(userId.toString()+",")
                                && key.indexOf(","+userId.toString()) == -1 && key.indexOf(", "+userId.toString()) == -1)) {
                            throw new BaseException("您不是当前审批节点处理人，无法点击此按钮！");
                        }
                    }
                }
                // 审批意见
                String comment = "";
                for (int i = 0; i < list.size(); i++) {
                    FlowTaskVo taskVo = new FlowTaskVo();
                    taskVo.setType("1");
                    taskVo.setTargetKey("2");
                    taskVo.setBusinessKey(list.get(i).getPurchaseRequireSid().toString());
                    taskVo.setFormId(list.get(i).getPurchaseRequireSid());
                    taskVo.setFormCode(list.get(i).getPurchaseRequireCode().toString());
                    taskVo.setFormType(FormType.PurchaseRequire.getCode());
                    taskVo.setUserId(userId.toString());
                    taskVo.setComment(reqPurchaseRequire.getComment());
                    try {
                        SysFormProcess process = workFlowService.returnOnly(taskVo);
                        // 如果已经没有进程了
                        if (!"1".equals(process.getFormStatus())) {
                            // 修改处理状态
                            ReqPurchaseRequire require = new ReqPurchaseRequire();
                            require.setPurchaseRequireSidList(new Long[]{list.get(i).getPurchaseRequireSid()}).setHandleStatus(HandleStatus.RETURNED.getCode());
                            this.updateHandle(require);
                        }
                        comment = process.getRemark();
                    } catch (BaseException e) {
                        throw e;
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(list.get(i).getPurchaseRequireSid(),
                            BusinessType.APPROVAL.getValue(), null, TITLE, comment);
                }
            }
        }
        return row;
    }

    /**
     * 申请单变更
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int change(ReqPurchaseRequire reqPurchaseRequire) {
        // 判断是否配置需要审批
        boolean flag = true;
        String remark = null;
        String handleStatus = "";
        handleStatus = reqPurchaseRequire.getHandleStatus();
        if (ConstantsEms.SUBMIT_STATUS.equals(reqPurchaseRequire.getHandleStatus())) {
            SysDefaultSettingClient settingClient = defaultSettingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (settingClient == null || !ConstantsEms.YES.equals(settingClient.getIsWorkflowPurchaseRequire())) {
                flag = false;
                remark = "提交并确认";
                reqPurchaseRequire.setHandleStatus(ConstantsEms.CHECK_STATUS);
            }
        }
        Long purchaseRequireSid = reqPurchaseRequire.getPurchaseRequireSid();
        ReqPurchaseRequire response = reqPurchaseRequireMapper.selectReqPurchaseRequireById(purchaseRequireSid);
        //验证是否确认状态
        if (!HandleStatus.CONFIRMED.getCode().equals(response.getHandleStatus())) {
            throw new BaseException("仅确认状态才允许变更");
        }
        reqPurchaseRequireMapper.updateAllById(reqPurchaseRequire);
        //申请单明细对象
        List<ReqPurchaseRequireItem> requireItemList = reqPurchaseRequire.getReqPurchaseRequireItemList();
        operateItem(reqPurchaseRequire, requireItemList);
        //申请单附件对象
        List<ReqPurchaseRequireAttachment> requireAttachList = reqPurchaseRequire.getAttachmentList();
        operateAttachment(reqPurchaseRequire, requireAttachList);
        // 提交启动审批
        if (ConstantsEms.SUBMIT_STATUS.equals(reqPurchaseRequire.getHandleStatus())) {
            // 启用审批流程
            if (flag) {
                this.submit(reqPurchaseRequire);
            }
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        msgList = BeanUtils.eq(response, reqPurchaseRequire);
        //插入日志
        MongodbUtil.insertUserLog(reqPurchaseRequire.getPurchaseRequireSid(), BusinessType.CHANGE.getValue(), msgList, TITLE, remark);
        return 1;
    }

    /**
     * 导入申请单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importData(MultipartFile file) {
        int num = 0, row = 1;
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();

            ReqPurchaseRequire reqPurchaseRequire = new ReqPurchaseRequire();
            List<ReqPurchaseRequireItem> reqPurchaseRequireItemList = new ArrayList<>();

            // 错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();

            // 商品/物料类别
            List<DictData> materialCategoryList = sysDictDataService.selectDictData("s_material_category");
            materialCategoryList = materialCategoryList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> materialCategoryMaps = materialCategoryList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 单据类型
            List<ConDocTypePurchaseRequire> documentTypeList = conDocTypePurchaseRequireMapper.selectConDocTypePurchaseRequireList(
                    new ConDocTypePurchaseRequire().setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS));
            Map<String, String> documentTypeMaps = documentTypeList.stream().collect(Collectors.toMap(ConDocTypePurchaseRequire::getName,
                    ConDocTypePurchaseRequire::getCode, (key1, key2) -> key2));
            // 业务类型
            List<ConBuTypePurchaseRequire> businessTypeList = conBuTypePurchaseRequireMapper.selectConBuTypePurchaseRequireList(
                    new ConBuTypePurchaseRequire().setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS));
            Map<String, String> businessTypeMaps = businessTypeList.stream().collect(Collectors.toMap(ConBuTypePurchaseRequire::getName,
                    ConBuTypePurchaseRequire::getCode, (key1, key2) -> key2));
            // 公司
            List<BasCompany> companyList = basCompanyMapper.selectBasCompanyList(new BasCompany());
            Map<String, BasCompany> companyMaps = companyList.stream().collect(Collectors.toMap(BasCompany::getShortName, Function.identity(), (key1, key2) -> key2));
            // 需求季
            List<BasProductSeason> productSeasonList = basProductSeasonMapper.selectBasProductSeasonList(new BasProductSeason());
            Map<String, BasProductSeason> productSeasonMaps = productSeasonList.stream().collect(Collectors.toMap(BasProductSeason::getProductSeasonName, Function.identity(),(key1, key2) -> key2));

            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

            for (int i = 0; i < readAll.size(); i++) {
                num = i + 1;

                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);

                    //商品/物料类别
                    String materialCategoryString = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                    String materialCategory = null;
                    if (StrUtil.isBlank(materialCategoryString)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品/物料类别不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if (StrUtil.isBlank(materialCategoryMaps.get(materialCategoryString))) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品/物料类别填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            materialCategory = materialCategoryMaps.get(materialCategoryString);
                        }
                    }
                    // 单据类型
                    String documentTypeName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                    String documentType = null;
                    if (StrUtil.isBlank(documentTypeName)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("单据类型名称不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if (StrUtil.isBlank(documentTypeMaps.get(documentTypeName))) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("单据类型名称填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            documentType = documentTypeMaps.get(documentTypeName);
                        }
                    }
                    // 业务类型
                    String businessTypeName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                    String businessType = null;
                    if (StrUtil.isBlank(businessTypeName)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("业务类型名称不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if (StrUtil.isBlank(businessTypeMaps.get(businessTypeName))) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("业务类型名称填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            businessType = businessTypeMaps.get(businessTypeName);
                        }
                    }
                    // 公司简称
                    String companyShortName = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                    Long companySid = null;
                    String companyCode = null;
                    if (StrUtil.isBlank(companyShortName)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("公司简称不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if (!companyMaps.containsKey(companyShortName)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("公司简称为“"+ companyShortName +"”不存在，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            BasCompany company = companyMaps.get(companyShortName);
                            if (ConstantsEms.DISENABLE_STATUS.equals(company.getStatus()) ||
                                    !ConstantsEms.CHECK_STATUS.equals(company.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("公司简称“" + companyShortName + "”必须是确认且启用的数据，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            else {
                                companySid = company.getCompanySid();
                                companyCode = company.getCompanyCode();
                            }
                        }
                    }
                    // 单据日期
                    String documentDate_s = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                    Date documentDate = null;
                    if (StrUtil.isBlank(documentDate_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("单据日期不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        try {
                            documentDate = format.parse(documentDate_s);
                        } catch (Exception e) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("单据日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    // 需求季
                    String productSeasonName = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                    Long productSeasonSid = null;
                    String productSeasonCode = null;
                    if (StrUtil.isNotBlank(productSeasonName)) {
                        if (!productSeasonMaps.containsKey(productSeasonName)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("需求季为“"+ productSeasonName +"”不存在，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            BasProductSeason productSeason = productSeasonMaps.get(productSeasonName);
                            if (ConstantsEms.DISENABLE_STATUS.equals(productSeason.getStatus()) ||
                                    !ConstantsEms.CHECK_STATUS.equals(productSeason.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("需求季“" + productSeasonName + "”必须是确认且启用的数据，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            else {
                                productSeasonSid = productSeason.getProductSeasonSid();
                                productSeasonCode = productSeason.getProductSeasonCode();
                            }
                        }
                    }
                    // 申请部门名称
                    String departmentName = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                    Long departmentSid = null;
                    String departmentCode = null;
                    if (StrUtil.isNotBlank(departmentName)) {
                        if (companySid != null){
                            try {
                                BasDepartment basDepartment = basDepartmentMapper.selectOne(new QueryWrapper<BasDepartment>().lambda()
                                        .eq(BasDepartment::getDepartmentName, departmentName)
                                        .eq(BasDepartment::getCompanySid, companySid));
                                if (basDepartment == null){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("“" + companyShortName + "”公司下的“" + departmentName + "”部门名称不存在，导入失败！");
                                    errMsgList.add(errMsg);
                                }else {
                                    if (!ConstantsEms.CHECK_STATUS.equals(basDepartment.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(basDepartment.getStatus())){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("对应的申请部门必须是确认且已启用的状态，导入失败！");
                                        errMsgList.add(errMsg);
                                    }else {
                                        departmentSid = basDepartment.getDepartmentSid();
                                        departmentCode = basDepartment.getDepartmentCode();
                                    }
                                }
                            }catch (Exception e){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("“" + companyShortName + "”公司下的“" + departmentName + "”部门名称存在重复，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                        else {
                            List<BasDepartment> basDepartmentList = basDepartmentMapper.selectList(new QueryWrapper<BasDepartment>().lambda()
                                    .eq(BasDepartment::getDepartmentName, departmentName));
                            if (CollectionUtil.isEmpty(basDepartmentList)){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(departmentName + "部门名称不存在，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                    // 收货人
                    String consignee = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                    if (StrUtil.isNotBlank(consignee) && consignee.length() > 30) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("收货人最大只能输入30位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    // 收货人联系电话
                    String consigneePhone = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                    if (StrUtil.isNotBlank(consigneePhone) && consigneePhone.length() > 20) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("收货人联系电话最大只能输入20位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    // 收货地址
                    String consigneeAddr = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                    // 备注
                    String remark = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();

                    reqPurchaseRequire.setMaterialCategory(materialCategory).setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setDocumentType(documentType).setBusinessType(businessType).setCompanySid(companySid)
                            .setCompanyCode(companyCode).setDocumentDate(documentDate).setProductSeasonSid(productSeasonSid)
                            .setProductSeasonCode(productSeasonCode).setRequireDepartmentSid(departmentSid)
                            .setRequireDepartmentCode(departmentCode).setConsignee(consignee).setConsigneePhone(consigneePhone)
                            .setConsigneeAddr(consigneeAddr).setRemark(remark).setImportType(BusinessType.IMPORT.getValue());
                }

                if (i < 5) {
                    continue;
                }

                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                ReqPurchaseRequireItem reqPurchaseRequireItem = new ReqPurchaseRequireItem();

                // 商品/物料编码
                String materialCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                Long materialSid = null;
                Long barcodeSid = null;
                Long barcodeCode = null;
                String unitBase = null;
                if (StrUtil.isBlank(materialCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品/物料编码不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                }
                // SKU1名称
                String sku1Name = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                Long sku1Sid = null;
                String sku1Code = null;
                if (StrUtil.isBlank(sku1Name)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("SKU1名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {

                }
                // SKU2名称
                String sku2Name = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                Long sku2Sid = null;
                String sku2Code = null;

                if (StrUtil.isNotBlank(materialCode) && StrUtil.isNotBlank(sku1Name)) {
                    List<BasMaterialBarcode> materialBarcodeList = basMaterialBarcodeMapper.selectBasMaterialBarcodePrecise(
                            new BasMaterialBarcode().setMaterialCode(materialCode).setSku1Name(sku1Name).setSku2Name(sku2Name));
                    if (CollectionUtil.isEmpty(materialBarcodeList)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("不存在该“商品/物料编码+SKU1名称+SKU2名称”组合的商品条码，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        boolean dim = true;
                        if (StrUtil.isBlank(sku2Name)) {
                            materialBarcodeList = materialBarcodeList.stream().filter(o -> o.getSku2Sid() == null).collect(Collectors.toList());
                            if (CollectionUtil.isEmpty(materialBarcodeList)) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("不存在该“商品/物料编码+SKU1名称+SKU2名称”组合的商品条码，导入失败！");
                                errMsgList.add(errMsg);
                                dim = false;
                            }
                        }
                        if (dim) {
                            if (ConstantsEms.DISENABLE_STATUS.equals(materialBarcodeList.get(0).getStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("该“商品/物料编码+SKU1名称+SKU2名称”组合的商品条码非启用状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            else {
                                barcodeSid = materialBarcodeList.get(0).getBarcodeSid();
                                barcodeCode = Long.parseLong(materialBarcodeList.get(0).getBarcode());
                                materialSid = materialBarcodeList.get(0).getMaterialSid();
                                materialCode = materialBarcodeList.get(0).getMaterialCode();
                                sku1Sid = materialBarcodeList.get(0).getSku1Sid();
                                sku1Code = materialBarcodeList.get(0).getSku1Code();
                                sku2Sid = materialBarcodeList.get(0).getSku2Sid();
                                sku2Code = materialBarcodeList.get(0).getSku2Code();
                                unitBase = materialBarcodeList.get(0).getUnitBase();
                            }
                        }
                    }
                }

                // 申请量
                String quantityS = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                BigDecimal quantity = null;
                if (StrUtil.isBlank(quantityS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("申请量不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDouble(quantityS,6,4)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("申请量，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        quantity = new BigDecimal(quantityS);
                        if (quantity != null && BigDecimal.ZERO.compareTo(quantity) > 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("申请量，必须大于等于0，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            quantity = quantity.divide(BigDecimal.ONE,4, BigDecimal.ROUND_HALF_UP);
                        }
                    }
                }

                // 需求日期
                String demandDateS = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                Date demandDate = null;
                if (StrUtil.isNotBlank(demandDateS)) {
                    if (!JudgeFormat.isValidDate(demandDateS)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("需求日期日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        demandDate = DateUtil.parse(demandDateS);
                    }
                }
                // 备注
                String remark = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();

                if (CollectionUtil.isEmpty(errMsgList)) {
                    reqPurchaseRequireItem.setMaterialCode(materialCode).setMaterialSid(materialSid)
                            .setBarcodeSid(barcodeSid).setBarcodeCode(barcodeCode).setUnitBase(unitBase)
                            .setSku1Sid(sku1Sid).setSku1Code(sku1Code).setSku2Sid(sku2Sid).setSku2Code(sku2Code)
                            .setQuantity(quantity).setDemandDate(demandDate).setRemark(remark);

                    reqPurchaseRequireItemList.add(reqPurchaseRequireItem);
                }
            }

            if (CollectionUtil.isNotEmpty(errMsgList)){
                return EmsResultEntity.error(errMsgList);
            }

            reqPurchaseRequire.setReqPurchaseRequireItemList(reqPurchaseRequireItemList);
            insertReqPurchaseRequire(reqPurchaseRequire);

        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }

        return EmsResultEntity.success(row);
    }

    /**
     * 申请单-明细对象
     */
    private void addInvGoodReceiptNoteItem(ReqPurchaseRequire reqPurchaseRequire, List<ReqPurchaseRequireItem> reqPurchaseRequireItemList) {
        reqPurchaseRequireItemList.forEach(o -> {
            o.setPurchaseRequireSid(reqPurchaseRequire.getPurchaseRequireSid());
            reqPurchaseRequireItemMapper.insert(o);
        });
    }

    private void copy(List<Object> objects, List<List<Object>> readAll) {
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        for (int i = lineSize; i < size; i++) {
            Object o = null;
            objects.add(o);
        }
    }
}
