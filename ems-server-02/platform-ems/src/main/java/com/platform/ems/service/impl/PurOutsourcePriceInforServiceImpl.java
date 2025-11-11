package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.PurOutsourcePriceInforItem;
import com.platform.ems.mapper.PurOutsourcePriceInforItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.PurOutsourcePriceInforMapper;
import com.platform.ems.domain.PurOutsourcePriceInfor;
import com.platform.ems.service.IPurOutsourcePriceInforService;

/**
 * 加工采购价格记录主(报价/核价/议价)Service业务层处理
 *
 * @author chenkw
 * @date 2022-04-01
 */
@Service
@SuppressWarnings("all")
public class PurOutsourcePriceInforServiceImpl extends ServiceImpl<PurOutsourcePriceInforMapper, PurOutsourcePriceInfor> implements IPurOutsourcePriceInforService {
    @Autowired
    private PurOutsourcePriceInforMapper purOutsourcePriceInforMapper;
    @Autowired
    private PurOutsourcePriceInforItemMapper purOutsourcePriceInforItemMapper;

    /**
     * 查询加工采购价格记录主(报价/核价/议价)
     *
     * @param outsourcePriceInforSid 加工采购价格记录主(报价/核价/议价)ID
     * @return 加工采购价格记录主(报价 / 核价 / 议价)
     */
    @Override
    public PurOutsourcePriceInfor selectPurOutsourcePriceInforById(Long outsourcePriceInforSid) {
        PurOutsourcePriceInfor purOutsourcePriceInfor = purOutsourcePriceInforMapper.selectPurOutsourcePriceInforById(outsourcePriceInforSid);
        List<PurOutsourcePriceInforItem> purOutsourcePriceInforItemList = purOutsourcePriceInforItemMapper.selectPurOutsourcePriceInforItemList(
                new PurOutsourcePriceInforItem().setOutsourcePriceInforSid(outsourcePriceInforSid));
        purOutsourcePriceInfor.setPurOutsourcePriceInforItemList(purOutsourcePriceInforItemList);
        return purOutsourcePriceInfor;
    }

    /**
     * 查询加工采购价格记录主(报价/核价/议价)列表
     *
     * @param purOutsourcePriceInfor 加工采购价格记录主(报价/核价/议价)
     * @return 加工采购价格记录主(报价 / 核价 / 议价)
     */
    @Override
    public List<PurOutsourcePriceInfor> selectPurOutsourcePriceInforList(PurOutsourcePriceInfor purOutsourcePriceInfor) {
        return purOutsourcePriceInforMapper.selectPurOutsourcePriceInforList(purOutsourcePriceInfor);
    }

