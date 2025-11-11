package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureOutsourceSettleItem;
import com.platform.ems.domain.dto.response.form.ManOutsourceSettleStatistics;

/**
 * 外发加工费结算单-明细Service接口
 * 
 * @author linhongwei
 * @date 2021-06-10
 */
public interface IManManufactureOutsourceSettleItemService extends IService<ManManufactureOutsourceSettleItem>{
    /**
     * 查询外发加工费结算单-明细
     * 
     * @param manufactureOutsourceSettleItemSid 外发加工费结算单-明细ID
     * @return 外发加工费结算单-明细
     */
    public ManManufactureOutsourceSettleItem selectManManufactureOutsourceSettleItemById(Long manufactureOutsourceSettleItemSid);

    /**
     * 查询外发加工费结算单-明细列表
     * 
     * @param manManufactureOutsourceSettleItem 外发加工费结算单-明细
     * @return 外发加工费结算单-明细集合
     */
    public List<ManManufactureOutsourceSettleItem> selectManManufactureOutsourceSettleItemList(ManManufactureOutsourceSettleItem manManufactureOutsourceSettleItem);

    /**
     * 查询外发加工费结算单-明细列表
     *
     * @param manManufactureOutsourceSettleItem 外发加工费结算单-明细
     * @return 外发加工费结算单-明细集合
     */
    public List<ManManufactureOutsourceSettleItem> selectManManufactureOutsourceSettleItemForm(ManManufactureOutsourceSettleItem manManufactureOutsourceSettleItem);

    /**
     * 查询商品外加工费统计报表
     *
     * @param request 条件
     * @return 外发加工费结算单-明细集合
     */
    public List<ManOutsourceSettleStatistics> selectManManufactureOutsourceSettleStatistics(ManOutsourceSettleStatistics request);

}
