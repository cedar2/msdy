package com.platform.ems.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.document.UserOperLog;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.core.domain.EmsBaseEntity;
import com.platform.api.service.RemoteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * test
 * @author c
 */
public  class MongodbUtil {

    private static MongoTemplate mongoTemplate;

    @Autowired
    private RemoteUserService remoteUserService;


    private static void init(){
        mongoTemplate = SpringUtil.getBean(MongoTemplate.class);
    }
    /**
     * @param sid id
     * @param businessType 日志类型
     * @param object1 更改前数据
     * @param object2 更改后数据
     * @param title 日志模块
     */
    public static void insertUserLog(Long sid, Integer businessType, Object object1,Object object2, String title) {
        init();
        List<OperMsg> msgList;
        msgList= BeanUtils.eq(object1, object2);
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setSid(sid);
        userOperLog.setBusinessType(businessType);
        userOperLog.setMsgList(msgList);
        userOperLog.setOperName(ApiThreadLocalUtil.get().getUsername());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            userOperLog.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        userOperLog.setOperTime(new Date());
        userOperLog.setTitle(title);
        mongoTemplate.save(userOperLog);
    }

    /**
     * @param sid id
     * @param businessType 日志类型
     * @param msgs 日志数据列表
     * @param title 日志模块
     */
    public static void insertUserLog(Long sid, Integer businessType, List<OperMsg> msgs, String title) {
        init();
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setSid(sid);
        userOperLog.setBusinessType(businessType);
        userOperLog.setMsgList(msgs);
        userOperLog.setOperName(ApiThreadLocalUtil.get().getUsername());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            userOperLog.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        userOperLog.setOperTime(new Date());
        userOperLog.setTitle(title);
        mongoTemplate.save(userOperLog);
    }

    /**
     * @param sid id
     * @param businessType 日志类型
     * @param title 日志模块
     */
    public static void insertUserLog(Long sid, Integer businessType, String title) {
        init();
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setSid(sid);
        userOperLog.setBusinessType(businessType);
        userOperLog.setOperName(ApiThreadLocalUtil.get().getUsername());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            userOperLog.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        userOperLog.setOperTime(new Date());
        userOperLog.setTitle(title);
        mongoTemplate.save(userOperLog);
    }

    /**
     *
     * @param sid id
     * @param businessType 日志类型
     * @param msgs 日志数据列表
     * @param title 日志模块
     * @param remark 备注
     */
    public static void insertUserLog(Long sid, Integer businessType, List<OperMsg> msgs, String title,String remark) {
        init();
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setSid(sid);
        userOperLog.setBusinessType(businessType);
        userOperLog.setMsgList(msgs);
        userOperLog.setRemark(remark);
        userOperLog.setOperName(ApiThreadLocalUtil.get().getUsername());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            userOperLog.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        userOperLog.setOperTime(new Date());
        userOperLog.setTitle(title);
        mongoTemplate.save(userOperLog);
    }

    public static void findString(@NotNull Object o){
        init();
        String idName=BeanUtils.findIdName(o);
        if(StrUtil.isNotEmpty(idName)){
            Object value=BeanUtils.getValue(o, idName);
            Long sid = Long.parseLong(String.valueOf(value));
            if(value!=null){
                Query query = new Query();
                query.addCriteria(Criteria.where("sid").is(sid));
                List<UserOperLog> userOperLogList = mongoTemplate.find(query, UserOperLog.class);
                //获取最近更改人和更改日期
                if (CollUtil.isNotEmpty(userOperLogList)) {
                    userOperLogList = userOperLogList.stream().sorted(Comparator.comparing(UserOperLog::getOperTime).reversed()).collect(Collectors.toList());
                    UserOperLog recent = userOperLogList.get(userOperLogList.size()-1);
                    ((EmsBaseEntity) o).setOperLogRecentDate(recent.getOperTime()).setOperLogRecentName(recent.getNickName());
                }
                ((EmsBaseEntity) o).setOperLogList(userOperLogList);
            }
        }
        return;
    }

    public static List<UserOperLog> find(@NotNull Long sid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sid").is(sid));
        return mongoTemplate.find(query, UserOperLog.class);
    }

    public static void find(@NotNull Object o){
        init();
        String idName=BeanUtils.findIdName(o);
        if(StrUtil.isNotEmpty(idName)){
            Object value=BeanUtils.getValue(o, idName);
            if(value!=null){
                Query query = new Query();
                query.addCriteria(Criteria.where("sid").is(value));
                List<UserOperLog> userOperLogList = mongoTemplate.find(query, UserOperLog.class);
                //获取最近更改人和更改日期
                if (CollUtil.isNotEmpty(userOperLogList)) {
                    userOperLogList = userOperLogList.stream().sorted(Comparator.comparing(UserOperLog::getOperTime).reversed()).collect(Collectors.toList());
                    UserOperLog recent = userOperLogList.get(0);
                    ((EmsBaseEntity) o).setOperLogRecentDate(recent.getOperTime()).setOperLogRecentName(recent.getNickName());
                }
                ((EmsBaseEntity) o).setOperLogList(userOperLogList);
            }
        }
        return;
    }

