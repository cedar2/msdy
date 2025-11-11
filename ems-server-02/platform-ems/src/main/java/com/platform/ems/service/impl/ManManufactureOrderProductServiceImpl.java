package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsProcess;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.form.ManManuOrderProductTracking;
import com.platform.ems.domain.dto.response.form.ManManufactureOrderProductStatistics;
import com.platform.ems.mapper.BasMaterialSkuMapper;
import com.platform.ems.mapper.ManManufactureOrderProductMapper;
import com.platform.ems.service.IManManufactureOrderProductService;
import com.platform.ems.util.LightUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 生产订单-产品明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@Service
@SuppressWarnings("all")
public class ManManufactureOrderProductServiceImpl extends ServiceImpl<ManManufactureOrderProductMapper, ManManufactureOrderProduct> implements IManManufactureOrderProductService {
    @Autowired
    private ManManufactureOrderProductMapper manManufactureOrderProductMapper;
    @Autowired
    private BasMaterialSkuMapper basMaterialSkuMapper;

    /**
     * 查询生产订单-产品明细
     *
     * @param manufactureOrderProductSid 生产订单-产品明细ID
     * @return 生产订单-产品明细
     */
    @Override
    public ManManufactureOrderProduct selectManManufactureOrderProductById(Long manufactureOrderProductSid) {
        ManManufactureOrderProduct manManufactureOrderProduct = manManufactureOrderProductMapper.selectManManufactureOrderProductById(manufactureOrderProductSid);
        return manManufactureOrderProduct;
    }

