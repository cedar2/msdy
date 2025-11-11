package com.platform.ems.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.api.service.RemoteSystemService;
import com.platform.api.service.RemoteUserService;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.security.utils.dingtalk.DdPushUtil;
import com.platform.common.security.utils.dingtalk.DingtalkConstants;
import com.platform.common.security.utils.wx.QiYePushUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsInventory;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.ManManufactureOrderProduct;
import com.platform.ems.domain.RepBusinessRemindSo;
import com.platform.ems.domain.SalSalesOrder;
import com.platform.ems.domain.SalSalesOrderItem;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ISalSalesOrderItemService;
import com.platform.system.domain.SysOverdueBusiness;
import com.platform.system.domain.SysToexpireBusiness;
import com.platform.system.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 销售订单到期&&逾期预警
 *
 * @author yangqz
 */
@Service
@EnableScheduling
@Component
@SuppressWarnings("all")
@Slf4j
public class SalSaleOrderWarningTask {
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;
    @Autowired
    private SalSalesOrderItemMapper salSalesOrderItemMapper;
    @Autowired
    private PurPurchaseOrderItemMapper purPurchaseOrderItemMapper;
    @Autowired
    private SysToexpireBusinessMapper sysToexpireBusinessMapper;
    @Autowired
    private SysOverdueBusinessMapper sysOverdueBusinessMapper;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private RepBusinessRemindSoMapper repBusinessRemindSoMapper;
    @Autowired
    private ISalSalesOrderItemService salSalesOrderItemService;
    @Autowired
    private SysDefaultSettingClientMapper sysDefaultSettingClientMapper;
    @Autowired
    private RemoteSystemService remoteSystemService;

