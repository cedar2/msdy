package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.QuaProductCheckItem;
import com.platform.ems.mapper.QuaProductCheckItemMapper;
import com.platform.ems.service.IQuaProductCheckItemService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 成衣检测单-检测项目Service业务层处理
 *
 * @author linhongwei
 * @date 2022-04-13
 */
@Service
@SuppressWarnings("all")
public class QuaProductCheckItemServiceImpl extends ServiceImpl<QuaProductCheckItemMapper, QuaProductCheckItem> implements IQuaProductCheckItemService {
    @Autowired
    private QuaProductCheckItemMapper quaProductCheckItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "成衣检测单-检测项目";

    /**
     * 查询成衣检测单-检测项目
     *
     * @param productCheckItemSid 成衣检测单-检测项目ID
     * @return 成衣检测单-检测项目
     */
    @Override
    public QuaProductCheckItem selectQuaProductCheckItemById(Long productCheckItemSid) {
        QuaProductCheckItem quaProductCheckItem = quaProductCheckItemMapper.selectQuaProductCheckItemById(productCheckItemSid);
        MongodbUtil.find(quaProductCheckItem);
        return quaProductCheckItem;
    }

    /**
     * 查询成衣检测单-检测项目列表
     *
     * @param quaProductCheckItem 成衣检测单-检测项目
     * @return 成衣检测单-检测项目
     */
    @Override
    public List<QuaProductCheckItem> selectQuaProductCheckItemList(QuaProductCheckItem quaProductCheckItem) {
        return quaProductCheckItemMapper.selectQuaProductCheckItemList(quaProductCheckItem);
    }

    /**
     * 新增成衣检测单-检测项目
     * 需要注意编码重复校验
     *
     * @param quaProductCheckItem 成衣检测单-检测项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaProductCheckItem(QuaProductCheckItem quaProductCheckItem) {
        int row = quaProductCheckItemMapper.insert(quaProductCheckItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(quaProductCheckItem.getProductCheckItemSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改成衣检测单-检测项目
     *
     * @param quaProductCheckItem 成衣检测单-检测项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaProductCheckItem(QuaProductCheckItem quaProductCheckItem) {
        QuaProductCheckItem response = quaProductCheckItemMapper.selectQuaProductCheckItemById(quaProductCheckItem.getProductCheckItemSid());
        int row = quaProductCheckItemMapper.updateById(quaProductCheckItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaProductCheckItem.getProductCheckItemSid(), BusinessType.UPDATE.ordinal(), response, quaProductCheckItem, TITLE);
        }
        return row;
    }

    /**
     * 变更成衣检测单-检测项目
     *
     * @param quaProductCheckItem 成衣检测单-检测项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaProductCheckItem(QuaProductCheckItem quaProductCheckItem) {
        QuaProductCheckItem response = quaProductCheckItemMapper.selectQuaProductCheckItemById(quaProductCheckItem.getProductCheckItemSid());
        int row = quaProductCheckItemMapper.updateAllById(quaProductCheckItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaProductCheckItem.getProductCheckItemSid(), BusinessType.CHANGE.ordinal(), response, quaProductCheckItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除成衣检测单-检测项目
     *
     * @param productCheckItemSids 需要删除的成衣检测单-检测项目ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaProductCheckItemByIds(List<Long> productCheckItemSids) {
        return quaProductCheckItemMapper.deleteBatchIds(productCheckItemSids);
    }

}
