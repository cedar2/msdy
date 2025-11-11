package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsPrice;
import com.platform.ems.domain.PurQuoteBargain;
import com.platform.ems.mapper.PurQuoteBargainMapper;
import com.platform.ems.service.IPurQuoteBargainService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.PurQuoteBargainItemMapper;
import com.platform.ems.domain.PurQuoteBargainItem;
import com.platform.ems.service.IPurQuoteBargainItemService;

/**
 * 报议价单明细(报价/核价/议价)Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-26
 */
@Service
@SuppressWarnings("all")
public class PurQuoteBargainItemServiceImpl extends ServiceImpl<PurQuoteBargainItemMapper, PurQuoteBargainItem> implements IPurQuoteBargainItemService {
    @Autowired
    private PurQuoteBargainItemMapper purQuoteBargainItemMapper;
    @Autowired
    private PurQuoteBargainMapper purQuoteBargainMapper;
    @Autowired
    private IPurQuoteBargainService purQuoteBargainService;

    /**
     * 查询报议价单明细(报价/核价/议价)
     *
     * @param outsourceQuoteBargainItemSid 报议价单明细(报价/核价/议价)ID
     * @return 报议价单明细(报价 / 核价 / 议价)
     */
    @Override
    public PurQuoteBargainItem selectPurRequestQuotationItemById(Long outsourceQuoteBargainItemSid) {
        return purQuoteBargainItemMapper.selectPurRequestQuotationItemByItemId(outsourceQuoteBargainItemSid);
    }

    /**
     * 查询报议价单明细(报价/核价/议价)列表
     *
     * @param purQuoteBargainItem 报议价单明细(报价/核价/议价)
     * @return 报议价单明细(报价 / 核价 / 议价)
     */
    @Override
    public List<PurQuoteBargainItem> selectPurRequestQuotationItemList(PurQuoteBargainItem purQuoteBargainItem) {
        return purQuoteBargainItemMapper.selectPurRequestQuotationItemList(purQuoteBargainItem);
    }