    /**
     * 新增加工采购价格记录主(报价/核价/议价)
     * 需要注意编码重复校验
     *
     * @param purOutsourcePriceInfor 加工采购价格记录主(报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurOutsourcePriceInfor(PurOutsourcePriceInfor purOutsourcePriceInfor) {
        int row = purOutsourcePriceInforMapper.insert(purOutsourcePriceInfor);
        if (row > 0) {
            insertItem(purOutsourcePriceInfor);
        }
        return row;
    }

    /**
     * 修改加工采购价格记录主(报价/核价/议价)
     *
     * @param purOutsourcePriceInfor 加工采购价格记录主(报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOutsourcePriceInfor(PurOutsourcePriceInfor purOutsourcePriceInfor) {
        int row = purOutsourcePriceInforMapper.updateById(purOutsourcePriceInfor);
        if (row > 0) {
            purOutsourcePriceInforItemMapper.delete(new QueryWrapper<PurOutsourcePriceInforItem>().lambda()
                    .eq(PurOutsourcePriceInforItem::getOutsourcePriceInforSid,purOutsourcePriceInfor.getOutsourcePriceInforSid()));
            insertItem(purOutsourcePriceInfor);
        }
        return row;
    }

    /**
     * 变更加工采购价格记录主(报价/核价/议价)
     *
     * @param purOutsourcePriceInfor 加工采购价格记录主(报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurOutsourcePriceInfor(PurOutsourcePriceInfor purOutsourcePriceInfor) {
        int row = purOutsourcePriceInforMapper.updateAllById(purOutsourcePriceInfor);
        if (row > 0) {
            purOutsourcePriceInforItemMapper.delete(new QueryWrapper<PurOutsourcePriceInforItem>().lambda()
                    .eq(PurOutsourcePriceInforItem::getOutsourcePriceInforSid,purOutsourcePriceInfor.getOutsourcePriceInforSid()));
            insertItem(purOutsourcePriceInfor);
        }
        return row;
    }

    /**
     * 批量删除加工采购价格记录主(报价/核价/议价)
     *
     * @param outsourcePriceInforSids 需要删除的加工采购价格记录主(报价/核价/议价)ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurOutsourcePriceInforByIds(List<Long> outsourcePriceInforSids) {
        int row = purOutsourcePriceInforMapper.deleteBatchIds(outsourcePriceInforSids);
        purOutsourcePriceInforItemMapper.delete(new QueryWrapper<PurOutsourcePriceInforItem>().lambda()
                .in(PurOutsourcePriceInforItem::getOutsourcePriceInforSid,outsourcePriceInforSids));
        return row;
    }

    /**
     * 批量插入加工采购价格记录明细表(报价/核价/议价)
     *
     * @param purOutsourcePriceInfor 需要删除的加工采购价格记录主(报价/核价/议价)
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int insertItem(PurOutsourcePriceInfor purOutsourcePriceInfor){
        int row = 0;
        if (CollectionUtil.isNotEmpty(purOutsourcePriceInfor.getPurOutsourcePriceInforItemList())){
            purOutsourcePriceInfor.getPurOutsourcePriceInforItemList().forEach(item->{
                item.setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN);
                item.setOutsourcePriceInforSid(purOutsourcePriceInfor.getOutsourcePriceInforSid());
            });
            row = purOutsourcePriceInforItemMapper.inserts(purOutsourcePriceInfor.getPurOutsourcePriceInforItemList());
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
    public int updatePriceInfor(PurOutsourcePriceInfor request,PurOutsourcePriceInforItem requestItem) {
        int row = 0;
        //查询是否存在各条件满足的记录主表
        //按款：(因为如果sku1Sid为空时，sql查询会是： sku1_sid = null，就查询不了数据)
        QueryWrapper<PurOutsourcePriceInfor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(PurOutsourcePriceInfor::getVendorSid,request.getVendorSid())
                .eq(PurOutsourcePriceInfor::getMaterialSid,request.getMaterialSid())
                .eq(PurOutsourcePriceInfor::getProcessSid,request.getProcessSid())
                .eq(PurOutsourcePriceInfor::getPriceDimension,request.getPriceDimension());
        if (request.getSku1Sid() != null){
            queryWrapper.lambda().eq(PurOutsourcePriceInfor::getSku1Sid,request.getSku1Sid());
        }
        List<PurOutsourcePriceInfor> response = purOutsourcePriceInforMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(response)){
            //更新价格
            response.forEach(info->{
                PurOutsourcePriceInforItem item = null;
                try {
                    item = purOutsourcePriceInforItemMapper.selectOne(new QueryWrapper<PurOutsourcePriceInforItem>().lambda()
                            .eq(PurOutsourcePriceInforItem::getOutsourcePriceInforSid,info.getOutsourcePriceInforSid()));
                }catch (Exception e){
                    throw new CustomException("旧数据异常，请联系管理员");
                }
                if (item != null){
                    requestItem.setOutsourcePriceInforItemSid(item.getOutsourcePriceInforItemSid());
                    purOutsourcePriceInforItemMapper.updateById(requestItem);
                }
            });
            row = response.size();
        }else {
            //生成记录
            request.setPurOutsourcePriceInforItemList(new ArrayList<PurOutsourcePriceInforItem>(){
                {
                    add(requestItem);
                }
            });
            row = insertPurOutsourcePriceInfor(request);
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
    public int updateAllPriceInfor(PurOutsourcePriceInfor request,PurOutsourcePriceInforItem requestItem) {
        int row = 0;
        //查询是否存在各条件满足的记录主表
        //按款：(因为如果sku1Sid为空时，sql查询会是： sku1_sid = null，就查询不了数据)
        QueryWrapper<PurOutsourcePriceInfor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(PurOutsourcePriceInfor::getVendorSid,request.getVendorSid())
                .eq(PurOutsourcePriceInfor::getMaterialSid,request.getMaterialSid())
                .eq(PurOutsourcePriceInfor::getProcessSid,request.getProcessSid())
                .eq(PurOutsourcePriceInfor::getPriceDimension,request.getPriceDimension());
        if (request.getSku1Sid() != null){
            queryWrapper.lambda().eq(PurOutsourcePriceInfor::getSku1Sid,request.getSku1Sid());
        }
        List<PurOutsourcePriceInfor> response = purOutsourcePriceInforMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(response)){
            //更新价格
            response.forEach(info->{
                PurOutsourcePriceInforItem item = null;
                try {
                    item = purOutsourcePriceInforItemMapper.selectOne(new QueryWrapper<PurOutsourcePriceInforItem>().lambda()
                            .eq(PurOutsourcePriceInforItem::getOutsourcePriceInforSid,info.getOutsourcePriceInforSid()));
                }catch (Exception e){
                    throw new CustomException("旧数据异常，请联系管理员");
                }
                if (item != null){
                    requestItem.setOutsourcePriceInforItemSid(item.getOutsourcePriceInforItemSid());
                    purOutsourcePriceInforItemMapper.updateAllById(requestItem);
                }
            });
            row = response.size();
        }else {
            //生成记录
            request.setPurOutsourcePriceInforItemList(new ArrayList<PurOutsourcePriceInforItem>(){
                {
                    add(requestItem);
                }
            });
            row = insertPurOutsourcePriceInfor(request);
        }
        return row;
    }

}