    public static void find(@NotNull Object o, Object id){
        init();
        if(id!=null){
            Query query = new Query();
            query.addCriteria(Criteria.where("sid").is(id));
            List<UserOperLog> userOperLogList = mongoTemplate.find(query, UserOperLog.class);
            //获取最近更改人和更改日期
            if (CollUtil.isNotEmpty(userOperLogList)) {
                userOperLogList = userOperLogList.stream().sorted(Comparator.comparing(UserOperLog::getOperTime).reversed()).collect(Collectors.toList());
                UserOperLog recent = userOperLogList.get(0);
                ((EmsBaseEntity) o).setOperLogRecentDate(recent.getOperTime()).setOperLogRecentName(recent.getNickName());
            }
            ((EmsBaseEntity) o).setOperLogList(userOperLogList);
        }
        return;
    }

    public static void removeBySid(Long sid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sid").is(sid));
        mongoTemplate.remove(query, UserOperLog.class);
    }

    public static void remove(Query query) {
        mongoTemplate.remove(query, UserOperLog.class);
    }

    /**
     * @param sid id
     * @param businessTypeValue 日志类型
     * @param object1 更改前数据
     * @param object2 更改后数据
     * @param title 日志模块
     */
    public static void insertUserLog(Long sid, String businessTypeValue, Object object1,Object object2, String title) {
        init();
        List<OperMsg> msgList;
        msgList= BeanUtils.eq(object1, object2);
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setSid(sid);
        userOperLog.setBusinessTypeValue(businessTypeValue);
        userOperLog.setMsgList(msgList);
        userOperLog.setOperName(ApiThreadLocalUtil.get().getUsername());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            userOperLog.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        userOperLog.setOperTime(new Date());
        userOperLog.setTitle(title);
        mongoTemplate.save(userOperLog);
    }

    /**
     * @param sid id
     * @param businessTypeValue 日志类型
     * @param object1 更改前数据
     * @param object2 更改后数据
     * @param title 日志模块
     */
    public static void insertUserLog(Long sid, String businessTypeValue, Object object1,Object object2, String title, String remark) {
        init();
        List<OperMsg> msgList;
        msgList= BeanUtils.eq(object1, object2);
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setSid(sid);
        userOperLog.setBusinessTypeValue(businessTypeValue);
        userOperLog.setMsgList(msgList);
        userOperLog.setOperName(ApiThreadLocalUtil.get().getUsername());
        userOperLog.setBefore(object1.toString());
        userOperLog.setAfter(object2.toString());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            userOperLog.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        userOperLog.setOperTime(new Date());
        userOperLog.setTitle(title);
        userOperLog.setRemark(remark);
        userOperLog.setComment(remark);
        mongoTemplate.save(userOperLog);
    }

    /**
     * @param sid id
     * @param businessTypeValue 日志类型
     * @param msgs 日志数据列表
     * @param title 日志模块
     */
    public static void insertUserLog(Long sid, String businessTypeValue, List<OperMsg> msgs, String title) {
        init();
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setSid(sid);
        userOperLog.setBusinessTypeValue(businessTypeValue);
        userOperLog.setMsgList(msgs);
        userOperLog.setOperName(ApiThreadLocalUtil.get().getUsername());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            userOperLog.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        userOperLog.setOperTime(new Date());
        userOperLog.setTitle(title);
        mongoTemplate.save(userOperLog);
    }

    /**
     * @param sid id
     * @param businessTypeValue 日志类型
     * @param title 日志模块
     */
    public static void insertUserLog(Long sid, String businessTypeValue, String title) {
        init();
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setSid(sid);
        userOperLog.setBusinessTypeValue(businessTypeValue);
        userOperLog.setOperName(ApiThreadLocalUtil.get().getUsername());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            userOperLog.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        userOperLog.setOperTime(new Date());
        userOperLog.setTitle(title);
        mongoTemplate.save(userOperLog);
    }
    /**
     * @param sid id
     * @param businessTypeValue 日志类型-行号
     * @param title 日志模块
     */
    public static void insertUserLogItem(Long sid, String businessTypeValue, String title,int itemNum) {
        init();
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setSid(sid);
        userOperLog.setBusinessTypeValue(businessTypeValue);
        userOperLog.setOperName(ApiThreadLocalUtil.get().getUsername());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            userOperLog.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        userOperLog.setOperTime(new Date());
        userOperLog.setItemNum(itemNum);
        userOperLog.setTitle(title);
        mongoTemplate.save(userOperLog);
    }

