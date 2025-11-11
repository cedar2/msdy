package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsPrice;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.PurOutsourceQuoteBargainItemMapper;
import com.platform.ems.util.MongodbDeal;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.PurOutsourceInquiryVendorMapper;
import com.platform.ems.service.IPurOutsourceInquiryVendorService;

/**
 * 加工询价单-供应商Service业务层处理
 *
 * @author chenkw
 * @date 2022-03-21
 */
@Service
@SuppressWarnings("all")
public class PurOutsourceInquiryVendorServiceImpl extends ServiceImpl<PurOutsourceInquiryVendorMapper, PurOutsourceInquiryVendor> implements IPurOutsourceInquiryVendorService {
    @Autowired
    private PurOutsourceInquiryVendorMapper purOutsourceInquiryVendorMapper;
    @Autowired
    private PurOutsourceQuoteBargainItemMapper purOutsourceQuoteBargainItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "加工询价单-供应商";

    private static final String MAIN_TITLE = "加工询价单";

    /**
     * 查询加工询价单-供应商
     *
     * @param outsourceInquiryVendorSid 加工询价单-供应商ID
     * @return 加工询价单-供应商
     */
    @Override
    public PurOutsourceInquiryVendor selectPurOutsourceInquiryVendorById(Long outsourceInquiryVendorSid) {
        PurOutsourceInquiryVendor purOutsourceInquiryVendor = purOutsourceInquiryVendorMapper.selectPurOutsourceInquiryVendorById(outsourceInquiryVendorSid);
        MongodbUtil.find(purOutsourceInquiryVendor);
        return purOutsourceInquiryVendor;
    }

    /**
     * 查询加工询价单-供应商列表
     *
     * @param purOutsourceInquiryVendor 加工询价单-供应商
     * @return 加工询价单-供应商
     */
    @Override
    public List<PurOutsourceInquiryVendor> selectPurOutsourceInquiryVendorList(PurOutsourceInquiryVendor purOutsourceInquiryVendor) {
        return purOutsourceInquiryVendorMapper.selectPurOutsourceInquiryVendorList(purOutsourceInquiryVendor);
    }

