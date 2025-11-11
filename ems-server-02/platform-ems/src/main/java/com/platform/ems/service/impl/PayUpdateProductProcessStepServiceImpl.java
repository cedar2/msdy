package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.StringUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IPayUpdateProductProcessStepItemService;
import com.platform.ems.service.IPayUpdateProductProcessStepService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 商品道序变更-主Service业务层处理
 *
 * @author chenkw
 * @date 2022-11-08
 */
@Service
@SuppressWarnings("all")
public class PayUpdateProductProcessStepServiceImpl extends ServiceImpl<PayUpdateProductProcessStepMapper, PayUpdateProductProcessStep> implements IPayUpdateProductProcessStepService {
    @Autowired
    private PayUpdateProductProcessStepMapper payUpdateProductProcessStepMapper;
    @Autowired
    private IPayUpdateProductProcessStepItemService payUpdateProductProcessStepItemService;
    @Autowired
    private PayUpdateProductProcessStepAttachMapper payUpdateProductProcessStepAttachMapper;
    @Autowired
    private PayProductProcessStepMapper payProductProcessStepMapper;
    @Autowired
    private PayProductProcessStepItemMapper payProductProcessStepItemMapper;
    @Autowired
    private PayProductProcessStepAttachMapper payProductProcessStepAttachMapper;
    @Autowired
    private PayUpdateProductProcessStepItemMapper payUpdateProductProcessStepItemMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private RemoteMenuService remoteMenuService;


    private static final String TITLE = "商品道序变更-主";

    private static final String PRODUCT_PROCESS_STEP_TITLE = "商品道序-主";

