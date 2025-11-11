package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.QuaRawmatCheckItem;
import com.platform.ems.mapper.QuaRawmatCheckItemMapper;
import com.platform.ems.service.IQuaRawmatCheckItemService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 面辅料检测单-检测项目Service业务层处理
 *
 * @author linhongwei
 * @date 2022-04-11
 */
@Service
@SuppressWarnings("all")
public class QuaRawmatCheckItemServiceImpl extends ServiceImpl<QuaRawmatCheckItemMapper, QuaRawmatCheckItem> implements IQuaRawmatCheckItemService {
    @Autowired
    private QuaRawmatCheckItemMapper quaRawmatCheckItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "面辅料检测单-检测项目";

    /**
     * 查询面辅料检测单-检测项目
     *
     * @param rawmatCheckItemSid 面辅料检测单-检测项目ID
     * @return 面辅料检测单-检测项目
     */
    @Override
    public QuaRawmatCheckItem selectQuaRawmatCheckItemById(Long rawmatCheckItemSid) {
        QuaRawmatCheckItem quaRawmatCheckItem = quaRawmatCheckItemMapper.selectQuaRawmatCheckItemById(rawmatCheckItemSid);
        MongodbUtil.find(quaRawmatCheckItem);
        return quaRawmatCheckItem;
    }

    /**
     * 查询面辅料检测单-检测项目列表
     *
     * @param quaRawmatCheckItem 面辅料检测单-检测项目
     * @return 面辅料检测单-检测项目
     */
    @Override
    public List<QuaRawmatCheckItem> selectQuaRawmatCheckItemList(QuaRawmatCheckItem quaRawmatCheckItem) {
        return quaRawmatCheckItemMapper.selectQuaRawmatCheckItemList(quaRawmatCheckItem);
    }

    /**
     * 新增面辅料检测单-检测项目
     * 需要注意编码重复校验
     *
     * @param quaRawmatCheckItem 面辅料检测单-检测项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaRawmatCheckItem(QuaRawmatCheckItem quaRawmatCheckItem) {
        int row = quaRawmatCheckItemMapper.insert(quaRawmatCheckItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(quaRawmatCheckItem.getRawmatCheckItemSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改面辅料检测单-检测项目
     *
     * @param quaRawmatCheckItem 面辅料检测单-检测项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaRawmatCheckItem(QuaRawmatCheckItem quaRawmatCheckItem) {
        QuaRawmatCheckItem response = quaRawmatCheckItemMapper.selectQuaRawmatCheckItemById(quaRawmatCheckItem.getRawmatCheckItemSid());
        int row = quaRawmatCheckItemMapper.updateById(quaRawmatCheckItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaRawmatCheckItem.getRawmatCheckItemSid(), BusinessType.UPDATE.ordinal(), response, quaRawmatCheckItem, TITLE);
        }
        return row;
    }

    /**
     * 变更面辅料检测单-检测项目
     *
     * @param quaRawmatCheckItem 面辅料检测单-检测项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaRawmatCheckItem(QuaRawmatCheckItem quaRawmatCheckItem) {
        QuaRawmatCheckItem response = quaRawmatCheckItemMapper.selectQuaRawmatCheckItemById(quaRawmatCheckItem.getRawmatCheckItemSid());
        int row = quaRawmatCheckItemMapper.updateAllById(quaRawmatCheckItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaRawmatCheckItem.getRawmatCheckItemSid(), BusinessType.CHANGE.ordinal(), response, quaRawmatCheckItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除面辅料检测单-检测项目
     *
     * @param rawmatCheckItemSids 需要删除的面辅料检测单-检测项目ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaRawmatCheckItemByIds(List<Long> rawmatCheckItemSids) {
        return quaRawmatCheckItemMapper.deleteBatchIds(rawmatCheckItemSids);
    }

}