    /**
     * 新增加工询价单-供应商
     * 需要注意编码重复校验
     *
     * @param purOutsourceInquiryVendor 加工询价单-供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurOutsourceInquiryVendor(PurOutsourceInquiryVendor purOutsourceInquiryVendor) {
        int row = purOutsourceInquiryVendorMapper.insert(purOutsourceInquiryVendor);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(purOutsourceInquiryVendor.getOutsourceInquiryVendorSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改加工询价单-供应商
     *
     * @param purOutsourceInquiryVendor 加工询价单-供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOutsourceInquiryVendor(PurOutsourceInquiryVendor purOutsourceInquiryVendor) {
        PurOutsourceInquiryVendor response = purOutsourceInquiryVendorMapper.selectPurOutsourceInquiryVendorById(purOutsourceInquiryVendor.getOutsourceInquiryVendorSid());
        int row = purOutsourceInquiryVendorMapper.updateById(purOutsourceInquiryVendor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purOutsourceInquiryVendor.getOutsourceInquiryVendorSid(), BusinessType.UPDATE.ordinal(), response, purOutsourceInquiryVendor, TITLE);
        }
        return row;
    }

    /**
     * 变更加工询价单-供应商
     *
     * @param purOutsourceInquiryVendor 加工询价单-供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurOutsourceInquiryVendor(PurOutsourceInquiryVendor purOutsourceInquiryVendor) {
        PurOutsourceInquiryVendor response = purOutsourceInquiryVendorMapper.selectPurOutsourceInquiryVendorById(purOutsourceInquiryVendor.getOutsourceInquiryVendorSid());
        int row = purOutsourceInquiryVendorMapper.updateAllById(purOutsourceInquiryVendor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purOutsourceInquiryVendor.getOutsourceInquiryVendorSid(), BusinessType.CHANGE.ordinal(), response, purOutsourceInquiryVendor, TITLE);
        }
        return row;
    }

    /**
     * 批量删除加工询价单-供应商
     *
     * @param outsourceInquiryVendorSids 需要删除的加工询价单-供应商ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurOutsourceInquiryVendorByIds(List<Long> outsourceInquiryVendorSids) {
        return purOutsourceInquiryVendorMapper.deleteBatchIds(outsourceInquiryVendorSids);
    }

    /**
     * 查询加工询价单供应商明细
     *
     * @param inquirySid 物料询价单ID
     * @return 物料询价单明细
     */
    @Override
    public List<PurOutsourceInquiryVendor> selectPurOutsourceInquiryVendorListById(Long OutsourceInquirySid) {
        List<PurOutsourceInquiryVendor> list = purOutsourceInquiryVendorMapper.selectPurOutsourceInquiryVendorListById(OutsourceInquirySid);
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())){
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() != null){
                list = list.stream().filter(o->ApiThreadLocalUtil.get().getSysUser().getVendorSid().equals(o.getVendorSid())).collect(Collectors.toList());
            }
            else {
                list.clear();
            }
        }
        if (CollectionUtil.isNotEmpty(list)){
            list.forEach(vendor->{
                List<PurOutsourceQuoteBargainItem> outsourcequoteBargainItemList = purOutsourceQuoteBargainItemMapper.selectPurOutsourceRequestQuotationItemList(
                        new PurOutsourceQuoteBargainItem().setVendorSid(vendor.getVendorSid()).setOutsourceInquirySid(vendor.getOutsourceInquirySid()));
                //如果在报价单里了
                if (CollectionUtil.isNotEmpty(outsourcequoteBargainItemList)){
                    //如果存在报价状态的报核议价单，那就是报价中
                    if (outsourcequoteBargainItemList.stream().allMatch(item-> !ConstantsPrice.BAOHEYI_STAGE_BJ.equals(item.getCurrentStage()))){
                        vendor.setQuoteStatus(ConstantsPrice.QUOTE_STATUS_C_YBJ);
                    }
                    else if (outsourcequoteBargainItemList.stream().allMatch(item-> ConstantsPrice.BAOHEYI_STAGE_BJ.equals(item.getCurrentStage()))){
                        vendor.setQuoteStatus(ConstantsPrice.QUOTE_STATUS_C_WBJ);
                    }else {
                        vendor.setQuoteStatus(ConstantsPrice.QUOTE_STATUS_C_BJZ);
                    }
                }else {
                    vendor.setQuoteStatus(ConstantsPrice.QUOTE_STATUS_C_WBJ);
                }
                MongodbUtil.find(vendor);
            });
        }
        return list;
    }

    /**
     * 批量新增加工询价单供应商明细
     * 需要注意编码重复校验
     *
     * @param PurOutsourceInquiryVendor 加工询价单供应商明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurOutsourceInquiryVendorList(List<PurOutsourceInquiryVendor> list, PurOutsourceInquiry purOutsourceInquiry) {
        if (CollectionUtil.isEmpty(list)) {
            return 0;
        }
        list.forEach(item -> {
            item.setClientId(ApiThreadLocalUtil.get().getClientId());
            item.setOutsourceInquirySid(purOutsourceInquiry.getOutsourceInquirySid());
        });
        int row = purOutsourceInquiryVendorMapper.inserts(list);
        if (row > 0) {
            list.forEach(item -> {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.insert(item.getOutsourceInquiryVendorSid(), purOutsourceInquiry.getHandleStatus(), msgList, TITLE,
                        MAIN_TITLE + ":" + purOutsourceInquiry.getOutsourceInquirySid().toString());
            });
        }
        return row;
    }

    /**
     * 批量删除加工询价单供应商明细
     *
     * @param outsourceInquirySids 需要删除的加工询价单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurOutsourceInquiryVendorByInquirySids(List<Long> outsourceInquirySids) {
        int row = purOutsourceInquiryVendorMapper.deletePurOutsourceInquiryVendorByInquirySids(outsourceInquirySids);
        if (row > 0) {
            outsourceInquirySids.forEach(sid -> {
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), null, TITLE, MAIN_TITLE + ":" + sid.toString());
            });
        }
        return row;
    }

    /**
     * 根据主表sid查询供应商明细sid列表
     *
     * @param purOutsourceInquiryItem
     * @return
     */
    @Override
    public List<Long> selectPurOutsourceInquiryVendorSidListById(Long[] purOutsourceInquirySids) {
        return purOutsourceInquiryVendorMapper.selectPurOutsourceInquiryVendorSidListById(purOutsourceInquirySids);
    }

    /**
     * 修改加工询价单供应商明细
     *
     * @param purOutsourceInquiryItem 加工询价单明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOutsourceInquiryVendorList(List<PurOutsourceInquiryVendor> list, PurOutsourceInquiry purOutsourceInquiry) {
        int row = 0;
        for (PurOutsourceInquiryVendor purOutsourceInquiryVendor : list) {
            row = purOutsourceInquiryVendorMapper.updateById(purOutsourceInquiryVendor);
        }
        return row;
    }

}
