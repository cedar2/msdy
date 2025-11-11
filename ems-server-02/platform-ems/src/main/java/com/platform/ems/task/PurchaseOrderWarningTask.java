package com.platform.ems.task;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.api.service.RemoteUserService;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsInventory;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.PurPurchaseOrder;
import com.platform.ems.domain.PurPurchaseOrderItem;
import com.platform.ems.domain.RepBusinessRemindPo;
import com.platform.ems.mapper.BasVendorMapper;
import com.platform.ems.mapper.PurPurchaseOrderItemMapper;
import com.platform.ems.mapper.PurPurchaseOrderMapper;
import com.platform.ems.mapper.RepBusinessRemindPoMapper;
import com.platform.ems.service.IPurPurchaseOrderItemService;
import com.platform.system.domain.SysOverdueBusiness;
import com.platform.system.domain.SysToexpireBusiness;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import com.platform.system.mapper.SysOverdueBusinessMapper;
import com.platform.system.mapper.SysToexpireBusinessMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 采购订单到期&&逾期预警
 *
 * @author yangqz
 */
@EnableScheduling
@Component
@SuppressWarnings("all")
@Slf4j
public class PurchaseOrderWarningTask {
    @Autowired
    private PurPurchaseOrderMapper purPurchaseOrderMapper;
    @Autowired
    private PurPurchaseOrderItemMapper purPurchaseOrderItemMapper;
    @Autowired
    private SysToexpireBusinessMapper sysToexpireBusinessMapper;
    @Autowired
    private SysOverdueBusinessMapper sysOverdueBusinessMapper;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private RepBusinessRemindPoMapper repBusinessRemindPoMapper;
    @Autowired
    private IPurPurchaseOrderItemService purPurchaseOrderItemService;
    @Autowired
    private SysDefaultSettingClientMapper sysDefaultSettingClientMapper;


