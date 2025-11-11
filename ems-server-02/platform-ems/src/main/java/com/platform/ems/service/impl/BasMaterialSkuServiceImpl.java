package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IBasMaterialService;
import com.platform.ems.service.IBasMaterialSkuService;
import com.platform.ems.service.IConBarcodeRangeConfigService;
import com.platform.ems.service.ISystemDictDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 物料&商品-SKU明细Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-12
 */
@Service
public class BasMaterialSkuServiceImpl implements IBasMaterialSkuService {
    @Autowired
    private BasMaterialSkuMapper basMaterialSkuMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private IBasMaterialService basMaterialService;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private BasSkuGroupItemMapper basSkuGroupItemMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private SalSalesOrderItemMapper salSalesOrderItemMapper;
    @Autowired
    private PurPurchaseOrderItemMapper purPurchaseOrderItemMapper;
    @Autowired
    private TecBomItemMapper tecBomItemMapper;
    @Autowired
    private TecBomHeadMapper tecBomHeadMapper;

    @Autowired
    private IConBarcodeRangeConfigService barcodeRangeConfigService;

    /**
     * 查询物料&商品-SKU明细
     *
     * @param materialSkuSid 物料&商品-SKU明细ID
     * @return 物料&商品-SKU明细
     */
    @Override
    public BasMaterialSku selectBasMaterialSkuById(String materialSkuSid) {
        return basMaterialSkuMapper.selectBasMaterialSkuById(materialSkuSid);
    }

    /**
     * 查询物料&商品-SKU明细列表
     *
     * @param basMaterialSku 物料&商品-SKU明细
     * @return 物料&商品-SKU明细
     */
    @Override
    public List<BasMaterialSku> selectBasMaterialSkuList(BasMaterialSku basMaterialSku) {
        return basMaterialSkuMapper.selectBasMaterialSkuList(basMaterialSku);
    }

    /**
     * 按 款色 查询
     *
     * @param basMaterialSku 物料&商品
     * @return 物料&商品-SKU明细集合
     */
    @Override
    public List<BasMaterial> selectBasMaterialSku1List(BasMaterialSku basMaterialSku) {
        return basMaterialSkuMapper.selectBasMaterialSku1List(basMaterialSku);
    }

