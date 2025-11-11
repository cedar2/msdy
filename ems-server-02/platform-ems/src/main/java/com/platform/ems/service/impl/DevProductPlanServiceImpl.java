package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.DevProductPlan;
import com.platform.ems.domain.DevProductPlanAttach;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.DevProductPlanAttachMapper;
import com.platform.ems.mapper.DevProductPlanMapper;
import com.platform.ems.service.IDevProductPlanService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 品类规划信息Service业务层处理
 *
 * @author qhq
 * @date 2021-11-08
 */
@Service
@SuppressWarnings("all")
public class DevProductPlanServiceImpl extends ServiceImpl<DevProductPlanMapper,DevProductPlan>  implements IDevProductPlanService {
    @Autowired
    private DevProductPlanMapper devProductPlanMapper;
    @Autowired
    private DevProductPlanAttachMapper devProductPlanAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "品类规划信息";
    /**
     * 查询品类规划信息
     *
     * @param productPlanSid 品类规划信息ID
     * @return 品类规划信息
     */
    @Override
    public DevProductPlan selectDevProductPlanById(Long productPlanSid) {
        DevProductPlan devProductPlan = devProductPlanMapper.selectDevProductPlanById(productPlanSid);
        MongodbUtil.find(devProductPlan);
        return  devProductPlan;
    }

    /**
     * 查询品类规划信息列表
     *
     * @param devProductPlan 品类规划信息
     * @return 品类规划信息
     */
    @Override
    public List<DevProductPlan> selectDevProductPlanList(DevProductPlan devProductPlan) {
        return devProductPlanMapper.selectDevProductPlanList(devProductPlan);
    }

    /**
     * 新增品类规划信息
     * 需要注意编码重复校验
     * @param devProductPlan 品类规划信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDevProductPlan(DevProductPlan devProductPlan) {
        if(devProductPlan.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode())){
            devProductPlan.setConfirmDate(new Date());
            devProductPlan.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row= devProductPlanMapper.insert(devProductPlan);
        if(row>0){
            List<DevProductPlanAttach> athList = devProductPlan.getAthList();
            if(athList!=null&&athList.size()>0){
                for(DevProductPlanAttach ath : athList){
                    ath.setProductPlanSid(devProductPlan.getProductPlanSid());
                    devProductPlanAttachMapper.insert(ath);
                }
            }
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(devProductPlan.getProductPlanSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改品类规划信息
     *
     * @param devProductPlan 品类规划信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDevProductPlan(DevProductPlan devProductPlan) {
        DevProductPlan response = devProductPlanMapper.selectDevProductPlanById(devProductPlan.getProductPlanSid());
        QueryWrapper<DevProductPlanAttach> athWrapper = new QueryWrapper<>();
        athWrapper.eq("product_plan_sid",devProductPlan.getProductPlanSid());
        devProductPlanAttachMapper.delete(athWrapper);
        List<DevProductPlanAttach> athList = devProductPlan.getAthList();
        if(athList!=null&&athList.size()>0){
            for(DevProductPlanAttach ath : athList){
                ath.setProductPlanSid(devProductPlan.getProductPlanSid());
                devProductPlanAttachMapper.insert(ath);
            }
        }
        devProductPlan.setUpdateDate(new Date());
        devProductPlan.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        if(devProductPlan.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode())){
            devProductPlan.setConfirmDate(new Date());
            devProductPlan.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row=devProductPlanMapper.updateAllById(devProductPlan);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(devProductPlan.getProductPlanSid(), BusinessType.UPDATE.ordinal(), response,devProductPlan,TITLE);
        }
        return row;
    }

    /**
     * 变更品类规划信息
     *
     * @param devProductPlan 品类规划信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDevProductPlan(DevProductPlan devProductPlan) {
        DevProductPlan response = devProductPlanMapper.selectDevProductPlanById(devProductPlan.getProductPlanSid());
        QueryWrapper<DevProductPlanAttach> athWrapper = new QueryWrapper<>();
        athWrapper.eq("product_plan_sid",devProductPlan.getProductPlanSid());
        devProductPlanAttachMapper.delete(athWrapper);
        List<DevProductPlanAttach> athList = devProductPlan.getAthList();
        if(athList!=null&&athList.size()>0){
            for(DevProductPlanAttach ath : athList){
                ath.setProductPlanSid(devProductPlan.getProductPlanSid());
                devProductPlanAttachMapper.insert(ath);
            }
        }
        int row=devProductPlanMapper.updateAllById(devProductPlan);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(devProductPlan.getProductPlanSid(), BusinessType.CHANGE.ordinal(), response,devProductPlan,TITLE);
        }
        return row;
    }

    /**
     * 批量删除品类规划信息
     *
     * @param productPlanSids 需要删除的品类规划信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDevProductPlanByIds(List<Long> productPlanSids) {
        return devProductPlanMapper.deleteBatchIds(productPlanSids);
    }

    /**
    * 启用/停用
    * @param devProductPlan
    * @return
    */
    @Override
    public int changeStatus(DevProductPlan devProductPlan){
        int row=0;
        Long[] sids=devProductPlan.getProductPlanSidList();
        if(sids!=null&&sids.length>0){
            row=devProductPlanMapper.update(null, new UpdateWrapper<DevProductPlan>().lambda().set(DevProductPlan::getStatus ,devProductPlan.getStatus() )
                    .in(DevProductPlan::getProductPlanSid,sids));
            for(Long id:sids){
                devProductPlan.setProductPlanSid(id);
                row=devProductPlanMapper.updateById( devProductPlan);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=devProductPlan.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(devProductPlan.getProductPlanSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param devProductPlan
     * @return
     */
    @Override
    public int check(DevProductPlan devProductPlan){
        int row=0;
        Long[] sids=devProductPlan.getProductPlanSidList();
        if(sids!=null&&sids.length>0){
            row=devProductPlanMapper.update(null,new UpdateWrapper<DevProductPlan>().lambda()
                    .set(DevProductPlan::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .set(DevProductPlan::getConfirmDate ,new Date())
                    .set(DevProductPlan::getConfirmerAccount ,ApiThreadLocalUtil.get().getUsername())
                    .in(DevProductPlan::getProductPlanSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
