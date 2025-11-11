package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConBuTypePurchaseOrder;
import com.platform.ems.plug.domain.ConDocBuTypeGroupPo;
import com.platform.ems.plug.domain.ConDocTypePurchaseOrder;
import com.platform.ems.plug.mapper.ConBuTypePurchaseOrderMapper;
import com.platform.ems.plug.mapper.ConDocBuTypeGroupPoMapper;
import com.platform.ems.plug.service.IConBuTypePurchaseOrderService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务类型_采购订单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypePurchaseOrderServiceImpl extends ServiceImpl<ConBuTypePurchaseOrderMapper,ConBuTypePurchaseOrder>  implements IConBuTypePurchaseOrderService {
    @Autowired
    private ConBuTypePurchaseOrderMapper conBuTypePurchaseOrderMapper;
    @Autowired
    private ConDocBuTypeGroupPoMapper conDocBuTypeGroupPoMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_采购订单";
    /**
     * 查询业务类型_采购订单
     *
     * @param sid 业务类型_采购订单ID
     * @return 业务类型_采购订单
     */
    @Override
    public ConBuTypePurchaseOrder selectConBuTypePurchaseOrderById(Long sid) {
        ConBuTypePurchaseOrder conBuTypePurchaseOrder = conBuTypePurchaseOrderMapper.selectConBuTypePurchaseOrderById(sid);
        MongodbUtil.find(conBuTypePurchaseOrder);
        return  conBuTypePurchaseOrder;
    }

    /**
     * 查询业务类型_采购订单列表
     *
     * @param conBuTypePurchaseOrder 业务类型_采购订单
     * @return 业务类型_采购订单
     */
    @Override
    public List<ConBuTypePurchaseOrder> selectConBuTypePurchaseOrderList(ConBuTypePurchaseOrder conBuTypePurchaseOrder) {
        return conBuTypePurchaseOrderMapper.selectConBuTypePurchaseOrderList(conBuTypePurchaseOrder);
    }

    /**
     * 新增业务类型_采购订单
     * 需要注意编码重复校验
     * @param conBuTypePurchaseOrder 业务类型_采购订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypePurchaseOrder(ConBuTypePurchaseOrder conBuTypePurchaseOrder) {
        List<ConBuTypePurchaseOrder> codeList = conBuTypePurchaseOrderMapper.selectList(new QueryWrapper<ConBuTypePurchaseOrder>().lambda()
                .eq(ConBuTypePurchaseOrder::getCode, conBuTypePurchaseOrder.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypePurchaseOrder> nameList = conBuTypePurchaseOrderMapper.selectList(new QueryWrapper<ConBuTypePurchaseOrder>().lambda()
                .eq(ConBuTypePurchaseOrder::getName, conBuTypePurchaseOrder.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypePurchaseOrderMapper.insert(conBuTypePurchaseOrder);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypePurchaseOrder.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_采购订单
     *
     * @param conBuTypePurchaseOrder 业务类型_采购订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypePurchaseOrder(ConBuTypePurchaseOrder conBuTypePurchaseOrder) {
        ConBuTypePurchaseOrder response = conBuTypePurchaseOrderMapper.selectConBuTypePurchaseOrderById(conBuTypePurchaseOrder.getSid());
        int row=conBuTypePurchaseOrderMapper.updateById(conBuTypePurchaseOrder);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypePurchaseOrder.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypePurchaseOrder,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_采购订单
     *
     * @param conBuTypePurchaseOrder 业务类型_采购订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypePurchaseOrder(ConBuTypePurchaseOrder conBuTypePurchaseOrder) {
        List<ConBuTypePurchaseOrder> nameList = conBuTypePurchaseOrderMapper.selectList(new QueryWrapper<ConBuTypePurchaseOrder>().lambda()
                .eq(ConBuTypePurchaseOrder::getName, conBuTypePurchaseOrder.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypePurchaseOrder.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypePurchaseOrder.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypePurchaseOrder response = conBuTypePurchaseOrderMapper.selectConBuTypePurchaseOrderById(conBuTypePurchaseOrder.getSid());
        int row = conBuTypePurchaseOrderMapper.updateAllById(conBuTypePurchaseOrder);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypePurchaseOrder.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypePurchaseOrder, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_采购订单
     *
     * @param sids 需要删除的业务类型_采购订单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypePurchaseOrderByIds(List<Long> sids) {
        return conBuTypePurchaseOrderMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypePurchaseOrder
    * @return
    */
    @Override
    public int changeStatus(ConBuTypePurchaseOrder conBuTypePurchaseOrder){
        int row=0;
        Long[] sids=conBuTypePurchaseOrder.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypePurchaseOrder.setSid(id);
                row=conBuTypePurchaseOrderMapper.updateById( conBuTypePurchaseOrder);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypePurchaseOrder.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypePurchaseOrder.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypePurchaseOrder
     * @return
     */
    @Override
    public int check(ConBuTypePurchaseOrder conBuTypePurchaseOrder){
        int row=0;
        Long[] sids=conBuTypePurchaseOrder.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypePurchaseOrder.setSid(id);
                row=conBuTypePurchaseOrderMapper.updateById( conBuTypePurchaseOrder);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypePurchaseOrder.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 业务类型_采购订单下拉框
     */
    @Override
    public List<ConBuTypePurchaseOrder> getList() {
        return conBuTypePurchaseOrderMapper.getList();
    }

    @Override
    public List<ConBuTypePurchaseOrder> getRelevancyBuList(ConDocTypePurchaseOrder conDocTypePurchaseOrder) {
        //单据类型编码
        String docCode = conDocTypePurchaseOrder.getCode();
        List<ConBuTypePurchaseOrder> list = new ArrayList<>();
        if (StrUtil.isNotEmpty(docCode)) {
            List<ConDocBuTypeGroupPo> typeGroupSoList =
                    conDocBuTypeGroupPoMapper.selectConDocBuTypeGroupPoList(new ConDocBuTypeGroupPo().setDocTypeCode(docCode)
                            .setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS));
            if (CollectionUtil.isNotEmpty(typeGroupSoList)) {
                //业务类型编码list
                List<String> buTypeCodeList = typeGroupSoList.stream().map(ConDocBuTypeGroupPo::getBuTypeCode).collect(Collectors.toList());
                conDocTypePurchaseOrder.setBuTypeCodeList(buTypeCodeList);
                list = conBuTypePurchaseOrderMapper.getRelevancyBuList(conDocTypePurchaseOrder);
            }
        }
        return list;
    }
}
