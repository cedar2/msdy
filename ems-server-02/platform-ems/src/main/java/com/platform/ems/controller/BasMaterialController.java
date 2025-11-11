package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.constant.HttpStatus;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;

import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsAuthorize;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.BasMaterialDisabledRequest;
import com.platform.ems.domain.dto.request.MaterialAddRequest;
import com.platform.ems.domain.dto.request.material.BasMaterialSkuRequest;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.domain.dto.response.export.BasMaterialWcyExportResponse;
import com.platform.ems.domain.dto.response.export.BasMaterialYpExportResponse;
import com.platform.ems.domain.dto.response.form.BasMaterialSaleStationCategoryForm;
import com.platform.ems.mapper.BasMaterialBarcodeMapper;
import com.platform.ems.mapper.BasMaterialSaleStationMapper;
import com.platform.ems.service.*;
import com.platform.api.service.RemoteSystemService;
import com.platform.common.core.domain.entity.SysRole;
import com.platform.system.domain.SysRoleMenu;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 物料&商品&服务档案Controller
 *
 * @author linhongwei
 * @date 2021-03-12
 */
@RestController
@RequestMapping("/archive/material")
@Api(tags = "物料&商品&服务档案")
@SuppressWarnings("all")
public class BasMaterialController extends BaseController {

