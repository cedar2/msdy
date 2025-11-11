package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.PurPriceInforItem;
import com.platform.ems.mapper.BasMaterialMapper;
import com.platform.ems.mapper.PurPriceInforItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.PurPriceInforMapper;
import com.platform.ems.domain.PurPriceInfor;
import com.platform.ems.service.IPurPriceInforService;

/**
 * 采购价格记录主(报价/核价/议价)Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-26
 */
@Service
@SuppressWarnings("all")
public class PurPriceInforServiceImpl extends ServiceImpl<PurPriceInforMapper,PurPriceInfor>  implements IPurPriceInforService {
    @Autowired
    private PurPriceInforMapper purPriceInforMapper;
    @Autowired
    private PurPriceInforItemMapper purPriceInforItemMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;

    /**
     * 查询采购价格记录主(报价/核价/议价)
     *
     * @param priceInforSid 采购价格记录主(报价/核价/议价)ID
     * @return 采购价格记录主(报价/核价/议价)
     */
    @Override
    public PurPriceInfor selectPurPriceInforById(Long priceInforSid) {
        return purPriceInforMapper.selectPurPriceInforById(priceInforSid);
    }

    /**
     * 查询采购价格记录主(报价/核价/议价)列表
     *
     * @param purPriceInfor 采购价格记录主(报价/核价/议价)
     * @return 采购价格记录主(报价/核价/议价)
     */
    @Override
    public List<PurPriceInfor> selectPurPriceInforList(PurPriceInfor purPriceInfor) {
        return purPriceInforMapper.selectPurPriceInforList(purPriceInfor);
    }

