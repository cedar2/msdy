package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.QuaSpecraftCheckItem;
import com.platform.ems.mapper.QuaSpecraftCheckItemMapper;
import com.platform.ems.service.IQuaSpecraftCheckItemService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 特殊工艺检测单-检测项目Service业务层处理
 *
 * @author linhongwei
 * @date 2022-04-12
 */
@Service
@SuppressWarnings("all")
public class QuaSpecraftCheckItemServiceImpl extends ServiceImpl<QuaSpecraftCheckItemMapper, QuaSpecraftCheckItem> implements IQuaSpecraftCheckItemService {
    @Autowired
    private QuaSpecraftCheckItemMapper quaSpecraftCheckItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "特殊工艺检测单-检测项目";

    /**
     * 查询特殊工艺检测单-检测项目
     *
     * @param specraftCheckItemSid 特殊工艺检测单-检测项目ID
     * @return 特殊工艺检测单-检测项目
     */
    @Override
    public QuaSpecraftCheckItem selectQuaSpecraftCheckItemById(Long specraftCheckItemSid) {
        QuaSpecraftCheckItem quaSpecraftCheckItem = quaSpecraftCheckItemMapper.selectQuaSpecraftCheckItemById(specraftCheckItemSid);
        MongodbUtil.find(quaSpecraftCheckItem);
        return quaSpecraftCheckItem;
    }

    /**
     * 查询特殊工艺检测单-检测项目列表
     *
     * @param quaSpecraftCheckItem 特殊工艺检测单-检测项目
     * @return 特殊工艺检测单-检测项目
     */
    @Override
    public List<QuaSpecraftCheckItem> selectQuaSpecraftCheckItemList(QuaSpecraftCheckItem quaSpecraftCheckItem) {
        return quaSpecraftCheckItemMapper.selectQuaSpecraftCheckItemList(quaSpecraftCheckItem);
    }

    /**
     * 新增特殊工艺检测单-检测项目
     * 需要注意编码重复校验
     *
     * @param quaSpecraftCheckItem 特殊工艺检测单-检测项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaSpecraftCheckItem(QuaSpecraftCheckItem quaSpecraftCheckItem) {
        int row = quaSpecraftCheckItemMapper.insert(quaSpecraftCheckItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(quaSpecraftCheckItem.getSpecraftCheckItemSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改特殊工艺检测单-检测项目
     *
     * @param quaSpecraftCheckItem 特殊工艺检测单-检测项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaSpecraftCheckItem(QuaSpecraftCheckItem quaSpecraftCheckItem) {
        QuaSpecraftCheckItem response = quaSpecraftCheckItemMapper.selectQuaSpecraftCheckItemById(quaSpecraftCheckItem.getSpecraftCheckItemSid());
        int row = quaSpecraftCheckItemMapper.updateById(quaSpecraftCheckItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaSpecraftCheckItem.getSpecraftCheckItemSid(), BusinessType.UPDATE.ordinal(), response, quaSpecraftCheckItem, TITLE);
        }
        return row;
    }

    /**
     * 变更特殊工艺检测单-检测项目
     *
     * @param quaSpecraftCheckItem 特殊工艺检测单-检测项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaSpecraftCheckItem(QuaSpecraftCheckItem quaSpecraftCheckItem) {
        QuaSpecraftCheckItem response = quaSpecraftCheckItemMapper.selectQuaSpecraftCheckItemById(quaSpecraftCheckItem.getSpecraftCheckItemSid());
        int row = quaSpecraftCheckItemMapper.updateAllById(quaSpecraftCheckItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaSpecraftCheckItem.getSpecraftCheckItemSid(), BusinessType.CHANGE.ordinal(), response, quaSpecraftCheckItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除特殊工艺检测单-检测项目
     *
     * @param specraftCheckItemSids 需要删除的特殊工艺检测单-检测项目ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaSpecraftCheckItemByIds(List<Long> specraftCheckItemSids) {
        return quaSpecraftCheckItemMapper.deleteBatchIds(specraftCheckItemSids);
    }

}
