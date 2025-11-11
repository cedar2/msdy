package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.collections4.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.InvRecordGoodsArrivalRequest;
import com.platform.ems.domain.dto.response.InvRecordGoodsArrivalResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.InvRecordGoodsArrivalAttachMapper;
import com.platform.ems.mapper.InvRecordGoodsArrivalDetailMapper;
import com.platform.ems.mapper.InvRecordGoodsArrivalItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.mapper.InvRecordGoodsArrivalMapper;
import com.platform.ems.service.IInvRecordGoodsArrivalService;

/**
 * 采购到货台账Service业务层处理
 *
 * @author linhongwei
 * @date 2022-06-27
 */
@Service
@SuppressWarnings("all")
public class InvRecordGoodsArrivalServiceImpl extends ServiceImpl<InvRecordGoodsArrivalMapper, InvRecordGoodsArrival> implements IInvRecordGoodsArrivalService {
    @Autowired
    private InvRecordGoodsArrivalMapper invRecordGoodsArrivalMapper;
    @Autowired
    private InvRecordGoodsArrivalItemMapper itemMapper;
    @Autowired
    private InvRecordGoodsArrivalAttachMapper attachMapper;
    @Autowired
    private InvRecordGoodsArrivalDetailMapper detailMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "采购到货台账";

    /**
     * 查询采购到货台账
     *
     * @param goodsArrivalSid 采购到货台账ID
     * @return 采购到货台账
     */
    @Override
    public InvRecordGoodsArrival selectInvRecordGoodsArrivalById(Long goodsArrivalSid) {
        InvRecordGoodsArrival invRecordGoodsArrival = invRecordGoodsArrivalMapper.selectInvRecordGoodsArrivalById(goodsArrivalSid);
        List<InvRecordGoodsArrivalItem> invRecordGoodsArrivalItems = itemMapper.selectInvRecordGoodsArrivalItemById(goodsArrivalSid);
        if(CollectionUtil.isNotEmpty(invRecordGoodsArrivalItems)){
            invRecordGoodsArrivalItems.forEach(li->{
                List<InvRecordGoodsArrivalDetail> invRecordGoodsArrivalDetails = detailMapper.selectInvRecordGoodsArrivalDetailById(li.getGoodsArrivalItemSid());
                li.setItemDetailList(invRecordGoodsArrivalDetails);
            });
        }
        invRecordGoodsArrival.setItemList(invRecordGoodsArrivalItems);
        List<InvRecordGoodsArrivalAttach> invRecordGoodsArrivalAttaches = attachMapper.selectInvRecordGoodsArrivalAttachById(goodsArrivalSid);
        invRecordGoodsArrival.setAttachList(invRecordGoodsArrivalAttaches);
        MongodbUtil.find(invRecordGoodsArrival);
        return invRecordGoodsArrival;
    }

    /**
     * 查询采购到货台账列表
     *
     * @param invRecordGoodsArrival 采购到货台账
     * @return 采购到货台账
     */
    @Override
    public List<InvRecordGoodsArrival> selectInvRecordGoodsArrivalList(InvRecordGoodsArrival invRecordGoodsArrival) {
        return invRecordGoodsArrivalMapper.selectInvRecordGoodsArrivalList(invRecordGoodsArrival);
    }
    /**
     * 查询采购到货台账 明细报表
     *
     * @param invRecordGoodsArrival 采购到货台账
     * @return 采购到货台账
     */
    @Override
    public List<InvRecordGoodsArrivalResponse> getReport(InvRecordGoodsArrivalRequest invRecordGoodsArrival) {
        return itemMapper.getReport(invRecordGoodsArrival);
    }

