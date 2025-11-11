package com.platform.common.utils;

import com.platform.common.constant.ConstantsEms;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import java.util.List;

/**
 * mongodb处理入口
 *
 * @author chenkw
 */
public class MongodbDeal {

    /**
     * 新建档案
     */
    public static void insert(Long sid, String handleStatus, List<OperMsg> msgs, String title, String remark) {
        /*
         * 暂存操作：暂存
         *
         * */
        if (HandleStatus.SAVE.getCode().equals(handleStatus)) {
            MongodbUtil.insertUserLog(sid, BusinessType.INSERT.getValue(), msgs, title, remark);
        }
        /*
         * 提交操作：提交
         *
         * */
        if (HandleStatus.SUBMIT.getCode().equals(handleStatus)) {
            MongodbUtil.insertUserLog(sid, BusinessType.INSERT.getValue(), msgs, title, null);
            MongodbUtil.insertUserLog(sid, BusinessType.SUBMIT.getValue(), null, title, remark);
        }
        /*
         * 确认操作：确认
         *
         * */
        if (HandleStatus.CONFIRMED.getCode().equals(handleStatus)) {
            MongodbUtil.insertUserLog(sid, BusinessType.INSERT.getValue(), msgs, title, null);
            MongodbUtil.insertUserLog(sid, BusinessType.CHECK.getValue(), null, title, remark);
        }
    }

    /**
     * 新建档案
     */
    public static void insert(Long sid, String handleStatus, List<OperMsg> msgs, String title, String remark, String importType) {
        if (BusinessType.IMPORT.getValue().equals(importType)) {
            MongodbUtil.insertUserLog(sid, BusinessType.IMPORT.getValue(), msgs, title, remark);
            return;
        }
        /*
         * 暂存操作：暂存
         *
         * */
        if (HandleStatus.SAVE.getCode().equals(handleStatus)) {
            MongodbUtil.insertUserLog(sid, BusinessType.INSERT.getValue(), msgs, title, remark);
        }
        /*
         * 提交操作：提交
         *
         * */
        if (HandleStatus.SUBMIT.getCode().equals(handleStatus)) {
            MongodbUtil.insertUserLog(sid, BusinessType.INSERT.getValue(), msgs, title, null);
            MongodbUtil.insertUserLog(sid, BusinessType.SUBMIT.getValue(), null, title, remark);
        }
        /*
         * 确认操作：确认
         *
         * */
        if (HandleStatus.CONFIRMED.getCode().equals(handleStatus)) {
            MongodbUtil.insertUserLog(sid, BusinessType.INSERT.getValue(), msgs, title, null);
            MongodbUtil.insertUserLog(sid, BusinessType.CHECK.getValue(), null, title, remark);
        }
    }

    /**
     * 进行修改数据操作
     * <p>
     * handleStatus1，object1：旧数据
     * handleStatus2，object2：新数据
     */
    public static void update(Long sid, String handleStatus1, String handleStatus2, List<OperMsg> msgs, String title, String remark) {
        /*
         * 暂存操作：编辑
         *
         * */
        if (HandleStatus.SAVE.getCode().equals(handleStatus2)) {
            MongodbUtil.insertUserLog(sid, BusinessType.UPDATE.getValue(), msgs, title, remark);
        }
        /*
         * 提交操作：提交  1:编辑提交，2：变更提交
         *
         * */
        if (HandleStatus.SUBMIT.getCode().equals(handleStatus2)) {
            if (HandleStatus.SAVE.getCode().equals(handleStatus1)
                    || HandleStatus.RETURNED.getCode().equals(handleStatus1)){
                MongodbUtil.insertUserLog(sid, BusinessType.UPDATE.getValue(), msgs, title, null);
                MongodbUtil.insertUserLog(sid, BusinessType.SUBMIT.getValue(), null, title, remark);
            }
            else {
                MongodbUtil.insertUserLog(sid, BusinessType.CHANGE.getValue(), msgs, title, null);
                MongodbUtil.insertUserLog(sid, BusinessType.SUBMIT.getValue(), null, title, remark);
            }
        }
        /*
         * 确认操作：变更   1:由编辑点确认，2：变更点确认
         *
         * */
        if (HandleStatus.CONFIRMED.getCode().equals(handleStatus2)) {
            //如果是
            if (HandleStatus.SAVE.getCode().equals(handleStatus1)) {
                MongodbUtil.insertUserLog(sid, BusinessType.UPDATE.getValue(), msgs, title, null);
                MongodbUtil.insertUserLog(sid, BusinessType.CHECK.getValue(), null, title, remark);
            }
            else {
                MongodbUtil.insertUserLog(sid, BusinessType.CHANGE.getValue(), msgs, title, remark);
            }
        }
    }

