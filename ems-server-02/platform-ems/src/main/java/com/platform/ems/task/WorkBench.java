package com.platform.ems.task;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.constant.UserConstants;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.security.utils.dingtalk.DdPushUtil;
import com.platform.common.security.utils.dingtalk.DingtalkConstants;
import com.platform.common.security.utils.wx.QiYePushUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysOverdueBusiness;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.domain.SysToexpireBusiness;
import com.platform.system.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 移动端：消息提醒-用户工作台提醒
 *
 * @author chenkw
 */
@Service
@EnableScheduling
@Component
@SuppressWarnings("all")
@Slf4j
public class WorkBench {

    @Resource
    private SysUserWorkItemCacheMapper workItemCacheMapper;
    @Resource
    private SysTodoTaskMapper todoTaskMapper;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private SysOverdueBusinessMapper overdueBusinessMapper;
    @Autowired
    private SysToexpireBusinessMapper toexpireBusinessMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysClientMapper sysClientMapper;
    @Value("${env.prefix}")
    private String env;

    @Scheduled(cron = "00 00 02 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void updateCache() {

        /**
         * 3.1 获取所要通知的用户账号及其所要提醒的各项的数量
         *  1）清空“用户工作台提醒缓存表”
         *  2）获取“待办、待批、已逾期、即将到期”数据库表中的数据
         *  3）第2）步中获取的数据根据“租户ID、用户账号”进行去重
         *  4）获取去重后的“租户ID、用户账号”，根据“租户ID、用户账号”逐个从“待办、待批、已逾期、即将到期”数据库表获取此用户账号的数据
         *       并统计对应的“待办项数量、待批项数量、已逾期项数量、即将到期项数量”，并将汇总的数据存放在“用户工作台提醒缓存表”中。
         */

        workItemCacheMapper.deleteAll(new SysUserWorkItemCache());

        List<SysTodoTask> todoTaskList = todoTaskMapper.selectListAll(new SysTodoTask());

        List<SysOverdueBusiness> overList = overdueBusinessMapper.selectListAll(new SysOverdueBusiness());

        List<SysToexpireBusiness> toexpireList = toexpireBusinessMapper.selectListAll(new SysToexpireBusiness());

        // 租户+用户id
        HashMap<String, SysUserWorkItemCache> cacheHashMap = new HashMap<>();

        // 待办待批
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            // 第一步按租户分组
            Map<String, List<SysTodoTask>> clientMap =  todoTaskList.stream().collect(Collectors.groupingBy(v -> v.getClientId()));
            if (clientMap != null && clientMap.size() > 0) {
                for (String clientId : clientMap.keySet()) {
                    List<SysTodoTask> clientList = clientMap.get(clientId);
                    if (clientList != null && clientMap.size() > 0) {
                        // 第二步按用户分组
                        Map<Long, List<SysTodoTask>> userMap =  clientList.stream().collect(Collectors.groupingBy(v -> v.getUserId()));
                        if (userMap != null && userMap.size() > 0) {
                            for (Long userId : userMap.keySet()) {
                                List<SysTodoTask> userList = userMap.get(userId);
                                if (CollectionUtil.isNotEmpty(userList)) {
                                    // 写入
                                    SysUserWorkItemCache cache = new SysUserWorkItemCache(clientId, userId);
                                    cache.setCreateDate(new Date()).setCreatorAccount("admin");
                                    cache.setClientId(clientId).setUserId(userId);
                                    // 待办
                                    List<SysTodoTask> dbList = userList.stream().filter(o-> ConstantsEms.TODO_TASK_DB.equals(o.getTaskCategory())).collect(Collectors.toList());
                                    if (CollectionUtil.isNotEmpty(dbList)) {
                                        cache.setDbQuantity(dbList.size());
                                    }
                                    // 待批
                                    List<SysTodoTask> dpList = userList.stream().filter(o-> ConstantsEms.TODO_TASK_DP.equals(o.getTaskCategory())).collect(Collectors.toList());
                                    if (CollectionUtil.isNotEmpty(dpList)) {
                                        cache.setDpQuantity(dpList.size());
                                    }
                                    cacheHashMap.put(clientId + "-" + userId, cache);
                                }
                            }
                        }
                    }
                }
            }
        }

