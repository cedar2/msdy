package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsProcess;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IPayProcessStepCompleteService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysBusinessBcstMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 计薪量申报-主Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-08
 */
@Service
@SuppressWarnings("all")
@Slf4j
public class PayProcessStepCompleteServiceImpl extends ServiceImpl<PayProcessStepCompleteMapper, PayProcessStepComplete> implements IPayProcessStepCompleteService {
    @Autowired
    private PayProcessStepCompleteMapper payProcessStepCompleteMapper;
    @Autowired
    private PayProcessStepCompleteItemMapper payProcessStepCompleteItemMapper;
    @Autowired
    private PayProductJijianSettleInforMapper payProductJijianSettleInforMapper;
    @Autowired
    private PayProcessStepCompleteAttachMapper payProcessStepCompleteAttachMapper;
    @Autowired
    private BasStaffMapper basStaffMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private ISysFormProcessService sysFormProcessService;
    @Autowired
    private PayProcessStepCompleteItemServiceImpl payProcessStepCompleteItemService;
    @Autowired
    private ISystemUserService sysUserService;
    @Autowired
    private ManManufactureOrderProductMapper manManufactureOrderProductMapper;
    @Autowired
    private ManManufactureOrderProcessMapper manManufactureOrderProcessMapper;
    @Autowired
    private ManDayManufactureProgressItemMapper manDayManufactureProgressItemMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "计薪量申报-主";