    /**
     * 新增采购到货台账
     * 需要注意编码重复校验
     *
     * @param invRecordGoodsArrival 采购到货台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvRecordGoodsArrival(InvRecordGoodsArrival invRecordGoodsArrival) {
        int row = invRecordGoodsArrivalMapper.insert(invRecordGoodsArrival);
        List<InvRecordGoodsArrivalItem> itemList = invRecordGoodsArrival.getItemList();
        if(CollectionUtil.isNotEmpty(itemList)){
            setItemNum(itemList);
            itemList.forEach(item->{
                item.setGoodsArrivalSid(invRecordGoodsArrival.getGoodsArrivalSid());
                List<InvRecordGoodsArrivalDetail> itemDetailList = item.getItemDetailList();
                if(CollectionUtil.isNotEmpty(itemDetailList)){
                    BigDecimal sum = itemDetailList.stream().map(h -> h.getArrivalQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if(sum.compareTo(item.getArrivalQuantity())==1){
                        throw new CustomException("第"+item.getItemNum()+"行，明细行的到货量总和不能大于本次到货量，请核实！");
                    }
                }
            });
            itemMapper.inserts(itemList);
            itemList.forEach(item->{
                List<InvRecordGoodsArrivalDetail> itemDetailList = item.getItemDetailList();
                if(CollectionUtil.isNotEmpty(itemDetailList)){
                    itemDetailList.forEach(li->{
                        li.setArrivalDetailSid(invRecordGoodsArrival.getGoodsArrivalSid())
                                .setGoodsArrivalItemSid(li.getGoodsArrivalItemSid());
                    });
                    detailMapper.inserts(itemDetailList);
                }
            });
        }
        List<InvRecordGoodsArrivalAttach> attachList = invRecordGoodsArrival.getAttachList();
        if(CollectionUtil.isNotEmpty(attachList)){
            attachList.forEach(attach->{
                attach.setGoodsArrivalSid(invRecordGoodsArrival.getGoodsArrivalSid());
            });
            attachMapper.inserts(attachList);
        }
        if (row > 0) {
            MongodbUtil.insertUserLog(invRecordGoodsArrival.getGoodsArrivalSid(), BusinessType.INSERT.getValue(),TITLE);
        }
        return row;
    }


    public void setItemNum(List<InvRecordGoodsArrivalItem> list){
        int size = list.size();
        if(size>0){
            for (int i=1;i<=size;i++){
                if(list.get(i-1).getItemNum()==null){
                    list.get(i-1).setItemNum(i);
                }
            }
        }
    }

    /**
     * 修改采购到货台账
     *
     * @param invRecordGoodsArrival 采购到货台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvRecordGoodsArrival(InvRecordGoodsArrival invRecordGoodsArrival) {
        InvRecordGoodsArrival response = invRecordGoodsArrivalMapper.selectInvRecordGoodsArrivalById(invRecordGoodsArrival.getGoodsArrivalSid());
        int row = invRecordGoodsArrivalMapper.updateById(invRecordGoodsArrival);
        //采购订单-明细对象
        List<InvRecordGoodsArrivalItem> itemList = invRecordGoodsArrival.getItemList();
        if (CollectionUtils.isNotEmpty(itemList)) {
            setItemNum(itemList);
            List<InvRecordGoodsArrivalItem> recordGoodsArrivalItems = itemMapper.selectList(new QueryWrapper<InvRecordGoodsArrivalItem>().lambda()
                    .eq(InvRecordGoodsArrivalItem::getGoodsArrivalSid, invRecordGoodsArrival.getGoodsArrivalSid())
            );
            List<Long> longs = recordGoodsArrivalItems.stream().map(li -> li.getGoodsArrivalItemSid()).collect(Collectors.toList());
            List<Long> longsNow = itemList.stream().map(li -> li.getGoodsArrivalItemSid()).collect(Collectors.toList());
            //两个集合取差集
            List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
            //删除明细
            if(CollectionUtil.isNotEmpty(reduce)){
                itemMapper.deleteBatchIds(reduce);
                detailMapper.delete(new QueryWrapper<InvRecordGoodsArrivalDetail>().lambda()
                        .in(InvRecordGoodsArrivalDetail::getGoodsArrivalItemSid,reduce));
            }
            //修改明细
            List<InvRecordGoodsArrivalItem> exitItem = itemList.stream().filter(li -> li.getGoodsArrivalItemSid() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(exitItem)){
                exitItem.forEach(li->{
                    itemMapper.updateAllById(li);
                    List<InvRecordGoodsArrivalDetail> itemDetailList = li.getItemDetailList();
                    detailMapper.delete(new QueryWrapper<InvRecordGoodsArrivalDetail>().lambda()
                            .eq(InvRecordGoodsArrivalDetail::getGoodsArrivalItemSid,li.getGoodsArrivalItemSid()));

                    if(CollectionUtil.isNotEmpty(itemDetailList)){
                        BigDecimal sum = itemDetailList.stream().map(h -> h.getArrivalQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        if(sum.compareTo(li.getArrivalQuantity())==1){
                            throw new CustomException("第"+li.getItemNum()+"行，明细行的到货量总和不能大于本次到货量，请核实！");
                        }
                        itemDetailList.forEach(i->{
                            i.setGoodsArrivalItemSid(li.getGoodsArrivalItemSid())
                                    .setArrivalDetailSid(li.getGoodsArrivalSid());
                        });
                        detailMapper.inserts(itemDetailList);
                    }
                });
            }
            //新增明细
            List<InvRecordGoodsArrivalItem> nullItem = itemList.stream().filter(li -> li.getGoodsArrivalItemSid() == null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(nullItem)){
                int max=0;
                if(CollectionUtils.isNotEmpty(recordGoodsArrivalItems)){
                    max = recordGoodsArrivalItems.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
                }
                for (int i = 0; i < nullItem.size(); i++) {
                    int maxItem=max+i+1;
                    nullItem.get(i).setItemNum(maxItem);
                    nullItem.get(i).setGoodsArrivalSid(invRecordGoodsArrival.getGoodsArrivalSid());
                    itemMapper.insert(nullItem.get(i));
                    List<InvRecordGoodsArrivalDetail> itemDetailList = nullItem.get(i).getItemDetailList();
                    if(CollectionUtil.isNotEmpty(itemDetailList)){
                        BigDecimal sum = itemDetailList.stream().map(h -> h.getArrivalQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                        if(sum.compareTo(nullItem.get(i).getArrivalQuantity())==1){
                            throw new CustomException("第"+nullItem.get(i).getItemNum()+"行，明细行的到货量总和不能大于本次到货量，请核实！");
                        }
                        int m=i;
                        itemDetailList.forEach(h->{
                            h.setGoodsArrivalItemSid(nullItem.get(m).getGoodsArrivalItemSid())
                                    .setArrivalDetailSid(nullItem.get(m).getGoodsArrivalSid());
                        });
                        detailMapper.inserts(itemDetailList);
                    }
                }
            }
        }else{
            itemMapper.delete(new QueryWrapper<InvRecordGoodsArrivalItem>().lambda()
                    .eq(InvRecordGoodsArrivalItem::getGoodsArrivalSid,invRecordGoodsArrival.getGoodsArrivalSid())
            );
        }
        if (row > 0) {
            String businessType=response.getHandleStatus().equals(ConstantsEms.SAVA_STATUS)?BusinessType.UPDATE.getValue():BusinessType.CHANGE.getValue();
            //插入日志
            MongodbUtil.insertUserLog(invRecordGoodsArrival.getGoodsArrivalSid(), BusinessType.UPDATE.getValue(),TITLE);
        }
        return row;
    }

    /**
     * 变更采购到货台账
     *
     * @param invRecordGoodsArrival 采购到货台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeInvRecordGoodsArrival(InvRecordGoodsArrival invRecordGoodsArrival) {
        InvRecordGoodsArrival response = invRecordGoodsArrivalMapper.selectInvRecordGoodsArrivalById(invRecordGoodsArrival.getGoodsArrivalSid());
        int row = invRecordGoodsArrivalMapper.updateAllById(invRecordGoodsArrival);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(invRecordGoodsArrival.getGoodsArrivalSid(), BusinessType.CHANGE.ordinal(), response, invRecordGoodsArrival, TITLE);
        }
        return row;
    }

    /**
     * 批量删除采购到货台账
     *
     * @param goodsArrivalSids 需要删除的采购到货台账ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvRecordGoodsArrivalByIds(List<Long> goodsArrivalSids) {
        int row = invRecordGoodsArrivalMapper.deleteBatchIds(goodsArrivalSids);
        itemMapper.delete(new QueryWrapper<InvRecordGoodsArrivalItem>().lambda()
        .in(InvRecordGoodsArrivalItem::getGoodsArrivalSid,goodsArrivalSids)
        );
        detailMapper.delete(new QueryWrapper<InvRecordGoodsArrivalDetail>().lambda()
        .in(InvRecordGoodsArrivalDetail::getArrivalDetailSid,goodsArrivalSids)
        );
        return row;
    }

    /**
     * 启用/停用
     *
     * @param invRecordGoodsArrival
     * @return
     */
    @Override
    public int changeStatus(InvRecordGoodsArrival invRecordGoodsArrival) {
        int row = 0;
        Long[] sids = invRecordGoodsArrival.getGoodsArrivalSidList();
        if (sids != null && sids.length > 0) {
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param invRecordGoodsArrival
     * @return
     */
    @Override
    public int check(InvRecordGoodsArrival invRecordGoodsArrival) {
        int row = 0;
        Long[] sids = invRecordGoodsArrival.getGoodsArrivalSidList();
        if (sids != null && sids.length > 0) {
            row = invRecordGoodsArrivalMapper.update(new InvRecordGoodsArrival(), new UpdateWrapper<InvRecordGoodsArrival>().lambda()
                    .set(InvRecordGoodsArrival::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .set(InvRecordGoodsArrival::getConfirmDate, new Date())
                    .set(InvRecordGoodsArrival::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                    .in(InvRecordGoodsArrival::getGoodsArrivalSid, sids));
            for (Long id : sids) {
                //插入日志
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.ordinal(), TITLE);
            }
        }
        return row;
    }

    /**
     * 作废
     *
     * @param invRecordGoodsArrival
     * @return
     */
    @Override
    public int invalid(InvRecordGoodsArrival invRecordGoodsArrival) {
        int row = 0;
        Long[] sids = invRecordGoodsArrival.getGoodsArrivalSidList();
        if (sids != null && sids.length > 0) {
            row = invRecordGoodsArrivalMapper.update(new InvRecordGoodsArrival(), new UpdateWrapper<InvRecordGoodsArrival>().lambda()
                    .set(InvRecordGoodsArrival::getHandleStatus, HandleStatus.INVALID.getCode())
                    .in(InvRecordGoodsArrival::getGoodsArrivalSid, sids));
            for (Long id : sids) {
                //插入日志
                MongodbUtil.insertApprovalLog(id, BusinessType.CHECK.getValue(), invRecordGoodsArrival.getExplain());
            }
        }
        return row;
    }

}
