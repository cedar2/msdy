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
import com.platform.ems.plug.domain.ConDocTypeSalesOrder;
import com.platform.ems.plug.mapper.ConDocTypeSalesOrderMapper;
import com.platform.ems.plug.service.IConDocTypeSalesOrderService;
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
 * 单据类型_销售订单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeSalesOrderServiceImpl extends ServiceImpl<ConDocTypeSalesOrderMapper,ConDocTypeSalesOrder>  implements IConDocTypeSalesOrderService {
    @Autowired
    private ConDocTypeSalesOrderMapper conDocTypeSalesOrderMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_销售订单";
    /**
     * 查询单据类型_销售订单
     *
     * @param sid 单据类型_销售订单ID
     * @return 单据类型_销售订单
     */
    @Override
    public ConDocTypeSalesOrder selectConDocTypeSalesOrderById(Long sid) {
        ConDocTypeSalesOrder conDocTypeSalesOrder = conDocTypeSalesOrderMapper.selectConDocTypeSalesOrderById(sid);
        MongodbUtil.find(conDocTypeSalesOrder);
        return  conDocTypeSalesOrder;
    }

    /**
     * 查询单据类型_销售订单列表
     *
     * @param conDocTypeSalesOrder 单据类型_销售订单
     * @return 单据类型_销售订单
     */
    @Override
    public List<ConDocTypeSalesOrder> selectConDocTypeSalesOrderList(ConDocTypeSalesOrder conDocTypeSalesOrder) {
        return conDocTypeSalesOrderMapper.selectConDocTypeSalesOrderList(conDocTypeSalesOrder);
    }

    /**
     * 新增单据类型_销售订单
     * 需要注意编码重复校验
     * @param conDocTypeSalesOrder 单据类型_销售订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeSalesOrder(ConDocTypeSalesOrder conDocTypeSalesOrder) {
        List<ConDocTypeSalesOrder> codeList = conDocTypeSalesOrderMapper.selectList(new QueryWrapper<ConDocTypeSalesOrder>().lambda()
                .eq(ConDocTypeSalesOrder::getCode, conDocTypeSalesOrder.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeSalesOrder> nameList = conDocTypeSalesOrderMapper.selectList(new QueryWrapper<ConDocTypeSalesOrder>().lambda()
                .eq(ConDocTypeSalesOrder::getName, conDocTypeSalesOrder.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDocTypeSalesOrderMapper.insert(conDocTypeSalesOrder);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeSalesOrder.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_销售订单
     *
     * @param conDocTypeSalesOrder 单据类型_销售订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeSalesOrder(ConDocTypeSalesOrder conDocTypeSalesOrder) {
        ConDocTypeSalesOrder response = conDocTypeSalesOrderMapper.selectConDocTypeSalesOrderById(conDocTypeSalesOrder.getSid());
        int row=conDocTypeSalesOrderMapper.updateById(conDocTypeSalesOrder);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeSalesOrder.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypeSalesOrder,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_销售订单
     *
     * @param conDocTypeSalesOrder 单据类型_销售订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeSalesOrder(ConDocTypeSalesOrder conDocTypeSalesOrder) {
        List<ConDocTypeSalesOrder> nameList = conDocTypeSalesOrderMapper.selectList(new QueryWrapper<ConDocTypeSalesOrder>().lambda()
                .eq(ConDocTypeSalesOrder::getName, conDocTypeSalesOrder.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeSalesOrder.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeSalesOrder.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypeSalesOrder response = conDocTypeSalesOrderMapper.selectConDocTypeSalesOrderById(conDocTypeSalesOrder.getSid());
        int row = conDocTypeSalesOrderMapper.updateAllById(conDocTypeSalesOrder);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeSalesOrder.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeSalesOrder, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_销售订单
     *
     * @param sids 需要删除的单据类型_销售订单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeSalesOrderByIds(List<Long> sids) {
        return conDocTypeSalesOrderMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypeSalesOrder
    * @return
    */
    @Override
    public int changeStatus(ConDocTypeSalesOrder conDocTypeSalesOrder){
        int row=0;
        Long[] sids=conDocTypeSalesOrder.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeSalesOrder.setSid(id);
                row=conDocTypeSalesOrderMapper.updateById( conDocTypeSalesOrder);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeSalesOrder.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeSalesOrder.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeSalesOrder
     * @return
     */
    @Override
    public int check(ConDocTypeSalesOrder conDocTypeSalesOrder){
        int row=0;
        Long[] sids=conDocTypeSalesOrder.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeSalesOrder.setSid(id);
                row=conDocTypeSalesOrderMapper.updateById( conDocTypeSalesOrder);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeSalesOrder.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConDocTypeSalesOrder> getConDocTypeSalesOrderList() {
        return conDocTypeSalesOrderMapper.getConDocTypeSalesOrderList();
    }

    @Override
    public List<ConDocTypeSalesOrder> getList(ConDocTypeSalesOrder conDocTypeSalesOrder) {
        return conDocTypeSalesOrderMapper.getList(conDocTypeSalesOrder);
    }
}
