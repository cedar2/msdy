package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IManProcessStepCompleteRecordItemService;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IManProcessStepCompleteRecordService;

import static java.util.stream.Collectors.toList;

/**
 * 商品道序完成量台账-主Service业务层处理
 *
 * @author chenkw
 * @date 2022-10-20
 */
@Service
@SuppressWarnings("all")
public class ManProcessStepCompleteRecordServiceImpl extends ServiceImpl<ManProcessStepCompleteRecordMapper, ManProcessStepCompleteRecord> implements IManProcessStepCompleteRecordService {
    @Autowired
    private ManProcessStepCompleteRecordMapper manProcessStepCompleteRecordMapper;
    @Autowired
    private ManProcessStepCompleteRecordItemMapper manProcessStepCompleteRecordItemMapper;
    @Autowired
    private ManProcessStepCompleteRecordAttachMapper manProcessStepCompleteRecordAttachMapper;
    @Autowired
    private PayProductProcessStepItemMapper payProductProcessStepItemMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private ManWorkCenterMapper manWorkCenterMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    @Autowired
    private IManProcessStepCompleteRecordItemService manProcessStepCompleteRecordItemService;

    private static final String TITLE = "商品道序完成量台账-主";

    /**
     * 查询商品道序完成量台账-主
     *
     * @param stepCompleteRecordSid 商品道序完成量台账-主ID
     * @return 商品道序完成量台账-主
     */
    @Override
    public ManProcessStepCompleteRecord selectManProcessStepCompleteRecordById(Long stepCompleteRecordSid) {
        ManProcessStepCompleteRecord manProcessStepCompleteRecord = manProcessStepCompleteRecordMapper.selectManProcessStepCompleteRecordById(stepCompleteRecordSid);
        if (manProcessStepCompleteRecord == null) {
            throw new BaseException("所选单号不存在");
        }
        manProcessStepCompleteRecord.setStepCompleteRecordItemList(new ArrayList<>());
        manProcessStepCompleteRecord.setAttachmentList(new ArrayList<>());
        // 明细
        List<ManProcessStepCompleteRecordItem> itemList = manProcessStepCompleteRecordItemService.selectManProcessStepCompleteRecordItemListById(stepCompleteRecordSid);
        if (CollectionUtil.isNotEmpty(itemList)) {
            manProcessStepCompleteRecord.setStepCompleteRecordItemList(itemList);
        }
        // 附件
        List<ManProcessStepCompleteRecordAttach> attachList = manProcessStepCompleteRecordAttachMapper.selectManProcessStepCompleteRecordAttachList(
                new ManProcessStepCompleteRecordAttach().setStepCompleteRecordSid(stepCompleteRecordSid));
        if (CollectionUtil.isNotEmpty(attachList)) {
            manProcessStepCompleteRecord.setAttachmentList(attachList);
        }
        // 操作日志
        MongodbUtil.find(manProcessStepCompleteRecord);
        return manProcessStepCompleteRecord;
    }

    /**
     * 复制商品道序完成量台账-主
     *
     * @param stepCompleteRecordSid 商品道序完成量台账-主ID
     * @return 商品道序完成量台账-主
     */
    @Override
    public ManProcessStepCompleteRecord copyManProcessStepCompleteRecordById(Long stepCompleteRecordSid) {
        ManProcessStepCompleteRecord manProcessStepCompleteRecord = manProcessStepCompleteRecordMapper.selectManProcessStepCompleteRecordById(stepCompleteRecordSid);
        if (manProcessStepCompleteRecord == null) {
            throw new BaseException("所选单号不存在");
        }
        manProcessStepCompleteRecord.setStepCompleteRecordItemList(new ArrayList<>());
        manProcessStepCompleteRecord.setAttachmentList(new ArrayList<>());
        manProcessStepCompleteRecord.setStepCompleteRecordSid(null).setStepCompleteRecordCode(null)
                .setHandleStatus(ConstantsEms.SAVA_STATUS).setCreateDate(null).setCreatorAccount(null)
                .setUpdateDate(null).setUpdaterAccount(null).setConfirmDate(null).setConfirmerAccount(null);
        // 明细
        List<ManProcessStepCompleteRecordItem> itemList = manProcessStepCompleteRecordItemMapper.selectManProcessStepCompleteRecordItemList(
                new ManProcessStepCompleteRecordItem().setStepCompleteRecordSid(stepCompleteRecordSid));
        if (CollectionUtil.isNotEmpty(itemList)) {
            itemList.forEach(item -> {
                item.setStepCompleteRecordItemSid(null).setStepCompleteRecordSid(null)
                        .setCreateDate(null).setCreatorAccount(null).setUpdateDate(null).setUpdaterAccount(null);
            });
            manProcessStepCompleteRecord.setStepCompleteRecordItemList(itemList);
        }
        return manProcessStepCompleteRecord;
    }