    /**
     * 查询商品道序变更-主
     *
     * @param updateProductProcessStepSid 商品道序变更-主ID
     * @return 商品道序变更-主
     */
    @Override
    public PayUpdateProductProcessStep selectPayUpdateProductProcessStepById(Long updateProductProcessStepSid) {
        PayUpdateProductProcessStep payUpdateProductProcessStep = payUpdateProductProcessStepMapper.selectPayUpdateProductProcessStepById(updateProductProcessStepSid);
        if (payUpdateProductProcessStep == null) {
            throw new BaseException("所选单号不存在");
        }
        // 商品图片
        if (StrUtil.isNotBlank(payUpdateProductProcessStep.getPicturePathSecond())) {
            String[] picturePathList = payUpdateProductProcessStep.getPicturePathSecond().split(";");
            payUpdateProductProcessStep.setPicturePathList(picturePathList);
        }
        payUpdateProductProcessStep.setAttachmentList(new ArrayList<>());
        payUpdateProductProcessStep.setUpdateItemList(new ArrayList<>());
        // 明细表
        List<PayUpdateProductProcessStepItem> updateItemList  = payUpdateProductProcessStepItemService.selectPayUpdateProductProcessStepItemListById(updateProductProcessStepSid);
        if (CollectionUtil.isNotEmpty(updateItemList)) {
            updateItemList.forEach(item->{
                if (item.getStepItemSid() == null) {
                    item.setOperateRemark("新增行");
                }
                else {
                    boolean gongjia = false;
                    boolean beilv = false;
                    if (item.getPrice() == null && item.getPriceBgq() != null
                            || item.getPrice() != null && item.getPriceBgq() == null
                            || (item.getPrice() != null && item.getPriceBgq() != null &&
                            item.getPrice().compareTo(item.getPriceBgq()) != 0)) {
                        gongjia = true;
                    }
                    if (item.getPriceRate() == null && item.getPriceRateBgq() != null
                            || item.getPriceRate() != null && item.getPriceRateBgq() == null
                            || (item.getPriceRate() != null && item.getPriceRateBgq() != null &&
                            item.getPriceRate().compareTo(item.getPriceRateBgq()) != 0)) {
                        beilv = true;
                    }
                    if (gongjia == true && beilv == true) {
                        item.setOperateRemark("改工价、改倍率");
                    } else if (gongjia == true) {
                        item.setOperateRemark("改工价");
                    } else if (beilv == true) {
                        item.setOperateRemark("改倍率");
                    }
                }
            });
            payUpdateProductProcessStep.setUpdateItemList(updateItemList);
        }
        // 明细表 被删除
        List<PayUpdateProductProcessStepItem> deleteItemList  = payUpdateProductProcessStepItemService.selectDeleteListById(updateProductProcessStepSid);
        if (CollectionUtil.isNotEmpty(deleteItemList)) {
            payUpdateProductProcessStep.setDeleteItemList(deleteItemList);
        }
        // 附件
        List<PayUpdateProductProcessStepAttach> attachmentList = payUpdateProductProcessStepAttachMapper.selectPayUpdateProductProcessStepAttachList(
                new PayUpdateProductProcessStepAttach().setUpdateProductProcessStepSid(updateProductProcessStepSid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            payUpdateProductProcessStep.setAttachmentList(attachmentList);
        }
        MongodbUtil.find(payUpdateProductProcessStep);
        return payUpdateProductProcessStep;
    }

    /**
     * 查询商品道序变更-主列表
     *
     * @param payUpdateProductProcessStep 商品道序变更-主
     * @return 商品道序变更-主
     */
    @Override
    public List<PayUpdateProductProcessStep> selectPayUpdateProductProcessStepList(PayUpdateProductProcessStep payUpdateProductProcessStep) {
        List<PayUpdateProductProcessStep> list = payUpdateProductProcessStepMapper.selectPayUpdateProductProcessStepList(payUpdateProductProcessStep);
        return list;
    }

    /**
     * 新增商品道序变更-主
     * 需要注意编码重复校验
     *
     * @param payUpdateProductProcessStep 商品道序变更-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPayUpdateProductProcessStep(PayUpdateProductProcessStep payUpdateProductProcessStep) {
        payUpdateProductProcessStep.setCreateDate(new Date());
        payUpdateProductProcessStep.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        payUpdateProductProcessStep.setUpdateDate(null);
        payUpdateProductProcessStep.setUpdaterAccount("");
        payUpdateProductProcessStep.setConfirmDate(null);
        payUpdateProductProcessStep.setConfirmerAccount("");
        payUpdateProductProcessStep.setHandleStatus("1");

        //更新版本号
        List<PayUpdateProductProcessStep> payUpdateList = payUpdateProductProcessStepMapper.selectList(new QueryWrapper<PayUpdateProductProcessStep>()
                .lambda().eq(PayUpdateProductProcessStep::getPlantSid, payUpdateProductProcessStep.getPlantSid())
                .eq(PayUpdateProductProcessStep::getDepartment, payUpdateProductProcessStep.getDepartment())
                .eq(PayUpdateProductProcessStep::getProductPriceType, payUpdateProductProcessStep.getProductPriceType())
                .eq(PayUpdateProductProcessStep::getJixinWangongType, payUpdateProductProcessStep.getJixinWangongType())
                .eq(PayUpdateProductProcessStep::getProductCode, payUpdateProductProcessStep.getProductCode()));
        payUpdateList = payUpdateList.stream().sorted(Comparator.comparing(PayUpdateProductProcessStep::getUpdateVersionId, Comparator.nullsFirst(Long::compareTo)).reversed())
                .collect(Collectors.toList());
        if (payUpdateList.size() > 0 && payUpdateList.get(0).getUpdateVersionId() != null) {
            payUpdateProductProcessStep.setUpdateVersionId(payUpdateList.get(0).getUpdateVersionId() + 1);
        }
        else {
            payUpdateProductProcessStep.setUpdateVersionId(1L);
        }

        int row = payUpdateProductProcessStepMapper.insert(payUpdateProductProcessStep);
        if (row > 0) {
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(payUpdateProductProcessStep.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_PRODUCT_PROCESS_STEP_UPDATE)
                        .setDocumentSid(payUpdateProductProcessStep.getUpdateProductProcessStepSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    String code = getCode(payUpdateProductProcessStep);
                    // 获取菜单id
                    SysMenu menu = new SysMenu();
                    menu.setMenuName(ConstantsEms.TODO_UP_PRO_STEP_INFO_MENU_NAME);
                    menu = remoteMenuService.getInfoByName(menu).getData();
                    if (menu != null && menu.getMenuId() != null) {
                        sysTodoTask.setMenuId(menu.getMenuId());
                    }
                    sysTodoTask.setTitle("商品道序变更" + code + "当前是保存状态，请及时处理！")
                            .setDocumentCode(code)
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(payUpdateProductProcessStep);
            }

            // 写入明细
            payUpdateProductProcessStep.setUpdateItemList(payUpdateProductProcessStep.getPayProductProcessStepItemList());

            if (CollectionUtil.isNotEmpty(payUpdateProductProcessStep.getUpdateItemList())) {
                payUpdateProductProcessStepItemService.insertPayUpdateProductProcessStepItemList(payUpdateProductProcessStep);
            }
            // 写入附件
            if (CollectionUtil.isNotEmpty(payUpdateProductProcessStep.getAttachmentList())) {
                payUpdateProductProcessStep.getAttachmentList().forEach(item->{
                    item.setUpdateProductProcessStepSid(payUpdateProductProcessStep.getUpdateProductProcessStepSid());
                });
                payUpdateProductProcessStepAttachMapper.inserts(payUpdateProductProcessStep.getAttachmentList());
            }

            //更新原数据的状态位和是否变更栏位
            PayProductProcessStep payProductProcessStepArg = new PayProductProcessStep();
            payProductProcessStepArg.setProductProcessStepSid(payUpdateProductProcessStep.getProductProcessStepSid());
            payProductProcessStepArg.setHandleStatus("5");
            payProductProcessStepArg.setIsUpdate("Y");
            payProductProcessStepMapper.updateAllBysId(payProductProcessStepArg);
            //插入日志
            List<OperMsg> msgList1 = new ArrayList<>();
            msgList1 = BeanUtils.eq(new PayProductProcessStep(), payProductProcessStepArg);
            MongodbDeal.update(payProductProcessStepArg.getProductProcessStepSid(), payProductProcessStepArg.getHandleStatus(), payProductProcessStepArg.getHandleStatus(), msgList1, PRODUCT_PROCESS_STEP_TITLE, null);

            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new PayUpdateProductProcessStep(), payUpdateProductProcessStep);
            MongodbDeal.insert(payUpdateProductProcessStep.getUpdateProductProcessStepSid(), payUpdateProductProcessStep.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    public String getCode(PayUpdateProductProcessStep payUpdateProductProcessStep){
        String code = "";
        if (payUpdateProductProcessStep.getProductCode()==null){
            if (payUpdateProductProcessStep.getSampleCodeSelf() != null){
                code = payUpdateProductProcessStep.getSampleCodeSelf();
            }
        }else {
            code = payUpdateProductProcessStep.getProductCode();
        }
        return code;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(PayUpdateProductProcessStep payUpdateProductProcessStep) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, payUpdateProductProcessStep.getUpdateProductProcessStepSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, payUpdateProductProcessStep.getUpdateProductProcessStepSid()));
        }
    }

    /**
     * 【优化】【商品道序】同一道序，在不同商品道序中，若工价不一致，给予提醒
     * @param payProductProcessStep 商品道序-主
     * @return 结果
     */
    @Override
    public EmsResultEntity checkPrice(PayUpdateProductProcessStep payUpdateProductProcessStep) {
        List<PayUpdateProductProcessStepItem> itemList = payUpdateProductProcessStep.getUpdateItemList();
        itemList = itemList.stream().sorted(Comparator.comparing(PayUpdateProductProcessStepItem::getSort, Comparator.nullsLast(BigDecimal::compareTo))).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(payUpdateProductProcessStep.getUpdateItemList())) {
            return EmsResultEntity.success();
        }
        List<Long> processStepSids = itemList.stream()
                .map(PayUpdateProductProcessStepItem::getProcessStepSid).collect(Collectors.toList());
        List<Long> stepItemSids = itemList.stream().filter(o -> o.getStepItemSid() != null)
                .map(PayUpdateProductProcessStepItem::getStepItemSid).collect(Collectors.toList());
        // 找出所有符合 工厂 + 道序sid 在道序明细表中的 商品道序明细
        List<PayUpdateProductProcessStepItem> tableList = payUpdateProductProcessStepItemMapper.selectPayUpdateProductProcessStepItemList(new PayUpdateProductProcessStepItem()
                .setPlantSid(payUpdateProductProcessStep.getPlantSid()).setProcessStepSids(processStepSids));
        // 变更编辑页面 去除自己的 数据 判断
        if (CollectionUtil.isNotEmpty(stepItemSids)) {
            tableList = tableList.stream().filter(o-> !stepItemSids.contains(o.getStepItemSid())).collect(toList());
        }
        Map<Long, List<PayUpdateProductProcessStepItem>> map = tableList.stream().collect(Collectors.groupingBy(o -> o.getProcessStepSid()));
        // 存放 异常信息
        List<CommonErrMsgResponse> msgList = new ArrayList<>();
        itemList.forEach(item->{
            if (item.getPrice() == null) {
                return;
            }
            List<PayUpdateProductProcessStepItem> compare = map.get(item.getProcessStepSid());
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
        });
        if (CollectionUtil.isNotEmpty(msgList)) {
            return EmsResultEntity.warning(msgList);
        }
        return EmsResultEntity.success();
    }