        // 已逾期
        if (CollectionUtil.isNotEmpty(overList)) {
            // 第一步按租户分组
            Map<String, List<SysOverdueBusiness>> clientMap = overList.stream().collect(Collectors.groupingBy(v -> v.getClientId()));
            if (clientMap != null && clientMap.size() > 0) {
                for (String clientId : clientMap.keySet()) {
                    List<SysOverdueBusiness> clientList = clientMap.get(clientId);
                    if (clientList != null && clientMap.size() > 0) {
                        // 第二步按用户分组
                        Map<Long, List<SysOverdueBusiness>> userMap = clientList.stream().collect(Collectors.groupingBy(v -> v.getUserId()));
                        if (userMap != null && userMap.size() > 0) {
                            for (Long userId : userMap.keySet()) {
                                List<SysOverdueBusiness> userList = userMap.get(userId);
                                if (CollectionUtil.isNotEmpty(userList)) {
                                    if (cacheHashMap.containsKey(clientId + "-" + userId)) {
                                        SysUserWorkItemCache itemCache = cacheHashMap.get(clientId + "-" + userId);
                                        itemCache.setYyqQuantity(userList.size());
                                        cacheHashMap.put(clientId + "-" + userId, itemCache);
                                    }
                                    else {
                                        // 写入
                                        SysUserWorkItemCache cache = new SysUserWorkItemCache(clientId, userId);
                                        cache.setCreateDate(new Date()).setCreatorAccount("admin");
                                        cache.setClientId(clientId).setUserId(userId).setYyqQuantity(userList.size());
                                        cacheHashMap.put(clientId + "-" + userId, cache);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 即将到期
        if (CollectionUtil.isNotEmpty(toexpireList)) {
            // 第一步按租户分组
            Map<String, List<SysToexpireBusiness>> clientMap = toexpireList.stream().collect(Collectors.groupingBy(v -> v.getClientId()));
            if (clientMap != null && clientMap.size() > 0) {
                for (String clientId : clientMap.keySet()) {
                    List<SysToexpireBusiness> clientList = clientMap.get(clientId);
                    if (clientList != null && clientMap.size() > 0) {
                        // 第二步按用户分组
                        Map<Long, List<SysToexpireBusiness>> userMap = clientList.stream().collect(Collectors.groupingBy(v -> v.getUserId()));
                        if (userMap != null && userMap.size() > 0) {
                            for (Long userId : userMap.keySet()) {
                                List<SysToexpireBusiness> userList = userMap.get(userId);
                                if (CollectionUtil.isNotEmpty(userList)) {
                                    if (cacheHashMap.containsKey(clientId + "-" + userId)) {
                                        SysUserWorkItemCache itemCache = cacheHashMap.get(clientId + "-" + userId);
                                        itemCache.setJjdqQuantity(userList.size());
                                        cacheHashMap.put(clientId + "-" + userId, itemCache);
                                    }
                                    else {
                                        // 写入
                                        SysUserWorkItemCache cache = new SysUserWorkItemCache(clientId, userId);
                                        cache.setCreateDate(new Date()).setCreatorAccount("admin");
                                        cache.setClientId(clientId).setUserId(userId).setJjdqQuantity(userList.size());
                                        cacheHashMap.put(clientId + "-" + userId, cache);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 写入数据库
        List<SysUserWorkItemCache> workItemCacheList = new ArrayList<>();
        if (cacheHashMap != null && cacheHashMap.size() > 0) {
            for (String key : cacheHashMap.keySet()) {
                workItemCacheList.add(cacheHashMap.get(key));
            }
        }
        if (workItemCacheList.size() > 0) {
            workItemCacheMapper.inserts(workItemCacheList);
        }

    }

    @Scheduled(cron = "00 10 09 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void sentMsgCache() {

        /**
         * 3.2 给各个用户发送消息
         *   1）将“用户工作台提醒缓存表”中的数据，逐条通过如下逻辑进行消息发送：
         *   A. 从租户信息表（s_sys_client）中获取各租户ID的“租户所使用IM工具”（im_software）字段的值，并按如下逻辑处理：
         *   》若“租户所使用IM工具”为空，则无需发送消息；否则，继续如下判断
         *    > 若“租户所使用IM工具”为“企微”，则从用户信息表（sys_user）中获取第1）步对应分组下的用户ID的“企业微信openId”（work_wechat_openid），
         *    通过企业微信给该openid发送消息；若未获取到用户ID的“企业微信openId”，则无需发送消息
         *    > 若“租户所使用IM工具”为“钉钉”，则从用户信息表（sys_user）中获取第1）步对应分组下的用户ID的“钉钉openId”（dingtalk_openid），
         *    通过钉钉给该openid发送消息；若未获取到用户ID的“钉钉openId”，则无需发送消息
         *    > 若“租户所使用IM工具”为“飞书”，则从用户信息表（sys_user）中获取第1）步对应分组下的用户ID的“飞书openID”（feishu_open_id），
         *    通过飞书给该openid发送消息；若未获取到用户ID的“飞书openID”，则无需发送消息
         */

        List<SysUserWorkItemCache> cacheList = workItemCacheMapper.selectListAll(new SysUserWorkItemCache());
        if (CollectionUtil.isNotEmpty(cacheList)) {
            // 租户配置
            List<SysClient> clientList = sysClientMapper.selectSysClientAll(new SysClient());
            Map<String, SysClient> imMap = clientList.stream().collect(Collectors.toMap(SysClient::getClientId, Function.identity(), (t1, t2) -> t1));
            // 租户分组
            Map<String, List<SysUserWorkItemCache>> cacheMap =  cacheList.stream().collect(Collectors.groupingBy(v -> v.getClientId()));
            if (cacheMap != null && cacheMap.size() > 0) {
                for (String clientId : cacheMap.keySet()) {
                    SysClient client = imMap.get(clientId);
                    if (client == null) {
                        continue;
                    }
                    List<SysUserWorkItemCache> itemCacheList = cacheMap.get(clientId);
                    // 企微
                    if ("QW".equals(client.getImSoftware())) {
                        // 对应的租户配置
                        SysUser user = new SysUser();
                        user.setWorkWechatAppkey(client.getWorkWechatAppkey());
                        user.setWorkWechatAppsecret(client.getWorkWechatAppsecret());
                        user.setWorkWechatAgentid(client.getWorkWechatAgentid());
                        for (SysUserWorkItemCache cache : itemCacheList) {
                            // 根据openid发送消息
                            if (cache.getWorkWechatOpenid() != null  && !"".equals(cache.getWorkWechatOpenid())){
                                user.setWorkWechatOpenid(cache.getWorkWechatOpenid());
                                String description = "\n<div class=\"normal\">待办项数量:  " + cache.getDbQuantity() + "</div> \n" +
                                        "<div class=\"normal\">待批项数量:  " + cache.getDpQuantity() + "</div> \n" +
                                        "<div class=\"normal\">已逾期项数量:  " + cache.getYyqQuantity() + "</div> \n" +
                                        "<div class=\"normal\">即将到期项数量:  " + cache.getJjdqQuantity() + "</div>";
                                // 跳转
                                String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + client.getWorkWechatAppkey() +
                                        "&redirect_uri=" + env + "%2F%3FjumpWorkbench%3D1%26platform%3DqiyeLogin%26response_type%3Dcode%26scope%3Dsnsapi_base" +
                                        "#wechat_redirect";
                                QiYePushUtil.sendQyMsgTextCard(user, "用户工作台提醒", description, url);
                            }
                        }
                    }
                    // 钉钉
                    else if ("DD".equals(client.getImSoftware())) {
                        // 对应的租户配置
                        SysUser user = new SysUser();
                        user.setDingtalkAppkey(client.getDingtalkAppkey());
                        user.setDingtalkAppsecret(client.getDingtalkAppsecret());
                        user.setDingtalkAgentid(client.getDingtalkAgentid());
                        for (SysUserWorkItemCache cache : itemCacheList) {
                            // 根据openid发送消息
                            if (cache.getDingtalkOpenid() != null  && !"".equals(cache.getDingtalkOpenid())){
                                user.setDingtalkOpenid(cache.getDingtalkOpenid());
                                // 内容
                                String title = "用户工作台提醒";
                                JSONObject textJson = new JSONObject();
                                textJson.put("msgtype", DingtalkConstants.MSG_TYPE_OA);
                                JSONObject oaJson = new JSONObject();
                                oaJson.put("message_url", env +"/?platform=dingTalkLogin&jumpWorkbench=1");
                                JSONObject oaJson1 = new JSONObject();
                                oaJson1.put("bgcolor", "FF0097FF");
                                oaJson1.put("text", "");
                                oaJson.put("head", oaJson1);
                                JSONObject oaJson2 = new JSONObject();
                                oaJson2.put("title", title);
                                JSONObject oaJson3 = new JSONObject();
                                oaJson3.put("key", "待办项数量：");
                                oaJson3.put("value", cache.getDbQuantity());
                                JSONObject oaJson4 = new JSONObject();
                                oaJson4.put("key", "待批项数量：");
                                oaJson4.put("value", cache.getDpQuantity());
                                JSONObject oaJson5 = new JSONObject();
                                oaJson5.put("key", "已逾期项数量：");
                                oaJson5.put("value", cache.getYyqQuantity());
                                JSONObject oaJson6 = new JSONObject();
                                oaJson6.put("key", "即将到期项数量：");
                                oaJson6.put("value", cache.getJjdqQuantity());
                                List<JSONObject> list = new ArrayList<>();
                                list.add(oaJson3);
                                list.add(oaJson4);
                                list.add(oaJson5);
                                list.add(oaJson6);
                                oaJson2.put("form", list);
                                oaJson.put("body", oaJson2);
                                textJson.put("oa", oaJson);
                                DdPushUtil.SendDdMsg(user, textJson);
                            }
                        }
                    }
                    // 飞书
                    else if ("FS".equals(imMap.get(clientId))) {
                        for (SysUserWorkItemCache cache : itemCacheList) {
                            if (cache.getFeishuOpenId() != null  && !"".equals(cache.getFeishuOpenId())){

                            }
                        }
                    }

                }
            }
        }
    }

    /**
     * 1、新增定时作业（每天早上十点）
     * 若租户的“租户有效期(至)”不为空且“租户有效期(至)”>=当前日期 且“租户有效期(至)”-当前日期 < 30，
     * 则给对应租户ID的租户管理员账号发业务动态信息（PC端）：账号有效期到YYYY/MM/DD，请及时续费！
     * 其中，YYYY/MM/DD显示为租户有效期至
     */
    @Scheduled(cron = "00 00 10 * * *")
    public void sentClient() {
        //
        sysBusinessBcstMapper.deleteAllByTitle(new SysBusinessBcst().setTitle("账号有效期到"));
        //
        List<SysClient> clientList = sysClientMapper.selectSysClientAll(new SysClient());
        if (CollectionUtil.isNotEmpty(clientList)) {
            clientList = clientList.stream().filter(o->ConstantsEms.ENABLE_STATUS.equals(o.getStatus())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(clientList)) {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                for (SysClient client : clientList) {
                    LocalDate today = LocalDate.now();
                    if (client.getEndDate() != null) {
                        LocalDate endDate = client.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        System.out.println(">>>>>>>>>>>>>>" + today.plusDays(30).format(df));
                        if (!endDate.isBefore(today) && !today.plusDays(30).isBefore(endDate)) {
                            List<SysUser> userList = sysUserMapper.selectSysUserListAll(new SysUser()
                                    .setClientId(client.getClientId()).setUserType(UserConstants.USER_TYPE_ADMIN));
                            if (CollectionUtil.isNotEmpty(userList)) {
                                String dateStr = endDate.format(df);
                                for (SysUser user : userList) {
                                    //给admin发动态
                                    SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                                    sysBusinessBcst.setTitle("账号有效期到" + dateStr + "，请及时续费！")
                                            .setNoticeDate(new Date())
                                            .setDocumentSid(Long.valueOf(client.getClientId()))
                                            .setDocumentCode(client.getClientCode());
                                    sysBusinessBcst.setUserId(user.getUserId());
                                    sysBusinessBcstMapper.insert(sysBusinessBcst);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