    /**
     * 查询商品道序完成量台账-主列表
     *
     * @param manProcessStepCompleteRecord 商品道序完成量台账-主
     * @return 商品道序完成量台账-主
     */
    @Override
    public List<ManProcessStepCompleteRecord> selectManProcessStepCompleteRecordList(ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        return manProcessStepCompleteRecordMapper.selectManProcessStepCompleteRecordList(manProcessStepCompleteRecord);
    }

    /**
     * 校验台账唯一性
     * 新建页面，“基本信息”弹窗中，点击“下一步”时，根据
     * “工厂(工序)、班组、操作部门、完成日期、商品工价类型、计薪完工类型、录入维度、商品编码”
     * 进行重复校验，相关提示信息需要修改为：
     * 工厂(工序)+班组+操作部门+商品工价类型+计薪完工类型+完成日期+商品编码(款号)+录入维度”组合已存在道序完成台账单，是否继续新建？
     * 若选择“确定”，则进入“道序完成台账新建”页面；若点击“取消”，则关闭提示弹窗
     *
     * @param manProcessStepCompleteRecord 商品道序完成量台账-主
     * @return 结果
     */
    @Override
    public EmsResultEntity verifyUnique(ManProcessStepCompleteRecord record) {
        // 唯一性校验
        QueryWrapper<ManProcessStepCompleteRecord> queryWrapper = new QueryWrapper<>();
        // 必填
        queryWrapper.lambda().eq(ManProcessStepCompleteRecord::getPlantSid, record.getPlantSid())
                .eq(ManProcessStepCompleteRecord::getWorkCenterSid, record.getWorkCenterSid())
                .eq(ManProcessStepCompleteRecord::getDepartment, record.getDepartment());
        // 选填
        if (record.getProductPriceType() == null) {
            queryWrapper.lambda().isNull(ManProcessStepCompleteRecord::getProductPriceType);
        } else {
            queryWrapper.lambda().eq(ManProcessStepCompleteRecord::getProductPriceType, record.getProductPriceType());
        }
        if (record.getJixinWangongType() == null) {
            queryWrapper.lambda().isNull(ManProcessStepCompleteRecord::getJixinWangongType);
        } else {
            queryWrapper.lambda().eq(ManProcessStepCompleteRecord::getJixinWangongType, record.getJixinWangongType());
        }
        if (record.getCompleteDate() == null) {
            queryWrapper.lambda().isNull(ManProcessStepCompleteRecord::getCompleteDate);
        } else {
            queryWrapper.lambda().eq(ManProcessStepCompleteRecord::getCompleteDate, record.getCompleteDate());
        }
        if (record.getProductSid() == null) {
            queryWrapper.lambda().isNull(ManProcessStepCompleteRecord::getProductSid);
        } else {
            queryWrapper.lambda().eq(ManProcessStepCompleteRecord::getProductSid, record.getProductSid());
        }
        if (record.getEnterDimension() == null) {
            queryWrapper.lambda().isNull(ManProcessStepCompleteRecord::getEnterDimension);
        } else {
            queryWrapper.lambda().eq(ManProcessStepCompleteRecord::getEnterDimension, record.getEnterDimension());
        }
        if (CollectionUtil.isNotEmpty(manProcessStepCompleteRecordMapper.selectList(queryWrapper))) {
            return EmsResultEntity.warning(null, "“工厂(工序)+班组+操作部门+商品工价类型+计薪完工类型+完成日期+商品编码(款号)+录入维度”组合已存在道序完成台账单，是否继续？");
        }
        return EmsResultEntity.success();
    }