    /**
     *
     * @param sid id
     * @param businessTypeValue 日志类型
     * @param msgs 日志数据列表
     * @param title 日志模块
     * @param remark 备注
     */
    public static void insertUserLog(Long sid, String businessTypeValue, List<OperMsg> msgs, String title,String remark) {
        init();
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setSid(sid);
        userOperLog.setBusinessTypeValue(businessTypeValue);
        userOperLog.setMsgList(msgs);
        userOperLog.setRemark(remark);
        userOperLog.setComment(remark);
        userOperLog.setOperName(ApiThreadLocalUtil.get().getUsername());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            userOperLog.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        userOperLog.setOperTime(new Date());
        userOperLog.setTitle(title);
        mongoTemplate.save(userOperLog);
    }

    /**
     * 定时任务
     * @param sid id
     * @param businessTypeValue 日志类型
     * @param msgs 日志数据列表
     * @param title 日志模块
     * @param remark 备注
     */
    public static void insertUserLogAdmin(Long sid, String businessTypeValue, List<OperMsg> msgs, String title, String remark) {
        init();
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setSid(sid);
        userOperLog.setBusinessTypeValue(businessTypeValue);
        userOperLog.setMsgList(msgs);
        userOperLog.setRemark(remark);
        userOperLog.setComment(remark);
        userOperLog.setOperName("admin");
        userOperLog.setNickName("系统");
        userOperLog.setOperTime(new Date());
        userOperLog.setTitle(title);
        mongoTemplate.save(userOperLog);
    }

    public static void insertApprovalLog(Long sid,String businessTypeValue,String comment){
        init();
        UserOperLog log = new UserOperLog();
        log.setSid(sid);
        log.setBusinessTypeValue(businessTypeValue);
        log.setComment(comment);
        log.setOperName(ApiThreadLocalUtil.get().getUsername());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            log.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        log.setOperTime(new Date());
        mongoTemplate.save(log);
    }
    public static void insertApprovalLogAddNum(Long sid,String businessTypeValue,String comment,int num){
        init();
        UserOperLog log = new UserOperLog();
        log.setSid(sid);
        log.setBusinessTypeValue(businessTypeValue);
        log.setComment(comment);
        log.setItemNum(num);
        log.setOperName(ApiThreadLocalUtil.get().getUsername());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            log.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        log.setOperTime(new Date());
        mongoTemplate.save(log);
    }
    //复制上面的加了个标题 @chenkw @2022:03:25:14:22:30
    public static void insertUserLogItem(Long sid,String businessTypeValue,String title,int num,String comment){
        init();
        UserOperLog log = new UserOperLog();
        log.setSid(sid);
        log.setTitle(title);
        log.setBusinessTypeValue(businessTypeValue);
        log.setComment(comment);
        log.setItemNum(num);
        log.setOperName(ApiThreadLocalUtil.get().getUsername());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            log.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        log.setOperTime(new Date());
        mongoTemplate.save(log);
    }

    /**
     * 明细编辑或者变更的操作日志
     *
     * @param sid id
     * @param handleStatus 主表处理状态 除了保存状态是编辑，其它都是变更
     * @param title 日志模块
     */
    public static void updateItemUserLog(Long sid, String handleStatus, Object object1,Object object2, String title) {
        init();
        List<OperMsg> msgList;
        msgList= BeanUtils.eq(object1, object2);
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setSid(sid);
        if (ConstantsEms.SAVA_STATUS.equals(handleStatus)) {
            userOperLog.setBusinessTypeValue(BusinessType.UPDATE.getValue());
        }
        else {
            userOperLog.setBusinessTypeValue(BusinessType.CHANGE.getValue());
        }
        userOperLog.setMsgList(msgList);
        userOperLog.setOperName(ApiThreadLocalUtil.get().getUsername());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            userOperLog.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        userOperLog.setOperTime(new Date());
        userOperLog.setTitle(title);
        mongoTemplate.save(userOperLog);
    }

    /**
     * 明细编辑或者变更的操作日志
     *
     * @param sid id
     * @param handleStatus 主表处理状态 除了保存状态是编辑，其它都是变更
     * @param title 日志模块
     */
    public static void updateItemUserLog(Long sid, String handleStatus, List<OperMsg> msgList, String title) {
        init();
        UserOperLog userOperLog = new UserOperLog();
        userOperLog.setSid(sid);
        if (ConstantsEms.SAVA_STATUS.equals(handleStatus)) {
            userOperLog.setBusinessTypeValue(BusinessType.UPDATE.getValue());
        }
        else {
            userOperLog.setBusinessTypeValue(BusinessType.CHANGE.getValue());
        }
        userOperLog.setMsgList(msgList);
        userOperLog.setOperName(ApiThreadLocalUtil.get().getUsername());
        if (ApiThreadLocalUtil.get().getSysUser()!=null){
            userOperLog.setNickName(ApiThreadLocalUtil.get().getSysUser().getNickName());
        }
        userOperLog.setOperTime(new Date());
        userOperLog.setTitle(title);
        mongoTemplate.save(userOperLog);
    }

    public static <T> void insertData(List<T> data, Consumer<T> consumer){
        data.parallelStream().forEach(consumer);
    }

}
