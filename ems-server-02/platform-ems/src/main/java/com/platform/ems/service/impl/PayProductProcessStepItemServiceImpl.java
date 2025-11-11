package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsProcess;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IPayProductProcessStepItemService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 商品道序-明细Service业务层处理
 *
 * @author c
 * @date 2021-09-08
 */
@Service
@SuppressWarnings("all")
public class PayProductProcessStepItemServiceImpl extends ServiceImpl<PayProductProcessStepItemMapper, PayProductProcessStepItem> implements IPayProductProcessStepItemService {
    @Autowired
    private PayProductProcessStepItemMapper payProductProcessStepItemMapper;
    @Autowired
    private PayProcessStepCompleteItemMapper payProcessStepCompleteItemMapper;
    @Autowired
    private PayProcessStepCompleteMapper payProcessStepCompleteMapper;
    @Autowired
    private ManManufactureOrderProcessMapper manManufactureOrderProcessMapper;
    @Autowired
    private ManDayManufactureProgressItemMapper manDayManufactureProgressItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "商品道序-明细";

    /**
     * 查询商品道序-明细
     *
     * @param stepItemSid 商品道序-明细ID
     * @return 商品道序-明细
     */
    @Override
    public PayProductProcessStepItem selectPayProductProcessStepItemById(Long stepItemSid) {
        PayProductProcessStepItem payProductProcessStepItem = payProductProcessStepItemMapper.selectPayProductProcessStepItemById(stepItemSid);
        MongodbUtil.find(payProductProcessStepItem);
        return payProductProcessStepItem;
    }

    /**
     * 查询商品道序-明细列表
     *
     * @param payProductProcessStepItem 商品道序-明细
     * @return 商品道序-明细
     */
    @Override
    public List<PayProductProcessStepItem> selectPayProductProcessStepItemList(PayProductProcessStepItem payProductProcessStepItem) {
        return payProductProcessStepItemMapper.selectPayProductProcessStepItemList(payProductProcessStepItem);
    }

    /**
     * 查询商品道序-明细报表
     *
     * @param payProductProcessStepItem 商品道序-明细
     * @return 商品道序-明细
     */
    @Override
    public List<PayProductProcessStepItem> selectPayProductProcessStepItemForm(PayProductProcessStepItem payProductProcessStepItem) {
        return payProductProcessStepItemMapper.selectPayProductProcessStepItemForm(payProductProcessStepItem);
    }

    /**
     * 查询商品道序-明细   (主要用于计薪量明细查询的接口)
     *
     * @param payProductProcessStepItem 商品道序-明细
     * @return 商品道序-明细
     */
    @Override
    public List<PayProductProcessStepItem> selectPayProductProcessStepItem(PayProductProcessStepItem payProductProcessStepItem) {
        return payProductProcessStepItemMapper.selectPayProductProcessStepItem(payProductProcessStepItem);
    }

