package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.UserOperLog;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.platform.ems.mapper.DelOutsourceDeliveryNoteItemMapper;
import com.platform.ems.domain.DelOutsourceDeliveryNoteItem;
import com.platform.ems.service.IDelOutsourceDeliveryNoteItemService;

/**
 * 外发加工收货单-明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@Service
@SuppressWarnings("all")
public class DelOutsourceDeliveryNoteItemServiceImpl extends ServiceImpl<DelOutsourceDeliveryNoteItemMapper,DelOutsourceDeliveryNoteItem>  implements IDelOutsourceDeliveryNoteItemService {
    @Autowired
    private DelOutsourceDeliveryNoteItemMapper delOutsourceDeliveryNoteItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "外发加工收货单-明细";
    /**
     * 查询外发加工收货单-明细
     *
     * @param deliveryNoteItemSid 外发加工收货单-明细ID
     * @return 外发加工收货单-明细
     */
    @Override
    public DelOutsourceDeliveryNoteItem selectDelOutsourceDeliveryNoteItemById(Long deliveryNoteItemSid) {
        DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem = delOutsourceDeliveryNoteItemMapper.selectDelOutsourceDeliveryNoteItemById(deliveryNoteItemSid);
        MongodbUtil.find(delOutsourceDeliveryNoteItem);
        return  delOutsourceDeliveryNoteItem;
    }

    /**
     * 查询外发加工收货单-明细列表
     *
     * @param delOutsourceDeliveryNoteItem 外发加工收货单-明细
     * @return 外发加工收货单-明细
     */
    @Override
    public List<DelOutsourceDeliveryNoteItem> selectDelOutsourceDeliveryNoteItemList(DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem) {
        return delOutsourceDeliveryNoteItemMapper.selectDelOutsourceDeliveryNoteItemList(delOutsourceDeliveryNoteItem);
    }

    /**
     * 新增外发加工收货单-明细
     * 需要注意编码重复校验
     * @param delOutsourceDeliveryNoteItem 外发加工收货单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDelOutsourceDeliveryNoteItem(DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem) {
        int row= delOutsourceDeliveryNoteItemMapper.insert(delOutsourceDeliveryNoteItem);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(delOutsourceDeliveryNoteItem.getDeliveryNoteItemSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改外发加工收货单-明细
     *
     * @param delOutsourceDeliveryNoteItem 外发加工收货单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDelOutsourceDeliveryNoteItem(DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem) {
        DelOutsourceDeliveryNoteItem response = delOutsourceDeliveryNoteItemMapper.selectDelOutsourceDeliveryNoteItemById(delOutsourceDeliveryNoteItem.getDeliveryNoteItemSid());
        int row=delOutsourceDeliveryNoteItemMapper.updateById(delOutsourceDeliveryNoteItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(delOutsourceDeliveryNoteItem.getDeliveryNoteItemSid(), BusinessType.UPDATE.getValue(), response,delOutsourceDeliveryNoteItem,TITLE);
        }
        return row;
    }

    /**
     * 变更外发加工收货单-明细
     *
     * @param delOutsourceDeliveryNoteItem 外发加工收货单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDelOutsourceDeliveryNoteItem(DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem) {
        DelOutsourceDeliveryNoteItem response = delOutsourceDeliveryNoteItemMapper.selectDelOutsourceDeliveryNoteItemById(delOutsourceDeliveryNoteItem.getDeliveryNoteItemSid());
                                                                                                                int row=delOutsourceDeliveryNoteItemMapper.updateAllById(delOutsourceDeliveryNoteItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(delOutsourceDeliveryNoteItem.getDeliveryNoteItemSid(), BusinessType.CHANGE.getValue(), response,delOutsourceDeliveryNoteItem,TITLE);
        }
        return row;
    }

    /**
     * 批量删除外发加工收货单-明细
     *
     * @param deliveryNoteItemSids 需要删除的外发加工收货单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDelOutsourceDeliveryNoteItemByIds(List<Long> deliveryNoteItemSids) {
        return delOutsourceDeliveryNoteItemMapper.deleteBatchIds(deliveryNoteItemSids);
    }

    /**
     *更改确认状态
     * @param delOutsourceDeliveryNoteItem
     * @return
     */
    @Override
    public int check(DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem){
        int row=0;
        Long[] sids=delOutsourceDeliveryNoteItem.getDeliveryNoteItemSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                delOutsourceDeliveryNoteItem.setDeliveryNoteItemSid(id);
                row=delOutsourceDeliveryNoteItemMapper.updateById( delOutsourceDeliveryNoteItem);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(delOutsourceDeliveryNoteItem.getDeliveryNoteItemSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 外发加工收货单明细报表
     */
    @Override
    public List<DelOutsourceDeliveryNoteItem> getItemList(DelOutsourceDeliveryNoteItem delOutsourceDeliveryNoteItem) {
        return delOutsourceDeliveryNoteItemMapper.getItemList(delOutsourceDeliveryNoteItem);
    }
}
