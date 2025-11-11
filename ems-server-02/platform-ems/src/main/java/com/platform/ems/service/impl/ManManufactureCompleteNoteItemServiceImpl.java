package com.platform.ems.service.impl;

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
import com.platform.ems.mapper.ManManufactureCompleteNoteItemMapper;
import com.platform.ems.domain.ManManufactureCompleteNoteItem;
import com.platform.ems.service.IManManufactureCompleteNoteItemService;

/**
 * 生产完工确认单-明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@Service
@SuppressWarnings("all")
public class ManManufactureCompleteNoteItemServiceImpl extends ServiceImpl<ManManufactureCompleteNoteItemMapper, ManManufactureCompleteNoteItem> implements IManManufactureCompleteNoteItemService {
    @Autowired
    private ManManufactureCompleteNoteItemMapper manManufactureCompleteNoteItemMapper;

    /**
     * 查询生产完工确认单-明细
     *
     * @param manufactureCompleteNoteItemSid 生产完工确认单-明细ID
     * @return 生产完工确认单-明细
     */
    @Override
    public ManManufactureCompleteNoteItem selectManManufactureCompleteNoteItemById(Long manufactureCompleteNoteItemSid) {
        ManManufactureCompleteNoteItem manManufactureCompleteNoteItem = manManufactureCompleteNoteItemMapper.selectManManufactureCompleteNoteItemById(manufactureCompleteNoteItemSid);
        return manManufactureCompleteNoteItem;
    }

    /**
     * 查询生产完工确认单-明细列表
     *
     * @param manManufactureCompleteNoteItem 生产完工确认单-明细
     * @return 生产完工确认单-明细
     */
    @Override
    public List<ManManufactureCompleteNoteItem> selectManManufactureCompleteNoteItemList(ManManufactureCompleteNoteItem manManufactureCompleteNoteItem) {
        return manManufactureCompleteNoteItemMapper.selectManManufactureCompleteNoteItemList(manManufactureCompleteNoteItem);
    }


}
