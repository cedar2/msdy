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
import com.platform.ems.plug.domain.ConBuTypeSalesOrder;
import com.platform.ems.plug.domain.ConDocBuTypeGroupSo;
import com.platform.ems.plug.domain.ConDocTypeSalesOrder;
import com.platform.ems.plug.mapper.ConBuTypeSalesOrderMapper;
import com.platform.ems.plug.mapper.ConDocBuTypeGroupSoMapper;
import com.platform.ems.plug.service.IConBuTypeSalesOrderService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务类型_销售订单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeSalesOrderServiceImpl extends ServiceImpl<ConBuTypeSalesOrderMapper,ConBuTypeSalesOrder>  implements IConBuTypeSalesOrderService {
    @Autowired
    private ConBuTypeSalesOrderMapper conBuTypeSalesOrderMapper;
    @Autowired
    private ConDocBuTypeGroupSoMapper conDocBuTypeGroupSoMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_销售订单";
    /**
     * 查询业务类型_销售订单
     *
     * @param sid 业务类型_销售订单ID
     * @return 业务类型_销售订单
     */
    @Override
    public ConBuTypeSalesOrder selectConBuTypeSalesOrderById(Long sid) {
        ConBuTypeSalesOrder conBuTypeSalesOrder = conBuTypeSalesOrderMapper.selectConBuTypeSalesOrderById(sid);
        MongodbUtil.find(conBuTypeSalesOrder);
        return  conBuTypeSalesOrder;
    }

    /**
     * 查询业务类型_销售订单列表
     *
     * @param conBuTypeSalesOrder 业务类型_销售订单
     * @return 业务类型_销售订单
     */
    @Override
    public List<ConBuTypeSalesOrder> selectConBuTypeSalesOrderList(ConBuTypeSalesOrder conBuTypeSalesOrder) {
        return conBuTypeSalesOrderMapper.selectConBuTypeSalesOrderList(conBuTypeSalesOrder);
    }

    /**
     * 新增业务类型_销售订单
     * 需要注意编码重复校验
     * @param conBuTypeSalesOrder 业务类型_销售订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeSalesOrder(ConBuTypeSalesOrder conBuTypeSalesOrder) {
        List<ConBuTypeSalesOrder> codeList = conBuTypeSalesOrderMapper.selectList(new QueryWrapper<ConBuTypeSalesOrder>().lambda()
                .eq(ConBuTypeSalesOrder::getCode, conBuTypeSalesOrder.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeSalesOrder> nameList = conBuTypeSalesOrderMapper.selectList(new QueryWrapper<ConBuTypeSalesOrder>().lambda()
                .eq(ConBuTypeSalesOrder::getName, conBuTypeSalesOrder.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeSalesOrderMapper.insert(conBuTypeSalesOrder);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeSalesOrder.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_销售订单
     *
     * @param conBuTypeSalesOrder 业务类型_销售订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeSalesOrder(ConBuTypeSalesOrder conBuTypeSalesOrder) {
        ConBuTypeSalesOrder response = conBuTypeSalesOrderMapper.selectConBuTypeSalesOrderById(conBuTypeSalesOrder.getSid());
        int row=conBuTypeSalesOrderMapper.updateById(conBuTypeSalesOrder);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeSalesOrder.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeSalesOrder,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_销售订单
     *
     * @param conBuTypeSalesOrder 业务类型_销售订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeSalesOrder(ConBuTypeSalesOrder conBuTypeSalesOrder) {
        List<ConBuTypeSalesOrder> nameList = conBuTypeSalesOrderMapper.selectList(new QueryWrapper<ConBuTypeSalesOrder>().lambda()
                .eq(ConBuTypeSalesOrder::getName, conBuTypeSalesOrder.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeSalesOrder.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeSalesOrder.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeSalesOrder response = conBuTypeSalesOrderMapper.selectConBuTypeSalesOrderById(conBuTypeSalesOrder.getSid());
        int row = conBuTypeSalesOrderMapper.updateAllById(conBuTypeSalesOrder);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeSalesOrder.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeSalesOrder, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_销售订单
     *
     * @param sids 需要删除的业务类型_销售订单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeSalesOrderByIds(List<Long> sids) {
        return conBuTypeSalesOrderMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeSalesOrder
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeSalesOrder conBuTypeSalesOrder){
        int row=0;
        Long[] sids=conBuTypeSalesOrder.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeSalesOrder.setSid(id);
                row=conBuTypeSalesOrderMapper.updateById( conBuTypeSalesOrder);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeSalesOrder.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeSalesOrder.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeSalesOrder
     * @return
     */
    @Override
    public int check(ConBuTypeSalesOrder conBuTypeSalesOrder){
        int row=0;
        Long[] sids=conBuTypeSalesOrder.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeSalesOrder.setSid(id);
                row=conBuTypeSalesOrderMapper.updateById( conBuTypeSalesOrder);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeSalesOrder.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConBuTypeSalesOrder> getConBuTypeSalesOrderList() {
        return conBuTypeSalesOrderMapper.getConBuTypeSalesOrderList();
    }

    /**  根据单据类型获取关联业务类型 */
    @Override
    public List<ConBuTypeSalesOrder> getRelevancyBuList(ConDocTypeSalesOrder conDocTypeSalesOrder) {
        //单据类型编码
        String docCode = conDocTypeSalesOrder.getCode();
        List<ConBuTypeSalesOrder> list = new ArrayList<>();
        if (StrUtil.isNotEmpty(docCode)) {
            List<ConDocBuTypeGroupSo> typeGroupSoList =
                    conDocBuTypeGroupSoMapper.selectConDocBuTypeGroupSoList(new ConDocBuTypeGroupSo().setDocTypeCode(docCode)
                            .setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS));
            if (CollectionUtil.isNotEmpty(typeGroupSoList)) {
                //业务类型编码list
                List<String> buTypeCodeList = typeGroupSoList.stream().map(ConDocBuTypeGroupSo::getBuTypeCode).collect(Collectors.toList());
                conDocTypeSalesOrder.setBuTypeCodeList(buTypeCodeList);
                list = conBuTypeSalesOrderMapper.getList(conDocTypeSalesOrder);
            }
        }
        return list;
    }
}
