package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.ManDayManufacturePlanItem;
import com.platform.ems.domain.ManMonthManufacturePlanItem;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.ManDayManufacturePlanItemMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.ManDayManufacturePlanMapper;
import com.platform.ems.domain.ManDayManufacturePlan;
import com.platform.ems.service.IManDayManufacturePlanService;

/**
 * 生产日计划Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-22
 */
@Service
@SuppressWarnings("all")
public class ManDayManufacturePlanServiceImpl extends ServiceImpl<ManDayManufacturePlanMapper,ManDayManufacturePlan>  implements IManDayManufacturePlanService {
    @Autowired
    private ManDayManufacturePlanMapper manDayManufacturePlanMapper;
    @Autowired
    private ManDayManufacturePlanItemMapper manDayManufacturePlanItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "生产日计划";
    /**
     * 查询生产日计划
     *
     * @param dayManufacturePlanSid 生产日计划ID
     * @return 生产日计划
     */
    @Override
    public ManDayManufacturePlan selectManDayManufacturePlanById(Long dayManufacturePlanSid) {
        ManDayManufacturePlan manDayManufacturePlan = manDayManufacturePlanMapper.selectManDayManufacturePlanById(dayManufacturePlanSid);
        if (manDayManufacturePlan == null){
            return null;
        }
        //生产日计划-明细
        ManDayManufacturePlanItem manDayManufacturePlanItem = new ManDayManufacturePlanItem();
//        manDayManufacturePlanItem.setDayManufacturePlanSid(dayManufacturePlanSid);
        List<ManDayManufacturePlanItem> manDayManufacturePlanItemList = manDayManufacturePlanItemMapper.selectManDayManufacturePlanItemList(manDayManufacturePlanItem);

        manDayManufacturePlan.setManDayManufacturePlanItemList(manDayManufacturePlanItemList);
        MongodbUtil.find(manDayManufacturePlan);
        return  manDayManufacturePlan;
    }

    /**
     * 查询生产日计划列表
     *
     * @param manDayManufacturePlan 生产日计划
     * @return 生产日计划
     */
    @Override
    public List<ManDayManufacturePlan> selectManDayManufacturePlanList(ManDayManufacturePlan manDayManufacturePlan) {
        return manDayManufacturePlanMapper.selectManDayManufacturePlanList(manDayManufacturePlan);
    }

    /**
     * 新增生产日计划
     * 需要注意编码重复校验
     * @param manDayManufacturePlan 生产日计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManDayManufacturePlan(ManDayManufacturePlan manDayManufacturePlan) {
        setConfirmInfo(manDayManufacturePlan);
        int row = manDayManufacturePlanMapper.insert(manDayManufacturePlan);
        if (row > 0){
            //生产日计划-明细
            List<ManDayManufacturePlanItem> manDayManufacturePlanItemList = manDayManufacturePlan.getManDayManufacturePlanItemList();
            if (CollectionUtils.isNotEmpty(manDayManufacturePlanItemList)) {
                addManDayManufacturePlanItem(manDayManufacturePlan, manDayManufacturePlanItemList);
            }
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(manDayManufacturePlan.getDayManufacturePlanSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ManDayManufacturePlan o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 采购合同信息-附件对象
     */
    private void addManDayManufacturePlanItem(ManDayManufacturePlan manDayManufacturePlan, List<ManDayManufacturePlanItem> manDayManufacturePlanItemList) {

        long i = 1;
        for (ManDayManufacturePlanItem planItem : manDayManufacturePlanItemList) {

            planItem.setItemNum(i);
            i++;
        }
        manDayManufacturePlanItemMapper.inserts(manDayManufacturePlanItemList);
    }

    /**
     * 修改生产日计划
     *
     * @param manDayManufacturePlan 生产日计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManDayManufacturePlan(ManDayManufacturePlan manDayManufacturePlan) {
        ManDayManufacturePlan response = manDayManufacturePlanMapper.selectManDayManufacturePlanById(manDayManufacturePlan.getDayManufacturePlanSid());
        setConfirmInfo(manDayManufacturePlan);
        int row = manDayManufacturePlanMapper.updateAllById(manDayManufacturePlan);
        if (row > 0){
            //生产日计划-明细
            List<ManDayManufacturePlanItem> manDayManufacturePlanItemList = manDayManufacturePlan.getManDayManufacturePlanItemList();
            if (CollectionUtils.isNotEmpty(manDayManufacturePlanItemList)) {
                manDayManufacturePlanItemList.stream().forEach(o ->{
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addManDayManufacturePlanItem(manDayManufacturePlan, manDayManufacturePlanItemList);
            }
            //插入日志
            MongodbUtil.insertUserLog(manDayManufacturePlan.getDayManufacturePlanSid(), BusinessType.UPDATE.ordinal(), response,manDayManufacturePlan,TITLE);
        }
        return row;
    }

    /**
     * 变更生产日计划
     *
     * @param manDayManufacturePlan 生产日计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManDayManufacturePlan(ManDayManufacturePlan manDayManufacturePlan) {
        ManDayManufacturePlan response = manDayManufacturePlanMapper.selectManDayManufacturePlanById(manDayManufacturePlan.getDayManufacturePlanSid());
        int row = manDayManufacturePlanMapper.updateAllById(manDayManufacturePlan);
        if (row > 0){
            //生产日计划-明细
            List<ManDayManufacturePlanItem> manDayManufacturePlanItemList = manDayManufacturePlan.getManDayManufacturePlanItemList();
            if (CollectionUtils.isNotEmpty(manDayManufacturePlanItemList)) {
                manDayManufacturePlanItemList.stream().forEach(o ->{
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addManDayManufacturePlanItem(manDayManufacturePlan, manDayManufacturePlanItemList);
            }
            //插入日志
            MongodbUtil.insertUserLog(manDayManufacturePlan.getDayManufacturePlanSid(), BusinessType.CHANGE.ordinal(), response,manDayManufacturePlan,TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产日计划
     *
     * @param dayManufacturePlanSids 需要删除的生产日计划ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManDayManufacturePlanByIds(List<Long> dayManufacturePlanSids) {
        ManDayManufacturePlan params = new ManDayManufacturePlan();
        params.setDayManufacturePlanSids(dayManufacturePlanSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = manDayManufacturePlanMapper.countByDomain(params);
        if (count != dayManufacturePlanSids.size()){
            throw new BaseException("仅保存状态才允许删除");
        }
        manDayManufacturePlanMapper.deleteBatchIds(dayManufacturePlanSids);
        //删除生产日计划-明细
        manDayManufacturePlanItemMapper.deleteManDayManufacturePlanItemByIds(dayManufacturePlanSids);
        return dayManufacturePlanSids.size();
    }

    /**
     *更改确认状态
     * @param manDayManufacturePlan
     * @return
     */
    @Override
    public int check(ManDayManufacturePlan manDayManufacturePlan){
        int row=0;
        Long[] sids=manDayManufacturePlan.getDayManufacturePlanSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                manDayManufacturePlan.setDayManufacturePlanSid(id);
                row=manDayManufacturePlanMapper.updateById( manDayManufacturePlan);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(manDayManufacturePlan.getDayManufacturePlanSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 生产日计划明细报表
     */
    @Override
    public List<ManDayManufacturePlanItem> getItemList(ManDayManufacturePlanItem manDayManufacturePlanItem) {
        return manDayManufacturePlanItemMapper.getItemList(manDayManufacturePlanItem);
    }
}