    @Autowired
    private IBasMaterialService basMaterialService;
    @Autowired
    private BasMaterialBarcodeMapper materialBarcodeMapper;
    @Autowired
    private IBasMaterialSkuService basMaterialSkuService;
    @Autowired
    private IBasMaterialBarcodeService basMaterialBarcodeService;
    @Autowired
    private IBasMaterialAttachmentService basMaterialAttachService;
    @Autowired
    private BasMaterialSaleStationMapper basMaterialSaleStationMapper;
    @Autowired
    private IBasMaterialImportService basMaterialImportService;
    @Autowired
    private IBasMaterialUpdateService basMaterialUpdateService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private RemoteSystemService remoteSystemService;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询物料&商品&服务档案列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询物料&商品&服务档案列表", notes = "查询物料&商品&服务档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    public TableDataInfo list(@RequestBody BasMaterial basMaterial) {
        if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory()) || (
            basMaterial.getMaterialCategoryList() != null && basMaterial.getMaterialCategoryList().length != 0 &&
                    CollectionUtil.isNotEmpty(Arrays.stream(basMaterial.getMaterialCategoryList())
                            .filter(o->o.equals(ConstantsEms.MATERIAL_CATEGORY_YP)).collect(Collectors.toList())))) {
            // 创建人数据权限匹配
            dataScope(basMaterial);
        }
        //供应商的账号只能查询到自己的供应商。
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())) {
            List<BasMaterial> list = new ArrayList<>();
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() != null) {
                basMaterial.setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid());
                startPage(basMaterial);
                list = basMaterialService.selectBasMaterialList(basMaterial);
                return getDataTable(list);
            } else {
                return getDataTable(list);
            }
        } else {
            startPage(basMaterial);
            List<BasMaterial> list = basMaterialService.selectBasMaterialList(basMaterial);
            return getDataTable(list);
        }
    }

    /**
     * 创建人权限匹配
     * @param frmSampleReview
     */
    private void dataScope(BasMaterial basMaterial) {
        String perms = ConstantsAuthorize.EMS_MATERIAL_YP_ALL;
        Long[] roleIds = null;
        List<SysRole> roleList = ApiThreadLocalUtil.get().getSysUser().getRoles();
        if (CollectionUtil.isNotEmpty(roleList)){
            roleIds = roleList.stream().map(SysRole::getRoleId).toArray(Long[]::new);
        }
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleIds(roleIds);
        roleMenu.setPerms(perms);
        boolean isAll = true;
        if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())){
            isAll = remoteSystemService.isHavePerms(roleMenu).getData();
        }
        if (!isAll){
            String creatorAccount = ApiThreadLocalUtil.get().getSysUser().getUserName();
            if (creatorAccount != null && !"".equals(creatorAccount)){
                //执行方法
                basMaterial.setCreatorUserName(creatorAccount);
            }
        }
    }

    /**
     * 其它单据添加明细时 分：按款 / 按款色 / 按款色码
     */
    @PostMapping("/item/list")
    @ApiOperation(value = "其它单据添加明细时 分：按款 / 按款色 / 按款色码", notes = "其它单据添加明细时 分：按款 / 按款色 / 按款色码")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    public TableDataInfo itemList(@RequestBody BasMaterial basMaterial) {
        List<BasMaterial> list = new ArrayList<>();
        //供应商的账号只能查询到自己的供应商。
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())) {
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() != null) {
                basMaterial.setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid());
            } else {
                return getDataTable(list);
            }
        }
        if ("K".equals(basMaterial.getDataDimension())) {
            startPage(basMaterial);
            list = basMaterialService.selectBasMaterialList(basMaterial);
            return getDataTable(list);
        }
        else if ("K1".equals(basMaterial.getDataDimension())) {
            BasMaterialSku materialSku = new BasMaterialSku();
            BeanCopyUtils.copyProperties(basMaterial, materialSku);
            startPage(basMaterial);
            list = basMaterialSkuService.selectBasMaterialSku1List(materialSku.setSku1Type(ConstantsEms.SKUTYP_YS));
            return getDataTable(list);
        }
        else if ("K12".equals(basMaterial.getDataDimension())) {
            BasMaterialBarcode barcode = new BasMaterialBarcode();
            BeanCopyUtils.copyProperties(basMaterial, barcode);
            startPage(barcode);
            List<BasMaterialBarcode> barcodeList = basMaterialBarcodeService.selectBasMaterialBarcodeList(barcode);
            TableDataInfo tableDataInfo = getDataTable(barcodeList);
            list = BeanCopyUtils.copyListProperties(barcodeList, BasMaterial::new);
            tableDataInfo.setRows(list);
            return tableDataInfo;
        }
        startPage(basMaterial);
        list = basMaterialService.selectBasMaterialList(basMaterial);
        return getDataTable(list);
    }

    /**
     * 查询物料&商品&服务档案列表
     */
    @PostMapping("/judge/disable")
    @ApiOperation(value = "商品停用校验", notes = "商品停用校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    public AjaxResult list(@RequestBody List<BasMaterialDisabledRequest> list) {
        List<BasMaterial> basMaterials = BeanCopyUtils.copyListProperties(list, BasMaterial::new);
        List<BasMaterialDisabledResponse> msgList = basMaterialService.judgeDisable(basMaterials);
        if (CollectionUtil.isNotEmpty(msgList)) {
            return AjaxResult.success("500", msgList);
        } else {
            return AjaxResult.success(1);
        }
    }

    /**
     * 查询物料档案sku列表
     */
    @PostMapping("/materialSkuList")
    @ApiOperation(value = "查询物料档案sku列表", notes = "查询物料档案sku列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    public TableDataInfo materialSkuList(@RequestBody BasMaterialSkuRequest request) {
        //供应商的账号只能查询到自己的供应商。
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())) {
            List<BasMaterialSku> list = new ArrayList<>();
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() != null) {
                request.setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid()).setHandleStatus(ConstantsEms.CHECK_STATUS);
                startPage(request);
                list = basMaterialService.getBasMaterialSku(request);
                return getDataTable(list);
            } else {
                return getDataTable(list);
            }
        } else {
            startPage(request);
            List<BasMaterialSku> list = basMaterialService.getBasMaterialSku(request);
            return getDataTable(list);
        }
    }

    /**
     * 导出物料&商品&服务档案列表
     */
    @ApiOperation(value = "导出物料&商品&服务档案列表", notes = "导出物料&商品&服务档案列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasMaterial basMaterial) throws IOException {
        if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory()) || (
                basMaterial.getMaterialCategoryList() != null && basMaterial.getMaterialCategoryList().length != 0 &&
                        CollectionUtil.isNotEmpty(Arrays.stream(basMaterial.getMaterialCategoryList())
                                .filter(o->o.equals(ConstantsEms.MATERIAL_CATEGORY_YP)).collect(Collectors.toList())))) {
            // 创建人数据权限匹配
            dataScope(basMaterial);
        }
        List<BasMaterial> list = basMaterialService.selectBasMaterialList(basMaterial);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(basMaterial.getMaterialCategory())) {
            if (ConstantsEms.MATERIAL_M.equals(basMaterial.getMaterialType())) {
                ExcelUtil<BasMaterialWLmianResponse> util = new ExcelUtil<>(BasMaterialWLmianResponse.class, dataMap);
                List<BasMaterialWLmianResponse> basMaterialMResponses = BeanCopyUtils.copyListProperties(list, BasMaterialWLmianResponse::new);
                util.exportExcel(response, basMaterialMResponses, "面料");
            }
            else if (ConstantsEms.MATERIAL_F.equals(basMaterial.getMaterialType())) {
                ExcelUtil<BasMaterialWLfuResponse> util = new ExcelUtil<>(BasMaterialWLfuResponse.class, dataMap);
                List<BasMaterialWLfuResponse> basMaterialMResponses = BeanCopyUtils.copyListProperties(list, BasMaterialWLfuResponse::new);
                util.exportExcel(response, basMaterialMResponses, "辅料");
            }
            else {
                ExcelUtil<BasMaterialMResponse> util = new ExcelUtil<>(BasMaterialMResponse.class, dataMap);
                List<BasMaterialMResponse> basMaterialMResponses = BeanCopyUtils.copyListProperties(list, BasMaterialMResponse::new);
                util.exportExcel(response, basMaterialMResponses, "物料");
            }
        } else if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(basMaterial.getMaterialCategory())) {
            if ("XIEFU".equals(basMaterial.getExportType())) {
                ExcelUtil<BasMaterialGxiefuResponse> util = new ExcelUtil<>(BasMaterialGxiefuResponse.class, dataMap);
                List<BasMaterialGxiefuResponse> basMaterialGxiefuResponse = BeanCopyUtils.copyListProperties(list, BasMaterialGxiefuResponse::new);
                util.exportExcel(response, basMaterialGxiefuResponse, "商品(鞋服)");
            }
            else {
                ExcelUtil<BasMaterialGResponse> util = new ExcelUtil<>(BasMaterialGResponse.class, dataMap);
                List<BasMaterialGResponse> basMaterialMResponses = BeanCopyUtils.copyListProperties(list, BasMaterialGResponse::new);
                util.exportExcel(response, basMaterialMResponses, "商品");
            }
        } else if (ConstantsEms.MATERIAL_CATEGORY_WCY.equals(basMaterial.getMaterialCategory())) {
            ExcelUtil<BasMaterialWcyExportResponse> util = new ExcelUtil<>(BasMaterialWcyExportResponse.class, dataMap);
            List<BasMaterialWcyExportResponse> basMaterialWcyExportResponse = BeanCopyUtils.copyListProperties(list, BasMaterialWcyExportResponse::new);
            util.exportExcel(response, basMaterialWcyExportResponse, "外采样");
        } else if (ConstantsEms.MATERIAL_CATEGORY_YP.equals(basMaterial.getMaterialCategory())) {
            ExcelUtil<BasMaterialYpExportResponse> util = new ExcelUtil<>(BasMaterialYpExportResponse.class, dataMap);
            List<BasMaterialYpExportResponse> basMaterialYpExportResponse = BeanCopyUtils.copyListProperties(list, BasMaterialYpExportResponse::new);
            util.exportExcel(response, basMaterialYpExportResponse, "样品");
        } else if (ConstantsEms.MATERIAL_CATEGORY_FW.equals(basMaterial.getMaterialCategory())) {
            ExcelUtil<BasMaterialSResponse> util = new ExcelUtil<>(BasMaterialSResponse.class, dataMap);
            List<BasMaterialSResponse> basMaterialMResponses = BeanCopyUtils.copyListProperties(list, BasMaterialSResponse::new);
            util.exportExcel(response, basMaterialMResponses, "服务");
        } else {
            ExcelUtil<BasMaterialYpExportResponse> util = new ExcelUtil<>(BasMaterialYpExportResponse.class, dataMap);
            List<BasMaterialYpExportResponse> basMaterialYpExportResponse = BeanCopyUtils.copyListProperties(list, BasMaterialYpExportResponse::new);
            util.exportExcel(response, basMaterialYpExportResponse, "样品");
        }
    }

    /**
     * 获取物料&商品&服务档案详细信息
     */
    @ApiOperation(value = "获取物料&商品&服务档案详细信息", notes = "获取物料&商品&服务档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long materialSid) {
        return AjaxResult.success(basMaterialService.selectBasMaterialById(materialSid));
    }

    @ApiOperation(value = "按款添加-查询", notes = "按款添加-查询")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    @PostMapping("/get/add/k")
    public AjaxResult addItem(@RequestBody MaterialAddRequest request) {
        return AjaxResult.success(basMaterialService.addBodyItem(request));
    }

    @ApiOperation(value = "按款添加-转换成对应的明细信息", notes = "按款添加-转换成对应的明细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    @PostMapping("/get/add/exchange")
    public AjaxResult addItemExchange(@RequestBody MaterialAddResponse data) {
        return AjaxResult.success(basMaterialService.getItem(data));
    }

    /**
     * 新增物料&商品&服务档案
     */
    @ApiOperation(value = "新增物料&商品&服务档案", notes = "新增物料&商品&服务档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @Log(title = "物料&商品&服务档案", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasMaterial basMaterial) {
        int row = basMaterialService.insertBasMaterial(basMaterial);
        return AjaxResult.success(basMaterial);
    }

    /**
     * 修改物料&商品&服务档案
     */
    @ApiOperation(value = "修改物料&商品&服务档案", notes = "修改物料&商品&服务档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @Log(title = "物料&商品&服务档案", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasMaterial basMaterial) {
        int i = basMaterialService.updateBasMaterial(basMaterial);
        if (i == 100) {
            return AjaxResult.success("请按需更新后续相关数据 或 停用相应的商品SKU条码！", 1);
        }
        return AjaxResult.success(i);
    }

    /**
     * 修改物料&商品&服务档案
     */
    @ApiOperation(value = "修改其它单据更新相关信息", notes = "修改物料&修改其它单据更新相关信息&服务档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/updateTo")
    public AjaxResult updateTo(@RequestBody HashMap<String, String> map) {
        return AjaxResult.success(basMaterialUpdateService.updateFromMaterial(map));
    }

    /**
     * 删除物料&商品&服务档案
     */
    @ApiOperation(value = "删除物料&商品&服务档案", notes = "删除物料&商品&服务档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @Log(title = "物料&商品&服务档案", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult delete(@RequestBody Long[] materialSids) {
        basMaterialService.deleteBasMaterialByIds(materialSids);
        return AjaxResult.success();
    }

    /**
     * 根据商品编码获取商品档案详情
     */
    @ApiOperation(value = "根据商品编码获取商品档案详情", notes = "根据商品编码获取商品档案详情")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    @PostMapping("/getInfoByCode")
    public AjaxResult getInfoByCode(@RequestBody BasMaterial basMaterial) {
        String materialCode = basMaterial.getMaterialCode();
        String businessType = basMaterial.getBusinessType();
        return AjaxResult.success(basMaterialService.selectBasMaterialByCode(materialCode, businessType));
    }

    /**
     * 根据商品编码获取商品档案详情
     */
    @ApiOperation(value = "创建bom时校验", notes = "创建bom时校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    @PostMapping("/judge/bom/material")
    public AjaxResult judgeBomCreate(Long materialSid) {
        return AjaxResult.success(basMaterialService.judgeBomCreate(materialSid));
    }

    /**
     * 样品档案查询页面确认前校验
     */
    @PostMapping("/confirm/check")
    @ApiOperation(value = "样品档案查询页面确认前校验", notes = "样品档案查询页面确认前校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirmCheck(@RequestBody BasMaterial basMaterial) {
        return AjaxResult.success(basMaterialService.confirmCheck(basMaterial));
    }

    /**
     * 物料&商品&服务档案确认
     */
    @Log(title = "物料&商品&服务档案", businessType = BusinessType.CHECK)
    @PostMapping("/confirm")
    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @ApiOperation(value = "物料&商品&服务档案确认", notes = "物料&商品&服务档案确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody BasMaterial basMaterial) {
        return AjaxResult.success(basMaterialService.confirm(basMaterial));
    }

    @PostMapping("/change/verify")
    @ApiOperation(value = "物料&商品&服务档案变更", notes = "物料&商品&服务档案变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult changeVerify(@RequestBody @Valid BasMaterial basMaterial) {
        return AjaxResult.success(basMaterialService.changeVerify(basMaterial));
    }

    /**
     * 物料&商品&服务档案变更
     */
    @Log(title = "物料&商品&服务档案", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @ApiOperation(value = "物料&商品&服务档案变更", notes = "物料&商品&服务档案变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid BasMaterial basMaterial) {
        String msg = basMaterialService.change(basMaterial);
        if (StrUtil.isBlank(msg)) {
            msg = "操作成功";
        }
        return AjaxResult.success(msg);
    }

    /**
     * 设置未排产提醒天数 物料&商品&服务档案
     */
    @PostMapping("/setWpcRemindDays")
    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @ApiOperation(value = "物料&商品&服务档案启用/停用", notes = "物料&商品&服务档案启用/停用")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult setWpcRemindDays(@RequestBody BasMaterial basMaterial) {
        return AjaxResult.success(basMaterialService.setWpcRemindDays(basMaterial));
    }

    /**
     * 批量启用/停用物料&商品&服务档案
     */
    @PostMapping("/status")
    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @ApiOperation(value = "物料&商品&服务档案启用/停用", notes = "物料&商品&服务档案启用/停用")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult status(@RequestBody BasMaterial basMaterial) {
        return AjaxResult.success(basMaterialService.status(basMaterial));
    }

    /**
     * 物料&商品&服务档案生成商品条码
     */
    @PostMapping("/createBarcode")
    @Log(title = "物料&商品&服务档案", businessType = BusinessType.INSERT)
    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @ApiOperation(value = "物料&商品&服务档案生成商品条码", notes = "物料&商品&服务档案确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult createBarcode(@RequestBody List<Long> materialSids) {
        return AjaxResult.success(basMaterialService.insertBarcode(materialSids));
    }

    /**
     * 查询商品条码列表，其他单据有需要用到的简洁版
     */
    @PostMapping("/barcodeList")
    @Cacheable(value = "basBarcodeList#3600", keyGenerator = "myKeyGenerator")
    @ApiOperation(value = "查询商品条码列表", notes = "查询商品条码列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialBarcode.class))
    public TableDataInfo getList(@RequestBody BasMaterialBarcode basMaterialBarcode) {
        startPage(basMaterialBarcode);
        List<BasMaterialBarcode> list = basMaterialService.getBasMaterialBarcodeList(basMaterialBarcode);
        return getDataTable(list);
    }

    /**
     * 查询商品条码列表，商品条码查询页面
     */
    @PostMapping("/barcode/list")
//    @Cacheable(value = "barcodeList#3600", keyGenerator = "myKeyGenerator")
    @ApiOperation(value = "查询商品条码列表", notes = "查询商品条码列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialBarcode.class))
    public TableDataInfo list(@RequestBody BasMaterialBarcode basMaterialBarcode) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        basMaterialBarcode.setClientId(ApiThreadLocalUtil.get().getClientId());
        basMaterialBarcode.setPageBegin(basMaterialBarcode.getPageSize()*(basMaterialBarcode.getPageNum()-1));
        List<BasMaterialBarcode> total = materialBarcodeMapper.selectBasMaterialBarcodeListSortCount(basMaterialBarcode);
        if (CollectionUtil.isNotEmpty(total)) {
            rspData.setTotal(total.get(0).getPageSize());
            List<BasMaterialBarcode> list = materialBarcodeMapper.selectBasMaterialBarcodeListSort(basMaterialBarcode);
            rspData.setRows(list);
        }
        else {
            rspData.setTotal(0);
            rspData.setRows(new ArrayList<>());
        }
        return rspData;
    }

    /**
     * 批量启用/停用商品条码
     */
    @PostMapping("/barcode/status")
    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @ApiOperation(value = "物料&商品&服务档案启用/停用", notes = "物料&商品&服务档案启用/停用")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult barcodeStatus(@RequestBody BasMaterialBarcode basMaterialBarcode) {
        return AjaxResult.success(basMaterialService.barcodeStatus(basMaterialBarcode));
    }

    @ApiOperation(value = "导出商品条码列表", notes = "导出商品条码列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/barcodeExport")
    public void barcodeExport(HttpServletResponse response, BasMaterialBarcode basMaterialBarcode) throws IOException {
        List<BasMaterialBarcode> list = basMaterialService.selectBasMaterialBarcodeList(basMaterialBarcode);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasMaterialBarcode> util = new ExcelUtil<>(BasMaterialBarcode.class, dataMap);
        util.exportExcel(response, list, "商品SKU条码");
    }

    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @ApiOperation(value = "设置产品级别", notes = "设置产品级别")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/barcode/setProductLevel")
    public AjaxResult barcodeSetProductLevel(@RequestBody BasMaterialBarcode basMaterialBarcode){
        if (ArrayUtil.isEmpty(basMaterialBarcode.getBarcodeSidList())) {
            throw new BaseException("请选择行！");
        }
        return toAjax(basMaterialBarcodeService.setProductLevel(basMaterialBarcode));
    }

    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @ApiOperation(value = "设置商品SKU编码(ERP)", notes = "设置商品SKU编码(ERP)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/barcode/setErpCode")
    public AjaxResult barcodeSetErpCode(@RequestBody BasMaterialBarcode basMaterialBarcode){
        if (ArrayUtil.isEmpty(basMaterialBarcode.getBarcodeSidList())) {
            throw new BaseException("请选择行！");
        }
        return toAjax(basMaterialBarcodeService.setErpCode(basMaterialBarcode));
    }

    /**
     * 发送邮件 编辑/变更商品的时候调用的接口
     */
    @ApiOperation(value = "发送邮件", notes = "发送邮件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/mailSent")
    public AjaxResult mailSent(@RequestBody BasMaterial basMaterial) {
        return AjaxResult.success(basMaterialService.sent(basMaterial));
    }

    /**
     * 是否创建线用量
     */
    @ApiOperation(value = "是否创建商品线用量", notes = "是否创建线用量")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeIsLine")
    public AjaxResult changeIsLine(@RequestBody List<Long> materialSids) {
        return AjaxResult.success(basMaterialService.changeIsLine(materialSids));
    }

    /**
     * 是否创建BOM
     */
    @ApiOperation(value = "是否创建BOM", notes = "是否创建BOM")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeIsBom")
    public AjaxResult changeIsBom(@RequestBody List<Long> materialSids) {
        return AjaxResult.success(basMaterialService.changeIsBom(materialSids));
    }

    /**
     * 是否创建产前成本核算
     */
    @ApiOperation(value = "是否创建产前成本核算", notes = "是否创建产前成本核算")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeIsCost")
    public AjaxResult changeIsCost(@RequestBody List<Long> materialSids) {
        return AjaxResult.success(basMaterialService.changeIsCost(materialSids));
    }

    /**
     * 是否上传工艺单
     */
    @ApiOperation(value = "是否上传工艺单", notes = "是否上传工艺单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeIsUploadGyd")
    public AjaxResult changeIsUploadGyd(@RequestBody List<Long> materialSids) {
        return AjaxResult.success(basMaterialService.changeIsUploadGyd(materialSids));
    }

    /**
     * 变更档案类别字段
     */
    @ApiOperation(value = "变更档案类别字段", notes = "变更档案类别字段（如：样品YP->商品SP）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/category")
    public AjaxResult changeCategory(@RequestBody BasMaterial basMaterial) {
        if (ArrayUtil.isEmpty(basMaterial.getMaterialSidList())) {
            throw new BaseException("请选择行");
        }
        if (StrUtil.isBlank(basMaterial.getMaterialCategory())) {
            throw new BaseException("参数丢失");
        }
        return toAjax(basMaterialService.changeCategory(basMaterial));
    }

    /**
     * 复制物料&商品&服务档案详细信息
     */
    @ApiOperation(value = "复制物料&商品&服务档案详细信息", notes = "复制物料&商品&服务档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    @PostMapping("/copyInfo")
    public AjaxResult copyInfo(@RequestBody Long materialSid) {
        return AjaxResult.success(basMaterialService.copyBasMaterialById(materialSid));
    }

    /**
     * 设置快反款
     */
    @ApiOperation(value = "设置快反款", notes = "设置快反款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setKuaiFan")
    public AjaxResult setKuaiFan(@RequestBody BasMaterial basMaterial) {
        return toAjax(basMaterialService.setKuaiFan(basMaterial));
    }

    /**
     * 设置我方跟单员
     */
    @ApiOperation(value = "设置我方跟单员", notes = "设置我方跟单员")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setOperator")
    public AjaxResult setOperator(@RequestBody BasMaterial basMaterial) {
        return toAjax(basMaterialService.setOperator(basMaterial));
    }

    /**
     * 设置供方业务员
     */
    @ApiOperation(value = "设置供方业务员", notes = "设置供方业务员")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setOperatorVendor")
    public AjaxResult setOperatorVendor(@RequestBody BasMaterial basMaterial) {
        return toAjax(basMaterialService.setOperatorVendor(basMaterial));
    }

    /**
     * 设置客方业务员
     */
    @ApiOperation(value = "设置客方业务员", notes = "设置客方业务员")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setOperatorCustomer")
    public AjaxResult setOperatorCustomer(@RequestBody BasMaterial basMaterial) {
        return toAjax(basMaterialService.setOperatorCustomer(basMaterial));
    }

    /**
     * 设置商品编码(款号)
     */
    @ApiOperation(value = "设置商品编码(款号)", notes = "设置商品编码(款号)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setMaterialCode")
    public AjaxResult setMaterialCode(@RequestBody BasMaterial basMaterial) {
        return toAjax(basMaterialService.setMaterialCode(basMaterial));
    }

    /**
     * 设置 负责生产工厂sid(默认)
     */
    @ApiOperation(value = "设置负责生产工厂sid(默认)", notes = "设置负责生产工厂sid(默认)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setProducePlant")
    public AjaxResult setProducePlant(@RequestBody BasMaterial basMaterial) {
        return toAjax(basMaterialService.setProducePlant(basMaterial));
    }

    /**
     * 设置物料/商品分类
     */
    @ApiOperation(value = "设置物料/商品分类", notes = "设置物料/商品分类")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setMaterialClass")
    public AjaxResult setMaterialClass(@RequestBody BasMaterial basMaterial) {
        return toAjax(basMaterialService.setMaterialClass(basMaterial));
    }

    /**
     * 设置库存价核算方法
     */
    @ApiOperation(value = "设置库存价核算方法", notes = "设置库存价核算方法")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setInventoryMethod")
    public AjaxResult setInventoryMethod(@RequestBody BasMaterial basMaterial) {
        return toAjax(basMaterialService.setInventoryMethod(basMaterial));
    }

    /**
     * 物料图片上传接口
     */
    @ApiOperation(value = "物料图片上传", notes = "物料图片上传")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setPicture")
    public AjaxResult setPicturePath(@RequestBody BasMaterial basMaterial) {
        return toAjax(basMaterialService.setPicture(basMaterial));
    }

    /**
     * 物料多图片上传接口
     */
    @ApiOperation(value = "物料多图片上传", notes = "物料多图片上传")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setPictures")
    public AjaxResult setPicturePaths(@RequestBody BasMaterial basMaterial) {
        return toAjax(basMaterialService.setPictures(basMaterial));
    }

    /**
     * 物料多图片上传接口
     */
    @ApiOperation(value = "物料多图片上传", notes = "物料多图片上传")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/detail")
    public AjaxResult selectBasMaterialDetail(Long materialSid) {
        return AjaxResult.success(basMaterialService.selectBasMaterialPicture(materialSid));
    }

    @ApiOperation(value = "获取物料&商品&服务档案详细信息", notes = "获取物料&商品&服务档案详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterial.class))
    @PostMapping("/getList")
    public AjaxResult getMaterialList(@RequestBody BasMaterial basMaterial) {
        //供应商的账号只能查询到自己的供应商。
        if (ConstantsEms.USER_ACCOUNT_TYPE_GYS.equals(ApiThreadLocalUtil.get().getSysUser().getAccountType())) {
            List<BasMaterial> list = new ArrayList<>();
            if (ApiThreadLocalUtil.get().getSysUser().getVendorSid() != null) {
                basMaterial.setVendorSid(ApiThreadLocalUtil.get().getSysUser().getVendorSid());
            } else {
                return AjaxResult.success(list);
            }
        }
        return AjaxResult.success(basMaterialService.selectMaterialList(basMaterial));
    }

    /**
     * 查询页面 上传附件前的校验
     */
    @ApiOperation(value = "查询页面-上传附件前的校验", notes = "查询页面-上传附件前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkAttach")
    public AjaxResult checkAttach(@RequestBody BasMaterialAttachment basMaterialAttachment) {
        return basMaterialAttachService.check(basMaterialAttachment);
    }

    @ApiOperation(value = "新增物料&商品&服务档案上传附件", notes = "新增物料&商品&服务档案上传附件")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/addAttach")
    public AjaxResult addAttachment(@RequestBody @Valid BasMaterialAttachment basMaterialAttachment) {
        return AjaxResult.success(basMaterialAttachService.insertBasMaterialAttachment(basMaterialAttachment));
    }

    @ApiOperation(value = "新增网店运营信息", notes = "新增网店运营信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/saleStation/add")
    public AjaxResult addSaleStation(@RequestBody BasMaterialSaleStation basMaterialSaleStation) {
        return AjaxResult.success(basMaterialSaleStationMapper.insert(basMaterialSaleStation));
    }

    @ApiOperation(value = "新增网店运营信息", notes = "新增网店运营信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/saleStation/setOperateStatus")
    public AjaxResult saleStationSetOperateStatus(@RequestBody BasMaterialSaleStation basMaterialSaleStation) {
        return AjaxResult.success(basMaterialSaleStationMapper.updateAllById(basMaterialSaleStation));
    }

    @ApiOperation(value = "新增网店运营信息", notes = "新增网店运营信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/saleStation/delete")
    public AjaxResult deleteSaleStation(@RequestBody Long[] materialSaleStationSids) {
        if (ArrayUtil.isEmpty(materialSaleStationSids)) {
            throw new BaseException("请选择行");
        }
        return AjaxResult.success(basMaterialSaleStationMapper.deleteBatchIds(Arrays.asList(materialSaleStationSids)));
    }

    /**
     * 物料/商品编码重复校验 在输入完编码就调用
     */
    @ApiOperation(value = "物料/商品编码重复校验 在输入完编码就调用", notes = "物料/商品编码重复校验 在输入完编码就调用")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkCode")
    public AjaxResult checkCode(@RequestBody BasMaterial basMaterial) {
        return AjaxResult.success((Object) basMaterialService.checkCode(basMaterial));
    }

    /**
     * 供应商+供方编码重复校验 (在新建编辑变更前调用) （物料）
     */
    @ApiOperation(value = "供应商+供方编码重复校验", notes = "供应商+供方编码重复校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkVendor")
    public AjaxResult checkVendor(@RequestBody BasMaterial basMaterial) {
        return basMaterialService.checkVendor(basMaterial);
    }

    /**
     * 我司样衣号重复校验 (在暂存和确认前调用) （商品）
     */
    @ApiOperation(value = "商品我司样衣号重复校验", notes = "商品我司样衣号重复校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/checkSelfCode")
    public AjaxResult checkSelfCode(@RequestBody BasMaterial basMaterial) {
        try {
            basMaterialService.checkSelfCode(basMaterial);
        } catch (Exception e) {
            return AjaxResult.success(e.getMessage());
        }
        return AjaxResult.success(true);
    }

    /**
     * 点击“新增行”按钮时，判断此商品的BOM处理状态是否是“审批中”，如是，则提示错误信息：此商品BOM正在审批中，不允许加色，请先将此商品的BOM驳回。
     * 点击“启用/停用”时，判断此商品的BOM处理状态是否是“审批中”，如是，则提示错误信息：此商品BOM正在审批中，不允许启用/停用颜色，请先将此商品的BOM驳回。
     */
    @ApiOperation(value = "判断此商品的BOM处理状态是否是审批中true/false", notes = "判断此商品的BOM处理状态是否是审批中true/false")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check/bomApproval")
    public AjaxResult checkBomApproval(Long materialSid) {
        try {
            basMaterialService.checkBomApproval(materialSid);
        } catch (Exception e) {
            return AjaxResult.success((Object) false);
        }
        return AjaxResult.success((Object) true);
    }

    /**
     * 标签信息
     */
    @ApiOperation(value = "商品条码标签信息", notes = "商品条码标签信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/barcode/label")
    public AjaxResult getBarcodeLabelInfo(@RequestBody Long barcode) {
        return AjaxResult.success(basMaterialService.getBarcodeLabelInfo(barcode));
    }

    /**
     * 标签信息
     */
    @ApiOperation(value = "标签信息", notes = "标签信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/label")
    public AjaxResult getLabelInfo(@RequestBody Long materialSid) {
        return AjaxResult.success(basMaterialService.getLabelInfo(materialSid));
    }

    /**
     * 报表中心 类目明细报表
     */
    @PostMapping("/sale/station/category/list")
    @ApiOperation(value = "报表中心类目明细报表", notes = "报表中心类目明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialSaleStationCategoryForm.class))
    public TableDataInfo saleStationCategory(@RequestBody BasMaterialSaleStationCategoryForm request) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setTotal(0);
        int pageNum = request.getPageNum();
        // 得到总数
        request.setPageNum(null);
        List<BasMaterialSaleStationCategoryForm> total = basMaterialService.selectBasMaterialSaleStationCategoryFormList(request);
        rspData.setRows(total);
        if (CollectionUtils.isNotEmpty(total)) {
            // 得到分页后的数据
            request.setPageNum(pageNum);
            List<BasMaterialSaleStationCategoryForm> list = basMaterialService.selectBasMaterialSaleStationCategoryFormList(request);
            rspData.setRows(list);
            rspData.setTotal(total.size());
        }
        return rspData;
    }

    @ApiOperation(value = "导出报表中心类目明细报表", notes = "导出报表中心类目明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/sale/station/category/export")
    public void saleStationCategoryExport(HttpServletResponse response, BasMaterialSaleStationCategoryForm request) throws IOException {
        request.setPageNum(null);
        List<BasMaterialSaleStationCategoryForm> list = basMaterialService.selectBasMaterialSaleStationCategoryFormList(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasMaterialSaleStationCategoryForm> util = new ExcelUtil<>(BasMaterialSaleStationCategoryForm.class, dataMap);
        util.exportExcel(response, list, "类目SPU汇总报表");
    }

    /**
     * 报表中心 类目明细报表 查看详情
     */
    @PostMapping("/sale/station/list")
    @ApiOperation(value = "报表中心类目明细报表查看详情", notes = "报表中心类目明细报表查看详情")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasMaterialSaleStation.class))
    public TableDataInfo saleStationList(@RequestBody BasMaterialSaleStation request) {
        startPage(request);
        List<BasMaterialSaleStation> saleStationList = basMaterialService.selectBasMaterialSaleStationList(request);
        return getDataTable(saleStationList);
    }

    /**
     * 物料&商品&服务档案生成商品条码
     */
    @PostMapping("/barcode/import")
    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @ApiOperation(value = "物料&商品&服务档案生成商品条码", notes = "物料&商品&服务档案确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importBarcodeByMaterialCode(@RequestParam MultipartFile file, @RequestParam String materialCategory) {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        HashMap<String, Object> response = basMaterialBarcodeService.importData(file, materialCategory);
        if (null != response.get("warn")){
            return AjaxResult.error("导入提示", response);
        }else {
            if (null != response.get("result")){
                return toAjax((int)response.get("result"));
            }
        }
        return toAjax(0);
    }

    /**
     * 文件导入更新商品SKU编码(ERP)
     */
    @Caching(evict = {
            @CacheEvict(value = "basBarcodeList", allEntries = true),
            @CacheEvict(value = "barcodeList", allEntries = true)
    })
    @ApiOperation(value = "文件导入更新商品SKU编码(ERP)", notes = "文件导入更新商品SKU编码(ERP)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/barcode/importErpCode")
    public AjaxResult barcodeImportErpCode(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(basMaterialBarcodeService.importErpCode(file));
    }

    /**
     * 导入模板的数据批量写入
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入模板的数据批量写入", notes = "导入模板的数据批量写入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataFL(@RequestBody List<BasMaterial> basMaterialList) throws Exception {
        if (basMaterialList == null) {
            return AjaxResult.error("导入失败");
        }
        return toAjax(basMaterialImportService.importDataSecond(basMaterialList));
    }

    /**
     * 导入物料-常规档案
     */
    @PostMapping("/import/cg")
    @ApiOperation(value = "导入物料-常规档案", notes = "导入物料-常规档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataCg(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        HashMap<String, Object> response = basMaterialImportService.importDataCg(file);
        if (null != response.get("success")) {
            return toAjax((int) response.get("success"));
        }
        Object err = response.get("errList");
        if (err instanceof Collection) {
            return AjaxResult.error("导入失败", response);
        } else {
            return AjaxResult.success(response);
        }
    }

    /**
     * 导入物料-辅料档案
     */
    @PostMapping("/import/FL")
    @ApiOperation(value = "导入物料-辅料档案", notes = "导入物料-辅料档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        HashMap<String, Object> response = basMaterialImportService.importData(file);
        if (null != response.get("success")) {
            return toAjax((int) response.get("success"));
        }
        Object err = response.get("errList");
        if (err instanceof Collection) {
            return AjaxResult.error("导入失败", response);
        } else {
            return AjaxResult.success(response);
        }
    }

    /**
     * 导入物料-辅料档案
     */
    @PostMapping("/import/ML")
    @ApiOperation(value = "导入物料-面料档案", notes = "导入物料-面料档案")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataM(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        HashMap<String, Object> response = basMaterialImportService.importDataM(file);
        if (null != response.get("success")) {
            return toAjax((int) response.get("success"));
        }
        Object err = response.get("errList");
        if (err instanceof Collection) {
            return AjaxResult.error("导入失败", response);
        } else {
            return AjaxResult.success(response);
        }
    }

    /**
     * 导入商品(鞋服)
     */
    @PostMapping("/import/G")
    @ApiOperation(value = "导入商品(鞋服)", notes = "导入商品(鞋服)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataG(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        HashMap<String, Object> response = basMaterialImportService.importDataG(file);
        if (null != response.get("success")) {
            return AjaxResult.success(EmsResultEntity.success((int) response.get("success"), String.valueOf(response.get("message"))));
        }
        Object err = response.get("errList");
        if (err != null && err instanceof Collection) {
            if (response.containsKey("warn") && response.get("warn") != null)
            {
                return AjaxResult.success(EmsResultEntity.warning(response.get("tableData"), (List<CommonErrMsgResponse>) err, null));
            }
            return AjaxResult.success(EmsResultEntity.error((List<CommonErrMsgResponse>) err));
        } else {
            return AjaxResult.success(EmsResultEntity.success((int) response.get("success"), String.valueOf(response.get("message"))));
        }
    }

    /**
     * 导入普通商品
     */
    @PostMapping("/import/p")
    @ApiOperation(value = "导入普通商品", notes = "导入普通商品")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataProduct(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        HashMap<String, Object> response = basMaterialImportService.importDataProduct(file);
        if (null != response.get("success")) {
            return AjaxResult.success(EmsResultEntity.success((int) response.get("success"), String.valueOf(response.get("message"))));
        }
        Object err = response.get("errList");
        if (err != null && err instanceof Collection) {
            if (response.containsKey("warn") && response.get("warn") != null)
            {
                return AjaxResult.success(EmsResultEntity.warning(response.get("tableData"), (List<CommonErrMsgResponse>) err, null));
            }
            return AjaxResult.success(EmsResultEntity.error((List<CommonErrMsgResponse>) err));
        } else {
            return AjaxResult.success(EmsResultEntity.success((int) response.get("success"), String.valueOf(response.get("message"))));
        }
    }

    /**
     * 更新数据导入 运营状态
     */
    @PostMapping("/sale/station/update/import")
    @ApiOperation(value = "更新数据导入运营状态", notes = "更新数据导入运营状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importUpdateSaleStation(MultipartFile file) throws Exception{
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(basMaterialImportService.importUpdateSaleStation(file));
    }

    /**
     * 下载模板
     */
    private void downloadTemplate(HttpServletResponse response, HttpServletRequest request, String target) {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/" + target;
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(target, "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        }
    }

    /**
     * 下载更新运营状态导入导入模板
     */
    @ApiOperation(value = "下载更新运营状态导入导入模板", notes = "下载更新运营状态导入导入模板")
    @PostMapping("/sale/station/update/import/template")
    public void importUpdateTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        downloadTemplate(response, request, "协服SCM_导入模板_运营状态_V1.0.xlsx");
    }

    @ApiOperation(value = "下载物料档案-常规物料导入模板", notes = "下载物料-常规物料导入模板")
    @PostMapping("/importTemplate/cg")
    public void importTemplateCg(HttpServletResponse response, HttpServletRequest request) throws IOException {
        downloadTemplate(response, request, "鞋服SCM_导入模板_常规物料档案_V1.0.xlsx");
    }

    @ApiOperation(value = "下载普通商品导入模板", notes = "下载普通商品导入模板")
    @PostMapping("/importTemplate/p")
    public void importTemplateProduct(HttpServletResponse response, HttpServletRequest request) throws IOException {
        downloadTemplate(response, request, "协服SCM_导入模板_常规商品档案_V1.0.xlsx");
    }

    @ApiOperation(value = "下载物料档案-辅料导入模板", notes = "下载物料-辅料导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        downloadTemplate(response, request, "EMS软件_导入模板_物料(辅料)_V0.1.xlsx");
    }

    @ApiOperation(value = "下载物料-面料档案导入模板", notes = "下载物料-面料导入模板")
    @PostMapping("/importTemplate/M")
    public void importTemplateM(HttpServletResponse response, HttpServletRequest request) throws IOException {
        downloadTemplate(response, request, "EMS软件_导入模板_物料(面料)_V0.1(1).xlsx");
    }

    @ApiOperation(value = "下载商品(鞋服)档案导入模板", notes = "下载商品(鞋服)导入模板")
    @PostMapping("/importTemplate/G")
    public void importTemplateG(HttpServletResponse response, HttpServletRequest request) throws IOException {
        downloadTemplate(response, request, "EMS软件_导入模板_商品_V0.1.xlsx");
    }

    @ApiOperation(value = "下载文件导入更新商品SKU编码(ERP)", notes = "文件导入更新商品SKU编码(ERP)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/barcode/importErpCode/downloadTemplate")
    public void barcodeImportErpCodeDownload(HttpServletResponse response, HttpServletRequest request) throws IOException {
        downloadTemplate(response, request, "协服PDM_商品SKU编码(ERP)_导入模板_V1.0.xlsx");
    }

    @ApiOperation(value = "下载批量更新商品条码的导入模板", notes = "下载批量更新商品条码的导入模板")
    @PostMapping("/barcode/importTemplate")
    public void importTemplateBarcode(HttpServletResponse response, HttpServletRequest request) throws IOException {
        downloadTemplate(response, request, "商品条码批量更新模板.xlsx");
    }

    /**
     * 商品条码导入商品条形码
     */
    @PostMapping("/barcode/import/shapeCode")
    @ApiOperation(value = "商品条码导入商品条形码", notes = "商品条码导入商品条形码")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importBarcodeShapeCode(MultipartFile file) throws Exception {
        return AjaxResult.success(basMaterialBarcodeService.importBarcodeShapeCode(file));
    }

    @ApiOperation(value = "下载商品条码导入商品条形码", notes = "下载商品条码导入商品条形码")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/barcode/import/shapeCode/downloadTemplate")
    public void importBarcodeShapeCodeDownload(HttpServletResponse response, HttpServletRequest request) throws IOException {
        downloadTemplate(response, request, "SCM_导入模板_商品条形码_V1.0.xlsx");
    }

}