    @Scheduled(cron = "00 26 01 * * * ")
    public void earlyWarning() {

        log.info("=====>删除即将到期已逾期旧数据");
        // 删除旧的缓存表数据
        repBusinessRemindPoMapper.delete(new RepBusinessRemindPo());
        // 删除采购订单即将到期预警通知表的数据
        sysToexpireBusinessMapper.delete(new SysToexpireBusiness().setTableName(ConstantsEms.TABLE_PURCHASE_ORDER));
        // 删除采购订单已逾期预警通知表的数据
        sysOverdueBusinessMapper.delete(new SysOverdueBusiness().setTableName(ConstantsEms.TABLE_PURCHASE_ORDER));
        // 待写入缓存表的数据
        List<RepBusinessRemindPo> repBusinessRemindPoList = new ArrayList<>();
        // 待写入即将到期预警通知表的数据
        List<SysToexpireBusiness> toexpireBusinessList = new ArrayList<>();
        // 待写入已逾期预警通知表的数据
        List<SysOverdueBusiness> overdueBusinessList = new ArrayList<>();
        log.info("=====>开始查询即将到期采购订单");
        // 获取即将到期采购订单明细
        List<PurPurchaseOrderItem> purPurchaseOrderItemToexpireList = purPurchaseOrderItemMapper.getToexpireBusiness();
        // 处理即将到期数据
        if (CollectionUtil.isNotEmpty(purPurchaseOrderItemToexpireList)) {
            // 计算库存数据
            try {
                purPurchaseOrderItemToexpireList = purPurchaseOrderItemService.handleIndex(purPurchaseOrderItemToexpireList);
            } catch (Exception e) {
                log.error("方法 handleIndex 报错，可能会没有获取到出入库量和交货量等数据");
            }
            purPurchaseOrderItemToexpireList = purPurchaseOrderItemService.handleIndex(purPurchaseOrderItemToexpireList);
            // 判断订单是否已发送预警提醒
            Map<Long, String> toexpireMap = new HashMap<>();
            Map<Long, List<PurPurchaseOrderItem>> itemListMap = purPurchaseOrderItemToexpireList.stream()
                    .collect(Collectors.groupingBy(o -> o.getPurchaseOrderSid()));
            // 遍历
            purPurchaseOrderItemToexpireList.forEach(item -> {
                // 写入缓存表
                RepBusinessRemindPo repBusinessRemindPo = new RepBusinessRemindPo();
                BeanCopyUtils.copyProperties(item, repBusinessRemindPo);
                repBusinessRemindPo.setRemindType(ConstantsEms.JJDQ);
                repBusinessRemindPo.setPurchaseOrderCode(Long.parseLong(item.getPurchaseOrderCode()));
                repBusinessRemindPo.setQuantityDingd(item.getQuantity());
                repBusinessRemindPo.setQuantityWeijh(item.getPartQuantity());
                repBusinessRemindPoList.add(repBusinessRemindPo);
                // 对应订单主表只需要发送一个预警提醒
                if (!toexpireMap.containsKey(item.getPurchaseOrderSid()) || toexpireMap.get(item.getPurchaseOrderSid()) == null) {
                    toexpireMap.put(item.getPurchaseOrderSid(), "1");
                    // 存入待发送预警通知
                    SysToexpireBusiness toexpireBusiness = new SysToexpireBusiness();
                    toexpireBusiness.setTitle(item.getVendorShortName() + ",  采购订单" + item.getPurchaseOrderCode() + "存在"
                                    + itemListMap.get(item.getPurchaseOrderSid()).size() + "行数据即将到期，请您及时关注")
                            .setTableName(ConstantsEms.TABLE_PURCHASE_ORDER)
                            .setDocumentSid(item.getPurchaseOrderSid())
                            .setDocumentCode(item.getPurchaseOrderCode().toString())
                            .setExpiredDate(item.getContractDate())
                            .setNoticeDate(new Date()).setClientId(item.getClientId())
                            .setUserId(item.getCreatorAccountId());
                    if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(item.getMaterialCategory())) {
                        toexpireBusiness.setMenuId(ConstantsWorkbench.purchase_order_wl);
                    } else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(item.getMaterialCategory())) {
                        toexpireBusiness.setMenuId(ConstantsWorkbench.purchase_order_sp);
                    }
                    toexpireBusinessList.add(toexpireBusiness);
                }
            });
        }
        log.info("=====>即将到期采购订单处理完成");
        log.info("=====>开始查询已逾期采购订单");
        // 获取已逾期采购订单明细
        List<PurPurchaseOrderItem> purPurchaseOrderItemOverdueList = purPurchaseOrderItemMapper.getOverdueBusiness();
        // 处理已逾期数据
        if (CollectionUtil.isNotEmpty(purPurchaseOrderItemOverdueList)) {
            // 计算库存数据
            try {
                purPurchaseOrderItemOverdueList = purPurchaseOrderItemService.handleIndex(purPurchaseOrderItemOverdueList);
            } catch (Exception e) {
                log.error("方法 handleIndex 报错，可能会没有获取到出入库量和发货量等数据");
            }
            // 判断订单是否已发送预警提醒
            Map<Long, String> toexpireMap = new HashMap<>();
            Map<Long, List<PurPurchaseOrderItem>> itemListMap = purPurchaseOrderItemOverdueList.stream()
                    .collect(Collectors.groupingBy(o -> o.getPurchaseOrderSid()));
            // 遍历
            purPurchaseOrderItemOverdueList.forEach(item -> {
                // 写入缓存表
                RepBusinessRemindPo repBusinessRemindPo = new RepBusinessRemindPo();
                BeanCopyUtils.copyProperties(item, repBusinessRemindPo);
                repBusinessRemindPo.setRemindType(ConstantsEms.YYQ);
                repBusinessRemindPo.setPurchaseOrderCode(Long.parseLong(item.getPurchaseOrderCode()));
                repBusinessRemindPo.setQuantityDingd(item.getQuantity());
                repBusinessRemindPo.setQuantityWeijh(item.getPartQuantity());
                repBusinessRemindPoList.add(repBusinessRemindPo);
                // 对应订单主表只需要发送一个预警提醒
                if (!toexpireMap.containsKey(item.getPurchaseOrderSid()) || toexpireMap.get(item.getPurchaseOrderSid()) == null) {
                    toexpireMap.put(item.getPurchaseOrderSid(), "1");
                    // 存入待发送预警通知
                    SysOverdueBusiness overdueBusiness = new SysOverdueBusiness();
                    overdueBusiness.setTitle(item.getVendorShortName() + ",  采购订单" + item.getPurchaseOrderCode() + "存在"
                                    + itemListMap.get(item.getPurchaseOrderSid()).size() + "行数据已逾期，请您及时关注")
                            .setTableName(ConstantsEms.TABLE_PURCHASE_ORDER)
                            .setDocumentSid(item.getPurchaseOrderSid())
                            .setDocumentCode(item.getPurchaseOrderCode().toString())
                            .setExpiredDate(item.getContractDate())
                            .setNoticeDate(new Date()).setClientId(item.getClientId())
                            .setUserId(item.getCreatorAccountId());
                    if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(item.getMaterialCategory())) {
                        overdueBusiness.setMenuId(ConstantsWorkbench.purchase_order_wl);
                    } else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(item.getMaterialCategory())) {
                        overdueBusiness.setMenuId(ConstantsWorkbench.purchase_order_sp);
                    }
                    overdueBusinessList.add(overdueBusiness);
                }
            });
        }
        log.info("=====>已逾期采购订单处理完成");
        log.info("=====>开始写入缓存表与预警逾期通知表");
        // 写入缓存表
        if (CollectionUtil.isNotEmpty(repBusinessRemindPoList)) {
            repBusinessRemindPoMapper.insertAll(repBusinessRemindPoList);
        }
        // 写入即将到期预警通知表
        if (CollectionUtil.isNotEmpty(toexpireBusinessList)) {
            sysToexpireBusinessMapper.insertAll(toexpireBusinessList);
        }
        // 写入已逾期预警通知表
        if (CollectionUtil.isNotEmpty(overdueBusinessList)) {
            sysOverdueBusinessMapper.insertAll(overdueBusinessList);
        }
        log.info("=====>写入完毕！");