    @Scheduled(cron = "00 25 01 * * * ")
    public void earlyWarning() {

        log.info("=====>删除即将到期已逾期旧数据");
        // 删除旧的缓存表数据
        repBusinessRemindSoMapper.delete(new RepBusinessRemindSo());
        // 删除销售订单即将到期预警通知表的数据
        sysToexpireBusinessMapper.delete(new SysToexpireBusiness().setTableName(ConstantsEms.TABLE_SALEORDER_ORDER));
        // 删除销售订单已逾期预警通知表的数据
        sysOverdueBusinessMapper.delete(new SysOverdueBusiness().setTableName(ConstantsEms.TABLE_SALEORDER_ORDER));
        // 待写入缓存表的数据
        List<RepBusinessRemindSo> repBusinessRemindSoList = new ArrayList<>();
        // 待写入即将到期预警通知表的数据
        List<SysToexpireBusiness> toexpireBusinessList = new ArrayList<>();
        // 待写入已逾期预警通知表的数据
        List<SysOverdueBusiness> overdueBusinessList = new ArrayList<>();
        log.info("=====>开始查询即将到期销售订单");
        // 获取即将到期销售订单明细
        List<SalSalesOrderItem> salSalesOrderItemToexpireList = salSalesOrderItemMapper.getToexpireBusiness();
        // 处理即将到期数据
        if (CollectionUtil.isNotEmpty(salSalesOrderItemToexpireList)) {
            // 计算库存数据
            try {
                salSalesOrderItemToexpireList = salSalesOrderItemService.handleIndex(salSalesOrderItemToexpireList);
            } catch (Exception e) {
                log.error("方法 handleIndex 报错，可能会没有获取到出入库量和发货量等数据");
            }
            salSalesOrderItemToexpireList = salSalesOrderItemService.handleIndex(salSalesOrderItemToexpireList);
            // 判断订单是否已发送预警提醒
            Map<Long, String> toexpireMap = new HashMap<>();
            Map<Long, List<SalSalesOrderItem>> itemListMap = salSalesOrderItemToexpireList.stream()
                    .collect(Collectors.groupingBy(o -> o.getSalesOrderSid()));
            // 遍历
            salSalesOrderItemToexpireList.forEach(item -> {
                // 写入缓存表
                RepBusinessRemindSo repBusinessRemindSo = new RepBusinessRemindSo();
                BeanCopyUtils.copyProperties(item, repBusinessRemindSo);
                repBusinessRemindSo.setRemindType(ConstantsEms.JJDQ);
                repBusinessRemindSo.setSalesOrderCode(Long.parseLong(item.getSalesOrderCode()));
                repBusinessRemindSo.setQuantityDingd(item.getQuantity());
                repBusinessRemindSo.setQuantityWeifh(item.getPartQuantity());
                repBusinessRemindSoList.add(repBusinessRemindSo);
                // 对应订单主表只需要发送一个预警提醒
                if (!toexpireMap.containsKey(item.getSalesOrderSid()) || toexpireMap.get(item.getSalesOrderSid()) == null) {
                    toexpireMap.put(item.getSalesOrderSid(), "1");
                    // 存入待发送预警通知
                    SysToexpireBusiness toexpireBusiness = new SysToexpireBusiness();
                    toexpireBusiness.setTitle(item.getCustomerShortName() + ",  销售订单" + item.getSalesOrderCode() + "存在"
                                    + itemListMap.get(item.getSalesOrderSid()).size() + "行数据即将到期，请您及时关注")
                            .setTableName(ConstantsEms.TABLE_SALEORDER_ORDER)
                            .setDocumentSid(item.getSalesOrderSid())
                            .setDocumentCode(item.getSalesOrderCode().toString())
                            .setExpiredDate(item.getContractDate())
                            .setNoticeDate(new Date()).setClientId(item.getClientId())
                            .setUserId(item.getCreatorAccountId());
                    if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(item.getMaterialCategory())) {
                        toexpireBusiness.setMenuId(ConstantsWorkbench.sale_order_wl);
                    } else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(item.getMaterialCategory())) {
                        toexpireBusiness.setMenuId(ConstantsWorkbench.sale_order_sp);
                    }
                    toexpireBusinessList.add(toexpireBusiness);
                }
            });
        }
        log.info("=====>即将到期销售订单处理完成");
        log.info("=====>开始查询已逾期销售订单");
        // 获取已逾期销售订单明细
        List<SalSalesOrderItem> salSalesOrderItemOverdueList = salSalesOrderItemMapper.getOverdueBusiness();
        // 处理已逾期数据
        if (CollectionUtil.isNotEmpty(salSalesOrderItemOverdueList)) {
            // 计算库存数据
            try {
                salSalesOrderItemOverdueList = salSalesOrderItemService.handleIndex(salSalesOrderItemOverdueList);
            } catch (Exception e) {
                log.error("方法 handleIndex 报错，可能会没有获取到出入库量和发货量等数据");
            }
            // 判断订单是否已发送预警提醒
            Map<Long, String> toexpireMap = new HashMap<>();
            Map<Long, List<SalSalesOrderItem>> itemListMap = salSalesOrderItemOverdueList.stream()
                    .collect(Collectors.groupingBy(o -> o.getSalesOrderSid()));
            // 遍历
            salSalesOrderItemOverdueList.forEach(item -> {
                // 写入缓存表
                RepBusinessRemindSo repBusinessRemindSo = new RepBusinessRemindSo();
                BeanCopyUtils.copyProperties(item, repBusinessRemindSo);
                repBusinessRemindSo.setRemindType(ConstantsEms.YYQ);
                repBusinessRemindSo.setSalesOrderCode(Long.parseLong(item.getSalesOrderCode()));
                repBusinessRemindSo.setQuantityDingd(item.getQuantity());
                repBusinessRemindSo.setQuantityWeifh(item.getPartQuantity());
                repBusinessRemindSoList.add(repBusinessRemindSo);
                // 对应订单主表只需要发送一个预警提醒
                if (!toexpireMap.containsKey(item.getSalesOrderSid()) || toexpireMap.get(item.getSalesOrderSid()) == null) {
                    toexpireMap.put(item.getSalesOrderSid(), "1");
                    // 存入待发送预警通知
                    SysOverdueBusiness overdueBusiness = new SysOverdueBusiness();
                    overdueBusiness.setTitle(item.getCustomerShortName() + ",  销售订单" + item.getSalesOrderCode() + "存在"
                                    + itemListMap.get(item.getSalesOrderSid()).size() + "行数据已逾期，请您及时关注")
                            .setTableName(ConstantsEms.TABLE_SALEORDER_ORDER)
                            .setDocumentSid(item.getSalesOrderSid())
                            .setDocumentCode(item.getSalesOrderCode().toString())
                            .setExpiredDate(item.getContractDate())
                            .setNoticeDate(new Date()).setClientId(item.getClientId())
                            .setUserId(item.getCreatorAccountId());
                    if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(item.getMaterialCategory())) {
                        overdueBusiness.setMenuId(ConstantsWorkbench.sale_order_wl);
                    } else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(item.getMaterialCategory())) {
                        overdueBusiness.setMenuId(ConstantsWorkbench.sale_order_sp);
                    }
                    overdueBusinessList.add(overdueBusiness);
                }
            });
        }
        log.info("=====>已逾期销售订单处理完成");
        log.info("=====>开始写入缓存表与预警逾期通知表");
        // 写入缓存表
        if (CollectionUtil.isNotEmpty(repBusinessRemindSoList)) {
            repBusinessRemindSoMapper.insertAll(repBusinessRemindSoList);
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

    }

    public File getEmptyExcelFile(XSSFWorkbook workbook, String msg) throws IOException {
        File outputFile = File.createTempFile(msg, ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            workbook.write(fos);
            return outputFile;
        }
    }

    @Scheduled(cron = "00 12 02 * * * ")
    public void updateInvStatus() {

        List<SalSalesOrder> orderListCk = salSalesOrderMapper.selectInOutStockStatusCk();
        if (CollectionUtil.isNotEmpty(orderListCk)) {
            Long[] sids = orderListCk.stream().map(SalSalesOrder::getSalesOrderSid).toArray(Long[]::new);
            System.out.println("更新全部出库" + sids);
            salSalesOrderMapper.update(null, new UpdateWrapper<SalSalesOrder>().lambda()
                    .in(SalSalesOrder::getSalesOrderSid, sids)
                    .set(SalSalesOrder::getInOutStockStatus, ConstantsInventory.IN_OUT_STORE_STATUS_QBCK));
        }

        List<SalSalesOrder> orderListRk = salSalesOrderMapper.selectInOutStockStatusRk();
        if (CollectionUtil.isNotEmpty(orderListRk)) {
            Long[] sids = orderListRk.stream().map(SalSalesOrder::getSalesOrderSid).toArray(Long[]::new);
            System.out.println("更新全部入库" + sids);
            salSalesOrderMapper.update(null, new UpdateWrapper<SalSalesOrder>().lambda()
                    .in(SalSalesOrder::getSalesOrderSid, sids)
                    .set(SalSalesOrder::getInOutStockStatus, ConstantsInventory.IN_OUT_STORE_STATUS_QBRK));
        }

    }

    @Autowired
    private ManManufactureOrderProductMapper manufactureOrderProductMapper;
    @Autowired
    private SysDefaultSettingClientMapper defaultSettingClientMapper;
    @Resource
    private SysClientMapper sysClientMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Value("${env.prefix}")
    private String env;

    @Scheduled(cron = "00 10 10 * * * ")
    public void daiPaichang() {
        // 日期格式化器
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SysDefaultSettingClient client = defaultSettingClientMapper.selectSysDefaultSettingClientById(ConstantsEms.CLIENT_ID_10001);
        /**
         * 1、获取待排产销售订单信息并进行排产提醒，具体逻辑如下
         * 1）从销售订单明细数据库表中获取【“处理状态”为“已确认”+“是否排产”为“是”】的“销售订单号、商品编码、合同交期”
         * 2）第1）步中获取的数据按“销售订单号、商品编码、合同交期”进行去重
         * 3）在非“已作废”状态的生产订单的“商品明细”数据表中，如“销售订单号+商品编码+合同交期”不存在，继续如下校验：
         *   》如“合同交期 - 当前日期 <= 商品未排产提醒天数（此字段从商品档案数据库表中获取）”，
         *   则给对应销售订单的“销售员”和销售待排产通知人员发送移动端消息提醒（移动端消息提醒样式见下图）
         *       注：发送业务动态提醒的人员要做去重
         */
        // 存放提醒人员的id
        Map<String , String> map = new HashMap<>();
        List<SalSalesOrderItem> salesOrderItemList = salSalesOrderItemMapper.getItemList(new SalSalesOrderItem()
                .setHandleStatus(ConstantsEms.CHECK_STATUS)
                .setIsManufacture(ConstantsEms.YES).setMaterialCategory(ConstantsEms.MATERIAL_CATEGORY_SP));
        if (CollectionUtil.isNotEmpty(salesOrderItemList)) {
            salesOrderItemList = salesOrderItemList.stream()
                    .filter(order -> order.getSalesOrderCode() != null && order.getMaterialCode() != null
                            && order.getContractDate() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(salesOrderItemList)) {
                Map<String, List<SalSalesOrderItem>> groupedOrders = salesOrderItemList.stream()
                        .collect(Collectors.groupingBy(order -> order.getSalesOrderCode() + "_" + order.getMaterialCode()
                                + "_" + dateFormat.format(order.getContractDate())));
                for (String key : groupedOrders.keySet()) {
                    List<SalSalesOrderItem> orderItemList = groupedOrders.get(key);
                    if (map.containsKey(orderItemList.get(0).getSalePersonId())) {
                        continue;
                    }
                    //
                    List<ManManufactureOrderProduct> productList = manufactureOrderProductMapper
                            .selectManManufactureList(new ManManufactureOrderProduct()
                                    .setSalesOrderCode(orderItemList.get(0).getSalesOrderCode())
                                    .setMaterialCode(orderItemList.get(0).getMaterialCode())
                                    .setContractDate(dateFormat.format(orderItemList.get(0).getContractDate())));
                    //
                    if (CollectionUtil.isEmpty(productList)) {
                        for (SalSalesOrderItem orderItem : orderItemList) {
                            if (StrUtil.isBlank(orderItem.getSalePersonId())) {
                                continue;
                            }
                            if (orderItem.getWpcRemindDays() == null) {
                                continue;
                            }
                            // 将 Date 转换为 LocalDate
                            LocalDate contractLocalDate = orderItem.getContractDate().toInstant()
                                    .atZone(ZoneId.systemDefault()).toLocalDate();
                            LocalDate currentDate = LocalDate.now();
                            // 计算两个 LocalDate 之间的天数差
                            long tianshu = ChronoUnit.DAYS.between(currentDate, contractLocalDate);
                            int remd = orderItem.getWpcRemindDays();
                            if (tianshu <= (long) remd) {
                                map.put(orderItem.getSalePersonId(), "1");
                                if (StrUtil.isNotBlank(client.getSaleDpcNoticeAccount())) {
                                    String[] ids = client.getSaleDpcNoticeAccount().split(";");
                                    for (String id : ids) {
                                        SysUser sysUser = sysUserMapper.selectSysUserByName(id);
                                        if (sysUser != null) {
                                            map.put(String.valueOf(sysUser.getUserId()), "1");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // 发送消息
        if (map != null && map.size() > 0) {
            // 租户配置
            SysClient sysClient = sysClientMapper.selectSysClientById(ConstantsEms.CLIENT_ID_10001);

            // 企微
            if ("QW".equals(sysClient.getImSoftware())) {
                // 对应的租户配置
                SysUser user = new SysUser();
                user.setWorkWechatAppkey(sysClient.getWorkWechatAppkey());
                user.setWorkWechatAppsecret(sysClient.getWorkWechatAppsecret());
                user.setWorkWechatAgentid(sysClient.getWorkWechatAgentid());
                for (String key : map.keySet()) {
                    SysUser sysUser = sysUserMapper.selectById(key);
                    // 根据openid发送消息
                    if (sysUser.getWorkWechatOpenid() != null  && !"".equals(sysUser.getWorkWechatOpenid())){
                        user.setWorkWechatOpenid(sysUser.getWorkWechatOpenid());
                        String description = "\n<div class=\"normal\">销售订单未排产提醒</div> \n" +
                                "<div class=\"normal\">存在未排产的销售订单数据，请及时跟进！</div>";
                        // 跳转
                        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + sysClient.getWorkWechatAppkey() + "&redirect_uri=" +
                                env + "%2F%3FjumpOrderNoSchedule%3D1%26platform%3DqiyeLogin%26response_type%3Dcode%26scope%3Dsnsapi_base#wechat_redirect";
                        QiYePushUtil.sendQyMsgTextCard(user, "用户工作台提醒", description, url);
                    }
                }
            }
            // 钉钉
            else if ("DD".equals(sysClient.getImSoftware())) {
                // 对应的租户配置
                SysUser user = new SysUser();
                user.setDingtalkAppkey(sysClient.getDingtalkAppkey());
                user.setDingtalkAppsecret(sysClient.getDingtalkAppsecret());
                user.setDingtalkAgentid(sysClient.getDingtalkAgentid());
                for (String key : map.keySet()) {
                    SysUser sysUser = sysUserMapper.selectById(key);
                    // 根据openid发送消息
                    if (sysUser.getDingtalkOpenid() != null  && !"".equals(sysUser.getDingtalkOpenid())){
                        user.setDingtalkOpenid(sysUser.getDingtalkOpenid());
                        // 内容
                        String title = "销售订单未排产提醒";
                        JSONObject textJson = new JSONObject();
                        textJson.put("msgtype", DingtalkConstants.MSG_TYPE_OA);
                        JSONObject oaJson = new JSONObject();
                        oaJson.put("message_url", env +"/?platform=dingTalkLogin&jumpOrderNoSchedule=1");
                        JSONObject oaJson1 = new JSONObject();
                        oaJson1.put("bgcolor", "FF0097FF");
                        oaJson1.put("text", "");
                        oaJson.put("head", oaJson1);
                        JSONObject oaJson2 = new JSONObject();
                        oaJson2.put("title", title);
                        JSONObject oaJson3 = new JSONObject();
                        oaJson3.put("key", "存在未排产的销售订单数据，请及时跟进！");
                        oaJson3.put("value", "");
                        List<JSONObject> list = new ArrayList<>();
                        list.add(oaJson3);
                        oaJson2.put("form", list);
                        oaJson.put("body", oaJson2);
                        textJson.put("oa", oaJson);
                        DdPushUtil.SendDdMsg(user, textJson);
                    }
                }
            }
        }
    }
}
