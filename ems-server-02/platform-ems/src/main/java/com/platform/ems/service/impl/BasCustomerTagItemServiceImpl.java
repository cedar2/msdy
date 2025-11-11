package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.BasCustomerTagItem;
import com.platform.ems.mapper.BasCustomerTagItemMapper;
import com.platform.ems.service.IBasCustomerTagItemService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户标签(分组)明细Service业务层处理
 *
 * @author c
 * @date 2022-03-30
 */
@Service
@SuppressWarnings("all")
public class BasCustomerTagItemServiceImpl extends ServiceImpl<BasCustomerTagItemMapper, BasCustomerTagItem> implements IBasCustomerTagItemService {
    @Autowired
    private BasCustomerTagItemMapper basCustomerTagItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "客户标签(分组)明细";

    /**
     * 查询客户标签(分组)明细
     *
     * @param customerTagItemSid 客户标签(分组)明细ID
     * @return 客户标签(分组)明细
     */
    @Override
    public BasCustomerTagItem selectBasCustomerTagItemById(Long customerTagItemSid) {
        BasCustomerTagItem basCustomerTagItem = basCustomerTagItemMapper.selectBasCustomerTagItemById(customerTagItemSid);
        MongodbUtil.find(basCustomerTagItem);
        return basCustomerTagItem;
    }

    /**
     * 查询客户标签(分组)明细列表
     *
     * @param basCustomerTagItem 客户标签(分组)明细
     * @return 客户标签(分组)明细
     */
    @Override
    public List<BasCustomerTagItem> selectBasCustomerTagItemList(BasCustomerTagItem basCustomerTagItem) {
        return basCustomerTagItemMapper.selectBasCustomerTagItemList(basCustomerTagItem);
    }

    /**
     * 新增客户标签(分组)明细
     * 需要注意编码重复校验
     *
     * @param basCustomerTagItem 客户标签(分组)明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasCustomerTagItem(BasCustomerTagItem basCustomerTagItem) {
        int row = basCustomerTagItemMapper.insert(basCustomerTagItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(basCustomerTagItem.getCustomerTagItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改客户标签(分组)明细
     *
     * @param basCustomerTagItem 客户标签(分组)明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasCustomerTagItem(BasCustomerTagItem basCustomerTagItem) {
        BasCustomerTagItem response = basCustomerTagItemMapper.selectBasCustomerTagItemById(basCustomerTagItem.getCustomerTagItemSid());
        int row = basCustomerTagItemMapper.updateById(basCustomerTagItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basCustomerTagItem.getCustomerTagItemSid(), BusinessType.UPDATE.getValue(), response, basCustomerTagItem, TITLE);
        }
        return row;
    }

    /**
     * 变更客户标签(分组)明细
     *
     * @param basCustomerTagItem 客户标签(分组)明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasCustomerTagItem(BasCustomerTagItem basCustomerTagItem) {
        BasCustomerTagItem response = basCustomerTagItemMapper.selectBasCustomerTagItemById(basCustomerTagItem.getCustomerTagItemSid());
        int row = basCustomerTagItemMapper.updateAllById(basCustomerTagItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basCustomerTagItem.getCustomerTagItemSid(), BusinessType.CHANGE.getValue(), response, basCustomerTagItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户标签(分组)明细
     *
     * @param customerTagItemSids 需要删除的客户标签(分组)明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasCustomerTagItemByIds(List<Long> customerTagItemSids) {
        return basCustomerTagItemMapper.deleteBatchIds(customerTagItemSids);
    }

}