    /**
     * 新增商品道序完成量台账-主
     * 需要注意编码重复校验
     *
     * @param manProcessStepCompleteRecord 商品道序完成量台账-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity insertManProcessStepCompleteRecord(ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        verify(manProcessStepCompleteRecord);
        setData(null, manProcessStepCompleteRecord);
        int row = manProcessStepCompleteRecordMapper.insert(manProcessStepCompleteRecord);
        if (row > 0) {
            // 写入明细
            if (CollectionUtil.isNotEmpty(manProcessStepCompleteRecord.getStepCompleteRecordItemList())) {
                manProcessStepCompleteRecordItemService.insertManProcessStepCompleteRecordItemList(manProcessStepCompleteRecord);
            }
            // 写入附件
            if (CollectionUtil.isNotEmpty(manProcessStepCompleteRecord.getAttachmentList())) {
                manProcessStepCompleteRecord.getAttachmentList().forEach(item->{
                    item.setStepCompleteRecordSid(manProcessStepCompleteRecord.getStepCompleteRecordSid());
                });
                manProcessStepCompleteRecordAttachMapper.inserts(manProcessStepCompleteRecord.getAttachmentList());
            }
            // 待办通知
            if (ConstantsEms.SAVA_STATUS.equals(manProcessStepCompleteRecord.getHandleStatus())) {
                ManProcessStepCompleteRecord record = manProcessStepCompleteRecordMapper.selectManProcessStepCompleteRecordById
                        (manProcessStepCompleteRecord.getStepCompleteRecordSid());
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_MAN_PROCESS_STEP_COMPELETE_RECORD)
                        .setDocumentSid(manProcessStepCompleteRecord.getStepCompleteRecordSid())
                        .setTitle("道序完成台账: " + record.getStepCompleteRecordCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(record.getStepCompleteRecordCode()))
                        .setNoticeDate(new Date()).setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ManProcessStepCompleteRecord(), manProcessStepCompleteRecord);
            MongodbDeal.insert(manProcessStepCompleteRecord.getStepCompleteRecordSid(), manProcessStepCompleteRecord.getHandleStatus(), msgList, TITLE, null);
            return EmsResultEntity.success(manProcessStepCompleteRecord.getStepCompleteRecordSid().toString(), "操作成功");
        }
        else {
            throw new BaseException("操作失败");
        }
    }

    /**
     * 一些简单的校验
     * @param manProcessStepCompleteRecord 商品道序完成量台账
     * @return 结果
     */
    private void verify(ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        List<ManProcessStepCompleteRecordItem> itemList = manProcessStepCompleteRecord.getStepCompleteRecordItemList();
        // 确认状态 明细不为空
        if (ConstantsEms.CHECK_STATUS.equals(manProcessStepCompleteRecord.getHandleStatus())) {
            if (CollectionUtil.isEmpty(itemList)) {
                throw new BaseException("道序明细为空，请检查！");
            }
        }
        // 明细重复性校验
        if (CollectionUtil.isNotEmpty(itemList)) {
            Map<String, ManProcessStepCompleteRecordItem> map = new HashMap<>();
            map = itemList.stream().collect(Collectors.toMap(o ->
                    String.valueOf(o.getWorkerSid())+"-"+String.valueOf(o.getProductSid())+"-"+String.valueOf(o.getSort())
                            +"-"+String.valueOf(o.getPaichanBatch()), Function.identity(), (t1, t2) -> t1));
            if (itemList.size() != map.size()) {
                throw new BaseException("存在相同的员工，商品编码(款号)，道序序号，排产批次号的完成量明细，请核实！");
            }
        }
    }

