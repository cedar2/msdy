package com.platform.ems.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.material.BasMaterialSkuRequest;
import com.platform.ems.domain.dto.response.ListSeasonResponse;
import com.platform.ems.plug.domain.ConBuTypePurchaseRequire;
import com.platform.ems.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品尺寸Controller
 *
 * @author olive
 * @date 2021-02-21
 */
@RestController
@RequestMapping("/productSize")
@Api(tags = "商品尺寸表")
public class TecMaterialSizeController extends BaseController {

    @Autowired
    private ITecMaterialSizeService tecMaterialSizeService;
    @Autowired
    private ITecMaterialPosInforService materialPosInforService;
    @Autowired
    private ITecMaterialPosInforDownService posInforDownService;
    @Autowired
    private ITecMaterialPosSizeDownService posSizeDownService;
    @Autowired
    private IBasMaterialService basMaterialService;
    @Autowired
    private ISBasProductSeasonService sBasProductSeasonService;
    @Autowired
    private ITecModelService modelSystemService;
    @Autowired
    private IBasSkuGroupService basSkuGroupService;
    @Autowired
    private ITecMaterialPosSizeService materialPosSizeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private ISystemUserService userService;

    private static final String LOCK_KEY = "MATERIALSIZE_STOCK";

    /**
     * 查询商品尺寸列表
     */
    // @PreAuthorize(hasPermi = "ems:size:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询商品尺寸列表", notes = "查询商品尺寸列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecMaterialSize.class))
    public TableDataInfo list(@RequestBody TecMaterialSize tecMaterialSize) {
        startPage(tecMaterialSize);
        List<TecMaterialSize> sizeList = tecMaterialSizeService.selectTecMaterialSizeList(tecMaterialSize);
        SysFormProcess formProcess = new SysFormProcess();
        for(TecMaterialSize size : sizeList){
            formProcess.setFormId(size.getMaterialSizeSid());
            List<SysFormProcess> list = formProcessService.selectSysFormProcessList(formProcess);
            if(list!=null&&list.size()>0) {
                formProcess = new SysFormProcess();
                formProcess = list.get(0);
                size.setApprovalNode(formProcess.getApprovalNode());
                size.setApprovalUserName(formProcess.getApprovalUserName());
                size.setSubmitDate(formProcess.getCreateDate());
                size.setSubmitUserName(userService.selectSysUserById(Long.valueOf(formProcess.getCreateById())).getNickName());
            }
        }
        return getDataTable(sizeList);
    }