    /**
     * 新增报议价单明细(报价/核价/议价)
     * 需要注意编码重复校验
     *
     * @param purQuoteBargainItem 报议价单明细(报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurRequestQuotationItem(PurQuoteBargainItem purQuoteBargainItem) {
        return purQuoteBargainItemMapper.insert(purQuoteBargainItem);
    }

    /**
     * 校验明细表中的价格是否填写正确
     *
     * @param purQuoteBargainItem 报议价单明细(报价/核价/议价)
     * @return 结果
     */
    @Override
    public void checkPrice(PurQuoteBargainItem purQuoteBargainItem) {
        if (purQuoteBargainItem.getHandleStatus() == null && purQuoteBargainItem.getQuoteBargainItemSid() != null){
            purQuoteBargainItem = purQuoteBargainItemMapper.selectById(purQuoteBargainItem.getQuoteBargainItemSid());
        }
        if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(purQuoteBargainItem.getCurrentStage())) {
            if (purQuoteBargainItem.getQuotePriceTax() == null) {
                throw new CustomException("报价未填写");
            }
            if (ConstantsEms.YES.equals(purQuoteBargainItem.getIsRecursionPrice())) {
                if (purQuoteBargainItem.getIncreQuoPriceTax() == null) {
                    throw new CustomException("递增报价未填写");
                }
                if (purQuoteBargainItem.getDecreQuoPriceTax() == null) {
                    throw new CustomException("递减报价未填写");
                }
            }
        }
        if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(purQuoteBargainItem.getCurrentStage())) {
            if (purQuoteBargainItem.getCheckPriceTax() == null) {
                throw new CustomException("核定价未填写");
            }
            if (ConstantsEms.YES.equals(purQuoteBargainItem.getIsRecursionPrice())) {
                if (purQuoteBargainItem.getIncreChePriceTax() == null) {
                    throw new CustomException("递增核定价未填写");
                }
                if (purQuoteBargainItem.getDecreChePriceTax() == null) {
                    throw new CustomException("递减核定价未填写");
                }
            }
        }
        if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(purQuoteBargainItem.getCurrentStage())) {
            if (purQuoteBargainItem.getPurchasePriceTax() == null) {
                throw new CustomException("采购价未填写");
            }
            if (ConstantsEms.YES.equals(purQuoteBargainItem.getIsRecursionPrice())) {
                if (purQuoteBargainItem.getIncrePurPriceTax() == null) {
                    throw new CustomException("递增采购价未填写");
                }
                if (purQuoteBargainItem.getDecrePurPriceTax() == null) {
                    throw new CustomException("递减采购价未填写");
                }
            }
        }
    }

    /**
     * 设置明细表中的价格更新时间
     *
     * @param purQuoteBargainItem 报议价单明细(报价/核价/议价)
     * @return 结果
     */
    private void setUpdateDate(PurQuoteBargainItem purQuoteBargainItem) {
        if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(purQuoteBargainItem.getCurrentStage())) {
            purQuoteBargainItem.setQuoteUpdateDate(new Date());
            purQuoteBargainItem.setQuoteUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(purQuoteBargainItem.getCurrentStage())) {
            purQuoteBargainItem.setCheckUpdateDate(new Date());
            purQuoteBargainItem.setCheckUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(purQuoteBargainItem.getCurrentStage())) {
            purQuoteBargainItem.setPurchaseUpdateDate(new Date());
            purQuoteBargainItem.setPurchaseUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
    }

    /**
     * 不含税价计算公式
     * @author chenkw
     * @param purQuoteBargainItem
     * @return 结果 不含税价
     */
    public BigDecimal formula(BigDecimal priceTax, BigDecimal taxRate){
        if (priceTax == null){
            return null;
        }
        BigDecimal notTax = priceTax.divide(BigDecimal.ONE.add(taxRate==null?BigDecimal.ONE:taxRate),6,BigDecimal.ROUND_HALF_UP);
        return notTax;

    }

    /**
     * 计算不含税价
     * @author chenkw
     * @param purQuoteBargainItem
     * @return 结果
     */
    public void calculatePriceTax(PurQuoteBargainItem purQuoteBargainItem){
        if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(purQuoteBargainItem.getCurrentStage())){
            //得到报价不含税金额
            purQuoteBargainItem.setQuotePrice(formula(purQuoteBargainItem.getQuotePriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到递增报价不含税金额
            purQuoteBargainItem.setIncreQuoPrice(formula(purQuoteBargainItem.getIncreQuoPriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到递减报价不含税金额
            purQuoteBargainItem.setDecreQuoPrice(formula(purQuoteBargainItem.getDecreQuoPriceTax(),purQuoteBargainItem.getTaxRate()));
        }
        if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(purQuoteBargainItem.getCurrentStage())){
            //得到核价不含税金额
            purQuoteBargainItem.setCheckPrice(formula(purQuoteBargainItem.getCheckPriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到递增核定价不含税金额
            purQuoteBargainItem.setIncreChePrice(formula(purQuoteBargainItem.getIncreChePriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到递减核定价不含税金额
            purQuoteBargainItem.setDecreChePrice(formula(purQuoteBargainItem.getDecreChePriceTax(),purQuoteBargainItem.getTaxRate()));
        }
        if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(purQuoteBargainItem.getCurrentStage())){
            //得到报价不含税金额
            purQuoteBargainItem.setQuotePrice(formula(purQuoteBargainItem.getQuotePriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到递增报价不含税金额
            purQuoteBargainItem.setIncreQuoPrice(formula(purQuoteBargainItem.getIncreQuoPriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到递减报价不含税金额
            purQuoteBargainItem.setDecreQuoPrice(formula(purQuoteBargainItem.getDecreQuoPriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到核价不含税金额
            purQuoteBargainItem.setCheckPrice(formula(purQuoteBargainItem.getCheckPriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到递增核定价不含税金额
            purQuoteBargainItem.setIncreChePrice(formula(purQuoteBargainItem.getIncreChePriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到递减核定价不含税金额
            purQuoteBargainItem.setDecreChePrice(formula(purQuoteBargainItem.getDecreChePriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到确认价不含税金额
            purQuoteBargainItem.setConfirmPrice(formula(purQuoteBargainItem.getConfirmPriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到递增确认价不含税金额
            purQuoteBargainItem.setIncreConfPrice(formula(purQuoteBargainItem.getIncreConfPriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到递减确认价不含税金额
            purQuoteBargainItem.setDecreConfPrice(formula(purQuoteBargainItem.getDecreConfPriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到采购价不含税金额
            purQuoteBargainItem.setPurchasePrice(formula(purQuoteBargainItem.getPurchasePriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到递增采购价不含税金额
            purQuoteBargainItem.setIncrePurPrice(formula(purQuoteBargainItem.getIncrePurPriceTax(),purQuoteBargainItem.getTaxRate()));
            //得到递减采购价不含税金额
            purQuoteBargainItem.setDecrePurPrice(formula(purQuoteBargainItem.getDecrePurPriceTax(),purQuoteBargainItem.getTaxRate()));
        }
    }

    /**
     * 修改报议价单明细(报价/核价/议价)
     *
     * @param purQuoteBargainItem 报议价单明细(报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurRequestQuotationItem(PurQuoteBargainItem purQuoteBargainItem) {
        purQuoteBargainItem.setConfirmPriceTax(purQuoteBargainItem.getPurchasePriceTax())
                .setIncreConfPriceTax(purQuoteBargainItem.getIncrePurPriceTax())
                .setDecreConfPriceTax(purQuoteBargainItem.getDecrePurPriceTax());
        checkPrice(purQuoteBargainItem);
        setUpdateDate(purQuoteBargainItem);
        calculatePriceTax(purQuoteBargainItem);
        //因为核价员字段是存储在主表中的
        LambdaUpdateWrapper<PurQuoteBargain> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PurQuoteBargain::getQuoteBargainSid,purQuoteBargainItem.getQuoteBargainSid());
        //如果是核价就更新主表的核价日期。
        if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(purQuoteBargainItem.getCurrentStage())){
            updateWrapper.set(PurQuoteBargain::getDateCheck,purQuoteBargainItem.getDateCheck());
            updateWrapper.set(PurQuoteBargain::getChecker,purQuoteBargainItem.getChecker());
            purQuoteBargainMapper.update(null, updateWrapper);
            MongodbUtil.insertUserLogItem(purQuoteBargainItem.getQuoteBargainSid(), BusinessType.PRICE.getValue(),"采购核价单", purQuoteBargainItem.getItemNum(),"核价更新");
        }
        if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(purQuoteBargainItem.getCurrentStage())){
            //涉及到主表需要修改的字段
            updateWrapper.set(PurQuoteBargain::getDateConfirm,purQuoteBargainItem.getDateConfirm());
            updateWrapper.set(PurQuoteBargain::getStartDate,purQuoteBargainItem.getStartDate());
            updateWrapper.set(PurQuoteBargain::getEndDate,purQuoteBargainItem.getEndDate());
            purQuoteBargainMapper.update(null, updateWrapper);
            MongodbUtil.insertUserLogItem(purQuoteBargainItem.getQuoteBargainSid(), BusinessType.PRICE.getValue(),"采购议价单", purQuoteBargainItem.getItemNum(),"议价更新");
        }
        purQuoteBargainItem.setUpdateDate(null).setUpdaterAccount(null);
        return purQuoteBargainItemMapper.updateAllById(purQuoteBargainItem);
    }

    /**
     * 批量删除报议价单明细(报价/核价/议价)
     *
     * @param outsourceQuoteBargainItemSids 需要删除的报议价单明细(报价/核价/议价)ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurRequestQuotationItemByIds(List<Long> outsourceQuoteBargainItemSids) {
        return purQuoteBargainItemMapper.deleteBatchIds(outsourceQuoteBargainItemSids);
    }

}
