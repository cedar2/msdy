package com.platform.ems.task;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.ManManufactureOrder;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.mapper.ManManufactureOrderMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 件薪管理 商品道序
 *
 * @author chenkw
 */
@Service
@EnableScheduling
@Component
@SuppressWarnings("all")
@Slf4j
public class ProcessStepWarningTask {

    @Autowired
    private ManManufactureOrderMapper manManufactureOrderMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;


    /**
     * 1、获取未完工的生产订单的“工厂、商品编码”
     * 2、根据“工厂、商品编码、商品工价类型（大货）”获取是否存在“处理状态”是“已确认”的商品道序工价，
     *  如不存在，则增加待办通知：商品XXX在工厂XXX的道序工价还未创建或未确认
     */
    @Scheduled(cron = "00 00 03 * * *")
    @Transactional(rollbackFor = Exception.class)
    public void processStepProduct() {
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getTableName,ConstantsTable.TABLE_MANUFACTURE_ORDER+";"+ConstantsTable.TABLE_PRODUCT_PROCESS_STEP)
                .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB));
        List<SysTodoTask> taskList = new ArrayList<>();
        List<ManManufactureOrder> orderList = manManufactureOrderMapper.getNotPayProductProcessStepList(new ManManufactureOrder()
                .setHandleStatus(ConstantsEms.CHECK_STATUS).setBusinessType(ConstantsEms.PRODUCT_PRICE_TYPE_DH)
                .setCompleteStatusList(new String[]{ConstantsEms.COMPLETE_STATUS_WKS, ConstantsEms.COMPLETE_STATUS_JXZ}));
        if (CollectionUtil.isNotEmpty(orderList)){
            orderList.forEach(item->{
                SysTodoTask task = new SysTodoTask();
                task.setTitle("商品"+ item.getMaterialCode() + "在" + item.getPlantName() + "的道序工价还未创建或未确认")
                        .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_MANUFACTURE_ORDER+";"+ConstantsTable.TABLE_PRODUCT_PROCESS_STEP)
                        .setDocumentSid(item.getManufactureOrderSid())
                        .setDocumentCode(item.getManufactureOrderCode())
                        .setNoticeDate(new Date())
                        .setUserId(item.getCreatorAccountId());
                taskList.add(task);
            });
            sysTodoTaskMapper.inserts(taskList);
        }
    }

}
