package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.SamOsbSampleReimburseItem;
import com.platform.ems.mapper.SamOsbSampleReimburseItemMapper;
import com.platform.ems.service.ISamOsbSampleReimburseItemService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 外采样报销单-明细Service业务层处理
 *
 * @author qhq
 * @date 2021-12-28
 */
@Service
@SuppressWarnings("all")
public class SamOsbSampleReimburseItemServiceImpl extends ServiceImpl<SamOsbSampleReimburseItemMapper,SamOsbSampleReimburseItem>  implements ISamOsbSampleReimburseItemService {
    @Autowired
    private SamOsbSampleReimburseItemMapper samOsbSampleReimburseItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "外采样报销单-明细";
    /**
     * 查询外采样报销单-明细
     *
     * @param reimburseItemSid 外采样报销单-明细ID
     * @return 外采样报销单-明细
     */
    @Override
    public SamOsbSampleReimburseItem selectSamOsbSampleReimburseItemById(Long reimburseItemSid) {
        SamOsbSampleReimburseItem samOsbSampleReimburseItem = samOsbSampleReimburseItemMapper.selectSamOsbSampleReimburseItemById(reimburseItemSid);
        MongodbUtil.find(samOsbSampleReimburseItem);
        return  samOsbSampleReimburseItem;
    }

    /**
     * 查询外采样报销单-明细列表
     *
     * @param samOsbSampleReimburseItem 外采样报销单-明细
     * @return 外采样报销单-明细
     */
    @Override
    public List<SamOsbSampleReimburseItem> selectSamOsbSampleReimburseItemList(SamOsbSampleReimburseItem samOsbSampleReimburseItem) {
        return samOsbSampleReimburseItemMapper.selectSamOsbSampleReimburseItemList(samOsbSampleReimburseItem);
    }

    /**
     * 新增外采样报销单-明细
     * 需要注意编码重复校验
     * @param samOsbSampleReimburseItem 外采样报销单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSamOsbSampleReimburseItem(SamOsbSampleReimburseItem samOsbSampleReimburseItem) {
        int row= samOsbSampleReimburseItemMapper.insert(samOsbSampleReimburseItem);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(samOsbSampleReimburseItem.getReimburseItemSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改外采样报销单-明细
     *
     * @param samOsbSampleReimburseItem 外采样报销单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSamOsbSampleReimburseItem(SamOsbSampleReimburseItem samOsbSampleReimburseItem) {
        SamOsbSampleReimburseItem response = samOsbSampleReimburseItemMapper.selectSamOsbSampleReimburseItemById(samOsbSampleReimburseItem.getReimburseItemSid());
        int row=samOsbSampleReimburseItemMapper.updateById(samOsbSampleReimburseItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(samOsbSampleReimburseItem.getReimburseItemSid(), BusinessType.UPDATE.ordinal(), response,samOsbSampleReimburseItem,TITLE);
        }
        return row;
    }

    /**
     * 变更外采样报销单-明细
     *
     * @param samOsbSampleReimburseItem 外采样报销单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSamOsbSampleReimburseItem(SamOsbSampleReimburseItem samOsbSampleReimburseItem) {
        SamOsbSampleReimburseItem response = samOsbSampleReimburseItemMapper.selectSamOsbSampleReimburseItemById(samOsbSampleReimburseItem.getReimburseItemSid());
                                                                    int row=samOsbSampleReimburseItemMapper.updateAllById(samOsbSampleReimburseItem);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(samOsbSampleReimburseItem.getReimburseItemSid(), BusinessType.CHANGE.ordinal(), response,samOsbSampleReimburseItem,TITLE);
        }
        return row;
    }

    /**
     * 批量删除外采样报销单-明细
     *
     * @param reimburseItemSids 需要删除的外采样报销单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSamOsbSampleReimburseItemByIds(List<Long> reimburseItemSids) {
        return samOsbSampleReimburseItemMapper.deleteBatchIds(reimburseItemSids);
    }

}
