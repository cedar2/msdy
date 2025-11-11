package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.InvInventoryLocationMaterialRequest;
import com.platform.ems.domain.dto.request.InvReserveInventoryRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.InvReserveInventoryResponse;
import com.platform.ems.domain.dto.response.form.InvInventoryLocationBarcodeStatisticsForm;
import com.platform.ems.domain.dto.response.form.InvInventoryLocationStoreStatisticsForm;
import com.platform.ems.domain.dto.response.form.InvInventorySpecialBarcodeStatisticsForm;
import com.platform.ems.domain.dto.response.form.InvInventorySpecialStoreStatisticsForm;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConMeasureUnit;
import com.platform.ems.plug.domain.ConMovementType;
import com.platform.ems.plug.domain.ConSpecialStock;
import com.platform.ems.plug.mapper.ConMeasureUnitMapper;
import com.platform.ems.plug.mapper.ConMovementTypeMapper;
import com.platform.ems.plug.mapper.ConSpecialStockMapper;
import com.platform.ems.service.IInvInventoryLocationService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 仓库库位库存Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-16
 */
@Service
@SuppressWarnings("all")
public class InvInventoryLocationServiceImpl extends ServiceImpl<InvInventoryLocationMapper,InvInventoryLocation>  implements IInvInventoryLocationService {
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private InvInventoryDocumentServiceImpl documentServiceImpl;
    @Autowired
    private BasStorehouseMapper basStorehouseMapper;
    @Autowired
    private BasStorehouseLocationMapper basStorehouseLocationMapper;
    @Autowired
    private ConSpecialStockMapper  conSpecialStockMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    @Autowired
    private InvStorehouseMaterialMapper  invStorehouseMaterialMapper;
    @Autowired
    private InvCusSpecialInventoryMapper invCusSpecialInventoryMapper;
    @Autowired
    private InvVenSpecialInventoryMapper invVenSpecialInventoryMapper;
    @Autowired
    private InvReserveInventoryMapper invReserveInventoryMapper;
    @Autowired
    private ConMovementTypeMapper conMovementTypeMapper;
    private static final String TITLE = "仓库库位库存";
    /**
     * 查询仓库库位库存
     *
     * @param locationStockSid 仓库库位库存ID
     * @return 仓库库位库存
     */
    @Override
    public InvInventoryLocation selectInvInventoryLocationById(Long locationStockSid) {
        InvInventoryLocation invInventoryLocation = invInventoryLocationMapper.selectInvInventoryLocationById(locationStockSid);
        MongodbUtil.find(invInventoryLocation);
        return  invInventoryLocation;
    }
    /**
     * 添加样品获取库存数量和库存价
     *
     */
    @Override
    public List<BasMaterial> getLocationMaterial(InvInventoryLocationMaterialRequest locationMaterial){
        Long storehouseLocationSid = locationMaterial.getStorehouseLocationSid();
        Long storehouseSid = locationMaterial.getStorehouseSid();
        List<BasMaterial> basMaterialList = locationMaterial.getBasMaterialList();
        basMaterialList.forEach(li->{
            InvInventoryLocation invInventoryLocation = invInventoryLocationMapper.selectOne(new QueryWrapper<InvInventoryLocation>().lambda()
                    .eq(InvInventoryLocation::getBarcodeSid, li.getBarcodeSid())
                    .eq(InvInventoryLocation::getStorehouseLocationSid, storehouseLocationSid)
                    .eq(InvInventoryLocation::getStorehouseSid, storehouseSid)
            );
            InvStorehouseMaterial invStorehouseMaterial = invStorehouseMaterialMapper.selectOne(new QueryWrapper<InvStorehouseMaterial>().lambda()
                    .eq(InvStorehouseMaterial::getBarcodeSid, li.getBarcodeSid())
                    .eq(InvStorehouseMaterial::getStorehouseSid, storehouseSid)
            );
            li.setStockQuantity(invInventoryLocation==null?null:invInventoryLocation.getUnlimitedQuantity());
            li.setPrice(invStorehouseMaterial==null?null:invStorehouseMaterial.getPrice());
        });
        return basMaterialList;
    }

    /**
     * 添加盘点获取数量
     *
     */
    @Override
    public List<BasMaterial> getLocationMaterialQu(InvInventoryLocationMaterialRequest locationMaterial){
        Long storehouseLocationSid = locationMaterial.getStorehouseLocationSid();
        String stock = locationMaterial.getSpecialStock();
        Long storehouseSid = locationMaterial.getStorehouseSid();
        List<BasMaterial> basMaterialList = locationMaterial.getBasMaterialList();
        //常规库存
        if (stock == null) {
            basMaterialList.forEach(li->{
                InvInventoryLocation invInventoryLocation = invInventoryLocationMapper.selectOne(new QueryWrapper<InvInventoryLocation>().lambda()
                        .eq(InvInventoryLocation::getBarcodeSid, li.getBarcodeSid())
                        .eq(InvInventoryLocation::getStorehouseSid, locationMaterial.getStorehouseSid())
                        .eq(InvInventoryLocation::getStorehouseLocationSid, locationMaterial.getStorehouseLocationSid())
                );
                if(invInventoryLocation!=null){
                    li.setStockQuantity(invInventoryLocation.getUnlimitedQuantity());
                }else{
                    li.setStockQuantity(BigDecimal.ZERO);
                }
            });
        }
        //供应商特殊库存
        if (ConstantsEms.VEN_CU.equals(stock) || ConstantsEms.VEN_RA.equals(stock)) {
            basMaterialList.forEach(li->{
                InvVenSpecialInventory invVenSpecialInventory = invVenSpecialInventoryMapper.selectOne(new QueryWrapper<InvVenSpecialInventory>().lambda()
                        .eq(InvVenSpecialInventory::getBarcodeSid, li.getBarcodeSid())
                        .eq(InvVenSpecialInventory::getSpecialStock, stock)
                        .eq(InvVenSpecialInventory::getVendorSid,locationMaterial.getVendorSid())
                        .eq(InvVenSpecialInventory::getStorehouseSid, locationMaterial.getStorehouseSid())
                        .eq(InvVenSpecialInventory::getStorehouseLocationSid, locationMaterial.getStorehouseLocationSid())
                );
                if(invVenSpecialInventory!=null){
                    li.setStockQuantity(invVenSpecialInventory.getUnlimitedQuantity());
                }else{
                    li.setStockQuantity(BigDecimal.ZERO);
                }
            });
        }
        //客户特殊库存
        if (ConstantsEms.CUS_RA.equals(stock) || ConstantsEms.CUS_VE.equals(stock)) {
            basMaterialList.forEach(li->{
                InvCusSpecialInventory invCusSpecialInventory = invCusSpecialInventoryMapper.selectOne(new QueryWrapper<InvCusSpecialInventory>().lambda()
                        .eq(InvCusSpecialInventory::getBarcodeSid, li.getBarcodeSid())
                        .eq(InvCusSpecialInventory::getSpecialStock, stock)
                        .eq(InvCusSpecialInventory::getCustomerSid,locationMaterial.getCustomerSid())
                        .eq(InvCusSpecialInventory::getStorehouseSid, locationMaterial.getStorehouseSid())
                        .eq(InvCusSpecialInventory::getStorehouseLocationSid, locationMaterial.getStorehouseLocationSid())
                );
                if(invCusSpecialInventory!=null){
                    li.setStockQuantity(invCusSpecialInventory.getUnlimitedQuantity());
                }else{
                    li.setStockQuantity(BigDecimal.ZERO);
                }
            });
        }
        return basMaterialList;
    }
    /**
     * 查询仓库库位库存列表
     *
     * @param invInventoryLocation 仓库库位库存
     * @return 仓库库位库存
     */
    @Override
    public List<InvInventoryLocation> selectInvInventoryLocationList(InvInventoryLocation invInventoryLocation) {
        return invInventoryLocationMapper.selectInvInventoryLocationList(invInventoryLocation);
    }
    /**
     * 库存预留报表
     *
     */
    @Override
    public List<InvReserveInventoryResponse> report(InvReserveInventoryRequest request) {
        return invReserveInventoryMapper.report(request);
    }

