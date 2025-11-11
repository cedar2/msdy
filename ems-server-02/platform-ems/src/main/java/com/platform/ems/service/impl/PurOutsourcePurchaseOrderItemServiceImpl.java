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
import com.platform.ems.mapper.PurOutsourcePurchaseOrderItemMapper;
import com.platform.ems.domain.PurOutsourcePurchaseOrderItem;
import com.platform.ems.service.IPurOutsourcePurchaseOrderItemService;

/**
 * 外发加工单-明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-17
 */
@Service
@SuppressWarnings("all")
public class PurOutsourcePurchaseOrderItemServiceImpl extends ServiceImpl<PurOutsourcePurchaseOrderItemMapper,PurOutsourcePurchaseOrderItem>  implements IPurOutsourcePurchaseOrderItemService {
    @Autowired
    private PurOutsourcePurchaseOrderItemMapper purOutsourcePurchaseOrderItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "外发加工单-明细";
    /**
     * 查询外发加工单-明细
     *
     * @param outsourcePurchaseOrderItemSid 外发加工单-明细ID
     * @return 外发加工单-明细
     */
    @Override
    public PurOutsourcePurchaseOrderItem selectPurOutsourcePurchaseOrderItemById(Long outsourcePurchaseOrderItemSid) {
        PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem = purOutsourcePurchaseOrderItemMapper.selectPurOutsourcePurchaseOrderItemById(outsourcePurchaseOrderItemSid);
        MongodbUtil.find(purOutsourcePurchaseOrderItem);
        return  purOutsourcePurchaseOrderItem;
    }

    /**
     * 查询外发加工单-明细列表
     *
     * @param purOutsourcePurchaseOrderItem 外发加工单-明细
     * @return 外发加工单-明细
     */
    @Override
    public List<PurOutsourcePurchaseOrderItem> selectPurOutsourcePurchaseOrderItemList(PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem) {
        return purOutsourcePurchaseOrderItemMapper.selectPurOutsourcePurchaseOrderItemList(purOutsourcePurchaseOrderItem);
    }

    /**
     * 新增外发加工单-明细
     * 需要注意编码重复校验
     * @param purOutsourcePurchaseOrderItem 外发加工单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurOutsourcePurchaseOrderItem(PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem) {
        int row= purOutsourcePurchaseOrderItemMapper.insert(purOutsourcePurchaseOrderItem);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(purOutsourcePurchaseOrderItem.getOutsourcePurchaseOrderItemSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改外发加工单-明细
     *
     * @param purOutsourcePurchaseOrderItem 外发加工单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOutsourcePurchaseOrderItem(PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem) {
        PurOutsourcePurchaseOrderItem response = purOutsourcePurchaseOrderItemMapper.selectPurOutsourcePurchaseOrderItemById(purOutsourcePurchaseOrderItem.getOutsourcePurchaseOrderItemSid());
        int row=purOutsourcePurchaseOrderItemMapper.updateById(purOutsourcePurchaseOrderItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(purOutsourcePurchaseOrderItem.getOutsourcePurchaseOrderItemSid(), BusinessType.UPDATE.ordinal(), response,purOutsourcePurchaseOrderItem,TITLE);
        }
        return row;
    }

    /**
     * 变更外发加工单-明细
     *
     * @param purOutsourcePurchaseOrderItem 外发加工单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurOutsourcePurchaseOrderItem(PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem) {
        PurOutsourcePurchaseOrderItem response = purOutsourcePurchaseOrderItemMapper.selectPurOutsourcePurchaseOrderItemById(purOutsourcePurchaseOrderItem.getOutsourcePurchaseOrderItemSid());
                                                                                                                                            int row=purOutsourcePurchaseOrderItemMapper.updateAllById(purOutsourcePurchaseOrderItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(purOutsourcePurchaseOrderItem.getOutsourcePurchaseOrderItemSid(), BusinessType.CHANGE.ordinal(), response,purOutsourcePurchaseOrderItem,TITLE);
        }
        return row;
    }

    /**
     * 批量删除外发加工单-明细
     *
     * @param outsourcePurchaseOrderItemSids 需要删除的外发加工单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurOutsourcePurchaseOrderItemByIds(List<Long> outsourcePurchaseOrderItemSids) {
        return purOutsourcePurchaseOrderItemMapper.deleteBatchIds(outsourcePurchaseOrderItemSids);
    }

    /**
     *更改确认状态
     * @param purOutsourcePurchaseOrderItem
     * @return
     */
    @Override
    public int check(PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem){
        int row=0;
        Long[] sids=purOutsourcePurchaseOrderItem.getOutsourcePurchaseOrderItemSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                purOutsourcePurchaseOrderItem.setOutsourcePurchaseOrderItemSid(id);
                row=purOutsourcePurchaseOrderItemMapper.updateById( purOutsourcePurchaseOrderItem);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(purOutsourcePurchaseOrderItem.getOutsourcePurchaseOrderItemSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 外发加工单明细报表
     */
    @Override
    public List<PurOutsourcePurchaseOrderItem> getItemList(PurOutsourcePurchaseOrderItem purOutsourcePurchaseOrderItem) {
        return purOutsourcePurchaseOrderItemMapper.getItemList(purOutsourcePurchaseOrderItem);
    }
}
