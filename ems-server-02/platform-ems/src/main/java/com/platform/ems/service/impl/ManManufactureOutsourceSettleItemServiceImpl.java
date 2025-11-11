package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.dto.response.form.ManOutsourceSettleStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.platform.ems.mapper.ManManufactureOutsourceSettleItemMapper;
import com.platform.ems.domain.ManManufactureOutsourceSettleItem;
import com.platform.ems.service.IManManufactureOutsourceSettleItemService;

/**
 * 外发加工费结算单明细报表Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-06-10
 */
@Service
@SuppressWarnings("all")
public class ManManufactureOutsourceSettleItemServiceImpl extends ServiceImpl<ManManufactureOutsourceSettleItemMapper,ManManufactureOutsourceSettleItem>  implements IManManufactureOutsourceSettleItemService {
    @Autowired
    private ManManufactureOutsourceSettleItemMapper manManufactureOutsourceSettleItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "外发加工费结算单明细报表";
    /**
     * 查询外发加工费结算单明细报表
     * 
     * @param manufactureOutsourceSettleItemSid 外发加工费结算单明细报表ID
     * @return 外发加工费结算单明细报表
     */
    @Override
    public ManManufactureOutsourceSettleItem selectManManufactureOutsourceSettleItemById(Long manufactureOutsourceSettleItemSid) {
        ManManufactureOutsourceSettleItem manManufactureOutsourceSettleItem = manManufactureOutsourceSettleItemMapper.selectManManufactureOutsourceSettleItemById(manufactureOutsourceSettleItemSid);
        return  manManufactureOutsourceSettleItem;
    }

    /**
     * 查询外发加工费结算单明细报表列表
     * 
     * @param manManufactureOutsourceSettleItem 外发加工费结算单明细报表
     * @return 外发加工费结算单明细报表
     */
    @Override
    public List<ManManufactureOutsourceSettleItem> selectManManufactureOutsourceSettleItemList(ManManufactureOutsourceSettleItem manManufactureOutsourceSettleItem) {
        manManufactureOutsourceSettleItem.setClientId(ApiThreadLocalUtil.get().getClientId());
        return manManufactureOutsourceSettleItemMapper.selectManManufactureOutsourceSettleItemList(manManufactureOutsourceSettleItem);
    }

    /**
     * 查询外发加工费结算单明细报表列表
     *
     * @param manManufactureOutsourceSettleItem 外发加工费结算单明细报表
     * @return 外发加工费结算单明细报表
     */
    @Override
    public List<ManManufactureOutsourceSettleItem> selectManManufactureOutsourceSettleItemForm(ManManufactureOutsourceSettleItem manManufactureOutsourceSettleItem) {
        return manManufactureOutsourceSettleItemMapper.selectManManufactureOutsourceSettleItemForm(manManufactureOutsourceSettleItem);
    }

    /**
     * 查询商品外加工费统计报表
     *
     * @param request 条件
     * @return 外发加工费结算单-明细集合
     */
    @Override
    public List<ManOutsourceSettleStatistics> selectManManufactureOutsourceSettleStatistics(ManOutsourceSettleStatistics request) {
        return manManufactureOutsourceSettleItemMapper.selectManManufactureOutsourceSettleStatistics(request);
    }
}
