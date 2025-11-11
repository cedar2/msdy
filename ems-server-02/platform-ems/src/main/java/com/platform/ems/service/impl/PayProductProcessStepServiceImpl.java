package com.platform.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.api.service.RemoteMenuService;
import com.platform.api.service.RemoteSystemService;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.common.core.domain.entity.SysRole;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConManufactureDepartment;
import com.platform.ems.plug.mapper.ConManufactureDepartmentMapper;
import com.platform.ems.service.*;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysRoleMenu;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysBusinessBcstMapper;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.service.ISysDictDataService;
import com.platform.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 商品道序-主Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-08
 */
@Service
@SuppressWarnings("all")
public class PayProductProcessStepServiceImpl extends ServiceImpl<PayProductProcessStepMapper, PayProductProcessStep> implements IPayProductProcessStepService {
    @Autowired
    private PayProductProcessStepMapper payProductProcessStepMapper;
    @Autowired
    private PayProductProcessStepItemMapper payProductProcessStepItemMapper;
    @Autowired
    private PayProductProcessStepAttachMapper payProductProcessStepAttachMapper;
    @Autowired
    private IPayUpdateProductProcessStepService payUpdateProductProcessStepService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private RemoteMenuService remoteMenuService;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private ISysFormProcessService sysFormProcessService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private PayProcessStepMapper payProcessStepMapper;
    @Autowired
    private ConManufactureDepartmentMapper conManufactureDepartmentMapper;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private IBasStaffService basStaffService;
    @Autowired
    private RemoteSystemService remoteSystemService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SysDefaultSettingClientMapper settingClientMapper;

    private static final String TITLE = "商品道序-主";

    private static final String IS_FINAL = "sys_yes_no";

    private static final String PRODUCT_PRICE_TYPE = "s_product_price_type";

