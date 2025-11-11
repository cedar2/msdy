package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.PayProcessStepCompleteTableRequest;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.domain.dto.response.export.PaySalaryWageExportReport;
import com.platform.ems.domain.dto.response.form.PaySalaryWageFormResponse;
import com.platform.ems.domain.dto.response.form.ProductProcessCompleteStatisticsSalary;
import com.platform.ems.mapper.BasStaffMapper;
import com.platform.ems.mapper.PayProcessStepCompleteItemMapper;
import com.platform.ems.mapper.PayProductJijianSettleInforMapper;
import com.platform.ems.mapper.PayProductProcessStepItemMapper;
import com.platform.ems.service.IPayProcessStepCompleteItemService;
import com.platform.ems.util.ExcelStyleUtil;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.api.service.RemoteSystemService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 计薪量申报-明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-08
 */
@Service
@SuppressWarnings("all")
public class PayProcessStepCompleteItemServiceImpl extends ServiceImpl<PayProcessStepCompleteItemMapper, PayProcessStepCompleteItem> implements IPayProcessStepCompleteItemService {
    @Autowired
    private PayProcessStepCompleteItemMapper payProcessStepCompleteItemMapper;
    @Autowired
    private PayProductProcessStepItemMapper payProductProcessStepItemMapper;
    @Autowired
    private PayProductJijianSettleInforMapper payProductJijianSettleInforMapper;
    @Autowired
    private RemoteSystemService remoteSystemService;
    @Autowired
    private BasStaffMapper basStaffMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "计薪量申报-明细";

    /**
     * 查询计薪量申报-明细
     *
     * @param stepCompleteItemSid 计薪量申报-明细ID
     * @return 计薪量申报-明细
     */
    @Override
    public PayProcessStepCompleteItem selectPayProcessStepCompleteItemById(Long stepCompleteItemSid) {
        PayProcessStepCompleteItem payProcessStepCompleteItem = payProcessStepCompleteItemMapper.selectPayProcessStepCompleteItemById(stepCompleteItemSid);
        MongodbUtil.find(payProcessStepCompleteItem);
        return payProcessStepCompleteItem;
    }

    /**
     * 查询计薪量申报-明细列表
     *
     * @param payProcessStepCompleteItem 计薪量申报-明细
     * @return 计薪量申报-明细
     */
    @Override
    public List<PayProcessStepCompleteItem> selectPayProcessStepCompleteItemList(PayProcessStepCompleteItem payProcessStepCompleteItem) {
        return payProcessStepCompleteItemMapper.selectPayProcessStepCompleteItemList(payProcessStepCompleteItem);
    }

    /**
     * 新增计薪量申报-明细
     * 需要注意编码重复校验
     *
     * @param payProcessStepCompleteItem 计薪量申报-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPayProcessStepCompleteItem(PayProcessStepCompleteItem payProcessStepCompleteItem) {
        int row = payProcessStepCompleteItemMapper.insert(payProcessStepCompleteItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(payProcessStepCompleteItem.getStepCompleteItemSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改计薪量申报-明细
     *
     * @param payProcessStepCompleteItem 计薪量申报-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePayProcessStepCompleteItem(PayProcessStepCompleteItem payProcessStepCompleteItem) {
        PayProcessStepCompleteItem response = payProcessStepCompleteItemMapper.selectPayProcessStepCompleteItemById(payProcessStepCompleteItem.getStepCompleteItemSid());
        int row = payProcessStepCompleteItemMapper.updateById(payProcessStepCompleteItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payProcessStepCompleteItem.getStepCompleteItemSid(), BusinessType.UPDATE.ordinal(), response, payProcessStepCompleteItem, TITLE);
        }
        return row;
    }

    /**
     * 变更计薪量申报-明细
     *
     * @param payProcessStepCompleteItem 计薪量申报-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePayProcessStepCompleteItem(PayProcessStepCompleteItem payProcessStepCompleteItem) {
        PayProcessStepCompleteItem response = payProcessStepCompleteItemMapper.selectPayProcessStepCompleteItemById(payProcessStepCompleteItem.getStepCompleteItemSid());
        int row = payProcessStepCompleteItemMapper.updateAllById(payProcessStepCompleteItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(payProcessStepCompleteItem.getStepCompleteItemSid(), BusinessType.CHANGE.ordinal(), response, payProcessStepCompleteItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除计薪量申报-明细
     *
     * @param stepCompleteItemSids 需要删除的计薪量申报-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePayProcessStepCompleteItemByIds(List<Long> stepCompleteItemSids) {
        return payProcessStepCompleteItemMapper.deleteBatchIds(stepCompleteItemSids);
    }

    /**
     * “工资清单”页签下方，在“考勤信息：页签旁边，新增一页签”计件量明细“
     *
     * @param payProcessStepCompleteItem PayProcessStepCompleteItem
     * @return int
     */
    @Override
    public List<PayProcessStepCompleteItem> getProcessStepCompleteWageItem(PayProcessStepCompleteItem payProcessStepCompleteItem) {
        return payProcessStepCompleteItemMapper.getProcessStepCompleteWageItem(payProcessStepCompleteItem);
    }

    /**
     * 刷新排序
     *
     * @param payProcessStepCompleteItemList PayProcessStepCompleteItem
     * @return payProcessStepCompleteItemList
     */
    @Override
    public List<PayProcessStepCompleteItem> sort(List<PayProcessStepCompleteItem> payProcessStepCompleteItemList){
        if (CollectionUtil.isNotEmpty(payProcessStepCompleteItemList)){
            payProcessStepCompleteItemList = payProcessStepCompleteItemList.stream()
                    .sorted(Comparator.comparing(PayProcessStepCompleteItem::getProductCode, Collator.getInstance(Locale.CHINA))
                        .thenComparing(PayProcessStepCompleteItem::getDepartment)
                        .thenComparing(PayProcessStepCompleteItem::getSort)
                        .thenComparing(PayProcessStepCompleteItem::getWorkerName, Collator.getInstance(Locale.CHINA))).collect(toList());
        }
        return payProcessStepCompleteItemList;
    }

    /**
     * 更新道序工价 / 工价倍率
     *
     * @param payProcessStepCompleteItemList PayProcessStepCompleteItem
     * @return payProcessStepCompleteItemList
     */
    @Override
    public List<PayProcessStepCompleteItem> updatePrice(PayProcessStepComplete payProcessStepComplete){
        List<PayProcessStepCompleteItem> itemList = payProcessStepComplete.getUpdatePriceItemList();
        if (CollectionUtil.isEmpty(itemList)){
            return new ArrayList<>();
        }
        itemList.forEach(item->{
            PayProductProcessStepItem payProductProcessStepItem = new PayProductProcessStepItem();
            try {
                payProductProcessStepItem = payProductProcessStepItemMapper.selectPayProductProcessStepItemBy(new PayProductProcessStepItem().setPlantSid(payProcessStepComplete.getPlantSid())
                        .setProductPriceType(payProcessStepComplete.getProductPriceType()).setJixinWangongType(payProcessStepComplete.getJixinWangongType())
                        .setDepartment(payProcessStepComplete.getDepartment()).setProductSid(item.getProductSid()).setStepItemSid(item.getProcessStepItemSid()));
            }catch (Exception e){
                log.warn("根据计薪量申报条件查询商品道序明细时查出多条，未获取到对应道序工价与工价倍率");
                payProductProcessStepItem = null;
            }
            if (payProductProcessStepItem != null){
                item.setPrice(payProductProcessStepItem.getPrice()).setPriceRate(payProductProcessStepItem.getPriceRate());
                if (item.getPrice() != null && item.getPriceRate() != null && item.getCompleteQuantity() != null && item.getWangongPriceRate() != null) {
                    item.setMoney(item.getPrice().multiply(item.getPriceRate()).multiply(item.getCompleteQuantity()).multiply(item.getWangongPriceRate()));
                }
            }
        });
        Map<String, PayProcessStepCompleteItem> map = itemList.stream()
                .collect(Collectors.toMap(o -> String.valueOf(o.getWorkerSid())+"-"+String.valueOf(o.getManufactureOrderSid())+
                        "-"+String.valueOf(o.getProductSid())+"-"+String.valueOf(o.getProcessStepSid())+
                        "-"+String.valueOf(o.getPaichanBatch()), Function.identity(), (t1,t2) -> t1));
        // 更新后的结果重新放进去 再返回所有明细行
        int j = 0;
        String key = "";
        List<PayProcessStepCompleteItem> list = payProcessStepComplete.getPayProcessStepCompleteItemList();
        PayProcessStepCompleteItem temp = null;
        for (int i = 0; i < list.size(); i++) {
            if (j > itemList.size()) {
                break;
            }
            temp = list.get(i);
            key = String.valueOf(temp.getWorkerSid())+"-"+String.valueOf(temp.getManufactureOrderSid())+
                    "-"+String.valueOf(temp.getProductSid())+"-"+String.valueOf(temp.getProcessStepSid())+
                    "-"+String.valueOf(temp.getPaichanBatch());
            if (map.containsKey(key)) {
                list.get(i).setPrice(map.get(key).getPrice()).setPriceRate(map.get(key).getPriceRate()).setMoney(map.get(key).getMoney());
            }
        }
        return list;
    }

    /**
     * 计薪量明细报表更新道序工价 / 工价倍率
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateItemPrice(List<PayProcessStepCompleteItem> itemList) {
        itemList.forEach(item->{
            PayProductProcessStepItem payProductProcessStepItem = new PayProductProcessStepItem();
            try {
                payProductProcessStepItem = payProductProcessStepItemMapper.selectPayProductProcessStepItemBy(new PayProductProcessStepItem().setPlantSid(item.getPlantSid())
                        .setProductPriceType(item.getProductPriceType()).setJixinWangongType(item.getJixinWangongType())
                        .setDepartment(item.getDepartment()).setProductSid(item.getProductSid()).setStepItemSid(item.getProcessStepItemSid()));
            }catch (Exception e){
                log.warn("根据计薪量申报条件查询商品道序明细时查出多条，未获取到对应道序工价与工价倍率");
                payProductProcessStepItem = null;
            }
            if (payProductProcessStepItem != null){
                item.setPrice(payProductProcessStepItem.getPrice()).setPriceRate(payProductProcessStepItem.getPriceRate());
                if (item.getPrice() != null && item.getPriceRate() != null && item.getCompleteQuantity() != null && item.getWangongPriceRate() != null) {
                    item.setMoney(item.getPrice().multiply(item.getPriceRate()).multiply(item.getCompleteQuantity()).multiply(item.getWangongPriceRate()));
                }
            }
        });
        int row = payProcessStepCompleteItemMapper.updatesPriceById(itemList);
        return row;
    }


    /**
     * 计薪明细预警 可选择性
     * 点击此按钮，进行结算量校验，如超量，则预警列显示红灯，否则预警列默认为：空
     */
    @Override
    public List<PayProcessStepCompleteItem> itemWarningBySelect(List<PayProcessStepCompleteItem> list, PayProcessStepComplete payProcessStepComplete) {
        List<PayProcessStepCompleteItem> itemList = payProcessStepComplete.getPayProcessStepCompleteItemList();
        if (CollectionUtil.isEmpty(list)){
            return new ArrayList<>();
        }
        EmsResultEntity entity = this.checkPayProductJijianSettleInforBySelect(list, payProcessStepComplete);
        List<CommonErrMsgResponse> responseList = entity.getMsgList();
        List<PayProcessStepCompleteItem> itemObject = responseList.stream().map(o->(PayProcessStepCompleteItem)o.getDate()).collect(toList());
        Map<String, List<PayProcessStepCompleteItem>> map = itemObject.stream()
                .collect(Collectors.groupingBy(o -> String.valueOf(o.getProductSid())+"-"+String.valueOf(o.getSort())+
                        "-"+String.valueOf(o.getPaichanBatch())));
        for (PayProcessStepCompleteItem item : list) {
            if (item.getCompleteQuantity() == null) {
                item.setLight("2");
                continue;
            }
            if (map.get(String.valueOf(item.getProductSid())+"-"+String.valueOf(item.getSort())+
                    "-"+String.valueOf(item.getPaichanBatch())) != null) {
                item.setLight("0");
            }
            else {
                item.setLight("1");
            }
        }
        return list;
    }

