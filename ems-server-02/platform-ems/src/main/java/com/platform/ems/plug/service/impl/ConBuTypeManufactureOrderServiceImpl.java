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
import com.platform.ems.plug.domain.ConBuTypeManufactureOrder;
import com.platform.ems.plug.mapper.ConBuTypeManufactureOrderMapper;
import com.platform.ems.plug.service.IConBuTypeManufactureOrderService;
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
 * 业务类型_生产订单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeManufactureOrderServiceImpl extends ServiceImpl<ConBuTypeManufactureOrderMapper,ConBuTypeManufactureOrder>  implements IConBuTypeManufactureOrderService {
    @Autowired
    private ConBuTypeManufactureOrderMapper conBuTypeManufactureOrderMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_生产订单";
    /**
     * 查询业务类型_生产订单
     *
     * @param sid 业务类型_生产订单ID
     * @return 业务类型_生产订单
     */
    @Override
    public ConBuTypeManufactureOrder selectConBuTypeManufactureOrderById(Long sid) {
        ConBuTypeManufactureOrder conBuTypeManufactureOrder = conBuTypeManufactureOrderMapper.selectConBuTypeManufactureOrderById(sid);
        MongodbUtil.find(conBuTypeManufactureOrder);
        return  conBuTypeManufactureOrder;
    }

    /**
     * 查询业务类型_生产订单列表
     *
     * @param conBuTypeManufactureOrder 业务类型_生产订单
     * @return 业务类型_生产订单
     */
    @Override
    public List<ConBuTypeManufactureOrder> selectConBuTypeManufactureOrderList(ConBuTypeManufactureOrder conBuTypeManufactureOrder) {
        return conBuTypeManufactureOrderMapper.selectConBuTypeManufactureOrderList(conBuTypeManufactureOrder);
    }

    /**
     * 新增业务类型_生产订单
     * 需要注意编码重复校验
     * @param conBuTypeManufactureOrder 业务类型_生产订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeManufactureOrder(ConBuTypeManufactureOrder conBuTypeManufactureOrder) {
        List<ConBuTypeManufactureOrder> codeList = conBuTypeManufactureOrderMapper.selectList(new QueryWrapper<ConBuTypeManufactureOrder>().lambda()
                .eq(ConBuTypeManufactureOrder::getCode, conBuTypeManufactureOrder.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeManufactureOrder> nameList = conBuTypeManufactureOrderMapper.selectList(new QueryWrapper<ConBuTypeManufactureOrder>().lambda()
                .eq(ConBuTypeManufactureOrder::getName, conBuTypeManufactureOrder.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeManufactureOrderMapper.insert(conBuTypeManufactureOrder);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeManufactureOrder.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_生产订单
     *
     * @param conBuTypeManufactureOrder 业务类型_生产订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeManufactureOrder(ConBuTypeManufactureOrder conBuTypeManufactureOrder) {
        ConBuTypeManufactureOrder response = conBuTypeManufactureOrderMapper.selectConBuTypeManufactureOrderById(conBuTypeManufactureOrder.getSid());
        int row=conBuTypeManufactureOrderMapper.updateById(conBuTypeManufactureOrder);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeManufactureOrder.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeManufactureOrder,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_生产订单
     *
     * @param conBuTypeManufactureOrder 业务类型_生产订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeManufactureOrder(ConBuTypeManufactureOrder conBuTypeManufactureOrder) {
        List<ConBuTypeManufactureOrder> nameList = conBuTypeManufactureOrderMapper.selectList(new QueryWrapper<ConBuTypeManufactureOrder>().lambda()
                .eq(ConBuTypeManufactureOrder::getName, conBuTypeManufactureOrder.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeManufactureOrder.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeManufactureOrder.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeManufactureOrder response = conBuTypeManufactureOrderMapper.selectConBuTypeManufactureOrderById(conBuTypeManufactureOrder.getSid());
        int row = conBuTypeManufactureOrderMapper.updateAllById(conBuTypeManufactureOrder);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeManufactureOrder.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeManufactureOrder, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_生产订单
     *
     * @param sids 需要删除的业务类型_生产订单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeManufactureOrderByIds(List<Long> sids) {
        return conBuTypeManufactureOrderMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeManufactureOrder
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeManufactureOrder conBuTypeManufactureOrder){
        int row=0;
        Long[] sids=conBuTypeManufactureOrder.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeManufactureOrder.setSid(id);
                row=conBuTypeManufactureOrderMapper.updateById( conBuTypeManufactureOrder);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeManufactureOrder.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeManufactureOrder.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeManufactureOrder
     * @return
     */
    @Override
    public int check(ConBuTypeManufactureOrder conBuTypeManufactureOrder){
        int row=0;
        Long[] sids=conBuTypeManufactureOrder.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeManufactureOrder.setSid(id);
                row=conBuTypeManufactureOrderMapper.updateById( conBuTypeManufactureOrder);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeManufactureOrder.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
