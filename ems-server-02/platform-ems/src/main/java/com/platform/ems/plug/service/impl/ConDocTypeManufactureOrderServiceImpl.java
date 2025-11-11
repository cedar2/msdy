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
import com.platform.ems.plug.domain.ConDocTypeManufactureOrder;
import com.platform.ems.plug.mapper.ConDocTypeManufactureOrderMapper;
import com.platform.ems.plug.service.IConDocTypeManufactureOrderService;
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
 * 单据类型_生产订单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeManufactureOrderServiceImpl extends ServiceImpl<ConDocTypeManufactureOrderMapper,ConDocTypeManufactureOrder>  implements IConDocTypeManufactureOrderService {
    @Autowired
    private ConDocTypeManufactureOrderMapper conDocTypeManufactureOrderMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_生产订单";
    /**
     * 查询单据类型_生产订单
     *
     * @param sid 单据类型_生产订单ID
     * @return 单据类型_生产订单
     */
    @Override
    public ConDocTypeManufactureOrder selectConDocTypeManufactureOrderById(Long sid) {
        ConDocTypeManufactureOrder conDocTypeManufactureOrder = conDocTypeManufactureOrderMapper.selectConDocTypeManufactureOrderById(sid);
        MongodbUtil.find(conDocTypeManufactureOrder);
        return  conDocTypeManufactureOrder;
    }

    /**
     * 查询单据类型_生产订单列表
     *
     * @param conDocTypeManufactureOrder 单据类型_生产订单
     * @return 单据类型_生产订单
     */
    @Override
    public List<ConDocTypeManufactureOrder> selectConDocTypeManufactureOrderList(ConDocTypeManufactureOrder conDocTypeManufactureOrder) {
        return conDocTypeManufactureOrderMapper.selectConDocTypeManufactureOrderList(conDocTypeManufactureOrder);
    }

    /**
     * 新增单据类型_生产订单
     * 需要注意编码重复校验
     * @param conDocTypeManufactureOrder 单据类型_生产订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeManufactureOrder(ConDocTypeManufactureOrder conDocTypeManufactureOrder) {
        List<ConDocTypeManufactureOrder> codeList = conDocTypeManufactureOrderMapper.selectList(new QueryWrapper<ConDocTypeManufactureOrder>().lambda()
                .eq(ConDocTypeManufactureOrder::getCode, conDocTypeManufactureOrder.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeManufactureOrder> nameList = conDocTypeManufactureOrderMapper.selectList(new QueryWrapper<ConDocTypeManufactureOrder>().lambda()
                .eq(ConDocTypeManufactureOrder::getName, conDocTypeManufactureOrder.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDocTypeManufactureOrderMapper.insert(conDocTypeManufactureOrder);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeManufactureOrder.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_生产订单
     *
     * @param conDocTypeManufactureOrder 单据类型_生产订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeManufactureOrder(ConDocTypeManufactureOrder conDocTypeManufactureOrder) {
        ConDocTypeManufactureOrder response = conDocTypeManufactureOrderMapper.selectConDocTypeManufactureOrderById(conDocTypeManufactureOrder.getSid());
        int row=conDocTypeManufactureOrderMapper.updateById(conDocTypeManufactureOrder);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeManufactureOrder.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypeManufactureOrder,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_生产订单
     *
     * @param conDocTypeManufactureOrder 单据类型_生产订单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeManufactureOrder(ConDocTypeManufactureOrder conDocTypeManufactureOrder) {
        List<ConDocTypeManufactureOrder> nameList = conDocTypeManufactureOrderMapper.selectList(new QueryWrapper<ConDocTypeManufactureOrder>().lambda()
                .eq(ConDocTypeManufactureOrder::getName, conDocTypeManufactureOrder.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeManufactureOrder.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeManufactureOrder.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypeManufactureOrder response = conDocTypeManufactureOrderMapper.selectConDocTypeManufactureOrderById(conDocTypeManufactureOrder.getSid());
        int row = conDocTypeManufactureOrderMapper.updateAllById(conDocTypeManufactureOrder);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeManufactureOrder.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeManufactureOrder, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_生产订单
     *
     * @param sids 需要删除的单据类型_生产订单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeManufactureOrderByIds(List<Long> sids) {
        return conDocTypeManufactureOrderMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypeManufactureOrder
    * @return
    */
    @Override
    public int changeStatus(ConDocTypeManufactureOrder conDocTypeManufactureOrder){
        int row=0;
        Long[] sids=conDocTypeManufactureOrder.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeManufactureOrder.setSid(id);
                row=conDocTypeManufactureOrderMapper.updateById( conDocTypeManufactureOrder);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeManufactureOrder.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeManufactureOrder.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeManufactureOrder
     * @return
     */
    @Override
    public int check(ConDocTypeManufactureOrder conDocTypeManufactureOrder){
        int row=0;
        Long[] sids=conDocTypeManufactureOrder.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeManufactureOrder.setSid(id);
                row=conDocTypeManufactureOrderMapper.updateById( conDocTypeManufactureOrder);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeManufactureOrder.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 单据类型_生产订单下拉框接口
     */
    @Override
    public List<ConDocTypeManufactureOrder> getList() {
        return conDocTypeManufactureOrderMapper.getList();
    }
}
