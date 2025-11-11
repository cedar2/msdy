package com.platform.ems.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.system.domain.SysOverdueBusiness;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.domain.SysToexpireBusiness;
import com.platform.system.mapper.SysOverdueBusinessMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.mapper.SysToexpireBusinessMapper;
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
 * 合同的预警、警示
 *
 * @author chenkw
 */
@EnableScheduling
@Component
@SuppressWarnings("all")
@Slf4j
@Service
public class ContractWarningTask {

    @Autowired
    private PurPurchaseContractMapper purPurchaseContractMapper;
    @Autowired
    private SalSaleContractMapper salSaleContractMapper;
    @Autowired
    private SalSalesIntentOrderMapper salesIntentOrderMapper;
    @Autowired
    private SysToexpireBusinessMapper sysToexpireBusinessMapper;
    @Autowired
    private SysOverdueBusinessMapper sysOverdueBusinessMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    @Autowired
    private RemoteMenuService remoteMenuService;

    @Scheduled(cron = "00 00 09 * * *")
    public void contractWarning() {
        // 即将到期
        List<SysToexpireBusiness> sysToexpireBusinessList = new ArrayList<>();
        // 已逾期
        List<SysOverdueBusiness> sysOverdueBusinessList = new ArrayList<>();
        log.info("=====>开始查询即将逾期采购合同");
        List<PurPurchaseContract> purchaseContractAboutList = purPurchaseContractMapper.getToexpireBusiness(2);
        if (CollectionUtil.isNotEmpty(purchaseContractAboutList)) {
            purchaseContractAboutList.forEach(contract -> {
                if (contract.getCreatorAccountId()!=null){
                    SysToexpireBusiness sysToexpireBusiness = new SysToexpireBusiness();
                    String endDate = DateUtil.format(contract.getEndDate(), "yyyy-MM-dd");
                    sysToexpireBusiness.setClientId(contract.getClientId())
                            .setTitle(contract.getVendorShortName() + "的采购合同 " + contract.getPurchaseContractCode() + " 即将到期，合同有效期至" + endDate)
                            .setTableName(ConstantsTable.TABLE_PURCHASE_CONTRACT)
                            .setDocumentSid(contract.getPurchaseContractSid())
                            .setDocumentCode(contract.getPurchaseContractCode())
                            .setExpiredDate(contract.getEndDate())
                            .setNoticeDate(new Date())
                            .setMenuId(ConstantsWorkbench.purchase_contract)
                            .setUserId(contract.getCreatorAccountId());
                    sysToexpireBusinessList.add(sysToexpireBusiness);
                }
            });
        }
        log.info("=====>开始查询已逾期采购合同");
        List<PurPurchaseContract> purchaseContractAlreadyList = purPurchaseContractMapper.getOverdueBusiness();
        if (CollectionUtil.isNotEmpty(purchaseContractAlreadyList)) {
            purchaseContractAlreadyList.forEach(contract -> {
                SysOverdueBusiness sysOverdueBusiness = new SysOverdueBusiness();
                if (contract.getCreatorAccountId()!=null){
                    String endDate = DateUtil.format(contract.getEndDate(), "yyyy-MM-dd");
                    sysOverdueBusiness.setClientId(contract.getClientId())
                            .setTitle(contract.getVendorShortName() + "的采购合同 " + contract.getPurchaseContractCode() + " 已逾期，合同有效期至" + endDate)
                            .setTableName(ConstantsTable.TABLE_PURCHASE_CONTRACT)
                            .setDocumentSid(contract.getPurchaseContractSid())
                            .setDocumentCode(contract.getPurchaseContractCode())
                            .setExpiredDate(contract.getEndDate())
                            .setNoticeDate(new Date())
                            .setMenuId(ConstantsWorkbench.purchase_contract)
                            .setUserId(contract.getCreatorAccountId());
                    sysOverdueBusinessList.add(sysOverdueBusiness);
                }
            });
        }
        log.info("=====>采购合同查询结束");
        log.info("=====>开始查询即将逾期销售合同");
        List<SalSaleContract> saleContractAboutList = salSaleContractMapper.getToexpireBusiness(2);
        if (CollectionUtil.isNotEmpty(saleContractAboutList)) {
            saleContractAboutList.forEach(contract -> {
                SysToexpireBusiness sysToexpireBusiness = new SysToexpireBusiness();
                if (contract.getCreatorAccountId()!=null){
                    String endDate = DateUtil.format(contract.getEndDate(), "yyyy-MM-dd");
                    sysToexpireBusiness.setClientId(contract.getClientId())
                            .setTitle(contract.getCustomerShortName() + "的销售合同 " + contract.getSaleContractCode() + " 即将到期，合同有效期至" + endDate)
                            .setTableName(ConstantsTable.TABLE_SALE_CONTRACT)
                            .setDocumentSid(contract.getSaleContractSid())
                            .setDocumentCode(contract.getSaleContractCode())
                            .setExpiredDate(contract.getEndDate())
                            .setNoticeDate(new Date())
                            .setMenuId(ConstantsWorkbench.sale_contract)
                            .setUserId(contract.getCreatorAccountId());
                    sysToexpireBusinessList.add(sysToexpireBusiness);
                }
            });
        }
        log.info("=====>开始查询已逾期销售合同");
        List<SalSaleContract> saleContractAlreadyList = salSaleContractMapper.getOverdueBusiness();
        if (CollectionUtil.isNotEmpty(saleContractAlreadyList)) {
            saleContractAlreadyList.forEach(contract -> {
                SysOverdueBusiness sysOverdueBusiness = new SysOverdueBusiness();
                if (contract.getCreatorAccountId()!=null){
                    String endDate = DateUtil.format(contract.getEndDate(), "yyyy-MM-dd");
                    sysOverdueBusiness.setClientId(contract.getClientId())
                            .setTitle(contract.getCustomerShortName() + "的销售合同 " + contract.getSaleContractCode() + " 已逾期，合同有效期至" + endDate)
                            .setTableName(ConstantsTable.TABLE_SALE_CONTRACT)
                            .setDocumentSid(contract.getSaleContractSid())
                            .setDocumentCode(contract.getSaleContractCode())
                            .setExpiredDate(contract.getEndDate())
                            .setNoticeDate(new Date())
                            .setMenuId(ConstantsWorkbench.sale_contract)
                            .setUserId(contract.getCreatorAccountId());
                    sysOverdueBusinessList.add(sysOverdueBusiness);
                }
            });
        }
        log.info("=====>销售合同查询结束");
        log.info("=====>更新新数据");
        // 删除过期的数据
        String[] tableNames = new String[]{ConstantsTable.TABLE_PURCHASE_CONTRACT,ConstantsTable.TABLE_SALE_CONTRACT};
        sysToexpireBusinessMapper.delete(new SysToexpireBusiness().setTableNameList(tableNames));
        sysOverdueBusinessMapper.delete(new SysOverdueBusiness().setTableNameList(tableNames));
        // 写入即将到期的合同
        if (CollectionUtil.isNotEmpty(sysToexpireBusinessList)){
            sysToexpireBusinessMapper.inserts(sysToexpireBusinessList);
        }
        // 写入已逾期的合同
        if (CollectionUtil.isNotEmpty(sysOverdueBusinessList)){
            sysOverdueBusinessMapper.inserts(sysOverdueBusinessList);
        }
        log.info("=====>更新完成");
    }

