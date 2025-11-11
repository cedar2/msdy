package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurOutsourcePriceInfor;
import com.platform.ems.domain.PurOutsourcePriceInforItem;

/**
 * 加工采购价格记录主(报价/核价/议价)Service接口
 *
 * @author linhongwei
 * @date 2022-04-01
 */
public interface IPurOutsourcePriceInforService extends IService<PurOutsourcePriceInfor> {
    /**
     * 查询加工采购价格记录主(报价/核价/议价)
     *
     * @param outsourcePriceInforSid 加工采购价格记录主(报价/核价/议价)ID
     * @return 加工采购价格记录主(报价 / 核价 / 议价)
     */
    public PurOutsourcePriceInfor selectPurOutsourcePriceInforById(Long outsourcePriceInforSid);

    /**
     * 查询加工采购价格记录主(报价/核价/议价)列表
     *
     * @param purOutsourcePriceInfor 加工采购价格记录主(报价/核价/议价)
     * @return 加工采购价格记录主(报价 / 核价 / 议价)集合
     */
    public List<PurOutsourcePriceInfor> selectPurOutsourcePriceInforList(PurOutsourcePriceInfor purOutsourcePriceInfor);

    /**
     * 新增加工采购价格记录主(报价/核价/议价)
     *
     * @param purOutsourcePriceInfor 加工采购价格记录主(报价/核价/议价)
     * @return 结果
     */
    public int insertPurOutsourcePriceInfor(PurOutsourcePriceInfor purOutsourcePriceInfor);

    /**
     * 修改加工采购价格记录主(报价/核价/议价)
     *
     * @param purOutsourcePriceInfor 加工采购价格记录主(报价/核价/议价)
     * @return 结果
     */
    public int updatePurOutsourcePriceInfor(PurOutsourcePriceInfor purOutsourcePriceInfor);

    /**
     * 变更加工采购价格记录主(报价/核价/议价)
     *
     * @param purOutsourcePriceInfor 加工采购价格记录主(报价/核价/议价)
     * @return 结果
     */
    public int changePurOutsourcePriceInfor(PurOutsourcePriceInfor purOutsourcePriceInfor);

    /**
     * 批量删除加工采购价格记录主(报价/核价/议价)
     *
     * @param outsourcePriceInforSids 需要删除的加工采购价格记录主(报价/核价/议价)ID
     * @return 结果
     */
    public int deletePurOutsourcePriceInforByIds(List<Long> outsourcePriceInforSids);

    /**
     * 修改加工采购价格记录明细或生成记录(报价/核价/议价)
     *
     * @param purOutsourcePriceInfor 加工采购价格记录主(报价/核价/议价)
     * @param purOutsourcePriceInforItem 加工采购价格记录明细(报价/核价/议价)
     * @return 结果
     */
    public int updatePriceInfor(PurOutsourcePriceInfor purOutsourcePriceInfor, PurOutsourcePriceInforItem purOutsourcePriceInforItem);

    /**
     * 修改加工采购价格记录明细或生成记录(报价/核价/议价)  全量更新
     *
     * @param purOutsourcePriceInfor 加工采购价格记录主(报价/核价/议价)
     * @param purOutsourcePriceInforItem 加工采购价格记录明细(报价/核价/议价)
     * @return 结果
     */
    public int updateAllPriceInfor(PurOutsourcePriceInfor purOutsourcePriceInfor, PurOutsourcePriceInforItem purOutsourcePriceInforItem);

}