    @Override
    public EmsResultEntity checkPayProductJijianSettleInforBySelect(List<PayProcessStepCompleteItem> list, PayProcessStepComplete payProcessStepComplete){
        if (CollectionUtil.isEmpty(list)) {
            return EmsResultEntity.success();
        }
        List<PayProcessStepCompleteItem> totalList = payProcessStepComplete.getPayProcessStepCompleteItemList();
        // === 对要校验的明细行按照 所属年月+工厂+班组+部门+完工类型+计薪类型 + 商品+排产批次号 进行分组，查询 出 分组后 每一组的计件结算信息
        Set<PayProcessStepCompleteItem> playerSet = new TreeSet<>(Comparator.comparing(o -> (o.getProductSid() + "" + String.valueOf(o.getPaichanBatch()))));
        playerSet.addAll(list);
        QueryWrapper<PayProductJijianSettleInfor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PayProductJijianSettleInfor::getYearmonth, payProcessStepComplete.getYearmonth())
                .eq(PayProductJijianSettleInfor::getPlantSid, payProcessStepComplete.getPlantSid())
                .eq(PayProductJijianSettleInfor::getWorkCenterSid, payProcessStepComplete.getWorkCenterSid())
                .eq(PayProductJijianSettleInfor::getDepartment, payProcessStepComplete.getDepartment())
                .eq(PayProductJijianSettleInfor::getProductPriceType, payProcessStepComplete.getProductPriceType())
                .eq(PayProductJijianSettleInfor::getJixinWangongType, payProcessStepComplete.getJixinWangongType());
        queryWrapper.lambda().and(wrapper -> {
            for (PayProcessStepCompleteItem item : playerSet){
                wrapper.or(e ->{
                    e.eq(PayProductJijianSettleInfor::getProductSid, item.getProductSid());
                    if (item.getPaichanBatch()==null){
                        e.isNull(PayProductJijianSettleInfor::getPaichanBatch);
                    }
                    else {
                        e.eq(PayProductJijianSettleInfor::getPaichanBatch, item.getPaichanBatch());
                    }
                });
            }
        });
        List<PayProductJijianSettleInfor> inforList = payProductJijianSettleInforMapper.selectList(queryWrapper);
        // === 将得到的计件结算信息 临时改为已确认
        List<Long> sidList = new ArrayList<>();
        HashMap<String, Long> infoSidMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(inforList)){
            for (PayProductJijianSettleInfor infor : inforList) {
                sidList.add(infor.getJijianSettleInforSid());
                infoSidMap.put(String.valueOf(infor.getYearmonth())+"-"+String.valueOf(infor.getPlantSid())
                        +"-"+String.valueOf(infor.getWorkCenterSid())+"-"+String.valueOf(infor.getDepartment())
                        +"-"+String.valueOf(infor.getProductPriceType())+"-"+String.valueOf(infor.getJixinWangongType())
                        +"-"+String.valueOf(infor.getProductSid())+"-"+String.valueOf(infor.getPaichanBatch()), infor.getJijianSettleInforSid());
            }
            payProductJijianSettleInforMapper.update(null, new UpdateWrapper<PayProductJijianSettleInfor>().lambda()
                    .set(PayProductJijianSettleInfor::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(PayProductJijianSettleInfor::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .set(PayProductJijianSettleInfor::getConfirmDate, new Date())
                    .in(PayProductJijianSettleInfor::getJijianSettleInforSid, sidList));
        }
        // === result 为当前页面整个表单明细中 根据 商品+排产批次号 分组后小计 计薪量 字段
        Map<String, Double> result = new HashMap<>();
        if (CollectionUtil.isNotEmpty(totalList)){
            result = totalList.stream().collect(
                            Collectors.groupingBy(o -> String.valueOf(o.getProductSid())+"-"+String.valueOf(o.getSort())+"-"+String.valueOf(o.getPaichanBatch()),
                                    Collectors.summingDouble(v -> Double.valueOf(v.getCompleteQuantity() != null ? v.getCompleteQuantity().toString() : "0"))));
        }
        // 计薪量小计查询条件
        PayProcessStepCompleteItem search = null;
        // 结算数小计查询条件
        PayProductJijianSettleInfor infor = new PayProductJijianSettleInfor();
        infor.setPlantSid(payProcessStepComplete.getPlantSid())
                .setProductPriceType(payProcessStepComplete.getProductPriceType())
                .setJixinWangongType(payProcessStepComplete.getJixinWangongType())
                .setDepartment(payProcessStepComplete.getDepartment())
                .setHandleStatus(ConstantsEms.CHECK_STATUS);
        // 批量报错信息
        List<CommonErrMsgResponse> responseList = new ArrayList<>();
        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
        // 金额有效小数位格式化
        DecimalFormat df1 = new DecimalFormat("###.#");
        DecimalFormat df3 = new DecimalFormat("########.###");
        // 开始遍历需要校验分组后的明细行
        Set<PayProcessStepCompleteItem> playerSetIncludeSort = new TreeSet<>(Comparator.comparing(o -> (o.getProductSid() + "_" + String.valueOf(o.getPaichanBatch())
                + "_" + String.valueOf(o.getSort()))));
        playerSetIncludeSort.addAll(list);
        String paichan = "";
        for (PayProcessStepCompleteItem item : playerSetIncludeSort){
            // 根据工厂+商品工价类型+计薪完工类型+操作部门+款号+排产批次号+道序序号 且 已确认 然后排除自身(因为页面可以修改，要单独加) 的 计薪量申报明细中所有计薪量的小计
            search = new PayProcessStepCompleteItem();
            search.setPlantSid(payProcessStepComplete.getPlantSid()).setProductPriceType(payProcessStepComplete.getProductPriceType())
                    .setJixinWangongType(payProcessStepComplete.getJixinWangongType()).setDepartment(payProcessStepComplete.getDepartment())
                    .setHandleStatus(ConstantsEms.CHECK_STATUS);
            search.setProductSid(item.getProductSid()).setPaichanBatch(item.getPaichanBatch()).setSort(item.getSort());
            if (payProcessStepComplete.getStepCompleteSid() != null) {
                search.setStepCompleteSid(payProcessStepComplete.getStepCompleteSid());
            }
            // 得到自身工厂+商品工价类型+计薪完工类型+操作部门+款号+排产批次号+道序序号 在数据库中其它计薪量申报单明细的计薪量小计
            Double y = payProcessStepCompleteItemMapper.countCompleteQuantity(search);
            BigDecimal totalCompleteQuantity = BigDecimal.ZERO;
            if (y != null) {
                totalCompleteQuantity = new BigDecimal(y);
            }
            // 得到自身商品+排产批次号（注意这里没有道序序号） 在表单中的计薪量小计
            BigDecimal selfFormTotal = BigDecimal.ZERO;
            if (result.get(String.valueOf(item.getProductSid())+"-"+String.valueOf(item.getSort())+"-"+String.valueOf(item.getPaichanBatch())) != null) {
                selfFormTotal = new BigDecimal(result.get(String.valueOf(item.getProductSid())+"-"+String.valueOf(item.getSort())+"-"+String.valueOf(item.getPaichanBatch())));
            }
            if (selfFormTotal != null) {
                totalCompleteQuantity = totalCompleteQuantity.add(selfFormTotal);
            }
            // 根据所选择计薪量申报单的“工厂、商品工价类型、计薪完工类型、操作部门、款号、排产批次号”，
            // 获取“商品计件结算信息表”中的“已确认”状态的结算数，并对获取的结算数进行小计；
            infor.setProductSid(item.getProductSid()).setPaichanBatch(item.getPaichanBatch());
            Double z = payProductJijianSettleInforMapper.countSettleQuantity(infor);
            BigDecimal totleSettleQuantity = BigDecimal.ZERO;
            if (z != null) {
                totleSettleQuantity = new BigDecimal(z);
            }
            //判断计薪量小计是否大于结算数小计，
            //如是，提示错误信息：商品编码XXX的道序序号XXX的计薪量XXX大于对应的结算数XXX；
            //并将将“商品计件结算信息表”中此数据（当前勾选的计薪量申报单的所属年月对应的计件结算信息）的处理状态改为“保存”。
            if (totalCompleteQuantity.compareTo(totleSettleQuantity) > 0){
                errMsgResponse = new CommonErrMsgResponse();
                paichan = "为空";
                if (item.getPaichanBatch() != null) {
                    paichan = item.getPaichanBatch().toString();
                }
                errMsgResponse.setMsg("商品编码" + item.getProductCode() + "，排产批次号" + paichan
                        + "的道序序号" + df1.format(item.getSort()) + "的计薪量" + df3.format(totalCompleteQuantity)
                        + "大于对应的结算数" + df3.format(totleSettleQuantity));
                errMsgResponse.setDate(item);
                responseList.add(errMsgResponse);
            }
        }
        // 点暂存校验的处理（不管是否超量，暂存数据时，都将“商品计件结算信息表”中此数据（当前的计薪量申报单的“所属年月+工厂+班组+商品工价类型+计薪完工类型+操作部门+款号+排产批次号“对应的计件结算信息）的处理状态改为“保存”）
        if (ConstantsEms.SAVA_STATUS.equals(payProcessStepComplete.getHandleStatus()) && CollectionUtil.isNotEmpty(sidList)) {
            payProductJijianSettleInforMapper.update(null, new UpdateWrapper<PayProductJijianSettleInfor>().lambda()
                    .set(PayProductJijianSettleInfor::getHandleStatus, ConstantsEms.SAVA_STATUS)
                    .set(PayProductJijianSettleInfor::getConfirmerAccount, null)
                    .set(PayProductJijianSettleInfor::getConfirmDate, null)
                    .in(PayProductJijianSettleInfor::getJijianSettleInforSid, sidList));
        }
        if (CollectionUtil.isNotEmpty(responseList)){
            if (ConstantsEms.CHECK_STATUS.equals(payProcessStepComplete.getHandleStatus()) && CollectionUtil.isNotEmpty(sidList)) {
                payProductJijianSettleInforMapper.update(null, new UpdateWrapper<PayProductJijianSettleInfor>().lambda()
                        .set(PayProductJijianSettleInfor::getHandleStatus, ConstantsEms.SAVA_STATUS)
                        .set(PayProductJijianSettleInfor::getConfirmerAccount, null)
                        .set(PayProductJijianSettleInfor::getConfirmDate, null)
                        .in(PayProductJijianSettleInfor::getJijianSettleInforSid, sidList));
            }
            return EmsResultEntity.error(responseList);
        }
        return EmsResultEntity.success();
    }

    /**
     * 计薪明细按款显示
     */
    @Override
    public PayProcessStepCompleteTableResponse itemTable(PayProcessStepCompleteTableRequest request) {
        PayProcessStepCompleteTableResponse response = new PayProcessStepCompleteTableResponse();
        BeanCopyUtils.copyProperties(request, response);
        // 计算 结算数累计(含本月)
        BigDecimal settleQuantityMonth = new BigDecimal(
                payProductJijianSettleInforMapper.countSettleQuantity(new PayProductJijianSettleInfor().setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setProductSid(request.getProductSid()).setPaichanBatch(request.getPaichanBatch()).setPlantSid(request.getPlantSid())
                        .setDepartment(request.getDepartment()).setJixinWangongType(request.getJixinWangongType()).setProductPriceType(request.getProductPriceType()))
        );
        if (!ConstantsEms.CHECK_STATUS.equals(request.getHandleStatus())) {
            settleQuantityMonth = settleQuantityMonth.add(new BigDecimal(
                    payProductJijianSettleInforMapper.countSettleQuantity(new PayProductJijianSettleInfor().setHandleStatus(ConstantsEms.SAVA_STATUS).setWorkCenterSid(request.getWorkCenterSid())
                            .setProductSid(request.getProductSid()).setPaichanBatch(request.getPaichanBatch()).setPlantSid(request.getPlantSid()).setYearmonth(request.getYearmonth())
                            .setDepartment(request.getDepartment()).setJixinWangongType(request.getJixinWangongType()).setProductPriceType(request.getProductPriceType()))
            ));
        }
        response.setSettleQuantityMonth(settleQuantityMonth);
        // 商品道序 明细
        List<PayProductProcessStepItem> stepItemList = payProductProcessStepItemMapper.selectPayProductProcessStepItemList(new PayProductProcessStepItem()
                .setProductSid(request.getProductSid()).setPlantSid(request.getPlantSid()).setDepartment(request.getDepartment())
                .setJixinWangongType(request.getJixinWangongType()).setProductPriceType(response.getProductPriceType())
                .setHandleStatus(ConstantsEms.CHECK_STATUS));
        List<PayProcessStepCompleteTableStepResponse> stepResponseList = new ArrayList<>();
        stepResponseList = BeanCopyUtils.copyListProperties(stepItemList, PayProcessStepCompleteTableStepResponse::new);
        // 排序
        stepResponseList = stepResponseList.stream()
                .sorted(Comparator.comparing(PayProcessStepCompleteTableStepResponse::getSort, Comparator.nullsFirst(BigDecimal::compareTo))).collect(toList());
        response.setStepItemList(stepResponseList);
        // 存放 员工 + 道序序号 作为 键， 计薪量 作为 值
        HashMap<String, BigDecimal> staffSortMaps = new HashMap<>();
        // 存放 每个道序序号 对应 计薪量的值， 会用来做小计
        HashMap<String, BigDecimal> sortQuantity = new HashMap<>();
        // 当前表单明细中符合所选中商品编码和排产批次号的行数据
        List<PayProcessStepCompleteItem> currentItemList = request.getPayProcessStepCompleteItemList().stream().filter(
                o -> o.getProductSid().equals(request.getProductSid()) && (
                        (o.getPaichanBatch() != null && o.getPaichanBatch().equals(request.getPaichanBatch())) ||
                                (o.getPaichanBatch() == null && request.getPaichanBatch() == null)
                )
        ).collect(toList());
        // 保持序号小数位一致性
        DecimalFormat df = new DecimalFormat("####.##");
        String sort = null;
        for (PayProcessStepCompleteTableStepResponse step : stepResponseList) {
            sort = df.format(step.getSort());
            // 道序序号  -> 计薪量
            if (sortQuantity.get(String.valueOf(sort)) == null) {
                sortQuantity.put(String.valueOf(sort), null);
            }
        }
        if (CollectionUtil.isNotEmpty(currentItemList)) {
            for (PayProcessStepCompleteItem item : currentItemList) {
                if (item.getSort() != null) {
                    sort = df.format(item.getSort());
                }
                // 员工 + 道序序号  -> 计薪量
                if (staffSortMaps.get(item.getWorkerSid()+"-"+String.valueOf(sort)) == null) {
                    staffSortMaps.put(item.getWorkerSid()+"-"+String.valueOf(sort), item.getCompleteQuantity());
                }
                // 道序序号  -> 计薪量
                if (!sortQuantity.containsKey(String.valueOf(sort))) {
                    sortQuantity.put(String.valueOf(sort), item.getCompleteQuantity());
                }
                else {
                    BigDecimal total = sortQuantity.get(String.valueOf(sort));
                    if (total == null) {
                        total = BigDecimal.ZERO;
                    }
                    total = total.add(item.getCompleteQuantity()==null?BigDecimal.ZERO:item.getCompleteQuantity());
                    sortQuantity.put(String.valueOf(sort), total);
                }
            }
        }
        // 表格要展示的 员工
        List<PayProcessStepCompleteTableStaffResponse> tableStaffList = new ArrayList<>();
        tableStaffList = BeanCopyUtils.copyListProperties(currentItemList, PayProcessStepCompleteTableStaffResponse::new);
        tableStaffList = tableStaffList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                new TreeSet<>(Comparator.comparing(PayProcessStepCompleteTableStaffResponse::getWorkerSid))), ArrayList::new));
        // 写入每行员工 的 每个 道序序号 对应 的计薪量
        List<PayProcessStepCompleteTableQuantityResponse> stepQuantityList = new ArrayList<>();
        stepQuantityList = BeanCopyUtils.copyListProperties(stepResponseList, PayProcessStepCompleteTableQuantityResponse::new);
        sort = null;
        for (PayProcessStepCompleteTableStaffResponse staff : tableStaffList) {
            // 声明每个员工自己的道序对应计薪量的内存空间，保证每个员工的道序对应计薪量都是自己独立内存空间
            List<PayProcessStepCompleteTableQuantityResponse> quantityList = new ArrayList<>();
            quantityList = BeanCopyUtils.copyListProperties(stepQuantityList, PayProcessStepCompleteTableQuantityResponse::new);
            for (PayProcessStepCompleteTableQuantityResponse item : quantityList) {
                if (item.getSort() != null) {
                    sort = df.format(item.getSort());
                }
                item.setCompleteQuantity(staffSortMaps.get(staff.getWorkerSid()+"-"+String.valueOf(sort)));
            }
            staff.setQuantityList(quantityList);
        }
        // 排序
        tableStaffList = tableStaffList.stream()
                .sorted(Comparator.comparing(PayProcessStepCompleteTableStaffResponse::getWorkerName, Collator.getInstance(Locale.CHINA))).collect(toList());
        // 小计
        PayProcessStepCompleteTableStaffResponse xiaoji = new PayProcessStepCompleteTableStaffResponse();
        xiaoji.setWorkerName("小计");
        List<PayProcessStepCompleteTableQuantityResponse> xiaojiQuantity = new ArrayList<>();
        sortQuantity.forEach((key, value) -> {
            PayProcessStepCompleteTableQuantityResponse step = new PayProcessStepCompleteTableQuantityResponse();
            step.setSort(new BigDecimal(key));
            step.setCompleteQuantity(value);
            xiaojiQuantity.add(step);
        });
        xiaoji.setQuantityList(xiaojiQuantity.stream()
                .sorted(Comparator.comparing(PayProcessStepCompleteTableQuantityResponse::getSort, Comparator.nullsFirst(BigDecimal::compareTo))).collect(toList()));
        tableStaffList.add(xiaoji);
        response.setStaffList(tableStaffList);
        return response;
    }

    /**
     * 计薪明细按款录入的显示  request  中的 productSid   和   paichanBatch   是勾选 选中行的 ，每勾选行 则 productSid 为空
     * 前端要额外定义字段
     */
    @Override
    public PayProcessStepCompleteTableResponse itemAddToTable(PayProcessStepComplete request) {
        // 返回的表单数据
        PayProcessStepCompleteTableResponse response = new PayProcessStepCompleteTableResponse();
        response.setProductCode(request.getProductCode()).setProductSid(request.getProductSid()).setMaterialName(request.getProductName())
                .setPaichanBatch(request.getPaichanBatch()).setYearmonth(request.getYearmonth()).setWorkCenterSid(request.getWorkCenterSid())
                .setPlantSid(request.getPlantSid()).setPlantCode(request.getPlantCode()).setDepartment(request.getDepartment())
                .setProductPriceType(request.getProductPriceType()).setJixinWangongType(request.getJixinWangongType()).setHandleStatus(request.getHandleStatus());
        // 前端当前页面展示带过来的所有明细
        List<PayProcessStepCompleteItem> itemList = request.getPayProcessStepCompleteItemList();
        // 按款录入的弹出框的列根据“工厂、操作部门、商品编码、商品工价类型、计薪完工类型”带出对应的道序序号
        List<PayProcessStepCompleteItem> processItemList = payProductProcessStepItemMapper.selectPayProductProcessStepItemSort(new PayProductProcessStepItem()
                .setProductSid(request.getProductSid()).setPlantSid(request.getPlantSid()).setDepartment(request.getDepartment())
                .setJixinWangongType(request.getJixinWangongType()).setProductPriceType(request.getProductPriceType())
                .setHandleStatus(ConstantsEms.CHECK_STATUS));
        List<PayProcessStepCompleteTableStepResponse> stepItemList = BeanCopyUtils.copyListProperties(processItemList, PayProcessStepCompleteTableStepResponse::new);
        response.setStepItemList(stepItemList);
        response.setPayProcessStepCompleteItemList(new ArrayList<>());
        response.setStaffList(new ArrayList<>());
        // 计算 结算数累计(含本月)
        BigDecimal settleQuantityMonth = new BigDecimal(
                payProductJijianSettleInforMapper.countSettleQuantity(new PayProductJijianSettleInfor().setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setProductSid(request.getProductSid()).setPaichanBatch(request.getPaichanBatch()).setPlantSid(request.getPlantSid())
                        .setDepartment(request.getDepartment()).setJixinWangongType(request.getJixinWangongType()).setProductPriceType(request.getProductPriceType()))
        );
        if (!ConstantsEms.CHECK_STATUS.equals(request.getHandleStatus())) {
            settleQuantityMonth = settleQuantityMonth.add(new BigDecimal(
                    payProductJijianSettleInforMapper.countSettleQuantity(new PayProductJijianSettleInfor().setHandleStatus(ConstantsEms.SAVA_STATUS).setWorkCenterSid(request.getWorkCenterSid())
                            .setProductSid(request.getProductSid()).setPaichanBatch(request.getPaichanBatch()).setPlantSid(request.getPlantSid()).setYearmonth(request.getYearmonth())
                            .setDepartment(request.getDepartment()).setJixinWangongType(request.getJixinWangongType()).setProductPriceType(request.getProductPriceType()))
            ));
        }
        response.setSettleQuantityMonth(settleQuantityMonth);
        if (!"Y".equals(request.getGouxuan())) {
            return response;
        }
        if (CollectionUtil.isNotEmpty(itemList)) {
            // 筛选出选中的 选中的 明细的 排产批次号 不是空的
            if (request.getPaichanBatch() != null) {
                itemList = itemList.stream().filter(o -> o.getProductSid().equals(request.getProductSid()) && o.getPaichanBatch() != null &&
                        o.getPaichanBatch().equals(request.getPaichanBatch())).collect(toList());
            }
            // 筛选出选中的 选中的 明细的 排产批次号 是空的 或者 是没有选中明细行
            else {
                itemList = itemList.stream().filter(o -> o.getProductSid().equals(request.getProductSid()) && o.getPaichanBatch() == null).collect(toList());
            }
            // 每行员工
            List<PayProcessStepCompleteTableStaffResponse> staffList = new ArrayList<>();
            Map<Long, PayProcessStepCompleteTableStaffResponse> staffMap = new HashMap<>();
            // 每行员工对应的商品道序
            List<PayProcessStepCompleteItem> staffCompleteItemList = new ArrayList<>();
            // 遍历 页面上的 明细， 分离出员工
            for (PayProcessStepCompleteItem completeItem : itemList) {
                PayProcessStepCompleteTableStaffResponse staff = new PayProcessStepCompleteTableStaffResponse();
                // staffMap若不存在员工 则 写入该员工的行数据
                if (!staffMap.containsKey(completeItem.getWorkerSid())) {
                    // 这属于第一次写入该员工
                    staff.setWorkerSid(completeItem.getWorkerSid())
                            .setWorkerCode(completeItem.getWorkerCode())
                            .setWorkerName(completeItem.getWorkerName());
                    // 每个员工对应都要有的 商品道序明细 就是每个各自的序号
                    List<PayProcessStepCompleteItem> staffProcessList = BeanCopyUtils.copyListProperties(processItemList, PayProcessStepCompleteItem::new);
                    // 对应该有的所有商品道序中去除 当前遍历到的 商品道序 , 然后写入当前的商品道序 进去
                    staffProcessList = staffProcessList.stream().filter(o -> o.getSort().compareTo(completeItem.getSort()) != 0).collect(toList());
                    staffProcessList.add(completeItem);
                    staff.setPayProcessStepCompleteItemList(staffProcessList);
                    staffMap.put(completeItem.getWorkerSid(),staff);
                    staffList.add(staff);
                }
                // 更新该员工 的 工序明细 将当前工序明细写入作为员工的工序明细
                else {
                    staff = staffMap.get(completeItem.getWorkerSid());
                    Long sid = staff.getWorkerSid();
                    // 将当前明细写入 该员工对应的 工序明细
                    staffCompleteItemList = staff.getPayProcessStepCompleteItemList();
                    // 去除 当前遍历到的 商品道序 , 然后写入当前的商品道序 进去
                    staffCompleteItemList = staffCompleteItemList.stream().filter(o -> o.getSort().compareTo(completeItem.getSort()) != 0).collect(toList());
                    staffCompleteItemList.add(completeItem);
                    staff.setPayProcessStepCompleteItemList(staffCompleteItemList);
                    staffMap.put(completeItem.getWorkerSid(),staff);
                    staffList = staffList.stream().filter(o -> !o.getWorkerSid().equals(sid)).collect(toList());
                    staffList.add(staff);
                }
            }
            // 针对 每一行员工 对 道序序号 进行排序
            staffList.forEach(staff->{
                List<PayProcessStepCompleteItem> completeItemList = staff.getPayProcessStepCompleteItemList();
                completeItemList = completeItemList.stream().sorted(Comparator.comparing(PayProcessStepCompleteItem::getSort,
                                Comparator.nullsLast(BigDecimal::compareTo))).collect(toList());
                staff.setPayProcessStepCompleteItemList(completeItemList);
            });
            staffList = staffList.stream().sorted(Comparator.comparing(PayProcessStepCompleteTableStaffResponse::getWorkerName,
                            Collator.getInstance(Locale.CHINA))).collect(toList());
            response.setStaffList(staffList);
        }
        return response;
    }

    /**
     * 计薪明细按款录入点添加
     */
    @Override
    public EmsResultEntity itemAddByTable(PayProcessStepCompleteTableResponse request) {
        // 当前弹出框背后 原本已经存在的 明细数据
        List<PayProcessStepCompleteItem> formItemList = request.getPayProcessStepCompleteItemList();
        List<PayProcessStepCompleteTableStaffResponse> staffList = request.getStaffList();
        if (CollectionUtil.isNotEmpty(staffList)) {
            // 员工 作为key 生成map 用来判断 哪个员工是新增的  因为新增的员工 行的小格数据是没有 道序数据的 要额外写入道序数据
            Map<String, List<PayProcessStepCompleteItem>> newStaffMap = formItemList.stream()
                    .collect(Collectors.groupingBy(o -> String.valueOf(o.getWorkerSid())));
            // 用来存放有计薪量的小格子
            List<PayProcessStepCompleteItem> itemList = new ArrayList<>();
            // 获取道序信息  给 新增的 小格写入  对应的 道序的 数据
            // 按款录入的弹出框的列根据“工厂、操作部门、商品编码、商品工价类型、计薪完工类型”带出对应的道序序号
            List<PayProcessStepCompleteItem> processItemList = payProductProcessStepItemMapper.selectPayProductProcessStepItemSort(new PayProductProcessStepItem()
                    .setProductSid(request.getProductSid()).setPlantSid(request.getPlantSid()).setDepartment(request.getDepartment())
                    .setJixinWangongType(request.getJixinWangongType()).setProductPriceType(request.getProductPriceType())
                    .setHandleStatus(ConstantsEms.CHECK_STATUS));
            // 道序明细行sid  做为 键
            Map<Long, PayProcessStepCompleteItem> processStepSortMap = processItemList.stream().collect(Collectors.toMap(PayProcessStepCompleteItem::getProcessStepItemSid, Function.identity()));
            // 遍历每行员工 拿到每个小格的 商品道序明细
            int i = 0;
            for (PayProcessStepCompleteTableStaffResponse staff : staffList) {
                i = 0;
                // 判断是否为新加的员工 后为每个小格的道序明细写入道序数据
                if (newStaffMap.get(staff.getWorkerSid()) == null) {
                    i = 1;
                }
                List<PayProcessStepCompleteItem> completeItemList = staff.getPayProcessStepCompleteItemList();
                // 过滤没有填写计薪量的小格，只要保留有填写计薪量的小格（商品道序明细）
                completeItemList = completeItemList.stream().filter(o -> o.getCompleteQuantity() != null).collect(toList());
                if (CollectionUtil.isNotEmpty(completeItemList)) {
                    for (PayProcessStepCompleteItem item : completeItemList) {
                        // 如果这笔是新的 商品道序明细 则要补充 员工 + 商品
                        if (item.getStepCompleteItemSid() == null) {
                            item.setWorkerSid(staff.getWorkerSid())
                                    .setWorkerCode(staff.getWorkerCode()).setWorkerName(staff.getWorkerName())
                                    .setProductSid(request.getProductSid()).setPaichanBatch(request.getPaichanBatch())
                                    .setProductCode(request.getProductCode());
                        }
                        if (i == 1) {
                            PayProcessStepCompleteItem processStep = processStepSortMap.get(item.getProcessStepItemSid());
                            if (processStep != null) {
                                item.setProcessStepSid(processStep.getProcessStepSid()).setProcessName(processStep.getProcessName())
                                        .setProcessStepCode(processStep.getProcessStepCode()).setProcessStepName(processStep.getProcessStepName())
                                        .setProcessStepItemSid(processStep.getProcessStepItemSid()).setProcessSid(processStep.getProcessSid())
                                        .setStepCategory(processStep.getStepCategory()).setProcessCode(processStep.getProcessCode())
                                        .setDepartment(processStep.getDepartment()).setDepartmentName(processStep.getDepartmentName())
                                        .setPrice(processStep.getPrice()).setPriceRate(processStep.getPriceRate()).setMaterialName(processStep.getMaterialName())
                                        .setManufactureOrderSid(processStep.getManufactureOrderSid()).setProductName(processStep.getProductName())
                                        .setManufactureOrderCode(processStep.getManufactureOrderCode()).setCreateDate(new Date());
                            }
                        }
                    }
                    itemList.addAll(completeItemList);
                }
            }
            // 筛选出选中的 选中的 明细的 排产批次号 不是空的
            if (request.getPaichanBatch() != null) {
                formItemList = formItemList.stream().filter(o -> !(o.getProductSid().equals(request.getProductSid()) &&
                        request.getPaichanBatch().equals(o.getPaichanBatch()))).collect(toList());
            }
            // 筛选出选中的 选中的 明细的 排产批次号 是空的 或者 是没有选中明细行
            else {
                formItemList = formItemList.stream().filter(o -> !(o.getProductSid().equals(request.getProductSid()) && o.getPaichanBatch() == null)).collect(toList());
            }
            if (CollectionUtil.isNotEmpty(itemList)) {
                formItemList.addAll(itemList);
                formItemList = formItemList.stream()
                        .sorted(Comparator.comparing(PayProcessStepCompleteItem::getProductCode, Collator.getInstance(Locale.CHINA))
                                .thenComparing(PayProcessStepCompleteItem::getDepartment)
                                .thenComparing(PayProcessStepCompleteItem::getSort)
                                .thenComparing(PayProcessStepCompleteItem::getWorkerName, Collator.getInstance(Locale.CHINA))).collect(toList());
                // 超量校验
                PayProcessStepComplete complete = new PayProcessStepComplete();
                BeanCopyUtils.copyProperties(request,complete);
                complete.setPayProcessStepCompleteItemList(formItemList);
                EmsResultEntity entity = this.checkPayProductJijianSettleInforBySelect(itemList, complete);
                if (EmsResultEntity.ERROR_TAG.equals(entity.getTag())) {
                    List<CommonErrMsgResponse> responseList = entity.getMsgList();
                    responseList.forEach(item->{
                        item.setDate(null);
                    });
                    entity.setTag(EmsResultEntity.WARN_TAG);
                    entity.setMsgList(responseList);
                    entity.setData(formItemList);
                    return entity;
                }
            }
        }
        else {
            // 如果 按款录入 没有员工行 了，有勾选数据的话才更新 ，没勾选数据的话没带进来则可以不用更新
            if ("Y".equals(request.getGouxuan())) {
                if (request.getPaichanBatch() != null ) {
                    formItemList = formItemList.stream().filter(o->o.getProductSid().equals(request.getProductSid())
                            && request.getPaichanBatch().equals(o.getPaichanBatch())).collect(toList());
                }
                else {
                    formItemList = formItemList.stream().filter(o->o.getProductSid().equals(request.getProductSid())
                            && o.getPaichanBatch() == null).collect(toList());
                }
            }
        }
        return EmsResultEntity.success(formItemList);
    }

    /**
     * 计薪量申报明细报表 查询
     */
    @Override
    public List<PaySalaryWageFormResponse> getProcessStepCompleteWage(PaySalaryWageFormResponse paySalaryBillItem) {
        return payProcessStepCompleteItemMapper.getProcessStepCompleteWageForm(paySalaryBillItem);
    }

    /**
     * 计薪量申报明细报表 打印汇总工资明细报表 -- 数据源
     */
    @Override
    public List<PaySalaryWageFormResponse> printProcessStepCompleteWageVo(PaySalaryWageFormResponse paySalaryBillItem) {
        return payProcessStepCompleteItemMapper.getCollectProcessStepCompleteWageForm(paySalaryBillItem);
    }

    /**
     * 计薪量申报明细报表 打印汇总工资明细报表按结算班组 -- 数据源
     */
    @Override
    public List<PaySalaryWageFormResponse> printProcessStepCompleteWageProcessVo(PaySalaryWageFormResponse paySalaryBillItem) {
        return payProcessStepCompleteItemMapper.getCollectProcessStepCompleteWageProcessForm(paySalaryBillItem);
    }

    /**
     * 计薪量申报明细报表 打印汇总工资明细报表 -- 按员工+班组
     */
    @Override
    public HashMap<String, Object> printProcessStepCompleteWage(List<PaySalaryWageFormResponse> itemList) {
        HashMap<String, Object> response = new HashMap<>();
        List<PaySalaryWageExportReport> exportReport = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(itemList)) {
            Map<String, List<PaySalaryWageFormResponse>> map = itemList.stream().collect(Collectors.groupingBy(PaySalaryWageFormResponse::getStaffAndWorkSid));
            for (List<PaySalaryWageFormResponse> group : map.values()) {
                PaySalaryWageExportReport report = new PaySalaryWageExportReport();
                report.setFormItem(group);
                report.setStaffName(group.get(0).getStaffName())
                        .setWorkCenterName(group.get(0).getWorkCenterName());
                BigDecimal quantity = group.parallelStream().map(PaySalaryWageFormResponse::getCompleteQuantity)
                        .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
                report.setQuantity(quantity);
                BigDecimal money = group.parallelStream().map(PaySalaryWageFormResponse::getMoney)
                        .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
                report.setMoney(money);
                exportReport.add(report);
            }
            if (CollectionUtil.isNotEmpty(exportReport)){
                Comparator comparing = Collator.getInstance(Locale.CHINA);
                exportReport = exportReport.stream().sorted((t1,t2)->comparing.compare(t1.getWorkCenterName(),t2.getWorkCenterName())).collect(toList());
            }
            BigDecimal moneySum = itemList.parallelStream().map(PaySalaryWageFormResponse::getMoney)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
            response.put("moneySum", moneySum);
            // 获取日期范围
            itemList = itemList.stream().filter(o->o.getReportDate() != null).sorted(Comparator.comparing(l -> l.getReportDate(), Comparator.nullsFirst(Date::compareTo))).collect(toList());
            if (CollectionUtil.isNotEmpty(itemList)){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateBegin = sdf.format(itemList.get(0).getReportDate());
                String dateEnd = sdf.format(itemList.get(itemList.size()-1).getReportDate());
                response.put("dateBegin", dateBegin);
                response.put("dateEnd", dateEnd);
            }
        }
        response.put("exportReport", exportReport);
        return response;
    }

    /**
     * 计薪量申报明细报表 打印汇总工资明细报表 -- 按员工
     */
    @Override
    public HashMap<String, Object> printProcessStepCompleteWageByStaff(List<PaySalaryWageFormResponse> itemList) {
        HashMap<String, Object> response = new HashMap<>();
        List<PaySalaryWageExportReport> exportReport = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(itemList)) {
            DecimalFormat df = new DecimalFormat("####.#");
            DecimalFormat df2 = new DecimalFormat("####.##");
            DecimalFormat df3 = new DecimalFormat("########.###");
            itemList.forEach(item->{
                item.setSortToString(df2.format(item.getSort()==null?BigDecimal.ZERO:item.getSort()));
                item.setPriceRateToString(df3.format(item.getPriceRate()==null?BigDecimal.ZERO:item.getPriceRate()));
                item.setWangongPriceRateToString(df3.format(item.getWangongPriceRate()==null?BigDecimal.ZERO:item.getWangongPriceRate()));
                item.setCompleteQuantityToString(df.format(item.getCompleteQuantity()==null?BigDecimal.ZERO:item.getCompleteQuantity()));
            });
            Map<Long, List<PaySalaryWageFormResponse>> map = itemList.stream().collect(Collectors.groupingBy(PaySalaryWageFormResponse::getStaffSid));
            for (List<PaySalaryWageFormResponse> group : map.values()) {
                group = group.stream()
                        .sorted(Comparator.comparing(PaySalaryWageFormResponse::getStaffName, Collator.getInstance(Locale.CHINA))
                                .thenComparing(PaySalaryWageFormResponse::getProductCode, Collator.getInstance(Locale.CHINA))
                                .thenComparing(PaySalaryWageFormResponse::getDepartment)
                                .thenComparing(PaySalaryWageFormResponse::getSort)).collect(toList());
                PaySalaryWageExportReport report = new PaySalaryWageExportReport();
                report.setFormItem(group);
                report.setStaffName(group.get(0).getStaffName())
                        .setWorkCenterName(group.get(0).getStaffWorkCenterName());
                BigDecimal quantity = group.parallelStream().map(PaySalaryWageFormResponse::getCompleteQuantity)
                        .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
                report.setQuantity(quantity);
                report.setQuantityToString(df.format(quantity==null?BigDecimal.ZERO:quantity));
                BigDecimal money = group.parallelStream().map(PaySalaryWageFormResponse::getMoney)
                        .reduce(BigDecimal.ZERO, BigDecimalSum::sum);
                report.setMoney(money);
                exportReport.add(report);
            }
            if (CollectionUtil.isNotEmpty(exportReport)){
                exportReport = exportReport.stream()
                        .sorted(Comparator.comparing(PaySalaryWageExportReport::getWorkCenterName, Collator.getInstance(Locale.CHINA))
                                .thenComparing(PaySalaryWageExportReport::getStaffName, Collator.getInstance(Locale.CHINA))).collect(toList());
            }
            BigDecimal moneySum = itemList.parallelStream().map(PaySalaryWageFormResponse::getMoney)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
            response.put("moneySum", moneySum);
            // 获取日期范围
            itemList = itemList.stream().filter(o->o.getReportDate() != null).sorted(Comparator.comparing(l -> l.getReportDate(), Comparator.nullsFirst(Date::compareTo))).collect(toList());
            if (CollectionUtil.isNotEmpty(itemList)){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateBegin = sdf.format(itemList.get(0).getReportDate());
                String dateEnd = sdf.format(itemList.get(itemList.size()-1).getReportDate());
                response.put("dateBegin", dateBegin);
                response.put("dateEnd", dateEnd);
            }
        }
        response.put("exportReport", exportReport);
        return response;
    }

    /**
     * 计薪量申报明细报表 打印汇总工资明细报表 -- 按员工的班组
     */
    @Override
    public HashMap<String, Object> printProcessStepCompleteWageByWorkCenter(List<PaySalaryWageFormResponse> itemList) {
        HashMap<String, Object> response = new HashMap<>();
        List<PaySalaryWageExportReport> exportReport = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(itemList)) {
            DecimalFormat df = new DecimalFormat("####.#");
            DecimalFormat df2 = new DecimalFormat("####.##");
            DecimalFormat df3 = new DecimalFormat("########.###");
            itemList.forEach(item->{
                item.setSortToString(df2.format(item.getSort()==null?BigDecimal.ZERO:item.getSort()));
                item.setPriceRateToString(df3.format(item.getPriceRate()==null?BigDecimal.ZERO:item.getPriceRate()));
                item.setWangongPriceRateToString(df3.format(item.getWangongPriceRate()==null?BigDecimal.ZERO:item.getWangongPriceRate()));
                item.setCompleteQuantityToString(df.format(item.getCompleteQuantity()==null?BigDecimal.ZERO:item.getCompleteQuantity()));
            });
            Map<Long, List<PaySalaryWageFormResponse>> map = itemList.stream().collect(Collectors.groupingBy(
                    x -> Optional.ofNullable(x.getStaffWorkCenterSid()).orElse(Long.valueOf(0))));
            for (List<PaySalaryWageFormResponse> group : map.values()) {
                group = group.stream()
                        .sorted(Comparator.comparing(PaySalaryWageFormResponse::getStaffName, Collator.getInstance(Locale.CHINA))
                                .thenComparing(PaySalaryWageFormResponse::getProductCode, Collator.getInstance(Locale.CHINA))
                                .thenComparing(PaySalaryWageFormResponse::getSort)).collect(toList());
                PaySalaryWageExportReport report = new PaySalaryWageExportReport();
                report.setFormItem(group);
                report.setStaffName(group.get(0).getStaffName())
                        .setWorkCenterName(group.get(0).getStaffWorkCenterName());
                BigDecimal quantity = group.parallelStream().map(PaySalaryWageFormResponse::getCompleteQuantity)
                        .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
                report.setQuantity(quantity);
                report.setQuantityToString(df.format(quantity==null?BigDecimal.ZERO:quantity));
                BigDecimal money = group.parallelStream().map(PaySalaryWageFormResponse::getMoney)
                        .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
                report.setMoney(money);
                exportReport.add(report);
            }
            if (CollectionUtil.isNotEmpty(exportReport)){
                Comparator comparing = Collator.getInstance(Locale.CHINA);
                exportReport = exportReport.stream().sorted((t1,t2)->comparing.compare(t1.getWorkCenterName(),t2.getWorkCenterName())).collect(toList());
            }
            BigDecimal moneySum = itemList.parallelStream().map(PaySalaryWageFormResponse::getMoney)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
            response.put("moneySum", moneySum);
            // 获取日期范围
            itemList = itemList.stream().filter(o->o.getReportDate() != null).sorted(Comparator.comparing(l -> l.getReportDate(), Comparator.nullsFirst(Date::compareTo))).collect(toList());
            if (CollectionUtil.isNotEmpty(itemList)){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateBegin = sdf.format(itemList.get(0).getReportDate());
                String dateEnd = sdf.format(itemList.get(itemList.size()-1).getReportDate());
                response.put("dateBegin", dateBegin);
                response.put("dateEnd", dateEnd);
            }
        }
        response.put("exportReport", exportReport);
        return response;
    }

    /**
     * 计薪量申报明细报表 打印汇总工资明细报表 -- 按结算的班组
     */
    @Override
    public HashMap<String, Object> printProcessStepCompleteWageByWorkCenterProcess(List<PaySalaryWageFormResponse> itemList) {
        HashMap<String, Object> response = new HashMap<>();
        List<PaySalaryWageExportReport> exportReport = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(itemList)) {
            DecimalFormat df = new DecimalFormat("####.#");
            DecimalFormat df2 = new DecimalFormat("####.##");
            DecimalFormat df3 = new DecimalFormat("########.###");
            itemList.forEach(item->{
                item.setSortToString(df2.format(item.getSort()==null?BigDecimal.ZERO:item.getSort()));
                item.setPriceRateToString(df3.format(item.getPriceRate()==null?BigDecimal.ZERO:item.getPriceRate()));
                item.setWangongPriceRateToString(df3.format(item.getWangongPriceRate()==null?BigDecimal.ZERO:item.getWangongPriceRate()));
                item.setCompleteQuantityToString(df.format(item.getCompleteQuantity()==null?BigDecimal.ZERO:item.getCompleteQuantity()));
            });
            Map<Long, List<PaySalaryWageFormResponse>> map = itemList.stream().collect(Collectors.groupingBy(
                    x -> Optional.ofNullable(x.getWorkCenterSid()).orElse(Long.valueOf(0))));
            for (List<PaySalaryWageFormResponse> group : map.values()) {
                group = group.stream()
                        .sorted(Comparator.comparing(PaySalaryWageFormResponse::getStaffName, Collator.getInstance(Locale.CHINA))
                                .thenComparing(PaySalaryWageFormResponse::getProductCode, Collator.getInstance(Locale.CHINA))
                                .thenComparing(PaySalaryWageFormResponse::getSort)).collect(toList());
                PaySalaryWageExportReport report = new PaySalaryWageExportReport();
                report.setFormItem(group);
                report.setStaffName(group.get(0).getStaffName())
                        .setWorkCenterName(group.get(0).getWorkCenterName());
                BigDecimal quantity = group.parallelStream().map(PaySalaryWageFormResponse::getCompleteQuantity)
                        .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
                report.setQuantity(quantity);
                report.setQuantityToString(df.format(quantity==null?BigDecimal.ZERO:quantity));
                BigDecimal money = group.parallelStream().map(PaySalaryWageFormResponse::getMoney)
                        .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
                report.setMoney(money);
                exportReport.add(report);
            }
            if (CollectionUtil.isNotEmpty(exportReport)){
                Comparator comparing = Collator.getInstance(Locale.CHINA);
                exportReport = exportReport.stream().sorted((t1,t2)->comparing.compare(t1.getWorkCenterName(),t2.getWorkCenterName())).collect(toList());
            }
            BigDecimal moneySum = itemList.parallelStream().map(PaySalaryWageFormResponse::getMoney)
                    .reduce(BigDecimal.ZERO,BigDecimalSum::sum);
            response.put("moneySum", moneySum);
            // 获取日期范围
            itemList = itemList.stream().filter(o->o.getReportDate() != null).sorted(Comparator.comparing(l -> l.getReportDate(), Comparator.nullsFirst(Date::compareTo))).collect(toList());
            if (CollectionUtil.isNotEmpty(itemList)){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateBegin = sdf.format(itemList.get(0).getReportDate());
                String dateEnd = sdf.format(itemList.get(itemList.size()-1).getReportDate());
                response.put("dateBegin", dateBegin);
                response.put("dateEnd", dateEnd);
            }
        }
        response.put("exportReport", exportReport);
        return response;
    }

    /**
     * 计薪量申报明细报表 打印汇总导出
     */
    @Override
    public void printExcelProcessStepCompleteWage(HttpServletResponse response, PaySalaryWageFormResponse paySalaryBillItem) {
        HashMap<String, Object> formMapByStaff = new HashMap<>();
        HashMap<String, Object> formMapByWorkCenter = new HashMap<>();
        HashMap<String, Object> formMapByWorkCenter2 = new HashMap<>();
        List<PaySalaryWageFormResponse> list = new ArrayList<>();
        List<PaySalaryWageFormResponse> list2 = new ArrayList<>();
        list = this.printProcessStepCompleteWageVo(paySalaryBillItem);
        list2 = this.printProcessStepCompleteWageProcessVo(paySalaryBillItem);
        formMapByStaff = this.printProcessStepCompleteWageByStaff(list);
        formMapByWorkCenter = this.printProcessStepCompleteWageByWorkCenter(list);
        formMapByWorkCenter2 = this.printProcessStepCompleteWageByWorkCenterProcess(list2);
        List<PaySalaryWageExportReport> exportReport = new ArrayList<>();
        String dateBegin = "";
        String dateEnd = "";
        BigDecimal moneySum = BigDecimal.ZERO;
        int size = 0;
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            // sheet 1 按员工
            if (formMapByStaff.get("exportReport") != null){
                exportReport = (List<PaySalaryWageExportReport>) formMapByStaff.get("exportReport");
            }
            if (formMapByStaff.get("dateBegin") != null){
                dateBegin = (String) formMapByStaff.get("dateBegin");
            }
            if (formMapByStaff.get("dateEnd") != null){
                dateEnd = (String) formMapByStaff.get("dateEnd");
            }
            if (formMapByStaff.get("moneySum") != null){
                moneySum = (BigDecimal) formMapByStaff.get("moneySum");
            }
            size = exportReport.size();
            printExcelProcessStepCompleteWageByStaff(workbook, exportReport, dateBegin, dateEnd, moneySum, size,paySalaryBillItem.getYearmonth());
            // sheet 2 按隶属班组
            exportReport = new ArrayList<>();
            dateBegin = "";
            dateEnd = "";
            moneySum = BigDecimal.ZERO;
            size = 0;
            if (formMapByWorkCenter.get("exportReport") != null){
                exportReport = (List<PaySalaryWageExportReport>) formMapByWorkCenter.get("exportReport");
            }
            if (formMapByWorkCenter.get("dateBegin") != null){
                dateBegin = (String) formMapByWorkCenter.get("dateBegin");
            }
            if (formMapByWorkCenter.get("dateEnd") != null){
                dateEnd = (String) formMapByWorkCenter.get("dateEnd");
            }
            if (formMapByWorkCenter.get("moneySum") != null){
                moneySum = (BigDecimal) formMapByWorkCenter.get("moneySum");
            }
            size = exportReport.size();
            printExcelProcessStepCompleteWageByWorkCenter(workbook, exportReport, dateBegin, dateEnd, moneySum, size,paySalaryBillItem.getYearmonth());
            // sheet 3 按结算班组
            exportReport = new ArrayList<>();
            dateBegin = "";
            dateEnd = "";
            moneySum = BigDecimal.ZERO;
            size = 0;
            if (formMapByWorkCenter2.get("exportReport") != null){
                exportReport = (List<PaySalaryWageExportReport>) formMapByWorkCenter2.get("exportReport");
            }
            if (formMapByWorkCenter2.get("dateBegin") != null){
                dateBegin = (String) formMapByWorkCenter2.get("dateBegin");
            }
            if (formMapByWorkCenter2.get("dateEnd") != null){
                dateEnd = (String) formMapByWorkCenter2.get("dateEnd");
            }
            if (formMapByWorkCenter2.get("moneySum") != null){
                moneySum = (BigDecimal) formMapByWorkCenter2.get("moneySum");
            }
            size = exportReport.size();
            printExcelProcessStepCompleteWageByWorkCenter2(workbook, exportReport, dateBegin, dateEnd, moneySum, size,paySalaryBillItem.getYearmonth());
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment; filename=" + new String("汇总导出".getBytes("gbk"), "iso8859-1") + ".xlsx");
            workbook.write(response.getOutputStream());
        }catch (Exception e){
            throw new CustomException("导出失败");
        }

    }

    /**
     * 计薪量申报明细报表 打印汇总导出 按员工维度
     */
    public void printExcelProcessStepCompleteWageByStaff(XSSFWorkbook workbook, List<PaySalaryWageExportReport> exportReport, String dateBegin, String dateEnd, BigDecimal moneySum, int size, String yearmonth) {
        // 绘制sheet表格
        Sheet sheet = workbook.createSheet("按员工");
        sheet.setDefaultColumnWidth(20);
        // 单元格格式
        CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
        CellStyle defaultCellStyleLeft = ExcelStyleUtil.getDefaultCellStyle(workbook);
        defaultCellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
        CellStyle defaultCellStyleNo = ExcelStyleUtil.getDefaultCellStyle(workbook);
        defaultCellStyleNo.setBorderBottom(BorderStyle.NONE);
        defaultCellStyleNo.setBorderLeft(BorderStyle.NONE);
        defaultCellStyleNo.setBorderRight(BorderStyle.NONE);
        defaultCellStyleNo.setBorderTop(BorderStyle.NONE);
        // 样式 - 灰色
        XSSFColor color = new XSSFColor(new java.awt.Color(238, 236, 225));
        XSSFCellStyle cellStyleGray = ExcelStyleUtil.getXSSFCellStyle(workbook,color);
        XSSFCellStyle cellStyleGrayLeft = ExcelStyleUtil.getXSSFCellStyle(workbook,color);
        cellStyleGrayLeft.setAlignment(HorizontalAlignment.LEFT);
        // 每段标题
        String[] titleTips={"姓名","商品编码(款号)","序号","道序名称","工价","倍率","调价率","数量","金额(元)"};
        // 0 ： 第一行为空行
        // 1 ： 第二行为统计行 : 页数 、 总金额 、 日期范围
        // 2 ： 第三行才开始
        // 统计行
        int i = 0;
        Row countRow = sheet.createRow(i);
        countRow.setHeightInPoints(22);
        Cell cell01 = countRow.createCell(4);
        cell01.setCellValue("总金额：");
        cell01.setCellStyle(defaultCellStyleNo);
        Cell cell02 = countRow.createCell(5);
        cell02.setCellValue(moneySum==null?"":moneySum.toString());
        cell02.setCellStyle(defaultCellStyleNo);
        Cell cell03 = countRow.createCell(6);
        cell03.setCellValue("日期范围：");
        cell03.setCellStyle(defaultCellStyleNo);
        Cell cell04 = countRow.createCell(7);
        cell04.setCellValue(dateBegin);
        cell04.setCellStyle(defaultCellStyleNo);
        Cell cell05 = countRow.createCell(8);
        cell05.setCellValue(dateEnd);
        cell05.setCellStyle(defaultCellStyleNo);
        int item = 0;
        List<PaySalaryWageFormResponse> formList = new ArrayList<>();
        while (item != size) {
            i += 1;
            Row titleRow = sheet.createRow(i++);
            titleRow.setHeightInPoints(22);
            // 每段标题
            for (int j = 0; j < titleTips.length; j++) {
                Cell cell = titleRow.createCell(j);
                cell.setCellValue(titleTips[j]);
                cell.setCellStyle(cellStyleGray);
            }
            // 下一行每段的明细
            formList = exportReport.get(item).getFormItem();
            for (int j = 0; j < formList.size(); j++) {
                Row currentRow = sheet.createRow(i++);
                currentRow.setHeightInPoints(22);
                //隔一列
                //姓名
                Cell cell11 = currentRow.createCell(0);
                cell11.setCellValue(formList.get(j).getStaffName());
                cell11.setCellStyle(defaultCellStyle);
                //款号名
                Cell cell12 = currentRow.createCell(1);
                cell12.setCellValue(formList.get(j).getProductCode());
                cell12.setCellStyle(defaultCellStyle);
                //工序号
                Cell cell13 = currentRow.createCell(2);
                cell13.setCellValue(formList.get(j).getSortToString() == null ? "" : formList.get(j).getSortToString());
                cell13.setCellStyle(defaultCellStyle);
                //工序名称
                Cell cell14 = currentRow.createCell(3);
                cell14.setCellValue(formList.get(j).getProcessStepName());
                cell14.setCellStyle(defaultCellStyleLeft);
                //工价
                Cell cell15 = currentRow.createCell(4);
                cell15.setCellValue(formList.get(j).getPrice() == null ? "" : formList.get(j).getPrice().toString());
                cell15.setCellStyle(defaultCellStyle);
                //倍率
                Cell cell16 = currentRow.createCell(5);
                cell16.setCellValue(formList.get(j).getPriceRateToString() == null ? "" : formList.get(j).getPriceRateToString());
                cell16.setCellStyle(defaultCellStyle);
                //调价倍率
                Cell cell17 = currentRow.createCell(6);
                cell17.setCellValue(formList.get(j).getWangongPriceRateToString() == null ? "" : formList.get(j).getWangongPriceRateToString());
                cell17.setCellStyle(defaultCellStyle);
                //数量
                Cell cell18 = currentRow.createCell(7);
                cell18.setCellValue(formList.get(j).getCompleteQuantityToString() == null ? "" : formList.get(j).getCompleteQuantityToString());
                cell18.setCellStyle(defaultCellStyle);
                //金额
                Cell cell19 = currentRow.createCell(8);
                cell19.setCellValue(formList.get(j).getMoney() == null ? "" : formList.get(j).getMoney().toString());
                cell19.setCellStyle(defaultCellStyle);
            }
            // 下一行每段的结尾汇总
            Row remainRow = sheet.createRow(i++);
            remainRow.setHeightInPoints(30);
            // 每段结尾汇总
            // 员工
            Cell cell11 = remainRow.createCell(0);
            cell11.setCellValue(exportReport.get(item).getStaffName());
            cell11.setCellStyle(cellStyleGray);
            // 班组
            Cell cell12 = remainRow.createCell(1);
            cell12.setCellValue(exportReport.get(item).getWorkCenterName());
            cell12.setCellStyle(cellStyleGray);
            Cell cell13 = remainRow.createCell(2);
            cell13.setCellValue("");
            cell13.setCellStyle(cellStyleGray);
            CellRangeAddress region1 = new CellRangeAddress(i - 1, i - 1, 1, 2);
            sheet.addMergedRegion(region1);
            // 签字
            Cell cell14 = remainRow.createCell(3);
            cell14.setCellValue(yearmonth);//需要修改
            cell14.setCellStyle(cellStyleGray);
            Cell cell15 = remainRow.createCell(4);
            cell15.setCellValue("    签字：");
            cell15.setCellStyle(cellStyleGrayLeft);
            Cell cell16 = remainRow.createCell(5);
            cell16.setCellValue("");
            cell16.setCellStyle(cellStyleGrayLeft);
            CellRangeAddress region2 = new CellRangeAddress(i - 1, i - 1, 4, 5);
            sheet.addMergedRegion(region2);
            Cell cell17 = remainRow.createCell(6);
            cell17.setCellValue("小计：");
            cell17.setCellStyle(cellStyleGray);
            // 数量
            Cell cell18 = remainRow.createCell(7);
            cell18.setCellValue(exportReport.get(item).getQuantityToString() == null ? "" : exportReport.get(item).getQuantityToString());
            cell18.setCellStyle(cellStyleGray);
            // 金额
            Cell cell19 = remainRow.createCell(8);
            cell19.setCellValue(exportReport.get(item).getMoney() == null ? "" : exportReport.get(item).getMoney().toString());
            cell19.setCellStyle(cellStyleGray);
            // 换段  空一行
            i += 1;
            item += 1;
        }
    }

    /**
     * 计薪量申报明细报表 打印汇总导出 按隶属班组维度
     */
    public void printExcelProcessStepCompleteWageByWorkCenter(XSSFWorkbook workbook, List<PaySalaryWageExportReport> exportReport, String dateBegin, String dateEnd, BigDecimal moneySum, int size, String yearmonth) {
        // 绘制excel表格
        Sheet sheet = workbook.createSheet("按隶属班组");
        sheet.setDefaultColumnWidth(20);
        // 单元格格式
        CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
        CellStyle defaultCellStyleLeft = ExcelStyleUtil.getDefaultCellStyle(workbook);
        defaultCellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
        CellStyle defaultCellStyleNo = ExcelStyleUtil.getDefaultCellStyle(workbook);
        defaultCellStyleNo.setBorderBottom(BorderStyle.NONE);
        defaultCellStyleNo.setBorderLeft(BorderStyle.NONE);
        defaultCellStyleNo.setBorderRight(BorderStyle.NONE);
        defaultCellStyleNo.setBorderTop(BorderStyle.NONE);
        // 样式 - 灰色
        XSSFColor color = new XSSFColor(new java.awt.Color(238, 236, 225));
        XSSFCellStyle cellStyleGray = ExcelStyleUtil.getXSSFCellStyle(workbook,color);
        XSSFCellStyle cellStyleGrayLeft = ExcelStyleUtil.getXSSFCellStyle(workbook,color);
        cellStyleGrayLeft.setAlignment(HorizontalAlignment.LEFT);
        // 每段标题
        String[] titleTips={"班组","姓名","商品编码(款号)","序号","道序名称","工价","倍率","调价率","数量","金额(元)"};
        // 0 ： 第一行为空行
        // 1 ： 第二行为统计行 : 页数 、 总金额 、 日期范围
        // 2 ： 第三行才开始
        // 统计行
        int i = 0;
        Row countRow = sheet.createRow(i);
        countRow.setHeightInPoints(22);
        Cell cell01 = countRow.createCell(5);
        cell01.setCellValue("总金额：");
        cell01.setCellStyle(defaultCellStyleNo);
        Cell cell02 = countRow.createCell(6);
        cell02.setCellValue(moneySum==null?"":moneySum.toString());
        cell02.setCellStyle(defaultCellStyleNo);
        Cell cell03 = countRow.createCell(7);
        cell03.setCellValue("日期范围：");
        cell03.setCellStyle(defaultCellStyleNo);
        Cell cell04 = countRow.createCell(8);
        cell04.setCellValue(dateBegin);
        cell04.setCellStyle(defaultCellStyleNo);
        Cell cell05 = countRow.createCell(9);
        cell05.setCellValue(dateEnd);
        cell05.setCellStyle(defaultCellStyleNo);
        int item = 0;
        List<PaySalaryWageFormResponse> formList = new ArrayList<>();
        while (item != size) {
            i += 1;
            Row titleRow = sheet.createRow(i++);
            titleRow.setHeightInPoints(22);
            // 每段标题
            for (int j = 0; j < titleTips.length; j++) {
                Cell cell = titleRow.createCell(j);
                cell.setCellValue(titleTips[j]);
                cell.setCellStyle(cellStyleGray);
            }
            // 下一行每段的明细
            formList = exportReport.get(item).getFormItem();
            for (int j = 0; j < formList.size(); j++) {
                Row currentRow = sheet.createRow(i++);
                currentRow.setHeightInPoints(22);
                //班组
                Cell cell10 = currentRow.createCell(0);
                cell10.setCellValue(formList.get(j).getStaffWorkCenterName());
                cell10.setCellStyle(defaultCellStyle);
                //姓名
                Cell cell11 = currentRow.createCell(1);
                cell11.setCellValue(formList.get(j).getStaffName());
                cell11.setCellStyle(defaultCellStyle);
                //款号名
                Cell cell12 = currentRow.createCell(2);
                cell12.setCellValue(formList.get(j).getProductCode());
                cell12.setCellStyle(defaultCellStyle);
                //工序号
                Cell cell13 = currentRow.createCell(3);
                cell13.setCellValue(formList.get(j).getSortToString() == null ? "" : formList.get(j).getSortToString());
                cell13.setCellStyle(defaultCellStyle);
                //工序名称
                Cell cell14 = currentRow.createCell(4);
                cell14.setCellValue(formList.get(j).getProcessStepName());
                cell14.setCellStyle(defaultCellStyleLeft);
                //工价
                Cell cell15 = currentRow.createCell(5);
                cell15.setCellValue(formList.get(j).getPrice() == null ? "" : formList.get(j).getPrice().toString());
                cell15.setCellStyle(defaultCellStyle);
                //倍率
                Cell cell16 = currentRow.createCell(6);
                cell16.setCellValue(formList.get(j).getPriceRateToString() == null ? "" : formList.get(j).getPriceRateToString());
                cell16.setCellStyle(defaultCellStyle);
                //调价倍率
                Cell cell17 = currentRow.createCell(7);
                cell17.setCellValue(formList.get(j).getWangongPriceRateToString() == null ? "" : formList.get(j).getWangongPriceRateToString());
                cell17.setCellStyle(defaultCellStyle);
                //数量
                Cell cell18 = currentRow.createCell(8);
                cell18.setCellValue(formList.get(j).getCompleteQuantityToString() == null ? "" : formList.get(j).getCompleteQuantityToString());
                cell18.setCellStyle(defaultCellStyle);
                //金额
                Cell cell19 = currentRow.createCell(9);
                cell19.setCellValue(formList.get(j).getMoney() == null ? "" : formList.get(j).getMoney().toString());
                cell19.setCellStyle(defaultCellStyle);
            }
            // 下一行每段的结尾汇总
            Row remainRow = sheet.createRow(i++);
            remainRow.setHeightInPoints(30);
            // 每段结尾汇总
            // 班组
            Cell cell10 = remainRow.createCell(0);
            cell10.setCellValue(exportReport.get(item).getWorkCenterName());
            cell10.setCellStyle(cellStyleGray);
            Cell cell11 = remainRow.createCell(1);
            cell11.setCellValue("");
            cell11.setCellStyle(cellStyleGray);
            Cell cell12 = remainRow.createCell(2);
            cell12.setCellValue("");
            cell12.setCellStyle(cellStyleGray);
            CellRangeAddress region1 = new CellRangeAddress(i - 1, i - 1, 0, 2);
            sheet.addMergedRegion(region1);
            // 签字
            Cell cell13 = remainRow.createCell(3);
            cell13.setCellValue(yearmonth);
            cell13.setCellStyle(cellStyleGray);
            Cell cell14 = remainRow.createCell(4);
            cell14.setCellValue("");
            cell14.setCellStyle(cellStyleGray);
            Cell cell15 = remainRow.createCell(5);
            cell15.setCellValue("");
            cell15.setCellStyle(cellStyleGrayLeft);
            Cell cell16 = remainRow.createCell(6);
            cell16.setCellValue("");
            cell16.setCellStyle(cellStyleGrayLeft);
            CellRangeAddress region2 = new CellRangeAddress(i - 1, i - 1, 3, 6);
            sheet.addMergedRegion(region2);
            Cell cell17 = remainRow.createCell(7);
            cell17.setCellValue("小计：");
            cell17.setCellStyle(cellStyleGray);
            // 数量
            Cell cell18 = remainRow.createCell(8);
            cell18.setCellValue(exportReport.get(item).getQuantityToString() == null ? "" : exportReport.get(item).getQuantityToString());
            cell18.setCellStyle(cellStyleGray);
            // 金额
            Cell cell19 = remainRow.createCell(9);
            cell19.setCellValue(exportReport.get(item).getMoney() == null ? "" : exportReport.get(item).getMoney().toString());
            cell19.setCellStyle(cellStyleGray);
            // 换段  空一行
            i += 1;
            item += 1;
        }
    }

    /**
     * 计薪量申报明细报表 打印汇总导出 按结算班组维度
     */
    public void printExcelProcessStepCompleteWageByWorkCenter2(XSSFWorkbook workbook, List<PaySalaryWageExportReport> exportReport, String dateBegin, String dateEnd, BigDecimal moneySum, int size, String yearmonth) {
        // 绘制excel表格
        Sheet sheet = workbook.createSheet("按结算班组");
        sheet.setDefaultColumnWidth(20);
        // 单元格格式
        CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
        CellStyle defaultCellStyleLeft = ExcelStyleUtil.getDefaultCellStyle(workbook);
        defaultCellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
        CellStyle defaultCellStyleNo = ExcelStyleUtil.getDefaultCellStyle(workbook);
        defaultCellStyleNo.setBorderBottom(BorderStyle.NONE);
        defaultCellStyleNo.setBorderLeft(BorderStyle.NONE);
        defaultCellStyleNo.setBorderRight(BorderStyle.NONE);
        defaultCellStyleNo.setBorderTop(BorderStyle.NONE);
        // 样式 - 灰色
        XSSFColor color = new XSSFColor(new java.awt.Color(238, 236, 225));
        XSSFCellStyle cellStyleGray = ExcelStyleUtil.getXSSFCellStyle(workbook,color);
        XSSFCellStyle cellStyleGrayLeft = ExcelStyleUtil.getXSSFCellStyle(workbook,color);
        cellStyleGrayLeft.setAlignment(HorizontalAlignment.LEFT);
        // 每段标题
        String[] titleTips={"班组(结算)","姓名","商品编码(款号)","序号","道序名称","工价","倍率","调价率","数量","金额(元)"};
        // 0 ： 第一行为空行
        // 1 ： 第二行为统计行 : 页数 、 总金额 、 日期范围
        // 2 ： 第三行才开始
        // 统计行
        int i = 0;
        Row countRow = sheet.createRow(i);
        countRow.setHeightInPoints(22);
        Cell cell01 = countRow.createCell(5);
        cell01.setCellValue("总金额：");
        cell01.setCellStyle(defaultCellStyleNo);
        Cell cell02 = countRow.createCell(6);
        cell02.setCellValue(moneySum==null?"":moneySum.toString());
        cell02.setCellStyle(defaultCellStyleNo);
        Cell cell03 = countRow.createCell(7);
        cell03.setCellValue("日期范围：");
        cell03.setCellStyle(defaultCellStyleNo);
        Cell cell04 = countRow.createCell(8);
        cell04.setCellValue(dateBegin);
        cell04.setCellStyle(defaultCellStyleNo);
        Cell cell05 = countRow.createCell(9);
        cell05.setCellValue(dateEnd);
        cell05.setCellStyle(defaultCellStyleNo);
        int item = 0;
        List<PaySalaryWageFormResponse> formList = new ArrayList<>();
        while (item != size) {
            i += 1;
            Row titleRow = sheet.createRow(i++);
            titleRow.setHeightInPoints(22);
            // 每段标题
            for (int j = 0; j < titleTips.length; j++) {
                Cell cell = titleRow.createCell(j);
                cell.setCellValue(titleTips[j]);
                cell.setCellStyle(cellStyleGray);
            }
            // 下一行每段的明细
            formList = exportReport.get(item).getFormItem();
            for (int j = 0; j < formList.size(); j++) {
                Row currentRow = sheet.createRow(i++);
                currentRow.setHeightInPoints(22);
                //班组
                Cell cell10 = currentRow.createCell(0);
                cell10.setCellValue(formList.get(j).getWorkCenterName());
                cell10.setCellStyle(defaultCellStyle);
                //姓名
                Cell cell11 = currentRow.createCell(1);
                cell11.setCellValue(formList.get(j).getStaffName());
                cell11.setCellStyle(defaultCellStyle);
                //款号名
                Cell cell12 = currentRow.createCell(2);
                cell12.setCellValue(formList.get(j).getProductCode());
                cell12.setCellStyle(defaultCellStyle);
                //工序号
                Cell cell13 = currentRow.createCell(3);
                cell13.setCellValue(formList.get(j).getSortToString() == null ? "" : formList.get(j).getSortToString());
                cell13.setCellStyle(defaultCellStyle);
                //工序名称
                Cell cell14 = currentRow.createCell(4);
                cell14.setCellValue(formList.get(j).getProcessStepName());
                cell14.setCellStyle(defaultCellStyleLeft);
                //工价
                Cell cell15 = currentRow.createCell(5);
                cell15.setCellValue(formList.get(j).getPrice() == null ? "" : formList.get(j).getPrice().toString());
                cell15.setCellStyle(defaultCellStyle);
                //倍率
                Cell cell16 = currentRow.createCell(6);
                cell16.setCellValue(formList.get(j).getPriceRateToString() == null ? "" : formList.get(j).getPriceRateToString());
                cell16.setCellStyle(defaultCellStyle);
                //调价倍率
                Cell cell17 = currentRow.createCell(7);
                cell17.setCellValue(formList.get(j).getWangongPriceRateToString() == null ? "" : formList.get(j).getWangongPriceRateToString());
                cell17.setCellStyle(defaultCellStyle);
                //数量
                Cell cell18 = currentRow.createCell(8);
                cell18.setCellValue(formList.get(j).getCompleteQuantityToString() == null ? "" : formList.get(j).getCompleteQuantityToString());
                cell18.setCellStyle(defaultCellStyle);
                //金额
                Cell cell19 = currentRow.createCell(9);
                cell19.setCellValue(formList.get(j).getMoney() == null ? "" : formList.get(j).getMoney().toString());
                cell19.setCellStyle(defaultCellStyle);
            }
            // 下一行每段的结尾汇总
            Row remainRow = sheet.createRow(i++);
            remainRow.setHeightInPoints(30);
            // 每段结尾汇总
            // 班组
            Cell cell10 = remainRow.createCell(0);
            cell10.setCellValue(exportReport.get(item).getWorkCenterName());
            cell10.setCellStyle(cellStyleGray);
            Cell cell11 = remainRow.createCell(1);
            cell11.setCellValue("");
            cell11.setCellStyle(cellStyleGray);
            Cell cell12 = remainRow.createCell(2);
            cell12.setCellValue("");
            cell12.setCellStyle(cellStyleGray);
            CellRangeAddress region1 = new CellRangeAddress(i - 1, i - 1, 0, 2);
            sheet.addMergedRegion(region1);
            // 签字
            Cell cell13 = remainRow.createCell(3);
            cell13.setCellValue(yearmonth);
            cell13.setCellStyle(cellStyleGray);
            Cell cell14 = remainRow.createCell(4);
            cell14.setCellValue("");
            cell14.setCellStyle(cellStyleGray);
            Cell cell15 = remainRow.createCell(5);
            cell15.setCellValue("");
            cell15.setCellStyle(cellStyleGrayLeft);
            Cell cell16 = remainRow.createCell(6);
            cell16.setCellValue("");
            cell16.setCellStyle(cellStyleGrayLeft);
            CellRangeAddress region2 = new CellRangeAddress(i - 1, i - 1, 3, 6);
            sheet.addMergedRegion(region2);
            Cell cell17 = remainRow.createCell(7);
            cell17.setCellValue("小计：");
            cell17.setCellStyle(cellStyleGray);
            // 数量
            Cell cell18 = remainRow.createCell(8);
            cell18.setCellValue(exportReport.get(item).getQuantityToString() == null ? "" : exportReport.get(item).getQuantityToString());
            cell18.setCellStyle(cellStyleGray);
            // 金额
            Cell cell19 = remainRow.createCell(9);
            cell19.setCellValue(exportReport.get(item).getMoney() == null ? "" : exportReport.get(item).getMoney().toString());
            cell19.setCellStyle(cellStyleGray);
            // 换段  空一行
            i += 1;
            item += 1;
        }
    }

    /**
     * 道序计薪量统计报表
     * 根据查询字段，从“计薪量申报头表及明细表中”获取满足条件的计薪量信息，
     * 并按“工厂、商品编码(款号)、道序序号、道序工价、工价倍率、调价率、操作部门、商品工价类型、计薪完工类型”对“计薪量”进行小计。
     */
    @Override
    public List<PayProcessStepCompleteItem> selectProcessStepCompleteItemForm(PayProcessStepComplete payProcessStepComplete) {
        return payProcessStepCompleteItemMapper.selectProcessStepCompleteItemForm(payProcessStepComplete);
    }

    /**
     * 商品计件工资统计报表
     *
     * @param payProcessStepCompleteItem 请求
     * @return 商品计件工资统计报表
     */
    @Override
    public List<ProductProcessCompleteStatisticsSalary> selectCompleteStatisticsSalaryList(ProductProcessCompleteStatisticsSalary payProcessStepCompleteItem) {
        payProcessStepCompleteItem.setClientId(ApiThreadLocalUtil.get().getClientId());
        return payProcessStepCompleteItemMapper.selectCompleteStatisticsSalaryList(payProcessStepCompleteItem);
    }
}
