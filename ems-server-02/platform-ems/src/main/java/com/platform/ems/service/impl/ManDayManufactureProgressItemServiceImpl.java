package com.platform.ems.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.*;
import com.platform.ems.mapper.*;
import com.platform.ems.util.ExcelStyleUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.data.BigDecimalSum;
import com.platform.ems.util.data.ComUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IManDayManufactureProgressItemService;

import javax.servlet.http.HttpServletResponse;

import static java.util.stream.Collectors.toList;

/**
 * 生产进度日报-明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@Service
@SuppressWarnings("all")
public class ManDayManufactureProgressItemServiceImpl extends ServiceImpl<ManDayManufactureProgressItemMapper,ManDayManufactureProgressItem> implements IManDayManufactureProgressItemService {
    @Autowired
    private ManDayManufactureProgressItemMapper manDayManufactureProgressItemMapper;
    @Autowired
    private ManDayManufactureProgressDetailMapper manDayManufactureProgressDetailMapper;
    @Autowired
    private PayProductProcessStepItemMapper payProductProcessStepItemMapper;
    @Autowired
    private ManManufactureOrderProductMapper manufactureOrderProductMapper;
    @Autowired
    private ManManufactureOrderProcessMapper manManufactureOrderProcessMapper;

    private static final String TITLE = "生产进度日报-明细";

    /**
     * 查询生产进度日报-明细
     *
     * @param dayManufactureProgressItemSid 生产进度日报-明细ID
     * @return 生产进度日报-明细
     */
    @Override
    public ManDayManufactureProgressItem selectManDayManufactureProgressItemById(Long dayManufactureProgressItemSid) {
        ManDayManufactureProgressItem manDayManufactureProgressItem = manDayManufactureProgressItemMapper.selectManDayManufactureProgressItemById(dayManufactureProgressItemSid);
        // 图片视频
        manDayManufactureProgressItem.setPicturePathList(ComUtil.strToArr(manDayManufactureProgressItem.getPicturePath()));
        manDayManufactureProgressItem.setVideoPathList(ComUtil.strToArr(manDayManufactureProgressItem.getVideoPath()));
        // 操作日志
        MongodbUtil.find(manDayManufactureProgressItem);
        return manDayManufactureProgressItem;
    }

    /**
     * 查询生产进度日报-明细列表
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 生产进度日报-明细
     */
    @Override
    public List<ManDayManufactureProgressItem> selectManDayManufactureProgressItemList(ManDayManufactureProgressItem manDayManufactureProgressItem) {
        List<ManDayManufactureProgressItem> list = manDayManufactureProgressItemMapper.selectManDayManufactureProgressItemList(manDayManufactureProgressItem);
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item -> {
                // 图片视频
                item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
                item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
            });
        }
        return list;
    }

    /**
     * 查询生产进度日报-明细报表
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 生产进度日报-明细
     */
    @Override
    public List<ManDayManufactureProgressItem> selectManDayManufactureProgressItemForm(ManDayManufactureProgressItem manDayManufactureProgressItem) {
        List<ManDayManufactureProgressItem> list = manDayManufactureProgressItemMapper.selectManDayManufactureProgressItemForm(manDayManufactureProgressItem);
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item -> {
                // 图片视频
                item.setPicturePathList(ComUtil.strToArr(item.getPicturePath()));
                item.setVideoPathList(ComUtil.strToArr(item.getVideoPath()));
            });
        }
        return list;
    }

    @Override
    public int selectCount(ManDayManufactureProgressItem manDayManufactureProgressItem){
        return manDayManufactureProgressItemMapper.selectCount(manDayManufactureProgressItem);
    }

    /**
     * 新增生产进度日报-明细
     * 需要注意编码重复校验
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManDayManufactureProgressItem(ManDayManufactureProgressItem manDayManufactureProgressItem) {
        int row= manDayManufactureProgressItemMapper.insert(manDayManufactureProgressItem);
        if(row>0){
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList= BeanUtils.eq(new ManDayManufactureProgressItem(), manDayManufactureProgressItem);
            MongodbDeal.insert(manDayManufactureProgressItem.getDayManufactureProgressItemSid(), manDayManufactureProgressItem.getHandleStatus(), msgList,TITLE, null);
        }
        return row;
    }

    /**
     * 批量新增生产进度日报-明细
     * 需要注意编码重复校验
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManDayManufactureProgressItem(List<ManDayManufactureProgressItem> manDayManufactureProgressItemList) {
        int row = 0;
        if (CollectionUtil.isNotEmpty(manDayManufactureProgressItemList)) {
            row = manDayManufactureProgressItemMapper.inserts(manDayManufactureProgressItemList);
            if(row>0){
                //插入日志
                List<OperMsg> msgList;
                for (ManDayManufactureProgressItem item : manDayManufactureProgressItemList) {
                    msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(new ManDayManufactureProgressItem(), item);
                    MongodbDeal.insert(item.getDayManufactureProgressItemSid(), item.getHandleStatus(), msgList, TITLE, null);
                }
            }
        }
        return row;
    }

    /**
     * 修改生产进度日报-明细
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManDayManufactureProgressItem(ManDayManufactureProgressItem manDayManufactureProgressItem) {
        ManDayManufactureProgressItem response = manDayManufactureProgressItemMapper.selectManDayManufactureProgressItemById(manDayManufactureProgressItem.getDayManufactureProgressItemSid());
        int row = manDayManufactureProgressItemMapper.updateById(manDayManufactureProgressItem);
        if(row>0){
            //插入日志
            List<OperMsg> msgList;
            msgList = BeanUtils.eq(response, manDayManufactureProgressItem);
            MongodbDeal.update(manDayManufactureProgressItem.getDayManufactureProgressItemSid(), response.getHandleStatus(),
                    manDayManufactureProgressItem.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更生产进度日报-明细
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManDayManufactureProgressItem(ManDayManufactureProgressItem manDayManufactureProgressItem) {
        ManDayManufactureProgressItem response = manDayManufactureProgressItemMapper.selectManDayManufactureProgressItemById(manDayManufactureProgressItem.getDayManufactureProgressItemSid());
        int row=manDayManufactureProgressItemMapper.updateAllById(manDayManufactureProgressItem);
        if(row>0){
            MongodbUtil.insertUserLog(manDayManufactureProgressItem.getDayManufactureProgressItemSid(), BusinessType.CHANGE.getValue(), response,manDayManufactureProgressItem,TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产进度日报-明细
     *
     * @param dayManufactureProgressItemSids 需要删除的生产进度日报-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManDayManufactureProgressItemByIds(List<Long> dayManufactureProgressItemSids) {
        return manDayManufactureProgressItemMapper.deleteBatchIds(dayManufactureProgressItemSids);
    }

    /*
     *  1》根据“所属年月+生产订单号+商品编码+所属生产工序”从“生产进度日报“中获取符合条件的生产进度明细数据（进度日报需为“已确认”状态），
     *      将明细行的”当天实际完成量“（quantity）累加得出
     */
    @Override
    public PayProcessStepCompleteItem getQuantity(ManDayManufactureProgressItem item){
        PayProcessStepCompleteItem completeItem = new PayProcessStepCompleteItem();
        BigDecimal quantity = new BigDecimal(0);
        // 如果所属年月+生产订单号+商品编码+所属生产工序其中一个为空则返回 0,如果不是常规计薪就返回null
        if (ConstantsEms.JXCG.equals(item.getJixinWangongType())){
            if (item.getYearmonth() == null || item.getProductSid() == null
                    || item.getManufactureOrderSid() == null || item.getProcessSid() == null){
                completeItem.setCompleteQuantitySys(BigDecimal.ZERO).setCompleteQuantity(BigDecimal.ZERO);
            }else {
                quantity = new BigDecimal(manDayManufactureProgressItemMapper.getQuantity(item));
            }
            completeItem.setCompleteQuantitySys(quantity).setCompleteQuantity(quantity);
        }
        // 获取道序工价和倍率
        List<PayProductProcessStepItem> stepItemList = payProductProcessStepItemMapper.getPrice(new PayProductProcessStepItem()
                .setProductSid(item.getProductSid()).setJixinWangongType(item.getJixinWangongType())
                        .setProcessStepSid(item.getProcessStepSid())
                .setPlantSid(item.getPlantSid()).setProductPriceType(item.getProductPriceType()));
        if (CollectionUtil.isNotEmpty(stepItemList)){
            if (stepItemList.size() > 1){
                throw new BaseException("工厂(工序)+商品完工类型+商品编码+计薪完工类型维度找到多个道序工价、工价倍率，请核实");
            }
            completeItem.setPrice(stepItemList.get(0).getPrice()).setPriceRate(stepItemList.get(0).getPriceRate());
        }
        else {
            completeItem.setPrice(BigDecimal.ZERO).setPriceRate(BigDecimal.ZERO);
        }
        return completeItem;
    }

    /**
     *  班组生产日报 “工序进度”页签，新增4个清单列：实裁量、已完成量(工序)、未完成量(计划)、未完成量(实裁)，不可编辑，放置于“完成量(首批)”清单列后
     *    1》已完成量(工序)
     *      根据“工厂(工序)+班组+生产订单+生产订单工序明细sid”从“班组生产日报明细表“中（s_man_day_manufacture_progress_item）
     *      获取所有符合条件的明细行【仅获取”已确认“状态的”班组生产日报“】，然后将所有明细行的“当天实际完成量/收料量”（quantity）累加得出
     *    2》实裁量  根据“生产订单号”从“生产订单工序明细表”中获取“是否第一个工序”为“是”的工序的已完成量，已完成量计算逻辑参照第 1》点
     *    3》未完成量(计划)  = 计划产量(工序) - 已完成量(工序)
     *    4》未完成量(实裁)  = 实裁量 - 已完成量(工序)
     */
    @Override
    public List<ManDayManufactureProgressItem> getCompleteQuantity(ManDayManufactureProgress manDayManufactureProgress) {
        List<ManDayManufactureProgressItem> response = manDayManufactureProgress.getDayManufactureProgressItemList();
        if (CollectionUtil.isEmpty(response)) {
            return new ArrayList<>();
        }
        response.forEach(item->{
            try {
                ManDayManufactureProgressItem tar = manDayManufactureProgressItemMapper.getCompleteQuantity(
                        new ManDayManufactureProgressItem().setWorkCenterSid(manDayManufactureProgress.getWorkCenterSid())
                                .setPlantSid(manDayManufactureProgress.getPlantSid()).setProgressDimension(item.getProgressDimension())
                                .setManufactureOrderSid(item.getManufactureOrderSid()).setSku1Sid(item.getSku1Sid())
                                .setManufactureOrderProcessSid(item.getManufactureOrderProcessSid())
                );
                if (tar == null) {
                    tar = new ManDayManufactureProgressItem();
                }
                item.setTotalCompleteQuantity(tar.getTotalCompleteQuantity());
                item.setIsCaichuangQuantity(tar.getIsCaichuangQuantity());
                item.setPlanUnfinishedQuantity(tar.getPlanUnfinishedQuantity());
                item.setShicaiUnfinishedQuantity(tar.getShicaiUnfinishedQuantity());
            } catch (Exception e) {
                log.warn("生产订单" + item.getManufactureOrderCode() + "工序" + item.getProcessName() + "获取已完成量(工序)和实裁数错误");
                throw new BaseException("获取已完成量(工序)和实裁数错误");
            }
        });
        return response;
    }

    /**
     * 报表中心生产管理生产月进度
     * 根据查询条件，从“班组生产日报明细”数据库表中获取“是否标志阶段完成的工序”为“是”的班组生产日报明细数据及对应的生产订单数据
     *
     * @param manDayProgressMonthFormRequest 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    @Override
    public ManDayProgressMonthFormData selectManDayManufactureProgressMonthForm(ManDayProgressMonthForm manDayProgressMonthFormRequest) {
        ManDayProgressMonthFormData response = new ManDayProgressMonthFormData();
        response.setDayList(new ArrayList<>());
        List<ManDayProgressMonthForm> list = manDayManufactureProgressItemMapper.selectManDayManufactureProgressMonthForm(manDayProgressMonthFormRequest);
        List<ManDayProgressMonthFormDay> dayFormList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            Long[] orderProcessSids = list.stream().map(ManDayProgressMonthForm::getManufactureOrderProcessSid).toArray(Long[]::new);
            Map<String, List<ManDayProgressMonthFormDay>> map = new HashMap<>();
            dayFormList = manDayManufactureProgressItemMapper.selectManDayManufactureProgressMonthDayList(new ManDayProgressMonthFormDay()
                    .setYearmonthday(manDayProgressMonthFormRequest.getYearmonthday()).setManufactureOrderProcessSidList(orderProcessSids));
            if (CollectionUtil.isNotEmpty(dayFormList)) {
                // 获取当前月所有日期  LocalDate.lengthOfMonth() 获取当前日期所在月份有多少天，返回int; 强大的java1.8 LocalDate
                String monthday = manDayProgressMonthFormRequest.getYearmonth();
                monthday = monthday.substring(monthday.length()-2,monthday.length());
                List<String> dateList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(sdf.parse(manDayProgressMonthFormRequest.getYearmonth()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int num = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                for (int i = 1; i <= num; i++) {
                    if (i<10){
                        dateList.add(monthday+"/0"+i);
                    }
                    else {
                        dateList.add(monthday+"/"+i);
                    }
                }
                //List<String> dateList = dayFormList.stream().map(ManDayProgressMonthFormDay::getMonthday).distinct().collect(Collectors.toList());
                response.setDayList(dateList);
                // 生产订单工序明细
                map = dayFormList.stream().collect(Collectors.groupingBy(o -> String.valueOf(o.getManufactureOrderProcessSid())));
                // 写入
                List<ManDayProgressMonthFormDay> days = null;
                //
                for (ManDayProgressMonthForm item : list) {
                    item.setYearmonth(manDayProgressMonthFormRequest.getYearmonth());
                    days = map.get(String.valueOf(item.getManufactureOrderProcessSid()));
                    if (days != null) {
                        // 相隔不超过一年的才可以这样对月日去重
                        List<String> rowDateList = days.stream().map(ManDayProgressMonthFormDay::getMonthday).distinct().collect(Collectors.toList());
                        //没有日期的
                        List<String> result = dateList.stream().filter(id -> !rowDateList.contains(id)).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(result)) {
                            for (String dates : result) {
                                days.add(new ManDayProgressMonthFormDay().setMonthday(dates).setManufactureOrderProcessSid(item.getManufactureOrderProcessSid()));
                            }
                        }
                        days = days.stream().sorted(Comparator.comparing(ManDayProgressMonthFormDay::getMonthday)).collect(toList());
                        item.setDayList(days);
                    }
                    else {
                        item.setDayList(new ArrayList<>());
                    }
                }
            }
        }
        response.setFormList(list);
        return response;
    }

    /**
     * 导出报表中心生产管理生产月进度
     * 根据查询条件，从“班组生产日报明细”数据库表中获取“是否标志阶段完成的工序”为“是”的班组生产日报明细数据及对应的生产订单数据
     *
     * @param manDayProgressMonthFormRequest 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    @Override
    public void exportFormMonth(HttpServletResponse response, ManDayProgressMonthFormData data) throws IOException {
        if (data != null && CollectionUtil.isNotEmpty(data.getFormList())) {
            List<ManDayProgressMonthForm> formList = data.getFormList();
            try {
                XSSFWorkbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("生产月进度报表");
                sheet.setDefaultColumnWidth(20);
                XSSFColor color;
                //样式 - 灰色
                color = new XSSFColor(new java.awt.Color(238, 236, 225));
                XSSFCellStyle cellStyleGray = ExcelStyleUtil.getXSSFCellStyle(workbook,color);
                //样式 - 红色
                color = new XSSFColor(new java.awt.Color(255, 0, 0));
                XSSFCellStyle cellStyleRed = ExcelStyleUtil.getXSSFCellStyle(workbook,color);
                //样式 - 绿色
                color = new XSSFColor(new java.awt.Color(146, 208, 80));
                XSSFCellStyle cellStyleGreen = ExcelStyleUtil.getXSSFCellStyle(workbook,color);
                String[] titlesFront = {"班组","商品编码(款号)","排产批次号","计划产量(整单)","实裁量","工序(阶段完成)"};
                String[] titlesBack = {"计划完成日期","本月计划完成量","上月累计完成量","未完成量","分配量","已完成总量","当前计划完成总量","工厂","操作部门","生产订单号","商品名称","基本单位"};
                //第一行数据 (标题行)
                Row rowHead = sheet.createRow(0);
                int i = 0;
                for (i = 0; i < titlesFront.length; i++) {
                    Cell cell = rowHead.createCell(i);
                    cell.setCellValue(titlesFront[i]);
                    cell.setCellStyle(cellStyleGray);
                }
                int j = 0;
                if (CollectionUtil.isNotEmpty(data.getDayList())) {
                    for (j = 0; j < data.getDayList().size(); j++) {
                        Cell cell = rowHead.createCell(i++);
                        cell.setCellValue(data.getDayList().get(j));
                        cell.setCellStyle(cellStyleRed);
                    }
                }
                for (i = i; i < titlesBack.length; i++) {
                    Cell cell = rowHead.createCell(i);
                    cell.setCellValue(titlesBack[i]);
                    cell.setCellStyle(cellStyleGray);
                }
                CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
                //第二行数据
                Row rowSecond = sheet.createRow(1);
//                // 考勤单号
//                Cell cell0 = rowSecond.createCell(0);
//                cell0.setCellValue(request.getSalaryBillCode());
//                cell0.setCellStyle(defaultCellStyle);
//                // 所属年月
//                Cell cell1 = rowSecond.createCell(1);
//                cell1.setCellValue(request.getYearmonth());
//                cell1.setCellStyle(defaultCellStyle);



            }catch (Exception e) {

            }



        }
    }

    /**
     * 报表中心生产管理生产日进度
     * 根据查询条件，从“班组生产日报明细”数据库表中获取“是否标志阶段完成的工序”为“是”的班组生产日报明细数据及对应的生产订单数据
     *
     * @param manDayProgressMonthFormRequest 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    @Override
    public ManDayProgressMonthFormData selectManDayManufactureProgressDayForm(ManDayProgressMonthForm manDayProgressMonthFormRequest) {
        ManDayProgressMonthFormData response = new ManDayProgressMonthFormData();
        response.setDayList(new ArrayList<>());
        List<ManDayProgressMonthForm> list = manDayManufactureProgressItemMapper.selectManDayManufactureProgressMonthForm(manDayProgressMonthFormRequest);
        List<ManDayProgressMonthFormDay> dayFormList = new ArrayList<>();
        // 写入日期
        List<String> dateList = new ArrayList<>();
        // 是否显示所选的全部区间日期
        if (!ConstantsEms.NO.equals(manDayProgressMonthFormRequest.getIsRange())) {
            // 获取选择区间内所有日期
            List<LocalDate> localDateList = new ArrayList<>();
            // 开始时间必须小于结束时间
            LocalDate startDate = LocalDate.parse(manDayProgressMonthFormRequest.getYearmonthdayBegin());
            LocalDate endDate = LocalDate.parse(manDayProgressMonthFormRequest.getYearmonthdayEnd());
            while (startDate.isBefore(endDate)) {
                localDateList.add(startDate);
                startDate = startDate.plusDays(1);
            }
            localDateList.add(endDate);
            if (localDateList.size() > 0) {
                // localDateList.get(0).format(DateTimeFormatter.ISO_LOCAL_DATE);
                for (LocalDate item : localDateList) {
                    int day = item.getDayOfMonth();
                    int month = item.getMonthValue();
                    int year = item.getYear();
                    String yearString = String.valueOf(year);
                    yearString = yearString.substring(2);
                    String monthString = String.valueOf(month);
                    if (month < 10) {
                        monthString = "0" + monthString;
                    }
                    if (day < 10) {
                        dateList.add(yearString+"/"+monthString+"/0"+String.valueOf(day));
                    }
                    else {
                        dateList.add(yearString+"/"+monthString+"/"+String.valueOf(day));
                    }
                }
            }
            //List<String> dateList = dayFormList.stream().map(ManDayProgressMonthFormDay::getMonthday).distinct().collect(Collectors.toList());
            response.setDayList(dateList);
        }
        if (CollectionUtil.isNotEmpty(list)) {
            // 新优化 分配量的取值
                /*
                 * 未获取到数据，根据如下逻辑获取：
                   2.1 通过“班组生产日报明细中的生产订单号、明细行中工序所属的“操作部门”，从“生产订单-工序表”中获取到该“生产订单、操作部门”下的所有工序明细行sid（manufacture_order_process_sid）
                   2.2 对2.1获取到的所有工序明细行sid，按“生产订单工序sid  + 班组生产日报明细sku1颜色sid  + 班组生产日报明细表的班组”维度，获取处理状态是“已确认”的周计划的“分配量”的值
                   2.3 若2.2 获取到多个“分配量”的值，则获取”周计划日期“最大的”分配量“的值
                   2.4 若2.3 获取到多个“分配量”的值，则随机取值其中1个
                 */
            for (ManDayProgressMonthForm item : list) {
                if (item.getQuantityFenpei() == null) {
                    List<ManManufactureOrderProcess> processList = manManufactureOrderProcessMapper.selectManManufactureOrderProcessList(
                            new ManManufactureOrderProcess().setManufactureOrderSid(item.getManufactureOrderSid())
                                    .setDepartmentSid(item.getDepartmentSid())
                    );
                    if (CollectionUtil.isNotEmpty(processList)) {
                        Long[] manOrderProSids = processList.stream().map(ManManufactureOrderProcess::getManufactureOrderProcessSid).toArray(Long[]::new);
                        List<ManDayManufactureProgressItem> newFenpei = manDayManufactureProgressItemMapper.getQuantityFenpei(new ManDayManufactureProgressItem()
                                .setManufactureOrderProcessSidList(manOrderProSids)
                                .setWorkCenterSid(item.getWorkCenterSid())
                                .setSku1Sid(item.getSku1Sid()).setSku1SidIsNull(ConstantsEms.YES));
                        if (CollectionUtil.isNotEmpty(newFenpei)) {
                            newFenpei = newFenpei.stream().sorted(Comparator.comparing(ManDayManufactureProgressItem::getDateStart).reversed()).collect(Collectors.toList());
                            item.setQuantityFenpei(newFenpei.get(0).getQuantityFenpei());
                        }
                    }
                }
            }
            Long[] orderProcessSids = list.stream().map(ManDayProgressMonthForm::getManufactureOrderProcessSid).toArray(Long[]::new);
            Map<String, List<ManDayProgressMonthFormDay>> map = new HashMap<>();
            dayFormList = manDayManufactureProgressItemMapper.selectManDayManufactureProgressMonthDayList(new ManDayProgressMonthFormDay()
                    .setYearmonthdayBegin(manDayProgressMonthFormRequest.getYearmonthdayBegin())
                    .setYearmonthdayEnd(manDayProgressMonthFormRequest.getYearmonthdayEnd())
                    .setManufactureOrderProcessSidList(orderProcessSids));
            if (CollectionUtil.isNotEmpty(dayFormList)) {
                // 是否显示所选的全部区间日期
                if (ConstantsEms.NO.equals(manDayProgressMonthFormRequest.getIsRange())) {
                    dateList = dayFormList.stream().map(ManDayProgressMonthFormDay::getYearmonthday).distinct().collect(Collectors.toList());
                    dateList = dateList.stream().sorted().collect(Collectors.toList());
                    response.setDayList(dateList);
                }
                // 生产订单工序明细
                map = dayFormList.stream().collect(Collectors.groupingBy(o -> String.valueOf(o.getManufactureOrderProcessSid()) +
                        "-" + String.valueOf(o.getWorkCenterSid()) + "-" + String.valueOf(o.getSku1Sid())));
                // 写入
                List<ManDayProgressMonthFormDay> days = null;
                //
                for (ManDayProgressMonthForm item : list) {
                    days = map.get(String.valueOf(item.getManufactureOrderProcessSid()) +
                            "-" + String.valueOf(item.getWorkCenterSid()) + "-" + String.valueOf(item.getSku1Sid()));
                    if (days != null) {
                        // 相隔不超过一年的才可以这样对月日去重
                        List<String> rowDateList = days.stream().map(ManDayProgressMonthFormDay::getYearmonthday).distinct().collect(Collectors.toList());
                        //没有日期的
                        List<String> result = dateList.stream().filter(id -> !rowDateList.contains(id)).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(result)) {
                            for (String dates : result) {
                                days.add(new ManDayProgressMonthFormDay().setYearmonthday(dates).setManufactureOrderProcessSid(item.getManufactureOrderProcessSid()));
                            }
                        }
                        days = days.stream().sorted(Comparator.comparing(ManDayProgressMonthFormDay::getYearmonthday)).collect(toList());
                        item.setDayList(days);
                    }
                    else {
                        days = new ArrayList<>();
                        if (CollectionUtil.isNotEmpty(dateList)) {
                            for (String dates : dateList) {
                                days.add(new ManDayProgressMonthFormDay().setYearmonthday(dates));
                            }
                        }
                        item.setDayList(days);
                    }
                }
            }
        }
        response.setFormList(list);
        if (CollectionUtil.isNotEmpty(list)) {
            BigDecimal sum1 = list.stream().map(ManDayProgressMonthForm::getTotalCompleteQuantityBefore).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            BigDecimal sum2 = list.stream().map(ManDayProgressMonthForm::getTotalCompleteQuantityIn).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            BigDecimal sum3 = list.stream().map(ManDayProgressMonthForm::getTotalCompleteQuantityAdd).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            BigDecimal sum4 = list.stream().map(ManDayProgressMonthForm::getTotalCompleteQuantity).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            BigDecimal sum5 = list.stream().map(ManDayProgressMonthForm::getCurrentTotalPlanQuantity).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            BigDecimal sum6 = list.stream().map(ManDayProgressMonthForm::getWeiCompleteQuantity).reduce(BigDecimal.ZERO, BigDecimalSum::sum);
            response.setTotalCompleteQuantityBeforeSum(sum1).setTotalCompleteQuantityInSum(sum2).setTotalCompleteQuantityAddSum(sum3)
                    .setTotalCompleteQuantitySum(sum4).setCurrentTotalPlanQuantitySum(sum5).setWeiCompleteQuantitySum(sum6);
        }
        return response;
    }

    /**
     * 导出报表中心生产管理生产日进度
     * 根据查询条件，从“班组生产日报明细”数据库表中获取“是否标志阶段完成的工序”为“是”的班组生产日报明细数据及对应的生产订单数据
     *
     * @param manDayProgressMonthFormRequest 生产进度日报-明细
     * @return 生产进度日报-明细集合
     */
    @Override
    public void exportFormDay(HttpServletResponse response, ManDayProgressMonthForm manDayProgressMonthFormRequest) throws IOException {

    }

    /**
     * 勾选明细进入尺码完工明细 默认带出 生产订单 根据外层所选择明细行的“生产订单号+商品编码+颜色”，从生产订单产品明细表中，自动带出商品的颜色&尺码明细
     *
     * @param manDayManufactureProgressItem 生产进度日报-明细
     * @return 生产进度日报-明细
     */
    @Override
    public List<ManDayManufactureProgressDetail> getManDayManufactureProgressDetail(ManDayManufactureProgressItem manDayManufactureProgressItem) {
        // 已维护的尺码完工明细
        List<ManDayManufactureProgressDetail> detailList = new ArrayList<>();
        Map<String, ManDayManufactureProgressDetail> map = new HashMap<>();
        if (CollectionUtil.isNotEmpty(manDayManufactureProgressItem.getProgressDetailList())) {
            detailList = manDayManufactureProgressItem.getProgressDetailList();
            map = detailList.stream().collect(Collectors.toMap(o->String.valueOf(o.getSku1Sid()+"-"+String.valueOf(o.getSku2Sid())), Function.identity(), (t1,t2) -> t1));
        }
        // 得到生产订单产品明细 的列表
        List<ManManufactureOrderProduct> productList = manufactureOrderProductMapper.selectManManufactureOrderProductList(new ManManufactureOrderProduct()
                .setManufactureOrderSid(manDayManufactureProgressItem.getManufactureOrderSid())
                .setMaterialSid(manDayManufactureProgressItem.getMaterialSid())
                .setSku1Sid(manDayManufactureProgressItem.getSku1Sid()));
        if (CollectionUtil.isNotEmpty(productList)) {
            for (ManManufactureOrderProduct item : productList) {
                if (!map.containsKey(String.valueOf(item.getSku1Sid()+"-"+String.valueOf(item.getSku2Sid())))) {
                    detailList.add(new ManDayManufactureProgressDetail().setSku1Sid(item.getSku1Sid()).setSort1(item.getSort1())
                            .setSku1Type(item.getSku1Type()).setSku1Name(item.getSku1Name()).setSort2(item.getSort2())
                            .setSku2Sid(item.getSku2Sid()).setSku2Type(item.getSku2Type()).setSku2Name(item.getSku2Name()));
                    map.put(String.valueOf(item.getSku1Sid()+"-"+String.valueOf(item.getSku2Sid())), null);
                }
            }
        }
        if (CollectionUtil.isEmpty(detailList)) {
            return detailList;
        }
        detailList = detailList.stream().sorted(
                Comparator.comparing(ManDayManufactureProgressDetail::getSort1, Comparator.nullsLast(BigDecimal::compareTo))
                        .thenComparing(ManDayManufactureProgressDetail::getSku1Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                        .thenComparing(ManDayManufactureProgressDetail::getSort2, Comparator.nullsLast(BigDecimal::compareTo))
                        .thenComparing(ManDayManufactureProgressDetail::getSku2Name, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
        ).collect(toList());
        return detailList;
    }

    /**
     * 生产日进度报表查看详情的 行转列
     *
     * @param manDayProgressMonthForm 生产日进度报表 行数据
     * @return 生产进度日报-明细
     */
    @Override
    public ManDayProgressDetailTable getManDayManufactureProgressDetailTable(ManDayProgressMonthForm manDayProgressMonthForm) {
        SimpleDateFormat formatter = new SimpleDateFormat( "yy/MM/dd");
        // 返回参数
        ManDayProgressDetailTable table = new ManDayProgressDetailTable();
        BeanCopyUtils.copyProperties(manDayProgressMonthForm, table);
        // 初始化行
        table.setItemList(new ArrayList<>());
        // 写入日期  年/月/日
        List<String> documentDateList = new ArrayList<>();
        // 查询 班组生产日报明细 带有主表的 汇报日期
        List<ManDayManufactureProgressItem> itemList = manDayManufactureProgressItemMapper.selectManDayManufactureProgressItemForm(
                new ManDayManufactureProgressItem().setSku1Sid(manDayProgressMonthForm.getSku1Sid())
                        .setWorkCenterSid(manDayProgressMonthForm.getWorkCenterSid()).setHandleStatus(ConstantsEms.CHECK_STATUS)
                        .setManufactureOrderProcessSid(manDayProgressMonthForm.getManufactureOrderProcessSid())
        );
        // 是否显示所选的全部区间日期
        if (!ConstantsEms.NO.equals(manDayProgressMonthForm.getIsRange())) {
            documentDateList = itemList.stream().map(o->{
                if (o.getDocumentDate() != null) {
                    return formatter.format(o.getDocumentDate());
                }
                else {
                    return null;
                }
            }).distinct().collect(toList());
            documentDateList.removeIf(Objects::isNull);
            documentDateList = documentDateList.stream().sorted().collect(toList());
            table.setDaysList(documentDateList);
        }
        // 获取行数据
        if (CollectionUtil.isNotEmpty(itemList)) {
            List<ManDayProgressDetailTableItem> tableItemList = new ArrayList<>();
            // 因为 所选行 有 生产订单工序明细sid，所以生产订单是同一个
            Long manOrderSid = itemList.get(0).getManufactureOrderSid();
            // 获取 生产订单产品明细中的尺码
            List<ManManufactureOrderProduct> productList = manufactureOrderProductMapper.selectManManufactureOrderProductList(
                    new ManManufactureOrderProduct().setManufactureOrderSid(manOrderSid));
            // 获取尺码的计划产量(商品)
            // 显示查询出来的生产订单的“商品明细“页签中对应尺码的计划产量；
            // 若表头的“颜色”字段有值，则显示对应“颜色+尺码”的计划产量；
            // 若表头的“颜色”字段没有值，则显示对应“尺码”下所有颜色的计划产量之和；
            Map<Long, BigDecimal> chimaMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(productList)) {
                for (int i = 0; i < productList.size(); i++) {
                    if (ConstantsEms.SKUTYP_CM.equals(productList.get(i).getSku1Type())) {
                        if (!chimaMap.containsKey(productList.get(i).getSku1Sid())) {
                            // 如果颜色有值
                            if (manDayProgressMonthForm.getSku1Sid() != null) {
                                if (manDayProgressMonthForm.getSku1Sid().equals(productList.get(i).getSku2Sid())) {
                                    chimaMap.put(productList.get(i).getSku1Sid(), productList.get(i).getQuantity());
                                }
                                else {
                                    chimaMap.put(productList.get(i).getSku1Sid(), BigDecimal.ZERO);
                                }
                            }
                            else {
                                chimaMap.put(productList.get(i).getSku1Sid(), productList.get(i).getQuantity());
                            }
                            tableItemList.add(new ManDayProgressDetailTableItem().setSku2Sid(productList.get(i).getSku1Sid())
                                    .setSku2Name(productList.get(i).getSku1Name()));
                        }
                        else {
                            // 如果颜色有值
                            if (manDayProgressMonthForm.getSku1Sid() != null) {
                                if (manDayProgressMonthForm.getSku1Sid().equals(productList.get(i).getSku2Sid())) {
                                    BigDecimal count = chimaMap.get(productList.get(i).getSku1Sid()).add(productList.get(i).getQuantity());
                                    chimaMap.put(productList.get(i).getSku1Sid(), count);
                                }
                            }
                            else {
                                BigDecimal count = chimaMap.get(productList.get(i).getSku1Sid()).add(productList.get(i).getQuantity());
                                chimaMap.put(productList.get(i).getSku1Sid(), count);
                            }
                        }
                    }
                    else if (ConstantsEms.SKUTYP_CM.equals(productList.get(i).getSku2Type())) {
                        if (!chimaMap.containsKey(productList.get(i).getSku2Sid())) {
                            // 如果颜色有值
                            if (manDayProgressMonthForm.getSku1Sid() != null) {
                                if (manDayProgressMonthForm.getSku1Sid().equals(productList.get(i).getSku1Sid())) {
                                    chimaMap.put(productList.get(i).getSku2Sid(), productList.get(i).getQuantity());
                                }
                                else {
                                    chimaMap.put(productList.get(i).getSku2Sid(), BigDecimal.ZERO);
                                }
                            }
                            else {
                                chimaMap.put(productList.get(i).getSku2Sid(), productList.get(i).getQuantity());
                            }
                            tableItemList.add(new ManDayProgressDetailTableItem().setSku2Sid(productList.get(i).getSku2Sid())
                                    .setSku2Name(productList.get(i).getSku2Name()));
                        }
                        else {
                            // 如果颜色有值
                            if (manDayProgressMonthForm.getSku1Sid() != null) {
                                if (manDayProgressMonthForm.getSku1Sid().equals(productList.get(i).getSku1Sid())) {
                                    BigDecimal count = chimaMap.get(productList.get(i).getSku2Sid()).add(productList.get(i).getQuantity());
                                    chimaMap.put(productList.get(i).getSku2Sid(), count);
                                }
                            }
                            else {
                                BigDecimal count = chimaMap.get(productList.get(i).getSku2Sid()).add(productList.get(i).getQuantity());
                                chimaMap.put(productList.get(i).getSku2Sid(), count);
                            }

                        }
                    }
                }
            }
            // 班组生产日报明细sid
            Long[] dayManProItemSidList = itemList.stream().map(ManDayManufactureProgressItem::getDayManufactureProgressItemSid).toArray(Long[]::new);
            // 查询完工明细
            List<ManDayManufactureProgressDetail> detailList = manDayManufactureProgressDetailMapper.selectManDayManufactureProgressDetailList(
                    new ManDayManufactureProgressDetail().setDayManufactureProgressItemSidList(dayManProItemSidList));
            // 是否显示所选的全部区间日期
            if (ConstantsEms.NO.equals(manDayProgressMonthForm.getIsRange())) {
                documentDateList = detailList.stream().map(o->{
                    if (o.getDocumentDate() != null) {
                        return formatter.format(o.getDocumentDate());
                    }
                    else {
                        return null;
                    }
                }).distinct().collect(toList());
                documentDateList.removeIf(Objects::isNull);
                documentDateList = documentDateList.stream().sorted().collect(toList());
                table.setDaysList(documentDateList);
            }
            // 记录某一日期的数量
            Map<String, BigDecimal> quantityMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(detailList)) {
                // 完工明细 用 日期 + 尺码 + 颜色 作为 map
                Map<String, List<ManDayManufactureProgressDetail>> map = new HashMap<>();
                if (manDayProgressMonthForm.getSku1Sid() != null) {
                    map = detailList.stream()
                            .collect(Collectors.groupingBy(o -> String.valueOf(formatter.format(o.getDocumentDate()))+"-"+String.valueOf(o.getSku2Sid())
                                    +"-"+String.valueOf(o.getSku1Sid())));
                }
                // 如果没颜色
                else {
                    map = detailList.stream()
                            .collect(Collectors.groupingBy(o -> String.valueOf(formatter.format(o.getDocumentDate()))+"-"+String.valueOf(o.getSku2Sid())
                                    +"-null"));
                }
                for (int i = 0; i < tableItemList.size(); i++) {
                    // 赋值尺码的计划产量
                    tableItemList.get(i).setPlanQuantity(chimaMap.get(tableItemList.get(i).getSku2Sid()));
                    // 每行的 完工量列表
                    List<ManDayProgressDetailTableQuantity> quantityList = new ArrayList<>();
                    for (int j = 0; j < documentDateList.size(); j++) {
                        ManDayProgressDetailTableQuantity quantity = new ManDayProgressDetailTableQuantity();
                        quantity.setQuantity(null);
                        // 列小计 也就是 日期的小计
                        BigDecimal decimal = BigDecimal.ZERO;
                        if (quantityMap.containsKey(documentDateList.get(j))) {
                            decimal = quantityMap.get(documentDateList.get(j));
                        }
                        else {
                            quantityMap.put(documentDateList.get(j), decimal);
                        }
                        // 根据 尺码 + 年月日 匹配 完工明细
                        String key = String.valueOf(documentDateList.get(j))+"-"+String.valueOf(tableItemList.get(i).getSku2Sid())
                                +"-"+String.valueOf(manDayProgressMonthForm.getSku1Sid());
                        if (map.containsKey(key)) {
                            List<ManDayManufactureProgressDetail> details = map.get(key);
                            if (CollectionUtil.isNotEmpty(details)) {
                                quantity.setQuantity(details.stream().filter(o->o.getQuantity() != null)
                                        .map(ManDayManufactureProgressDetail::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add));
                                decimal = decimal.add(quantity.getQuantity());
                                quantityMap.put(documentDateList.get(j), decimal);
                            }
                        }
                        quantity.setMonthday(documentDateList.get(j));
                        quantityList.add(quantity);
                    }
                    // 行小计
                    BigDecimal sum = quantityList.stream().filter(o->o.getQuantity() != null).map(ManDayProgressDetailTableQuantity::getQuantity)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    tableItemList.get(i).setTotalQuantity(sum);
                    // 每行的未完成量
                    tableItemList.get(i).setWeiQuantity(tableItemList.get(i).getPlanQuantity().subtract(sum));
                    // 完工量列表
                    tableItemList.get(i).setQuantityList(quantityList);
                }
            }
            else {
                for (int i = 0; i < tableItemList.size(); i++) {
                    // 赋值尺码的计划产量
                    tableItemList.get(i).setPlanQuantity(chimaMap.get(tableItemList.get(i).getSku2Sid()));
                    // 每行的 完工量列表
                    List<ManDayProgressDetailTableQuantity> quantityList = new ArrayList<>();
                    tableItemList.get(i).setQuantityList(quantityList);
                    // 每行的小计
                    tableItemList.get(i).setTotalQuantity(BigDecimal.ZERO);
                    // 每行的未完成量
                    tableItemList.get(i).setWeiQuantity(tableItemList.get(i).getPlanQuantity());
                }
            }
            // 行排序
            if (CollectionUtil.isNotEmpty(tableItemList)) {
                tableItemList = tableItemList.stream().sorted(Comparator.comparing(ManDayProgressDetailTableItem::getSku2Name, Comparator.nullsLast(String::compareTo))
                        .thenComparing(ManDayProgressDetailTableItem::getSku2Name, Collator.getInstance(Locale.CHINA))).collect(toList());
                // 小计列合计
                BigDecimal totalSum = tableItemList.stream().filter(o->o.getTotalQuantity() != null).map(ManDayProgressDetailTableItem::getTotalQuantity)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                // 计划产量商品列合计
                BigDecimal planSum = tableItemList.stream().filter(o->o.getTotalQuantity() != null).map(ManDayProgressDetailTableItem::getPlanQuantity)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                // 未完成量商品列合计
                BigDecimal weiSum = tableItemList.stream().filter(o->o.getTotalQuantity() != null).map(ManDayProgressDetailTableItem::getWeiQuantity)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                ManDayProgressDetailTableItem last = new ManDayProgressDetailTableItem();
                last.setSku2Name("合计").setTotalQuantity(totalSum).setPlanQuantity(planSum).setWeiQuantity(weiSum);
                List<ManDayProgressDetailTableQuantity> lastQuantityList = new ArrayList<>();
                for (int i = 0; i < documentDateList.size(); i++) {
                    ManDayProgressDetailTableQuantity quantity = new ManDayProgressDetailTableQuantity();
                    quantity.setMonthday(documentDateList.get(i)).setQuantity(quantityMap.get(documentDateList.get(i)));
                    lastQuantityList.add(quantity);
                }
                last.setQuantityList(lastQuantityList);
                tableItemList.add(last);
            }
            table.setItemList(tableItemList);
        }
        return table;
    }

    /**
     * 对明细 sku1为尺码 的 进行关于尺码排序
     * @param itemList
     */
    public List<ManDayManufactureProgressDetail> sortBySku1Cm(List<ManDayManufactureProgressDetail> itemList){
        itemList.forEach(li -> {
            String skuName = li.getSku1Name();
            String[] nameSplit = skuName.split("/");
            if (nameSplit.length == 1) {
                li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
            } else {
                String[] name2split = nameSplit[1].split("\\(");
                if (name2split.length == 2) {
                    li.setSecondSort(name2split[0].replaceAll("[a-zA-Z]", ""));

                    li.setThirdSort(name2split[1].replaceAll("[a-zA-Z]", ""));
                } else {
                    li.setSecondSort(nameSplit[1].replaceAll("[a-zA-Z]", ""));
                }
                li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
            }
        });
        List<ManDayManufactureProgressDetail> allList = new ArrayList<>();
        List<ManDayManufactureProgressDetail> allThirdList = new ArrayList<>();
        List<ManDayManufactureProgressDetail> sortThird = itemList.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
        List<ManDayManufactureProgressDetail> sortThirdNull = itemList.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
        sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
        allThirdList.addAll(sortThird);
        allThirdList.addAll(sortThirdNull);
        List<ManDayManufactureProgressDetail> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
        sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
        List<ManDayManufactureProgressDetail> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
        allList.addAll(sort);
        allList.addAll(sortNull);
        itemList = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))).collect(Collectors.toList());
        return itemList;
    }

    /**
     * 对明细 sku2为尺码 的 进行关于尺码排序
     * @param itemList
     */
    public List<ManDayManufactureProgressDetail> sortBySku2Cm(List<ManDayManufactureProgressDetail> itemList){
        itemList.forEach(li -> {
            String skuName = li.getSku2Name();
            String[] nameSplit = skuName.split("/");
            if (nameSplit.length == 1) {
                li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
            } else {
                String[] name2split = nameSplit[1].split("\\(");
                if (name2split.length == 2) {
                    li.setSecondSort(name2split[0].replaceAll("[a-zA-Z]", ""));

                    li.setThirdSort(name2split[1].replaceAll("[a-zA-Z]", ""));
                } else {
                    li.setSecondSort(nameSplit[1].replaceAll("[a-zA-Z]", ""));
                }
                li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
            }
        });
        List<ManDayManufactureProgressDetail> allList = new ArrayList<>();
        List<ManDayManufactureProgressDetail> allThirdList = new ArrayList<>();
        List<ManDayManufactureProgressDetail> sortThird = itemList.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
        List<ManDayManufactureProgressDetail> sortThirdNull = itemList.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
        sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
        allThirdList.addAll(sortThird);
        allThirdList.addAll(sortThirdNull);
        List<ManDayManufactureProgressDetail> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
        sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
        List<ManDayManufactureProgressDetail> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
        allList.addAll(sort);
        allList.addAll(sortNull);
        itemList = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))).collect(Collectors.toList());
        return itemList;
    }
}
