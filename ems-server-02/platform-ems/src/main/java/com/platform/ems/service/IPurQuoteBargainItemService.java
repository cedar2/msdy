package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurQuoteBargainItem;

/**
 * 报核议价单明细(报价/核价/议价)Service接口
 * 
 * @author linhongwei
 * @date 2021-04-26
 */
public interface IPurQuoteBargainItemService extends IService<PurQuoteBargainItem>{
    /**
     * 查询报核议价单明细(报价/核价/议价)
     * 
     * @param requestQuotationItemSid 报核议价单明细(报价/核价/议价)ID
     * @return 报核议价单明细(报价/核价/议价)
     */
    public PurQuoteBargainItem selectPurRequestQuotationItemById(Long requestQuotationItemSid);

    /**
     * 查询报核议价单明细(报价/核价/议价)列表
     * 
     * @param purQuoteBargainItem 报核议价单明细(报价/核价/议价)
     * @return 报核议价单明细(报价/核价/议价)集合
     */
    public List<PurQuoteBargainItem> selectPurRequestQuotationItemList(PurQuoteBargainItem purQuoteBargainItem);

    /**
     * 新增报核议价单明细(报价/核价/议价)
     * 
     * @param purQuoteBargainItem 报核议价单明细(报价/核价/议价)
     * @return 结果
     */
    public int insertPurRequestQuotationItem(PurQuoteBargainItem purQuoteBargainItem);

    /**
     * 修改报核议价单明细(报价/核价/议价) 提交、流转
     * 
     * @param purQuoteBargainItem 报核议价单明细(报价/核价/议价)
     * @return 结果
     */
    public int updatePurRequestQuotationItem(PurQuoteBargainItem purQuoteBargainItem);

    /**
     * 批量删除报核议价单明细(报价/核价/议价)
     * 
     * @param requestQuotationItemSids 需要删除的报核议价单明细(报价/核价/议价)ID
     * @return 结果
     */
    public int deletePurRequestQuotationItemByIds(List<Long> requestQuotationItemSids);

    /**
     * 检查明细的相关价格金额有没有填写完整
     *
     * @param purQuoteBargainItem 报核议价单明细(报价/核价/议价)
     * @return 报核议价单明细(报价/核价/议价)集合
     */
    public void checkPrice(PurQuoteBargainItem purQuoteBargainItem);

    /**
     * 计算出不含税价
     *
     * @param purQuoteBargainItem 报核议价单明细(报价/核价/议价)
     * @return 报核议价单明细(报价/核价/议价)集合
     */
    public void calculatePriceTax(PurQuoteBargainItem purQuoteBargainItem);

}