    /**
     *
     * @param payUpdateProductProcessStep
     * @return
     */
    @Override
    public EmsResultEntity verifyPrice(PayUpdateProductProcessStep payUpdateProductProcessStep) {
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>(); // 报错信息
        List<CommonErrMsgResponse> warnList = new ArrayList<>();  // 可忽略信息
        // 得到 此时的 商品编码 或者 我司样衣号
        String code = getCode(payUpdateProductProcessStep);
        // 变更页面点确认的校验
        PayProductProcessStep processStep = new PayProductProcessStep();
        List<PayUpdateProductProcessStepItem> itemList = payUpdateProductProcessStep.getPayProductProcessStepItemList();
        if (CollectionUtil.isNotEmpty(itemList)) {
            //明细行工价之和 (倍率后)
            BigDecimal priceSumAfter = priceSumAfter(itemList);
            if (priceSumAfter.compareTo(payUpdateProductProcessStep.getLimitPrice()) > 0) {
                processStep.setVerify(ConstantsEms.YES);
                CommonErrMsgResponse msg = new CommonErrMsgResponse();
                msg.setMsg("商品编码/我司样衣号" + code + "，道序工价小计(倍率后)(变更后)，高于“商品工价上限(倍率后)(变更后)”");
                errMsgList.add(msg);
                return EmsResultEntity.error(processStep, errMsgList, null);
            } else {
                processStep.setVerify(ConstantsEms.NO);
                EmsResultEntity priceResult = this.checkPrice(payUpdateProductProcessStep);
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
        Long[] updateProductProcessStepSidList = payUpdateProductProcessStep.getUpdateProductProcessStepSidList();
        if (ArrayUtil.isNotEmpty(updateProductProcessStepSidList)) {
            List<String> productCodeList = new ArrayList<>();
            List<String> codeList = new ArrayList<>();
            PayUpdateProductProcessStep info = new PayUpdateProductProcessStep();
            for (Long updateProductProcessStepSid : updateProductProcessStepSidList) {
                info = selectPayUpdateProductProcessStepById(updateProductProcessStepSid);
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
                msg.setMsg("商品编码/我司样衣号" + productCodeList.toString() + "，道序工价小计(倍率后)(变更后)，高于“商品工价上限(倍率后)(变更后)”");
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
    private BigDecimal priceSumAfter(List<PayUpdateProductProcessStepItem> itemList) {
        BigDecimal sum = BigDecimal.ZERO;
        if (CollectionUtil.isNotEmpty(itemList)) {
            BigDecimal totalPriceAfter = BigDecimal.ZERO;
            for (PayUpdateProductProcessStepItem stepItem : itemList) {
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
     * 修改商品道序变更-主
     *
     * @param payUpdateProductProcessStep 商品道序变更-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayUpdateProductProcessStep(PayUpdateProductProcessStep payUpdateProductProcessStep) {
        PayUpdateProductProcessStep original = payUpdateProductProcessStepMapper.selectPayUpdateProductProcessStepById(payUpdateProductProcessStep.getUpdateProductProcessStepSid());
        int row = payUpdateProductProcessStepMapper.updateById(payUpdateProductProcessStep);
        if (row > 0) {
            // 修改明细
            payUpdateProductProcessStepItemService.updatePayUpdateProductProcessStepItemList(payUpdateProductProcessStep);
            // 修改附件
            this.updatePayUpdateProductProcessStepAttach(payUpdateProductProcessStep);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, payUpdateProductProcessStep);
            MongodbDeal.update(payUpdateProductProcessStep.getUpdateProductProcessStepSid(), original.getHandleStatus(), payUpdateProductProcessStep.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 商品道序变更提交更新
     *
     * @param payUpdateProductProcessStep 商品道序变更提交更新
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(PayUpdateProductProcessStep payUpdateProductProcessStep) {
        PayUpdateProductProcessStep original = payUpdateProductProcessStepMapper.selectPayUpdateProductProcessStepById(payUpdateProductProcessStep.getUpdateProductProcessStepSid());
        payUpdateProductProcessStep.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
//        payUpdateProductProcessStep.setHandleStatus("3");
        if("5".equals(payUpdateProductProcessStep.getHandleStatus())){
            payUpdateProductProcessStep.setConfirmDate(new Date());
            payUpdateProductProcessStep.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = payUpdateProductProcessStepMapper.updateById(payUpdateProductProcessStep);
        if (row > 0) {
            //更新原数据的状态位和是否变更栏位
            PayProductProcessStep payProductProcessStepArg = new PayProductProcessStep();

            payProductProcessStepArg.setProductProcessStepSid(payUpdateProductProcessStep.getProductProcessStepSid());
            if ("3".equals(payUpdateProductProcessStep.getHandleStatus())){     //提交时
                payProductProcessStepArg.setHandleStatus("2");
                payProductProcessStepArg.setIsUpdate("Y");
            }else if("1".equals(payUpdateProductProcessStep.getHandleStatus())){    //驳回到起点时
                payProductProcessStepArg.setHandleStatus("5");
            }else if("4".equals(payUpdateProductProcessStep.getHandleStatus())){    //已退回
                payProductProcessStepArg.setHandleStatus("5");
                payProductProcessStepArg.setIsUpdate("Y");
            }else if("5".equals(payUpdateProductProcessStep.getHandleStatus())){    //已审批
                payProductProcessStepArg.setHandleStatus("5");
                payProductProcessStepArg.setIsUpdate("N");

                //覆盖数据
                //商品道序主表
                PayProductProcessStep payProductProcessStep = payProductProcessStepMapper.selectPayProductProcessStepById
                        (payUpdateProductProcessStep.getProductProcessStepSid());
                PayProductProcessStep payProductProcessStepNew = new PayProductProcessStep();
                BeanUtils.copyProperties(payUpdateProductProcessStep, payProductProcessStepNew, new String[]{"createDate", "creatorAccount"});
                payProductProcessStepMapper.updateById(payProductProcessStepNew);

                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(payProductProcessStep, payProductProcessStepNew);
                MongodbDeal.check(payProductProcessStepNew.getProductProcessStepSid(), payProductProcessStepNew.getHandleStatus(), msgList, PRODUCT_PROCESS_STEP_TITLE, null);

                // 附件清单
                List<PayUpdateProductProcessStepAttach> updateAttachList = payUpdateProductProcessStepAttachMapper
                        .selectList(new QueryWrapper<PayUpdateProductProcessStepAttach>().lambda()
                                .eq(PayUpdateProductProcessStepAttach::getUpdateProductProcessStepSid, payUpdateProductProcessStep.getUpdateProductProcessStepSid()));
                // 删除原
                payProductProcessStepAttachMapper.delete(new QueryWrapper<PayProductProcessStepAttach>().lambda()
                        .eq(PayProductProcessStepAttach::getProductProcessStepSid, payUpdateProductProcessStep.getProductProcessStepSid()));
                // 覆盖附件
                if (CollectionUtil.isNotEmpty(updateAttachList)) {
                    List<PayProductProcessStepAttach> attaches = BeanCopyUtils.copyListProperties(updateAttachList, PayProductProcessStepAttach::new);
                    attaches.forEach(item->{
                        item.setAttachmentSid(null).setProductProcessStepSid(payUpdateProductProcessStep.getProductProcessStepSid());
                    });
                    payProductProcessStepAttachMapper.inserts(attaches);
                }

                //商品道序明细表
//                payProductProcessStepItemMapper.deleteByProcessStepSid(payUpdateProductProcessStep.getProductProcessStepSid());
                //获取明细
                List<PayUpdateProductProcessStepItem> payUpdateItemList = payUpdateProductProcessStepItemMapper.selectList(new QueryWrapper<PayUpdateProductProcessStepItem>()
                        .lambda().eq(PayUpdateProductProcessStepItem::getUpdateProductProcessStepSid, payUpdateProductProcessStep.getUpdateProductProcessStepSid()));
                List<PayProductProcessStepItem> payItemListAdd = new ArrayList<PayProductProcessStepItem>();
                List<PayProductProcessStepItem> payItemListUpd = new ArrayList<PayProductProcessStepItem>();
                List<Long> productProcessStepSidsDel = new ArrayList<Long>();
                payUpdateItemList.forEach(payUpdateItem ->{
                    PayProductProcessStepItem payProductVO = new PayProductProcessStepItem();
                    if (!"Y".equals(payUpdateItem.getDelFlagBiangz())){  //删除的数据不回写
                        if (StringUtils.isNotNull(payUpdateItem.getStepItemSid())){     //修改
                            BeanUtils.copyProperties(payUpdateItem, payProductVO, new String[]{"createDate", "creatorAccount"});
                            payProductVO.setProductProcessStepSid(payProductProcessStep.getProductProcessStepSid());

                            payItemListUpd.add(payProductVO);
                        }else{      //新增
                            BeanUtils.copyProperties(payUpdateItem, payProductVO);
                            payProductVO.setProductProcessStepSid(payProductProcessStep.getProductProcessStepSid());

                            payItemListAdd.add(payProductVO);
                        }
                    }else{
                        productProcessStepSidsDel.add(payUpdateItem.getStepItemSid());
                    }
                });
                if (CollectionUtil.isNotEmpty(payItemListAdd)) {     //新增
                    payProductProcessStepItemMapper.inserts(payItemListAdd);
                }
                if (CollectionUtil.isNotEmpty(payItemListUpd)) {    //修改
                    for (PayProductProcessStepItem payUpd : payItemListUpd) {
                        payProductProcessStepItemMapper.updateAllById(payUpd);
                    }
                }
                if (productProcessStepSidsDel.size() > 0) {    //删除
                    payProductProcessStepItemMapper.delete(new UpdateWrapper<PayProductProcessStepItem>().lambda()
                            .in(PayProductProcessStepItem::getStepItemSid, productProcessStepSidsDel));
                }
            }

            payProductProcessStepMapper.updateAllBysId(payProductProcessStepArg);

        }
        return row;
    }


    /**
     * 变更商品道序变更-主
     *
     * @param payUpdateProductProcessStep 商品道序变更-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePayUpdateProductProcessStep(PayUpdateProductProcessStep payUpdateProductProcessStep) {
        PayUpdateProductProcessStep response = payUpdateProductProcessStepMapper.selectPayUpdateProductProcessStepById(payUpdateProductProcessStep.getUpdateProductProcessStepSid());
        int row = payUpdateProductProcessStepMapper.updateAllById(payUpdateProductProcessStep);
        if (row > 0) {
            // 修改明细
            payUpdateProductProcessStepItemService.updatePayUpdateProductProcessStepItemList(payUpdateProductProcessStep);
            // 修改附件
            this.updatePayUpdateProductProcessStepAttach(payUpdateProductProcessStep);
            //插入日志
            MongodbUtil.insertUserLog(payUpdateProductProcessStep.getUpdateProductProcessStepSid(), BusinessType.CHANGE.getValue(), response, payUpdateProductProcessStep, TITLE);
        }
        return row;
    }

    /**
     * 批量修改附件信息
     *
     * @param payUpdateProductProcessStep 商品道序变更
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePayUpdateProductProcessStepAttach(PayUpdateProductProcessStep payUpdateProductProcessStep) {
        // 先删后加
        payUpdateProductProcessStepAttachMapper.delete(new QueryWrapper<PayUpdateProductProcessStepAttach>().lambda()
                .eq(PayUpdateProductProcessStepAttach::getUpdateProductProcessStepSid, payUpdateProductProcessStep.getUpdateProductProcessStepSid()));
        if (CollectionUtil.isNotEmpty(payUpdateProductProcessStep.getAttachmentList())) {
            payUpdateProductProcessStep.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getAttachmentSid() == null) {
                    att.setUpdateProductProcessStepSid(payUpdateProductProcessStep.getUpdateProductProcessStepSid());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            payUpdateProductProcessStepAttachMapper.inserts(payUpdateProductProcessStep.getAttachmentList());
        }
    }

    /**
     * 批量删除商品道序变更-主
     *
     * @param updateProductProcessStepSids 需要删除的商品道序变更-主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayUpdateProductProcessStepByIds(List<Long> updateProductProcessStepSids) {
        List<PayUpdateProductProcessStep> list = payUpdateProductProcessStepMapper.selectList(new QueryWrapper<PayUpdateProductProcessStep>()
                .lambda().in(PayUpdateProductProcessStep::getUpdateProductProcessStepSid, updateProductProcessStepSids));
        Long productProcessStepSid = list.get(0).getProductProcessStepSid();
        list = list.stream().filter(o-> !HandleStatus.SAVE.getCode().equals(o.getHandleStatus()) && !HandleStatus.RETURNED.getCode().equals(o.getHandleStatus()))
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BaseException("只有保存或者已退回状态才允许删除！");
        }
        int row = payUpdateProductProcessStepMapper.deleteBatchIds(updateProductProcessStepSids);
        if (row > 0) {
            // 删除明细
            payUpdateProductProcessStepItemService.deletePayUpdateProductProcessStepItemByStep(updateProductProcessStepSids);
            // 删除附件
            payUpdateProductProcessStepAttachMapper.delete(new QueryWrapper<PayUpdateProductProcessStepAttach>().lambda()
                    .in(PayUpdateProductProcessStepAttach::getUpdateProductProcessStepSid, updateProductProcessStepSids));
            // 商品道序表更新
            PayProductProcessStep payProductProcessStepArg = new PayProductProcessStep();
            payProductProcessStepArg.setProductProcessStepSid(productProcessStepSid);
            payProductProcessStepArg.setHandleStatus("5");
            payProductProcessStepArg.setIsUpdate("N");
            payProductProcessStepMapper.updateAllBysId(payProductProcessStepArg);

            PayUpdateProductProcessStep payupdProductProcessStep = new PayUpdateProductProcessStep();
            updateProductProcessStepSids.forEach(updateproductProcessStepSid -> {
                payupdProductProcessStep.setUpdateProductProcessStepSid(updateproductProcessStepSid);
                //校验是否存在待办
                checkTodoExist(payupdProductProcessStep);
            });

            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new PayUpdateProductProcessStep());
                MongodbUtil.insertUserLog(o.getUpdateProductProcessStepSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
            // 商品道序的操作日志
            MongodbUtil.insertUserLog(payProductProcessStepArg.getProductProcessStepSid(), BusinessType.CANCEL_CHANGE.getValue(), null, PRODUCT_PROCESS_STEP_TITLE);
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param payUpdateProductProcessStep
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(PayUpdateProductProcessStep payUpdateProductProcessStep) {
        int row = 0;
        Long[] sids = payUpdateProductProcessStep.getUpdateProductProcessStepSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<PayUpdateProductProcessStep> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(PayUpdateProductProcessStep::getUpdateProductProcessStepSid, sids);
            updateWrapper.set(PayUpdateProductProcessStep::getHandleStatus, payUpdateProductProcessStep.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(payUpdateProductProcessStep.getHandleStatus())) {
                updateWrapper.set(PayUpdateProductProcessStep::getConfirmDate, new Date());
                updateWrapper.set(PayUpdateProductProcessStep::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = payUpdateProductProcessStepMapper.update(null, updateWrapper);
            if (row > 0) {
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, payUpdateProductProcessStep.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

}
