package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.BasStaff;
import com.platform.ems.domain.PaySalaryBillItem;
import com.platform.ems.domain.dto.request.PaySalaryBillItemRequest;
import com.platform.ems.domain.dto.response.PaySalaryBillItemExResponse;
import com.platform.ems.mapper.PaySalaryBillItemMapper;
import com.platform.ems.service.IPaySalaryBillItemService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * 工资单-明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-14
 */
@Service
@SuppressWarnings("all")
public class PaySalaryBillItemServiceImpl extends ServiceImpl<PaySalaryBillItemMapper, PaySalaryBillItem> implements IPaySalaryBillItemService {
    @Autowired
    private PaySalaryBillItemMapper paySalaryBillItemMapper;

    private static final String TITLE = "工资单-明细";

    /**
     * 查询工资单-明细
     *
     * @param billItemSid 工资单-明细ID
     * @return 工资单-明细
     */
    @Override
    public PaySalaryBillItem selectPaySalaryBillItemById(Long billItemSid) {
        PaySalaryBillItem paySalaryBillItem = paySalaryBillItemMapper.selectPaySalaryBillItemById(billItemSid);
        MongodbUtil.find(paySalaryBillItem);
        return paySalaryBillItem;
    }

    /**
     * 查询工资单-明细列表
     *
     * @param paySalaryBillItem 工资单-明细
     * @return 工资单-明细
     */
    @Override
    public List<PaySalaryBillItemExResponse> getReport(PaySalaryBillItemRequest paySalaryBillItemRequest){
       return  paySalaryBillItemMapper.getReport(paySalaryBillItemRequest);
    }

    /**
     * 新增工资单-明细
     * 需要注意编码重复校验
     *
     * @param paySalaryBillItem 工资单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPaySalaryBillItem(PaySalaryBillItem paySalaryBillItem) {
        int row = paySalaryBillItemMapper.insert(paySalaryBillItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList= BeanUtils.eq(new PaySalaryBillItem(), paySalaryBillItem);
            MongodbDeal.insert(paySalaryBillItem.getBillItemSid(), paySalaryBillItem.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改工资单-明细
     *
     * @param paySalaryBillItem 工资单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePaySalaryBillItem(PaySalaryBillItem paySalaryBillItem) {
        PaySalaryBillItem response = paySalaryBillItemMapper.selectPaySalaryBillItemById(paySalaryBillItem.getBillItemSid());
        int row = paySalaryBillItemMapper.updateById(paySalaryBillItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList= BeanUtils.eq(response, paySalaryBillItem);
            MongodbDeal.update(paySalaryBillItem.getBillItemSid(), response.getHandleStatus(), paySalaryBillItem.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更工资单-明细
     *
     * @param paySalaryBillItem 工资单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePaySalaryBillItem(PaySalaryBillItem paySalaryBillItem) {
        PaySalaryBillItem response = paySalaryBillItemMapper.selectPaySalaryBillItemById(paySalaryBillItem.getBillItemSid());
        int row = paySalaryBillItemMapper.updateAllById(paySalaryBillItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(paySalaryBillItem.getBillItemSid(), BusinessType.CHANGE.getValue(), response, paySalaryBillItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除工资单-明细
     *
     * @param billItemSids 需要删除的工资单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePaySalaryBillItemByIds(List<Long> billItemSids) {
        List<PaySalaryBillItem> response = paySalaryBillItemMapper.selectBatchIds(billItemSids);
        PaySalaryBillItem newone = new PaySalaryBillItem();
        int row = paySalaryBillItemMapper.deleteBatchIds(billItemSids);
        if (row > 0) {
            response.forEach(item->{
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                msgList= BeanUtils.eq(item, newone);
            });
        }
        return row;
    }

    /**
     * 查询员工工资单-明细列表
     * @param staffList 员工列表
     * @param paySalaryBillItem 工资单-明细
     * @return 工资单-明细
     */
    @Override
    public List<PaySalaryBillItem> getProcessStepCompleteWage(List<BasStaff> staffList, PaySalaryBillItem paySalaryBillItem) {
        // 返回带有工资信息的列表
        List<PaySalaryBillItem> responseList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(staffList)) {
            responseList = BeanCopyUtils.copyListProperties(staffList, PaySalaryBillItem::new);
            // 获取 计件工资和返修费
            Long[] staffSidList = staffList.stream().map(BasStaff::getStaffSid).toArray(Long[]::new);
            List<PaySalaryBillItem> processStepCompleteList = paySalaryBillItemMapper.getProcessStepCompleteWage(new PaySalaryBillItem().setStaffSidList(staffSidList)
                    .setYearmonth(paySalaryBillItem.getYearmonth()));
            if (CollectionUtil.isNotEmpty(processStepCompleteList)){
                responseList = responseList.stream().map(staff -> processStepCompleteList.stream()
                                .filter(complete -> staff.getStaffSid().equals(complete.getStaffSid())).findFirst()
                                .map(complete -> {staff.setWagePieceSys(complete.getWagePieceSys());staff.setWagePiece(complete.getWagePiece())
                                ;staff.setWageFanxiuSys(complete.getWageFanxiuSys());staff.setWageFanxiu(complete.getWageFanxiu());return staff; })
                                .orElse(staff))
                        .collect(toList());
            }
        }
        return responseList;
    }

    /**
     * 工资单明细报表设置工资成本分摊
     * @param paySalaryBillItem 工资单-明细
     * @return 工资单-明细集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setSalaryCostAllocateType(PaySalaryBillItem paySalaryBillItem) {
        int row = 0;
        if (paySalaryBillItem.getBillItemSidList().length == 0){
            return row;
        }
        LambdaUpdateWrapper<PaySalaryBillItem> updateWrapper = new LambdaUpdateWrapper<>();
        //工资成本分摊
        updateWrapper.in(PaySalaryBillItem::getBillItemSid, paySalaryBillItem.getBillItemSidList());
        updateWrapper.set(PaySalaryBillItem::getSalaryCostAllocateType, paySalaryBillItem.getSalaryCostAllocateType());
        row = paySalaryBillItemMapper.update(null, updateWrapper);
        return row;
    }

}