    /**
     * 查询商品道序-主
     *
     * @param productSid 商品道序-主ID
     * @return 商品道序-主
     */
    @Override
    public PayProductProcessStep selectPayProductProcessStepById(Long productProcessStepSid) {
        PayProductProcessStep payProductProcessStep = payProductProcessStepMapper.selectPayProductProcessStepById(productProcessStepSid);
        if (payProductProcessStep == null) {
            return null;
        }
        payProductProcessStep.setPayProductProcessStepItemList(new ArrayList<>());
        payProductProcessStep.setAttachmentList(new ArrayList<>());
        // 变更页面需要
        payProductProcessStep.setLimitPriceBgq(payProductProcessStep.getLimitPrice());
        payProductProcessStep.setTotalPriceBlqBgq(payProductProcessStep.getTotalPriceBlq());
        payProductProcessStep.setTotalPriceBlhBgq(payProductProcessStep.getTotalPriceBlh());
        // 商品图片
        if (StrUtil.isNotBlank(payProductProcessStep.getPicturePathSecond())) {
            String[] picturePathList = payProductProcessStep.getPicturePathSecond().split(";");
            payProductProcessStep.setPicturePathList(picturePathList);
        }
        List<PayProductProcessStepItem> payProductProcessStepItemList =
                payProductProcessStepItemMapper.selectPayProductProcessStepItemList(new PayProductProcessStepItem().setProductProcessStepSid(productProcessStepSid));
        if (CollectionUtil.isNotEmpty(payProductProcessStepItemList)) {
            //工价小计
            BigDecimal totalPrice = payProductProcessStepItemList.stream().map(PayProductProcessStepItem::getActualPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            totalPrice = totalPrice.setScale(3, BigDecimal.ROUND_HALF_UP);
            payProductProcessStep.setTotalPrice(totalPrice);
            // 变更页面需要
            payProductProcessStepItemList.forEach(item->{
                item.setPriceBgq(item.getPrice());
                item.setPriceRateBgq(item.getPriceRate());
            });
            payProductProcessStep.setPayProductProcessStepItemList(payProductProcessStepItemList);
        } else {
            payProductProcessStep.setTotalPrice(new BigDecimal(BigDecimal.ZERO.stripTrailingZeros().toPlainString()));
        }
        // 附件清单
        List<PayProductProcessStepAttach> payProductProcessStepAttachList =
                payProductProcessStepAttachMapper.selectPayProductProcessStepAttachList(new PayProductProcessStepAttach().setProductProcessStepSid(productProcessStepSid));
        if (CollectionUtil.isNotEmpty(payProductProcessStepAttachList)) {
            payProductProcessStep.setAttachmentList(payProductProcessStepAttachList);
        }
        MongodbUtil.find(payProductProcessStep);
        return payProductProcessStep;
    }

    /**
     * 查询商品道序-主列表
     *
     * @param payProductProcessStep 商品道序-主
     * @return 商品道序-主
     */
    @Override
    public List<PayProductProcessStep> selectPayProductProcessStepList(PayProductProcessStep payProductProcessStep) {
        List<PayProductProcessStep> list = payProductProcessStepMapper.selectPayProductProcessStepList(payProductProcessStep);
        if (CollectionUtil.isNotEmpty(list)){
            list.forEach(item->{
                List<PayProductProcessStepItem> itemList = payProductProcessStepItemMapper.selectList(new QueryWrapper<PayProductProcessStepItem>()
                        .lambda().eq(PayProductProcessStepItem::getProductProcessStepSid, item.getProductProcessStepSid()));
                if (CollectionUtil.isNotEmpty(itemList)){
                    BigDecimal totalPriceBefore = BigDecimal.ZERO;
                    BigDecimal totalPriceAfter = BigDecimal.ZERO;
                    for (PayProductProcessStepItem stepItem : itemList) {
                        BigDecimal price = BigDecimal.ZERO;
                        BigDecimal priceRate = BigDecimal.ZERO;
                        if (stepItem.getPrice() != null){
                            price = stepItem.getPrice();
                        }
                        if (stepItem.getPriceRate() != null){
                            priceRate = stepItem.getPriceRate();
                        }
                        totalPriceBefore = totalPriceBefore.add(price);
                        totalPriceAfter = totalPriceAfter.add(price.multiply(priceRate));
                    }
                    item.setTotalPriceBefore(totalPriceBefore).setTotalPriceAfter(totalPriceAfter);
                }
            });
        }
        return list;
    }

    /**
     * 新增商品道序-主
     * 需要注意编码重复校验
     *
     * @param payProductProcessStep 商品道序-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayProductProcessStep insertPayProductProcessStep(PayProductProcessStep payProductProcessStep) {
        String code = getCode(payProductProcessStep);
        //按款号+工厂+生产工艺类型校验是否唯一
        List<PayProductProcessStep> list = verify(payProductProcessStep);
        if (CollUtil.isNotEmpty(list)) {
            List<DictData> dictDataList = sysDictDataService.selectDictData("s_product_price_type");
            dictDataList = dictDataList.stream().filter(o -> o.getDictValue().equals(list.get(0).getProductPriceType())).collect(Collectors.toList());
            List<DictData> dictDataList2 = sysDictDataService.selectDictData("s_jixin_wangong_type");
            dictDataList2 = dictDataList2.stream().filter(o -> o.getDictValue().equals(list.get(0).getJixinWangongType())).collect(Collectors.toList());
            //
            String departmentName = "";
            try {
                ConManufactureDepartment department = conManufactureDepartmentMapper.selectOne(new QueryWrapper<ConManufactureDepartment>()
                        .lambda().eq(ConManufactureDepartment::getCode, payProductProcessStep.getDepartment()));
                if (department != null && department.getName() != null) {
                    departmentName = department.getName();
                }
            }catch (Exception e) {
                log.warn("department not found！");
            }
            payProductProcessStep = list.get(0);
            payProductProcessStep.setMsg("该工厂下，已创建商品编码/我司样衣号" + code + "，商品工价类型" + dictDataList.get(0).getDictLabel() + "，计薪完工类型" +
                    dictDataList2.get(0).getDictLabel() + "，操作部门" + departmentName + "的商品道序，正在跳转页面！");
        } else {
            List<PayProductProcessStepItem> payProductProcessStepItemList = payProductProcessStep.getPayProductProcessStepItemList();
            this.verifySort(payProductProcessStepItemList);
            //设置确认信息
            setConfirmInfo(payProductProcessStep);
            BasPlant basPlant = basPlantMapper.selectBasPlantById(payProductProcessStep.getPlantSid());
            String plantCode=null;
            if(BeanUtil.isNotEmpty(basPlant)){
                plantCode = basPlant.getPlantCode();
            }
            int row = payProductProcessStepMapper.insert(payProductProcessStep.setPlantCode(plantCode));
            //商品道序-明细对象
            if (CollectionUtil.isNotEmpty(payProductProcessStepItemList)) {
                addPayProductProcessStepItem(payProductProcessStep, payProductProcessStepItemList);
                //回写道序
                collbackProcessStep(payProductProcessStep, payProductProcessStepItemList);
            }
            //商品道序-附件对象
            List<PayProductProcessStepAttach> payProductProcessStepAttachList = payProductProcessStep.getAttachmentList();
            if (CollectionUtil.isNotEmpty(payProductProcessStepAttachList)) {
                addPayProductProcessStepAttach(payProductProcessStep, payProductProcessStepAttachList);
            }
            PayProductProcessStep processStep = payProductProcessStepMapper.selectPayProductProcessStepById(payProductProcessStep.getProductProcessStepSid());
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(payProductProcessStep.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_PRODUCT_PROCESS_STEP)
                        .setDocumentSid(payProductProcessStep.getProductProcessStepSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    // 获取菜单id
                    SysMenu menu = new SysMenu();
                    menu.setMenuName(ConstantsEms.TODO_PRO_STEP_INFO_MENU_NAME);
                    menu = remoteMenuService.getInfoByName(menu).getData();
                    if (menu != null && menu.getMenuId() != null) {
                        sysTodoTask.setMenuId(menu.getMenuId());
                    }
                    sysTodoTask.setTitle("商品道序" + code + "当前是保存状态，请及时处理！")
                            .setDocumentCode(code)
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(payProductProcessStep);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(payProductProcessStep.getProductProcessStepSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }

        return payProductProcessStep;
    }

    /**
     * 【优化】【商品道序】同一道序，在不同商品道序中，若工价不一致，给予提醒
     * @param payProductProcessStep 商品道序-主
     * @return 结果
     */
    @Override
    public EmsResultEntity checkPrice(PayProductProcessStep payProductProcessStep) {
        if (CollectionUtil.isEmpty(payProductProcessStep.getPayProductProcessStepItemList())) {
            return EmsResultEntity.success();
        }
        List<PayProductProcessStepItem> itemList = payProductProcessStep.getPayProductProcessStepItemList();
        itemList = itemList.stream().sorted(Comparator.comparing(PayProductProcessStepItem::getSort, Comparator.nullsLast(BigDecimal::compareTo))).collect(Collectors.toList());
        List<Long> processStepSids = itemList.stream()
                .map(PayProductProcessStepItem::getProcessStepSid).collect(Collectors.toList());
        List<Long> stepItemSids = itemList.stream().filter(o -> o.getStepItemSid() != null)
                .map(PayProductProcessStepItem::getStepItemSid).collect(Collectors.toList());
        // 找出所有符合 工厂 + 道序sid 在道序明细表中的 商品道序明细
        List<PayProductProcessStepItem> tableList = payProductProcessStepItemMapper.selectPayProductProcessStepItemList(new PayProductProcessStepItem()
                .setPlantSid(payProductProcessStep.getPlantSid()).setProcessStepSids(processStepSids));
        // 变更编辑页面 去除自己的 数据 判断
        if (CollectionUtil.isNotEmpty(stepItemSids)) {
            tableList = tableList.stream().filter(o-> !stepItemSids.contains(o.getStepItemSid())).collect(toList());
        }
        Map<Long, List<PayProductProcessStepItem>> map = tableList.stream().collect(Collectors.groupingBy(o -> o.getProcessStepSid()));
        // 存放 异常信息
        List<CommonErrMsgResponse> msgList = new ArrayList<>();
        itemList.forEach(item->{
            if (!"Y".equals(item.getDelFlagBiangz())){
                if (item.getPrice() == null) {
                    return;
                }
                List<PayProductProcessStepItem> compare = map.get(item.getProcessStepSid());
                if (compare == null) {
                    return;
                }
                for (int i = 0; i < compare.size(); i++) {
                    if (compare.get(i).getPrice() == null) {
                        continue;
                    }
                    // 工价不一致
                    if (item.getPrice().compareTo(compare.get(i).getPrice()) != 0) {
                        CommonErrMsgResponse msg = new CommonErrMsgResponse();
                        msg.setSort(item.getSort());
                        msg.setItemNum(item.getExportNum());
                        msg.setMsg("道序“" + item.getProcessStepName() + "”，道序工价与系统中其它款号的道序工价不一致，请核实！");
                        msgList.add(msg);
                        break;
                    }
                }
            }

        });
        if (CollectionUtil.isNotEmpty(msgList)) {
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                    .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            if (settingClient != null && ConstantsEms.S_MESSAGE_DISPLAT_TYPE_TS.equals(settingClient.getNoticeTypeProcessPriceInconsistent())) {
                return EmsResultEntity.warning(msgList);
            }
            else if (settingClient != null && ConstantsEms.S_MESSAGE_DISPLAT_TYPE_BC.equals(settingClient.getNoticeTypeProcessPriceInconsistent())) {
                return EmsResultEntity.error(msgList);
            }
        }
        return EmsResultEntity.success();
    }

    public String getCode(PayProductProcessStep payProductProcessStep){
        String code = "";
        if (payProductProcessStep.getProductCode()==null){
            if (payProductProcessStep.getSampleCodeSelf() != null){
                code = payProductProcessStep.getSampleCodeSelf();
            }
        }else {
            code = payProductProcessStep.getProductCode();
        }
        return code;
    }

    /**
     * 按款号+工厂+生产工艺类型校验是否唯一
     */
    private List<PayProductProcessStep> verify(PayProductProcessStep payProductProcessStep) {
        List<PayProductProcessStep> list = payProductProcessStepMapper.selectList(new QueryWrapper<PayProductProcessStep>().lambda()
                .eq(PayProductProcessStep::getProductSid, payProductProcessStep.getProductSid())
                .eq(PayProductProcessStep::getPlantSid, payProductProcessStep.getPlantSid())
                .eq(PayProductProcessStep::getProductPriceType, payProductProcessStep.getProductPriceType())
                .eq(PayProductProcessStep::getJixinWangongType, payProductProcessStep.getJixinWangongType())
                .eq(PayProductProcessStep::getDepartment, payProductProcessStep.getDepartment()));
        return list;
    }

    /**
     * 回写道序
     */
    private void collbackProcessStep(PayProductProcessStep payProductProcessStep, List<PayProductProcessStepItem> payProductProcessStepItemList) {
        //不存在相同道序，则插入一笔新数据，并回写编码及sid
        List<PayProductProcessStepItem> stepItemList = payProductProcessStepItemList.stream().filter(item -> item.getProcessStepSid() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(stepItemList)) {
            PayProcessStep payProcessStep = new PayProcessStep();
            for (PayProductProcessStepItem item : stepItemList) {
                BeanUtil.copyProperties(item, payProcessStep, new String[]{"creatorAccount", "createDate", "updaterAccount", "updateDate"});
                payProcessStep.setConfirmDate(new Date())
                        .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername())
                        .setStatus(ConstantsEms.ENABLE_STATUS)
                        .setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setCurrency(ConstantsEms.RMB)
                        .setCurrencyUnit(ConstantsEms.YUAN);
                payProcessStepMapper.insert(payProcessStep);
                PayProcessStep step = new PayProcessStep();
                step.setProcessStepName(item.getProcessStepName());
                List<PayProcessStep> processStepList = payProcessStepMapper.selectPayProcessStepList(step);
                payProductProcessStepItemMapper.updateById(new PayProductProcessStepItem().setStepItemSid(item.getStepItemSid())
                        .setProcessStepSid(processStepList.get(0).getProcessStepSid())
                        .setProcessStepCode(processStepList.get(0).getProcessStepCode()));
            }
        }
        //已存在相同道序，确认时更新道序档案处理状态为已确认
        stepItemList = payProductProcessStepItemList.stream().filter(o -> o.getProcessStepSid() != null).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(stepItemList)) {
            for (PayProductProcessStepItem item : stepItemList) {
                PayProcessStep payProcessStep = payProcessStepMapper.selectPayProcessStepById(item.getProcessStepSid());
                if (ConstantsEms.CHECK_STATUS.equals(payProductProcessStep.getHandleStatus()) && ConstantsEms.SAVA_STATUS.equals(payProcessStep.getHandleStatus())) {
                    payProcessStep.setHandleStatus(ConstantsEms.CHECK_STATUS);
                    payProcessStep.setConfirmDate(new Date());
                    payProcessStep.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                    payProcessStepMapper.updateAllById(payProcessStep);
                }
            }
        }
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(PayProductProcessStep payProductProcessStep) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, payProductProcessStep.getProductProcessStepSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, payProductProcessStep.getProductProcessStepSid()));
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(PayProductProcessStep o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            List<PayProductProcessStepItem> itemList = o.getPayProductProcessStepItemList();
            if (CollectionUtil.isEmpty(itemList)) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 检查明细序号是否重复
     */
    public void verifySort(List<PayProductProcessStepItem> payProductProcessStepItemList) {
        if (payProductProcessStepItemList == null || payProductProcessStepItemList.size() == 0){
            return;
        }
        int size = payProductProcessStepItemList.size();
        payProductProcessStepItemList = payProductProcessStepItemList.stream().filter(distinctByKey(s->s.getSort())).collect(Collectors.toList());
        if (size != payProductProcessStepItemList.size()){
            throw new BaseException("“道序列表”页签，序号不允许重复，请检查！");
        }
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * 商品道序-明细对象
     */
    private void addPayProductProcessStepItem(PayProductProcessStep payProductProcessStep, List<PayProductProcessStepItem> payProductProcessStepItemList) {
//        deleteItem(payProductProcessStep);
        payProductProcessStepItemList.forEach(o -> {
            o.setProductProcessStepSid(payProductProcessStep.getProductProcessStepSid());
        });
        payProductProcessStepItemMapper.inserts(payProductProcessStepItemList);
    }

    private void deleteItem(PayProductProcessStep payProductProcessStep) {
        payProductProcessStepItemMapper.delete(
                new UpdateWrapper<PayProductProcessStepItem>()
                        .lambda()
                        .eq(PayProductProcessStepItem::getProductProcessStepSid, payProductProcessStep.getProductProcessStepSid())
        );
    }

    /**
     * 商品道序-附件对象
     */
    private void addPayProductProcessStepAttach(PayProductProcessStep payProductProcessStep, List<PayProductProcessStepAttach> payProductProcessStepAttachList) {
//        deleteAttach(payProductProcessStep);
        payProductProcessStepAttachList.forEach(o -> {
            o.setProductProcessStepSid(payProductProcessStep.getProductProcessStepSid());
        });
        payProductProcessStepAttachMapper.inserts(payProductProcessStepAttachList);
    }

    private void deleteAttach(PayProductProcessStep payProductProcessStep) {
        payProductProcessStepAttachMapper.delete(
                new UpdateWrapper<PayProductProcessStepAttach>()
                        .lambda()
                        .eq(PayProductProcessStepAttach::getProductProcessStepSid, payProductProcessStep.getProductProcessStepSid())
        );
    }


    /**
     * 修改商品道序-主
     *
     * @param payProductProcessStep 商品道序-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayProductProcessStep updatePayProductProcessStep(PayProductProcessStep payProductProcessStep) {
        //按款号+工厂+生产工艺类型校验是否唯一
        PayProductProcessStep processStep = checkUnique(payProductProcessStep);
        if (processStep.getProductProcessStepSid() == null) {
            this.verifySort(payProductProcessStep.getPayProductProcessStepItemList());
            setConfirmInfo(payProductProcessStep);
            payProductProcessStep.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
            PayProductProcessStep response = payProductProcessStepMapper.selectPayProductProcessStepById(payProductProcessStep.getProductProcessStepSid());
            BasPlant basPlant = basPlantMapper.selectBasPlantById(payProductProcessStep.getPlantSid());
            String plantCode = null;
            if(BeanUtil.isNotEmpty(plantCode)){
                plantCode = basPlant.getPlantCode();
            }
            int row = payProductProcessStepMapper.updateAllById(payProductProcessStep.setPlantCode(plantCode));
            //商品道序-明细对象
            List<PayProductProcessStepItem> stepItemList = payProductProcessStep.getPayProductProcessStepItemList();
            operateItem(payProductProcessStep, stepItemList);
            //商品道序-附件对象
            List<PayProductProcessStepAttach> stepAttachList = payProductProcessStep.getAttachmentList();
            operateAttachment(payProductProcessStep, stepAttachList);
            if (!ConstantsEms.SAVA_STATUS.equals(payProductProcessStep.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(payProductProcessStep);
            }
            //插入日志
            MongodbUtil.insertUserLog(payProductProcessStep.getProductProcessStepSid(), BusinessType.UPDATE.getValue(), response, payProductProcessStep, TITLE);
        }
        return processStep;
    }

    private PayProductProcessStep checkUnique(PayProductProcessStep payProductProcessStep) {

        String code = getCode(payProductProcessStep);
        List<PayProductProcessStep> list = verify(payProductProcessStep);
        PayProductProcessStep processStep = new PayProductProcessStep();
        if (CollUtil.isNotEmpty(list)) {
            List<DictData> productPriceTypeList = sysDictDataService.selectDictData("s_product_price_type");
            productPriceTypeList = productPriceTypeList.stream()
                    .filter(o -> o.getDictValue().equals(payProductProcessStep.getProductPriceType())).collect(Collectors.toList());
            List<DictData> dictDataList = sysDictDataService.selectDictData("s_jixin_wangong_type");
            dictDataList = dictDataList.stream().filter(o -> o.getDictValue().equals(list.get(0).getJixinWangongType())).collect(Collectors.toList());
            String departmentName = "";
            try {
                ConManufactureDepartment department = conManufactureDepartmentMapper.selectOne(new QueryWrapper<ConManufactureDepartment>()
                        .lambda().eq(ConManufactureDepartment::getCode, payProductProcessStep.getDepartment()));
                if (department != null && department.getName() != null) {
                    departmentName = department.getName();
                }
            }catch (Exception e) {
                log.warn("department not found！");
            }
            for (PayProductProcessStep o : list) {
                if (!o.getProductProcessStepSid().equals(payProductProcessStep.getProductProcessStepSid())) {
                    processStep.setMsg("该工厂下，已创建商品编码/我司样衣号" + code + "，商品工价类型" + productPriceTypeList.get(0).getDictLabel() + "，计薪完工类型"+
                            dictDataList.get(0).getDictLabel() + "，操作部门" + departmentName + "的商品道序，正在跳转页面！");
                    processStep.setProductProcessStepSid(o.getProductProcessStepSid());
                }
            }
        }
        return processStep;
    }

    /**
     * 商品道序-明细
     */
    private void operateItem(PayProductProcessStep payProductProcessStep, List<PayProductProcessStepItem> stepItemList) {
        if (CollectionUtil.isNotEmpty(stepItemList)) {
            //新增
            List<PayProductProcessStepItem> addList = stepItemList.stream().filter(o -> o.getStepItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addPayProductProcessStepItem(payProductProcessStep, addList);
            }
            //编辑
            List<PayProductProcessStepItem> editList = stepItemList.stream().filter(o -> o.getStepItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    payProductProcessStepItemMapper.updateAllById(o);
                });
            }
            //原有数据
            List<PayProductProcessStepItem> itemList = payProductProcessStepItemMapper.selectList(new QueryWrapper<PayProductProcessStepItem>().lambda()
                    .eq(PayProductProcessStepItem::getProductProcessStepSid, payProductProcessStep.getProductProcessStepSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(PayProductProcessStepItem::getStepItemSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = stepItemList.stream().map(PayProductProcessStepItem::getStepItemSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                payProductProcessStepItemMapper.deleteBatchIds(result);
            }
            /*if (ConstantsEms.CHECK_STATUS.equals(payProductProcessStep.getHandleStatus())) {
                //确认时回写道序
                collbackProcessStep(stepItemList);
            }*/
            //回写道序
            collbackProcessStep(payProductProcessStep, stepItemList);
        } else {
            deleteItem(payProductProcessStep);
        }
    }

    /**
     * 商品道序-附件
     */
    private void operateAttachment(PayProductProcessStep payProductProcessStep, List<PayProductProcessStepAttach> stepAttachList) {
        if (CollectionUtil.isNotEmpty(stepAttachList)) {
            //新增
            List<PayProductProcessStepAttach> addList = stepAttachList.stream().filter(o -> o.getAttachmentSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(addList)) {
                addPayProductProcessStepAttach(payProductProcessStep, addList);
            }
            //编辑
            List<PayProductProcessStepAttach> editList = stepAttachList.stream().filter(o -> o.getAttachmentSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(editList)) {
                editList.forEach(o -> {
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
                    payProductProcessStepAttachMapper.updateAllById(o);
                });
            }
            //原有数据
            List<PayProductProcessStepAttach> itemList =
                    payProductProcessStepAttachMapper.selectList(new QueryWrapper<PayProductProcessStepAttach>().lambda()
                            .eq(PayProductProcessStepAttach::getProductProcessStepSid, payProductProcessStep.getProductProcessStepSid()));
            //原有数据ids
            List<Long> originalIds = itemList.stream().map(PayProductProcessStepAttach::getAttachmentSid).collect(Collectors.toList());
            //现有数据ids
            List<Long> currentIds = stepAttachList.stream().map(PayProductProcessStepAttach::getAttachmentSid).collect(Collectors.toList());
            //清空删除的数据
            List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(result)) {
                payProductProcessStepAttachMapper.deleteBatchIds(result);
            }
        } else {
            deleteAttach(payProductProcessStep);
        }
    }

    /**
     * 变更商品道序-主
     *
     * @param payProductProcessStep 商品道序-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayProductProcessStep changePayProductProcessStep(PayProductProcessStep payProductProcessStep) {
        String code = getCode(payProductProcessStep);
        //按款号+工厂+生产工艺类型校验是否唯一
        PayProductProcessStep processStep = checkUnique(payProductProcessStep);
        if (processStep.getProductProcessStepSid() == null) {
            this.verifySort(payProductProcessStep.getPayProductProcessStepItemList());
            setConfirmInfo(payProductProcessStep);
            payProductProcessStep.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
            PayProductProcessStep response = payProductProcessStepMapper.selectPayProductProcessStepById(payProductProcessStep.getProductProcessStepSid());
            BasPlant basPlant = basPlantMapper.selectBasPlantById(payProductProcessStep.getPlantSid());
            String plantCode=null;
            if(Objects.nonNull(basPlant)){
                plantCode = basPlant.getPlantCode();
            }
            int row = payProductProcessStepMapper.updateAllById(payProductProcessStep.setPlantCode(plantCode));
            //商品道序-明细对象
            List<PayProductProcessStepItem> stepItemList = payProductProcessStep.getPayProductProcessStepItemList();
            operateItem(payProductProcessStep, stepItemList);
            //商品道序-附件对象
            List<PayProductProcessStepAttach> stepAttachList = payProductProcessStep.getAttachmentList();
            operateAttachment(payProductProcessStep, stepAttachList);
            SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
            sysBusinessBcst.setTitle("商品道序" + code + "已更新")
                    .setDocumentSid(payProductProcessStep.getProductSid())
                    .setDocumentCode(code)
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysBusinessBcstMapper.insert(sysBusinessBcst);
            if (ConstantsEms.CHECK_STATUS.equals(payProductProcessStep.getHandleStatus())){
                if (ConstantsEms.PRODUCT_PRICE_TYPE_DH.equals(payProductProcessStep.getProductPriceType())){
                    if (payProductProcessStep.getPlantSid() != null){
                        BasPlant plant = basPlantMapper.selectById(payProductProcessStep.getPlantSid());
                        if (plant != null){
                            // 删除相关待办
                            String title = "商品"+ payProductProcessStep.getProductCode() + "在" + plant.getPlantName() + "的道序工价还未创建或未确认" ;
                            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                    .eq(SysTodoTask::getTitle, title)
                                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_MANUFACTURE_ORDER+";"+ConstantsTable.TABLE_PRODUCT_PROCESS_STEP)
                                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB));
                        }
                    }
                }
            }
            //插入日志
            MongodbUtil.insertUserLog(payProductProcessStep.getProductProcessStepSid(), BusinessType.CHANGE.getValue(), response, payProductProcessStep, TITLE);
        }
        return processStep;
    }

    /**
     * 变更页面点暂存商品道序-主
     *
     * @param payProductProcessStep 商品道序-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayProductProcessStep newUpdatePayProductProcessStep(PayProductProcessStep payProductProcessStep) {
        //按款号+工厂+生产工艺类型校验是否唯一
        PayProductProcessStep processStep = checkUnique(payProductProcessStep);
        if (processStep.getProductProcessStepSid() == null) {
            this.verifySort(payProductProcessStep.getPayProductProcessStepItemList());
            // 存到 商品道序变更 的主表 明细表 附件表
            PayUpdateProductProcessStep updateProductProcessStep = new PayUpdateProductProcessStep();
            BeanCopyUtils.copyProperties(payProductProcessStep, updateProductProcessStep);
            // 更新字段
            updateProductProcessStep.setHandleStatus(ConstantsEms.SAVA_STATUS);
            updateProductProcessStep.setCreateDate(null).setUpdateDate(null).setConfirmDate(null)
                    .setCreatorAccount(null).setUpdaterAccount(null).setConfirmerAccount(null);
            if (payProductProcessStep.getPlantSid() != null) {
                BasPlant basPlant = basPlantMapper.selectBasPlantById(payProductProcessStep.getPlantSid());
                if (basPlant != null) {
                    updateProductProcessStep.setPlantCode(basPlant.getPlantCode());
                }
            }
            if (CollectionUtil.isNotEmpty(payProductProcessStep.getPayProductProcessStepItemList())) {
                List<PayUpdateProductProcessStepItem> updateProductProcessStepItemList = BeanCopyUtils.copyListProperties
                        (payProductProcessStep.getPayProductProcessStepItemList(), PayUpdateProductProcessStepItem::new);
                updateProductProcessStepItemList.forEach(item->{
                    item.setCreateDate(null).setUpdateDate(null).setCreatorAccount(null).setUpdaterAccount(null);
                });
                updateProductProcessStep.setUpdateItemList(updateProductProcessStepItemList);
            }
            if (CollectionUtil.isNotEmpty(payProductProcessStep.getAttachmentList())) {
                List<PayUpdateProductProcessStepAttach> updateProductProcessStepAttachList = BeanCopyUtils.copyListProperties
                        (payProductProcessStep.getAttachmentList(), PayUpdateProductProcessStepAttach::new);
                updateProductProcessStepAttachList.forEach(item->{
                    item.setCreateDate(null).setUpdateDate(null).setCreatorAccount(null).setUpdaterAccount(null);
                });
                updateProductProcessStep.setAttachmentList(updateProductProcessStepAttachList);
            }
            payUpdateProductProcessStepService.insertPayUpdateProductProcessStep(updateProductProcessStep);
            // 对商品道序处理
            payProductProcessStepMapper.update(null, new LambdaUpdateWrapper<PayProductProcessStep>()
                    .set(PayProductProcessStep::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(PayProductProcessStep::getIsUpdate, ConstantsEms.YES)
                    .eq(PayProductProcessStep::getProductProcessStepSid, payProductProcessStep.getProductProcessStepSid()));
            MongodbUtil.insertUserLog(payProductProcessStep.getProductProcessStepSid(), BusinessType.CHANGE.getValue(), null,  TITLE);
            return processStep;
        }
        else {
            return processStep;
        }
    }

    /**
     * 批量删除商品道序-主
     *
     * @param productSids 需要删除的商品道序-主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayProductProcessStepByIds(List<Long> productProcessStepSids) {
        String[] handleStatus = new String[]{ConstantsEms.SAVA_STATUS,ConstantsEms.BACK_STATUS};
        Integer count = payProductProcessStepMapper.selectCount(new QueryWrapper<PayProductProcessStep>().lambda()
                .in(PayProductProcessStep::getHandleStatus, handleStatus)
                .in(PayProductProcessStep::getProductProcessStepSid, productProcessStepSids));
        if (count != productProcessStepSids.size()) {
            throw new BaseException("仅保存和已退回状态才充许删除");
        }
        payProductProcessStepMapper.delete(new UpdateWrapper<PayProductProcessStep>().lambda()
                .in(PayProductProcessStep::getProductProcessStepSid, productProcessStepSids));
        //删除商品道序-明细对象
        payProductProcessStepItemMapper.delete(new UpdateWrapper<PayProductProcessStepItem>().lambda()
                .in(PayProductProcessStepItem::getProductProcessStepSid, productProcessStepSids));
        //删除商品道序-附件对象
        payProductProcessStepAttachMapper.delete(new UpdateWrapper<PayProductProcessStepAttach>().lambda()
                .in(PayProductProcessStepAttach::getProductProcessStepSid, productProcessStepSids));
        PayProductProcessStep payProductProcessStep = new PayProductProcessStep();
        productProcessStepSids.forEach(productProcessStepSid -> {
            payProductProcessStep.setProductProcessStepSid(productProcessStepSid);
            //校验是否存在待办
            checkTodoExist(payProductProcessStep);
        });
        return productProcessStepSids.size();
    }

    /**
     * 更改确认状态
     *
     * @param payProductProcessStep
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(PayProductProcessStep payProductProcessStep) {
        int row = 0;
        Long[] sids = payProductProcessStep.getProductProcessStepSidList();
        if (sids != null && sids.length > 0) {
            UpdateWrapper<PayProductProcessStep> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(PayProductProcessStep::getHandleStatus, payProductProcessStep.getHandleStatus())
                    .in(PayProductProcessStep::getProductProcessStepSid, sids);
            if (ConstantsEms.CHECK_STATUS.equals(payProductProcessStep.getHandleStatus())){
                updateWrapper.lambda().set(PayProductProcessStep::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                        .set(PayProductProcessStep::getConfirmDate, new Date());
            }
            row = payProductProcessStepMapper.update(null, updateWrapper);
            if (ConstantsEms.CHECK_STATUS.equals(payProductProcessStep.getHandleStatus())){
                for (Long id : sids) {
                    PayProductProcessStep processStep = selectPayProductProcessStepById(id);
                    List<PayProductProcessStepItem> itemList = processStep.getPayProductProcessStepItemList();
                    if (CollUtil.isNotEmpty(itemList)) {
                        //回写道序
                        collbackProcessStep(processStep, itemList);
                    }
                    if (ConstantsEms.PRODUCT_PRICE_TYPE_DH.equals(processStep.getProductPriceType())){
                        // 删除相关待办
                        String title = "商品"+ processStep.getProductCode() + "在" + processStep.getPlantName() + "的道序工价还未创建或未确认" ;
                        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                .eq(SysTodoTask::getTitle, title)
                                .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_MANUFACTURE_ORDER+";"+ConstantsTable.TABLE_PRODUCT_PROCESS_STEP)
                                .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB));
                    }
                    // 删除保存/待审批/已驳回的待办
                    sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                            .eq(SysTodoTask::getDocumentSid, id)
                            .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB));
                }
            }
            else {
                for (Long id : sids) {
                    payProductProcessStep.setProductProcessStepSid(id);
                    //校验是否存在待办
                    checkTodoExist(payProductProcessStep);
                }
            }

        }
        return row;
    }

    /**
     * 商品道序下拉框接口
     */
    @Override
    public List<PayProductProcessStep> getList(PayProductProcessStep payProductProcessStep) {
        return payProductProcessStepMapper.getList
                (payProductProcessStep);
    }

    /**
     * 确认校验明细工价是否大于商品工价上限
     */
    @Override
    public EmsResultEntity verifyPrice(PayProductProcessStep payProductProcessStep) {
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>(); // 报错信息
        List<CommonErrMsgResponse> warnList = new ArrayList<>();  // 可忽略信息
        // 得到 此时的 商品编码 或者 我司样衣号
        String code = getCode(payProductProcessStep);
        // 变更页面点确认的校验
        PayProductProcessStep processStep = new PayProductProcessStep();
        List<PayProductProcessStepItem> itemList = payProductProcessStep.getPayProductProcessStepItemList();
        if (CollectionUtil.isNotEmpty(itemList)) {
            //明细行工价之和 (倍率后)
            BigDecimal priceSumAfter = priceSumAfter(itemList);
            if (priceSumAfter.compareTo(payProductProcessStep.getLimitPrice()) > 0) {
                processStep.setVerify(ConstantsEms.YES);
                CommonErrMsgResponse msg = new CommonErrMsgResponse();
                if ("BG".equals(payProductProcessStep.getIsUpdatePps())) {
                    msg.setMsg("商品编码/我司样衣号" + code + "，道序工价小计(倍率后)(变更后)，高于“商品工价上限(倍率后)(变更后)”");
                } else {
                    msg.setMsg("商品编码/我司样衣号" + code + "，道序工价小计(倍率后)，高于“商品工价上限(倍率后)”");
                }
                errMsgList.add(msg);
                return EmsResultEntity.error(processStep, errMsgList, null);
            } else {
                processStep.setVerify(ConstantsEms.NO);
                EmsResultEntity priceResult = this.checkPrice(payProductProcessStep);
                warnList = priceResult.getMsgList(); // 校验工价是否一致
                if (CollectionUtil.isNotEmpty(warnList) && EmsResultEntity.WARN_TAG.equals(priceResult.getTag())) {
                    return EmsResultEntity.warning(processStep, warnList, null);
                }
                else if (CollectionUtil.isNotEmpty(warnList) && EmsResultEntity.ERROR_TAG.equals(priceResult.getTag())) {
                    return EmsResultEntity.error(processStep, warnList, null);
                }
                return EmsResultEntity.success(processStep);
            }
        }
        //查询页面确认
        Long[] productProcessStepSidList = payProductProcessStep.getProductProcessStepSidList();
        if (ArrayUtil.isNotEmpty(productProcessStepSidList)) {
            List<String> productCodeList = new ArrayList<>();
            List<String> codeList = new ArrayList<>();
            PayProductProcessStep info = new PayProductProcessStep();
            for (Long productProcessStepSid : productProcessStepSidList) {
                info = selectPayProductProcessStepById(productProcessStepSid);
                code = getCode(info);
                itemList = info.getPayProductProcessStepItemList();
                if (CollectionUtil.isNotEmpty(itemList)) {
                    //明细行工价之和
                    BigDecimal priceSumAfter = priceSumAfter(itemList);
                    if (priceSumAfter.compareTo(info.getLimitPrice()) > 0) {
                        productCodeList.add(code);
                    }
                } else {
                    codeList.add(code);
                }
                // 校验工价是否一致
                EmsResultEntity priceResult = this.checkPrice(info);
                List<CommonErrMsgResponse> msgList = priceResult.getMsgList();
                if (CollectionUtil.isNotEmpty(msgList) && EmsResultEntity.WARN_TAG.equals(priceResult.getTag())) {
                    warnList.addAll(msgList);
                }
                else if (CollectionUtil.isNotEmpty(msgList) && EmsResultEntity.ERROR_TAG.equals(priceResult.getTag())) {
                    errMsgList.addAll(msgList);
                }
            }

            if (CollectionUtil.isNotEmpty(codeList)) {
                CommonErrMsgResponse msg = new CommonErrMsgResponse();
                msg.setMsg("商品编码/我司样衣号" + codeList.toString() + "的道序" + "明细行为空，请检查！");
                errMsgList.add(msg);
            }
            if (CollectionUtil.isNotEmpty(productCodeList)) {
                processStep.setVerify(ConstantsEms.YES);
                CommonErrMsgResponse msg = new CommonErrMsgResponse();
                if ("BG".equals(payProductProcessStep.getIsUpdatePps())) {
                    msg.setMsg("商品编码/我司样衣号" + productCodeList.toString() + "，道序工价小计(倍率后)(变更后)，高于“商品工价上限(倍率后)(变更后)”");
                } else {
                    msg.setMsg("商品编码/我司样衣号" + productCodeList.toString() + "，道序工价小计(倍率后)，高于“商品工价上限(倍率后)”");
                }
                errMsgList.add(msg);
            } else {
                processStep.setVerify(ConstantsEms.NO);
                if (CollectionUtil.isNotEmpty(warnList)) {
                    return EmsResultEntity.warning(processStep, warnList, null);
                }
            }
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return EmsResultEntity.error(processStep, errMsgList, null);
            }
        }
        if (CollectionUtil.isEmpty(itemList)) {
            processStep.setVerify(ConstantsEms.YES);
            CommonErrMsgResponse msg = new CommonErrMsgResponse();
            msg.setMsg(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            errMsgList.add(msg);
            return EmsResultEntity.error(processStep, errMsgList, null);
        }
        return EmsResultEntity.success(processStep);
    }

    /**
     * 明细行工价之和 （倍率前）
     */
    private BigDecimal priceSum(List<PayProductProcessStepItem> itemList) {
        BigDecimal sum = BigDecimal.ZERO;
        if (CollectionUtil.isNotEmpty(itemList)) {
            sum = itemList.stream().filter(item -> item.getPrice() != null)
                    .map(PayProductProcessStepItem::getPrice).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
        }
        return sum;
    }

    /**
     * 明细行工价之和 （倍率前）
     */
    private BigDecimal priceSumAfter(List<PayProductProcessStepItem> itemList) {
        BigDecimal sum = BigDecimal.ZERO;
        if (CollectionUtil.isNotEmpty(itemList)) {
            BigDecimal totalPriceAfter = BigDecimal.ZERO;
            for (PayProductProcessStepItem stepItem : itemList) {
                if (!"Y".equals(stepItem.getDelFlagBiangz())){
                    BigDecimal price = BigDecimal.ZERO;
                    BigDecimal priceRate = BigDecimal.ZERO;
                    if (stepItem.getPrice() != null){
                        price = stepItem.getPrice();
                    }
                    if (stepItem.getPriceRate() != null){
                        priceRate = stepItem.getPriceRate();
                    }
                    totalPriceAfter = totalPriceAfter.add(price.multiply(priceRate));
                }
            }
            sum = totalPriceAfter.setScale(4, BigDecimal.ROUND_HALF_UP);
        }
        return sum;
    }

    /**
     * 添加专用道序时校验名称是否重复
     */
    @Override
    public PayProductProcessStepItem verifyProcess(PayProductProcessStepItem payProductProcessStepItem) {
        if (payProductProcessStepItem == null) {
            return null;
        }
        List<PayProcessStep> list = payProcessStepMapper.selectList(new QueryWrapper<PayProcessStep>().lambda()
                .eq(PayProcessStep::getProcessStepName, payProductProcessStepItem.getProcessStepName()));
        //已存在相同道序，则回写编码及sid
        if (CollectionUtil.isNotEmpty(list)) {
            PayProcessStep payProcessStep = list.get(0);
            BeanUtil.copyProperties(payProcessStep, payProductProcessStepItem, new String[]{"creatorAccount", "createDate", "updaterAccount", "updateDate", "remark"});
            payProductProcessStepItem.setStepCategory(payProcessStep.getStepCategory());
        }
        return payProductProcessStepItem;
    }

    /**
     * 导入商品道序
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importData(MultipartFile file) {
        List<PayProductProcessStep> productProcessStepList = new ArrayList<>();
        //商品道序明细
        List<PayProductProcessStepItem> productProcessStepItemList = new ArrayList<>();
        // 验证重复
        List<PayProductProcessStep> verifyList = new ArrayList<>();
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
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
            // 是否工序的最后一道道序
            List<DictData> yesNoList=sysDictDataService.selectDictData("sys_yes_no");
            yesNoList = yesNoList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> yesNoMaps=yesNoList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            // 商品工价类型
            List<DictData> productPriceTypeList=sysDictDataService.selectDictData("s_product_price_type");
            productPriceTypeList = productPriceTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> productPriceTypeMaps=productPriceTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            // 计薪完工类型
            List<DictData> completeTypeList = sysDictDataService.selectDictData("s_jixin_wangong_type");
            completeTypeList = completeTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> completeTypeMaps=completeTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //excel表里面工厂和商品工价类型和商品编码的缓存
            HashMap<String, String> mainMap = new HashMap<>();
            //excel表里面工厂和商品工价类型和商品编码和道序的缓存
            HashMap<String, String> itemMap = new HashMap<>();
            //excel表里面主表对应序号的缓存
            HashMap<String, String> sortMap = new HashMap<>();
            //excel表里面每行唯一主表的缓存
            HashMap<String, Object> productProcessStepMap = new HashMap<>();
            // 存放唯一主表
            PayProductProcessStep payProductProcessStep =null;
            // 工厂权限
            Long[] roleIds = null;
            List<SysRole> roleList = ApiThreadLocalUtil.get().getSysUser().getRoles();
            if (CollectionUtil.isNotEmpty(roleList)){
                roleIds = roleList.stream().map(SysRole::getRoleId).toArray(Long[]::new);
            }
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleIds(roleIds);
            roleMenu.setPerms("ems:plant:all");
            boolean isAll = true;
            if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())){
                isAll = remoteSystemService.isHavePerms(roleMenu).getData();
            }
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                int num = i + 1;
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                //唯一主表
                payProductProcessStep = new PayProductProcessStep();
                /*
                 * 工厂简称
                 */
                String plantShortName = objects.get(0)==null||objects.get(0)==""?null:objects.get(0).toString();
                Long plantSid = null;
                String plantCode = null;
                if(plantShortName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("工厂简称，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        BasPlant basPlant = basPlantMapper.selectOne(new QueryWrapper<BasPlant>()
                                .lambda().eq(BasPlant::getShortName, plantShortName));
                        if(basPlant==null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("工厂" + plantShortName + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else{
                            if (!ConstantsEms.ENABLE_STATUS.equals(basPlant.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basPlant.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("工厂，必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            else {
                                if (!isAll){
                                    Long staffSid = ApiThreadLocalUtil.get().getSysUser().getStaffSid();
                                    if (staffSid != null) {
                                        BasStaff staff = basStaffService.selectBasStaffById(staffSid);
                                        if (!basPlant.getPlantSid().equals(String.valueOf(staff.getDefaultPlantSid()))) {
                                            errMsg = new CommonErrMsgResponse();
                                            errMsg.setItemNum(num);
                                            errMsg.setMsg("无权限导入" + plantShortName + "（工厂简称）的商品道序，导入失败！");
                                            errMsgList.add(errMsg);
                                        }else {
                                            plantSid = Long.parseLong(basPlant.getPlantSid());
                                            plantCode = basPlant.getPlantCode();
                                        }
                                    }
                                }
                                else {
                                    plantSid = Long.parseLong(basPlant.getPlantSid());
                                    plantCode = basPlant.getPlantCode();
                                }
                            }
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(plantShortName +"工厂档案存在重复，请先检查该工厂，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 商品工价类型
                 */
                String productPriceTypeName = objects.get(1)==null||objects.get(1)==""?null:objects.get(1).toString();
                String productPriceType = null;
                if (productPriceTypeName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品工价类型，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if(StrUtil.isBlank(productPriceTypeMaps.get(productPriceTypeName))){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品工价类型，填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        productPriceType=productPriceTypeMaps.get(productPriceTypeName);
                    }
                }
                /*
                 * 计薪完工类型
                 */
                String completeTypeName = objects.get(2)==null||objects.get(2)==""?null:objects.get(2).toString();
                String completeType = null;
                if (completeTypeName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("计薪完工类型，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if(StrUtil.isBlank(completeTypeMaps.get(completeTypeName))){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计薪完工类型，填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        completeType=completeTypeMaps.get(completeTypeName);
                    }
                }
                /*
                 * 操作部门名称
                 */
                String departmentName = objects.get(3)==null||objects.get(3)==""?null:objects.get(3).toString();
                String department = "";
                if (departmentName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("操作部门名称，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        ConManufactureDepartment conManufactureDepartment = conManufactureDepartmentMapper.selectOne(new QueryWrapper<ConManufactureDepartment>()
                                .lambda().eq(ConManufactureDepartment::getName, departmentName));
                        if(conManufactureDepartment==null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("操作部门" + departmentName + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else{
                            if (!ConstantsEms.ENABLE_STATUS.equals(conManufactureDepartment.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(conManufactureDepartment.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("操作部门，必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            else {
                                department = conManufactureDepartment.getCode();
                            }
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(departmentName +"操作部门配置存在重复，请先检查该操作部门，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 商品编码
                 */
                String productCode = objects.get(4)==null||objects.get(4)==""?null:objects.get(4).toString();
                Long productSid = null;
                if (productCode == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品编码，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>()
                                .lambda().eq(BasMaterial::getMaterialCode, productCode));
                        if(basMaterial==null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品" + productCode + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else{
                            if (!ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("商品，必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            productSid = basMaterial.getMaterialSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(productCode +"商品档案存在重复，请先检查该商品，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                //验证系统中是否存在主表了
                String  main = null;
                verifyList = new ArrayList<>();
                if (plantSid != null && productPriceType != null && productSid != null && completeType != null && department != null){
                    main = plantSid.toString() + productPriceType + productSid.toString() + completeType + department;
                    //用来获取表格第一次出现的 工厂和商品工价类型和商品编码
                    if (mainMap.get(main) == null) {
                        mainMap.put(main, String.valueOf(num));
                        verifyList = payProductProcessStepMapper.selectPayProductProcessStepList(new PayProductProcessStep().setDepartment(department)
                                .setProductSid(productSid).setPlantSid(plantSid).setProductPriceType(productPriceType).setJixinWangongType(completeType));
                        if (CollectionUtil.isNotEmpty(verifyList)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("系统中，工厂'" + plantShortName + "'，已创建商品编码'" + productCode + "'，商品工价类型'" + productPriceTypeName +
                                    "'，计薪完工类型'" + completeTypeName + "'，操作部门名称'" + departmentName +
                                    "'的商品道序，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        //商品道序
                        payProductProcessStep.setPlantSid(plantSid);
                        payProductProcessStep.setPlantName(plantShortName);
                        payProductProcessStep.setProductSid(productSid);
                        payProductProcessStep.setProductCode(productCode);
                        payProductProcessStep.setProductPriceType(productPriceType);
                        payProductProcessStep.setJixinWangongType(completeType);
                        payProductProcessStep.setDepartment(department);
                        payProductProcessStep.setDepartmentName(departmentName);
                        payProductProcessStep.setPlantCode(plantCode);
                        payProductProcessStep.setCurrency(ConstantsFinance.CURRENCY_CNY);
                        payProductProcessStep.setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN);
                        payProductProcessStep.setHandleStatus(ConstantsEms.SAVA_STATUS);
                    }
                }
                /*
                 * 商品工价上限(倍率后)
                 */
                String limitPrice_s = objects.get(5)==null||objects.get(5)==""?null:objects.get(5).toString();
                BigDecimal limitPrice = null;
                if (limitPrice_s == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品工价上限(倍率后)，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(limitPrice_s,6,4)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品工价上限(倍率后)，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        limitPrice = new BigDecimal(limitPrice_s);
                        if (limitPrice != null && BigDecimal.ZERO.compareTo(limitPrice) >= 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品工价上限(倍率后)，不能小于等于0，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            limitPrice=limitPrice.divide(BigDecimal.ONE,4,BigDecimal.ROUND_HALF_UP);
                        }
                    }
                }
                // 控制只保存表中第一笔主表的 商品工价上限(倍率后) 和 工价倍率(商品)
                if (payProductProcessStep.getProductSid() != null){
                    payProductProcessStep.setLimitPrice(limitPrice);
                    productProcessStepMap.put(main,payProductProcessStep);
                }
                /*
                 * 道序序号
                 */
                String sort_s = objects.get(6)==null||objects.get(6)==""?null:objects.get(6).toString();
                BigDecimal sort = null;
                if (StrUtil.isBlank(sort_s)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("道序序号，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(sort_s,4,0)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("道序序号，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        sort = new BigDecimal(sort_s);
                        if (sort != null && BigDecimal.ZERO.compareTo(sort) >= 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("道序序号，不能小于等于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        if (StrUtil.isNotBlank(main)){
                            if (sortMap.get(main+sort_s) == null){
                                sortMap.put(main+sort_s, sort_s);
                            }
                            else {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("表格中，工厂‘" + plantShortName + "’，商品编码‘" + productCode + "’，商品工价类型‘" + productPriceTypeName
                                        + "’，计薪完工类型‘" + completeTypeName + "’，操作部门‘" + departmentName + "’的商品道序，道序序号‘" + sort_s + "’存在重复，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }
                /*
                 * 道序名称
                 */
                String processStepName = objects.get(7)==null||objects.get(7)==""?null:objects.get(7).toString();
                Long processStepSid = null;
                PayProcessStep payProcessStep = null;
                // 商品道序明细
                PayProductProcessStepItem payProductProcessStepItem = null;
                if (processStepName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("道序名称，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        payProcessStep = payProcessStepMapper.selectOne(new QueryWrapper<PayProcessStep>()
                                .lambda().eq(PayProcessStep::getProcessStepName, processStepName));
                        if(payProcessStep==null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("道序" + processStepName + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else{
                            if (!ConstantsEms.ENABLE_STATUS.equals(payProcessStep.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(payProcessStep.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("道序，必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            processStepSid = payProcessStep.getProcessStepSid();
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(processStepName +"道序存在重复，请先检查该道序，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                // 有道序是否重复
                String item = null;
                // 主表三个字段的key 和 道序 和主表没有在系统中了才走判断
                if(StrUtil.isNotBlank(main) && processStepSid != null && CollectionUtil.isEmpty(verifyList)){
                    item = main + ":" + processStepSid.toString();
                    // 主表三个字段 和 道序 形成新的key
                    if (itemMap.get(item) == null) {
                        itemMap.put(item, String.valueOf(num));
                        // 如果不存在重复就写入 商品道序明细
                        payProductProcessStepItem = new PayProductProcessStepItem();
                        payProductProcessStepItem.setStepCategory(payProcessStep.getStepCategory());
                        payProductProcessStepItem.setProcessStepCode(payProcessStep.getProcessStepCode());
                        payProductProcessStepItem.setProcessStepSid(payProcessStep.getProcessStepSid());
                        payProductProcessStepItem.setStandardPrice(payProcessStep.getStandardPrice());
                        payProductProcessStepItem.setProcessSid(payProcessStep.getProcessSid());
                        payProductProcessStepItem.setTaskUnit(payProcessStep.getTaskUnit());
                        payProductProcessStepItem.setProcessStepName(processStepName);
                        payProductProcessStepItem.setExportNum(num);
                    }else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格中，工厂'" + plantShortName + "'，商品编码'" + productCode + "'，商品工价类型'" + productPriceTypeName +
                                "'，计薪完工类型'" + completeTypeName + "'，操作部门名称'" + departmentName +
                                "'的商品道序已存在'"+ processStepName +"'道序，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 工价(元)
                 */
                String price_s = objects.get(8)==null||objects.get(8)==""?null:objects.get(8).toString();
                BigDecimal price = null;
                if (price_s == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("工价(元)，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(price_s,5,4)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("工价(元)，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        price = new BigDecimal(price_s);
                        if (price != null && BigDecimal.ZERO.compareTo(price) > 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("工价(元)，不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            price=price.divide(BigDecimal.ONE,4,BigDecimal.ROUND_HALF_UP);
                        }
                    }
                }
                /*
                 * 倍率(道序)
                 */
                String priceRate_s = objects.get(9)==null||objects.get(9)==""?null:objects.get(9).toString();
                BigDecimal priceRate = null;
                if (priceRate_s == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("倍率(道序)，不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDouble(priceRate_s,2,3)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("倍率(道序)，数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        priceRate = new BigDecimal(priceRate_s);
                        if (priceRate != null && BigDecimal.ONE.compareTo(priceRate) > 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("倍率(道序)，必须大于等于1，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            priceRate=priceRate.divide(BigDecimal.ONE,3,BigDecimal.ROUND_HALF_UP);
                        }
                    }
                }
                /*
                 * 是否工序的最后一道道序
                 */
                String isFinalName = objects.get(10)==null||objects.get(10)==""?null:objects.get(10).toString();
                String isFinal = null;
                if (isFinalName != null){
                    if(StrUtil.isBlank(yesNoMaps.get(isFinalName))){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否工序的最后一道道序，填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        isFinal=yesNoMaps.get(isFinalName);
                    }
                }
                /*
                 * 道序备注
                 */
                String remark = objects.get(11)==null||objects.get(11)==""?null:objects.get(11).toString();
                if (remark != null && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                // 如果此行存在明细则补充后面字段值
                if (payProductProcessStepItem != null){
                    payProductProcessStepItem.setPrice(price);
                    payProductProcessStepItem.setPriceRate(priceRate);
                    payProductProcessStepItem.setIsFinal(isFinal);
                    payProductProcessStepItem.setSort(sort);
                    payProductProcessStepItem.setRemark(remark);
                }
                // 找出写入缓存的唯一主表 ，然后补充后面字段 和 本行明细
                if (productProcessStepMap.get(main) != null){
                    payProductProcessStep = (PayProductProcessStep)productProcessStepMap.get(main);
                    if (payProductProcessStepItem != null) {
                        // 如果当前行不是第一个明细行
                        if (CollectionUtil.isNotEmpty(payProductProcessStep.getPayProductProcessStepItemList())){
                            List<PayProductProcessStepItem> newList = payProductProcessStep.getPayProductProcessStepItemList();
                            newList.add(payProductProcessStepItem);
                            payProductProcessStep.setPayProductProcessStepItemList(newList);
                        }
                        // 如果当前行是第一个明细行
                        else {
                            List<PayProductProcessStepItem> newList = new ArrayList<>();
                            newList.add(payProductProcessStepItem);
                            payProductProcessStep.setPayProductProcessStepItemList(newList);
                        }
                    }
                    productProcessStepMap.put(main, payProductProcessStep);
                }
                // 用来判断表格中 工价不一致
                if (payProductProcessStepItem != null) {
                    payProductProcessStepItem.setPlantSid(plantSid);
                    productProcessStepItemList.add(payProductProcessStepItem);
                }
            }
            for (Object value : productProcessStepMap.values()) {
                productProcessStepList.add((PayProductProcessStep)value);
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        if (CollUtil.isNotEmpty(productProcessStepList)) {
            List<CommonErrMsgResponse> warnList = new ArrayList<>();
            List<CommonErrMsgResponse> finalWarnList = warnList;
            // 表格中工价不一致
            Map<String, List<PayProductProcessStepItem>> priceMap = productProcessStepItemList.stream().collect(Collectors.groupingBy(e ->
                    String.valueOf(e.getPlantSid()) + "-"+ String.valueOf(e.getProcessStepSid())));
            // 遍历同一个工厂道序下的列表
            for (String key : priceMap.keySet()) {
                List<PayProductProcessStepItem> list = priceMap.get(key);
                for (int i = 0; i+1 < list.size(); i++) {
                    if (list.get(i+1).getPrice().compareTo(list.get(0).getPrice()) != 0) {
                        CommonErrMsgResponse msg = new CommonErrMsgResponse();
                        msg.setSort(list.get(i+1).getSort());
                        msg.setItemNum(list.get(i+1).getExportNum());
                        msg.setMsg("表格中，道序“" + list.get(i+1).getProcessStepName() + "”，道序工价与其它款号的道序工价不一致，请核实！");
                        warnList.add(msg);
                    }
                }
            }
            // 系统中工价不一致
            productProcessStepList.forEach(item->{
                EmsResultEntity b = this.checkPrice(item);
                if (CollectionUtil.isNotEmpty(b.getMsgList())) {
                    finalWarnList.addAll(b.getMsgList());
                }
            });
            if (CollectionUtil.isNotEmpty(warnList)) {
                warnList = warnList.stream().sorted(Comparator.comparing(CommonErrMsgResponse::getItemNum)).collect(toList());
                // 根据配置校验是要提醒还是报错
                SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                        .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
                if (CollectionUtil.isEmpty(errMsgList) && settingClient != null && ConstantsEms.S_MESSAGE_DISPLAT_TYPE_TS.equals(settingClient.getNoticeTypeProcessPriceInconsistent())) {
                    return EmsResultEntity.warning(productProcessStepList, warnList, null);
                }
                else if (settingClient != null && ConstantsEms.S_MESSAGE_DISPLAT_TYPE_BC.equals(settingClient.getNoticeTypeProcessPriceInconsistent())) {
                    errMsgList.addAll(warnList);
                }
            }
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return EmsResultEntity.error(errMsgList);
            }
            //待办通知
            List<SysTodoTask> todoTaskList = new ArrayList<>();
            productProcessStepList.forEach(o -> {
                // 计算商品工价小计倍率前/倍率后
                if (CollectionUtil.isNotEmpty(o.getPayProductProcessStepItemList())){
                    BigDecimal blq = o.getPayProductProcessStepItemList().parallelStream().filter(e -> e.getPrice() != null).map(PayProductProcessStepItem::getPrice)
                            .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
                    BigDecimal blh = o.getPayProductProcessStepItemList().stream().map(x->
                            x.getPrice().multiply(x.getPriceRate())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    o.setTotalPriceBlq(blq.setScale(4, BigDecimal.ROUND_HALF_UP)).setTotalPriceBlh(blh.setScale(4, BigDecimal.ROUND_HALF_UP));
                }
                payProductProcessStepMapper.insert(o);
                MongodbUtil.insertUserLog(o.getProductProcessStepSid(), BusinessType.IMPORT.getValue(), TITLE);
                if (CollectionUtil.isNotEmpty(o.getPayProductProcessStepItemList())){
                    o.getPayProductProcessStepItemList().forEach(item->{
                        item.setProductProcessStepSid(o.getProductProcessStepSid());
                    });
                    payProductProcessStepItemMapper.inserts(o.getPayProductProcessStepItemList());
                }
                PayProductProcessStep payProductProcessStep = payProductProcessStepMapper.selectPayProductProcessStepById(o.getProductProcessStepSid());
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_PAY_PROCESS_STEP)
                        .setDocumentSid(o.getProductProcessStepSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    String code = getCode(payProductProcessStep);
                    sysTodoTask.setTitle("商品道序" + code + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(payProductProcessStep.getProductCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    todoTaskList.add(sysTodoTask);
                }
            });
            sysTodoTaskMapper.inserts(todoTaskList);
        } else {
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return EmsResultEntity.error(errMsgList);
            }
            throw new BaseException("请填写数据后再进行导入");
        }
        return EmsResultEntity.success(null, "导入成功");
    }

    /**
     * 导入需要忽略并继续时直接写入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importAddData(List<PayProductProcessStep> stepList) {
        int row = 0;
        if (CollectionUtil.isNotEmpty(stepList)) {
            for (PayProductProcessStep payProductProcessStep : stepList) {
                // 计算商品工价小计倍率前/倍率后
                if (CollectionUtil.isNotEmpty(payProductProcessStep.getPayProductProcessStepItemList())){
                    BigDecimal blq = payProductProcessStep.getPayProductProcessStepItemList().parallelStream().filter(e ->
                                    e.getPrice() != null).map(PayProductProcessStepItem::getPrice)
                            .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
                    BigDecimal blh = payProductProcessStep.getPayProductProcessStepItemList().stream().map(x->
                            x.getPrice().multiply(x.getPriceRate())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    payProductProcessStep.setTotalPriceBlq(blq.setScale(4, BigDecimal.ROUND_HALF_UP)).setTotalPriceBlh(blh.setScale(4, BigDecimal.ROUND_HALF_UP));
                }
                row += payProductProcessStepMapper.insert(payProductProcessStep);
                Long sid = payProductProcessStep.getProductProcessStepSid();
                MongodbUtil.insertUserLog(sid, BusinessType.IMPORT.getValue(), TITLE);
                if (CollectionUtil.isNotEmpty(payProductProcessStep.getPayProductProcessStepItemList())){
                    payProductProcessStep.getPayProductProcessStepItemList().forEach(item->{
                        item.setProductProcessStepSid(sid);
                    });
                    payProductProcessStepItemMapper.inserts(payProductProcessStep.getPayProductProcessStepItemList());
                }
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_PRODUCT_PROCESS_STEP)
                        .setDocumentSid(sid);
                sysTodoTask.setTitle("商品道序" + payProductProcessStep.getProductCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(payProductProcessStep.getProductCode())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        }
        return row;
    }

    /**
     * 导入商品道序 单款
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importDataSingle(MultipartFile file) {
        PayProductProcessStep payProductProcessStep = new PayProductProcessStep();
        //商品道序明细
        List<PayProductProcessStepItem> productProcessStepItemList = new ArrayList<>();
        // 验证重复
        List<PayProductProcessStep> verifyList = new ArrayList<>();
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
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
            // 是否工序的最后一道道序
            List<DictData> yesNoList=sysDictDataService.selectDictData("sys_yes_no");
            yesNoList = yesNoList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> yesNoMaps=yesNoList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            // 商品工价类型
            List<DictData> productPriceTypeList=sysDictDataService.selectDictData("s_product_price_type");
            productPriceTypeList = productPriceTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> productPriceTypeMaps=productPriceTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            // 计薪完工类型
            List<DictData> completeTypeList = sysDictDataService.selectDictData("s_jixin_wangong_type");
            completeTypeList = completeTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String,String> completeTypeMaps=completeTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //excel表里面工厂和商品工价类型和商品编码的缓存
            HashMap<String, String> mainMap = new HashMap<>();
            //excel表里面主表对应序号的缓存
            HashMap<String, String> sortMap = new HashMap<>();
            //excel表里面主表对应序号的缓存
            HashMap<String, String> stepMap = new HashMap<>();


            // 工厂权限
            Long[] roleIds = null;
            List<SysRole> roleList = ApiThreadLocalUtil.get().getSysUser().getRoles();
            if (CollectionUtil.isNotEmpty(roleList)){
                roleIds = roleList.stream().map(SysRole::getRoleId).toArray(Long[]::new);
            }
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleIds(roleIds);
            roleMenu.setPerms("ems:plant:all");
            boolean isAll = true;
            if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())){
                isAll = remoteSystemService.isHavePerms(roleMenu).getData();
            }
            //唯一主表
            payProductProcessStep = new PayProductProcessStep();
            String productCode = null; Long productSid = null;
            String limitPrice_s = null; BigDecimal limitPrice = null;
            String plantShortName = null; Long plantSid = null; String plantCode = null;
            String productPriceTypeName = null; String productPriceType = null;
            String completeTypeName = null; String completeType = null;
            String departmentName = null; String department = null;
            String remark = null;

            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                int num = i + 1;
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);

                if (i == 2) {
                    /*
                     * 商品编码
                     */
                    productCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                    if (productCode == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品编码，不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        try {
                            BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>()
                                    .lambda().eq(BasMaterial::getMaterialCode, productCode));
                            if (basMaterial == null) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("商品" + productCode + "不存在，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                if (!ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())) {
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("商品，必须是确认且已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                } else {
                                    productSid = basMaterial.getMaterialSid();
                                }
                            }
                        } catch (Exception e) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(productCode + "商品档案存在重复，请先检查该商品，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    /*
                     * 商品工价上限(倍率后)
                     */
                    limitPrice_s = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                    if (limitPrice_s == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品工价上限(倍率后) ，不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if (!JudgeFormat.isValidDouble(limitPrice_s, 6, 4)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品工价上限(倍率后) ，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            limitPrice = new BigDecimal(limitPrice_s);
                            if (limitPrice != null && BigDecimal.ZERO.compareTo(limitPrice) >= 0) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("商品工价上限(倍率后) ，不能小于等于0，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                limitPrice = limitPrice.divide(BigDecimal.ONE, 4, BigDecimal.ROUND_HALF_UP);
                            }
                        }
                    }
                    /*
                     * 工厂简称
                     */
                    plantShortName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                    if (plantShortName == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("工厂简称，不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        try {
                            BasPlant basPlant = basPlantMapper.selectOne(new QueryWrapper<BasPlant>()
                                    .lambda().eq(BasPlant::getShortName, plantShortName));
                            if (basPlant == null) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("工厂" + plantShortName + "不存在，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                if (!ConstantsEms.ENABLE_STATUS.equals(basPlant.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basPlant.getHandleStatus())) {
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("工厂，必须是确认且已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                } else {
                                    if (!isAll) {
                                        Long staffSid = ApiThreadLocalUtil.get().getSysUser().getStaffSid();
                                        if (staffSid != null) {
                                            BasStaff staff = basStaffService.selectBasStaffById(staffSid);
                                            if (!basPlant.getPlantSid().equals(String.valueOf(staff.getDefaultPlantSid()))) {
                                                errMsg = new CommonErrMsgResponse();
                                                errMsg.setItemNum(num);
                                                errMsg.setMsg("无权限导入" + plantShortName + "（工厂简称）的商品道序，导入失败！");
                                                errMsgList.add(errMsg);
                                            } else {
                                                plantSid = Long.parseLong(basPlant.getPlantSid());
                                                plantCode = basPlant.getPlantCode();
                                            }
                                        }
                                    } else {
                                        plantSid = Long.parseLong(basPlant.getPlantSid());
                                        plantCode = basPlant.getPlantCode();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(plantShortName + "工厂档案存在重复，请先检查该工厂，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    /*
                     * 商品工价类型
                     */
                    productPriceTypeName = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                    if (productPriceTypeName == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品工价类型，不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if (StrUtil.isBlank(productPriceTypeMaps.get(productPriceTypeName))) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品工价类型，填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            productPriceType = productPriceTypeMaps.get(productPriceTypeName);
                        }
                    }
                    /*
                     * 计薪完工类型
                     */
                    completeTypeName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                    if (completeTypeName == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计薪完工类型，不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if (StrUtil.isBlank(completeTypeMaps.get(completeTypeName))) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("计薪完工类型，填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            completeType = completeTypeMaps.get(completeTypeName);
                        }
                    }
                    /*
                     * 操作部门
                     */
                    departmentName = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                    if (departmentName == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("操作部门，不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        if (departmentName == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("操作部门名称，不能为空，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            try {
                                ConManufactureDepartment conManufactureDepartment = conManufactureDepartmentMapper.selectOne(new QueryWrapper<ConManufactureDepartment>()
                                        .lambda().eq(ConManufactureDepartment::getName, departmentName));
                                if (conManufactureDepartment == null) {
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("操作部门" + departmentName + "不存在，导入失败！");
                                    errMsgList.add(errMsg);
                                } else {
                                    if (!ConstantsEms.ENABLE_STATUS.equals(conManufactureDepartment.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(conManufactureDepartment.getHandleStatus())) {
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("操作部门，必须是确认且已启用的状态，导入失败！");
                                        errMsgList.add(errMsg);
                                    } else {
                                        department = conManufactureDepartment.getCode();
                                    }
                                }
                            } catch (Exception e) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(departmentName + "操作部门配置存在重复，请先检查该操作部门，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                    /*
                     * 道序备注
                     */
                    remark = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                    if (remark != null && remark.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("备注长度不能大于600位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    if (CollectionUtil.isEmpty(errMsgList)) {
                        //验证系统中是否存在主表了
                        String main = null;
                        if (plantSid != null && productPriceType != null && productSid != null && completeType != null && department != null) {
                            main = plantSid.toString() + productPriceType + productSid.toString() + completeType + department;
                            //用来获取表格第一次出现的 工厂和商品工价类型和商品编码
                            if (mainMap.get(main) == null) {
                                mainMap.put(main, String.valueOf(num));
                                verifyList = payProductProcessStepMapper.selectPayProductProcessStepList(new PayProductProcessStep().setDepartment(department)
                                        .setProductSid(productSid).setPlantSid(plantSid).setProductPriceType(productPriceType).setJixinWangongType(completeType));
                                if (CollectionUtil.isNotEmpty(verifyList)) {
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("系统中，工厂'" + plantShortName + "'，已创建商品编码'" + productCode + "'，商品工价类型'" + productPriceTypeName +
                                            "'，计薪完工类型'" + completeTypeName + "'，操作部门'" + departmentName +
                                            "'的商品道序，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                //商品道序
                                payProductProcessStep.setPlantSid(plantSid);
                                payProductProcessStep.setProductSid(productSid);
                                payProductProcessStep.setProductCode(productCode);
                                payProductProcessStep.setProductPriceType(productPriceType);
                                payProductProcessStep.setJixinWangongType(completeType);
                                payProductProcessStep.setDepartment(department);
                                payProductProcessStep.setPlantCode(plantCode);
                                payProductProcessStep.setLimitPrice(limitPrice);
                                payProductProcessStep.setCurrency(ConstantsFinance.CURRENCY_CNY);
                                payProductProcessStep.setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN);
                                payProductProcessStep.setHandleStatus(ConstantsEms.SAVA_STATUS);
                            }
                        }
                    }
                }
                else {
                    if (i < 5) {
                        //直接跳过到输入数据的那一行
                        continue;
                    }
                    // 商品道序明细
                    PayProductProcessStepItem payProductProcessStepItem = new PayProductProcessStepItem();
                    /*
                     * 道序序号
                     */
                    String sort_s = objects.get(0)==null||objects.get(0)==""?null:objects.get(0).toString();
                    BigDecimal sort = null;
                    if (StrUtil.isBlank(sort_s)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("道序序号，不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        if (!JudgeFormat.isValidDouble(sort_s,4,0)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("道序序号，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (sortMap.get(sort_s) == null) {
                                sortMap.put(sort_s, "1");
                                sort = new BigDecimal(sort_s);
                                if (sort != null && BigDecimal.ZERO.compareTo(sort) >= 0) {
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("道序序号，不能小于等于0，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }
                            else {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("序号存在重复，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                    /*
                     * 道序名称
                     */
                    String processStepName = objects.get(1)==null||objects.get(1)==""?null:objects.get(1).toString();
                    Long processStepSid = null;
                    PayProcessStep payProcessStep = null;
                    if (processStepName == null){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("道序名称，不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        if (stepMap.get(processStepName) != null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("道序名称，不能重复，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            stepMap.put(processStepName, "1");
                            try {
                                payProcessStep = payProcessStepMapper.selectOne(new QueryWrapper<PayProcessStep>()
                                        .lambda().eq(PayProcessStep::getProcessStepName, processStepName));
                                if(payProcessStep==null){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("道序" + processStepName + "不存在，导入失败！");
                                    errMsgList.add(errMsg);
                                }else{
                                    if (!ConstantsEms.ENABLE_STATUS.equals(payProcessStep.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(payProcessStep.getHandleStatus())){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("道序，必须是确认且已启用的状态，导入失败！");
                                        errMsgList.add(errMsg);
                                    }
                                    processStepSid = payProcessStep.getProcessStepSid();
                                    payProductProcessStepItem.setStepCategory(payProcessStep.getStepCategory());
                                    payProductProcessStepItem.setProcessStepCode(payProcessStep.getProcessStepCode());
                                    payProductProcessStepItem.setProcessStepSid(payProcessStep.getProcessStepSid());
                                    payProductProcessStepItem.setStandardPrice(payProcessStep.getStandardPrice());
                                    payProductProcessStepItem.setProcessSid(payProcessStep.getProcessSid());
                                    payProductProcessStepItem.setTaskUnit(payProcessStep.getTaskUnit());
                                    payProductProcessStepItem.setProcessStepName(processStepName);
                                }
                            }catch (Exception e){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(processStepName +"道序存在重复，请先检查该道序，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                    /*
                     * 工价(元)
                     */
                    String price_s = objects.get(2)==null||objects.get(2)==""?null:objects.get(2).toString();
                    BigDecimal price = null;
                    if (price_s == null){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("工价(元)，不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        if (!JudgeFormat.isValidDouble(price_s,5,4)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("工价(元)，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            price = new BigDecimal(price_s);
                            if (price != null && BigDecimal.ZERO.compareTo(price) > 0) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("工价(元)，不能小于0，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                price=price.divide(BigDecimal.ONE,4,BigDecimal.ROUND_HALF_UP);
                            }
                        }
                    }
                    /*
                     * 倍率(道序)
                     */
                    String priceRate_s = objects.get(3)==null||objects.get(3)==""?null:objects.get(3).toString();
                    BigDecimal priceRate = null;
                    if (priceRate_s == null){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("倍率(道序)，不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        if (!JudgeFormat.isValidDouble(priceRate_s,2,3)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("倍率(道序)，数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            priceRate = new BigDecimal(priceRate_s);
                            if (priceRate != null && BigDecimal.ONE.compareTo(priceRate) > 0) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("倍率(道序)，必须大于等于1，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                priceRate=priceRate.divide(BigDecimal.ONE,3,BigDecimal.ROUND_HALF_UP);
                            }
                        }
                    }
                    /*
                     * 是否工序的最后一道道序
                     */
                    String isFinalName = objects.get(4)==null||objects.get(4)==""?null:objects.get(4).toString();
                    String isFinal = null;
                    if (isFinalName != null){
                        if(StrUtil.isBlank(yesNoMaps.get(isFinalName))){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("是否工序的最后一道道序，填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            isFinal=yesNoMaps.get(isFinalName);
                        }
                    }
                    /*
                     * 道序备注
                     */
                    String itemRemark = objects.get(5)==null||objects.get(5)==""?null:objects.get(5).toString();
                    if (itemRemark != null && itemRemark.length() > 600){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("道序备注长度不能大于600位，导入失败！");
                        errMsgList.add(errMsg);
                    }

                    if (payProductProcessStep != null) {
                        payProductProcessStepItem.setPrice(price);
                        payProductProcessStepItem.setPriceRate(priceRate);
                        payProductProcessStepItem.setIsFinal(isFinal);
                        payProductProcessStepItem.setSort(sort);
                        payProductProcessStepItem.setRemark(itemRemark);
                        payProductProcessStepItem.setExportNum(num);
                        productProcessStepItemList.add(payProductProcessStepItem);
                    }
                }
            }
            if (payProductProcessStep != null) {
                payProductProcessStep.setPayProductProcessStepItemList(productProcessStepItemList);
                List<CommonErrMsgResponse> warnList = this.checkPrice(payProductProcessStep).getMsgList();
                if (CollectionUtil.isNotEmpty(warnList)) {
                    warnList = warnList.stream().sorted(Comparator.comparing(CommonErrMsgResponse::getItemNum)).collect(toList());
                    // 把数据返回前端，方便忽略错误时 调接口 写入
                    List<PayProductProcessStep> stepList = new ArrayList<>();
                    stepList.add(payProductProcessStep);
                    // 根据配置校验是要提醒还是报错
                    SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>()
                            .lambda().eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
                    if (CollectionUtil.isEmpty(errMsgList) && settingClient != null && ConstantsEms.S_MESSAGE_DISPLAT_TYPE_TS.equals(settingClient.getNoticeTypeProcessPriceInconsistent())) {
                        return EmsResultEntity.warning(stepList, warnList, null);
                    }
                    else if (settingClient != null && ConstantsEms.S_MESSAGE_DISPLAT_TYPE_BC.equals(settingClient.getNoticeTypeProcessPriceInconsistent())) {
                        errMsgList.addAll(warnList);
                    }
                }
                if (CollUtil.isNotEmpty(errMsgList)) {
                    return EmsResultEntity.error(errMsgList);
                }
                // 计算商品工价小计倍率前/倍率后
                if (CollectionUtil.isNotEmpty(productProcessStepItemList)){
                    BigDecimal blq = productProcessStepItemList.parallelStream().filter(e -> e.getPrice() != null).map(PayProductProcessStepItem::getPrice)
                            .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
                    BigDecimal blh = productProcessStepItemList.stream().map(x->
                            x.getPrice().multiply(x.getPriceRate())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    payProductProcessStep.setTotalPriceBlq(blq.setScale(4, BigDecimal.ROUND_HALF_UP)).setTotalPriceBlh(blh.setScale(4, BigDecimal.ROUND_HALF_UP));
                }
                payProductProcessStepMapper.insert(payProductProcessStep);
                Long sid = payProductProcessStep.getProductProcessStepSid();
                MongodbUtil.insertUserLog(sid, BusinessType.IMPORT.getValue(), TITLE);
                if (CollectionUtil.isNotEmpty(productProcessStepItemList)){
                    productProcessStepItemList.forEach(item->{
                        item.setProductProcessStepSid(sid);
                    });
                    payProductProcessStepItemMapper.inserts(productProcessStepItemList);
                }
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_PRODUCT_PROCESS_STEP)
                        .setDocumentSid(sid);
                sysTodoTask.setTitle("商品道序" + productCode + "当前是保存状态，请及时处理！")
                        .setDocumentCode(productCode)
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            if (CollUtil.isNotEmpty(errMsgList)) {
                return EmsResultEntity.error(errMsgList);
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success("导入成功");
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
