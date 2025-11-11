package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.BasVendorTagItem;
import com.platform.ems.mapper.BasVendorTagItemMapper;
import com.platform.ems.service.IBasVendorTagItemService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 供应商标签(分组)明细Service业务层处理
 *
 * @author c
 * @date 2022-03-30
 */
@Service
@SuppressWarnings("all")
public class BasVendorTagItemServiceImpl extends ServiceImpl<BasVendorTagItemMapper, BasVendorTagItem> implements IBasVendorTagItemService {
    @Autowired
    private BasVendorTagItemMapper basVendorTagItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商标签(分组)明细";

    /**
     * 查询供应商标签(分组)明细
     *
     * @param vendorTagItemSid 供应商标签(分组)明细ID
     * @return 供应商标签(分组)明细
     */
    @Override
    public BasVendorTagItem selectBasVendorTagItemById(Long vendorTagItemSid) {
        BasVendorTagItem basVendorTagItem = basVendorTagItemMapper.selectBasVendorTagItemById(vendorTagItemSid);
        MongodbUtil.find(basVendorTagItem);
        return basVendorTagItem;
    }

    /**
     * 查询供应商标签(分组)明细列表
     *
     * @param basVendorTagItem 供应商标签(分组)明细
     * @return 供应商标签(分组)明细
     */
    @Override
    public List<BasVendorTagItem> selectBasVendorTagItemList(BasVendorTagItem basVendorTagItem) {
        return basVendorTagItemMapper.selectBasVendorTagItemList(basVendorTagItem);
    }

    /**
     * 新增供应商标签(分组)明细
     * 需要注意编码重复校验
     *
     * @param basVendorTagItem 供应商标签(分组)明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorTagItem(BasVendorTagItem basVendorTagItem) {
        int row = basVendorTagItemMapper.insert(basVendorTagItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(basVendorTagItem.getVendorTagItemSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商标签(分组)明细
     *
     * @param basVendorTagItem 供应商标签(分组)明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorTagItem(BasVendorTagItem basVendorTagItem) {
        BasVendorTagItem response = basVendorTagItemMapper.selectBasVendorTagItemById(basVendorTagItem.getVendorTagItemSid());
        int row = basVendorTagItemMapper.updateById(basVendorTagItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorTagItem.getVendorTagItemSid(), BusinessType.UPDATE.ordinal(), response, basVendorTagItem, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商标签(分组)明细
     *
     * @param basVendorTagItem 供应商标签(分组)明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorTagItem(BasVendorTagItem basVendorTagItem) {
        BasVendorTagItem response = basVendorTagItemMapper.selectBasVendorTagItemById(basVendorTagItem.getVendorTagItemSid());
        int row = basVendorTagItemMapper.updateAllById(basVendorTagItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorTagItem.getVendorTagItemSid(), BusinessType.CHANGE.ordinal(), response, basVendorTagItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商标签(分组)明细
     *
     * @param vendorTagItemSids 需要删除的供应商标签(分组)明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorTagItemByIds(List<Long> vendorTagItemSids) {
        return basVendorTagItemMapper.deleteBatchIds(vendorTagItemSids);
    }

}
