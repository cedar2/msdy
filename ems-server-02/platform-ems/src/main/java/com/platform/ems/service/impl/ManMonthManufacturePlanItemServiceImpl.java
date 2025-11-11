package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.ManMonthManufacturePlanItem;
import com.platform.ems.mapper.ManMonthManufacturePlanItemMapper;
import com.platform.ems.service.IManMonthManufacturePlanItemService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 生产月计划-明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-07-16
 */
@Service
@SuppressWarnings("all")
public class ManMonthManufacturePlanItemServiceImpl extends ServiceImpl<ManMonthManufacturePlanItemMapper, ManMonthManufacturePlanItem> implements IManMonthManufacturePlanItemService {
    @Autowired
    private ManMonthManufacturePlanItemMapper manMonthManufacturePlanItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "生产月计划-明细";

    /**
     * 查询生产月计划-明细
     *
     * @param monthManufacturePlanItemSid 生产月计划-明细ID
     * @return 生产月计划-明细
     */
    @Override
    public ManMonthManufacturePlanItem selectManMonthManufacturePlanItemById(Long monthManufacturePlanItemSid) {
        ManMonthManufacturePlanItem manMonthManufacturePlanItem = manMonthManufacturePlanItemMapper.selectManMonthManufacturePlanItemById(monthManufacturePlanItemSid);
        MongodbUtil.find(manMonthManufacturePlanItem);
        return manMonthManufacturePlanItem;
    }

    /**
     * 查询生产月计划-明细列表
     *
     * @param manMonthManufacturePlanItem 生产月计划-明细
     * @return 生产月计划-明细
     */
    @Override
    public List<ManMonthManufacturePlanItem> selectManMonthManufacturePlanItemList(ManMonthManufacturePlanItem manMonthManufacturePlanItem) {
        return manMonthManufacturePlanItemMapper.selectManMonthManufacturePlanItemList(manMonthManufacturePlanItem);
    }

    /**
     * 新增生产月计划-明细
     * 需要注意编码重复校验
     *
     * @param manMonthManufacturePlanItem 生产月计划-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManMonthManufacturePlanItem(ManMonthManufacturePlanItem manMonthManufacturePlanItem) {
        int row = manMonthManufacturePlanItemMapper.insert(manMonthManufacturePlanItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(manMonthManufacturePlanItem.getMonthManufacturePlanItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改生产月计划-明细
     *
     * @param manMonthManufacturePlanItem 生产月计划-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManMonthManufacturePlanItem(ManMonthManufacturePlanItem manMonthManufacturePlanItem) {
        ManMonthManufacturePlanItem response = manMonthManufacturePlanItemMapper.selectManMonthManufacturePlanItemById(manMonthManufacturePlanItem.getMonthManufacturePlanItemSid());
        int row = manMonthManufacturePlanItemMapper.updateById(manMonthManufacturePlanItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manMonthManufacturePlanItem.getMonthManufacturePlanItemSid(), BusinessType.UPDATE.getValue(), response, manMonthManufacturePlanItem, TITLE);
        }
        return row;
    }

    /**
     * 变更生产月计划-明细
     *
     * @param manMonthManufacturePlanItem 生产月计划-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManMonthManufacturePlanItem(ManMonthManufacturePlanItem manMonthManufacturePlanItem) {
        ManMonthManufacturePlanItem response = manMonthManufacturePlanItemMapper.selectManMonthManufacturePlanItemById(manMonthManufacturePlanItem.getMonthManufacturePlanItemSid());
        int row = manMonthManufacturePlanItemMapper.updateAllById(manMonthManufacturePlanItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manMonthManufacturePlanItem.getMonthManufacturePlanItemSid(), BusinessType.CHANGE.getValue(), response, manMonthManufacturePlanItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产月计划-明细
     *
     * @param monthManufacturePlanItemSids 需要删除的生产月计划-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManMonthManufacturePlanItemByIds(List<Long> monthManufacturePlanItemSids) {
        return manMonthManufacturePlanItemMapper.deleteBatchIds(monthManufacturePlanItemSids);
    }

}