    /**
     * 写入一些需要后端自己获取的数据
     * @param old 商品道序完成量台账 数据库中原数据
     * @param manProcessStepCompleteRecord 商品道序完成量台账 新的
     * @return 结果
     */
    private void setData(ManProcessStepCompleteRecord old, ManProcessStepCompleteRecord manProcessStepCompleteRecord){
        if (manProcessStepCompleteRecord == null) {
            return;
        }
        if (old == null) {
            old = new ManProcessStepCompleteRecord();
        }
        // 确认状态
        if (!manProcessStepCompleteRecord.getHandleStatus().equals(old.getHandleStatus())
                && ConstantsEms.CHECK_STATUS.equals(manProcessStepCompleteRecord.getHandleStatus())) {
            manProcessStepCompleteRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 工厂
        if (manProcessStepCompleteRecord.getPlantSid() == null) {
            manProcessStepCompleteRecord.setPlantCode(null);
        } else if (!manProcessStepCompleteRecord.getPlantSid().equals(old.getPlantSid())) {
            setPlant((manProcessStepCompleteRecord));
        }
        // 班组
        if (manProcessStepCompleteRecord.getWorkCenterSid() == null) {
            manProcessStepCompleteRecord.setWorkCenterCode(null);
        } else if (!manProcessStepCompleteRecord.getWorkCenterSid().equals(old.getWorkCenterSid())) {
            setWorkCenter((manProcessStepCompleteRecord));
        }
        // 班组
        if (manProcessStepCompleteRecord.getProductSid() == null) {
            manProcessStepCompleteRecord.setProductCode(null);
        } else if (!manProcessStepCompleteRecord.getProductSid().equals(old.getProductSid())) {
            setMaterial((manProcessStepCompleteRecord));
        }
    }

    /**
     * 工厂
     */
    private void setPlant(ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        BasPlant plant = basPlantMapper.selectById(manProcessStepCompleteRecord.getPlantSid());
        if (plant != null) {
            manProcessStepCompleteRecord.setPlantCode(plant.getPlantCode());
        }
    }

    /**
     * 班组
     */
    private void setWorkCenter(ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        ManWorkCenter workCenter = manWorkCenterMapper.selectById(manProcessStepCompleteRecord.getWorkCenterSid());
        if (workCenter != null) {
            manProcessStepCompleteRecord.setWorkCenterCode(workCenter.getWorkCenterCode());
        }
    }

    /**
     * 商品
     */
    private void setMaterial(ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        BasMaterial material = basMaterialMapper.selectById(manProcessStepCompleteRecord.getProductSid());
        if (material != null) {
            manProcessStepCompleteRecord.setProductCode(material.getMaterialCode());
        }
    }

    /**
     * 修改商品道序完成量台账-主
     *
     * @param manProcessStepCompleteRecord 商品道序完成量台账-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManProcessStepCompleteRecord(ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        verify(manProcessStepCompleteRecord);
        ManProcessStepCompleteRecord original = manProcessStepCompleteRecordMapper.selectManProcessStepCompleteRecordById(manProcessStepCompleteRecord.getStepCompleteRecordSid());
        setData(original, manProcessStepCompleteRecord);
        int row = manProcessStepCompleteRecordMapper.updateById(manProcessStepCompleteRecord);
        if (row > 0) {
            // 确认操作删除待办
            if (ConstantsEms.CHECK_STATUS.equals(manProcessStepCompleteRecord.getHandleStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getDocumentSid, manProcessStepCompleteRecord.getStepCompleteRecordSid()));
            }
            // 修改明细
            manProcessStepCompleteRecordItemService.updateManProcessStepCompleteRecordItemList(manProcessStepCompleteRecord);
            // 修改附件
            this.updateManProcessStepCompleteRecordAttach(manProcessStepCompleteRecord);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, manProcessStepCompleteRecord);
            MongodbDeal.update(manProcessStepCompleteRecord.getStepCompleteRecordSid(), original.getHandleStatus(), manProcessStepCompleteRecord.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量修改附件信息
     *
     * @param manProcessStepCompleteRecord 商品道序完成量台账
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateManProcessStepCompleteRecordAttach(ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        // 先删后加
        manProcessStepCompleteRecordAttachMapper.delete(new QueryWrapper<ManProcessStepCompleteRecordAttach>().lambda()
                .eq(ManProcessStepCompleteRecordAttach::getStepCompleteRecordSid, manProcessStepCompleteRecord.getStepCompleteRecordSid()));
        if (CollectionUtil.isNotEmpty(manProcessStepCompleteRecord.getAttachmentList())) {
            manProcessStepCompleteRecord.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getAttachmentSid() == null) {
                    att.setStepCompleteRecordSid(manProcessStepCompleteRecord.getStepCompleteRecordSid());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            manProcessStepCompleteRecordAttachMapper.inserts(manProcessStepCompleteRecord.getAttachmentList());
        }
    }

    /**
     * 变更商品道序完成量台账-主
     *
     * @param manProcessStepCompleteRecord 商品道序完成量台账-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManProcessStepCompleteRecord(ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        verify(manProcessStepCompleteRecord);
        ManProcessStepCompleteRecord response = manProcessStepCompleteRecordMapper.selectManProcessStepCompleteRecordById(manProcessStepCompleteRecord.getStepCompleteRecordSid());
        setData(response, manProcessStepCompleteRecord);
        int row = manProcessStepCompleteRecordMapper.updateAllById(manProcessStepCompleteRecord);
        if (row > 0) {
            // 修改明细
            manProcessStepCompleteRecordItemService.updateManProcessStepCompleteRecordItemList(manProcessStepCompleteRecord);
            // 修改附件
            this.updateManProcessStepCompleteRecordAttach(manProcessStepCompleteRecord);
            //插入日志
            MongodbUtil.insertUserLog(manProcessStepCompleteRecord.getStepCompleteRecordSid(), BusinessType.CHANGE.getValue(), response, manProcessStepCompleteRecord, TITLE);
        }
        return row;
    }

    /**
     * 批量删除商品道序完成量台账-主
     *
     * @param stepCompleteRecordSids 需要删除的商品道序完成量台账-主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManProcessStepCompleteRecordByIds(List<Long> stepCompleteRecordSids) {
        List<ManProcessStepCompleteRecord> list = manProcessStepCompleteRecordMapper.selectList(new QueryWrapper<ManProcessStepCompleteRecord>()
                .lambda().in(ManProcessStepCompleteRecord::getStepCompleteRecordSid, stepCompleteRecordSids));
        int row = manProcessStepCompleteRecordMapper.deleteBatchIds(stepCompleteRecordSids);
        if (row > 0) {
            // 删除待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .in(SysTodoTask::getDocumentSid, stepCompleteRecordSids));
            // 删除明细
            manProcessStepCompleteRecordItemService.deleteManProcessStepCompleteRecordItemByRecordIds(stepCompleteRecordSids);
            // 删除附件
            manProcessStepCompleteRecordAttachMapper.delete(new QueryWrapper<ManProcessStepCompleteRecordAttach>().lambda()
                    .in(ManProcessStepCompleteRecordAttach::getStepCompleteRecordSid, stepCompleteRecordSids));
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ManProcessStepCompleteRecord());
                MongodbUtil.insertUserLog(o.getStepCompleteRecordSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param manProcessStepCompleteRecord
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ManProcessStepCompleteRecord manProcessStepCompleteRecord) {
        int row = 0;
        Long[] sids = manProcessStepCompleteRecord.getStepCompleteRecordSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<ManProcessStepCompleteRecord> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(ManProcessStepCompleteRecord::getStepCompleteRecordSid, sids);
            updateWrapper.set(ManProcessStepCompleteRecord::getHandleStatus, manProcessStepCompleteRecord.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(manProcessStepCompleteRecord.getHandleStatus())) {
                updateWrapper.set(ManProcessStepCompleteRecord::getConfirmDate, new Date());
                updateWrapper.set(ManProcessStepCompleteRecord::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = manProcessStepCompleteRecordMapper.update(null, updateWrapper);
            if (row > 0) {
                // 确认操作删除待办
                if (ConstantsEms.CHECK_STATUS.equals(manProcessStepCompleteRecord.getHandleStatus())) {
                    sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                            .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                            .in(SysTodoTask::getDocumentSid, sids));
                }
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, manProcessStepCompleteRecord.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

    /**
     * 道序完成台账明细报表数据获取
     * @param stepFinishDetail
     * @return
     */
    @Override
    public List<StepFinishDetail> itemProcessStepCompleteExportData( StepFinishDetail stepFinishDetail) {
        List<StepFinishDetail> stepFinishDetails = manProcessStepCompleteRecordItemMapper.selectManProcessStepFinishDetailList(stepFinishDetail);
        return stepFinishDetails;
    }

    /**
     * 员工完成量汇总数据
     * @param staffCompleteSummary
     * @return
     */
    @Override
    public List<StaffCompleteSummary> getStaffCompleteSummary(StaffCompleteSummary staffCompleteSummary) {
        List<StaffCompleteSummary> summaryList = manProcessStepCompleteRecordItemMapper.selectStaffCompleteSummary(staffCompleteSummary);
        return summaryList;
    }

    /**
     * 员工完成量汇总的查看详情
     */
    @Override
    public StaffCompleteSummaryTable getStaffCompleteSummaryTable(StaffCompleteSummary request) {
        StaffCompleteSummaryTable table = new StaffCompleteSummaryTable();
        BeanCopyUtils.copyProperties(request, table);
        table.setProcessList(new ArrayList<>());
        table.setStaffList(new ArrayList<>());
        // 通过“商品编码、工厂、操作部门、商品工价类型、计薪完工类型，从商品道序中获取对应的道序序号及其名称，将道序序号值作为列名显示
        List<PayProductProcessStepItem> productProcessStepItemList = payProductProcessStepItemMapper.selectPayProductProcessStepItemList(
                new PayProductProcessStepItem().setProductSid(request.getProductSid()).setPlantSid(request.getPlantSid())
                        .setDepartment(request.getDepartment())
                        .setProductPriceType(request.getProductPriceType()).setJixinWangongType(request.getJixinWangongType())
        );
        // 商品道序序号行
        List<StaffCompleteSummaryTableProcess> processList = new ArrayList<>();
//        Map<Long, StaffCompleteSummaryTableProcess> processMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(productProcessStepItemList)) {
            processList = BeanCopyUtils.copyListProperties(productProcessStepItemList,
                    StaffCompleteSummaryTableProcess::new);
            processList = processList.stream()
                    .sorted(Comparator.comparing(StaffCompleteSummaryTableProcess::getSort)).collect(toList());
            table.setProcessList(processList);
//            // 转为键值对 商品道序明细作为key
//            processMap =  processList.stream()
//                    .collect(Collectors.toMap(o -> Long.valueOf(o.getStepItemSid()), Function.identity(), (t1,t2) -> t1));
        }
        // 在此道序完成台账明细中，通过所选择的行的“商品编码、排产批次号”，获取符合条件的道序完成台账明细信息，
        // 并转换成矩阵表形式进行显示（行转列由后端进行处理），并在最后一行显示各列的“小计”
        List<StaffCompleteSummary> recordItemList = manProcessStepCompleteRecordItemMapper.selectStaffCompleteSummary(new StaffCompleteSummary()
                .setProductSid(request.getProductSid()).setPaichanBatch(request.getPaichanBatch()).setPaichanBatchIs(ConstantsEms.YES)
                .setWorkCenterSid(request.getWorkCenterSid()).setPlantSid(request.getPlantSid()).setDepartment(request.getDepartment())
                .setCompleteDateBegin(request.getCompleteDateBegin()).setCompleteDateEnd(request.getCompleteDateEnd()));
        if (CollectionUtil.isEmpty(recordItemList)) {
            return table;
        }
        // 将员工 以及 商品道序明细sid  作为key, 完成量作为值
        Map<String, BigDecimal> recordQuantityMap = new HashMap<>();
        recordQuantityMap = recordItemList.stream().collect(Collectors.toMap(e->
                String.valueOf(e.getWorkerSid())+String.valueOf(e.getSort()), o-> o.getCompleteQuantity(),(t1, t2) -> t2));
        // 小计行
        List<StaffCompleteSummaryTableQuantity> quantitySumList = new ArrayList<>();
        Map<String, BigDecimal> sumMap = new HashMap<>();
        // 每行主键是员工，所以对员工分组
        Map<Long, List<StaffCompleteSummary>> staffMap = new HashMap<>();
        staffMap =  recordItemList.stream().collect(Collectors.groupingBy(StaffCompleteSummary::getWorkerSid));
        // 声明员工行对象
        List<StaffCompleteSummaryTableStaff> staffList = new ArrayList<>();
        // 遍历员工map，并再遍历员工组内的明细
        for (Long workSid : staffMap.keySet()) {
            List<StaffCompleteSummary> itemList = staffMap.get(workSid);
            // 每行对象
            StaffCompleteSummaryTableStaff staff = new StaffCompleteSummaryTableStaff();
            // 每行基本信息
            staff.setWorkerSid(itemList.get(0).getWorkerSid())
                    .setWorkerCode(itemList.get(0).getWorkerCode())
                    .setWorkerName(itemList.get(0).getWorkerName());
            staff.setQuantityList(new ArrayList<>());
            // 每行的商品道序完成量
            List<StaffCompleteSummaryTableQuantity> quantityList = new ArrayList<>();
            for (int i = 0; i < processList.size(); i++) {
                // 获取商品道序明细小计
                BigDecimal sum = BigDecimal.ZERO;
                if (sumMap.get(String.valueOf(processList.get(i).getSort())) != null) {
                    sum = sumMap.get(String.valueOf(processList.get(i).getSort()));
                }
                StaffCompleteSummaryTableQuantity quantity = new StaffCompleteSummaryTableQuantity();
                quantity.setProcessStepItemSid(processList.get(i).getProcessStepItemSid());
                if (recordQuantityMap.get(String.valueOf(workSid)+String.valueOf(processList.get(i).getSort())) != null) {
                    quantity.setCompleteQuantity(recordQuantityMap.get(String.valueOf(workSid)+String.valueOf(processList.get(i).getSort())));
                    // 小计计算
                    if (quantity.getCompleteQuantity() != null) {
                        sum = sum.add(quantity.getCompleteQuantity());
                    }
                }
                quantityList.add(quantity);
                // 小计
                sumMap.put(String.valueOf(processList.get(i).getSort()), sum);
            }
            staff.setQuantityList(quantityList);
            staffList.add(staff);
        }
        staffList = staffList.stream().sorted(Comparator.comparing(StaffCompleteSummaryTableStaff::getWorkerName)).collect(toList());
        // 设置小计行
        StaffCompleteSummaryTableStaff count = new StaffCompleteSummaryTableStaff();
        for (int i = 0; i < processList.size(); i++) {
            StaffCompleteSummaryTableQuantity quantity = new StaffCompleteSummaryTableQuantity();
            quantity.setSort(processList.get(i).getSort());
            quantity.setCompleteQuantity(sumMap.get(String.valueOf(processList.get(i).getSort())));
            quantitySumList.add(quantity);
        }
        count.setWorkerName("小计").setQuantityList(quantitySumList);
        staffList.add(count);
        table.setStaffList(staffList);
        return table;
    }
}