    /**
     * 新增商品道序-明细
     * 需要注意编码重复校验
     *
     * @param payProductProcessStepItem 商品道序-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPayProductProcessStepItem(PayProductProcessStepItem payProductProcessStepItem) {
        int row = payProductProcessStepItemMapper.insert(payProductProcessStepItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(payProductProcessStepItem.getStepItemSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改商品道序-明细
     *
     * @param payProductProcessStepItem 商品道序-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayProductProcessStepItem(PayProductProcessStepItem payProductProcessStepItem) {
        PayProductProcessStepItem response = payProductProcessStepItemMapper.selectPayProductProcessStepItemById(payProductProcessStepItem.getStepItemSid());
        int row = payProductProcessStepItemMapper.updateById(payProductProcessStepItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payProductProcessStepItem.getStepItemSid(), BusinessType.UPDATE.ordinal(), response, payProductProcessStepItem, TITLE);
        }
        return row;
    }

    /**
     * 变更商品道序-明细
     *
     * @param payProductProcessStepItem 商品道序-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePayProductProcessStepItem(PayProductProcessStepItem payProductProcessStepItem) {
        PayProductProcessStepItem response = payProductProcessStepItemMapper.selectPayProductProcessStepItemById(payProductProcessStepItem.getStepItemSid());
        int row = payProductProcessStepItemMapper.updateAllById(payProductProcessStepItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payProductProcessStepItem.getStepItemSid(), BusinessType.CHANGE.ordinal(), response, payProductProcessStepItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除商品道序-明细
     *
     * @param stepItemSids 需要删除的商品道序-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayProductProcessStepItemByIds(List<Long> stepItemSids) {
        return payProductProcessStepItemMapper.deleteBatchIds(stepItemSids);
    }

    @Override
    public List<PayProductProcessStepItem> getManOrderItemList(PayProductProcessStepItem payProductProcessStepItem) {
        List<PayProductProcessStepItem> itemList = payProductProcessStepItemMapper.getManOrderItemList(payProductProcessStepItem);
        if (CollectionUtil.isNotEmpty(itemList)){
            if (ConstantsEms.JXCG.equals(payProductProcessStepItem.getJixinWangongType())){
                String quantity = "";
                for (PayProductProcessStepItem item : itemList) {
                    quantity = manDayManufactureProgressItemMapper.getQuantity(new ManDayManufactureProgressItem()
                            .setYearmonth(payProductProcessStepItem.getYearmonth()).setManufactureOrderSid(Long.parseLong(item.getManufactureOrderSid()))
                            .setProductSid(item.getProductSid()).setProcessSid(item.getProcessSid())
                            .setPlantSid(payProductProcessStepItem.getPlantSid()).setWorkCenterSid(Long.parseLong(payProductProcessStepItem.getWorkCenterSid())));
                    item.setCompleteQuantitySys(new BigDecimal(quantity)).setCompleteQuantity(new BigDecimal(quantity));

                    // 完成量校验参考工序 , 参考工序所引用数量类型  根据“生产订单号+工厂(工序)+班组+工序”获取“生产订单工序明细表”中的值
                    List<ManManufactureOrderProcess> processList = manManufactureOrderProcessMapper.selectManManufactureOrderProcessList(new ManManufactureOrderProcess()
                            .setManufactureOrderSid(Long.parseLong(item.getManufactureOrderSid())).setProcessSid(item.getProcessSid())
                            .setPlantSid(payProductProcessStepItem.getPlantSid()).setWorkCenterSid(Long.parseLong(payProductProcessStepItem.getWorkCenterSid())));
                    if (CollectionUtil.isNotEmpty(processList)){
                        // 完成量校验参考工序 , 参考工序所引用数量类型
                        item.setQuantityReferProcessSid(processList.get(0).getQuantityReferProcessSid());
                        item.setQuantityReferProcessCode(processList.get(0).getQuantityReferProcessCode());
                        item.setQuantityReferProcessName(processList.get(0).getQuantityReferProcessName());
                        item.setQuantityTypeReferProcess(processList.get(0).getQuantityTypeReferProcess());
                        // 参考工序校验量
                        List<ManDayManufactureProgressItem> dayList = manDayManufactureProgressItemMapper.selectManDayManufactureProgressItemList(
                                new ManDayManufactureProgressItem().setManufactureOrderSid(Long.parseLong(item.getManufactureOrderSid()))
                                        .setMaterialSid(item.getMaterialSid()).setProcessSid(item.getQuantityReferProcessSid())
                                        .setCompleteType(payProductProcessStepItem.getJixinWangongType()));
                        BigDecimal quantityReferProcess = BigDecimal.ZERO;
                        // 取值逻辑：根据“完成量校验参考工序”、“参考工序所引用数量类型”，获取对应数量类型的值；例如：”车缝“的”完成量校验参考工序“为”裁床“，
                        // “参考工序所引用数量类型”为”完成量“，则参考工序校验量，为”裁床工序的完成量“（从生产进度日报明细表中汇总，
                        // 根据“生产订单号+商品编码+工序+计薪完工类型”获取所有符合条件的生产进度日报明细行，将“当天完成量/收料量”累加得出）
                        if (CollectionUtil.isNotEmpty(dayList) && ConstantsProcess.QUANTITY_TYPE_REFER_PROCESS_WC.equals(item.getQuantityTypeReferProcess())){
                            quantityReferProcess = dayList.parallelStream().filter(o->o.getQuantity() != null)
                                    .map(ManDayManufactureProgressItem::getQuantity).reduce(BigDecimal.ZERO,BigDecimal::add);
                        }
                        if (CollectionUtil.isNotEmpty(dayList) && ConstantsProcess.QUANTITY_TYPE_REFER_PROCESS_JS.equals(item.getQuantityTypeReferProcess())){
                            quantityReferProcess = dayList.parallelStream().filter(o->o.getJieshouQuantity() != null)
                                    .map(ManDayManufactureProgressItem::getJieshouQuantity).reduce(BigDecimal.ZERO,BigDecimal::add);
                        }
                        if (CollectionUtil.isNotEmpty(dayList) && ConstantsProcess.QUANTITY_TYPE_REFER_PROCESS_FL.equals(item.getQuantityTypeReferProcess())){
                            quantityReferProcess = dayList.parallelStream().filter(o->o.getIssueQuantity() != null)
                                    .map(ManDayManufactureProgressItem::getIssueQuantity).reduce(BigDecimal.ZERO,BigDecimal::add);
                        }
                        item.setQuantityReferProcess(quantityReferProcess.toString());
                    }

                    // 已计薪量  根据“生产订单号+商品编码+工序+计薪完工类型”获取“已确认”的计薪量申报单的明细行的“计薪量”并进行累加
                    List<PayProcessStepCompleteItem> stepCompleteItemList = payProcessStepCompleteItemMapper.selectPayProcessStepCompleteItemList(new
                            PayProcessStepCompleteItem().setManufactureOrderSid(Long.parseLong(item.getManufactureOrderSid()))
                            .setProductSid(item.getMaterialSid()).setProcessSid(item.getProcessSid()).setJixinWangongType(payProductProcessStepItem.getJixinWangongType())
                            .setHandleStatus(ConstantsEms.CHECK_STATUS));
                    if (CollectionUtil.isNotEmpty(stepCompleteItemList)){
                        BigDecimal cumulativeQuantity = BigDecimal.ZERO;
                        cumulativeQuantity = stepCompleteItemList.parallelStream().filter(o->o.getCompleteQuantity() != null)
                                .map(PayProcessStepCompleteItem::getCompleteQuantity).reduce(BigDecimal.ZERO,BigDecimal::add);
                        item.setCumulativeQuantity(cumulativeQuantity);
                    }
                }
            }
        }
        return itemList;
    }

    /**
     * 校验明细是否可删除
     */
    @Override
    public int verifyItem(Long[] stepItemSids) {
        //计薪量申报单明细
        for (Long stepItemSid : stepItemSids) {
            List<PayProcessStepCompleteItem> itemList = payProcessStepCompleteItemMapper.selectPayProcessStepCompleteItemList(new PayProcessStepCompleteItem().setProcessStepItemSid(stepItemSid));
            if (CollectionUtil.isNotEmpty(itemList)) {
                throw new BaseException("道序编码" + itemList.get(0).getProcessStepCode() + "的道序已被计薪量申报单" + itemList.get(0).getStepCompleteCode() + "引用，无法删除！");
            }
        }
        return stepItemSids.length;
    }
}