    /**
     * 修改启停状态
     */
    public static void status(Long sid, String type, List<OperMsg> msgs, String title, String remark) {
        /*
         * 启用操作：1
         *
         * */
        if (ConstantsEms.ENABLE_STATUS.equals(type)) {
            MongodbUtil.insertUserLog(sid, BusinessType.ENABLE.getValue(), msgs, title, remark);
        }
        /*
         * 停用操作：2
         *
         * */
        else if (ConstantsEms.DISENABLE_STATUS.equals(type)) {
            MongodbUtil.insertUserLog(sid, BusinessType.DISENABLE.getValue(), msgs, title, remark);
        }
    }

    /**
     * 修改处理状态
     */
    public static void check(Long sid, String businessType, List<OperMsg> msgs, String title, String remark) {
        /*
         * 保存操作：1
         *
         * */
        if (HandleStatus.SAVE.getCode().equals(businessType)) {
            MongodbUtil.insertUserLog(sid, BusinessType.SAVE.getValue(), msgs, title, remark);
        }
        /*
         * 提交操作：3
         *
         * */
        else if (HandleStatus.SUBMIT.getCode().equals(businessType)) {
            MongodbUtil.insertUserLog(sid, BusinessType.SUBMIT.getValue(), msgs, title, remark);
        }
        /*
         * 退回操作：4
         *
         * */
        else if (HandleStatus.RETURNED.getCode().equals(businessType)) {
            MongodbUtil.insertUserLog(sid, BusinessType.RETURN.getValue(), msgs, title, remark);
        }
        /*
         * 确认操作：5
         *
         * */
        else if (HandleStatus.CONFIRMED.getCode().equals(businessType)) {
            MongodbUtil.insertUserLog(sid, BusinessType.CONFIRM.getValue(), msgs, title, remark);
        }
        /*
         * 完成操作：6
         *
         * */
        else if (HandleStatus.COMPLETED.getCode().equals(businessType)) {
            MongodbUtil.insertUserLog(sid, BusinessType.COMPLETED.getValue(), msgs, title, remark);
        }
        /*
         * 关闭操作：7
         *
         * */
        else if (HandleStatus.CLOSED.getCode().equals(businessType)) {
            MongodbUtil.insertUserLog(sid, BusinessType.CLOSE.getValue(), msgs, title, remark);
        }
        /*
         * 作废操作：8
         *
         * */
        else if (HandleStatus.INVALID.getCode().equals(businessType)) {
            MongodbUtil.insertUserLog(sid, BusinessType.CANCEL.getValue(), msgs, title, remark);
        }
        /*
         * 结案操作：9
         *
         * */
        else if (HandleStatus.CONCLUDE.getCode().equals(businessType)) {
            MongodbUtil.insertUserLog(sid, BusinessType.CONCLUDE.getValue(), msgs, title, remark);
        }
        /*
         * 红冲操作：A
         *
         * */
        else if (HandleStatus.REDDASHED.getCode().equals(businessType)) {
            MongodbUtil.insertUserLog(sid, BusinessType.REDDASHED.getValue(), msgs, title, remark);
        }
        /*
         * 过帐操作：B
         *
         * */
        else if (HandleStatus.POSTING.getCode().equals(businessType)) {
            MongodbUtil.insertUserLog(sid, BusinessType.POSTING.getValue(), msgs, title, remark);
        }
    }
}
