package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.mapper.SalSalesOrderDeliveryPlanMapper;
import com.platform.ems.domain.SalSalesOrderDeliveryPlan;
import com.platform.ems.service.ISalSalesOrderDeliveryPlanService;

/**
 * 销售订单-发货计划Service业务层处理
 *
 * @author linhongwei
 * @date 2021-11-01
 */
@Service
@SuppressWarnings("all")
public class SalSalesOrderDeliveryPlanServiceImpl extends ServiceImpl<SalSalesOrderDeliveryPlanMapper,SalSalesOrderDeliveryPlan>  implements ISalSalesOrderDeliveryPlanService {
    @Autowired
    private SalSalesOrderDeliveryPlanMapper salSalesOrderDeliveryPlanMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "销售订单-发货计划";
    /**
     * 查询销售订单-发货计划
     *
     * @param deliveryPlanSid 销售订单-发货计划ID
     * @return 销售订单-发货计划
     */
    @Override
    public List<SalSalesOrderDeliveryPlan> selectSalSalesOrderDeliveryPlanById(Long salesOrderItemSid) {
        List<SalSalesOrderDeliveryPlan> salSalesOrderDeliveryPlans = salSalesOrderDeliveryPlanMapper.selectSalSalesOrderDeliveryPlanById(salesOrderItemSid);
        return  salSalesOrderDeliveryPlans;
    }

    /**
     * 查询销售订单-发货计划列表
     *
     * @param salSalesOrderDeliveryPlan 销售订单-发货计划
     * @return 销售订单-发货计划
     */
    @Override
    public List<SalSalesOrderDeliveryPlan> selectSalSalesOrderDeliveryPlanList(SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan) {
        return salSalesOrderDeliveryPlanMapper.selectSalSalesOrderDeliveryPlanList(salSalesOrderDeliveryPlan);
    }

    /**
     * 新增销售订单-发货计划
     * 需要注意编码重复校验
     * @param salSalesOrderDeliveryPlan 销售订单-发货计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalSalesOrderDeliveryPlan(List<SalSalesOrderDeliveryPlan> salSalesOrderDeliveryPlans) {
        int row;
        if(CollectionUtil.isNotEmpty(salSalesOrderDeliveryPlans)){
            Long salesOrderItemSid = salSalesOrderDeliveryPlans.get(0).getSalesOrderItemSid();
            salSalesOrderDeliveryPlanMapper.delete( new QueryWrapper<SalSalesOrderDeliveryPlan>().lambda()
            .eq(SalSalesOrderDeliveryPlan::getSalesOrderItemSid,salesOrderItemSid)
            );
            row= salSalesOrderDeliveryPlanMapper.inserts(salSalesOrderDeliveryPlans);
        }else{
            row=1;
        }
        return row;
    }

    /**
     * 修改销售订单-发货计划
     *
     * @param salSalesOrderDeliveryPlan 销售订单-发货计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalSalesOrderDeliveryPlan(SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan) {
        return 1;
    }

    /**
     * 变更销售订单-发货计划
     *
     * @param salSalesOrderDeliveryPlan 销售订单-发货计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSalSalesOrderDeliveryPlan(SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan) {
        return 1;
    }

    /**
     * 批量删除销售订单-发货计划
     *
     * @param deliveryPlanSids 需要删除的销售订单-发货计划ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalSalesOrderDeliveryPlanByIds(List<Long> deliveryPlanSids) {
        return salSalesOrderDeliveryPlanMapper.deleteBatchIds(deliveryPlanSids);
    }

    /**
    * 启用/停用
    * @param salSalesOrderDeliveryPlan
    * @return
    */
    @Override
    public int changeStatus(SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan){
        int row=0;
        return row;
    }


    /**
     *更改确认状态
     * @param salSalesOrderDeliveryPlan
     * @return
     */
    @Override
    public int check(SalSalesOrderDeliveryPlan salSalesOrderDeliveryPlan){
        int row=0;
        return row;
    }


}