    /**
     * 新增物料&商品-SKU明细
     *
     * @param basMaterialSku 物料&商品-SKU明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasMaterialSku(BasMaterialSku basMaterialSku) {
        return basMaterialSkuMapper.insert(basMaterialSku);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasMaterialSkuList(Long materialSid, List<BasMaterialSku> basMaterialSkuList) {
        // 区分一下是否要新建还是启用
        List<BasMaterialSku> needStatusMaterialSkuList = basMaterialSkuList.stream().filter(o->o.getMaterialSkuSid()!=null).collect(Collectors.toList());
        for (BasMaterialSku item : needStatusMaterialSkuList) {
            basMaterialSkuMapper.update(null, new UpdateWrapper<BasMaterialSku>().lambda()
                    .eq(BasMaterialSku::getMaterialSkuSid, item.getMaterialSkuSid())
                    .set(BasMaterialSku::getStatus, ConstantsEms.ENABLE_STATUS));
            if (ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus())) {
                if (ConstantsEms.SKUTYP_CM.equals(item.getSku1Type())) {
                    basMaterialBarcodeMapper.update(null, new UpdateWrapper<BasMaterialBarcode>().lambda()
                            .set(BasMaterialBarcode::getStatus, ConstantsEms.ENABLE_STATUS)
                            .eq(BasMaterialBarcode::getMaterialSid, item.getMaterialSid())
                            .eq(BasMaterialBarcode::getSku1Sid, item.getSkuSid()));
                }
                else if (ConstantsEms.SKUTYP_CM.equals(item.getSku2Type())) {
                    basMaterialBarcodeMapper.update(null, new UpdateWrapper<BasMaterialBarcode>().lambda()
                            .set(BasMaterialBarcode::getStatus, ConstantsEms.ENABLE_STATUS)
                            .eq(BasMaterialBarcode::getMaterialSid, item.getMaterialSid())
                            .eq(BasMaterialBarcode::getSku2Sid, item.getSkuSid()));
                }

            }
        }
        // 新增的
        basMaterialSkuList = basMaterialSkuList.stream().filter(o->o.getMaterialSkuSid()==null).collect(Collectors.toList());
        // 当前系统中的
        List<BasMaterialSku> itemList = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda()
                .eq(BasMaterialSku::getMaterialSid,materialSid).orderByDesc(BasMaterialSku::getItemNum));
        int maxNum = 0;
        if (CollectionUtil.isNotEmpty(itemList)){
            if ((Integer)itemList.get(0).getItemNum() != null){
                maxNum = itemList.get(0).getItemNum();
            }
        }
        for (BasMaterialSku materialSku : basMaterialSkuList) {
            materialSku.setItemNum(++maxNum);
        }
        int row = basMaterialSkuMapper.inserts(basMaterialSkuList);
        BasMaterial material = basMaterialMapper.selectById(materialSid);
        if (ConstantsEms.CHECK_STATUS.equals(material.getHandleStatus())) {
            List<BasMaterialBarcode> barcodeList = new ArrayList<>();
            for (BasMaterialSku materialSku : basMaterialSkuList) {
                if (material.getSku1Type().equals(materialSku.getSkuType())) {
                    List<BasMaterialSku> newnewList = itemList.stream().filter(o->!o.getSkuType().equals(material.getSku1Type())).collect(Collectors.toList());
                    for (BasMaterialSku item : newnewList) {
                        Long nextCode = barcodeRangeConfigService.nextId();
                        BasMaterialBarcode barcode = new BasMaterialBarcode();
                        barcode.setMaterialSid(materialSid).setBarcode(nextCode.toString())
                                .setSku1Code(materialSku.getSkuCode()).setSku2Code(item.getSkuCode())
                                .setSku1Type(materialSku.getSkuType()).setSku2Type(item.getSkuType())
                                .setSku1Sid(materialSku.getSkuSid()).setSku2Sid(item.getSkuSid());
                        if (ConstantsEms.DISENABLE_STATUS.equals(material.getStatus())) {
                            barcode.setStatus(material.getStatus());
                        } else {
                            barcode.setStatus(item.getStatus());
                        }
                        barcodeList.add(barcode);
                    }
                }
                else if (material.getSku2Type().equals(materialSku.getSkuType())) {
                    List<BasMaterialSku> newnewList = itemList.stream().filter(o->!o.getSkuType().equals(material.getSku2Type())).collect(Collectors.toList());
                    for (BasMaterialSku item : newnewList) {
                        Long nextCode = barcodeRangeConfigService.nextId();
                        BasMaterialBarcode barcode = new BasMaterialBarcode();
                        barcode.setMaterialSid(materialSid).setBarcode(nextCode.toString())
                                .setSku1Code(item.getSkuCode()).setSku2Code(materialSku.getSkuCode())
                                .setSku1Type(item.getSkuType()).setSku2Type(materialSku.getSkuType())
                                .setSku1Sid(item.getSkuSid()).setSku2Sid(materialSku.getSkuSid());
                        if (ConstantsEms.DISENABLE_STATUS.equals(material.getStatus())) {
                            barcode.setStatus(material.getStatus());
                        } else {
                            barcode.setStatus(item.getStatus());
                        }
                        barcodeList.add(barcode);
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(barcodeList)) {
                basMaterialBarcodeMapper.inserts(barcodeList);
            }
        }
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HashMap<String, Object> status(BasMaterialSku basMaterialSku){
        Long[] materialSkuSidList = basMaterialSku.getMaterialSkuSidList();
        if (ArrayUtil.isEmpty(materialSkuSidList)){
            return null;
        }
        int row = 0;
        HashMap<String, Object> res = new HashMap<>();
        List<BasMaterialSku> list = basMaterialSkuMapper.selectBasMaterialSkuList(new BasMaterialSku()
                .setMaterialSkuSidList(materialSkuSidList));
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        // 启用成功的提示信息
        String message = "";
        boolean flag = false;
        boolean flag1 = false;
        if (ConstantsEms.ENABLE_STATUS.equals(basMaterialSku.getStatus())){
            for (BasMaterialSku materialSku : list) {
                if (materialSku.getSkuType().equals(ConstantsEms.SKUTYP_YS) && (
                        materialSku.getMaterialCategory().equals(ConstantsEms.MATERIAL_CATEGORY_SP) ||
                                materialSku.getMaterialCategory().equals(ConstantsEms.MATERIAL_CATEGORY_YP))) {
                    String code = materialSku.getMaterialCode() == null ? "" : materialSku.getMaterialCode() + ";";
                    message = message + code;
                    flag1 = true;
                    try {
                        basMaterialService.checkBomApproval(materialSku.getMaterialSid());
                    } catch (BaseException e) {
                        errMsg = new CommonErrMsgResponse();
                        if (materialSku.getMaterialCategory().equals(ConstantsEms.MATERIAL_CATEGORY_SP)) {
                            errMsg.setMsg("商品" + materialSku.getMaterialName() + "的BOM正在审批中，不允许启用颜色，请先将商品的BOM驳回");
                        }
                        else if (materialSku.getMaterialCategory().equals(ConstantsEms.MATERIAL_CATEGORY_YP)) {
                            errMsg.setMsg("样品" + materialSku.getSampleCodeSelf() + "的BOM正在审批中，不允许启用颜色，请先将商品的BOM驳回");
                        }
                        errMsgList.add(errMsg);
                    }
                }

            }
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                res.put("errList",errMsgList);
                return res;
            }
            else {
                row = basMaterialSkuMapper.update(null,new UpdateWrapper<BasMaterialSku>().lambda()
                        .set(BasMaterialSku::getStatus,ConstantsEms.ENABLE_STATUS).in(BasMaterialSku::getMaterialSkuSid,materialSkuSidList));
                if (flag1) {
                    message = "请及时更新商品"  + message + "新启用颜色的BOM信息";
                    flag = true;
                }
                //处理商品条码
                list.forEach(item->{
                    if (item.getMaterialStatus().equals(ConstantsEms.ENABLE_STATUS)) {
                        basMaterialBarcodeMapper.update(null,new UpdateWrapper<BasMaterialBarcode>().lambda()
                                .set(BasMaterialBarcode::getStatus,ConstantsEms.ENABLE_STATUS).eq(BasMaterialBarcode::getMaterialSid,item.getMaterialSid())
                                .and(wrapper -> wrapper.eq(BasMaterialBarcode::getSku1Sid,item.getSkuSid()).or().eq(BasMaterialBarcode::getSku2Sid,item.getSkuSid())));
                    }
                });
            }
        }
        if (ConstantsEms.DISENABLE_STATUS.equals(basMaterialSku.getStatus())){
            for (BasMaterialSku materialSku : list) {
                if (materialSku.getSkuType().equals(ConstantsEms.SKUTYP_YS) && (
                        materialSku.getMaterialCategory().equals(ConstantsEms.MATERIAL_CATEGORY_SP) ||
                                materialSku.getMaterialCategory().equals(ConstantsEms.MATERIAL_CATEGORY_YP))) {
                    try {
                        basMaterialService.checkBomApproval(materialSku.getMaterialSid());
                    } catch (BaseException e) {
                        errMsg = new CommonErrMsgResponse();
                        if (materialSku.getMaterialCategory().equals(ConstantsEms.MATERIAL_CATEGORY_SP)) {
                            errMsg.setMsg("商品" + materialSku.getMaterialName() + "的BOM正在审批中，不允许启用颜色，请先将商品的BOM驳回");
                        }
                        else if (materialSku.getMaterialCategory().equals(ConstantsEms.MATERIAL_CATEGORY_YP)) {
                            errMsg.setMsg("样品" + materialSku.getSampleCodeSelf() + "的BOM正在审批中，不允许启用颜色，请先将商品的BOM驳回");
                        }
                        errMsgList.add(errMsg);
                    }
                }

            }
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                res.put("errList",errMsgList);
                return res;
            }
            else {
                row = basMaterialSkuMapper.update(null,new UpdateWrapper<BasMaterialSku>().lambda()
                        .set(BasMaterialSku::getStatus,ConstantsEms.DISENABLE_STATUS).in(BasMaterialSku::getMaterialSkuSid,materialSkuSidList));
                //处理商品条码
                list.forEach(item->{
                    basMaterialBarcodeMapper.update(null,new UpdateWrapper<BasMaterialBarcode>().lambda()
                            .set(BasMaterialBarcode::getStatus,ConstantsEms.DISENABLE_STATUS).eq(BasMaterialBarcode::getMaterialSid,item.getMaterialSid())
                            .and(wrapper -> wrapper.eq(BasMaterialBarcode::getSku1Sid,item.getSkuSid()).or().eq(BasMaterialBarcode::getSku2Sid,item.getSkuSid())));
                });
            }
        }
        res.put("success",row);
        res.put("message",null);
        if (flag) {
            res.put("message",message);
        }
        return res;
    }

    /**
     * 物料商品sku明细报表启用停用
     * @param basMaterialSku
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity changeStatus(BasMaterialSku basMaterialSku) {
        Long[] sids = basMaterialSku.getMaterialSkuSidList();
        List<CommonErrMsgResponse> responseList = new ArrayList<>();
        if (sids != null && sids.length > 0) {
            // 获取数据
            List<BasMaterialSku> materialSkuList = basMaterialSkuMapper.selectBasMaterialSkuList
                    (new BasMaterialSku().setMaterialSkuSidList(sids).setHandleStatus(ConstantsEms.CHECK_STATUS));
            if (CollectionUtil.isNotEmpty(materialSkuList) && sids.length != materialSkuList.size()) {
                throw new BaseException("物料/商品档案为已确认状态才可点击");
            }
            /**
             * 1、新增按钮：启用
             * 仅物料/商品档案本身为已确认状态可点击，支持多选，进行如下判断：
             * 1）如所选择行的“物料类别”是“商品“或”样品”，SKU类型为“颜色”，且对应的BOM处于审批中状态
             * 报错：商品/样品XXX的BOM正在审批中，不允许启用颜色，请先将商品的BOM驳回（若为商品，XXX为商品编码；
             * 若为样品，XXX为我司样衣号）
             * 否则，将所选择行的“启用/停用”改为：启用，
             * 然后判断物料/商品档案本身的启停状态：
             * 若物料/商品档案本身的启停状态为“启用”，同时将对应的商品SKU条码的启用/停用状态设置为：启用；
             * 若物料/商品档案本身的启停状态为“停用”，则不做其它处理
             */
            if (CollectionUtil.isNotEmpty(materialSkuList) && StrUtil.isNotBlank(basMaterialSku.getStatus())) {
                for (BasMaterialSku materialSku : materialSkuList) {
                    if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(materialSku.getMaterialCategory())
                            || ConstantsEms.MATERIAL_CATEGORY_YP.equals(materialSku.getMaterialCategory())) {
                        if (ConstantsEms.SKUTYP_YS.equals(materialSku.getSkuType())) {
                            List<TecBomHead> bomHeadList = tecBomHeadMapper.selectList(new QueryWrapper<TecBomHead>()
                                    .lambda().eq(TecBomHead::getMaterialSid, materialSku.getMaterialSid())
                                    .in(TecBomHead::getHandleStatus,
                                            new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()}));
                            if (CollectionUtil.isNotEmpty(bomHeadList)) {
                                String name = "商品";
                                if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(materialSku.getMaterialCategory())) {
                                    name = "样品";
                                }
                                String status = "启用";
                                if (ConstantsEms.DISENABLE_STATUS.equals(basMaterialSku.getStatus())) {
                                    status = "停用";
                                }
                                String code = materialSku.getMaterialCode();
                                if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(materialSku.getMaterialCategory())) {
                                    code = materialSku.getSampleCodeSelf();
                                }
                                String title = name + code + "的BOM正在审批中，不允许"
                                        + status + "颜色，请先将" + name+ "的BOM驳回";
                                responseList.add(new CommonErrMsgResponse().setMsg(title));
                            }
                        }
                    }
                }
                if (CollectionUtil.isEmpty(responseList)) {
                    for (BasMaterialSku materialSku : materialSkuList) {
                        if (ConstantsEms.ENABLE_STATUS.equals(basMaterialSku.getStatus())) {
                            if (materialSku.getMaterialStatus().equals(ConstantsEms.ENABLE_STATUS)) {
                                LambdaUpdateWrapper<BasMaterialBarcode> barcodeWrapper = new LambdaUpdateWrapper<>();
                                barcodeWrapper.eq(BasMaterialBarcode::getMaterialSid, materialSku.getMaterialSid())
                                        .eq(BasMaterialBarcode::getSku1Sid, materialSku.getSkuSid())
                                        .set(BasMaterialBarcode::getStatus, ConstantsEms.ENABLE_STATUS);
                                basMaterialBarcodeMapper.update(null, barcodeWrapper);
                            }
                        }
                        else {
                            LambdaUpdateWrapper<BasMaterialBarcode> barcodeWrapper = new LambdaUpdateWrapper<>();
                            barcodeWrapper.eq(BasMaterialBarcode::getMaterialSid, materialSku.getMaterialSid())
                                    .eq(BasMaterialBarcode::getSku1Sid, materialSku.getSkuSid())
                                    .set(BasMaterialBarcode::getStatus, ConstantsEms.DISENABLE_STATUS);
                            basMaterialBarcodeMapper.update(null, barcodeWrapper);
                        }
                    }
                }
                else {
                    return EmsResultEntity.error(responseList);
                }
            }
        }
        return EmsResultEntity.success();
    }

    private List<CommonErrMsgResponse> verifyInventory(List<BasMaterialSku> materialSkuList){
        if (CollectionUtil.isEmpty(materialSkuList)){
            return new ArrayList<>();
        }
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        for (BasMaterialSku item : materialSkuList) {
            List<InvInventoryLocation> invInventoryLocationList = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>().lambda()
                    .eq(InvInventoryLocation::getMaterialSid, item.getMaterialSid())
                    .and(wrapper -> wrapper.eq(InvInventoryLocation::getSku1Sid,item.getSkuSid()).or().eq(InvInventoryLocation::getSku2Sid,item.getSkuSid()))
                    .and(wrapper -> wrapper.ne(InvInventoryLocation::getVendorConsignQuantity, BigDecimal.ZERO)
                            .or().ne(InvInventoryLocation::getVendorSubcontractQuantity, BigDecimal.ZERO)
                            .or().ne(InvInventoryLocation::getCustomerConsignQuantity, BigDecimal.ZERO)
                            .or().ne(InvInventoryLocation::getCustomerSubcontractQuantity, BigDecimal.ZERO)
                            .or().ne(InvInventoryLocation::getUnlimitedQuantity, BigDecimal.ZERO)));
            if (CollectionUtil.isNotEmpty(invInventoryLocationList)){
                errMsg = new CommonErrMsgResponse();
                errMsg.setMsg(item.getMaterialCode() + "的SKU明细 " + item.getSkuName() + " 已存在库存，无法停用！");
                errMsgList.add(errMsg);
            }
        }
        return errMsgList;
    }

    private List<CommonErrMsgResponse> verifyOrder(List<BasMaterialSku> materialSkuList){
        if (CollectionUtil.isEmpty(materialSkuList)){
            return new ArrayList<>();
        }
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        String[] ORDER_STATUS = {HandleStatus.SAVE.getCode(),HandleStatus.CONFIRMED.getCode(),HandleStatus.RETURNED.getCode(),HandleStatus.SUBMIT.getCode()
                ,HandleStatus.CHANGEAPPROVAL.getCode()};
        for (BasMaterialSku item : materialSkuList) {
            List<Long> salOrderItemSidList = salSalesOrderItemMapper.judgeOrderAndMaterial(new SalSalesOrderItem().setHandleStatusList(ORDER_STATUS)
                    .setMaterialSid(item.getMaterialSid()).setSkuSid(item.getSkuSid()));
            if (CollectionUtil.isNotEmpty(salOrderItemSidList)){
                errMsg = new CommonErrMsgResponse();
                errMsg.setMsg(item.getMaterialCode() + "的SKU明细 " + item.getSkuName() + " 已被销售订单引用，无法停用！");
                errMsgList.add(errMsg);
            }
            List<Long> purOrderItemSidList = purPurchaseOrderItemMapper.judgeOrderAndMaterial(new PurPurchaseOrderItem().setHandleStatusList(ORDER_STATUS)
                    .setMaterialSid(item.getMaterialSid()).setSkuSid(item.getSkuSid()));
            if (CollectionUtil.isNotEmpty(purOrderItemSidList)){
                errMsg = new CommonErrMsgResponse();
                errMsg.setMsg(item.getMaterialCode() + "的SKU明细 " + item.getSkuName() + " 已被采购订单引用，无法停用！");
                errMsgList.add(errMsg);
            }
        }
        return errMsgList;
    }

    private List<CommonErrMsgResponse> verifyBom(List<BasMaterialSku> materialSkuList){
        if (CollectionUtil.isEmpty(materialSkuList)){
            return new ArrayList<>();
        }
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        String[] BOM_STATUS = {HandleStatus.SAVE.getCode(),HandleStatus.RETURNED.getCode(),HandleStatus.SUBMIT.getCode()};
        for (BasMaterialSku item : materialSkuList) {
            List<Long> bomItemSidList = tecBomItemMapper.judgeBomAndMaterial(new TecBomItem().setBomMaterialSid(item.getMaterialSid())
                    .setHandleStatusList(BOM_STATUS).setBomMaterialSku1Sid(item.getSkuSid()).setBomMaterialSku2Sid(item.getSkuSid()));
            if (CollectionUtil.isNotEmpty(bomItemSidList)){
                errMsg = new CommonErrMsgResponse();
                errMsg.setMsg(item.getMaterialCode() + "的SKU明细 " + item.getSkuName() + " 已被BOM引用，无法停用！");
                errMsgList.add(errMsg);
            }
        }
        return errMsgList;
    }

    /**
     * 修改物料&商品-SKU明细
     *
     * @param basMaterialSku 物料&商品-SKU明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasMaterialSku(BasMaterialSku basMaterialSku) {
        return basMaterialSkuMapper.updateById(basMaterialSku);
    }

    /**
     * 批量删除物料&商品-SKU明细
     *
     * @param materialSkuSids 需要删除的物料&商品-SKU明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasMaterialSkuByIds(List<String> materialSkuSids) {
        return basMaterialSkuMapper.deleteBatchIds(materialSkuSids);
    }

    /**
     * 查询物料&商品-SKU明细报表
     *
     * @param basMaterialSku 物料&商品-SKU明细
     * @return 物料&商品-SKU明细集合
     */
    @Override
    public List<BasMaterialSku> getReportForm(BasMaterialSku basMaterialSku) {
        return basMaterialSkuMapper.getReportForm(basMaterialSku);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setPictureList(List<BasMaterialSku> basMaterialSkuList) {
        int row = 0;
        if (CollectionUtil.isEmpty(basMaterialSkuList)){
            return row;
        }
        basMaterialSkuList.forEach(basMaterialSku->{
            setPicture(basMaterialSku);
        });
        return basMaterialSkuList.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setPicture(BasMaterialSku basMaterialSku) {
        int row = 0;
        if (basMaterialSku.getMaterialSkuSid() == null){
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasMaterialSku> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BasMaterialSku::getMaterialSkuSid,basMaterialSku.getMaterialSkuSid());
        if (StrUtil.isBlank(basMaterialSku.getPicturePath())){
            basMaterialSku.setPicturePath(null);
        }
        updateWrapper.set(BasMaterialSku::getPicturePath, basMaterialSku.getPicturePath());
        row = basMaterialSkuMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 商品
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HashMap<String, Object> importData(MultipartFile file, String materialCategory) {
        // 返回体
        HashMap<String, Object> response = new HashMap<>();
        List<BasMaterialSku> basMaterialSkuList = new ArrayList<>();
        Map<Long,List<BasMaterialSku>> materialSkuList = new HashMap<>();
        List<String> materialSidList = new ArrayList<>();
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        //错误信息
        List<CommonErrMsgResponse> warnMsgList = new ArrayList<>();
        CommonErrMsgResponse warnMsg = null;
        String categoryName = "";
        if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(materialCategory)){
            categoryName = "物料";
        }
        if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(materialCategory)){
            categoryName = "商品";
        }
        // 需要处理启停状态的已存在的明细
        List<BasMaterialSku> needStatusMaterialSkuList = new ArrayList<>();
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //sku类型
            List<DictData> skuTypeList = sysDictDataService.selectDictData("s_sku_type");
            skuTypeList = skuTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> skuTypeMaps = skuTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //
            HashMap<String, String> skuMap = new HashMap<>();
            HashMap<String, BasMaterial> codeMap = new HashMap<>();
            HashMap<String, Integer> typeMap = new HashMap<>();
            //
            BasMaterial material = null;

            for (int i = 0; i < readAll.size(); i++) {
                int num = i + 1;
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);

                String materialCode = objects.get(0)==null||objects.get(0)==""?null:objects.get(0).toString();
                String skuTypeName = objects.get(1)==null||objects.get(1)==""?null:objects.get(1).toString();
                String skuNames = objects.get(2)==""||objects.get(2)==null?null:objects.get(2).toString();
                if (materialCode == null && skuTypeName == null && skuNames == null){
                    continue;
                }
                /*
                 * 物料/商品编码
                 */
                Long materialSid = null;
                String materialSku1Type = null, materialSku2Type = null, isSkuMaterial = null;
                Long sku2GroupSid = null;
                if(materialCode == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg(categoryName + "编码不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    material = new BasMaterial();
                    // 判断是否与表格内的编码重复
                    BasMaterial temp = codeMap.get(materialCode);
                    if (temp == null){
                        try {
                            material = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda().eq(BasMaterial::getMaterialCode, materialCode)
                                    .eq(BasMaterial::getMaterialCategory, materialCategory));
                        }catch (TooManyResultsException e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(categoryName + "编码" + materialCode + "存在重复数据，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        if (material == null || material.getMaterialSid() == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(categoryName + "编码" + materialCode + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            materialSid = material.getMaterialSid();
                            materialSku1Type = material.getSku1Type();
                            materialSku2Type = material.getSku2Type();
                            isSkuMaterial = material.getIsSkuMaterial();
                            sku2GroupSid = material.getSku2GroupSid();
                            codeMap.put(materialCode, material);
                            if (ConstantsEms.CHECK_STATUS.equals(material.getHandleStatus()) && ConstantsEms.ENABLE_STATUS.equals(material.getStatus())){
                                materialSidList.add(materialSid.toString());
                            }
                        }
                    }else {
                        materialSid = temp.getMaterialSid();
                        materialSku1Type = temp.getSku1Type();
                        materialSku2Type = temp.getSku2Type();
                        isSkuMaterial = temp.getIsSkuMaterial();
                        sku2GroupSid = temp.getSku2GroupSid();
                    }
                }
                /*
                 * SKU类型
                 */
                String skuType = null;
                if(skuTypeName == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("SKU类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    skuType= skuTypeMaps.get(skuTypeName);
                    if (StrUtil.isBlank(skuType)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("SKU类型配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        if(materialCode != null && !(skuType.equals(materialSku1Type) || skuType.equals(materialSku2Type))){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(categoryName + "档案" + materialCode + "中不存在SKU类型" + skuTypeName + "，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else if (StrUtil.isNotBlank(materialCode)) {
                            if (typeMap.containsKey(String.valueOf(materialCode) + "-" + String.valueOf(skuType))) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(categoryName + "编码" + materialCode + "的SKU类型" + skuTypeName + "在表格中已填写数据，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            else {
                                typeMap.put(String.valueOf(materialCode) + "-" + String.valueOf(skuType), num);
                            }
                        }
                    }
                }
                /*
                 * SKU名称
                 */
                if (skuNames == null){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("SKU名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                if(skuNames!=null){
                    if (materialSid != null && !ConstantsEms.YES.equals(isSkuMaterial)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("非SKU物料，SKU名称应为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    String[] basSkus = skuNames.split(";|；");
                    //字符串拆分数组后利用set去重复
                    Set<String> staffsSet = new HashSet<>(Arrays.asList(basSkus));
                    // 处理提示信息
                    if (skuType != null){
                        for (String s : staffsSet) {
                            BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda().eq(BasSku::getSkuType,skuType)
                                    .eq(BasSku::getSkuName, s));
                            if (basSku == null){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("找不到SKU类型为" + skuTypeName + "的 " + s +" 档案，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                BasMaterialSku skuItem = basMaterialSkuMapper.selectOne(new QueryWrapper<BasMaterialSku>()
                                        .lambda()
                                        .eq(BasMaterialSku::getMaterialSid,materialSid)
                                        .eq(BasMaterialSku::getSkuSid,basSku.getSkuSid()));
                                if (skuItem == null){
                                    if (!ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus()) || ConstantsEms.DISENABLE_STATUS.equals(basSku.getStatus())){
                                        errMsg = new CommonErrMsgResponse();
                                        errMsg.setItemNum(num);
                                        errMsg.setMsg("SKU类型为" + skuTypeName + "的 " + s +" 档案未确认或已停用，导入失败！");
                                        errMsgList.add(errMsg);
                                    }
                                    if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(materialCategory) && sku2GroupSid != null && ConstantsEms.SKUTYP_CM.equals(basSku.getSkuType())){
                                        List<BasSkuGroupItem> groupItemList = basSkuGroupItemMapper.selectList(new QueryWrapper<BasSkuGroupItem>()
                                                .lambda().eq(BasSkuGroupItem::getSkuGroupSid,sku2GroupSid).eq(BasSkuGroupItem::getSkuSid,basSku.getSkuSid()));
                                        if (CollectionUtil.isEmpty(groupItemList)){
                                            warnMsg = new CommonErrMsgResponse();
                                            warnMsg.setItemNum(num);
                                            warnMsg.setMsg("尺码组" + s + "不属于商品" + materialCode +"的尺码组，是否确认导入！");
                                            warnMsgList.add(warnMsg);
                                        }
                                    }
                                    if (materialSid != null){
                                        if (skuMap.get(materialSid.toString()+s) == null){
                                            //存进商品sku明细列表
                                            if (CollectionUtils.isEmpty(errMsgList)){
                                                List<BasMaterialSku> res = materialSkuList.get(materialSid);
                                                BasMaterialSku materialSku = new BasMaterialSku();
                                                materialSku.setMaterialSid(materialSid);
                                                materialSku.setSkuSid(basSku.getSkuSid()).setSkuType(basSku.getSkuType())
                                                        .setSkuCode(basSku.getSkuCode());
                                                materialSku.setStatus(ConstantsEms.ENABLE_STATUS).setCreateDate(new Date())
                                                        .setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                                                if (CollectionUtil.isEmpty(res)){
                                                    res = new ArrayList<BasMaterialSku>(){{add(materialSku);}};
                                                }else {
                                                    res.add(materialSku);
                                                }
                                                basMaterialSkuList.add(materialSku);
                                                materialSkuList.put(materialSid,res);
                                            }
                                            skuMap.put(materialSid+s,String.valueOf(num));
                                        } else {}
                                    }
                                }
                                else {
                                    if (ConstantsEms.DISENABLE_STATUS.equals(skuItem.getStatus())) {
                                        if (material != null) {
                                            skuItem.setHandleStatus(material.getHandleStatus())
                                                    .setSku1Type(material.getSku1Type())
                                                    .setSku2Type(material.getSku2Type());
                                        }
                                        needStatusMaterialSkuList.add(skuItem);
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                    else {
                        for (String s : staffsSet) {
                            BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda().eq(BasSku::getSkuName, s));
                            if (basSku == null){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("找不到SKU为 " + s +" 的档案，导入失败！");
                                errMsgList.add(errMsg);
                            } else {
                                if (!(ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus()))){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("SKU为" + s +" 的档案必须是确认状态，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            }
                        }
                    }
                }
            }
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
        //检查有没有报错
        if (CollectionUtil.isNotEmpty(errMsgList)){
            response.put("errList",errMsgList);
            response.put("warn",null);
            response.put("tableData",null);
        }else {
            if (CollectionUtil.isNotEmpty(warnMsgList)){
                response.put("errList",warnMsgList);
                response.put("warn",true);
                if (CollectionUtil.isNotEmpty(basMaterialSkuList)){
                    basMaterialSkuList.addAll(needStatusMaterialSkuList);
                    response.put("tableData",basMaterialSkuList);
                }else {
                    response.put("tableData",null);
                }
            }else {
                if (!materialSkuList.isEmpty()){
                    Set keyset = materialSkuList.keySet();
                    for(Object key:keyset){
                        this.insertBasMaterialSkuList((Long)key,materialSkuList.get(key));
                    }
                }
                if (CollectionUtil.isNotEmpty(needStatusMaterialSkuList)) {
                    for (BasMaterialSku item : needStatusMaterialSkuList) {
                        basMaterialSkuMapper.update(null, new UpdateWrapper<BasMaterialSku>().lambda()
                                .eq(BasMaterialSku::getMaterialSkuSid, item.getMaterialSkuSid())
                                .set(BasMaterialSku::getStatus, ConstantsEms.ENABLE_STATUS));
                        if (ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus())) {
                            if (ConstantsEms.SKUTYP_CM.equals(item.getSku1Type())) {
                                basMaterialBarcodeMapper.update(null, new UpdateWrapper<BasMaterialBarcode>().lambda()
                                                .set(BasMaterialBarcode::getStatus, ConstantsEms.ENABLE_STATUS)
                                        .eq(BasMaterialBarcode::getMaterialSid, item.getMaterialSid())
                                        .eq(BasMaterialBarcode::getSku1Sid, item.getSkuSid()));
                            }
                            else if (ConstantsEms.SKUTYP_CM.equals(item.getSku2Type())) {
                                basMaterialBarcodeMapper.update(null, new UpdateWrapper<BasMaterialBarcode>().lambda()
                                        .set(BasMaterialBarcode::getStatus, ConstantsEms.ENABLE_STATUS)
                                        .eq(BasMaterialBarcode::getMaterialSid, item.getMaterialSid())
                                        .eq(BasMaterialBarcode::getSku2Sid, item.getSkuSid()));
                            }

                        }
                    }
                }
                response.put("errList",null);
                response.put("warn",null);
                if (CollectionUtil.isNotEmpty(materialSidList)){
                    response.put("tableData",materialSidList);
                }else {
                    response.put("tableData",null);
                }
            }
        }
        return response;
    }

    //填充
    public void copy(List<Object> objects,List<List<Object>> readAll){
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i=lineSize;i<size;i++){
            Object o = new Object();
            o=null;
            objects.add(o);
        }
    }
}