    /**
     * 查询计薪量申报-主
     *
     * @param stepCompleteSid 计薪量申报-主ID
     * @return 计薪量申报-主
     */
    @Override
    public PayProcessStepComplete selectPayProcessStepCompleteById(Long stepCompleteSid) {
        PayProcessStepComplete payProcessStepComplete = payProcessStepCompleteMapper.selectPayProcessStepCompleteById(stepCompleteSid);
        if (payProcessStepComplete == null) {
            return null;
        }
        List<PayProcessStepCompleteItem> payProcessStepCompleteItemList = null;
        if (ConstantsProcess.JIXIN_ENTER_MODE_GLSC.equals(payProcessStepComplete.getEnterMode())){
            payProcessStepCompleteItemList =
                    payProcessStepCompleteItemMapper.selectPayProcessStepCompleteItemList(new PayProcessStepCompleteItem().setStepCompleteSid(stepCompleteSid));
            for (PayProcessStepCompleteItem item : payProcessStepCompleteItemList) {
                // 完成量校验参考工序 , 参考工序所引用数量类型  根据“生产订单号+工厂(工序)+班组+工序”获取“生产订单工序明细表”中的值
                List<ManManufactureOrderProcess> processReferList = manManufactureOrderProcessMapper.selectManManufactureOrderProcessList(new ManManufactureOrderProcess()
                        .setManufactureOrderSid(item.getManufactureOrderSid()).setProcessSid(item.getProcessSid())
                        .setPlantSid(payProcessStepComplete.getPlantSid()).setWorkCenterSid(payProcessStepComplete.getWorkCenterSid()));
                if (CollectionUtil.isNotEmpty(processReferList)){
                    // 完成量校验参考工序 , 参考工序所引用数量类型
                    item.setQuantityReferProcessSid(processReferList.get(0).getQuantityReferProcessSid());
                    item.setQuantityReferProcessCode(processReferList.get(0).getQuantityReferProcessCode());
                    item.setQuantityReferProcessName(processReferList.get(0).getQuantityReferProcessName());
                    item.setQuantityTypeReferProcess(processReferList.get(0).getQuantityTypeReferProcess());
                    // 参考工序校验量
                    List<ManDayManufactureProgressItem> dayList = manDayManufactureProgressItemMapper.selectManDayManufactureProgressItemList(
                            new ManDayManufactureProgressItem().setManufactureOrderSid(item.getManufactureOrderSid())
                                    .setMaterialSid(item.getProductSid()).setProcessSid(item.getQuantityReferProcessSid())
                                    .setCompleteType(payProcessStepComplete.getJixinWangongType()));
                    BigDecimal quantityReferProcess = BigDecimal.ZERO;
                    // 取值逻辑：根据“完成量校验参考工序”、“参考工序所引用数量类型”，获取对应数量类型的值；例如：”车缝“的”完成量校验参考工序“为”裁床“，
                    // “参考工序所引用数量类型”为”完成量“，则参考工序校验量，为”裁床工序的完成量“（从生产进度日报明细表中汇总，
                    // 根据“生产订单号+商品编码+工序+计薪完工类型”获取所有符合条件的生产进度日报明细行，将“当天完成量/收料量”累加得出）
                    if (CollectionUtil.isNotEmpty(dayList) && ConstantsProcess.QUANTITY_TYPE_REFER_PROCESS_WC.equals(item.getQuantityTypeReferProcess())){
                        quantityReferProcess = dayList.parallelStream().filter(o -> o.getQuantity() != null)
                                .map(ManDayManufactureProgressItem::getQuantity).reduce(BigDecimal.ZERO,BigDecimal::add);
                    }
                    if (CollectionUtil.isNotEmpty(dayList) && ConstantsProcess.QUANTITY_TYPE_REFER_PROCESS_JS.equals(item.getQuantityTypeReferProcess())){
                        quantityReferProcess = dayList.parallelStream().filter(o -> o.getJieshouQuantity() != null)
                                .map(ManDayManufactureProgressItem::getJieshouQuantity).reduce(BigDecimal.ZERO,BigDecimal::add);
                    }
                    if (CollectionUtil.isNotEmpty(dayList) && ConstantsProcess.QUANTITY_TYPE_REFER_PROCESS_FL.equals(item.getQuantityTypeReferProcess())){
                        quantityReferProcess = dayList.parallelStream().filter(o -> o.getIssueQuantity() != null)
                                .map(ManDayManufactureProgressItem::getIssueQuantity).reduce(BigDecimal.ZERO,BigDecimal::add);
                    }
                    item.setQuantityReferProcess(quantityReferProcess.toString());
                }
                // 已计薪量  根据“生产订单号+商品编码+工序+计薪完工类型”获取“已确认”的计薪量申报单的明细行的“计薪量”并进行累加
                List<PayProcessStepCompleteItem> stepCompleteItemList = payProcessStepCompleteItemMapper.selectPayProcessStepCompleteItemList(new
                        PayProcessStepCompleteItem().setManufactureOrderSid(item.getManufactureOrderSid())
                        .setProductSid(item.getProductSid()).setProcessSid(item.getProcessSid()).setJixinWangongType(payProcessStepComplete.getJixinWangongType())
                        .setHandleStatus(ConstantsEms.CHECK_STATUS));
                if (CollectionUtil.isNotEmpty(stepCompleteItemList)){
                    BigDecimal cumulativeQuantity = BigDecimal.ZERO;
                    cumulativeQuantity = stepCompleteItemList.parallelStream().filter(o -> o.getCompleteQuantity() != null)
                            .map(PayProcessStepCompleteItem::getCompleteQuantity).reduce(BigDecimal.ZERO,BigDecimal::add);
                    item.setCumulativeQuantity(cumulativeQuantity);
                }
            }
        }
        else if (ConstantsProcess.JIXIN_ENTER_MODE_BGLSC.equals(payProcessStepComplete.getEnterMode())) {
            payProcessStepCompleteItemList =
                    payProcessStepCompleteItemMapper.selectPayProcessStepCompleteItemJoinList(new PayProcessStepCompleteItem().setStepCompleteSid(stepCompleteSid));
        }
        List<PayProcessStepCompleteAttach> payProcessStepCompleteAttachList =
                payProcessStepCompleteAttachMapper.selectPayProcessStepCompleteAttachList(new PayProcessStepCompleteAttach().setStepCompleteSid(stepCompleteSid));
        if (CollectionUtil.isNotEmpty(payProcessStepCompleteItemList)) {
            payProcessStepCompleteItemList = payProcessStepCompleteItemList.stream()
                    .sorted(Comparator.comparing(PayProcessStepCompleteItem::getProductCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(PayProcessStepCompleteItem::getDepartment, Comparator.nullsLast(String::compareTo))
                            .thenComparing(PayProcessStepCompleteItem::getSort, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(PayProcessStepCompleteItem::getWorkerName, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))).collect(toList());
        }else {
            payProcessStepCompleteItemList = new ArrayList<>();
        }
        payProcessStepComplete.setPayProcessStepCompleteItemList(payProcessStepCompleteItemList);
        if (payProcessStepCompleteAttachList == null){
            payProcessStepCompleteAttachList = new ArrayList<>();
        }
        payProcessStepComplete.setPayProcessStepCompleteAttachList(payProcessStepCompleteAttachList);
        payProcessStepComplete.setPayProductJijianSettleInforList(new ArrayList<>());
        MongodbUtil.find(payProcessStepComplete);
        return payProcessStepComplete;
    }

    /**
     * 查询计薪量申报-主列表
     *
     * @param payProcessStepComplete 计薪量申报-主
     * @return 计薪量申报-主
     */
    @Override
    public List<PayProcessStepComplete> selectPayProcessStepCompleteList(PayProcessStepComplete payProcessStepComplete) {
        return payProcessStepCompleteMapper.selectPayProcessStepCompleteList(payProcessStepComplete);
    }

    /**
     * 新增计薪量申报-主
     * 需要注意编码重复校验
     *
     * @param payProcessStepComplete 计薪量申报-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String insertPayProcessStepComplete(PayProcessStepComplete payProcessStepComplete) {
        setConfirmInfo(payProcessStepComplete);
        if (StrUtil.isBlank(payProcessStepComplete.getReportCycle())) {
            payProcessStepComplete.setReportCycle(ConstantsEms.REPORT_CYCLE_MONTH);
        }
        int row = payProcessStepCompleteMapper.insert(payProcessStepComplete);
        //计薪量申报-明细对象
        List<PayProcessStepCompleteItem> payProcessStepCompleteItemList = payProcessStepComplete.getPayProcessStepCompleteItemList();
        if (CollectionUtil.isNotEmpty(payProcessStepCompleteItemList)) {
            checkItemUnique(payProcessStepComplete);
            addPayProcessStepCompleteItem(payProcessStepComplete, payProcessStepCompleteItemList);
        }
        //计薪量申报-附件对象
        List<PayProcessStepCompleteAttach> payProcessStepCompleteAttachList = payProcessStepComplete.getPayProcessStepCompleteAttachList();
        if (CollectionUtil.isNotEmpty(payProcessStepCompleteAttachList)) {
            addPayProcessStepCompleteAttach(payProcessStepComplete, payProcessStepCompleteAttachList);
        }
        PayProcessStepComplete stepComplete = payProcessStepCompleteMapper.selectPayProcessStepCompleteById(payProcessStepComplete.getStepCompleteSid());
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(payProcessStepComplete.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_PROCESS_STEP_COMPLETE)
                    .setDocumentSid(payProcessStepComplete.getStepCompleteSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                String productCode = payProcessStepComplete.getProductCode() == null ? "" : "（款号：" + payProcessStepComplete.getProductCode() + "）";
                sysTodoTask.setTitle("计薪量申报" + stepComplete.getStepCompleteCode() + productCode + "当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(stepComplete.getStepCompleteCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        }
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(payProcessStepComplete.getStepCompleteSid(), payProcessStepComplete.getHandleStatus(), msgList, TITLE, null);
        }
        return payProcessStepComplete.getStepCompleteSid() == null ? null : payProcessStepComplete.getStepCompleteSid().toString();
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(PayProcessStepComplete o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            if (CollectionUtil.isEmpty(o.getPayProcessStepCompleteItemList())) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 新增行时设置行号
     */
    public long getMaxItemNum(PayProcessStepComplete payProcessStepComplete){
        List<PayProcessStepCompleteItem> itemList = payProcessStepCompleteItemMapper.selectList(new QueryWrapper<PayProcessStepCompleteItem>().lambda()
                .eq(PayProcessStepCompleteItem::getStepCompleteSid,payProcessStepComplete.getStepCompleteSid()).orderByDesc(PayProcessStepCompleteItem::getItemNum));
        long maxNum = 0;
        if (CollectionUtil.isNotEmpty(itemList)){
            if (itemList.get(0).getItemNum() != null){
                maxNum = itemList.get(0).getItemNum();
            }
        }
        return maxNum;
    }

    /**
     * 商品计件结算信息对象
     */
    private void selectPayProductJijianSettleInfor(PayProcessStepComplete payProcessStepComplete){
        if (CollectionUtil.isNotEmpty(payProcessStepComplete.getPayProcessStepCompleteItemList())){
            Long[] productSidList = payProcessStepComplete.getPayProcessStepCompleteItemList().stream().map(PayProcessStepCompleteItem::getProductSid).toArray(Long[]::new);
            PayProductJijianSettleInfor infor = new PayProductJijianSettleInfor();
            infor.setYearmonth(payProcessStepComplete.getYearmonth())
                    .setPlantSid(payProcessStepComplete.getPlantSid())
                    .setWorkCenterSid(payProcessStepComplete.getWorkCenterSid())
                    .setDepartment(payProcessStepComplete.getDepartment())
                    .setProductPriceType(payProcessStepComplete.getProductPriceType())
                    .setJixinWangongType(payProcessStepComplete.getJixinWangongType());
            infor.setProductSidList(productSidList);
            List<PayProductJijianSettleInfor> inforList = payProductJijianSettleInforMapper.selectPayProductJijianSettleInforList(infor);
            if (CollectionUtil.isNotEmpty(inforList)){
                inforList = inforList.stream().filter(item ->
                        payProcessStepComplete.getPayProcessStepCompleteItemList().stream().map(up -> up.getProductSid()+String.valueOf(up.getPaichanBatch())).collect(
                        Collectors.toList()).contains(item.getProductSid()+String.valueOf(item.getPaichanBatch()))).collect(Collectors.toList());
            }
            if (inforList == null){
                inforList = new ArrayList<>();
            }
            payProcessStepComplete.setPayProductJijianSettleInforList(inforList);
        }
        else {
            payProcessStepComplete.setPayProductJijianSettleInforList(new ArrayList<>());
        }
    }

    /**
     * 校验明细唯一性-明细对象
     */
    private void checkItemUnique(PayProcessStepComplete payProcessStepComplete) {
        List<PayProcessStepCompleteItem> payProcessStepCompleteItemList = payProcessStepComplete.getPayProcessStepCompleteItemList();
        if (CollectionUtil.isNotEmpty(payProcessStepCompleteItemList)) {
            if (ConstantsProcess.JIXIN_ENTER_MODE_GLSC.equals(payProcessStepComplete.getEnterMode())){
                Map<String, List<PayProcessStepCompleteItem>> map = payProcessStepCompleteItemList.stream()
                        .collect(Collectors.groupingBy(o -> String.valueOf(o.getWorkerSid())+"-"+String.valueOf(o.getManufactureOrderSid())+
                                "-"+String.valueOf(o.getProductSid())+"-"+String.valueOf(o.getProcessStepSid())+
                                "-"+String.valueOf(o.getPaichanBatch())));
                if (map.size() != payProcessStepCompleteItemList.size()) {
                    throw new BaseException("存在相同的员工，生产订单号，商品编码(款号)，道序序号，排产批次号的计薪量明细，请核实！");
                }
            }
            else if (ConstantsProcess.JIXIN_ENTER_MODE_BGLSC.equals(payProcessStepComplete.getEnterMode())){
                Map<String, List<PayProcessStepCompleteItem>> map = payProcessStepCompleteItemList.stream()
                        .collect(Collectors.groupingBy(o -> String.valueOf(o.getWorkerSid())+
                                "-"+String.valueOf(o.getProductSid())+"-"+String.valueOf(o.getProcessStepSid())+
                                "-"+String.valueOf(o.getPaichanBatch())));
                if (map.size() != payProcessStepCompleteItemList.size()) {
                    throw new BaseException("存在相同的员工，商品编码(款号)，道序序号，排产批次号的计薪量明细，请核实！");
                }
            }
            else {}
        }
    }

    /**
     * 计薪量申报-明细对象
     */
    private void addPayProcessStepCompleteItem(PayProcessStepComplete payProcessStepComplete, List<PayProcessStepCompleteItem> payProcessStepCompleteItemList) {
        // 程序计时 测试
        long startTime = 0, endTime = 0;
        log.info("开始进行获取明细最大行号操作 ——————");
        startTime = System.currentTimeMillis();
        long maxNum = getMaxItemNum(payProcessStepComplete);
        endTime = System.currentTimeMillis();
        log.info("获取明细最大行号用时： "+(endTime-startTime)+"ms");
        // === ===//
        log.info("开始进行对新增行写入行号，sid和如果需要获取员工号操作 ——————");
        startTime = System.currentTimeMillis();
        for (PayProcessStepCompleteItem item : payProcessStepCompleteItemList) {
            item.setStepCompleteSid(payProcessStepComplete.getStepCompleteSid());
            if (item.getWorkerCode() == null && item.getWorkerSid() != null){
                BasStaff basStaff = basStaffMapper.selectById(item.getWorkerSid());
                item.setWorkerCode(basStaff.getStaffCode());
            }
            if (item.getStepCompleteItemSid() == null) {
                item.setItemNum(++maxNum);
            }
        }
        payProcessStepCompleteItemMapper.inserts(payProcessStepCompleteItemList);
        endTime = System.currentTimeMillis();
        log.info("新增行数据插入完成用时："+(endTime-startTime)+"ms");
    }

    private void deleteItem(PayProcessStepComplete payProcessStepComplete) {
        payProcessStepCompleteItemMapper.delete(
                new UpdateWrapper<PayProcessStepCompleteItem>()
                        .lambda()
                        .eq(PayProcessStepCompleteItem::getStepCompleteSid, payProcessStepComplete.getStepCompleteSid())
        );
    }

    /**
     * 计薪量申报-附件对象
     */
    private void addPayProcessStepCompleteAttach(PayProcessStepComplete payProcessStepComplete, List<PayProcessStepCompleteAttach> payProcessStepCompleteAttachList) {
        payProcessStepCompleteAttachList.forEach(o -> {
            o.setStepCompleteSid(payProcessStepComplete.getStepCompleteSid());
        });
        payProcessStepCompleteAttachMapper.inserts(payProcessStepCompleteAttachList);
    }

    private void deleteAttach(PayProcessStepComplete payProcessStepComplete) {
        payProcessStepCompleteAttachMapper.delete(
                new UpdateWrapper<PayProcessStepCompleteAttach>()
                        .lambda()
                        .eq(PayProcessStepCompleteAttach::getStepCompleteSid, payProcessStepComplete.getStepCompleteSid())
        );
    }

    /**
     * 查找是否已存在相同维度的计薪量
     */
    @Override
    public String verifyUnique(PayProcessStepComplete payProcessStepComplete){
        if (payProcessStepComplete.getPlantSid() == null || payProcessStepComplete.getWorkCenterSid() == null
                || StrUtil.isBlank(payProcessStepComplete.getProductPriceType()) || StrUtil.isBlank(payProcessStepComplete.getJixinWangongType())
                || StrUtil.isBlank(payProcessStepComplete.getYearmonth()) || StrUtil.isBlank(payProcessStepComplete.getDepartment())){
            return null;
        }
        else {
            try {
                QueryWrapper<PayProcessStepComplete> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda()
                        .eq(PayProcessStepComplete::getPlantSid, payProcessStepComplete.getPlantSid())
                        .eq(PayProcessStepComplete::getWorkCenterSid, payProcessStepComplete.getWorkCenterSid())
                        .eq(PayProcessStepComplete::getProductPriceType, payProcessStepComplete.getProductPriceType())
                        .eq(PayProcessStepComplete::getJixinWangongType, payProcessStepComplete.getJixinWangongType())
                        .eq(PayProcessStepComplete::getYearmonth, payProcessStepComplete.getYearmonth())
                        .eq(PayProcessStepComplete::getDepartment, payProcessStepComplete.getDepartment());
                if (payProcessStepComplete.getStepCompleteSid() != null){
                    queryWrapper.lambda().ne(PayProcessStepComplete::getStepCompleteSid, payProcessStepComplete.getStepCompleteSid());
                }
                if (payProcessStepComplete.getEnterDimension() != null) {
                    queryWrapper.lambda().eq(PayProcessStepComplete::getEnterDimension, payProcessStepComplete.getEnterDimension());
                }
                else {
                    queryWrapper.lambda().isNull(PayProcessStepComplete::getEnterDimension);
                }
                if (payProcessStepComplete.getProductSid() != null) {
                    queryWrapper.lambda().eq(PayProcessStepComplete::getProductSid, payProcessStepComplete.getProductSid());
                }
                else {
                    queryWrapper.lambda().isNull(PayProcessStepComplete::getProductSid);
                }
                PayProcessStepComplete complete =  payProcessStepCompleteMapper.selectOne(queryWrapper);
                if (complete != null){
                    return complete.getStepCompleteSid().toString();
                }
            }catch (Exception e){
                throw new BaseException("“工厂(工序)+班组+操作部门+商品工价类型+计薪完工类型+所属年月+商品编码(款号)+录入维度”，已存在多笔计薪量申报单，请核实");
            }
        }
        return null;
    }

    /**
     * 修改计薪量申报-主
     *
     * @param payProcessStepComplete 计薪量申报-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayProcessStepComplete(PayProcessStepComplete payProcessStepComplete) {
        // 程序计时 测试
        long startTime = 0, endTime = 0, startTimeTotal = 0, endTimeTotal = 0;
        log.info("开始执行编辑/变更操作 ——————");
        startTimeTotal = System.currentTimeMillis();
        // === ===//
        log.info("开始校验是否已存在计薪量申报单操作 ——————");
        startTime = System.currentTimeMillis();
        String sid = verifyUnique(payProcessStepComplete);
        endTime = System.currentTimeMillis();
        log.info("校验工厂(工序)+班组+操作部门+商品工价类型+计薪完工类型+所属年月是否已存在计薪量申报单用时： "+(endTime-startTime)+"ms");
        if (sid != null){
            throw new BaseException("工厂(工序)+班组+商品工价类型+计薪完工类型+所属年月+商品编码(款号)+录入维度”组合已存在计薪量申报单");
        }
        // === ===//
        setConfirmInfo(payProcessStepComplete);
        //计薪量申报-明细对象
        List<PayProcessStepCompleteItem> completeItemList = payProcessStepComplete.getPayProcessStepCompleteItemList();
        // === ===//
        log.info("开始校验计薪量明细是否存在重复操作 ——————");
        startTime = System.currentTimeMillis();
        checkItemUnique(payProcessStepComplete);
        endTime = System.currentTimeMillis();
        log.info("校验员工(+生产订单号)+商品编码(款号)+道序序号+排产批次号的计薪量明细是否存在重复用时： "+(endTime-startTime)+"ms");
        // === ===//
        log.info("开始进行计薪量明细行新增编辑删除操作 ——————");
        startTime = System.currentTimeMillis();
        operateItem(payProcessStepComplete, completeItemList);
        endTime = System.currentTimeMillis();
        log.info("计薪量明细行新增编辑删除处理用时： "+(endTime-startTime)+"ms");
        // === ===//
        //计薪量申报-附件对象
        List<PayProcessStepCompleteAttach> completeAttachList = payProcessStepComplete.getPayProcessStepCompleteAttachList();
        operateAttachment(payProcessStepComplete, completeAttachList);
        PayProcessStepComplete response = payProcessStepCompleteMapper.selectPayProcessStepCompleteById(payProcessStepComplete.getStepCompleteSid());
        payProcessStepComplete.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = payProcessStepCompleteMapper.updateAllById(payProcessStepComplete);
        if (row > 0) {
            if (!ConstantsEms.SAVA_STATUS.equals(payProcessStepComplete.getHandleStatus())) {
                //校验是否存在待办
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PROCESS_STEP_COMPLETE)
                        .eq(SysTodoTask::getDocumentSid, payProcessStepComplete.getStepCompleteSid()));
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList= BeanUtils.eq(response, payProcessStepComplete);
            MongodbDeal.update(payProcessStepComplete.getStepCompleteSid(), response.getHandleStatus() ,payProcessStepComplete.getHandleStatus(), msgList, TITLE, null);
        }
        endTimeTotal = System.currentTimeMillis();
        log.info("接口执行结束，用时： "+(endTimeTotal-startTimeTotal)+"ms");
        return row;
    }

    /**
     * 商品道序-明细
     */
    private void operateItem(PayProcessStepComplete payProcessStepComplete, List<PayProcessStepCompleteItem> completeItemList) {
        if (CollectionUtil.isNotEmpty(completeItemList)) {
            // 程序计时 测试
            long startTime = 0, endTime = 0;
            //新增
            log.info("开始进行计薪量明细行新增操作 ——————");
            startTime = System.currentTimeMillis();
            List<PayProcessStepCompleteItem> addList = completeItemList.stream().filter(o -> o.getStepCompleteItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addPayProcessStepCompleteItem(payProcessStepComplete, addList);
            }
            endTime = System.currentTimeMillis();
            log.info("计薪量明细行新增处理用时： "+(endTime-startTime)+"ms");
            // === ===//
            log.info("开始进行计薪量明细行更新操作 ——————");
            startTime = System.currentTimeMillis();
            //编辑
            List<PayProcessStepCompleteItem> editList = completeItemList.stream().filter(o -> o.getStepCompleteItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                String userName = ApiThreadLocalUtil.get().getUsername();Date date = new Date();
                editList.forEach(o -> {o.setUpdaterAccount(userName).setUpdateDate(date);});
                payProcessStepCompleteItemMapper.updatesAllById(editList);
            }
            endTime = System.currentTimeMillis();
            log.info("计薪量明细行更新处理用时： "+(endTime-startTime)+"ms");
            //原有数据
            log.info("开始进行计薪量明细行删除操作(读取数据库的明细 和 当前明细判断哪些是要删除的) ——————");
            startTime = System.currentTimeMillis();
            List<PayProcessStepCompleteItem> itemList = payProcessStepCompleteItemMapper.selectList(new QueryWrapper<PayProcessStepCompleteItem>().lambda()
                    .eq(PayProcessStepCompleteItem::getStepCompleteSid, payProcessStepComplete.getStepCompleteSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(PayProcessStepCompleteItem::getStepCompleteItemSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = completeItemList.stream().map(PayProcessStepCompleteItem::getStepCompleteItemSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                payProcessStepCompleteItemMapper.deleteBatchIds(result);
            }
            endTime = System.currentTimeMillis();
            log.info("计薪量明细行删除处理用时： "+(endTime-startTime)+"ms");
        } else {
            deleteItem(payProcessStepComplete);
        }
    }

    /**
     * 商品道序-附件
     */
    private void operateAttachment(PayProcessStepComplete payProcessStepComplete, List<PayProcessStepCompleteAttach> completeAttachList) {
        if (CollectionUtil.isNotEmpty(completeAttachList)) {
            //新增
            List<PayProcessStepCompleteAttach> addList = completeAttachList.stream().filter(o -> o.getAttachmentSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addPayProcessStepCompleteAttach(payProcessStepComplete, addList);
            }
            //编辑
            List<PayProcessStepCompleteAttach> editList = completeAttachList.stream().filter(o -> o.getAttachmentSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    payProcessStepCompleteAttachMapper.updateAllById(o);
                });
            }
            //原有数据
            List<PayProcessStepCompleteAttach> itemList =
                    payProcessStepCompleteAttachMapper.selectList(new QueryWrapper<PayProcessStepCompleteAttach>().lambda()
                            .eq(PayProcessStepCompleteAttach::getStepCompleteSid, payProcessStepComplete.getStepCompleteSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(PayProcessStepCompleteAttach::getAttachmentSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = completeAttachList.stream().map(PayProcessStepCompleteAttach::getAttachmentSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                payProcessStepCompleteAttachMapper.deleteBatchIds(result);
            }
        } else {
            deleteAttach(payProcessStepComplete);
        }
    }

    /**
     * 变更计薪量申报-主
     *
     * @param payProcessStepComplete 计薪量申报-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePayProcessStepComplete(PayProcessStepComplete payProcessStepComplete) {
        String sid = verifyUnique(payProcessStepComplete);
        if (sid != null){
            throw new BaseException("工厂(工序)+班组+商品工价类型+计薪完工类型+所属年月+商品编码(款号)+录入维度”组合已存在计薪量申报单");
        }
        setConfirmInfo(payProcessStepComplete);
        payProcessStepComplete.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        //计薪量申报-明细对象
        List<PayProcessStepCompleteItem> completeItemList = payProcessStepComplete.getPayProcessStepCompleteItemList();
        checkItemUnique(payProcessStepComplete);
        operateItem(payProcessStepComplete, completeItemList);
        //计薪量申报-附件对象
        List<PayProcessStepCompleteAttach> completeAttachList = payProcessStepComplete.getPayProcessStepCompleteAttachList();
        operateAttachment(payProcessStepComplete, completeAttachList);
        PayProcessStepComplete response = payProcessStepCompleteMapper.selectPayProcessStepCompleteById(payProcessStepComplete.getStepCompleteSid());
        payProcessStepComplete.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = payProcessStepCompleteMapper.updateAllById(payProcessStepComplete);
        if (row > 0) {
            SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
            sysBusinessBcst.setTitle("计薪量申报单" + payProcessStepComplete.getStepCompleteCode() + "已更新")
                    .setDocumentSid(payProcessStepComplete.getStepCompleteSid())
                    .setDocumentCode(String.valueOf(payProcessStepComplete.getStepCompleteCode()))
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysBusinessBcstMapper.insert(sysBusinessBcst);
            //插入日志
            MongodbUtil.insertUserLog(payProcessStepComplete.getStepCompleteSid(), BusinessType.CHANGE.getValue(), response, payProcessStepComplete, TITLE);
        }
        return row;
    }

    /**
     * 批量删除计薪量申报-主
     *
     * @param stepCompleteSids 需要删除的计薪量申报-主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayProcessStepCompleteByIds(List<Long> stepCompleteSids) {
        List<String> handleStatusList = new ArrayList<>();
        handleStatusList.add(ConstantsEms.SAVA_STATUS);
        handleStatusList.add(ConstantsEms.BACK_STATUS);
        Integer count = payProcessStepCompleteMapper.selectCount(new QueryWrapper<PayProcessStepComplete>().lambda()
                .in(PayProcessStepComplete::getHandleStatus, handleStatusList)
                .in(PayProcessStepComplete::getStepCompleteSid, stepCompleteSids));
        if (count != stepCompleteSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT_APPROVE);
        }
        //删除计薪量申报-明细对象
        payProcessStepCompleteItemMapper.delete(new UpdateWrapper<PayProcessStepCompleteItem>().lambda()
                .in(PayProcessStepCompleteItem::getStepCompleteSid, stepCompleteSids));
        //删除计薪量申报-附件对象
        payProcessStepCompleteAttachMapper.delete(new UpdateWrapper<PayProcessStepCompleteAttach>().lambda()
                .in(PayProcessStepCompleteAttach::getStepCompleteSid, stepCompleteSids));
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PROCESS_STEP_COMPLETE)
                .in(SysTodoTask::getDocumentSid, stepCompleteSids));
        return payProcessStepCompleteMapper.deleteBatchIds(stepCompleteSids);
    }

    /**
     * 更改确认状态
     *
     * @param payProcessStepComplete
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(PayProcessStepComplete payProcessStepComplete) {
        int row = 0;
        Long[] sids = payProcessStepComplete.getStepCompleteSidList();
        if (ArrayUtil.isNotEmpty(sids)) {
            row = payProcessStepCompleteMapper.update(null, new UpdateWrapper<PayProcessStepComplete>().lambda()
                    .set(PayProcessStepComplete::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(PayProcessStepComplete::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(PayProcessStepComplete::getConfirmDate, new Date())
                    .in(PayProcessStepComplete::getStepCompleteSid, sids));
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param payProcessStepComplete
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity confirm(PayProcessStepComplete payProcessStepComplete) {
        int row = 0;
        Long[] sids = payProcessStepComplete.getStepCompleteSidList();
        if (ArrayUtil.isNotEmpty(sids)) {
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            CommonErrMsgResponse errMsg = null;
            for (int i = 0; i < sids.length; i++) {
                PayProcessStepComplete complete = this.selectPayProcessStepCompleteById(sids[i]);
                complete.setHandleStatus(ConstantsEms.CHECK_STATUS);
                if (CollectionUtil.isEmpty(complete.getPayProcessStepCompleteItemList())){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setMsg("计薪量申报单号"+complete.getStepCompleteCode()+"明细为空，无法确认！");
                    errMsgList.add(errMsg);
                }
                else {
                    // 如果有返回提示信息并忽略继续的要注意这里的方法有改了计件信息的处理状态，要处理事务如何回滚的问题
                    // 注意这里和 checkVerify 有重复的校验，处理状态不一样就可以避免
                    EmsResultEntity errMsgJijianList = payProcessStepCompleteItemService.checkPayProductJijianSettleInforBySelect(complete.getPayProcessStepCompleteItemList(),complete);
                    if (errMsgJijianList != null && EmsResultEntity.ERROR_TAG.equals(errMsgJijianList.getTag())){
                        List<CommonErrMsgResponse> errMsgJijianListData = (List<CommonErrMsgResponse>) errMsgJijianList.getMsgList();
                        errMsgList.addAll(errMsgJijianListData);
                    }
                    if (ConstantsProcess.JIXIN_ENTER_MODE_GLSC.equals(complete.getEnterMode())){
                        EmsResultEntity warnMsgVerfyList = checkVerify(complete);
                        if(warnMsgVerfyList != null && EmsResultEntity.WARN_TAG.equals(warnMsgVerfyList.getTag())){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setMsg("计薪量申报单号"+complete.getStepCompleteCode()+"明细存在累计计薪量大于参考工序校验量的情况，请核实！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(errMsgList)){
                return EmsResultEntity.error(errMsgList);
            }
            row = payProcessStepCompleteMapper.update(null, new UpdateWrapper<PayProcessStepComplete>().lambda()
                    .set(PayProcessStepComplete::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(PayProcessStepComplete::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(PayProcessStepComplete::getConfirmDate, new Date())
                    .in(PayProcessStepComplete::getStepCompleteSid, sids));
            for (Long sid : sids) {
                MongodbDeal.check(sid, HandleStatus.CONFIRMED.getCode(), null, TITLE, null);
            }
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                            .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_PROCESS_STEP_COMPLETE)
                    .in(SysTodoTask::getDocumentSid, sids));
        }
        return EmsResultEntity.success(row);
    }

    /**
     * 单据提交校验
     *
     * @param payProcessStepComplete
     * @return
     */
    @Override
    public EmsResultEntity checkVerify(PayProcessStepComplete payProcessStepComplete){
        if (CollectionUtil.isNotEmpty(payProcessStepComplete.getPayProcessStepCompleteItemList())){
            payProcessStepComplete.getPayProcessStepCompleteItemList().forEach(item->{
                if (!item.getDepartment().equals(payProcessStepComplete.getDepartment())){
                    throw new BaseException("计薪明细的操作部门与表头的操作部门不一致，请检查！");
                }
            });
        }
        if (ConstantsEms.SAVA_STATUS.equals(payProcessStepComplete.getHandleStatus())) { }
        else if (ConstantsEms.CHECK_STATUS.equals(payProcessStepComplete.getHandleStatus())) {
            if (ConstantsProcess.JIXIN_ENTER_MODE_GLSC.equals(payProcessStepComplete.getEnterMode())){
                List<PayProcessStepCompleteItem> payProcessStepCompleteItemList = payProcessStepComplete.getPayProcessStepCompleteItemList();
                List<CommonErrMsgResponse> warnMsgList = new ArrayList<>();
                CommonErrMsgResponse warnMsg = null;
                if (CollectionUtil.isNotEmpty(payProcessStepCompleteItemList)){
                    for (PayProcessStepCompleteItem item : payProcessStepCompleteItemList) {
                        if (item.getCumulativeQuantity() == null){
                            item.setCumulativeQuantity(BigDecimal.ZERO);
                        }
                        if (item.getCompleteQuantity() == null){
                            item.setCompleteQuantity(BigDecimal.ZERO);
                        }
                        if (item.getQuantityReferProcess() == null){
                            // 参考工序校验量
                            List<ManDayManufactureProgressItem> dayList = manDayManufactureProgressItemMapper.selectManDayManufactureProgressItemList(
                                    new ManDayManufactureProgressItem().setManufactureOrderSid(item.getManufactureOrderSid())
                                            .setMaterialSid(item.getProductSid()).setProcessSid(item.getQuantityReferProcessSid())
                                            .setCompleteType(payProcessStepComplete.getJixinWangongType()));
                            if (CollectionUtil.isNotEmpty(dayList)){
                                BigDecimal quantityReferProcess = dayList.parallelStream().filter(o -> o.getQuantity() != null)
                                        .map(ManDayManufactureProgressItem::getQuantity).reduce(BigDecimal.ZERO,BigDecimal::add);
                                if (item.getCumulativeQuantity().add(item.getCompleteQuantity()).compareTo(quantityReferProcess) > 0){
                                    warnMsg = new CommonErrMsgResponse();
                                    // 前端不会做拿名称，所以让后端做
                                    BasStaff staff = basStaffMapper.selectById(item.getWorkerSid());
                                    String staffName = "";
                                    if (staff != null){
                                        staffName = staff.getStaffName() + "+";
                                    }
                                    warnMsg.setMsg(staffName + "生产订单号" + item.getManufactureOrderCode() + "+商品编码" + item.getProductCode() +
                                            "+道序编码" + item.getProcessStepName() +"，累计计薪量大于参考工序校验量，请核实！");
                                    warnMsgList.add(warnMsg);
                                }
                            }
                        }
                        else {
                            if (item.getCumulativeQuantity().add(item.getCompleteQuantity()).compareTo(new BigDecimal(item.getQuantityReferProcess())) > 0){
                                warnMsg = new CommonErrMsgResponse();
                                // 前端不会做拿名称，所以让后端做
                                BasStaff staff = basStaffMapper.selectById(item.getWorkerSid());
                                String staffName = "";
                                if (staff != null){
                                    staffName = staff.getStaffName() + "+";
                                }
                                warnMsg.setMsg(staffName + "生产订单号" + item.getManufactureOrderCode() + "+商品编码" + item.getProductCode() +
                                        "+道序编码" + item.getProcessStepName() +"，累计计薪量大于参考工序校验量，请核实！");
                                warnMsgList.add(warnMsg);
                            }
                        }
                    }
                }
                else {
                    if (CollectionUtil.isNotEmpty(warnMsgList)){
                        return EmsResultEntity.warning(warnMsgList);
                    }
                }
            }
        }
        return EmsResultEntity.success();
    }

    /**
     * 单据提交校验
     *
     * @param payProcessStepComplete
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int verify(PayProcessStepComplete payProcessStepComplete) {
        if (null == payProcessStepComplete.getStepCompleteSid() || StrUtil.isBlank(payProcessStepComplete.getHandleStatus())) {
            throw new CustomException("参数错误");
        }
        PayProcessStepComplete stepComplete = selectPayProcessStepCompleteById(payProcessStepComplete.getStepCompleteSid());
        if (CollectionUtil.isEmpty(stepComplete.getPayProcessStepCompleteItemList())) {
            throw new CustomException("计薪量申报单号" + stepComplete.getStepCompleteCode() + ConstantsEms.SUBMIT_DETAIL_LINE_STATEMENT);
        }
        return payProcessStepCompleteMapper.updateById(payProcessStepComplete);
    }

    /**
     * 累计计薪量申报
     */
    @Override
    public PayProcessStepCompleteItem getQuantity(PayProcessStepCompleteItem item) {
        //根据生产订单、商品、道序、完工类型获取累计计薪量申报
        List<PayProcessStepCompleteItem> itemList = payProcessStepCompleteItemMapper.selectList(new QueryWrapper<PayProcessStepCompleteItem>().lambda()
                .eq(PayProcessStepCompleteItem::getManufactureOrderSid, item.getManufactureOrderSid())
                .eq(PayProcessStepCompleteItem::getProductSid, item.getProductSid())
                .eq(PayProcessStepCompleteItem::getProcessStepItemSid, item.getProcessStepItemSid())
                .eq(PayProcessStepCompleteItem::getJixinWangongType, ConstantsEms.JXCG));
        PayProcessStepCompleteItem payProcessStepCompleteItem = new PayProcessStepCompleteItem();
        if (CollectionUtil.isNotEmpty(itemList)) {
            //累计计薪量申报
            BigDecimal cumulativeQuantity = itemList.stream().filter(o -> o.getCompleteQuantity() != null)
                    .map(PayProcessStepCompleteItem::getCompleteQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
            payProcessStepCompleteItem.setCumulativeQuantity(cumulativeQuantity);
        }

        //根据生产订单、商品获取生产订单量
        List<ManManufactureOrderProduct> productList = manManufactureOrderProductMapper.selectList(new QueryWrapper<ManManufactureOrderProduct>().lambda()
                .eq(ManManufactureOrderProduct::getManufactureOrderSid, item.getManufactureOrderSid())
                .eq(ManManufactureOrderProduct::getMaterialSid, item.getProductSid()));
        if (CollectionUtil.isNotEmpty(productList)) {
            //生产订单量
            BigDecimal manufactureQuantity = productList.stream().filter(o -> o.getQuantity() != null)
                    .map(ManManufactureOrderProduct::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
            payProcessStepCompleteItem.setProcessQuantity(manufactureQuantity);
        }
        return payProcessStepCompleteItem;
    }
}