    /**
     * 查询生产订单-产品明细列表
     *
     * @param manufactureOrderSidList 生产订单
     * @return 生产订单-产品明细集合
     */
    @Override
    public List<ManManufactureOrderProduct> selectManManufactureOrderProductListByOrderSid(List<Long> manufactureOrderSidList) {
        Long[] orderSidList = manufactureOrderSidList.stream().toArray(Long[]::new);
        List<ManManufactureOrderProduct> list = manManufactureOrderProductMapper.selectManManufactureOrderProductList(new ManManufactureOrderProduct().setManufactureOrderSidList(orderSidList));
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item->{
                item.setSalesOrderCode(null);
            });
        }
        return list;
    }

    /**
     * 查询生产订单-产品明细列表
     *
     * @param manManufactureOrderProduct 生产订单-产品明细
     * @return 生产订单-产品明细
     */
    @Override
    public List<ManManufactureOrderProduct> selectManManufactureOrderProductList(ManManufactureOrderProduct manManufactureOrderProduct) {
        List<ManManufactureOrderProduct> products = manManufactureOrderProductMapper.selectManManufactureOrderProductList(manManufactureOrderProduct);
        if (CollectionUtil.isNotEmpty(products)) {
            products.forEach(item->{
                if ("YWG".equals(item.getCompleteStatus())) {
                    item.setLight(LightUtil.LIGHT_BLUE); // 蓝灯
                }
                else {
                    LightUtil.setLight(item, item.getPlanEndDate(), null, item.getToexpireDaysScdd());
                }
            });
        }
        return products;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManManufactureOrderProduct(ManManufactureOrderProduct manManufactureOrderProduct) {
        Long[] productSids = manManufactureOrderProduct.getManufactureOrderProductSidList();
        if (ArrayUtil.isEmpty(productSids)) {
            throw new BaseException("参数错误");
        }
        ManManufactureOrderProduct orderProduct = new ManManufactureOrderProduct().setHandleStatus(ConstantsEms.CHECK_STATUS).setManufactureOrderProductSidList(productSids);
        List<ManManufactureOrderProduct> list =
                manManufactureOrderProductMapper.selectManManufactureOrderProductList(orderProduct);
        if (productSids.length != list.size()) {
            throw new BaseException("非已确认状态的生产订单，无法进行此操作！");
        }
        for (Long productSid : productSids) {
            manManufactureOrderProduct.setManufactureOrderProductSid(productSid);
            manManufactureOrderProductMapper.updateById(manManufactureOrderProduct);
        }
        return productSids.length;
    }

    /*
     * 设置计划投产日期
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setPlanStart(ManManufactureOrderProduct manManufactureOrderProduct) {
        if (manManufactureOrderProduct.getManufactureOrderProductSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<ManManufactureOrderProduct> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        if (manManufactureOrderProduct.getPlanStartDate() == null) {
            manManufactureOrderProduct.setPlanStartDate(null);
        }
        //计划投产日期
        updateWrapper.in(ManManufactureOrderProduct::getManufactureOrderProductSid, manManufactureOrderProduct.getManufactureOrderProductSidList())
                .set(ManManufactureOrderProduct::getPlanStartDate, manManufactureOrderProduct.getPlanStartDate());
        row = manManufactureOrderProductMapper.update(null, updateWrapper);
        return row;
    }

    /*
     * 设置计划完工日期
     */
    @Override
    public int setPlanEnd(ManManufactureOrderProduct manManufactureOrderProduct) {
        if (manManufactureOrderProduct.getManufactureOrderProductSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<ManManufactureOrderProduct> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        if (manManufactureOrderProduct.getPlanEndDate() == null) {
            manManufactureOrderProduct.setPlanEndDate(null);
        }
        //计划完工日期
        updateWrapper.in(ManManufactureOrderProduct::getManufactureOrderProductSid, manManufactureOrderProduct.getManufactureOrderProductSidList())
                .set(ManManufactureOrderProduct::getPlanEndDate, manManufactureOrderProduct.getPlanEndDate());
        row = manManufactureOrderProductMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 设置即将到期提醒天数
     * @param product
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setToexpireDays(ManManufactureOrderProduct product) {
        if (product.getManufactureOrderProductSidList().length == 0){
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<ManManufactureOrderProduct> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //即将到期天数
        updateWrapper.in(ManManufactureOrderProduct::getManufactureOrderProductSid, product.getManufactureOrderProductSidList());
        updateWrapper.set(ManManufactureOrderProduct::getToexpireDaysScddSp, product.getToexpireDaysScddSp());
        row = manManufactureOrderProductMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 生产进度跟踪报表（商品）
     *
     * @param request
     * @return 生产进度跟踪报表（商品）集合
     */
    @Override
    public List<ManManuOrderProductTracking> selectManufactureOrderProductTrackingList(ManManuOrderProductTracking request) {
        request.setClientId(ApiThreadLocalUtil.get().getClientId());
        if (ConstantsProcess.MAN_DOC_ANKU.equals(request.getDocumentType())) {
            return manManufactureOrderProductMapper.selectManufactureOrderProductTrackingListByKu(request);
        }
        else if (ConstantsProcess.MAN_DOC_ANDAN.equals(request.getDocumentType())) {
            return manManufactureOrderProductMapper.selectManufactureOrderProductTrackingListByDan(request);
        }
        return new ArrayList<>();
    }

    /**
     * 查询商品生产统计报表
     *
     * @param manManufactureOrderProduct 请求
     * @return 商品生产统计报表
     */
    @Override
    public List<ManManufactureOrderProductStatistics> selectManManufactureOrderProductStatistics(ManManufactureOrderProductStatistics manManufactureOrderProduct) {
        manManufactureOrderProduct.setClientId(ApiThreadLocalUtil.get().getClientId());
        return manManufactureOrderProductMapper.selectManManufactureOrderProductStatistics(manManufactureOrderProduct);
    }

    /**
     * 获取SKU颜色尺码双下拉框
     *
     * @param manManufactureOrderProduct 生产订单-产品明细
     * @return 获取SKU颜色尺码双下拉框
     */
    @Override
    public HashMap<String, List<BasSku>> getSkuList(ManManufactureOrderProduct manManufactureOrderProduct){
        HashMap<String, List<BasSku>> response = new HashMap<>();
        if (manManufactureOrderProduct.getMaterialSid() == null){
            return response;
        }
        List<BasSku> ysSkuList = new ArrayList<>();
        List<BasSku> cmSkuList = new ArrayList<>();
        // 仅现实生产订单中的尺码
        if (manManufactureOrderProduct.getManufactureOrderSid() != null){
            List<ManManufactureOrderProduct> productList = manManufactureOrderProductMapper.selectManManufactureOrderProductList(new ManManufactureOrderProduct()
                    .setManufactureOrderSid(manManufactureOrderProduct.getManufactureOrderSid())
                    .setMaterialSid(manManufactureOrderProduct.getMaterialSid()));
            if (CollectionUtil.isNotEmpty(productList)){
                BasSku sku = new BasSku();
                for (ManManufactureOrderProduct product : productList) {
                    if (product.getSku1Sid() != null){
                        sku = new BasSku();
                        sku.setSkuSid(product.getSku1Sid());
                        sku.setSkuCode(product.getSku1Code());
                        sku.setSkuName(product.getSku1Name());
                        sku.setSkuType(product.getSku1Type());
                        ysSkuList.add(sku);
                    }
                    if (product.getSku2Sid() != null){
                        sku = new BasSku();
                        sku.setSkuSid(product.getSku2Sid());
                        sku.setSkuCode(product.getSku2Code());
                        sku.setSkuName(product.getSku2Name());
                        sku.setSkuType(product.getSku2Type());
                        cmSkuList.add(sku);
                    }
                }
                if (CollectionUtil.isNotEmpty(ysSkuList)){
                    ysSkuList = ysSkuList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(BasSku::getSkuSid))), ArrayList::new));
                }
                if (CollectionUtil.isNotEmpty(cmSkuList)){
                    cmSkuList = cmSkuList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(BasSku::getSkuSid))), ArrayList::new));
                }
            }
        }
        // 找商品中的颜色尺码
        else {
            List<BasMaterialSku> materialSkuList = basMaterialSkuMapper.selectBasMaterialSkuList(new BasMaterialSku()
                    .setMaterialSid(manManufactureOrderProduct.getMaterialSid())
                    .setHandleStatus(ConstantsEms.CHECK_STATUS)
                    .setStatus(ConstantsEms.ENABLE_STATUS));
            if (CollectionUtil.isNotEmpty(materialSkuList)){
                BasSku sku = new BasSku();
                for (BasMaterialSku materialSku : materialSkuList) {
                    sku = new BasSku();
                    sku.setSkuSid(materialSku.getSkuSid());
                    sku.setSkuCode(materialSku.getSkuCode());
                    sku.setSkuName(materialSku.getSkuName());
                    sku.setSkuType(materialSku.getSku1Type());
                    if (ConstantsEms.SKUTYP_YS.equals(materialSku.getSkuType())){
                        ysSkuList.add(sku);
                    }
                    else if (ConstantsEms.SKUTYP_CM.equals(materialSku.getSkuType())){
                        cmSkuList.add(sku);
                    }
                }
            }
        }
        response.put(ConstantsEms.SKUTYP_YS, ysSkuList);
        response.put(ConstantsEms.SKUTYP_CM, cmSkuList);
        return response;
    }

}
