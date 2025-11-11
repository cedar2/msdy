package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.PurInquiry;
import com.platform.ems.util.MongodbDeal;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.PurInquiryItemMapper;
import com.platform.ems.domain.PurInquiryItem;
import com.platform.ems.service.IPurInquiryItemService;

/**
 * 物料询价单明细Service业务层处理
 *
 * @author chenkw
 * @date 2022-01-11
 */
@Service
@SuppressWarnings("all")
public class PurInquiryItemServiceImpl extends ServiceImpl<PurInquiryItemMapper, PurInquiryItem> implements IPurInquiryItemService {
    @Autowired
    private PurInquiryItemMapper purInquiryItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "采购物料询价单-明细";

    private static final String MAIN_TITLE = "采购物料询价单";

    /**
     * 查询物料询价单明细
     *
     * @param inquiryItemSid 物料询价单明细ID
     * @return 物料询价单明细
     */
    @Override
    public PurInquiryItem selectPurInquiryItemById(Long inquiryItemSid) {
        PurInquiryItem purInquiryItem = purInquiryItemMapper.selectPurInquiryItemById(inquiryItemSid);
        MongodbUtil.find(purInquiryItem);
        return purInquiryItem;
    }

    /**
     * 查询物料询价单明细列表
     *
     * @param purInquiryItem 物料询价单明细
     * @return 物料询价单明细
     */
    @Override
    public List<PurInquiryItem> selectPurInquiryItemList(PurInquiryItem purInquiryItem) {
        return purInquiryItemMapper.selectPurInquiryItemList(purInquiryItem);
    }

    /**
     * 新增物料询价单明细
     * 需要注意编码重复校验
     *
     * @param purInquiryItem 物料询价单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurInquiryItem(PurInquiryItem purInquiryItem) {
        int row = purInquiryItemMapper.insert(purInquiryItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(purInquiryItem.getInquiryItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改物料询价单明细
     *
     * @param purInquiryItem 物料询价单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurInquiryItem(PurInquiryItem purInquiryItem) {
        PurInquiryItem response = purInquiryItemMapper.selectPurInquiryItemById(purInquiryItem.getInquiryItemSid());
        int row = purInquiryItemMapper.updateById(purInquiryItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purInquiryItem.getInquiryItemSid(), BusinessType.UPDATE.getValue(), response, purInquiryItem, TITLE);
        }
        return row;
    }

    /**
     * 变更物料询价单明细
     *
     * @param purInquiryItem 物料询价单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurInquiryItem(PurInquiryItem purInquiryItem) {
        PurInquiryItem response = purInquiryItemMapper.selectPurInquiryItemById(purInquiryItem.getInquiryItemSid());
        int row = purInquiryItemMapper.updateAllById(purInquiryItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purInquiryItem.getInquiryItemSid(), BusinessType.CHANGE.getValue(), response, purInquiryItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除物料询价单明细
     *
     * @param inquiryItemSids 需要删除的物料询价单明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurInquiryItemByIds(List<Long> inquiryItemSids) {
        int row = purInquiryItemMapper.deleteBatchIds(inquiryItemSids);
        if (row > 0) {
            inquiryItemSids.forEach(sid -> {
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), TITLE);
            });
        }
        return row;
    }

    /**
     * 查询物料询价单明细
     *
     * @param inquirySid 物料询价单ID
     * @return 物料询价单明细
     */
    @Override
    public List<PurInquiryItem> selectPurInquiryItemListById(Long inquirySid) {
        List<PurInquiryItem> list = purInquiryItemMapper.selectPurInquiryItemListById(inquirySid);
        list.forEach(item -> {
            MongodbUtil.find(item);
        });
        return list;
    }

    /**
     * 批量新增物料询价单明细
     * 需要注意编码重复校验
     *
     * @param purInquiryItem 物料询价单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurInquiryItemList(List<PurInquiryItem> list, PurInquiry purInquiry) {
        if (CollectionUtil.isEmpty(list)) {
            return 0;
        }
        list.forEach(item -> {
            item.setClientId(ApiThreadLocalUtil.get().getClientId());
            item.setInquirySid(purInquiry.getInquirySid());
        });
        int row = purInquiryItemMapper.inserts(list);
        if (row > 0) {
            list.forEach(item -> {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.insert(item.getInquiryItemSid(), purInquiry.getHandleStatus(), msgList, TITLE, MAIN_TITLE + ":" + purInquiry.getInquirySid().toString());
            });
        }
        return row;
    }

    /**
     * 修改物料询价单明细
     *
     * @param purInquiryItem 物料询价单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurInquiryItemList(List<PurInquiryItem> list, PurInquiry purInquiry) {
        int row = 0;
        for (PurInquiryItem purInquiryItem : list) {
            PurInquiryItem response = purInquiryItemMapper.selectPurInquiryItemById(purInquiryItem.getInquiryItemSid());
            purInquiryItem.setUpdaterAccount(null).setUpdateDate(null);
            row = purInquiryItemMapper.updateById(purInquiryItem);
            if (row > 0) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(response, purInquiryItem);
                MongodbDeal.update(purInquiryItem.getInquiryItemSid(), purInquiry.getHandleStatus(), purInquiryItem.getHandleStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 批量删除物料询价单明细
     *
     * @param inquiryItemSids 需要删除的物料询价单明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurInquiryItemByInquirySids(List<Long> inquirySids) {
        int row = purInquiryItemMapper.deletePurInquiryItemByInquirySids(inquirySids);
        if (row > 0) {
            inquirySids.forEach(sid -> {
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), null, TITLE, MAIN_TITLE + ":" + sid.toString());
            });
        }
        return row;
    }

    /**
     * 根据主表sid查询明细sid列表
     *
     * @param purInquiryItem
     * @return
     */
    @Override
    public List<Long> selectPurInquiryItemSidListById(Long[] purInquirySids) {
        return purInquiryItemMapper.selectPurInquiryItemSidListById(purInquirySids);
    }

    /**
     * 查询物料询价单明细报表
     *
     * @param purInquiryItem 物料询价单明细
     * @return 物料询价单明细
     */
    @Override
    public List<PurInquiryItem> getReportForm(PurInquiryItem purInquiryItem) {
        return purInquiryItemMapper.getReportForm(purInquiryItem);
    }

}
