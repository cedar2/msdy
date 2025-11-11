package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.PurOutsourceInquiry;
import com.platform.ems.util.MongodbDeal;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.mapper.PurOutsourceInquiryItemMapper;
import com.platform.ems.domain.PurOutsourceInquiryItem;
import com.platform.ems.service.IPurOutsourceInquiryItemService;

/**
 * 加工询价单明细Service业务层处理
 *
 * @author chenkw
 * @date 2022-01-11
 */
@Service
@SuppressWarnings("all")
public class PurOutsourceInquiryItemServiceImpl extends ServiceImpl<PurOutsourceInquiryItemMapper, PurOutsourceInquiryItem> implements IPurOutsourceInquiryItemService {
    @Autowired
    private PurOutsourceInquiryItemMapper purOutsourceInquiryItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "加工询价单-明细";

    private static final String MAIN_TITLE = "加工询价单";

    /**
     * 查询加工询价单明细
     *
     * @param outsourceInquiryItemSid 加工询价单明细ID
     * @return 加工询价单明细
     */
    @Override
    public PurOutsourceInquiryItem selectPurOutsourceInquiryItemById(Long outsourceInquiryItemSid) {
        PurOutsourceInquiryItem purOutsourceInquiryItem = purOutsourceInquiryItemMapper.selectPurOutsourceInquiryItemById(outsourceInquiryItemSid);
        MongodbUtil.find(purOutsourceInquiryItem);
        return purOutsourceInquiryItem;
    }

    /**
     * 查询加工询价单明细列表
     *
     * @param purOutsourceInquiryItem 加工询价单明细
     * @return 加工询价单明细
     */
    @Override
    public List<PurOutsourceInquiryItem> selectPurOutsourceInquiryItemList(PurOutsourceInquiryItem purOutsourceInquiryItem) {
        return purOutsourceInquiryItemMapper.selectPurOutsourceInquiryItemList(purOutsourceInquiryItem);
    }

    /**
     * 新增加工询价单明细
     * 需要注意编码重复校验
     *
     * @param purOutsourceInquiryItem 加工询价单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurOutsourceInquiryItem(PurOutsourceInquiryItem purOutsourceInquiryItem) {
        int row = purOutsourceInquiryItemMapper.insert(purOutsourceInquiryItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(purOutsourceInquiryItem.getOutsourceInquiryItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改加工询价单明细
     *
     * @param purOutsourceInquiryItem 加工询价单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOutsourceInquiryItem(PurOutsourceInquiryItem purOutsourceInquiryItem) {
        PurOutsourceInquiryItem response = purOutsourceInquiryItemMapper.selectPurOutsourceInquiryItemById(purOutsourceInquiryItem.getOutsourceInquiryItemSid());
        int row = purOutsourceInquiryItemMapper.updateById(purOutsourceInquiryItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purOutsourceInquiryItem.getOutsourceInquiryItemSid(), BusinessType.UPDATE.getValue(), response, purOutsourceInquiryItem, TITLE);
        }
        return row;
    }

    /**
     * 变更加工询价单明细
     *
     * @param purOutsourceInquiryItem 加工询价单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurOutsourceInquiryItem(PurOutsourceInquiryItem purOutsourceInquiryItem) {
        PurOutsourceInquiryItem response = purOutsourceInquiryItemMapper.selectPurOutsourceInquiryItemById(purOutsourceInquiryItem.getOutsourceInquiryItemSid());
        int row = purOutsourceInquiryItemMapper.updateAllById(purOutsourceInquiryItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purOutsourceInquiryItem.getOutsourceInquiryItemSid(), BusinessType.CHANGE.getValue(), response, purOutsourceInquiryItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除加工询价单明细
     *
     * @param outsourceInquiryItemSids 需要删除的加工询价单明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurOutsourceInquiryItemByIds(List<Long> outsourceInquiryItemSids) {
        int row = purOutsourceInquiryItemMapper.deleteBatchIds(outsourceInquiryItemSids);
        if (row > 0) {
            outsourceInquiryItemSids.forEach(sid -> {
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), TITLE);
            });
        }
        return row;
    }

    /**
     * 查询加工询价单明细
     *
     * @param inquirySid 物料询价单ID
     * @return 物料询价单明细
     */
    @Override
    public List<PurOutsourceInquiryItem> selectPurOutsourceInquiryItemListById(Long OutsourceInquirySid) {
        List<PurOutsourceInquiryItem> list = purOutsourceInquiryItemMapper.selectPurOutsourceInquiryItemListById(OutsourceInquirySid);
        list.forEach(item -> {
            MongodbUtil.find(item);
        });
        return list;
    }