/*
        log.info("=====>开始查询即将到采购订单");
        Date now = new Date();
        List<PurPurchaseOrder> purPurchaseOrders = purPurchaseOrderMapper.selectList(new QueryWrapper<PurPurchaseOrder>().lambda().eq(PurPurchaseOrder::getHandleStatus, ConstantsEms.CHECK_STATUS));
        if (ArrayUtil.isNotEmpty(purPurchaseOrders)) {
            String clientId = purPurchaseOrders.get(0).getClientId();
            SysDefaultSettingClient sysDefaultSettingClient = sysDefaultSettingClientMapper.selectSysDefaultSettingClientById(clientId);
            BigDecimal toexpireDaysCgdd=null;
            if(sysDefaultSettingClient!=null){
                if(sysDefaultSettingClient.getToexpireDaysCgddSys()!=null){
                    toexpireDaysCgdd = sysDefaultSettingClient.getToexpireDaysCgddSys();
                }else{
                    toexpireDaysCgdd = new BigDecimal("7");
                }
            }
            BigDecimal daysCgdd = sysDefaultSettingClient.getToexpireDaysCgdd();
            //即将到期天数
            int repireDay = daysCgdd!=null?daysCgdd.intValue():toexpireDaysCgdd.intValue();
            List<PurPurchaseOrderItem> purPurchaseExpireList = new ArrayList<>();
            List<PurPurchaseOrderItem> purPurchaseOverList = new ArrayList<>();
            SysOverdueBusiness business = new SysOverdueBusiness();
            business.setTableName(ConstantsEms.TABLE_PURCHASE_ORDER)
                    .setClientId(clientId);
            sysOverdueBusinessMapper.delete(business);
            repBusinessRemindPoMapper.delete(new QueryWrapper<RepBusinessRemindPo>().lambda()
                    .eq(RepBusinessRemindPo::getClientId,clientId)
            );
            purPurchaseOrders.forEach(order -> {
                List<RepBusinessRemindPo> repBusinessRemindPos = new ArrayList<>();
                PurPurchaseOrder purPurchaseOrder = purPurchaseOrderMapper.selectPurPurchaseOrderById(order.getPurchaseOrderSid());
                PurPurchaseOrderItem purPurchaseOrderItem = new PurPurchaseOrderItem();
                purPurchaseOrderItem.setPurchaseOrderSid(order.getPurchaseOrderSid());
                List<PurPurchaseOrderItem> purPurchaseOrderItemList = purPurchaseOrderItemMapper.getItemList(purPurchaseOrderItem);
                purPurchaseOrderItemList = purPurchaseOrderItemList.stream().filter(li -> li.getContractDate() != null).collect(Collectors.toList());
                purPurchaseOrderItemList = purPurchaseOrderItemService.handleIndex(purPurchaseOrderItemList);
                int count = 0;
                int countEx = 0;
                for (int i = 0; i < purPurchaseOrderItemList.size(); i++) {
                    //即将到期
                    if (DateUtil.parse(DateUtil.format(purPurchaseOrderItemList.get(i).getContractDate(), "yyyy-MM-dd")).getTime() >= DateUtil.parse(DateUtil.format(now, "yyyy-MM-dd")).getTime()
                            && DateUtil.parse(DateUtil.format(purPurchaseOrderItemList.get(i).getContractDate(), "yyyy-MM-dd")).getTime() <= DateUtil.parse(DateUtil.format(DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, purPurchaseOrderItemList.get(i).getToexpireDays()!=null? purPurchaseOrderItemList.get(i).getToexpireDays().intValue():repireDay), "yyyy-MM-dd")).getTime()) {
                        purPurchaseOrderItemList.get(i).setPurchaseOrderCode(purPurchaseOrder.getPurchaseOrderCode().toString())
                                .setVendorName(purPurchaseOrder.getVendorName())
                                .setProductSeasonName(purPurchaseOrder.getProductSeasonName())
                                .setBusinessTypeName(purPurchaseOrder.getBusinessTypeName())
                                .setDocumentTypeName(purPurchaseOrder.getDocumentTypeName())
                                .setBuyerName(purPurchaseOrder.getNickName())
                                .setCompanyName(purPurchaseOrder.getCompanyShortName())
                                .setPurchaseContractCode(purPurchaseOrder.getPurchaseContractCode());
                        purPurchaseExpireList.add(purPurchaseOrderItemList.get(i));
                        RepBusinessRemindPo repBusinessRemindPo = new RepBusinessRemindPo();
                        BeanCopyUtils.copyProperties(purPurchaseOrder, repBusinessRemindPo);
                        BeanCopyUtils.copyProperties(purPurchaseOrderItemList.get(i), repBusinessRemindPo);
                        repBusinessRemindPo.setRemindType("JJDQ");
                        repBusinessRemindPo.setCompanyName(purPurchaseOrder.getCompanyShortName());
                        repBusinessRemindPo.setQuantityDingd(purPurchaseOrderItemList.get(i).getQuantity());
                        repBusinessRemindPo.setQuantityWeijh(purPurchaseOrderItemList.get(i).getPartQuantity());
                        repBusinessRemindPos.add(repBusinessRemindPo);
                        count++;
                    }
                    //合同交期 已逾期
                    if (DateUtil.parse(DateUtil.format(purPurchaseOrderItemList.get(i).getContractDate(), "yyyy-MM-dd")).getTime() < DateUtil.parse(DateUtil.format(now, "yyyy-MM-dd")).getTime()) {
                        purPurchaseOrderItemList.get(i).setPurchaseOrderCode(purPurchaseOrder.getPurchaseOrderCode().toString())
                                .setVendorName(purPurchaseOrder.getVendorName())
                                .setProductSeasonName(purPurchaseOrder.getProductSeasonName())
                                .setBusinessTypeName(purPurchaseOrder.getBusinessTypeName())
                                .setDocumentTypeName(purPurchaseOrder.getDocumentTypeName())
                                .setBuyerName(purPurchaseOrder.getNickName())
                                .setCompanyName(purPurchaseOrder.getCompanyName())
                                .setPurchaseContractCode(purPurchaseOrder.getPurchaseContractCode());
                        purPurchaseOverList.add(purPurchaseOrderItemList.get(i));
                        RepBusinessRemindPo repBusinessRemindPo = new RepBusinessRemindPo();
                        BeanCopyUtils.copyProperties(purPurchaseOrder, repBusinessRemindPo);
                        BeanCopyUtils.copyProperties(purPurchaseOrderItemList.get(i), repBusinessRemindPo);
                        repBusinessRemindPo.setRemindType("YYQ");
                        repBusinessRemindPo.setCompanyName(purPurchaseOrder.getCompanyShortName());
                        repBusinessRemindPo.setQuantityDingd(purPurchaseOrderItemList.get(i).getQuantity());
                        repBusinessRemindPo.setQuantityWeijh(purPurchaseOrderItemList.get(i).getPartQuantity());
                        repBusinessRemindPos.add(repBusinessRemindPo);
                        countEx++;
                    }
                }
                //看板报表
                if (CollectionUtil.isNotEmpty(repBusinessRemindPos)) {
                    repBusinessRemindPoMapper.inserts(repBusinessRemindPos);
                }
                if (count > 0) {
                    SysToexpireBusiness sysToexpireBusiness = new SysToexpireBusiness();
                    Long userid = 1L;
                    R<LoginUser> userInfo = remoteUserService.getUserInfo(purPurchaseOrder.getCreatorAccount());
                    if (userInfo != null) {
                        if (userInfo.getData() != null) {
                            userid = userInfo.getData().getSysUser().getUserId();
                        }
                    }
                    BasVendor basVendor = basVendorMapper.selectById(purPurchaseOrder.getVendorSid());
                    sysToexpireBusiness.setTitle(basVendor == null ? "" : basVendor.getShortName() + ",  采购订单" + purPurchaseOrder.getPurchaseOrderCode() + "存在" + count + "行数据即将到期，请您及时关注")
                            .setTableName(ConstantsEms.TABLE_PURCHASE_ORDER)
                            .setDocumentSid(purPurchaseOrder.getPurchaseOrderSid())
                            .setDocumentCode(purPurchaseOrder.getPurchaseOrderCode().toString())
                            .setNoticeDate(new Date())
                            .setExpiredDate(purPurchaseOrder.getCreateDate())
                            .setUserId(userid);
                    if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(order.getMaterialCategory())) {
                        sysToexpireBusiness.setMenuId(ConstantsWorkbench.purchase_order_wl);
                    } else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(order.getMaterialCategory())) {
                        sysToexpireBusiness.setMenuId(ConstantsWorkbench.purchase_order_sp);
                    }
                    sysToexpireBusinessMapper.insert(sysToexpireBusiness);
                }
                if (countEx > 0) {
                    SysOverdueBusiness sysOverdueBusiness = new SysOverdueBusiness();
                    Long userid = 1L;
                    R<LoginUser> userInfo = remoteUserService.getUserInfo(purPurchaseOrder.getCreatorAccount());
                    if (userInfo != null) {
                        if (userInfo.getData() != null) {
                            userid = userInfo.getData().getSysUser().getUserId();
                        }
                    }
                    BasVendor basVendor = basVendorMapper.selectById(purPurchaseOrder.getVendorSid());
                    sysOverdueBusiness.setTitle(basVendor == null ? "" : basVendor.getShortName() + ",  采购订单" + purPurchaseOrder.getPurchaseOrderCode() + "存在" + countEx + "行数据已逾期，请您及时关注")
                            .setTableName(ConstantsEms.TABLE_PURCHASE_ORDER)
                            .setDocumentSid(purPurchaseOrder.getPurchaseOrderSid())
                            .setDocumentCode(purPurchaseOrder.getPurchaseOrderCode().toString())
                            .setExpiredDate(purPurchaseOrder.getCreateDate())
                            .setNoticeDate(new Date())
                            .setUserId(userid);
                    if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(order.getMaterialCategory())) {
                        sysOverdueBusiness.setMenuId(ConstantsWorkbench.purchase_order_wl);
                    } else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(order.getMaterialCategory())) {
                        sysOverdueBusiness.setMenuId(ConstantsWorkbench.purchase_order_sp);
                    }
                    sysOverdueBusinessMapper.insert(sysOverdueBusiness);
                }
            });
            //发送邮件 即将到期
            if (CollectionUtil.isNotEmpty(purPurchaseExpireList)) {
                XSSFWorkbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("采购订单即将到期明细");
                sheet.setDefaultColumnWidth(18);
                String[] titles = {"采购订单号", "供应商", "公司", "产品季", "单据类型", "业务类型", "采购员", "采购合同号", "物料编码", "物料名称", "SKU1名称", "SKU2名称", "订单量", "合同交期"};
                //第一行数据
                Row rowBOMHead = sheet.createRow(0);
                //第一行样式
                CellStyle cellStyle = ExcelStyleUtil.getStyle(workbook);
                //第一行数据
                ExcelStyleUtil.setCellStyleLime(cellStyle);
                ExcelStyleUtil.setBorderStyle(cellStyle);
                for (int i = 0; i < titles.length; i++) {
                    Cell cell = rowBOMHead.createCell(i);
                    cell.setCellValue(titles[i]);
                    cell.setCellStyle(cellStyle);
                }
                CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
                for (int i = 0; i < purPurchaseExpireList.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    //采购订单号
                    Cell cell0 = row.createCell(0);
                    cell0.setCellValue(purPurchaseExpireList.get(i).getPurchaseOrderCode());
                    cell0.setCellStyle(defaultCellStyle);

                    //供应商
                    Cell cell1 = row.createCell(1);
                    cell1.setCellValue(purPurchaseExpireList.get(i).getVendorName());
                    cell1.setCellStyle(defaultCellStyle);

                    //公司
                    Cell cell2 = row.createCell(2);
                    cell2.setCellValue(purPurchaseExpireList.get(i).getCompanyName());
                    cell2.setCellStyle(defaultCellStyle);

                    //产品季
                    Cell cell3 = row.createCell(3);
                    cell3.setCellValue(purPurchaseExpireList.get(i).getProductSeasonName());
                    cell3.setCellStyle(defaultCellStyle);

                    //单据类型
                    Cell cell4 = row.createCell(4);
                    cell4.setCellValue(purPurchaseExpireList.get(i).getDocumentTypeName());
                    cell4.setCellStyle(defaultCellStyle);

                    //业务类型
                    Cell cell5 = row.createCell(5);
                    cell5.setCellValue(purPurchaseExpireList.get(i).getBusinessTypeName());
                    cell5.setCellStyle(defaultCellStyle);

                    //采购员
                    Cell cell6 = row.createCell(6);
                    cell6.setCellValue(purPurchaseExpireList.get(i).getBuyerName());
                    cell6.setCellStyle(defaultCellStyle);

                    //采购合同号
                    Cell cell7 = row.createCell(7);
                    cell7.setCellValue(purPurchaseExpireList.get(i).getPurchaseContractCode());
                    cell7.setCellStyle(defaultCellStyle);

                    //物料编码
                    Cell cell8 = row.createCell(8);
                    cell8.setCellValue(purPurchaseExpireList.get(i).getMaterialCode());
                    cell8.setCellStyle(defaultCellStyle);

                    //物名称
                    Cell cell9 = row.createCell(9);
                    cell9.setCellValue(purPurchaseExpireList.get(i).getMaterialName());
                    cell9.setCellStyle(defaultCellStyle);

                    //sku1
                    Cell cell10 = row.createCell(10);
                    cell10.setCellValue(purPurchaseExpireList.get(i).getSku1Name());
                    cell10.setCellStyle(defaultCellStyle);

                    //sku2
                    Cell cell11 = row.createCell(11);
                    cell11.setCellValue(purPurchaseExpireList.get(i).getSku2Name());
                    cell11.setCellStyle(defaultCellStyle);

                    //订单量
                    Cell cell12 = row.createCell(12);
                    cell12.setCellValue(purPurchaseExpireList.get(i).getQuantity().toString());
                    cell12.setCellStyle(defaultCellStyle);
                    //合同交期
                    Cell cell13 = row.createCell(13);
                    cell13.setCellValue(DateUtil.format(purPurchaseExpireList.get(i).getContractDate(), "yyyy-MM-dd"));
                    cell13.setCellStyle(defaultCellStyle);
                }
                String mailtext =
                        "<font color=\"warning\">采购订单具体即将到期明细请查阅附件，谢谢！</font><br />";
                String title = "【采购订单具体即将到期明细】 当前有 " + purPurchaseExpireList.size() + "条明细信息，请知悉";
                try {
                    String msg = "采购订单即将到期明细";
                    File file = getEmptyExcelFile(workbook, msg);
//                    MailUtil.send("2453862941@qq.com", null,
//                            null, title, mailtext, true, file);
                    file.deleteOnExit();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("邮件发送失败");
                }
            }
            //发送邮件 已逾期
            if (CollectionUtil.isNotEmpty(purPurchaseOverList)) {
                XSSFWorkbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("采购订单已逾期明细");
                sheet.setDefaultColumnWidth(18);
                String[] titles = {"采购订单号", "供应商", "公司", "产品季", "单据类型", "业务类型", "采购员", "采购合同号", "物料编码", "物料名称", "SKU1名称", "SKU2名称", "订单量", "合同交期"};
                //第一行数据
                Row rowBOMHead = sheet.createRow(0);
                //第一行样式
                CellStyle cellStyle = ExcelStyleUtil.getStyle(workbook);
                //第一行数据
                ExcelStyleUtil.setCellStyleLime(cellStyle);
                ExcelStyleUtil.setBorderStyle(cellStyle);
                for (int i = 0; i < titles.length; i++) {
                    Cell cell = rowBOMHead.createCell(i);
                    cell.setCellValue(titles[i]);
                    cell.setCellStyle(cellStyle);
                }
                CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
                for (int i = 0; i < purPurchaseOverList.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    //采购订单号
                    Cell cell0 = row.createCell(0);
                    cell0.setCellValue(purPurchaseOverList.get(i).getPurchaseOrderCode());
                    cell0.setCellStyle(defaultCellStyle);

                    //供应商
                    Cell cell1 = row.createCell(1);
                    cell1.setCellValue(purPurchaseOverList.get(i).getVendorName());
                    cell1.setCellStyle(defaultCellStyle);

                    //公司
                    Cell cell2 = row.createCell(2);
                    cell2.setCellValue(purPurchaseOverList.get(i).getCompanyName());
                    cell2.setCellStyle(defaultCellStyle);

                    //产品季
                    Cell cell3 = row.createCell(3);
                    cell3.setCellValue(purPurchaseOverList.get(i).getProductSeasonName());
                    cell3.setCellStyle(defaultCellStyle);

                    //单据类型
                    Cell cell4 = row.createCell(4);
                    cell4.setCellValue(purPurchaseOverList.get(i).getDocumentTypeName());
                    cell4.setCellStyle(defaultCellStyle);

                    //业务类型
                    Cell cell5 = row.createCell(5);
                    cell5.setCellValue(purPurchaseOverList.get(i).getBusinessTypeName());
                    cell5.setCellStyle(defaultCellStyle);

                    //采购员
                    Cell cell6 = row.createCell(6);
                    cell6.setCellValue(purPurchaseOverList.get(i).getBuyerName());
                    cell6.setCellStyle(defaultCellStyle);

                    //采购合同号
                    Cell cell7 = row.createCell(7);
                    cell7.setCellValue(purPurchaseOverList.get(i).getPurchaseContractCode());
                    cell7.setCellStyle(defaultCellStyle);

                    //物料编码
                    Cell cell8 = row.createCell(8);
                    cell8.setCellValue(purPurchaseOverList.get(i).getMaterialCode());
                    cell8.setCellStyle(defaultCellStyle);

                    //物名称
                    Cell cell9 = row.createCell(9);
                    cell9.setCellValue(purPurchaseOverList.get(i).getMaterialName());
                    cell9.setCellStyle(defaultCellStyle);

                    //sku1
                    Cell cell10 = row.createCell(10);
                    cell10.setCellValue(purPurchaseOverList.get(i).getSku1Name());
                    cell10.setCellStyle(defaultCellStyle);

                    //sku2
                    Cell cell11 = row.createCell(11);
                    cell11.setCellValue(purPurchaseOverList.get(i).getSku2Name());
                    cell11.setCellStyle(defaultCellStyle);

                    //订单量
                    Cell cell12 = row.createCell(12);
                    cell12.setCellValue(purPurchaseOverList.get(i).getQuantity().toString());
                    cell12.setCellStyle(defaultCellStyle);
                    //合同交期
                    Cell cell13 = row.createCell(13);
                    cell13.setCellValue(DateUtil.format(purPurchaseOverList.get(i).getContractDate(), "yyyy-MM-dd"));
                    cell13.setCellStyle(defaultCellStyle);
                }
                String mailtext =
                        "<font color=\"warning\">采购订单具体已逾期明细请查阅附件，谢谢！</font><br />";
                String title = "【采购订单具体已逾期明细】 当前有 " + purPurchaseOverList.size() + "条明细信息，请知悉";
                try {
                    String msg = "采购订单已逾期明细";
                    File file = getEmptyExcelFile(workbook, msg);
//                    MailUtil.send("2453862941@qq.com", null,
//                            null, title, mailtext, true, file);
                    file.deleteOnExit();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("邮件发送失败");
                }
            }
        }
 */
    }

    @Scheduled(cron = "00 10 02 * * * ")
    public void updateInvStatus() {

        List<PurPurchaseOrder> orderListCk = purPurchaseOrderMapper.selectInOutStockStatusCk();
        if (CollectionUtil.isNotEmpty(orderListCk)) {
            Long[] sids = orderListCk.stream().map(PurPurchaseOrder::getPurchaseOrderSid).toArray(Long[]::new);
            System.out.println("更新全部出库" + sids);
            purPurchaseOrderMapper.update(null, new UpdateWrapper<PurPurchaseOrder>().lambda()
                    .in(PurPurchaseOrder::getPurchaseOrderSid, sids)
                    .set(PurPurchaseOrder::getInOutStockStatus, ConstantsInventory.IN_OUT_STORE_STATUS_QBCK));
        }

        List<PurPurchaseOrder> orderListRk = purPurchaseOrderMapper.selectInOutStockStatusRk();
        if (CollectionUtil.isNotEmpty(orderListRk)) {
            Long[] sids = orderListRk.stream().map(PurPurchaseOrder::getPurchaseOrderSid).toArray(Long[]::new);
            System.out.println("更新全部入库" + sids);
            purPurchaseOrderMapper.update(null, new UpdateWrapper<PurPurchaseOrder>().lambda()
                    .in(PurPurchaseOrder::getPurchaseOrderSid, sids)
                    .set(PurPurchaseOrder::getInOutStockStatus, ConstantsInventory.IN_OUT_STORE_STATUS_QBRK));
        }

    }


}