    /**
     * 新增仓库库位库存
     * 需要注意编码重复校验
     * @param invInventoryLocation 仓库库位库存
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvInventoryLocation(InvInventoryLocation invInventoryLocation) {
        int row= invInventoryLocationMapper.insert(invInventoryLocation);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(invInventoryLocation.getLocationStockSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    @Override
    public int getMaterialLocation(List<InvInventoryLocation> list) {
        list.forEach(item->{
            List<InvInventoryLocation> invInventoryLocations = invInventoryLocationMapper.selectList(new QueryWrapper<InvInventoryLocation>().lambda()
                    .eq(InvInventoryLocation::getStorehouseSid, item.getStorehouseSid())
                    .eq(InvInventoryLocation::getBarcodeSid, item.getBarcodeSid())
            );
            BasStorehouse basStorehouse = basStorehouseMapper.selectById(item.getStorehouseSid());
            if(CollectionUtils.isEmpty(invInventoryLocations)){
                if(item.getSku2Name()!=null){
                    throw new CustomException(basStorehouse.getStorehouseName()+",sku1为"+item.getSku1Name()+"、sku2为"+item.getSku2Name()+"、"+item.getMaterialCode()+"的"+item.getMaterialName()+"不存在库存，无法添加");
                }else{
                    throw new CustomException(basStorehouse.getStorehouseName()+",sku为"+item.getSku1Name()+"、"+item.getMaterialCode()+"的"+item.getMaterialName()+"不存在库存，无法添加");
                }
            }else{
                BigDecimal sum = invInventoryLocations.stream().map(li -> li.getUnlimitedQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                if(sum.compareTo(BigDecimal.ZERO)!=1){
                    if(item.getSku2Name()!=null){
                        throw new CustomException(basStorehouse.getStorehouseName()+",sku1为"+item.getSku1Name()+"、sku2为"+item.getSku2Name()+"、"+item.getMaterialCode()+"的"+item.getMaterialName()+"库存量为0，无法添加");
                    }else{
                        throw new CustomException(basStorehouse.getStorehouseName()+",sku为"+item.getSku1Name()+"、"+item.getMaterialCode()+"的"+item.getMaterialName()+"库存量为0，无法添加");
                    }
                }
            }
        });
        return 1;
    }

    /**
     * 修改仓库库位库存
     *
     * @param invInventoryLocation 仓库库位库存
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvInventoryLocation(InvInventoryLocation invInventoryLocation) {
        InvInventoryLocation response = invInventoryLocationMapper.selectInvInventoryLocationById(invInventoryLocation.getLocationStockSid());
        int row=invInventoryLocationMapper.updateById(invInventoryLocation);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invInventoryLocation.getLocationStockSid(), BusinessType.UPDATE.ordinal(), response,invInventoryLocation,TITLE);
        }
        return row;
    }

    /**
     * 变更仓库库位库存
     *
     * @param invInventoryLocation 仓库库位库存
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeInvInventoryLocation(InvInventoryLocation invInventoryLocation) {
        InvInventoryLocation response = invInventoryLocationMapper.selectInvInventoryLocationById(invInventoryLocation.getLocationStockSid());
                                                                                                                                                                    int row=invInventoryLocationMapper.updateAllById(invInventoryLocation);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invInventoryLocation.getLocationStockSid(), BusinessType.CHANGE.ordinal(), response,invInventoryLocation,TITLE);
        }
        return row;
    }

    /**
     * 批量删除仓库库位库存
     *
     * @param locationStockSids 需要删除的仓库库位库存ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvInventoryLocationByIds(List<Long> locationStockSids) {
        return invInventoryLocationMapper.deleteBatchIds(locationStockSids);
    }

    /**
     * 释放库存预留
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvReserve(List<Long> locationStockSids) {
        return invReserveInventoryMapper.deleteBatchIds(locationStockSids);
    }

    /**
    * 启用/停用
    * @param invInventoryLocation
    * @return
    */
    @Override
    public int changeStatus(InvInventoryLocation invInventoryLocation){
        int row=0;
        Long[] sids=invInventoryLocation.getLocationStockSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                invInventoryLocation.setLocationStockSid(id);
                row=invInventoryLocationMapper.updateById( invInventoryLocation);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(invInventoryLocation.getLocationStockSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,null);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param invInventoryLocation
     * @return
     */
    @Override
    public int check(InvInventoryLocation invInventoryLocation){
        int row=0;
        Long[] sids=invInventoryLocation.getLocationStockSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                invInventoryLocation.setLocationStockSid(id);
                row=invInventoryLocationMapper.updateById( invInventoryLocation);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(invInventoryLocation.getLocationStockSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }
    /**
     * 库存初始化 旧
     */
    @Override
    public InvInventoryDocument importDataInvOld(MultipartFile file){
        Long basStorehouseSid=null;
        Long StorehouseLocationSid=null;
        Long vendorSid=null;
        Long customerSid=null;
        String specialStock=null;
        List<Long> bardCodeList = new ArrayList<>();
        try{
            File toFile=null;
            try {
                toFile   = FileUtils.multipartFileToFile(file);
            }catch (Exception e){
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            Map<String,String> specialMaps = conSpecialStockMapper.getList().stream().collect(Collectors.toMap(ConSpecialStock::getName, ConSpecialStock::getCode, (key1, key2) -> key2));
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            ArrayList<InvInventoryDocumentItem> invInventoryDocumentItems = new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long sku1Sid=null;
                Long sku2Sid=null;
                Long materialSid=null;
                Long barcodeSid=null;
                String barcode=null;
                String sku1Name=null;
                String sku1code=null;
                String sku2Name=null;
                String sku2Code=null;
                String materialName=null;
                BasMaterialBarcode basMaterialBarcode=null;
                if (i < 2 || i==3||i==4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        throw new BaseException("作业类型名称，不能为空，导入失败");
                    }
                    if(!objects.get(0).equals("初始化库存")){
                        throw new BaseException("作业类型名称，只能是初始化库存，导入失败");
                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                        throw new BaseException("过账日期，不能为空，导入失败");
                    }
                    boolean validDate = JudgeFormat.isValidDate(objects.get(1).toString());
                    if(!validDate){
                        throw new BaseException("过账日期，格式错误，导入失败");
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        throw new BaseException("仓库编码，不能为空，导入失败");
                    }
                    BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                            .eq(BasStorehouse::getStorehouseCode, objects.get(2).toString())
                    );
                    if (basStorehouse == null) {
                        throw new BaseException("没有编码为" + objects.get(2).toString() + "的仓库，导入失败");
                    } else {
                        if(!ConstantsEms.CHECK_STATUS.equals(basStorehouse.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouse.getStatus())){
                            throw new BaseException("仓库必须是确认且已启用状态，导入失败");
                        }
                        basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        throw new BaseException("库位编码，不能为空，导入失败");
                    }
                    BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                            .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                            .eq(BasStorehouseLocation::getLocationCode, objects.get(3).toString())
                    );
                    if (basStorehouseLocation == null) {
                        throw new BaseException("编码为" + objects.get(2).toString() + "的仓库下没有"+objects.get(3).toString()+"的库位，导入失败");
                    } else {
                        if(!ConstantsEms.CHECK_STATUS.equals(basStorehouseLocation.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouseLocation.getStatus())){
                            throw new BaseException("库位必须是确认且已启用状态，导入失败");
                        }
                        StorehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                    }
                    if(objects.get(4) != null && objects.get(4) != ""){
                        String special = specialMaps.get(objects.get(4).toString());
                        if(special==null){
                            throw new BaseException("特殊库存数据格式错误，导入失败");
                        }
                        specialStock=special;
                    }
                    if(objects.get(4)==null ||objects.get(4)==""){
                        if(objects.get(5)!=null&&objects.get(5)!=""){
                            throw new BaseException("供应商简称必须为空，导入失败");
                        }
                        if(objects.get(6)!=null&&objects.get(6)!=""){
                            throw new BaseException("客户简称必须为空，导入失败");
                        }
                    }
                    if(objects.get(4)!=null&&objects.get(4)!=""){
                        if(ConstantsEms.VEN_RA.equals(specialMaps.get(objects.get(4).toString()))||ConstantsEms.VEN_CU.equals(specialMaps.get(objects.get(4).toString()))){
                            if(objects.get(5)==null||objects.get(5)==""){
                                throw new BaseException("供应商简称不能为空，导入失败");
                            }
                            if(objects.get(6)!=null&&objects.get(6)!=""){
                                throw new BaseException("客户简称必须为空，导入失败");
                            }
                        }else if(ConstantsEms.CUS_RA.equals(specialMaps.get(objects.get(4).toString()))||ConstantsEms.CUS_VE.equals(specialMaps.get(objects.get(4).toString()))){
                            if(objects.get(6)==null||objects.get(6)==""){
                                throw new BaseException("客户简称不能为空，导入失败");
                            }
                            if(objects.get(5)!=null&&objects.get(5)!=""){
                                throw new BaseException("供应商简称必须为空，导入失败");
                            }
                        }
                    }
                    if (objects.get(5) != "" &&objects.get(5) != null) {
                        String vendorCode = objects.get(5).toString();
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                                .lambda().eq(BasVendor::getShortName, vendorCode)
                        );
                        if (basVendor == null) {
                            throw new BaseException("简称为" + vendorCode + "没有对应的供应商，导入失败");
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basVendor.getStatus())){
                                throw new BaseException("供应商必须是确认且已启用状态，导入失败");
                            }
                            vendorSid = Long.valueOf(basVendor.getVendorSid());
                        }
                    }
                    if (objects.get(6) != ""&&objects.get(6) != null) {
                        String customerCode = objects.get(6).toString();
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                                .lambda().eq(BasCustomer::getShortName, customerCode));
                        if (basCustomer == null) {
                            throw new BaseException("简称为" + customerCode + "没有对应的客户，导入失败");
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basCustomer.getStatus())){
                                throw new BaseException("客户必须是确认且已启用状态，导入失败");
                            }
                            customerSid = basCustomer.getCustomerSid();
                        }
                    }
                    String account = objects.get(1).toString();
                    Date accountDate = DateUtil.parse(account);
                    invInventoryDocument.setType(ConstantsEms.RU_KU)
                            .setMovementType(ConstantsEms.STARTLOCATION)
                            .setStorehouseSid(basStorehouseSid)
                            .setStorehouseLocationSid(StorehouseLocationSid)
                            .setAccountDate(accountDate)
                            .setSpecialStock((objects.get(4)==""||objects.get(4)==null)?null:specialMaps.get(objects.get(4).toString()))
                            .setCustomerSid(customerSid)
                            .setVendorSid(vendorSid)
                            .setRemark((objects.get(7)==""||objects.get(7)==null)?null:objects.get(7).toString());
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copyItem(objects, readAll);
                int num=i+1;
                if (objects.get(0) == null || objects.get(0) == "") {
                    throw new BaseException("第"+num+"行,物料/商品编码不可为空，导入失败");
                }
                BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                        .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                );
                if (basMaterial==null) {
                    throw new BaseException("第"+num+"行,没有编码为"+objects.get(0).toString()+"的商品，导入失败");
                }else{
                    if(!ConstantsEms.CHECK_STATUS.equals(basMaterial.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basMaterial.getStatus())){
                        throw new BaseException("第"+num+"行,物料/商品必须是确认且已启用状态，导入失败");
                    }
                    materialName=basMaterial.getMaterialName();
                    materialSid=basMaterial.getMaterialSid();
                }
                if (objects.get(1) == null || objects.get(1) == "") {
                    throw new BaseException("第"+num+"行,SKU1名称不可为空，导入失败");
                }
                BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                        .eq(BasSku::getSkuName, objects.get(1).toString())
                );
                if (basSku==null) {
                    throw new BaseException("第"+num+"行,没有名称为"+objects.get(1).toString()+"的sku1，导入失败");
                }else{
                    if(!basSku.getSkuType().equals(ConstantsEms.SKUTYP_YS)){
                        throw new BaseException("第"+num+"行,SKU1名称必须是颜色类型，导入失败");
                    }
                    if(!ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basSku.getStatus())){
                        throw new BaseException("第"+num+"行,SKU1名称必须是确认且已启用状态，导入失败");
                    }
                    sku1Name=basSku.getSkuName();
                    sku1code=basSku.getSkuCode();
                    sku1Sid=basSku.getSkuSid();
                }
                if (objects.get(2) != null && objects.get(2) != "") {
                    BasSku basSku2 = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                            .eq(BasSku::getSkuName, objects.get(2).toString())
                    );
                    if (basSku2==null) {
                        throw new BaseException("第"+num+"行,没有名称为"+objects.get(2).toString()+"的sku2，导入失败");
                    }else{
                        if(!basSku.getSkuType().equals(ConstantsEms.SKUTYP_YS)){
                            throw new BaseException("第"+num+"行,SKU2名称不能是颜色类型，导入失败");
                        }
                        if(!ConstantsEms.CHECK_STATUS.equals(basSku2.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basSku2.getStatus())){
                            throw new BaseException("第"+num+"行,SKU2名称必须是确认且已启用状态，导入失败");
                        }
                        sku2Code=basSku2.getSkuCode();
                        sku2Name=basSku2.getSkuName();
                        sku2Sid=basSku2.getSkuSid();
                    }
                }
                if (objects.get(3) == null || objects.get(3) == "") {
                    throw new BaseException("第"+num+"行,数量 不可为空，导入失败");
                }
                boolean validInt = JudgeFormat.isValidDouble(objects.get(3).toString());
                if(!validInt){
                    throw new BaseException("第"+num+"行,数量格式错误，导入失败");
                }
                if(specialStock!=null){
                    if(objects.get(4) != null && objects.get(4) != ""){
                        throw new BaseException("第"+num+"行,"+objects.get(4).toString()+"库存价必须为空，导入失败");
                    }
                }else{
                    if(objects.get(4) == null || objects.get(4) == ""){
                        throw new BaseException("第"+num+"行,库存价不允许为空，导入失败");
                    }
                }
                if (objects.get(4) != null && objects.get(4) != "") {
                    boolean validDouble = JudgeFormat.isValidDouble(objects.get(4).toString());
                    if(!validDouble){
                        throw new BaseException("第"+num+"行,库存价格式错误，导入失败");
                    }
                }
                Double mount = Double.valueOf(objects.get(3).toString());
                if(mount<0){
                    throw new BaseException("第"+num+"行的数量小于0，不允许导入，导入失败");
                }
                if(objects.get(4)!=""&&objects.get(4)!=null){
                    Double mountPrice = Double.valueOf(objects.get(4).toString());
                    if(mountPrice<0){
                        throw new BaseException("第"+num+"行的库存价小于0，不允许导入，导入失败");
                    }
                }
                if(sku1Sid!=null&&sku2Sid!=null){
                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                            .eq(BasMaterialBarcode::getSku2Sid, sku2Sid)
                    );
                }else if(sku1Sid!=null&&sku2Sid==null){
                    basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getMaterialSid, materialSid)
                            .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                            .isNull(BasMaterialBarcode::getSku2Sid)
                    );
                }
                if(basMaterialBarcode==null){
                    throw new BaseException("第"+num+"行没有对应的商品条码存在，导入失败");
                }else{
                    if(!ConstantsEms.ENABLE_STATUS.equals(basMaterialBarcode.getStatus())){
                        throw new BaseException("第"+num+"行对应的商品条码必须已启用的状态，导入失败");
                    }
                    barcodeSid=basMaterialBarcode.getBarcodeSid();
                    barcode=basMaterialBarcode.getBarcode();
                    bardCodeList.add(barcodeSid);
                }
                InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                BasMaterial material = basMaterialMapper.selectById(materialSid);
                String unitBase = material.getUnitBase();
                String unitBaseName = material.getUnitBaseName();
                invInventoryDocumentItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setSku1Code(sku1code)
                        .setSku1Name(sku1Name)
                        .setSku2Code(sku2Code)
                        .setSku2Name(sku2Name)
                        .setUnitBase(unitBase)
                        .setUnitBaseName(unitBaseName)
                        .setBarcode(barcode)
                        .setMaterialSid(materialSid)
                        .setMaterialCode(objects.get(0).toString())
                        .setMaterialName(materialName)
                        .setBarcodeSid(barcodeSid)
                        .setPrice(objects.get(4)==null||objects.get(4)==""?null:BigDecimal.valueOf(Double.valueOf(objects.get(4).toString())).divide(BigDecimal.ONE,5,BigDecimal.ROUND_HALF_UP))
                        .setQuantity(objects.get(3)==null||objects.get(3)==""?null:BigDecimal.valueOf(Double.valueOf(objects.get(3).toString())).divide(BigDecimal.ONE,4,BigDecimal.ROUND_HALF_UP))
                        .setRemark((objects.get(5)==""||objects.get(5)==null)?null:objects.get(5).toString());
                invInventoryDocumentItems.add(invInventoryDocumentItem);
            }
            HashSet<Long> longs = new HashSet<>(bardCodeList);
            if(longs.size()!=bardCodeList.size()){
                for (int i=0;i<bardCodeList.size();i++){
                    for (int j=i+1;j<bardCodeList.size();j++){
                        if(bardCodeList.get(i).equals(bardCodeList.get(j))){
                            int nu=j+1+5;
                            throw new BaseException("第"+nu+"行，商品条码重复，请核实");
                        }
                    }
                }
            }
            invInventoryDocument.setDocumentCategory("RK");
            invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocumentItems);
            invInventoryDocument.setInitializeStatus(ConstantsEms.YES);
            return invInventoryDocument;
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
    }
    /**
     * 库存初始化
     */
    @Override
    public AjaxResult importDataInv(MultipartFile file){
        Long basStorehouseSid=null;
        Long StorehouseLocationSid=null;
        Long vendorSid=null;
        Long customerSid=null;
        String specialStock=null;
        String isRepeteBacode=null;
        List<Long> bardCodeList = new ArrayList<>();
        try{
            File toFile=null;
            try {
                toFile   = FileUtils.multipartFileToFile(file);
            }catch (Exception e){
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            Map<String,String> specialMaps = conSpecialStockMapper.getList().stream().collect(Collectors.toMap(ConSpecialStock::getName, ConSpecialStock::getCode, (key1, key2) -> key2));
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            ArrayList<InvInventoryDocumentItem> invInventoryDocumentItems = new ArrayList<>();
            List<CommonErrMsgResponse> msgList=new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long sku1Sid=null;
                Long sku2Sid=null;
                Long materialSid=null;
                Long barcodeSid=null;
                String barcode=null;
                String sku1Code=null;
                String sku2Code=null;
                String materialName=null;
                BigDecimal quantity=null;
                BigDecimal price=null;
                Date accountDate=null;
                String unitBase=null;
                String unitBaseName=null;
                BasMaterial basMaterial=null;
                if (i < 2 || i==3||i==4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("作业类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!objects.get(0).equals("初始化库存")){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("作业类型名称，只能是初始化库存，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("过账日期，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        boolean validDate = JudgeFormat.isValidDate(objects.get(1).toString());
                        if(!validDate){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("过账日期，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            String account = objects.get(1).toString();
                            accountDate = DateUtil.parse(account);
                        }
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("仓库，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseName, objects.get(2).toString())
                        );
                        if (basStorehouse == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("没有名称为" + objects.get(2).toString() + "的仓库，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouse.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouse.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("仓库必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                        }
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("库位，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationName, objects.get(3).toString())
                        );
                        if (basStorehouseLocation == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("名称为" + objects.get(2).toString() + "的仓库下没有"+objects.get(3).toString()+"的库位，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouseLocation.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouseLocation.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("库位必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            StorehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                        }
                    }
                    if(objects.get(4) != null && objects.get(4) != ""){
                        String special = specialMaps.get(objects.get(4).toString());
                        if(special==null){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("特殊库存数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        specialStock=special;
                    }
                    if(objects.get(4)==null ||objects.get(4)==""){
                        if(objects.get(5)!=null&&objects.get(5)!=""){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("供应商简称必须为空，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        if(objects.get(6)!=null&&objects.get(6)!=""){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("客户简称必须为空，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if(objects.get(4)!=null&&objects.get(4)!=""){
                        if(ConstantsEms.VEN_RA.equals(specialMaps.get(objects.get(4).toString()))||ConstantsEms.VEN_CU.equals(specialMaps.get(objects.get(4).toString()))){
                            if(objects.get(5)==null||objects.get(5)==""){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("供应商简称不能为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            if(objects.get(6)!=null&&objects.get(6)!=""){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("客户简称必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }else if(ConstantsEms.CUS_RA.equals(specialMaps.get(objects.get(4).toString()))||ConstantsEms.CUS_VE.equals(specialMaps.get(objects.get(4).toString()))){
                            if(objects.get(6)==null||objects.get(6)==""){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("客户简称不能为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            if(objects.get(5)!=null&&objects.get(5)!=""){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("供应商简称必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    if (objects.get(5) != "" &&objects.get(5) != null) {
                        String vendorCode = objects.get(5).toString();
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                                .lambda().eq(BasVendor::getShortName, vendorCode)
                        );
                        if (basVendor == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("简称为" + vendorCode + "没有对应的供应商，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basVendor.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("供应商必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            vendorSid = Long.valueOf(basVendor.getVendorSid());
                        }
                    }
                    if (objects.get(6) != ""&&objects.get(6) != null) {
                        String customerCode = objects.get(6).toString();
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                                .lambda().eq(BasCustomer::getShortName, customerCode));
                        if (basCustomer == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("简称为" + customerCode + "没有对应的客户，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basCustomer.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("客户必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            customerSid = basCustomer.getCustomerSid();
                        }
                    }
                    if(objects.get(7) == ""||objects.get(7) == null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("是否允许重复明细不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!"是".equals(objects.get(7).toString())&&!"否".equals(objects.get(7).toString())){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("是否允许重复明细格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            isRepeteBacode=objects.get(7).toString();
                        }
                    }
                    invInventoryDocument.setType(ConstantsEms.RU_KU)
                            .setMovementType(ConstantsEms.STARTLOCATION)
                            .setStorehouseSid(basStorehouseSid)
                            .setStorehouseLocationSid(StorehouseLocationSid)
                            .setAccountDate(accountDate)
                            .setSpecialStock((objects.get(4)==""||objects.get(4)==null)?null:specialMaps.get(objects.get(4).toString()))
                            .setCustomerSid(customerSid)
                            .setVendorSid(vendorSid)
                            .setRemark((objects.get(8)==""||objects.get(8)==null)?null:objects.get(8).toString());
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copyItem(objects, readAll);
                int num=i+1;
                // 物料/商品编码
                String materialCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (materialCode == null) {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("物料/商品编码不可为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                // sku1名称
                String sku1Name = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                // sku2名称
                String sku2Name = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                // 数量
                if (objects.get(3) == null || objects.get(3) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("数量 不可为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    boolean validInt = JudgeFormat.isValidDouble(objects.get(3).toString());
                    if(!validInt){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("数量格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        Double mount = Double.valueOf(objects.get(3).toString());
                        if(mount<=0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("数量小于等于0，不允许导入，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        quantity=BigDecimal.valueOf(Double.valueOf(objects.get(3).toString())).divide(BigDecimal.ONE,4,BigDecimal.ROUND_HALF_UP);
                    }

                }
                if(specialStock!=null){
                    if(objects.get(4) != null && objects.get(4) != ""){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg(objects.get(4).toString()+"库存价必须为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }else{
//                    if(objects.get(4) == null || objects.get(4) == ""){
//                      //  throw new BaseException("第"+num+"行,库存价不允许为空，导入失败");
//                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
//                        errMsgResponse.setItemNum(num);
//                        errMsgResponse.setMsg("库存价不允许为空，导入失败");
//                        msgList.add(errMsgResponse);
//                    }
                }
                if (objects.get(4) != null && objects.get(4) != "") {
                    boolean validDouble = JudgeFormat.isValidDouble(objects.get(4).toString());
                    if(!validDouble){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("库存价格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(objects.get(4)!=""&&objects.get(4)!=null){
                            Double mountPrice = Double.valueOf(objects.get(4).toString());
                            if(mountPrice<0){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("库存价小于0，不允许导入，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                        price= BigDecimal.valueOf(Double.valueOf(objects.get(4).toString())).divide(BigDecimal.ONE,5,BigDecimal.ROUND_HALF_UP);
                    }
                }
                try {
                    if (materialCode != null) {
                        BasMaterialBarcode basMaterialBarcode = basMaterialBarcodeMapper.selectBasMaterialBarcodeListByInvImport(new BasMaterialBarcode()
                                .setMaterialCode(materialCode).setSku1Name(sku1Name).setSku2Name(sku2Name));
                        if (basMaterialBarcode != null && basMaterialBarcode.getBarcodeSid() != null) {
                            // 物料商品信息
                            materialName = basMaterialBarcode.getMaterialName();
                            materialSid = basMaterialBarcode.getMaterialSid();
                            unitBase = basMaterialBarcode.getUnitBase();
                            unitBaseName = basMaterialBarcode.getUnitBaseName();
                            // sku1信息
                            sku1Name = basMaterialBarcode.getSku1Name();
                            sku1Code = basMaterialBarcode.getSku1Code();
                            sku1Sid = basMaterialBarcode.getSku1Sid();
                            // sku2信息
                            sku2Code = basMaterialBarcode.getSku2Code();
                            sku2Name = basMaterialBarcode.getSku2Name();
                            sku2Sid = basMaterialBarcode.getSku2Sid();
                            // 商品条码信息
                            barcodeSid = basMaterialBarcode.getBarcodeSid();
                            barcode = basMaterialBarcode.getBarcode();
                            bardCodeList.add(basMaterialBarcode.getBarcodeSid());
                        }
                        else {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("此组合的商品条码不存在，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                    }
                } catch (TooManyResultsException e) {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("此组合的商品条码系统中存在多笔，请联系管理员，导入失败！");
                    msgList.add(errMsgResponse);
                }
                InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                invInventoryDocumentItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setSku1Code(sku1Code)
                        .setSku1Name(sku1Name)
                        .setSku2Code(sku2Code)
                        .setSku2Name(sku2Name)
                        .setStorehouseSid(basStorehouseSid)
                        .setStorehouseLocationSid(StorehouseLocationSid)
                        .setUnitBase(unitBase)
                        .setUnitBaseName(unitBaseName)
                        .setBarcode(barcode)
                        .setMaterialSid(materialSid)
                        .setMaterialCode(materialCode)
                        .setMaterialName(materialName)
                        .setBarcodeSid(barcodeSid)
                        .setPrice(objects.get(4)==null||objects.get(4)==""?BigDecimal.ZERO:price)
                        .setQuantity(objects.get(3)==null||objects.get(3)==""?null:quantity)
                        .setRemark((objects.get(5)==""||objects.get(5)==null)?null:objects.get(5).toString());
                invInventoryDocumentItems.add(invInventoryDocumentItem);
            }
            if(CollectionUtil.isNotEmpty(bardCodeList)){
                if("否".equals(isRepeteBacode)){
                    HashSet<Long> longs = new HashSet<>(bardCodeList);
                    if(longs.size()!=bardCodeList.size()){
                        for (int i=0;i<bardCodeList.size();i++){
                            for (int j=i+1;j<bardCodeList.size();j++){
                                if(bardCodeList.get(i).equals(bardCodeList.get(j))){
                                    int nu=j+1+5;
                                    // throw new BaseException("第"+nu+"行，商品条码重复，请核实");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(nu);
                                    errMsgResponse.setMsg("明细行重复，请核实");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        }
                    }
                }
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("报错信息",msgList);
            }
            invInventoryDocument.setDocumentCategory("RK");
            invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocumentItems);
            invInventoryDocument.setInitializeStatus(ConstantsEms.YES);
            return AjaxResult.success(invInventoryDocument);

        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
    }
    /**
     * 库存出库初始化
     */
    @Override
    public AjaxResult importDataInvCHK(MultipartFile file){
        Long basStorehouseSid=null;
        Long StorehouseLocationSid=null;
        Long vendorSid=null;
        Long customerSid=null;
        String specialStock=null;
        String isRepeteBacode=null;
        List<Long> bardCodeList = new ArrayList<>();
        try{
            File toFile=null;
            try {
                toFile   = FileUtils.multipartFileToFile(file);
            }catch (Exception e){
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            Map<String,String> specialMaps = conSpecialStockMapper.getList().stream().collect(Collectors.toMap(ConSpecialStock::getName, ConSpecialStock::getCode, (key1, key2) -> key2));
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            ArrayList<InvInventoryDocumentItem> invInventoryDocumentItems = new ArrayList<>();
            List<CommonErrMsgResponse> msgList=new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long sku1Sid=null;
                Long sku2Sid=null;
                Long materialSid=null;
                Long barcodeSid=null;
                String barcode=null;
                String sku1Code=null;
                String sku2Code=null;
                String materialName=null;
                BigDecimal quantity=null;
                BigDecimal price=null;
                Date accountDate=null;
                String unitBase=null;
                BasMaterial basMaterial=null;
                String unitBaseName=null;
                String moveTypeCode=null;
                if (i < 2 || i==3||i==4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("作业类型名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!objects.get(0).toString().equals("其它出库-常规/自采物料")&&!objects.get(0).toString().equals("其它出库-客供料")){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("作业类型配置错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            ConMovementType conMovementType = conMovementTypeMapper.selectOne(new QueryWrapper<ConMovementType>().lambda()
                                    .eq(ConMovementType::getName, objects.get(0).toString())
                            );
                            moveTypeCode=conMovementType.getCode();
                            if(objects.get(0).toString().equals("其它出库-常规库存")){
                                if(objects.get(4) != null && objects.get(4) != ""){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("特殊库存必须为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }
                            if(objects.get(0).toString().equals("其它出库-客供料")){
                                if(objects.get(4)==null ||objects.get(4)==""){
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(3);
                                    errMsgResponse.setMsg("特殊库存不能为空，导入失败");
                                    msgList.add(errMsgResponse);
                                }else{
                                    String special = specialMaps.get(objects.get(4).toString());
                                    if(!ConstantsEms.CUS_RA.equals(special)){
                                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                        errMsgResponse.setItemNum(3);
                                        errMsgResponse.setMsg("特殊库存必须为客供料库存，导入失败");
                                        msgList.add(errMsgResponse);
                                    }
                                }
                            }
                        }
                    }
                    if(CollectionUtil.isNotEmpty(msgList)){
                        return AjaxResult.error("报错信息",msgList);
                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("过账日期，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        boolean validDate = JudgeFormat.isValidDate(objects.get(1).toString());
                        if(!validDate){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("过账日期，格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            String account = objects.get(1).toString();
                            accountDate = DateUtil.parse(account);
                        }
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("仓库，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        BasStorehouse basStorehouse = basStorehouseMapper.selectOne(new QueryWrapper<BasStorehouse>().lambda()
                                .eq(BasStorehouse::getStorehouseName, objects.get(2).toString())
                        );
                        if (basStorehouse == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("没有名称为" + objects.get(2).toString() + "的仓库，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouse.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouse.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("仓库必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            basStorehouseSid = Long.valueOf(basStorehouse.getStorehouseSid());
                        }
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("库位，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        BasStorehouseLocation basStorehouseLocation = basStorehouseLocationMapper.selectOne(new QueryWrapper<BasStorehouseLocation>().lambda()
                                .eq(BasStorehouseLocation::getStorehouseSid, basStorehouseSid)
                                .eq(BasStorehouseLocation::getLocationName, objects.get(3).toString())
                        );
                        if (basStorehouseLocation == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("名称为" + objects.get(2).toString() + "的仓库下没有"+objects.get(3).toString()+"的库位，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basStorehouseLocation.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basStorehouseLocation.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("库位必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            StorehouseLocationSid = Long.valueOf(basStorehouseLocation.getStorehouseLocationSid());
                        }
                    }
                    if(objects.get(4) != null && objects.get(4) != ""){
                        String special = specialMaps.get(objects.get(4).toString());
                        if(special==null){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("特殊库存数据格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            specialStock=special;
                        }
                    }
                    if(objects.get(4)==null ||objects.get(4)==""){
                        if(objects.get(5)!=null&&objects.get(5)!=""){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("供应商简称必须为空，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        if(objects.get(6)!=null&&objects.get(6)!=""){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("客户简称必须为空，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                    if(objects.get(4)!=null&&objects.get(4)!=""){
                        if(ConstantsEms.VEN_RA.equals(specialMaps.get(objects.get(4).toString()))||ConstantsEms.VEN_CU.equals(specialMaps.get(objects.get(4).toString()))){
                            if(objects.get(5)==null||objects.get(5)==""){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("供应商简称不能为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            if(objects.get(6)!=null&&objects.get(6)!=""){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("客户简称必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }else if(ConstantsEms.CUS_RA.equals(specialMaps.get(objects.get(4).toString()))||ConstantsEms.CUS_VE.equals(specialMaps.get(objects.get(4).toString()))){
                            if(objects.get(6)==null||objects.get(6)==""){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("客户简称不能为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            if(objects.get(5)!=null&&objects.get(5)!=""){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("供应商简称必须为空，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                    if (objects.get(5) != "" &&objects.get(5) != null) {
                        String vendorCode = objects.get(5).toString();
                        BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                                .lambda().eq(BasVendor::getShortName, vendorCode)
                        );
                        if (basVendor == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("简称为" + vendorCode + "没有对应的供应商，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basVendor.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basVendor.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("供应商必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            vendorSid = Long.valueOf(basVendor.getVendorSid());
                        }
                    }
                    if (objects.get(6) != ""&&objects.get(6) != null) {
                        String customerCode = objects.get(6).toString();
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                                .lambda().eq(BasCustomer::getShortName, customerCode));
                        if (basCustomer == null) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("简称为" + customerCode + "没有对应的客户，导入失败");
                            msgList.add(errMsgResponse);
                        } else {
                            if(!ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(basCustomer.getStatus())){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(3);
                                errMsgResponse.setMsg("客户必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                            customerSid = basCustomer.getCustomerSid();
                        }
                    }
                    if(objects.get(7) == ""||objects.get(7) == null){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(3);
                        errMsgResponse.setMsg("是否允许重复明细不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!"是".equals(objects.get(7).toString())&&!"否".equals(objects.get(7).toString())){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(3);
                            errMsgResponse.setMsg("是否允许重复明细格式错误，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            isRepeteBacode=objects.get(7).toString();
                        }
                    }
                    invInventoryDocument.setType(ConstantsEms.CHU_KU)
                            .setMovementType(moveTypeCode)
                            .setMovementTypeName(objects.get(0).toString())
                            .setStorehouseSid(basStorehouseSid)
                            .setStorehouseLocationSid(StorehouseLocationSid)
                            .setAccountDate(accountDate)
                            .setSpecialStock((objects.get(4)==""||objects.get(4)==null)?null:specialMaps.get(objects.get(4).toString()))
                            .setCustomerSid(customerSid)
                            .setVendorSid(vendorSid)
                            .setRemark((objects.get(8)==""||objects.get(8)==null)?null:objects.get(8).toString());
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copyItem(objects, readAll);
                int num=i+1;
                // 物料/商品编码
                String materialCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (materialCode == null) {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("物料/商品编码不可为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                // sku1名称
                String sku1Name = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                // sku2名称
                String sku2Name = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                // 数量
                if (objects.get(3) == null || objects.get(3) == "") {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("数量 不可为空，导入失败");
                    msgList.add(errMsgResponse);
                }else{
                    boolean validInt = JudgeFormat.isValidDouble(objects.get(3).toString());
                    if(!validInt){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("数量格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        Double mount = Double.valueOf(objects.get(3).toString());
                        if(mount<=0){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("数量小于等于0，不允许导入，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        quantity=BigDecimal.valueOf(Double.valueOf(objects.get(3).toString())).divide(BigDecimal.ONE,4,BigDecimal.ROUND_HALF_UP);
                    }

                }
                try {
                    if (materialCode != null) {
                        BasMaterialBarcode basMaterialBarcode = basMaterialBarcodeMapper.selectBasMaterialBarcodeListByInvImport(new BasMaterialBarcode()
                                .setMaterialCode(materialCode).setSku1Name(sku1Name).setSku2Name(sku2Name));
                        if (basMaterialBarcode != null && basMaterialBarcode.getBarcodeSid() != null) {
                            // 物料商品信息
                            materialName = basMaterialBarcode.getMaterialName();
                            materialSid = basMaterialBarcode.getMaterialSid();
                            unitBase = basMaterialBarcode.getUnitBase();
                            unitBaseName = basMaterialBarcode.getUnitBaseName();
                            // sku1信息
                            sku1Name = basMaterialBarcode.getSku1Name();
                            sku1Code = basMaterialBarcode.getSku1Code();
                            sku1Sid = basMaterialBarcode.getSku1Sid();
                            // sku2信息
                            sku2Code = basMaterialBarcode.getSku2Code();
                            sku2Name = basMaterialBarcode.getSku2Name();
                            sku2Sid = basMaterialBarcode.getSku2Sid();
                            // 商品条码信息
                            barcodeSid = basMaterialBarcode.getBarcodeSid();
                            barcode = basMaterialBarcode.getBarcode();
                            bardCodeList.add(basMaterialBarcode.getBarcodeSid());
                        }
                        else {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("此组合的商品条码不存在，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                    }
                } catch (TooManyResultsException e) {
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("此组合的商品条码系统中存在多笔，请联系管理员，导入失败！");
                    msgList.add(errMsgResponse);
                }
                InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                invInventoryDocumentItem.setSku1Sid(sku1Sid)
                        .setSku2Sid(sku2Sid)
                        .setSku1Code(sku1Code)
                        .setSku1Name(sku1Name)
                        .setSku2Code(sku2Code)
                        .setSku2Name(sku2Name)
                        .setStorehouseSid(basStorehouseSid)
                        .setStorehouseLocationSid(StorehouseLocationSid)
                        .setUnitBase(unitBase)
                        .setUnitBaseName(unitBaseName)
                        .setBarcode(barcode)
                        .setMaterialSid(materialSid)
                        .setMaterialCode(materialCode)
                        .setMaterialName(materialName)
                        .setBarcodeSid(barcodeSid)
                        .setQuantity(objects.get(3)==null||objects.get(3)==""?null:quantity)
                        .setRemark((objects.get(4)==""||objects.get(4)==null)?null:objects.get(4).toString());
                invInventoryDocumentItems.add(invInventoryDocumentItem);
            }
            if(CollectionUtil.isNotEmpty(bardCodeList)){
                if("否".equals(isRepeteBacode)){
                    HashSet<Long> longs = new HashSet<>(bardCodeList);
                    if(longs.size()!=bardCodeList.size()){
                        for (int i=0;i<bardCodeList.size();i++){
                            for (int j=i+1;j<bardCodeList.size();j++){
                                if(bardCodeList.get(i).equals(bardCodeList.get(j))){
                                    int nu=j+1+5;
                                    // throw new BaseException("第"+nu+"行，商品条码重复，请核实");
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(nu);
                                    errMsgResponse.setMsg("明细行重复，请核实");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        }
                    }
                }
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("报错信息",msgList);
            }
            setItemNum(invInventoryDocumentItems);
            String SpecialStockTemp=specialStock;
            InvInventoryLocation invInventoryLocation = new InvInventoryLocation();
            BeanCopyUtils.copyProperties(invInventoryDocument,invInventoryLocation);
            Map<Long, List<InvInventoryDocumentItem>> listMap = invInventoryDocumentItems.stream().collect(Collectors.groupingBy(v -> v.getBarcodeSid()));
            listMap.keySet().stream().forEach(l->{
                List<InvInventoryDocumentItem> items = listMap.get(l);
                if(items.size()==1){
                    //商品条码不重复情况下
                    items.forEach(m->{
                        InvInventoryLocation locationAble=null;
                        invInventoryLocation.setBarcodeSid(m.getBarcodeSid());
                        if(SpecialStockTemp==null){
                            locationAble = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
                        }else if(ConstantsEms.VEN_CU.equals(SpecialStockTemp) || ConstantsEms.VEN_RA.equals(SpecialStockTemp)){
                            locationAble = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                        }else{
                            locationAble = invCusSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                        }
                        if(locationAble==null){
                            locationAble=new InvInventoryLocation();
                            locationAble.setAbleQuantity(BigDecimal.ZERO);
                        }
                        if(m.getQuantity().compareTo(locationAble.getAbleQuantity())==1){
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(m.getItemNum());
                            errMsgResponse.setMsg("可用库存不足，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    });
                }else{
                    BigDecimal sum = items.stream().map(h -> h.getQuantity()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    InvInventoryDocumentItem note = items.get(0);
                    InvInventoryLocation locationAble=null;
                    invInventoryLocation.setBarcodeSid(note.getBarcodeSid());
                    if(SpecialStockTemp==null){
                        locationAble = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
                    }else if(ConstantsEms.VEN_CU.equals(SpecialStockTemp) || ConstantsEms.VEN_RA.equals(SpecialStockTemp)){
                        locationAble = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                    }else{
                        locationAble = invCusSpecialInventoryMapper.getLocationAble(invInventoryLocation);
                    }
                    if(locationAble==null){
                        locationAble=new InvInventoryLocation();
                        locationAble.setAbleQuantity(BigDecimal.ZERO);
                    }
                    if(sum.compareTo(locationAble.getAbleQuantity())==1){
                        BigDecimal comsum=BigDecimal.ZERO;
                        for (int i = 0; i < items.size(); i++) {
                            comsum=items.get(i).getQuantity().add(comsum);
                            if(comsum.compareTo(locationAble.getAbleQuantity())==1){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(items.get(i).getItemNum());
                                errMsgResponse.setMsg("可用库存不足，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                }
            });
//            for (int i = 0; i <invInventoryDocumentItems.size() ; i++) {
//                InvInventoryLocation locationAble=null;
//                invInventoryLocation.setBarcodeSid(invInventoryDocumentItems.get(i).getBarcodeSid());
//                if(SpecialStockTemp==null){
//                    locationAble = invInventoryLocationMapper.getLocationAble(invInventoryLocation);
//                }else if(ConstantsEms.VEN_CU.equals(SpecialStockTemp) || ConstantsEms.VEN_RA.equals(SpecialStockTemp)){
//                    locationAble = invVenSpecialInventoryMapper.getLocationAble(invInventoryLocation);
//                }else{
//                    locationAble = invCusSpecialInventoryMapper.getLocationAble(invInventoryLocation);
//                }
//                if(locationAble==null){
//                    locationAble=new InvInventoryLocation();
//                    locationAble.setAbleQuantity(BigDecimal.ZERO);
//                }
//                if(invInventoryDocumentItems.get(i).getQuantity().compareTo(locationAble.getAbleQuantity())==1){
//                    int nu=i+6;
//                    // throw new BaseException("第"+nu+"行，商品条码重复，请核实");
//                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
//                    errMsgResponse.setItemNum(nu);
//                    errMsgResponse.setMsg("可用库存不足，导入失败");
//                    msgList.add(errMsgResponse);
//                }
//            }
            invInventoryDocumentItems.forEach(li->li.setItemNum(null));
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("报错信息",msgList);
            }
            invInventoryDocument.setDocumentCategory("CK");
            invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocumentItems);
            invInventoryDocument.setInitializeStatus(ConstantsEms.YES);
         return AjaxResult.success(invInventoryDocument);

        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
    }

    /**
     * 行号赋值
     */
    public void  setItemNum(List<InvInventoryDocumentItem> list){
        int size = list.size();
        if(size>0){
            for (int i=1;i<=size;i++){
                if(list.get(i-1).getItemNum()==null){
                    list.get(i-1).setItemNum(i+5);
                }
            }
        }
    }
    //填充-主表
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
    //填充-明细表
    public void copyItem(List<Object> objects,List<List<Object>> readAll){
        //获取第三行的列数
        int size = readAll.get(3).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i=lineSize;i<size;i++){
            Object o = new Object();
            o=null;
            objects.add(o);
        }
    }

    /**
     * 导入甲供料结算单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importData(MultipartFile file) {
        List<InvInventoryDocumentItem> invInventoryDocumentItemList = new ArrayList<>();
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
            for (int i = 0; i < readAll.size(); i++) {
                if (i == 2) {
                    List<Object> objects = readAll.get(i);

                    //供应商编码
                    String vendorCode = objects.get(0) == null ? null : objects.get(0).toString();
                    if (StrUtil.isEmpty(vendorCode)) {
                        throw new BaseException("供应商编码不能为空");
                    }
                    List<BasVendor> basVendorList =
                            basVendorMapper.selectList(new QueryWrapper<BasVendor>().lambda().eq(BasVendor::getVendorCode, vendorCode));
                    if (CollectionUtils.isEmpty(basVendorList)) {
                        throw new BaseException(objects.get(0).toString() + "供应商编码不存在");
                    }
                    basVendorList.forEach(basVendor -> {
                        invInventoryDocument.setVendorSid(basVendor.getVendorSid());
                    });

                    //作业类型-甲供料结算
                    invInventoryDocument.setMovementType(ConstantsEms.ARMOR_FOR_MATERIALS);

                    //单据日期
                    Date documentDate = DateUtil.parse(objects.get(2) == null ? null : objects.get(2).toString());
                    if (documentDate == null) {
                        throw new BaseException("单据日期不能为空");
                    }
                    invInventoryDocument.setDocumentDate(documentDate);

                    //仓库编码
                    String storehouseCode = objects.get(3) == null ? null : objects.get(3).toString();
                    if (StrUtil.isEmpty(storehouseCode)) {
                        throw new BaseException("仓库编码不能为空");
                    }
                    List<BasStorehouse> basStorehouseList = basStorehouseMapper.selectList(new QueryWrapper<BasStorehouse>().lambda()
                            .eq(BasStorehouse::getStorehouseCode, storehouseCode));
                    if (CollectionUtils.isEmpty(basStorehouseList)) {
                        throw new BaseException(objects.get(3).toString() + "仓库编码不存在");
                    }
                    basStorehouseList.forEach(basStorehouse -> {
                        invInventoryDocument.setStorehouseSid(Long.parseLong(basStorehouse.getStorehouseSid()));
                        //库位编码校验
                        String locationCode = objects.get(4) == null ? null : objects.get(4).toString();

                        if (StrUtil.isEmpty(locationCode)) {
                            throw new BaseException("库位编码不能为空");
                        }
                        List<BasStorehouseLocation> basStorehouseLocationList =
                                basStorehouseLocationMapper.selectList(new QueryWrapper<BasStorehouseLocation>().lambda()
                                        .eq(BasStorehouseLocation::getStorehouseSid, basStorehouse.getStorehouseSid())
                                        .eq(BasStorehouseLocation::getLocationCode, locationCode));
                        if (CollectionUtils.isEmpty(basStorehouseLocationList)) {
                            throw new BaseException("仓库编码为：" + objects.get(3).toString() + "的仓库下无库位编码为：" + objects.get(4).toString() + "的库位");
                        }
                        basStorehouseLocationList.forEach(basStorehouseLocation -> {
                            invInventoryDocument.setStorehouseLocationSid(basStorehouseLocation.getStorehouseLocationSid());
                        });
                    });

                    //特殊库存
                    invInventoryDocument.setSpecialStock(ConstantsEms.VEN_RA);

                    String remark = "";
                    if (objects.size() > 6) {
                        remark = objects.get(6) == null ? null : objects.get(6).toString();
                        invInventoryDocument.setHandleStatus(HandleStatus.SAVE.getCode())
                                            .setType(ConstantsEms.CHU_KU)
                                            .setDocumentCategory(ConstantsEms.DOCUMENT_CATEGORY_OWNER_FEED)
                                            .setRemark(remark);
                    }
                }

                if (i < 5){
                    continue;
                }
                List<Object> objectList = readAll.get(i);
                InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                String materialCode = objectList.get(0) == null ? null : objectList.get(0).toString();
                if (StrUtil.isEmpty(materialCode)) {
                    throw new BaseException("物料/商品编码不能为空");
                }
                List<BasMaterial> basMaterialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                        .eq(BasMaterial::getMaterialCode, materialCode));
                if (CollectionUtils.isEmpty(basMaterialList)) {
                    throw new BaseException(objectList.get(0).toString() + "物料/商品编码不存在");
                }
                basMaterialList.forEach(basMaterial -> {
                    invInventoryDocumentItem.setMaterialSid(basMaterial.getMaterialSid());
                    invInventoryDocumentItem.setUnitBase(basMaterial.getUnitBase());
                });

                String sku1Code = objectList.get(1) == null ? null : objectList.get(1).toString();
                if (StrUtil.isEmpty(sku1Code)) {
                    throw new BaseException("SKU1编码不能为空");
                }
                List<BasSku> basSkuList1 = basSkuMapper.selectList(new QueryWrapper<BasSku>().lambda().eq(BasSku::getSkuCode, sku1Code));
                Long sku1Sid = null;
                Long sku2Sid = null;
                if (CollectionUtils.isEmpty(basSkuList1)) {
                    throw new BaseException(objectList.get(1).toString() + "SKU1编码不存在");
                }
                for (BasSku basSku : basSkuList1) {
                    sku1Sid = basSku.getSkuSid();
                }
                invInventoryDocumentItem.setSku1Sid(sku1Sid);
                String sku2Code = objectList.get(2) == null ? null : objectList.get(2).toString();
                if (StrUtil.isNotEmpty(sku2Code)) {
                    List<BasSku> basSkuList2 = basSkuMapper.selectList(new QueryWrapper<BasSku>().lambda().eq(BasSku::getSkuCode, sku2Code));
                    if (CollectionUtils.isEmpty(basSkuList2)) {
                        throw new BaseException(objectList.get(2).toString() + "SKU2编码不存在");
                    }
                    for (BasSku basSku : basSkuList2) {
                        sku2Sid = basSku.getSkuSid();
                    }
                }
                if (sku2Sid == null){
                    List<BasMaterialBarcode> basMaterialBarcodeList =
                            basMaterialBarcodeMapper.selectList(new QueryWrapper<BasMaterialBarcode>().lambda()
                                    .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                                    .eq(BasMaterialBarcode::getMaterialSid, invInventoryDocumentItem.getMaterialSid()));
                    if (CollectionUtils.isEmpty(basMaterialBarcodeList)){
                        throw new BaseException("物料/商品编码为" + objectList.get(0).toString() + "的商品下不存在" + objectList.get(1).toString() + "的sku1编码");
                    }
                    basMaterialBarcodeList.forEach(basMaterialBarcode -> {
                        invInventoryDocumentItem.setBarcodeSid(basMaterialBarcode.getBarcodeSid());
                    });
                }else {
                    List<BasMaterialBarcode> basMaterialBarcodeList =
                            basMaterialBarcodeMapper.selectList(new QueryWrapper<BasMaterialBarcode>().lambda()
                                    .eq(BasMaterialBarcode::getSku1Sid, sku1Sid)
                                    .eq(BasMaterialBarcode::getSku2Sid, sku2Sid)
                                    .eq(BasMaterialBarcode::getMaterialSid, invInventoryDocumentItem.getMaterialSid()));
                    if (CollectionUtils.isEmpty(basMaterialBarcodeList)){
                        throw new BaseException("物料/商品编码为" + objectList.get(0).toString() + "的商品下不存在" + objectList.get(1).toString() +
                                "的sku1编码或不存在" + objectList.get(2).toString() + "的sku2编码");
                    }
                    basMaterialBarcodeList.forEach(basMaterialBarcode -> {
                        invInventoryDocumentItem.setBarcodeSid(basMaterialBarcode.getBarcodeSid());
                    });
                    invInventoryDocumentItem.setSku2Sid(sku2Sid);
                }

                String amount = objectList.get(3) == null ? null : objectList.get(3).toString();
                BigDecimal quantity = new BigDecimal(amount);
                if (quantity == null || quantity.compareTo(BigDecimal.ZERO) == -1) {
                    throw new BaseException("结数量不能为空或小于0");
                }
                String itemRemark = objectList.get(4) == null ? null : objectList.get(4).toString();
                invInventoryDocumentItem.setQuantity(quantity)
                                        .setRemark(itemRemark);

                invInventoryDocumentItemList.add(invInventoryDocumentItem);
            }
            invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocumentItemList);
            //导入甲供料结算单
            documentServiceImpl.insertInvInventoryDocument(invInventoryDocument);
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return 1;
    }


    /**
     * 库存统计报表 按SKU
     *
     * @param request InvInventoryLocationBarcodeStatisticsForm 仓库库位库存
     */
    @Override
    public List<InvInventoryLocationBarcodeStatisticsForm> selectInvInventoryLocationStatisticsForm(InvInventoryLocationBarcodeStatisticsForm request) {
        return invInventoryLocationMapper.selectInvInventoryLocationBarcodeStatisticsForm(request);
    }

    /**
     * 库存统计报表 按仓库
     *
     * @param request InvInventoryLocationStoreStatisticsForm 仓库库位库存
     */
    @Override
    public List<InvInventoryLocationStoreStatisticsForm> selectInvInventoryLocationStatisticsForm(InvInventoryLocationStoreStatisticsForm request) {
        return invInventoryLocationMapper.selectInvInventoryLocationStoreStatisticsForm(request);
    }

    /**
     * 库存统计报表 按仓库 更新日期
     *
     * @param request InvStorehouseMaterial 仓库物料信息对象

     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateInvStorehouseMaterial(InvStorehouseMaterial request) {
        if (ArrayUtil.isEmpty(request.getStorehouseMaterialSidList())) {
            throw new BaseException("请选择行");
        }
        int row = 0;

        LambdaUpdateWrapper<InvStorehouseMaterial> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(InvStorehouseMaterial::getStorehouseMaterialSid, request.getStorehouseMaterialSidList());
        // 判读是否修改
        boolean flag = false;
        // 最近一次采购入库日期
        if (ConstantsEms.YES.equals(request.getLatestPurchaseEntryDateUpd())) {
            updateWrapper.set(InvStorehouseMaterial::getLatestPurchaseEntryDate, request.getLatestPurchaseEntryDate());
            flag = true;
        }

        // 最近一次生产入库日期
        if (ConstantsEms.YES.equals(request.getLatestManufactEntryDateUpd())) {
            updateWrapper.set(InvStorehouseMaterial::getLatestManufactEntryDate, request.getLatestManufactEntryDate());
            flag = true;
        }

        // 最近一次调拨入库日期
        if (ConstantsEms.YES.equals(request.getLatestTransferEntryDateUpd())) {
            updateWrapper.set(InvStorehouseMaterial::getLatestTransferEntryDate, request.getLatestTransferEntryDate());
            flag = true;
        }

        // 最近一次入库日期
        if (ConstantsEms.YES.equals(request.getLatestStockEntryDateUpd())) {
            updateWrapper.set(InvStorehouseMaterial::getLatestStockEntryDate, request.getLatestStockEntryDate());
            flag = true;
        }

        // 最近一次销售出库日期
        if (ConstantsEms.YES.equals(request.getLatestSaleOutDateUpd())) {
            updateWrapper.set(InvStorehouseMaterial::getLatestSaleOutDate, request.getLatestSaleOutDate());
            flag = true;
        }

        // 最近一次调拨出库日期
        if (ConstantsEms.YES.equals(request.getLatestTransferOutDateUpd())) {
            updateWrapper.set(InvStorehouseMaterial::getLatestTransferOutDate, request.getLatestTransferOutDate());
            flag = true;
        }

        // 最近一次领料出库日期
        if (ConstantsEms.YES.equals(request.getLatestRequisitionOutDateUpd())) {
            updateWrapper.set(InvStorehouseMaterial::getLatestRequisitionOutDate, request.getLatestRequisitionOutDate());
            flag = true;
        }

        // 最近一次领料出库日期
        if (ConstantsEms.YES.equals(request.getLatestStockOutDateUpd())) {
            updateWrapper.set(InvStorehouseMaterial::getLatestStockOutDate, request.getLatestStockOutDate());
            flag = true;
        }

        // 修改
        if (flag) {
            row = invStorehouseMaterialMapper.update(null, updateWrapper);
        }
        return AjaxResult.success(null, row);
    }

    /**
     * 特殊库存统计报表 按SKU
     *
     * @param request InvInventorySpecialBarcodeStatisticsForm 仓库库位库存
     */
    @Override
    public List<InvInventorySpecialBarcodeStatisticsForm> selectInvInventorySpecialStatisticsForm(InvInventorySpecialBarcodeStatisticsForm request) {
        return invInventoryLocationMapper.selectInvInventorySpecialBarcodeStatisticsForm(request);
    }

    /**
     * 特殊库存统计报表 按仓库
     *
     * @param request InvInventorySpecialStoreStatisticsForm 仓库库位库存
     */
    @Override
    public List<InvInventorySpecialStoreStatisticsForm> selectInvInventorySpecialStatisticsForm(InvInventorySpecialStoreStatisticsForm request) {
        return invInventoryLocationMapper.selectInvInventorySpecialStoreStatisticsForm(request);
    }

    /**
     * 移动端库存报表
     */
    @Override
    public List<InvInventoryLocation> selectMobInvLocFormList(InvInventoryLocation inventory) {
        return invInventoryLocationMapper.selectMobInvLocFormList(inventory);
    }

}