    public void sentNotice(){
        log.info("=====>开始查询即将到期销售合同");
        Date now=new Date();
        List<SalSaleContract> contractList=salSaleContractMapper.selectList(new QueryWrapper<SalSaleContract>().lambda().eq(SalSaleContract::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .ge(SalSaleContract::getEndDate, DateUtil.format(now, "yyyy-MM-dd"))
                .le(SalSaleContract::getStartDate, DateUtil.format(now, "yyyy-MM-dd"))
                .le(SalSaleContract::getEndDate,  DateUtil.format(DateUtil.offset(now, DateField.DAY_OF_MONTH, 3), "yyyy-MM-dd")));
        if(ArrayUtil.isNotEmpty(contractList)){
            contractList.forEach(c->{
                String markdowntext="<font color=\"warning\">销售合同即将到期通知</font> \n" +
                        ">销售合同号：<font color=\"info\">"+c.getSaleContractCode()+"</font> \n" +
                        ">合同交期：<font color=\"info\">"+c.getEndDate()+"</font> \n" +
                        ">销售员：<font color=\"info\">"+c.getSalePerson()+"</font> \n" +
                        ">备注：<font color=\"info\">"+c.getRemark()+"</font>";
                try {
                    //         QiYePushUtil.SendQyMsgMarkdown(WxConstants.ALL_TOUSER, WxConstants.SCM_AGENT_ID, markdowntext);
                }catch (Exception e){
                    e.printStackTrace();
                    log.info("企业微信推送失败");
                }
                String mailtext="<font color=\"warning\">销售合同即将到期通知</font>   <br />" +
                        "销售合同号：<font color=\"info\">"+c.getSaleContractCode()+"</font>  <br />" +
                        "合同交期：<font color=\"info\">"+c.getEndDate()+"</font>  <br />"+
                        "销售员：<font color=\"info\">"+c.getSalePerson()+"</font>  <br />"  +
                        "备注：<font color=\"info\">"+c.getRemark()+"</font>";
                try{
                    MailUtil.send("414254651@qq.com","1210236541@qq.com",null, "【即将到期通知】销售合同"+c.getSaleContractCode()+"即将到期", mailtext, true);
                }catch (Exception e){
                    e.printStackTrace();
                    log.info("邮件发送失败");
                }
            });
        }
        log.info("=====>开始查询已逾期销售合同");
        contractList=salSaleContractMapper.selectList(new QueryWrapper<SalSaleContract>().lambda().eq(SalSaleContract::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .lt(SalSaleContract::getEndDate, DateUtil.format(new Date(), "yyyy-MM-dd")));
        if(ArrayUtil.isNotEmpty(contractList)){
            contractList.forEach(c->{
                String markdowntext="<font color=\"warning\">销售合同已逾期通知</font> \n" +
                        ">销售合同号：<font color=\"info\">"+c.getSaleContractCode()+"</font> \n" +
                        ">合同交期：<font color=\"info\">"+c.getEndDate()+"</font> \n" +
                        ">销售员：<font color=\"info\">"+c.getSalePerson()+"</font> \n" +
                        ">备注：<font color=\"info\">"+c.getRemark()+"</font>";
                try {
                    //       QiYePushUtil.SendQyMsgMarkdown(WxConstants.ALL_TOUSER, WxConstants.SCM_AGENT_ID, markdowntext);
                }catch (Exception e){
                    e.printStackTrace();
                    log.info("企业微信推送失败");
                }
                String mailtext="<font color=\"warning\">销售合同已逾期通知</font>   <br />" +
                        "销售合同号：<font color=\"info\">"+c.getSaleContractCode()+"</font>  <br />" +
                        "合同交期：<font color=\"info\">"+c.getEndDate()+"</font>  <br />"+
                        "销售员：<font color=\"info\">"+c.getSalePerson()+"</font>  <br />"  +
                        "备注：<font color=\"info\">"+c.getRemark()+"</font>";
                try{
                    MailUtil.send("414254651@qq.com","1210236541@qq.com",null, "【已逾期通知】销售合同"+c.getSaleContractCode()+"已逾期", mailtext, true);
                }catch (Exception e){
                    e.printStackTrace();
                    log.info("邮件发送失败");
                }
            });
        }
        log.info("==>处理结束");
    }


    /**
     * 每天上午9:30跑定时任务，查询出所有处理状态为“已确认”且签收状态(纸质协议)为“未签收”的销售框架协议，
     * 给协议的“销售员”、系统默认设置的“销售财务对接人员”触发待办事项，跟进框架协议及时签收
     */
    @Scheduled(cron = "15 30 09 * * *")
    public void saleContractKuangjia() {
        sysTodoTaskMapper.deleteIgnore(new SysTodoTask().setTitle("待签收，请及时签收")
                .setTableNameList(new String[]{ConstantsTable.TABLE_SALE_CONTRACT, ConstantsTable.TABLE_PURCHASE_CONTRACT}));
        // 销售
        SalSaleContract salSaleContract = new SalSaleContract();
        salSaleContract.setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setSignInStatus(ConstantsEms.SIGN_IN_STATUS_WQS);
        List<SalSaleContract> contractList = salSaleContractMapper.selectSaleContractKuangjia(salSaleContract);
        if (CollUtil.isNotEmpty(contractList)) {
            List<SysTodoTask> todoTaskList = new ArrayList<>();
            // 获取菜单id
            Long menuId = null;
            SysMenu menu = new SysMenu();
            menu.setMenuName(ConstantsWorkbench.TODO_SAL_CONTRACT_MENU_NAME);
            try {
                menu = remoteMenuService.getInfoByName(menu).getData();
            } catch (Exception e){
                log.warn(ConstantsWorkbench.TODO_SAL_CONTRACT_MENU_NAME + "菜单获取失败！");
            }
            if (menu != null && menu.getMenuId() != null) {
                menuId = menu.getMenuId();
            }
            for (SalSaleContract contract : contractList) {
                if (contract.getSalePersonId() != null) {
                    SysTodoTask sysTodoTask1 = new SysTodoTask();
                    sysTodoTask1.setNoticeDate(new Date()).setMenuId(menuId)
                            .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName(ConstantsTable.TABLE_SALE_CONTRACT);
                    sysTodoTask1.setClientId(contract.getClientId())
                            .setDocumentSid(contract.getSaleContractSid())
                            .setDocumentCode(contract.getSaleContractCode());
                    sysTodoTask1.setTitle("销售合同" + contract.getSaleContractCode() + "待签收，请及时签收！")
                            .setUserId(contract.getSalePersonId());
                    todoTaskList.add(sysTodoTask1);
                }
                if (StrUtil.isNotBlank(contract.getSaleFinanceAccountId())) {
                    String[] userId = contract.getSaleFinanceAccountId().split(";");
                    for (String id : userId) {
                        if (contract.getSalePersonId() == null || !id.equals(String.valueOf(contract.getSalePersonId()))) {
                            SysTodoTask sysTodoTask2 = new SysTodoTask();
                            sysTodoTask2.setNoticeDate(new Date()).setMenuId(menuId)
                                    .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                    .setTableName(ConstantsTable.TABLE_SALE_CONTRACT);
                            sysTodoTask2.setClientId(contract.getClientId())
                                    .setDocumentSid(contract.getSaleContractSid())
                                    .setDocumentCode(contract.getSaleContractCode());
                            sysTodoTask2.setTitle("销售合同" + contract.getSaleContractCode() + "待签收，请及时签收！")
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

        // 采购
        PurPurchaseContract purchaseContract = new PurPurchaseContract();
        purchaseContract.setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setSignInStatus(ConstantsEms.SIGN_IN_STATUS_WQS);
        List<PurPurchaseContract> purchaseContractList = purPurchaseContractMapper.selectPurchaseContractKuangjia(purchaseContract);
        if (CollUtil.isNotEmpty(purchaseContractList)) {
            List<SysTodoTask> todoTaskList = new ArrayList<>();
            // 获取菜单id
            Long menuId = null;
            SysMenu menu = new SysMenu();
            menu.setMenuName(ConstantsWorkbench.TODO_PUR_CONTRACT_MENU_NAME);
            try {
                menu = remoteMenuService.getInfoByName(menu).getData();
            } catch (Exception e){
                log.warn(ConstantsWorkbench.TODO_PUR_CONTRACT_MENU_NAME + "菜单获取失败！");
            }
            if (menu != null && menu.getMenuId() != null) {
                menuId = menu.getMenuId();
            }
            for (PurPurchaseContract contract : purchaseContractList) {
                if (contract.getBuyerId() != null) {
                    SysTodoTask sysTodoTask1 = new SysTodoTask();
                    sysTodoTask1.setNoticeDate(new Date()).setMenuId(menuId)
                            .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName(ConstantsTable.TABLE_PURCHASE_CONTRACT);
                    sysTodoTask1.setClientId(contract.getClientId())
                            .setDocumentSid(contract.getPurchaseContractSid())
                            .setDocumentCode(contract.getPurchaseContractCode());
                    sysTodoTask1.setTitle("采购合同" + contract.getPurchaseContractCode() + "待签收，请及时签收！")
                            .setUserId(contract.getBuyerId());
                    todoTaskList.add(sysTodoTask1);
                }
                if (StrUtil.isNotBlank(contract.getPurchaseFinanceAccountId())) {
                    String[] userId = contract.getPurchaseFinanceAccountId().split(";");
                    for (String id : userId) {
                        if (contract.getBuyerId() == null || !id.equals(String.valueOf(contract.getBuyerId()))) {
                            SysTodoTask sysTodoTask2 = new SysTodoTask();
                            sysTodoTask2.setNoticeDate(new Date()).setMenuId(menuId)
                                    .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                    .setTableName(ConstantsTable.TABLE_PURCHASE_CONTRACT);
                            sysTodoTask2.setClientId(contract.getClientId())
                                    .setDocumentSid(contract.getPurchaseContractSid())
                                    .setDocumentCode(contract.getPurchaseContractCode());
                            sysTodoTask2.setTitle("采购合同" + contract.getPurchaseContractCode() + "待签收，请及时签收！")
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
     * 每天上午9:30跑定时任务，查询出所有处理状态为“已确认”且上传状态(纸质合同)为“未上传”的采购合同，
     * 给合同的“采购员”、系统默认设置的“采购财务对接人员”触发待办事项，跟进合同附件及时上传，待办消息内容：采购合同XXX待上传，请及时处理！
     *
     * 每天上午9:30跑定时任务，查询出所有处理状态为“已确认”且上传状态(纸质合同)为“未上传”的销售合同，
     * 给合同的“销售员”、系统默认设置的“销售财务对接人员”触发待办事项，跟进合同附件及时上传，待办消息内容：销售合同XXX待上传，请及时处理！
     */
    @Scheduled(cron = "45 30 09 * * *")
    public void saleContractUpload() {
        // 删除
        sysTodoTaskMapper.deleteIgnore(new SysTodoTask().setTitle("待上传，请及时处理！")
                .setTableNameList(new String[]{ConstantsTable.TABLE_SALE_CONTRACT, ConstantsTable.TABLE_PURCHASE_CONTRACT}));
        // 销售合同
        SalSaleContract saleContract = new SalSaleContract();
        saleContract.setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS);
        List<SalSaleContract> saleContractList = salSaleContractMapper.selectSaleContractKuangjia(saleContract);
        if (CollUtil.isNotEmpty(saleContractList)) {
            List<SysTodoTask> todoTaskList = new ArrayList<>();
            // 获取菜单id
            Long menuId = null;
            SysMenu menu = new SysMenu();
            menu.setMenuName(ConstantsWorkbench.TODO_SAL_CONTRACT_MENU_NAME);
            try {
                menu = remoteMenuService.getInfoByName(menu).getData();
            } catch (Exception e){
                log.warn(ConstantsWorkbench.TODO_SAL_CONTRACT_MENU_NAME + "菜单获取失败！");
            }
            if (menu != null && menu.getMenuId() != null) {
                menuId = menu.getMenuId();
            }
            for (SalSaleContract contract : saleContractList) {
                if (contract.getSalePersonId() != null) {
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    sysTodoTask.setClientId(contract.getClientId())
                            .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName(ConstantsTable.TABLE_SALE_CONTRACT)
                            .setDocumentSid(contract.getSaleContractSid());
                    sysTodoTask.setTitle("销售合同" + contract.getSaleContractCode() + "待上传，请及时处理！")
                            .setDocumentCode(contract.getSaleContractCode())
                            .setNoticeDate(new Date())
                            .setMenuId(menuId)
                            .setUserId(contract.getSalePersonId());
                    todoTaskList.add(sysTodoTask);
                }
                if (StrUtil.isNotBlank(contract.getSaleFinanceAccountId())) {
                    String[] userId = contract.getSaleFinanceAccountId().split(";");
                    for (String id : userId) {
                        if (contract.getSalePersonId() == null || !id.equals(String.valueOf(contract.getSalePersonId()))) {
                            SysTodoTask sysTodoTask2 = new SysTodoTask();
                            sysTodoTask2.setClientId(contract.getClientId())
                                    .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                    .setTableName(ConstantsTable.TABLE_SALE_CONTRACT)
                                    .setDocumentSid(contract.getSaleContractSid());
                            sysTodoTask2.setTitle("销售合同" + contract.getSaleContractCode() + "待上传，请及时处理！")
                                    .setDocumentCode(contract.getSaleContractCode())
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

        // 采购合同
        PurPurchaseContract purchaseContract = new PurPurchaseContract();
        purchaseContract.setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS);
        List<PurPurchaseContract> purchaseContractList = purPurchaseContractMapper.selectPurchaseContractKuangjia(purchaseContract);
        if (CollUtil.isNotEmpty(purchaseContractList)) {
            List<SysTodoTask> todoTaskList = new ArrayList<>();
            // 获取菜单id
            Long menuId = null;
            SysMenu menu = new SysMenu();
            menu.setMenuName(ConstantsWorkbench.TODO_PUR_CONTRACT_MENU_NAME);
            try {
                menu = remoteMenuService.getInfoByName(menu).getData();
            } catch (Exception e){
                log.warn(ConstantsWorkbench.TODO_PUR_CONTRACT_MENU_NAME + "菜单获取失败！");
            }
            if (menu != null && menu.getMenuId() != null) {
                menuId = menu.getMenuId();
            }
            for (PurPurchaseContract contract : purchaseContractList) {
                if (contract.getBuyerId() != null) {
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    sysTodoTask.setClientId(contract.getClientId())
                            .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName(ConstantsTable.TABLE_PURCHASE_CONTRACT)
                            .setDocumentSid(contract.getPurchaseContractSid());
                    sysTodoTask.setTitle("采购合同" + contract.getPurchaseContractCode() + "待上传，请及时处理！")
                            .setDocumentCode(contract.getPurchaseContractCode())
                            .setNoticeDate(new Date())
                            .setMenuId(menuId)
                            .setUserId(contract.getBuyerId());
                    todoTaskList.add(sysTodoTask);
                }
                if (StrUtil.isNotBlank(contract.getPurchaseFinanceAccountId())) {
                    String[] userId = contract.getPurchaseFinanceAccountId().split(";");
                    for (String id : userId) {
                        if (contract.getBuyerId() == null || !id.equals(String.valueOf(contract.getBuyerId()))) {
                            SysTodoTask sysTodoTask2 = new SysTodoTask();
                            sysTodoTask2.setClientId(contract.getClientId())
                                    .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                    .setTableName(ConstantsTable.TABLE_PURCHASE_CONTRACT)
                                    .setDocumentSid(contract.getPurchaseContractSid());
                            sysTodoTask2.setTitle("采购合同" + contract.getPurchaseContractCode() + "待上传，请及时处理！")
                                    .setDocumentCode(contract.getPurchaseContractCode())
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
