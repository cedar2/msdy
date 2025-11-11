package com.platform.ems.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsOrder;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.PurPurchaseOrder;
import com.platform.ems.domain.SalSalesIntentOrder;
import com.platform.ems.domain.SalSalesOrder;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.mapper.PurPurchaseOrderMapper;
import com.platform.ems.mapper.SalSalesIntentOrderMapper;
import com.platform.ems.mapper.SalSalesOrderMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.entity.SysMenu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 未排产销售订单提醒\无销售价的销售订单提醒\无采购价的采购订单提醒\销售状况-销售占比/销售 趋势/销售同比
 * 引用临时过渡合同，未修改合同号的采购订单提醒
 * @author yangqz
 */
@EnableScheduling
@Component
@SuppressWarnings("all")
@Slf4j
@Service
public class OrderTask {

    @Autowired
    private SalSalesOrderMapper saleOrderMapepr;
    @Autowired
    private PurPurchaseOrderMapper purchaseOrderMapepr;
    @Autowired
    private SalSalesIntentOrderMapper salesIntentOrderMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    @Autowired
    private RemoteMenuService remoteMenuService;

    /**
     * 若订单里的合同的“特殊用途”为“临时过渡”，合同确认时（即处理状态变为“已确认”时），给销售员/采购员发待办消息：销售/采购订单XXXXX使用的是过渡合同，请按需处理！
     * 当且仅当合同的过渡合同改为常规合同（即“特殊用途”为空的合同），删除该待办消息（除变更外，订单处有单独按钮可做修改）
     */
    @Scheduled(cron = "00 15 06 * * *")
    public void earlyWarningOrder() {
        log.info("=====>删除订单里的已确认的合同的“特殊用途”为“临时过渡“的订单的待办");
        sysTodoTaskMapper.deleteIgnore(new SysTodoTask()
                .setTableNameList(new String[]{ConstantsTable.TABLE_SALE_ORDER + "-" + ConstantsTable.TABLE_SALE_CONTRACT,
                        ConstantsTable.TABLE_PURCHASE_ORDER + "-" + ConstantsTable.TABLE_PURCHASE_CONTRACT}));
        log.info("=====>开始查询订单里的已确认的销售合同的“特殊用途”为“临时过渡“的销售订单。");
        List<SysTodoTask> todoTaskList = new ArrayList<>();
        // 销售订单
        List<SalSalesOrder> saleOrderList = saleOrderMapepr.selectListInterceptorIgnore(new SalSalesOrder()
                .setContractPurpose(ConstantsOrder.CONTRACT_PURPOSE_LSGD).setHandleStatus(ConstantsEms.CHECK_STATUS));
        if (CollUtil.isNotEmpty(saleOrderList)) {
            // 获取菜单id
            Long menuId = null;
            SysMenu menu = new SysMenu();
            menu.setMenuName(ConstantsWorkbench.TODO_SAL_ORDER_MENU_NAME);
            try {
                menu = remoteMenuService.getInfoByName(menu).getData();
            } catch (Exception e){
                log.warn(ConstantsWorkbench.TODO_SAL_ORDER_MENU_NAME + "菜单获取失败！");
            }
            if (menu != null && menu.getMenuId() != null) {
                menuId = menu.getMenuId();
            }
            for (SalSalesOrder order : saleOrderList) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setClientId(order.getClientId())
                        .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_SALE_ORDER + "-" + ConstantsTable.TABLE_SALE_CONTRACT)
                        .setDocumentSid(order.getSalesOrderSid());
                sysTodoTask.setTitle("销售订单" + order.getSalesOrderCode() + "使用的是过渡合同，请按需处理！")
                        .setDocumentCode(order.getSalesOrderCode())
                        .setNoticeDate(new Date())
                        .setMenuId(menuId)
                        .setUserId(order.getSalePersonId());
                todoTaskList.add(sysTodoTask);
            }
        }
        log.info("=====>开始查询订单里的已确认的采购合同的“特殊用途”为“临时过渡“的采购订单。");
        // 采购订单
        List<PurPurchaseOrder> purchaseOrderList = purchaseOrderMapepr.selectListInterceptorIgnore(new PurPurchaseOrder()
                .setContractPurpose(ConstantsOrder.CONTRACT_PURPOSE_LSGD).setHandleStatus(ConstantsEms.CHECK_STATUS));
        if (CollUtil.isNotEmpty(purchaseOrderList)) {
            // 获取菜单id
            Long menuId = null;
            SysMenu menu = new SysMenu();
            menu.setMenuName(ConstantsWorkbench.TODO_PUR_ORDER_MENU_NAME);
            try {
                menu = remoteMenuService.getInfoByName(menu).getData();
            } catch (Exception e){
                log.warn(ConstantsWorkbench.TODO_PUR_ORDER_MENU_NAME + "菜单获取失败！");
            }
            if (menu != null && menu.getMenuId() != null) {
                menuId = menu.getMenuId();
            }
            for (PurPurchaseOrder order : purchaseOrderList) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setClientId(order.getClientId())
                        .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_PURCHASE_ORDER + "-" + ConstantsTable.TABLE_PURCHASE_CONTRACT)
                        .setDocumentSid(order.getPurchaseOrderSid());
                sysTodoTask.setTitle("采购订单" + order.getPurchaseOrderCode() + "使用的是过渡合同，请按需处理！")
                        .setDocumentCode(String.valueOf(order.getPurchaseOrderCode()))
                        .setNoticeDate(new Date())
                        .setMenuId(menuId)
                        .setUserId(order.getBuyerId());
                todoTaskList.add(sysTodoTask);
            }
        }
        log.info("=====>开始写入对应的待办。");
        if (CollUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.inserts(todoTaskList);
        }
        log.info("=====>订单中的合同为临时过渡的待办提醒任务完成！");
    }

    /**
     * 每天上午9:30跑定时任务，查询出所有处理状态为“已确认”且签收状态(纸质合同)为“未签收”的销售订单，
     * 给销售订单的“销售员”、系统默认设置的“销售财务对接人员”触发待办事项，跟进销售订单合同及时签收
     */
    public void salesOrderContractPaper() {
        sysTodoTaskMapper.deleteIgnore(new SysTodoTask().setTitle("的订单合同待签收，请及时处理！")
                .setTableNameList(new String[]{ConstantsTable.TABLE_SALE_ORDER, ConstantsTable.TABLE_PURCHASE_ORDER,
                        ConstantsTable.TABLE_SAL_SALES_INTENT_ORDER}));
        // 销售
        SalSalesOrder salSalesOrder = new SalSalesOrder();
        salSalesOrder.setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setSignInStatus(ConstantsEms.SIGN_IN_STATUS_WQS);
        List<SalSalesOrder> orderList = saleOrderMapepr.selectListContractPaperInterceptorIgnore(salSalesOrder);
        if (CollUtil.isNotEmpty(orderList)) {
            List<SysTodoTask> todoTaskList = new ArrayList<>();
            // 获取菜单id
            Long menuId = null;
            SysMenu menu = new SysMenu();
            menu.setMenuName(ConstantsWorkbench.TODO_SAL_ORDER_MENU_NAME);
            try {
                menu = remoteMenuService.getInfoByName(menu).getData();
            } catch (Exception e){
                log.warn(ConstantsWorkbench.TODO_SAL_ORDER_MENU_NAME + "菜单获取失败！");
            }
            if (menu != null && menu.getMenuId() != null) {
                menuId = menu.getMenuId();
            }
            for (SalSalesOrder order : orderList) {
                if (order.getSalePersonId() != null) {
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    sysTodoTask.setClientId(order.getClientId())
                            .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName(ConstantsTable.TABLE_SALE_ORDER)
                            .setDocumentSid(order.getSalesOrderSid());
                    sysTodoTask.setTitle("销售订单" + order.getSalesOrderCode() + "的订单合同待签收，请及时处理！")
                            .setDocumentCode(order.getSalesOrderCode())
                            .setNoticeDate(new Date())
                            .setMenuId(menuId)
                            .setUserId(order.getSalePersonId());
                    todoTaskList.add(sysTodoTask);
                }
                if (StrUtil.isNotBlank(order.getSaleFinanceAccountId())) {
                    String[] userId = order.getSaleFinanceAccountId().split(";");
                    for (String id : userId) {
                        if (order.getSalePersonId() == null || !id.equals(String.valueOf(order.getSalePersonId()))) {
                            SysTodoTask sysTodoTask2 = new SysTodoTask();
                            sysTodoTask2.setClientId(order.getClientId())
                                    .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                    .setTableName(ConstantsTable.TABLE_SALE_ORDER)
                                    .setDocumentSid(order.getSalesOrderSid());
                            sysTodoTask2.setTitle("销售订单" + order.getSalesOrderCode() + "的订单合同待签收，请及时处理！")
                                    .setDocumentCode(order.getSalesOrderCode())
                                    .setNoticeDate(new Date())
                                    .setMenuId(menuId)
                                    .setUserId(Long.parseLong(id));
                            todoTaskList.add(sysTodoTask2);
                        }

                    }
                }
            }
            if (CollUtil.isNotEmpty(todoTaskList)) {
                sysTodoTaskMapper.inserts(todoTaskList);
            }
        }

        //采购
        PurPurchaseOrder purchaseOrder = new PurPurchaseOrder();
        purchaseOrder.setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setSignInStatus(ConstantsEms.SIGN_IN_STATUS_WQS);
        List<PurPurchaseOrder> purchaseOrderList = purchaseOrderMapepr.selectListContractPaperInterceptorIgnore(purchaseOrder);
        if (CollUtil.isNotEmpty(purchaseOrderList)) {
            List<SysTodoTask> todoTaskList = new ArrayList<>();
            // 获取菜单id
            Long menuId = null;
            SysMenu menu = new SysMenu();
            menu.setMenuName(ConstantsWorkbench.TODO_PUR_ORDER_MENU_NAME);
            try {
                menu = remoteMenuService.getInfoByName(menu).getData();
            } catch (Exception e){
                log.warn(ConstantsWorkbench.TODO_PUR_ORDER_MENU_NAME + "菜单获取失败！");
            }
            if (menu != null && menu.getMenuId() != null) {
                menuId = menu.getMenuId();
            }
            for (PurPurchaseOrder order : purchaseOrderList) {
                if (order.getBuyerId() != null) {
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    sysTodoTask.setClientId(order.getClientId())
                            .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName(ConstantsTable.TABLE_PURCHASE_ORDER)
                            .setDocumentSid(order.getPurchaseOrderSid());
                    sysTodoTask.setTitle("采购订单" + order.getPurchaseOrderCode() + "的订单合同待签收，请及时处理！")
                            .setDocumentCode(String.valueOf(order.getPurchaseOrderCode()))
                            .setNoticeDate(new Date())
                            .setMenuId(menuId)
                            .setUserId(order.getBuyerId());
                    todoTaskList.add(sysTodoTask);
                }
                if (StrUtil.isNotBlank(order.getPurchaseFinanceAccountId())) {
                    String[] userId = order.getPurchaseFinanceAccountId().split(";");
                    for (String id : userId) {
                        if (order.getBuyerId() == null || !id.equals(String.valueOf(order.getBuyerId()))) {
                            SysTodoTask sysTodoTask2 = new SysTodoTask();
                            sysTodoTask2.setClientId(order.getClientId())
                                    .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                    .setTableName(ConstantsTable.TABLE_PURCHASE_ORDER)
                                    .setDocumentSid(order.getPurchaseOrderSid());
                            sysTodoTask2.setTitle("采购订单" + order.getPurchaseOrderCode() + "的订单合同待签收，请及时处理！")
                                    .setDocumentCode(String.valueOf(order.getPurchaseOrderCode()))
                                    .setNoticeDate(new Date())
                                    .setMenuId(menuId)
                                    .setUserId(Long.parseLong(id));
                            todoTaskList.add(sysTodoTask2);
                        }

                    }
                }
            }
            if (CollUtil.isNotEmpty(todoTaskList)) {
                sysTodoTaskMapper.inserts(todoTaskList);
            }
        }


        /*
        每天上午9:30跑定时任务，查询出所有处理状态为“已确认”且签收状态(纸质合同)为“未签收”的销售意向单，给销售意向单的“销售员”、
        系统默认设置的“销售财务对接人员”触发待办事项，跟进销售意向单纸质合同及时签收，待办消息内容：销售意向单XXX的订单合同待签收，请及时处理！
         */

        // 写入新 销售意向单
        SalSalesIntentOrder salesIntentOrder = new SalSalesIntentOrder();
        salesIntentOrder.setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setSignInStatus(ConstantsEms.SIGN_IN_STATUS_WQS);
        List<SalSalesIntentOrder> salesIntentOrderList = salesIntentOrderMapper.selectSalesIntentOrderList(salesIntentOrder);
        if (CollUtil.isNotEmpty(salesIntentOrderList)) {
            List<SysTodoTask> todoTaskList = new ArrayList<>();
            // 获取菜单id
            Long menuId = null;
            SysMenu menu = new SysMenu();
            menu.setMenuName(ConstantsWorkbench.TODO_SAL_INTENT_MENU_NAME);
            try {
                menu = remoteMenuService.getInfoByName(menu).getData();
            } catch (Exception e){
                log.warn(ConstantsWorkbench.TODO_SAL_INTENT_MENU_NAME + "菜单获取失败！");
            }
            if (menu != null && menu.getMenuId() != null) {
                menuId = menu.getMenuId();
            }
            for (SalSalesIntentOrder intentOrder : salesIntentOrderList) {
                if (intentOrder.getSalePerson() != null) {
                    SysTodoTask sysTodoTask1 = new SysTodoTask();
                    sysTodoTask1.setNoticeDate(new Date()).setMenuId(menuId)
                            .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName(ConstantsTable.TABLE_SAL_SALES_INTENT_ORDER);
                    sysTodoTask1.setClientId(intentOrder.getClientId())
                            .setDocumentSid(intentOrder.getSalesIntentOrderSid())
                            .setDocumentCode(String.valueOf(intentOrder.getSalesIntentOrderCode()));
                    sysTodoTask1.setTitle("销售意向单" + intentOrder.getSalesIntentOrderCode() + "的订单合同待签收，请及时签收！")
                            .setUserId(intentOrder.getSalePersonId());
                    todoTaskList.add(sysTodoTask1);
                }
                if (StrUtil.isNotBlank(intentOrder.getSaleFinanceAccountId())) {
                    String[] userId = intentOrder.getSaleFinanceAccountId().split(";");
                    for (String id : userId) {
                        if (intentOrder.getSalePersonId() == null || !id.equals(String.valueOf(intentOrder.getSalePersonId()))) {
                            SysTodoTask sysTodoTask2 = new SysTodoTask();
                            sysTodoTask2.setNoticeDate(new Date()).setMenuId(menuId)
                                    .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                    .setTableName(ConstantsTable.TABLE_SALE_CONTRACT);
                            sysTodoTask2.setClientId(intentOrder.getClientId())
                                    .setDocumentSid(intentOrder.getSalesIntentOrderSid())
                                    .setDocumentCode(String.valueOf(intentOrder.getSalesIntentOrderCode()));
                            sysTodoTask2.setTitle("销售意向单" + intentOrder.getSalesIntentOrderCode() + "的订单合同待签收，请及时签收！")
                                    .setUserId(Long.parseLong(id));
                            todoTaskList.add(sysTodoTask2);
                        }
                    }
                }
            }
            if (CollUtil.isNotEmpty(todoTaskList)) {
                sysTodoTaskMapper.inserts(todoTaskList);
            }
        }
    }

    /**
     * 每天上午9:30跑定时任务，查询出所有处理状态为“已确认”且上传状态(纸质合同)为“未上传”的销售意向单，给销售意向单的“销售员”、
     * 系统默认设置的“销售财务对接人员”触发待办事项，跟进销售意向单纸质合同及时上传，待办消息内容：销售意向单XXX的订单合同待上传，请及时处理！
     */
    public void WscZhizhihetong() {
        // 删除
        sysTodoTaskMapper.deleteIgnore(new SysTodoTask().setTitle("订单合同待上传，请及时处理！")
                .setTableNameList(new String[]{ConstantsTable.TABLE_SALE_ORDER, ConstantsTable.TABLE_PURCHASE_ORDER,
                        ConstantsTable.TABLE_SAL_SALES_INTENT_ORDER}));
        // 销售订单
        SalSalesOrder salesOrder2 = new SalSalesOrder();
        salesOrder2.setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS);
        List<SalSalesOrder> saleOrderList2 = saleOrderMapepr.selectListContractPaperInterceptorIgnore(salesOrder2);
        if (CollUtil.isNotEmpty(saleOrderList2)) {
            List<SysTodoTask> todoTaskList = new ArrayList<>();
            // 获取菜单id
            Long menuId = null;
            SysMenu menu = new SysMenu();
            menu.setMenuName(ConstantsWorkbench.TODO_SAL_ORDER_MENU_NAME);
            try {
                menu = remoteMenuService.getInfoByName(menu).getData();
            } catch (Exception e){
                log.warn(ConstantsWorkbench.TODO_SAL_ORDER_MENU_NAME + "菜单获取失败！");
            }
            if (menu != null && menu.getMenuId() != null) {
                menuId = menu.getMenuId();
            }
            for (SalSalesOrder order : saleOrderList2) {
                if (order.getSalePersonId() != null) {
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    sysTodoTask.setClientId(order.getClientId())
                            .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName(ConstantsTable.TABLE_SALE_ORDER)
                            .setDocumentSid(order.getSalesOrderSid());
                    sysTodoTask.setTitle("销售订单" + order.getSalesOrderCode() + "的订单合同待上传，请及时处理！")
                            .setDocumentCode(String.valueOf(order.getSalesOrderCode()))
                            .setNoticeDate(new Date())
                            .setMenuId(menuId)
                            .setUserId(order.getSalePersonId());
                    todoTaskList.add(sysTodoTask);
                }
                if (StrUtil.isNotBlank(order.getSaleFinanceAccountId())) {
                    String[] userId = order.getSaleFinanceAccountId().split(";");
                    for (String id : userId) {
                        if (order.getSalePersonId() == null || !id.equals(String.valueOf(order.getSalePersonId()))) {
                            SysTodoTask sysTodoTask2 = new SysTodoTask();
                            sysTodoTask2.setClientId(order.getClientId())
                                    .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                    .setTableName(ConstantsTable.TABLE_SALE_ORDER)
                                    .setDocumentSid(order.getSalesOrderSid());
                            sysTodoTask2.setTitle("销售订单" + order.getSalesOrderCode() + "的订单合同待上传，请及时处理！")
                                    .setDocumentCode(String.valueOf(order.getSalesOrderCode()))
                                    .setNoticeDate(new Date())
                                    .setMenuId(menuId)
                                    .setUserId(Long.parseLong(id));
                            todoTaskList.add(sysTodoTask2);
                        }

                    }
                }
            }
            if (CollUtil.isNotEmpty(todoTaskList)) {
                sysTodoTaskMapper.inserts(todoTaskList);
            }
        }

        // 采购订单
        PurPurchaseOrder purchaseOrder2 = new PurPurchaseOrder();
        purchaseOrder2.setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS);
        List<PurPurchaseOrder> purchaseOrderist2 = purchaseOrderMapepr.selectListContractPaperInterceptorIgnore(purchaseOrder2);
        if (CollUtil.isNotEmpty(purchaseOrderist2)) {
            List<SysTodoTask> todoTaskList = new ArrayList<>();
            // 获取菜单id
            Long menuId = null;
            SysMenu menu = new SysMenu();
            menu.setMenuName(ConstantsWorkbench.TODO_PUR_ORDER_MENU_NAME);
            try {
                menu = remoteMenuService.getInfoByName(menu).getData();
            } catch (Exception e){
                log.warn(ConstantsWorkbench.TODO_PUR_ORDER_MENU_NAME + "菜单获取失败！");
            }
            if (menu != null && menu.getMenuId() != null) {
                menuId = menu.getMenuId();
            }
            for (PurPurchaseOrder order : purchaseOrderist2) {
                if (order.getBuyerId() != null) {
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    sysTodoTask.setClientId(order.getClientId())
                            .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName(ConstantsTable.TABLE_PURCHASE_ORDER)
                            .setDocumentSid(order.getPurchaseOrderSid());
                    sysTodoTask.setTitle("采购订单" + order.getPurchaseOrderCode() + "的订单合同待上传，请及时处理！")
                            .setDocumentCode(String.valueOf(order.getPurchaseOrderCode()))
                            .setNoticeDate(new Date())
                            .setMenuId(menuId)
                            .setUserId(order.getBuyerId());
                    todoTaskList.add(sysTodoTask);
                }
                if (StrUtil.isNotBlank(order.getPurchaseFinanceAccountId())) {
                    String[] userId = order.getPurchaseFinanceAccountId().split(";");
                    for (String id : userId) {
                        if (order.getBuyerId() == null || !id.equals(String.valueOf(order.getBuyerId()))) {
                            SysTodoTask sysTodoTask2 = new SysTodoTask();
                            sysTodoTask2.setClientId(order.getClientId())
                                    .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                    .setTableName(ConstantsTable.TABLE_PURCHASE_ORDER)
                                    .setDocumentSid(order.getPurchaseOrderSid());
                            sysTodoTask2.setTitle("采购订单" + order.getPurchaseOrderCode() + "的订单合同待上传，请及时处理！")
                                    .setDocumentCode(String.valueOf(order.getPurchaseOrderCode()))
                                    .setNoticeDate(new Date())
                                    .setMenuId(menuId)
                                    .setUserId(Long.parseLong(id));
                            todoTaskList.add(sysTodoTask2);
                        }

                    }
                }
            }
            if (CollUtil.isNotEmpty(todoTaskList)) {
                sysTodoTaskMapper.inserts(todoTaskList);
            }
        }

        // 销售意向单
        SalSalesIntentOrder salesIntentOrder = new SalSalesIntentOrder();
        salesIntentOrder.setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS);
        List<SalSalesIntentOrder> intentOrderList = salesIntentOrderMapper.selectSalesIntentOrderList(salesIntentOrder);
        if (CollUtil.isNotEmpty(intentOrderList)) {
            List<SysTodoTask> todoTaskList = new ArrayList<>();
            // 获取菜单id
            Long menuId = null;
            SysMenu menu = new SysMenu();
            menu.setMenuName(ConstantsWorkbench.TODO_SAL_INTENT_MENU_NAME);
            try {
                menu = remoteMenuService.getInfoByName(menu).getData();
            } catch (Exception e){
                log.warn(ConstantsWorkbench.TODO_SAL_INTENT_MENU_NAME + "菜单获取失败！");
            }
            if (menu != null && menu.getMenuId() != null) {
                menuId = menu.getMenuId();
            }
            for (SalSalesIntentOrder order : intentOrderList) {
                if (order.getSalePersonId() != null) {
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    sysTodoTask.setClientId(order.getClientId())
                            .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName(ConstantsTable.TABLE_SALE_ORDER)
                            .setDocumentSid(order.getSalesIntentOrderSid());
                    sysTodoTask.setTitle("销售意向单" + order.getSalesIntentOrderCode() + "的订单合同待上传，请及时处理！")
                            .setDocumentCode(String.valueOf(order.getSalesIntentOrderCode()))
                            .setNoticeDate(new Date())
                            .setMenuId(menuId)
                            .setUserId(order.getSalePersonId());
                    todoTaskList.add(sysTodoTask);
                }
                if (StrUtil.isNotBlank(order.getSaleFinanceAccountId())) {
                    String[] userId = order.getSaleFinanceAccountId().split(";");
                    for (String id : userId) {
                        if (order.getSalePersonId() == null || !id.equals(String.valueOf(order.getSalePersonId()))) {
                            SysTodoTask sysTodoTask2 = new SysTodoTask();
                            sysTodoTask2.setClientId(order.getClientId())
                                    .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                    .setTableName(ConstantsTable.TABLE_SAL_SALES_INTENT_ORDER)
                                    .setDocumentSid(order.getSalesIntentOrderSid());
                            sysTodoTask2.setTitle("销售意向单" + order.getSalesIntentOrderCode() + "的订单合同待上传，请及时处理！")
                                    .setDocumentCode(String.valueOf(order.getSalesIntentOrderCode()))
                                    .setNoticeDate(new Date())
                                    .setMenuId(menuId)
                                    .setUserId(Long.parseLong(id));
                            todoTaskList.add(sysTodoTask2);
                        }

                    }
                }
            }
            if (CollUtil.isNotEmpty(todoTaskList)) {
                sysTodoTaskMapper.inserts(todoTaskList);
            }
        }
    }
}