    /**
     * 新增采购价格记录主(报价/核价/议价)
     * 需要注意编码重复校验
     * @param purPriceInfor 采购价格记录主(报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurPriceInfor(PurPriceInfor purPriceInfor) {
        int row = 0;
        row = purPriceInforMapper.insert(purPriceInfor);
        if (row > 0) {
            insertItem(purPriceInfor);
        }
        return row;
    }

    /**
     * 修改采购价格记录主(报价/核价/议价)
     *
     * @param purPriceInfor 采购价格记录主(报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurPriceInfor(PurPriceInfor purPriceInfor) {
        return purPriceInforMapper.updateById(purPriceInfor);
    }

    /**
     * 批量删除采购价格记录主(报价/核价/议价)
     *
     * @param priceInforSids 需要删除的采购价格记录主(报价/核价/议价)ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurPriceInforByIds(List<Long> priceInforSids) {
        return purPriceInforMapper.deleteBatchIds(priceInforSids);
    }

    /**
     * 批量插入采购价格记录明细表(报价/核价/议价)
     *
     * @param purPriceInfor 需要删除的采购价格记录主(报价/核价/议价)
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int insertItem(PurPriceInfor purPriceInfor){
        int row = 0;
        if (CollectionUtil.isNotEmpty(purPriceInfor.getPurPriceInforItemList())){
            purPriceInfor.getPurPriceInforItemList().forEach(item->{
                item.setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN);
                item.setPriceInforSid(purPriceInfor.getPriceInforSid());
            });
            row = purPriceInforItemMapper.inserts(purPriceInfor.getPurPriceInforItemList());
        }
        return row;
    }

    /**
     * 修改加工采购价格记录明细或生成记录(报价/核价/议价)
     *
     * @param request 加工采购价格记录主(报价/核价/议价)
     * @param requestItem 加工采购价格记录明细(报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePriceInfor(PurPriceInfor request, PurPriceInforItem requestItem) {
        int row = 0;
        //查询是否存在各条件满足的记录主表
        QueryWrapper<PurPriceInfor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PurPriceInfor::getVendorSid,request.getVendorSid())
                .eq(PurPriceInfor::getMaterialSid,request.getMaterialSid())
                .eq(PurPriceInfor::getRawMaterialMode,request.getRawMaterialMode())
                .eq(PurPriceInfor::getPurchaseMode,request.getPurchaseMode())
                .eq(PurPriceInfor::getPriceDimension,request.getPriceDimension());
        if (request.getSku1Sid() != null){
            queryWrapper.lambda().eq(PurPriceInfor::getSku1Sid,request.getSku1Sid());
        }
        List<PurPriceInfor> response = purPriceInforMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(response)){
            //更新价格
            response.forEach(info->{
                PurPriceInforItem item = null;
                try {
                    item = purPriceInforItemMapper.selectOne(new QueryWrapper<PurPriceInforItem>().lambda()
                            .eq(PurPriceInforItem::getPriceInforSid,info.getPriceInforSid()));
                }catch (Exception e){
                    throw new CustomException("旧数据异常，请联系管理员");
                }
                if (item != null){
                    requestItem.setPriceInforItemSid(item.getPriceInforItemSid());
                    purPriceInforItemMapper.updateById(requestItem);
                }
            });
            row = response.size();
        }else {
            //生成记录
            request.setPurPriceInforItemList(new ArrayList<PurPriceInforItem>(){
                {
                    add(requestItem);
                }
            });
            row = insertPurPriceInfor(request);
        }
        return row;
    }

    /**
     * 修改加工采购价格记录明细或生成记录(报价/核价/议价) 全量更新
     *
     * @param request 加工采购价格记录主(报价/核价/议价)
     * @param requestItem 加工采购价格记录明细(报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAllPriceInfor(PurPriceInfor request, PurPriceInforItem requestItem) {
        int row = 0;
        //查询是否存在各条件满足的记录主表
        QueryWrapper<PurPriceInfor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PurPriceInfor::getVendorSid,request.getVendorSid())
                .eq(PurPriceInfor::getMaterialSid,request.getMaterialSid())
                .eq(PurPriceInfor::getRawMaterialMode,request.getRawMaterialMode())
                .eq(PurPriceInfor::getPurchaseMode,request.getPurchaseMode())
                .eq(PurPriceInfor::getPriceDimension,request.getPriceDimension());
        if (request.getSku1Sid() != null){
            queryWrapper.lambda().eq(PurPriceInfor::getSku1Sid,request.getSku1Sid());
        }
        List<PurPriceInfor> response = purPriceInforMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(response)){
            //更新价格
            response.forEach(info->{
                PurPriceInforItem item = null;
                try {
                    item = purPriceInforItemMapper.selectOne(new QueryWrapper<PurPriceInforItem>().lambda()
                            .eq(PurPriceInforItem::getPriceInforSid,info.getPriceInforSid()));
                }catch (Exception e){
                    throw new CustomException("旧数据异常，请联系管理员");
                }
                if (item != null){
                    requestItem.setPriceInforItemSid(item.getPriceInforItemSid());
                    purPriceInforItemMapper.updateAllById(requestItem);
                    //回写物料当前报价
                    BasMaterial material = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>()
                            .lambda().eq(BasMaterial::getVendorSid, request.getVendorSid())
                            .eq(BasMaterial::getMaterialSid, request.getMaterialSid())
                            .eq(BasMaterial::getRawMaterialMode, request.getRawMaterialMode())
                            .eq(BasMaterial::getPriceDimension, request.getPriceDimension()));
                    if (material != null && material.getQuotePriceTax().compareTo(requestItem.getQuotePriceTax()) != 0){
                        basMaterialMapper.updateAllById(material.setUnitPrice(requestItem.getUnitPrice()).setQuotePriceTax(requestItem.getQuotePriceTax())
                                .setUnitConversionRatePrice(requestItem.getUnitConversionRate()));
                    }
                }
            });
            row = response.size();
        }else {
            //生成记录
            request.setPurPriceInforItemList(new ArrayList<PurPriceInforItem>(){
                {
                    add(requestItem);
                }
            });
            row = insertPurPriceInfor(request);
        }
        return row;
    }



}
