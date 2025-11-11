package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConDocTypePurchaseOrder;
import com.platform.ems.plug.mapper.ConDocTypePurchaseOrderMapper;
import com.platform.ems.plug.service.IConDocTypePurchaseOrderService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单据类型_采购订单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypePurchaseOrderServiceImpl extends ServiceImpl<ConDocTypePurchaseOrderMapper,ConDocTypePurchaseOrder>  implements IConDocTypePurchaseOrderService {
    @Autowired
    private ConDocTypePurchaseOrderMapper conDocTypePurchaseOrderMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_采购订单";
    /**
     * 查询单据类型_采购订单
     *
     * @param sid 单据类型_采购订单ID
     * @return 单据类型_采购订单
     */
    @Override
    public ConDocTypePurchaseOrder selectConDocTypePurchaseOrderById(Long sid) {
        ConDocTypePurchaseOrder conDocTypePurchaseOrder = conDocTypePurchaseOrderMapper.selectConDocTypePurchaseOrderById(sid);
        MongodbUtil.find(conDocTypePurchaseOrder);
        return  conDocTypePurchaseOrder;
    }

    /**
     * 查询单据类型_采购订单列表
     *
     * @param conDocTypePurchaseOrder 单据类型_采购订单
     * @return 单据类型_采购订单
     */
    @Override
    public List<ConDocTypePurchaseOrder> selectConDocTypePurchaseOrderList(ConDocTypePurchaseOrder conDocTypePurchaseOrder) {
        return conDocTypePurchaseOrderMapper.selectConDocTypePurchaseOrderList(conDocTypePurchaseOrder);
    }

    /**
     * 新增单据类型_采购订单
     * 需要注意编码重复校验
     * @param conDocTypePurchaseOrder 单据类型_采购订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypePurchaseOrder(ConDocTypePurchaseOrder conDocTypePurchaseOrder) {
        List<ConDocTypePurchaseOrder> codeList = conDocTypePurchaseOrderMapper.selectList(new QueryWrapper<ConDocTypePurchaseOrder>().lambda()
                .eq(ConDocTypePurchaseOrder::getCode, conDocTypePurchaseOrder.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypePurchaseOrder> nameList = conDocTypePurchaseOrderMapper.selectList(new QueryWrapper<ConDocTypePurchaseOrder>().lambda()
                .eq(ConDocTypePurchaseOrder::getName, conDocTypePurchaseOrder.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDocTypePurchaseOrderMapper.insert(conDocTypePurchaseOrder);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypePurchaseOrder.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_采购订单
     *
     * @param conDocTypePurchaseOrder 单据类型_采购订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypePurchaseOrder(ConDocTypePurchaseOrder conDocTypePurchaseOrder) {
        ConDocTypePurchaseOrder response = conDocTypePurchaseOrderMapper.selectConDocTypePurchaseOrderById(conDocTypePurchaseOrder.getSid());
        int row=conDocTypePurchaseOrderMapper.updateById(conDocTypePurchaseOrder);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypePurchaseOrder.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypePurchaseOrder,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_采购订单
     *
     * @param conDocTypePurchaseOrder 单据类型_采购订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypePurchaseOrder(ConDocTypePurchaseOrder conDocTypePurchaseOrder) {
        List<ConDocTypePurchaseOrder> nameList = conDocTypePurchaseOrderMapper.selectList(new QueryWrapper<ConDocTypePurchaseOrder>().lambda()
                .eq(ConDocTypePurchaseOrder::getName, conDocTypePurchaseOrder.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypePurchaseOrder.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypePurchaseOrder.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypePurchaseOrder response = conDocTypePurchaseOrderMapper.selectConDocTypePurchaseOrderById(conDocTypePurchaseOrder.getSid());
        int row = conDocTypePurchaseOrderMapper.updateAllById(conDocTypePurchaseOrder);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypePurchaseOrder.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypePurchaseOrder, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_采购订单
     *
     * @param sids 需要删除的单据类型_采购订单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypePurchaseOrderByIds(List<Long> sids) {
        return conDocTypePurchaseOrderMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypePurchaseOrder
    * @return
    */
    @Override
    public int changeStatus(ConDocTypePurchaseOrder conDocTypePurchaseOrder){
        int row=0;
        Long[] sids=conDocTypePurchaseOrder.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypePurchaseOrder.setSid(id);
                row=conDocTypePurchaseOrderMapper.updateById( conDocTypePurchaseOrder);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypePurchaseOrder.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypePurchaseOrder.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypePurchaseOrder
     * @return
     */
    @Override
    public int check(ConDocTypePurchaseOrder conDocTypePurchaseOrder){
        int row=0;
        Long[] sids=conDocTypePurchaseOrder.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypePurchaseOrder.setSid(id);
                row=conDocTypePurchaseOrderMapper.updateById( conDocTypePurchaseOrder);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypePurchaseOrder.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 单据类型_采购订单下拉框
     */
    @Override
    public List<ConDocTypePurchaseOrder> getList() {
        return conDocTypePurchaseOrderMapper.getList();
    }

    /**
     * 单据类型_采购订单下拉框
     */
    @Override
    public List<ConDocTypePurchaseOrder> getDocList(ConDocTypePurchaseOrder conDocTypePurchaseOrder) {
        return conDocTypePurchaseOrderMapper.getDocList(conDocTypePurchaseOrder);
    }
}
