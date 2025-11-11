package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.PurOutsourcePurchaseOrderAttachment;
import com.platform.ems.domain.PurOutsourcePurchaseOrderItem;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.PurOutsourcePurchaseOrderAttachmentMapper;
import com.platform.ems.mapper.PurOutsourcePurchaseOrderItemMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.PurOutsourcePurchaseOrderMapper;
import com.platform.ems.domain.PurOutsourcePurchaseOrder;
import com.platform.ems.service.IPurOutsourcePurchaseOrderService;

/**
 * 外发加工单Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@Service
@SuppressWarnings("all")
public class PurOutsourcePurchaseOrderServiceImpl extends ServiceImpl<PurOutsourcePurchaseOrderMapper,PurOutsourcePurchaseOrder>  implements IPurOutsourcePurchaseOrderService {
    @Autowired
    private PurOutsourcePurchaseOrderMapper purOutsourcePurchaseOrderMapper;

    @Autowired
    private PurOutsourcePurchaseOrderItemMapper purOutsourcePurchaseOrderItemMapper;

    @Autowired
    private PurOutsourcePurchaseOrderAttachmentMapper purOutsourcePurchaseOrderAttachmentMapper;

    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "外发加工单";
    /**
     * 查询外发加工单
     *
     * @param outsourcePurchaseOrderSid 外发加工单ID
     * @return 外发加工单
     */
    @Override
    public PurOutsourcePurchaseOrder selectPurOutsourcePurchaseOrderById(Long outsourcePurchaseOrderSid) {
        PurOutsourcePurchaseOrder purOutsourcePurchaseOrder = purOutsourcePurchaseOrderMapper.selectPurOutsourcePurchaseOrderById(outsourcePurchaseOrderSid);
        if (purOutsourcePurchaseOrder == null){
            return null;
        }
        //外发加工单-明细
        PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem = new PurOutsourcePurchaseOrderItem();
        purOutsourcePurchaseOrderItem.setOutsourcePurchaseOrderSid(outsourcePurchaseOrderSid);
        List<PurOutsourcePurchaseOrderItem> purOutsourcePurchaseOrderItemList =
                purOutsourcePurchaseOrderItemMapper.selectPurOutsourcePurchaseOrderItemList(purOutsourcePurchaseOrderItem);
        //外发加工单-附件
        PurOutsourcePurchaseOrderAttachment purOutsourcePurchaseOrderAttachment = new PurOutsourcePurchaseOrderAttachment();
        purOutsourcePurchaseOrderAttachment.setOutsourcePurchaseOrderSid(outsourcePurchaseOrderSid);
        List<PurOutsourcePurchaseOrderAttachment> purOutsourcePurchaseOrderAttachmentList =
                purOutsourcePurchaseOrderAttachmentMapper.selectPurOutsourcePurchaseOrderAttachmentList(purOutsourcePurchaseOrderAttachment);
        //外发加工单-合作伙伴
        //TODO
        //日志
        MongodbUtil.find(purOutsourcePurchaseOrder);

        purOutsourcePurchaseOrder.setPurOutsourcePurchaseOrderItemList(purOutsourcePurchaseOrderItemList);
        purOutsourcePurchaseOrder.setPurOutsourcePurchaseOrderAttachmentList(purOutsourcePurchaseOrderAttachmentList);
        return  purOutsourcePurchaseOrder;
    }

    /**
     * 查询外发加工单列表
     *
     * @param purOutsourcePurchaseOrder 外发加工单
     * @return 外发加工单
     */
    @Override
    public List<PurOutsourcePurchaseOrder> selectPurOutsourcePurchaseOrderList(PurOutsourcePurchaseOrder purOutsourcePurchaseOrder) {
        return purOutsourcePurchaseOrderMapper.selectPurOutsourcePurchaseOrderList(purOutsourcePurchaseOrder);
    }

    /**
     * 新增外发加工单
     * 需要注意编码重复校验
     * @param purOutsourcePurchaseOrder 外发加工单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurOutsourcePurchaseOrder(PurOutsourcePurchaseOrder purOutsourcePurchaseOrder) {
        setConfirmInfo(purOutsourcePurchaseOrder);
        int row= purOutsourcePurchaseOrderMapper.insert(purOutsourcePurchaseOrder);
        if( row >0){
            //外发加工单-明细对象
            List<PurOutsourcePurchaseOrderItem> purOutsourcePurchaseOrderItemList =
                    purOutsourcePurchaseOrder.getPurOutsourcePurchaseOrderItemList();
            if (CollectionUtils.isNotEmpty(purOutsourcePurchaseOrderItemList)) {
                addPurOutsourcePurchaseOrderItem(purOutsourcePurchaseOrder, purOutsourcePurchaseOrderItemList);
            }
            //外发加工单-附件对象
            List<PurOutsourcePurchaseOrderAttachment> purOutsourcePurchaseOrderAttachmentList =
                    purOutsourcePurchaseOrder.getPurOutsourcePurchaseOrderAttachmentList();
            if (CollectionUtils.isNotEmpty(purOutsourcePurchaseOrderAttachmentList)) {
                addPurOutsourcePurchaseOrderAttachment(purOutsourcePurchaseOrder, purOutsourcePurchaseOrderAttachmentList);
            }
            //外发加工单-合作伙伴对象
            //TODO
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(purOutsourcePurchaseOrder.getOutsourcePurchaseOrderSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(PurOutsourcePurchaseOrder o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 外发加工单-明细对象
     */
    private void addPurOutsourcePurchaseOrderItem(PurOutsourcePurchaseOrder purOutsourcePurchaseOrder, List<PurOutsourcePurchaseOrderItem> purOutsourcePurchaseOrderItemList) {
        List<PurOutsourcePurchaseOrderItem> itemList=purOutsourcePurchaseOrderItemMapper.selectList(new QueryWrapper<PurOutsourcePurchaseOrderItem>().lambda()
        .eq(PurOutsourcePurchaseOrderItem::getOutsourcePurchaseOrderSid, purOutsourcePurchaseOrder.getOutsourcePurchaseOrderSid()).orderByDesc(PurOutsourcePurchaseOrderItem::getItemNum));
        Long maxNum=1L;
        if(itemList!=null&&itemList.size()>0){
            maxNum=itemList.get(0).getItemNum();
            purOutsourcePurchaseOrderItemMapper.delete(
                    new UpdateWrapper<PurOutsourcePurchaseOrderItem>()
                            .lambda()
                            .eq(PurOutsourcePurchaseOrderItem::getOutsourcePurchaseOrderSid, purOutsourcePurchaseOrder.getOutsourcePurchaseOrderSid())
            );
        }
        for (int i = 0; i < purOutsourcePurchaseOrderItemList.size(); i++) {
            PurOutsourcePurchaseOrderItem item= purOutsourcePurchaseOrderItemList.get(i);
            item.setOutsourcePurchaseOrderSid(purOutsourcePurchaseOrder.getOutsourcePurchaseOrderSid());
            item.setItemNum(maxNum);
            maxNum++;
            purOutsourcePurchaseOrderItemMapper.insert(item);
        }
    }

    /**
     * 外发加工单-附件对象
     */
    private void addPurOutsourcePurchaseOrderAttachment(PurOutsourcePurchaseOrder purOutsourcePurchaseOrder, List<PurOutsourcePurchaseOrderAttachment> purOutsourcePurchaseOrderAttachmentList) {
        purOutsourcePurchaseOrderAttachmentMapper.delete(
                new UpdateWrapper<PurOutsourcePurchaseOrderAttachment>()
                        .lambda()
                        .eq(PurOutsourcePurchaseOrderAttachment::getOutsourcePurchaseOrderSid, purOutsourcePurchaseOrder.getOutsourcePurchaseOrderSid())
        );
        purOutsourcePurchaseOrderAttachmentList.forEach(o -> {
            o.setOutsourcePurchaseOrderSid(purOutsourcePurchaseOrder.getOutsourcePurchaseOrderSid());
            purOutsourcePurchaseOrderAttachmentMapper.insert(o);
        });
    }
    /**
     * 修改外发加工单
     *
     * @param purOutsourcePurchaseOrder 外发加工单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOutsourcePurchaseOrder(PurOutsourcePurchaseOrder purOutsourcePurchaseOrder) {
        PurOutsourcePurchaseOrder response = purOutsourcePurchaseOrderMapper.selectPurOutsourcePurchaseOrderById(purOutsourcePurchaseOrder.getOutsourcePurchaseOrderSid());
        setConfirmInfo(purOutsourcePurchaseOrder);
        int row = purOutsourcePurchaseOrderMapper.updateById(purOutsourcePurchaseOrder);
        if (row > 0){
            //外发加工单-明细对象
            List<PurOutsourcePurchaseOrderItem> purOutsourcePurchaseOrderItemList =
                    purOutsourcePurchaseOrder.getPurOutsourcePurchaseOrderItemList();
            if (CollectionUtils.isNotEmpty(purOutsourcePurchaseOrderItemList)) {
                purOutsourcePurchaseOrderItemList.stream().forEach(o ->{
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addPurOutsourcePurchaseOrderItem(purOutsourcePurchaseOrder, purOutsourcePurchaseOrderItemList);
            }
            //外发加工单-附件对象
            List<PurOutsourcePurchaseOrderAttachment> purOutsourcePurchaseOrderAttachmentList =
                    purOutsourcePurchaseOrder.getPurOutsourcePurchaseOrderAttachmentList();
            if (CollectionUtils.isNotEmpty(purOutsourcePurchaseOrderAttachmentList)) {
                purOutsourcePurchaseOrderAttachmentList.stream().forEach(o ->{
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addPurOutsourcePurchaseOrderAttachment(purOutsourcePurchaseOrder, purOutsourcePurchaseOrderAttachmentList);
            }
            //外发加工单-合作伙伴对象
            //TODO
            //插入日志
            MongodbUtil.insertUserLog(purOutsourcePurchaseOrder.getOutsourcePurchaseOrderSid(), BusinessType.UPDATE.ordinal(), response,purOutsourcePurchaseOrder,TITLE);
        }
        return row;
    }

    /**
     * 变更外发加工单
     *
     * @param purOutsourcePurchaseOrder 外发加工单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurOutsourcePurchaseOrder(PurOutsourcePurchaseOrder purOutsourcePurchaseOrder) {
        PurOutsourcePurchaseOrder response = purOutsourcePurchaseOrderMapper.selectPurOutsourcePurchaseOrderById(purOutsourcePurchaseOrder.getOutsourcePurchaseOrderSid());
                                                                                                                                                    int row=purOutsourcePurchaseOrderMapper.updateAllById(purOutsourcePurchaseOrder);
        if (row > 0){
            //外发加工单-明细对象
            List<PurOutsourcePurchaseOrderItem> purOutsourcePurchaseOrderItemList =
                    purOutsourcePurchaseOrder.getPurOutsourcePurchaseOrderItemList();
            if (CollectionUtils.isNotEmpty(purOutsourcePurchaseOrderItemList)) {
                purOutsourcePurchaseOrderItemList.stream().forEach(o ->{
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addPurOutsourcePurchaseOrderItem(purOutsourcePurchaseOrder, purOutsourcePurchaseOrderItemList);
            }
            //外发加工单-附件对象
            List<PurOutsourcePurchaseOrderAttachment> purOutsourcePurchaseOrderAttachmentList =
                    purOutsourcePurchaseOrder.getPurOutsourcePurchaseOrderAttachmentList();
            if (CollectionUtils.isNotEmpty(purOutsourcePurchaseOrderAttachmentList)) {
                purOutsourcePurchaseOrderAttachmentList.stream().forEach(o ->{
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addPurOutsourcePurchaseOrderAttachment(purOutsourcePurchaseOrder, purOutsourcePurchaseOrderAttachmentList);
            }
            //外发加工单-合作伙伴对象
            //TODO
            //插入日志
            MongodbUtil.insertUserLog(purOutsourcePurchaseOrder.getOutsourcePurchaseOrderSid(), BusinessType.CHANGE.ordinal(), response,purOutsourcePurchaseOrder,TITLE);
        }
        return row;
    }

    /**
     * 批量删除外发加工单
     *
     * @param outsourcePurchaseOrderSids 需要删除的外发加工单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurOutsourcePurchaseOrderByIds(List<Long> outsourcePurchaseOrderSids) {
        PurOutsourcePurchaseOrder params = new PurOutsourcePurchaseOrder();
        params.setOutsourcePurchaseOrderSids(outsourcePurchaseOrderSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = purOutsourcePurchaseOrderMapper.countByDomain(params);
        if (count != outsourcePurchaseOrderSids.size()){
            throw new BaseException("仅保存状态才允许删除");
        }
        //删除外发加工单
        purOutsourcePurchaseOrderMapper.deleteBatchIds(outsourcePurchaseOrderSids);
        //删除外发加工单明细
        purOutsourcePurchaseOrderItemMapper.deleteOutsourcePurchaseOrderItemByIds(outsourcePurchaseOrderSids);
        //删除外发加工单附件
        purOutsourcePurchaseOrderAttachmentMapper.deleteOutsourcePurchaseOrderAttachmentByIds(outsourcePurchaseOrderSids);
        //删除外发加工单合作伙伴
        //TODO
        return outsourcePurchaseOrderSids.size();
    }

    /**
     *更改确认状态
     * @param purOutsourcePurchaseOrder
     * @return
     */
    @Override
    public int check(PurOutsourcePurchaseOrder purOutsourcePurchaseOrder){
        int row=0;
        Long[] sids=purOutsourcePurchaseOrder.getOutsourcePurchaseOrderSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                purOutsourcePurchaseOrder.setOutsourcePurchaseOrderSid(id);
                row=purOutsourcePurchaseOrderMapper.updateById( purOutsourcePurchaseOrder);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(purOutsourcePurchaseOrder.getOutsourcePurchaseOrderSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