    @Log(title = "商品尺寸", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品尺寸列表", notes = "导出商品尺寸列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecMaterialSize request) throws IOException {
        List<TecMaterialSize> list = tecMaterialSizeService.selectTecMaterialSizeList(request);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<TecMaterialSize> util = new ExcelUtil<>(TecMaterialSize.class, dataMap);
        util.exportExcel(response, list, "商品尺寸_" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品尺寸", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody TecMaterialSize materialSize) {
        if (ArrayUtil.isEmpty(materialSize.getMaterialSizeSidList()) || CharSequenceUtil.isEmpty(materialSize.getStatus())) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(tecMaterialSizeService.changeStatus(materialSize));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品尺寸", businessType = BusinessType.UPDATE)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody TecMaterialSize materialSize) {
        if (ArrayUtil.isEmpty(materialSize.getMaterialSizeSidList())) {
            throw new CheckedException("参数缺失");
        }
        materialSize.setConfirmDate(new Date());
        materialSize.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        materialSize.setHandleStatus(ConstantsEms.CHECK_STATUS);
        return toAjax(tecMaterialSizeService.check(materialSize));
    }


    /**
     * 新增商品尺寸
     */
    //@PreAuthorize(hasPermi = "ems:size:add")
    @Log(title = "商品尺寸", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "新增商品尺寸", notes = "新增商品尺寸")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult add(@RequestBody @Valid TecMaterialSize materialSize) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        lock.lock(20L, TimeUnit.SECONDS);
        int i = 0;
        try {
            if (CharSequenceUtil.isEmpty(materialSize.getMaterialCode())) {
                throw new CheckedException("商品编码参数缺失！");
            }
            //商品尺寸表
            Long materialSid = materialSize.getMaterialSid();
            if (tecMaterialSizeService.isExist(materialSid)) {
                return AjaxResult.error("该商品档案的尺寸信息已存在！");
            }
            if (!materialSize.getStatus().equals(ConstantsEms.ENABLE_STATUS)) {
                return AjaxResult.error("已启用的商品才能建立商品尺寸表！");
            }
            String username = ApiThreadLocalUtil.get().getUsername();
            String clientId = ApiThreadLocalUtil.get().getSysUser().getClientId();
            Date date = new Date();
            Long materialSizeSid = IdWorker.getId();
            if (ConstantsEms.CHECK_STATUS.equals(materialSize.getHandleStatus())) {
                materialSize.setConfirmDate(new Date());
                materialSize.setConfirmerAccount(username);
            }
            /* 尺寸表id */
            materialSize.setMaterialSizeSid(materialSizeSid);
            /* 物料档案id */
            materialSize.setCreateDate(date);
            materialSize.setClientId(clientId);
            materialSize.setCreatorAccount(username);
            i = tecMaterialSizeService.insertTecMaterialSize(materialSize);
            //商品尺寸部位表
            List<TecMaterialPosInfor> productPosInfoRequests = materialSize.getPosInforList();
            for (TecMaterialPosInfor materialPosInfor : productPosInfoRequests) {
                //插入部位
                String materialPosInforSid = IdWorker.getIdStr();
                /* 物料档案id*/
                //            materialPosInfor.setMaterialSid(materialSize.getMaterialSid());
                /*  商品尺寸表id*/
                materialPosInfor.setMaterialSizeSid(materialSizeSid);
                /* 商品尺寸部位id */
                materialPosInfor.setMaterialPosInforSid(materialPosInforSid);
                materialPosInfor.setClientId(clientId);
                materialPosInfor.setCreateDate(date);
                materialPosInfor.setCreatorAccount(username);
                i &= materialPosInforService.insertTecMaterialPosInfor(materialPosInfor);
                //商品尺寸部位尺寸表
                List<TecMaterialPosSize> productPosSizeRequests = materialPosInfor.getPosSizeList();
                for (TecMaterialPosSize materialPosSize : productPosSizeRequests) {
                    //插入尺寸
                    String materialPosSizeSid = IdWorker.getIdStr();
                    materialPosSize.setMaterialPosInforSid(materialPosInforSid);
                    materialPosSize.setMaterialPosSizeSid(materialPosSizeSid);
                    materialPosSize.setClientId(clientId);
                    materialPosSize.setCreateDate(date);
                    materialPosSize.setCreatorAccount(username);
                    i &= materialPosSizeService.insertTecMaterialPosSize(materialPosSize);
                }
            }
            //下装部位信息
            List<TecMaterialPosInforDown> posInforDownList = materialSize.getPosInforDownList();
            Optional<List<TecMaterialPosInforDown>> downList = Optional.ofNullable(posInforDownList);
            downList.ifPresent(d -> {
                d.forEach(down -> {
                    Long downSid = IdWorker.getId();
                    down.setMaterialPosInforSid(downSid);
                    down.setMaterialSizeSid(Long.valueOf(materialSizeSid));
                    down.setCreateDate(date);
                    posInforDownService.insertTecMaterialPosInforDown(down);
                    List<TecMaterialPosSizeDown> posSizeDownList = down.getPosSizeDownList();
                    posSizeDownList.forEach(size -> {
                        size.setMaterialPosInforSid(downSid);
                        size.setCreateDate(date);
                        size.setCreatorAccount(username);
                        posSizeDownService.insertTecMaterialPosSizeDown(size);
                    });
                });
            });
        } catch (CustomException e) {
            throw new CustomException(e.getMessage());
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return toAjax(i);
    }

    /**
     * 获取商品尺寸详细信息
     */
    //@PreAuthorize(hasPermi = "ems:size:query")
    @PostMapping("/getInfo")
    @ApiOperation(value = "获取商品尺寸详细信息", notes = "获取商品尺寸详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecMaterialSize.class))
    public AjaxResult getInfo(Long materialSizeSid) {
        if (materialSizeSid == null) {
            throw new CheckedException("参数缺失");
        }
        //商品尺寸表
        TecMaterialSize materialSize = tecMaterialSizeService.selectTecMaterialSizeById(materialSizeSid);
        if (materialSize == null) {
            return AjaxResult.error("获取详情失败");
        }
        //商品档案sku明细
        List<BasMaterialSku> materialSkuList = basMaterialService.getBasMaterialSku(new BasMaterialSkuRequest().setMaterialSid(materialSize.getMaterialSid()).setSkuType("2"));
        materialSize.setMaterialSkuList(materialSkuList);
        //商品尺寸部位表
        TecMaterialPosInfor materialPosInfoQuery = new TecMaterialPosInfor();
        materialPosInfoQuery.setMaterialSizeSid(materialSizeSid);
        List<TecMaterialPosInfor> materialPosInfos = materialPosInforService.selectTecMaterialPosInforList(materialPosInfoQuery);
        List<TecMaterialPosInfor> materialPosInforList = new ArrayList<>();
        for (TecMaterialPosInfor materialPosInfo : materialPosInfos) {
            //商品尺寸部位尺寸表
            TecMaterialPosSize materialPosSizeQuery = new TecMaterialPosSize();
            materialPosSizeQuery.setMaterialPosInforSid(materialPosInfo.getMaterialPosInforSid());
            List<TecMaterialPosSize> materialPosSizes = materialPosSizeService.selectTecMaterialPosSizeList(materialPosSizeQuery);
            materialPosInfo.setPosSizeList(materialPosSizes);
            materialPosInforList.add(materialPosInfo);
        }
        //有序
        List<TecMaterialPosInfor> withSerial = materialPosInforList.stream().filter(item -> item.getSerialNum() != null).collect(Collectors.toList());
        //有序
        List<TecMaterialPosInfor> noSerial = materialPosInforList.stream().filter(item -> item.getSerialNum() == null).collect(Collectors.toList());
        ArrayList<TecMaterialPosInfor> newMaterialPosInforList = new ArrayList<>();
        newMaterialPosInforList.addAll(withSerial);
        newMaterialPosInforList.addAll(noSerial);
        //商品尺寸部位表
        materialSize.setPosInforList(newMaterialPosInforList);
        //下装
        List<TecMaterialPosInforDown> materialPosInforDowns = posInforDownService.selectTecMaterialPosInforDownList(new TecMaterialPosInforDown().setMaterialSizeSid(Long.valueOf(materialSizeSid)));
        List<TecMaterialPosInforDown> materialPosInforDownList = new ArrayList<>();
        materialPosInforDowns.forEach(down -> {
            List<TecMaterialPosSizeDown> posSizeDowns = posSizeDownService.selectTecMaterialPosSizeDownList(new TecMaterialPosSizeDown().setMaterialPosInforSid(down.getMaterialPosInforSid()));
            down.setPosSizeDownList(posSizeDowns);
            materialPosInforDownList.add(down);
        });
        //有序
        List<TecMaterialPosInforDown> withSerialDown = materialPosInforDownList.stream().filter(item -> item.getSerialNum() != null).collect(Collectors.toList());
        //有序
        List<TecMaterialPosInforDown> noSerialDown = materialPosInforDownList.stream().filter(item -> item.getSerialNum() == null).collect(Collectors.toList());
        ArrayList<TecMaterialPosInforDown> newMaterialPosInforDowns = new ArrayList<>();
        newMaterialPosInforDowns.addAll(withSerialDown);
        newMaterialPosInforDowns.addAll(noSerialDown);
        materialSize.setPosInforDownList(newMaterialPosInforDowns);
        SysFormProcess formProcess = new SysFormProcess();
        formProcess.setFormId(materialSize.getMaterialSizeSid());
        List<SysFormProcess> list = formProcessService.selectSysFormProcessList(formProcess);
        if(list!=null&&list.size()>0) {
            formProcess = new SysFormProcess();
            formProcess = list.get(0);
            materialSize.setApprovalNode(formProcess.getApprovalNode());
            materialSize.setApprovalUserName(formProcess.getApprovalUserName());
            materialSize.setSubmitDate(formProcess.getCreateDate());
            materialSize.setSubmitUserName(userService.selectSysUserById(Long.valueOf(formProcess.getCreateById())).getNickName());
        }
        return AjaxResult.success(materialSize);
    }

    /**
     * * 修改商品尺寸
     */
    //@PreAuthorize(hasPermi = "ems:size:edit")
    @Log(title = "商品尺寸", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改商品尺寸", notes = "修改商品尺寸")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult edit(@RequestBody @Valid TecMaterialSize request) {
        return toAjax(tecMaterialSizeService.updateTecMaterialSize(request));
    }

    /**
     * 修改某一商品尺寸处理状态
     */
    /*@PreAuthorize(hasPermi = "ems:size:edit")
    @Log(title = "商品尺寸", businessType = BusinessType.UPDATE)
    @PutMapping("/{sId}/handleStatus")
    public AjaxResult putHandleStatus(@PathVariable String sId, String handleStatus) {
        if (StringUtils.isBlank(sId)) {
            return AjaxResult.error("sId 不能为空");
        }
        if (StatusUtil.judgeHandleStatus(materialSizeService.getHandleStatus(sId), handleStatus)) {
            return AjaxResult.error("修改商品尺寸处理状态失败");
        }
        return toAjax(Integer.parseInt(materialSizeService.putHandleStatus(sId, handleStatus)));
    }*/

    /**
     * 修改某一商品尺寸启用/停用状态
     */
    /*@PreAuthorize(hasPermi = "ems:size:edit")
    @Log(title = "商品尺寸", businessType = BusinessType.UPDATE)
    @PutMapping("/{sId}/status")
    public AjaxResult putStatus(@PathVariable String sId, String status) {
        if (StringUtils.isBlank(sId)) {
            return AjaxResult.error("sId 不能为空");
        }
        if (StatusUtil.judgeStatus(materialSizeService.getHandleStatus(sId), status)) {
            return AjaxResult.error("修改商品尺寸启用/停用状态失败");
        }
        return toAjax(Integer.parseInt(materialSizeService.putStatus(sId, status)));
    }*/

    /**
     * 删除商品尺寸
     */
    //@PreAuthorize(hasPermi = "ems:size:remove")
    @ApiOperation(value = "删除商品尺寸", notes = "删除商品尺寸")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品尺寸", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> materialSizeSids) {
        if (CollUtil.isEmpty(materialSizeSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(tecMaterialSizeService.deleteTecMaterialSizeByIds(materialSizeSids));
    }

    @ApiOperation(value = "获取商品档案下拉列表", notes = "获取商品档案下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品尺寸", businessType = BusinessType.DELETE)
    @PostMapping("/getMaterialList")
    public AjaxResult getMaterialInfo() {
        List<BasMaterial> materialList = tecMaterialSizeService.selectBasMaterialList();
        return AjaxResult.success(materialList);
    }


    @ApiOperation(value = "根据编码获取商品档案详情", notes = "根据编码获取商品档案详情")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品尺寸", businessType = BusinessType.DELETE)
    @PostMapping("/getMaterialInfo")
    public AjaxResult getMaterialInfo(String materialCode) {
        if (CharSequenceUtil.isEmpty(materialCode)) {
            throw new CheckedException("商品编码参数缺失！");
        }
        BasMaterial basMaterial = basMaterialService.selectBasMaterialByCode(materialCode, "material");
        if (basMaterial != null) {
            //查询是否已经建过尺寸档案
            Long materialSid = basMaterial.getMaterialSid();
            if (tecMaterialSizeService.isExist(materialSid)) {
                return AjaxResult.error("该商品档案的尺寸信息已存在！");
            }
            if (!basMaterial.getStatus().equals(ConstantsEms.ENABLE_STATUS)) {
                return AjaxResult.error("已启用的商品才能建立商品尺寸表！");
            }
            //查询版型档案
            Long modelSid = basMaterial.getModelSid();
            if (modelSid != null) {
                //版型档案详细尺寸信息列表
                TecModel modelSystemDetailResponse = modelSystemService.getDetail(modelSid);
                basMaterial.setModelPosInforList(modelSystemDetailResponse.getPosInforList());
                basMaterial.setModelPosInforDownList(modelSystemDetailResponse.getPosInforDownList());
                //查询尺码组
                TecModel tecModel = modelSystemService.selectTecModelById(modelSid);
                if (tecModel != null) {
                    Long skuGroupSid = tecModel.getSkuGroupSid();
                    if (skuGroupSid != null) {
                        BasSkuGroup skuGroup = new BasSkuGroup();
                        skuGroup.setSkuGroupSid(skuGroupSid);
                        List<BasSkuGroup> skuResponseList = basSkuGroupService.selectBasSkuGroupList(skuGroup);
                        basMaterial.setBasSkuResponseList(skuResponseList);
                    }
                    Long skuGroupDownSid = tecModel.getDownSkuGroupSid();
                    if (skuGroupDownSid != null) {
                        BasSkuGroup skuGroup = new BasSkuGroup();
                        skuGroup.setSkuGroupSid(skuGroupDownSid);
                        List<BasSkuGroup> skuResponseList = basSkuGroupService.selectBasSkuGroupList(skuGroup);
                        basMaterial.setBasSkuDownResponseList(skuResponseList);
                    }
                }
            }
            //查询产品季档案
            Long productSeasonSid = basMaterial.getProductSeasonSid();
            if (productSeasonSid != null) {
                ListSeasonResponse response = sBasProductSeasonService.selectSBasProductSeasonById(productSeasonSid);
                basMaterial.setSeasonResponse(response);
            }
            return AjaxResult.success(basMaterial);
        }
        return AjaxResult.error("获取失败");
    }

}
