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
import com.platform.ems.mapper.PurQuoteBargainItemMapper;
import com.platform.ems.util.MongodbDeal;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.PurInquiryVendorMapper;
import com.platform.ems.service.IPurInquiryVendorService;

/**
 * 物料询价单-供应商Service业务层处理
 *
 * @author chenkw
 * @date 2022-03-21
 */
@Service
@SuppressWarnings("all")
public class PurInquiryVendorServiceImpl extends ServiceImpl<PurInquiryVendorMapper, PurInquiryVendor> implements IPurInquiryVendorService {
    @Autowired
    private PurInquiryVendorMapper purInquiryVendorMapper;
    @Autowired
    private PurQuoteBargainItemMapper purQuoteBargainItemMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "物料询价单-供应商";

    private static final String MAIN_TITLE = "物料询价单";

    /**
     * 查询物料询价单-供应商
     *
     * @param inquiryVendorSid 物料询价单-供应商ID
     * @return 物料询价单-供应商
     */
    @Override
    public PurInquiryVendor selectPurInquiryVendorById(Long inquiryVendorSid) {
        PurInquiryVendor purInquiryVendor = purInquiryVendorMapper.selectPurInquiryVendorById(inquiryVendorSid);
        MongodbUtil.find(purInquiryVendor);
        return purInquiryVendor;
    }

    /**
     * 查询物料询价单-供应商列表
     *
     * @param purInquiryVendor 物料询价单-供应商
     * @return 物料询价单-供应商
     */
    @Override
    public List<PurInquiryVendor> selectPurInquiryVendorList(PurInquiryVendor purInquiryVendor) {
        return purInquiryVendorMapper.selectPurInquiryVendorList(purInquiryVendor);
    }

    /**
     * 新增物料询价单-供应商
     * 需要注意编码重复校验
     *
     * @param purInquiryVendor 物料询价单-供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurInquiryVendor(PurInquiryVendor purInquiryVendor) {
        int row = purInquiryVendorMapper.insert(purInquiryVendor);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(purInquiryVendor.getInquiryVendorSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改物料询价单-供应商
     *
     * @param purInquiryVendor 物料询价单-供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurInquiryVendor(PurInquiryVendor purInquiryVendor) {
        PurInquiryVendor response = purInquiryVendorMapper.selectPurInquiryVendorById(purInquiryVendor.getInquiryVendorSid());
        int row = purInquiryVendorMapper.updateById(purInquiryVendor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purInquiryVendor.getInquiryVendorSid(), BusinessType.UPDATE.ordinal(), response, purInquiryVendor, TITLE);
        }
        return row;
    }

    /**
     * 变更物料询价单-供应商
     *
     * @param purInquiryVendor 物料询价单-供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurInquiryVendor(PurInquiryVendor purInquiryVendor) {
        PurInquiryVendor response = purInquiryVendorMapper.selectPurInquiryVendorById(purInquiryVendor.getInquiryVendorSid());
        int row = purInquiryVendorMapper.updateAllById(purInquiryVendor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purInquiryVendor.getInquiryVendorSid(), BusinessType.CHANGE.ordinal(), response, purInquiryVendor, TITLE);
        }
        return row;
    }

    /**
     * 批量删除物料询价单-供应商
     *
     * @param inquiryVendorSids 需要删除的物料询价单-供应商ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurInquiryVendorByIds(List<Long> inquiryVendorSids) {
        return purInquiryVendorMapper.deleteBatchIds(inquiryVendorSids);
    }

    /**
     * 查询物料询价单供应商明细
     *
     * @param inquirySid 物料询价单ID
     * @return 物料询价单明细
     */
    @Override
    public List<PurInquiryVendor> selectPurInquiryVendorListById(Long inquirySid) {
        List<PurInquiryVendor> list = purInquiryVendorMapper.selectPurInquiryVendorListById(inquirySid);
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
                List<PurQuoteBargainItem> quoteBargainItemList = purQuoteBargainItemMapper.selectPurRequestQuotationItemList(
                        new PurQuoteBargainItem().setVendorSid(vendor.getVendorSid()).setInquirySid(vendor.getInquirySid()));
                //如果在报价单里了
                if (CollectionUtil.isNotEmpty(quoteBargainItemList)){
                    //如果存在报价状态的报核议价单，那就是报价中
                    if (quoteBargainItemList.stream().allMatch(item-> ConstantsEms.SUBMIT_STATUS.equals(item.getHandleStatus()) || ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus()))){
                        vendor.setQuoteStatus(ConstantsPrice.QUOTE_STATUS_C_YBJ);
                    }
                    else if (quoteBargainItemList.stream().anyMatch(item-> ConstantsEms.SUBMIT_STATUS.equals(item.getHandleStatus()) || ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus()))){
                        vendor.setQuoteStatus(ConstantsPrice.QUOTE_STATUS_C_BJZ);
                    }else {
                        vendor.setQuoteStatus(ConstantsPrice.QUOTE_STATUS_C_WBJ);
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
     * 批量新增物料询价单供应商明细
     * 需要注意编码重复校验
     *
     * @param purInquiryItem 物料询价单供应商明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurInquiryVendorList(List<PurInquiryVendor> list, PurInquiry purInquiry) {
        if (CollectionUtil.isEmpty(list)) {
            return 0;
        }
        list.forEach(item -> {
            item.setClientId(ApiThreadLocalUtil.get().getClientId());
            item.setInquirySid(purInquiry.getInquirySid());
        });
        int row = purInquiryVendorMapper.inserts(list);
        if (row > 0) {
            list.forEach(item -> {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.insert(item.getInquiryVendorSid(), purInquiry.getHandleStatus(), msgList, TITLE, MAIN_TITLE + ":" + purInquiry.getInquirySid().toString());
            });
        }
        return row;
    }

    /**
     * 批量删除采购询价单供应商明细
     *
     * @param outsourceInquirySids 需要删除的采购询价单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurInquiryVendorByInquirySids(List<Long> inquirySids) {
        int row = purInquiryVendorMapper.deletePurInquiryVendorByInquirySids(inquirySids);
        if (row > 0) {
            inquirySids.forEach(sid -> {
                MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), null, TITLE, MAIN_TITLE + ":" + sid.toString());
            });
        }
        return row;
    }

    /**
     * 根据主表sid查询供应商明细sid列表
     *
     * @param purOutsourceInquirySids
     * @return
     */
    @Override
    public List<Long> selectPurInquiryVendorSidListById(Long[] purInquirySids) {
        return purInquiryVendorMapper.selectPurInquiryVendorSidListById(purInquirySids);
    }


    /**
     * 修改物料询价单供应商明细
     *
     * @param purInquiryItem 物料询价单供应商明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurInquiryVendorList(List<PurInquiryVendor> list, PurInquiry purInquiry) {
        int row = 0;
        for (PurInquiryVendor purInquiryVendor : list) {
            PurInquiryVendor response = purInquiryVendorMapper.selectPurInquiryVendorById(purInquiryVendor.getInquiryVendorSid());
            row = purInquiryVendorMapper.updateById(purInquiryVendor);
        }
        return row;
    }

}