    /**
     * 批量新增加工询价单明细
     * 需要注意编码重复校验
     *
     * @param purOutsourceInquiry 加工询价单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurOutsourceInquiryItemList(List<PurOutsourceInquiryItem> list, PurOutsourceInquiry purOutsourceInquiry) {
        if (CollectionUtil.isEmpty(list)) {
            return 0;
        }
        list.forEach(item -> {
            item.setClientId(ApiThreadLocalUtil.get().getClientId());
            item.setOutsourceInquirySid(purOutsourceInquiry.getOutsourceInquirySid());
        });
        int row = purOutsourceInquiryItemMapper.inserts(list);
        if (row > 0) {
            list.forEach(item -> {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.insert(item.getOutsourceInquiryItemSid(), purOutsourceInquiry.getHandleStatus(), msgList, TITLE, MAIN_TITLE + ":" + purOutsourceInquiry.getOutsourceInquirySid().toString());
            });
        }
        return row;
    }

    /**
     * 修改加工询价单明细
     *
     * @param purOutsourceInquiryItem 加工询价单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOutsourceInquiryItemList(List<PurOutsourceInquiryItem> list, PurOutsourceInquiry purOutsourceInquiry) {
        int row = 0;
        for (PurOutsourceInquiryItem purOutsourceInquiryItem : list) {
            PurOutsourceInquiryItem response = purOutsourceInquiryItemMapper.selectPurOutsourceInquiryItemById(purOutsourceInquiryItem.getOutsourceInquiryItemSid());
            purOutsourceInquiryItem.setUpdaterAccount(null).setUpdateDate(null);
            row = purOutsourceInquiryItemMapper.updateById(purOutsourceInquiryItem);
            if (row > 0) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(response, purOutsourceInquiryItem);
                MongodbDeal.update(purOutsourceInquiryItem.getOutsourceInquiryItemSid(), purOutsourceInquiry.getHandleStatus(), purOutsourceInquiryItem.getHandleStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 批量删除加工询价单明细
     *
     * @param outsourceInquirySids 需要删除的加工询价单明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurOutsourceInquiryItemByInquirySids(List<Long> outsourceInquirySids) {
        int row = purOutsourceInquiryItemMapper.deletePurOutsourceInquiryItemByInquirySids(outsourceInquirySids);
        if (row > 0) {
            outsourceInquirySids.forEach(sid -> {
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), null, TITLE, MAIN_TITLE + ":" + sid.toString());
            });
        }
        return row;
    }

    /**
     * 根据主表sid查询明细sid列表
     *
     * @param purOutsourceInquiryItem
     * @return
     */
    @Override
    public List<Long> selectPurOutsourceInquiryItemSidListById(Long[] purOutsourceInquirySids) {
        return purOutsourceInquiryItemMapper.selectPurOutsourceInquiryItemSidListById(purOutsourceInquirySids);
    }

    /**
     * 查询加工询价单明细报表
     *
     * @param purOutsourceInquiryItem 加工询价单明细
     * @return 加工询价单明细
     */
    @Override
    public List<PurOutsourceInquiryItem> getReportForm(PurOutsourceInquiryItem purOutsourceInquiryItem) {
        return purOutsourceInquiryItemMapper.getReportForm(purOutsourceInquiryItem);
    }
}
