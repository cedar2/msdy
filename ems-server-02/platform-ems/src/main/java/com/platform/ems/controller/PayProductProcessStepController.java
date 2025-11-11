package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.platform.common.constant.HttpStatus;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasStaff;
import com.platform.ems.domain.PayProductProcessStep;
import com.platform.ems.domain.PayProductProcessStepItem;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.export.PayProductProcessStepItemFormExport;
import com.platform.ems.mapper.PayProductProcessStepItemMapper;
import com.platform.ems.service.IBasStaffService;
import com.platform.ems.service.IPayProductProcessStepItemService;
import com.platform.ems.service.IPayProductProcessStepService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.MongodbUtil;
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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品道序-主Controller
 *
 * @author linhongwei
 * @date 2021-09-08
 */
@RestController
@RequestMapping("/product/process/step")
@Api(tags = "商品道序-主")
public class PayProductProcessStepController extends BaseController {

    @Autowired
    private IPayProductProcessStepService payProductProcessStepService;
    @Autowired
    private IPayProductProcessStepItemService productProcessStepItemService;
    @Autowired
    private PayProductProcessStepItemMapper payProductProcessStepItemMapper;
    @Autowired
    private IBasStaffService basStaffService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private RemoteSystemService remoteSystemService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    @Autowired
    RedissonClient redissonClient;

    private static final String LOCK_KEY = "PRODUCT_PROCESS_STEP_IMPORT";

    private static final String TITLE = "商品道序-主";

    private static final String FILLE_PATH = "/template";

    /**
     * 查询商品道序-主列表
     */
    @PreAuthorize(hasPermi = "ems:product:process:step:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询商品道序-主列表", notes = "查询商品道序-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProductProcessStep.class))
    public TableDataInfo list(@RequestBody PayProductProcessStep payProductProcessStep) {
        Long[] roleIds = null;
        List<SysRole> roleList = ApiThreadLocalUtil.get().getSysUser().getRoles();
        if (CollectionUtil.isNotEmpty(roleList)){
            roleIds = roleList.stream().map(SysRole::getRoleId).toArray(Long[]::new);
        }
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleIds(roleIds);
        roleMenu.setPerms("ems:plant:all");
        boolean isAll = true;
        if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())){
            isAll = remoteSystemService.isHavePerms(roleMenu).getData();
        }
        if (!isAll){
            /*
             * 获取此账号档案的“员工sid”，在通过“员工sid”，在员工档案获取此员工所属的工厂；
             * 如工厂为空，则无法查看任何数据；如工厂不为空，则显示“工厂”为此工厂的商品道序数据。
             */
            Long staffSid = ApiThreadLocalUtil.get().getSysUser().getStaffSid();
            if (staffSid != null){
                BasStaff staff = basStaffService.selectBasStaffById(staffSid);
                if (staff.getDefaultPlantSid() != null){
                    payProductProcessStep.setPlantSid(staff.getDefaultPlantSid());
                    startPage(payProductProcessStep);
                    List<PayProductProcessStep> list = payProductProcessStepService.selectPayProductProcessStepList(payProductProcessStep);
                    return getDataTable(list);
                }
            }
            return getDataTable(new ArrayList<>());
        }
        else {
            startPage(payProductProcessStep);
            List<PayProductProcessStep> list = payProductProcessStepService.selectPayProductProcessStepList(payProductProcessStep);
            return getDataTable(list);
        }
    }

    /**
     * 导出商品道序-主列表
     */
    @PreAuthorize(hasPermi = "ems:product:process:step:export")
    @Log(title = "商品道序-主", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品道序-主列表", notes = "导出商品道序-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PayProductProcessStep payProductProcessStep) throws IOException {
        List<PayProductProcessStep> list = new ArrayList<>();
        Long[] roleIds = null;
        List<SysRole> roleList = ApiThreadLocalUtil.get().getSysUser().getRoles();
        if (CollectionUtil.isNotEmpty(roleList)){
            roleIds = roleList.stream().map(SysRole::getRoleId).toArray(Long[]::new);
        }
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleIds(roleIds);
        roleMenu.setPerms("ems:plant:all");
        boolean isAll = true;
        if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())){
            isAll = remoteSystemService.isHavePerms(roleMenu).getData();
        }
        if (isAll){
            list = payProductProcessStepService.selectPayProductProcessStepList(payProductProcessStep);
        }
        else {
            /*
             * 获取此账号档案的“员工sid”，在通过“员工sid”，在员工档案获取此员工所属的工厂；
             * 如工厂为空，则无法查看任何数据；如工厂不为空，则显示“工厂”为此工厂的商品道序数据。
             */
            Long staffSid = ApiThreadLocalUtil.get().getSysUser().getStaffSid();
            if (staffSid != null){
                BasStaff staff = basStaffService.selectBasStaffById(staffSid);
                if (staff.getDefaultPlantSid() != null){
                    payProductProcessStep.setPlantSid(staff.getDefaultPlantSid());
                    list = payProductProcessStepService.selectPayProductProcessStepList(payProductProcessStep);
                }
            }
        }
        if (CollectionUtil.isNotEmpty(list)) {
            DecimalFormat df3 = new DecimalFormat("########.###");
            DecimalFormat df4 = new DecimalFormat("########.####");
            list.forEach(item->{
                item.setLimitPriceToString(item.getLimitPrice() == null ? null : df3.format(item.getLimitPrice()));
                item.setTotalPriceBeforeToString(item.getTotalPriceBefore() == null ? null : df4.format(item.getTotalPriceBefore()));
                item.setTotalPriceAfterToString(item.getTotalPriceAfter() == null ? null : df4.format(item.getTotalPriceAfter()));
            });
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PayProductProcessStep> util = new ExcelUtil<>(PayProductProcessStep.class, dataMap);
        util.exportExcel(response, list, "商品道序");
    }


    /**
     * 获取商品道序-主详细信息
     */
    @ApiOperation(value = "获取商品道序-主详细信息", notes = "获取商品道序-主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProductProcessStep.class))
    @PreAuthorize(hasPermi = "ems:product:process:step:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long productProcessStepSid) {
        if (productProcessStepSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(payProductProcessStepService.selectPayProductProcessStepById(productProcessStepSid));
    }

    /**
     * 新增商品道序-主
     */
    @ApiOperation(value = "新增商品道序-主", notes = "新增商品道序-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:product:process:step:add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @Log(title = "商品道序-主", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PayProductProcessStep payProductProcessStep) {
        if (payProductProcessStep.getProductCode() == null){
            if (payProductProcessStep.getSampleCodeSelf() == null){
                throw new BaseException("商品编码/我司样衣号不能为空");
            }
        }
        return AjaxResult.success(payProductProcessStepService.insertPayProductProcessStep(payProductProcessStep));
    }

    @PostMapping("/add/verify")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "新增商品道序-主前工价不一致的校验", notes = "新增商品道序-主前工价不一致的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = EmsResultEntity.class))
    public AjaxResult addVerify(@RequestBody @Valid PayProductProcessStep payProductProcessStep) {
        if (payProductProcessStep.getProductCode() == null){
            if (payProductProcessStep.getSampleCodeSelf() == null){
                throw new BaseException("商品编码/我司样衣号不能为空");
            }
        }
        return AjaxResult.success(payProductProcessStepService.checkPrice(payProductProcessStep));
    }


    /**
     * 修改商品道序-主
     */
    @ApiOperation(value = "修改商品道序-主", notes = "修改商品道序-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:product:process:step:edit")
    @Log(title = "商品道序-主", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid PayProductProcessStep payProductProcessStep) {
        return AjaxResult.success(payProductProcessStepService.updatePayProductProcessStep(payProductProcessStep));
    }

    /**
     * 变更商品道序-主
     */
    @ApiOperation(value = "变更商品道序-主", notes = "变更商品道序-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:product:process:step:change")
    @Log(title = "商品道序-主", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid PayProductProcessStep payProductProcessStep) {
        return AjaxResult.success(payProductProcessStepService.changePayProductProcessStep(payProductProcessStep));
    }

    @ApiOperation(value = "变更商品道序页面点暂存", notes = "变更商品道序页面点暂存")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "变更商品道序页面点暂存", businessType = BusinessType.CHANGE)
    @PostMapping("/update")
    public AjaxResult update(@RequestBody @Valid PayProductProcessStep payProductProcessStep) {
        return AjaxResult.success(payProductProcessStepService.newUpdatePayProductProcessStep(payProductProcessStep));
    }

    /**
     * 删除商品道序-主
     */
    @ApiOperation(value = "删除商品道序-主", notes = "删除商品道序-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:product:process:step:remove")
    @Log(title = "商品道序-主", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> productProcessStepSids) {
        if (CollectionUtils.isEmpty(productProcessStepSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(payProductProcessStepService.deletePayProductProcessStepByIds(productProcessStepSids));
    }

    @PreAuthorize(hasPermi = "ems:product:process:step:check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品道序-主", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody PayProductProcessStep payProductProcessStep) {
        int row = 0;
        Long[] sids = payProductProcessStep.getProductProcessStepSidList();
        if (sids == null || sids.length == 0) {
            return toAjax(row);
        }
        row = payProductProcessStepService.check(payProductProcessStep);
        for (Long sid : sids) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(sid, payProductProcessStep.getHandleStatus(), msgList, TITLE);
        }
        return toAjax(row);
    }

    /**
     * 商品道序下拉框接口
     */
    @PostMapping("/getList")
    @ApiOperation(value = "商品道序下拉框接口", notes = "商品道序下拉框接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProductProcessStep.class))
    public AjaxResult getList(@RequestBody PayProductProcessStep payProductProcessStep) {
        return AjaxResult.success(payProductProcessStepService.getList(payProductProcessStep));
    }

    /**
     * 查询商品道序-明细列表、
     * 主要计薪量申报的明细查询--按生产订单查询
     */
    @PostMapping("/getItemAndMoList")
    @ApiOperation(value = "查询商品道序-明细列表", notes = "查询商品道序-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProductProcessStepItem.class))
    public TableDataInfo getItemAndMoList(@RequestBody PayProductProcessStepItem payProductProcessStepItem) {
        List<PayProductProcessStepItem> itemList = payProductProcessStepItemMapper.getManOrderItemList(payProductProcessStepItem);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(new ArrayList<>());
        rspData.setMsg("查询成功");
        rspData.setTotal(itemList.size());
        if (itemList.size() > 0){
            List<PayProductProcessStepItem> response = productProcessStepItemService.getManOrderItemList(payProductProcessStepItem);
            rspData.setRows(response);
        }
        return rspData;
    }

    /**
     * 查询商品道序-明细列表、
     * 主要计薪量申报的明细查询--不按生产订单查询
     */
    @PostMapping("/getItemList")
    @ApiOperation(value = "查询商品道序-明细列表", notes = "查询商品道序-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProductProcessStepItem.class))
    public TableDataInfo getItemList(@RequestBody PayProductProcessStepItem payProductProcessStepItem) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setTotal(0);
        rspData.setRows(new ArrayList<>());
        rspData.setMsg("查询成功");
        List<PayProductProcessStepItem> list = productProcessStepItemService.selectPayProductProcessStepItem(payProductProcessStepItem);
        long total = list.size();
        rspData.setTotal(total);
        if (total > 0){
            if (payProductProcessStepItem.getPageSize() != null && payProductProcessStepItem.getPageNum() != null
                    && payProductProcessStepItem.getPageSize() > 0 && (payProductProcessStepItem.getPageSize()*(payProductProcessStepItem.getPageNum()-1) >= 0)){
                list = list.stream().skip(payProductProcessStepItem.getPageSize()*(payProductProcessStepItem.getPageNum()-1)).limit(payProductProcessStepItem.getPageSize()).collect(Collectors.toList());
            }
            rspData.setRows(list);
        }
        return rspData;
    }

    /**
     * 变更校验明细是否可删除
     */
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "变更校验明细是否可删除", notes = "变更校验明细是否可删除")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "变更校验明细是否可删除", businessType = BusinessType.DELETE)
    @PostMapping("/verifyItem")
    public AjaxResult verifyItem(@RequestBody Long[] stepItemSids) {
        if (ArrayUtil.isEmpty(stepItemSids)) {
            throw new BaseException("参数缺失");
        }
        return toAjax(productProcessStepItemService.verifyItem(stepItemSids));
    }


    /**
     * 确认校验明细工价是否大于商品工价上限
     */
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "确认校验明细工价是否大于商品工价上限", notes = "确认校验明细工价是否大于商品工价上限")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "确认校验明细工价是否大于商品工价上限", businessType = BusinessType.DELETE)
    @PostMapping("/verifyPrice")
    public AjaxResult verifyPrice(@RequestBody PayProductProcessStep payProductProcessStep) {
        return AjaxResult.success(payProductProcessStepService.verifyPrice(payProductProcessStep));
    }

    /**
     * 添加专用道序时校验名称是否重复
     */
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "添加专用道序时校验名称是否重复", notes = "添加专用道序时校验名称是否重复")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "添加专用道序时校验名称是否重复", businessType = BusinessType.DELETE)
    @PostMapping("/verifyProcess")
    public AjaxResult verifyProcess(@RequestBody PayProductProcessStepItem payProductProcessStepItem) {
        return AjaxResult.success(payProductProcessStepService.verifyProcess(payProductProcessStepItem));
    }

    @PreAuthorize(hasPermi = "ems:product:process:step:import")
    @PostMapping("/import")
    @ApiOperation(value = "导入商品道序多款", notes = "导入商品道序多款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        RLock lock = redissonClient.getLock(LOCK_KEY);
        if (lock.isLocked()) {
            throw new CustomException("系统处理中，请勿重复点击按钮");
        }
        lock.lock(5L, TimeUnit.SECONDS);
        try {
            return AjaxResult.success(payProductProcessStepService.importData(file));
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize(hasPermi = "ems:product:process:step:import")
    @PostMapping("/import/single")
    @ApiOperation(value = "导入商品道序单款", notes = "导入商品道序单款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataSingle(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        RLock lock = redissonClient.getLock(LOCK_KEY);
        if (lock.isLocked()) {
            throw new CustomException("系统处理中，请勿重复点击按钮");
        }
        lock.lock(5L, TimeUnit.SECONDS);
        try {
            return AjaxResult.success(payProductProcessStepService.importDataSingle(file));
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PostMapping("/import/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "导入商品道序单款", notes = "导入商品道序单款")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importAddData(@RequestBody List<PayProductProcessStep> stepList) {
        return AjaxResult.success(payProductProcessStepService.importAddData(stepList));
    }

    @ApiOperation(value = "下载商品道序导入模板-多款", notes = "下载商品道序导入模板-多款")
    @PostMapping("/importTemplate/multiple")
    public void importTemplateMultiple(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/SCM软件_导入模板_多款_商品道序_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("SCM软件_导入模板_多款_商品道序_V1.0.xlsx", "UTF-8"));
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

    @ApiOperation(value = "下载商品道序导入模板-单款", notes = "下载商品道序导入模板-单款")
    @PostMapping("/importTemplate/single")
    public void importTemplateSingle(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/SCM软件_导入模板_单款_商品道序_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("SCM软件_导入模板_单款_商品道序_V1.0.xlsx", "UTF-8"));
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
     * 查询商品道序明细报表
     */
    @PreAuthorize(hasPermi = "ems:product:process:step:item:form")
    @PostMapping("/item/form")
    @ApiOperation(value = "查询商品道序明细报表", notes = "查询商品道序明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProductProcessStepItem.class))
    public TableDataInfo itemList(@RequestBody PayProductProcessStepItem payProductProcessStepItem) {
        Long[] roleIds = null;
        List<SysRole> roleList = ApiThreadLocalUtil.get().getSysUser().getRoles();
        if (CollectionUtil.isNotEmpty(roleList)){
            roleIds = roleList.stream().map(SysRole::getRoleId).toArray(Long[]::new);
        }
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleIds(roleIds);
        roleMenu.setPerms("ems:plant:all");
        boolean isAll = true;
        if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())){
            isAll = remoteSystemService.isHavePerms(roleMenu).getData();
        }
        if (!ConstantsEms.CLIENT_ID_10000.equals(ApiThreadLocalUtil.get().getClientId())){
            payProductProcessStepItem.setClientId(ApiThreadLocalUtil.get().getClientId());
        }
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setTotal(0);
        rspData.setRows(new ArrayList<>());
        rspData.setMsg("查询成功");
        if (isAll){
            List<PayProductProcessStepItem> list = productProcessStepItemService.selectPayProductProcessStepItemForm(payProductProcessStepItem);
            long total = list.size();
            rspData.setTotal(total);
            if (total > 0){
                if (payProductProcessStepItem.getPageSize() != null && payProductProcessStepItem.getPageNum() != null
                        && payProductProcessStepItem.getPageSize() > 0 && (payProductProcessStepItem.getPageSize()*(payProductProcessStepItem.getPageNum()-1) >= 0)){
                    list = list.stream().skip(payProductProcessStepItem.getPageSize()*(payProductProcessStepItem.getPageNum()-1)).limit(payProductProcessStepItem.getPageSize()).collect(Collectors.toList());
                }
                rspData.setRows(list);
            }
        }
        else {
            /*
             * 获取此账号档案的“员工sid”，在通过“员工sid”，在员工档案获取此员工所属的工厂；
             * 如工厂为空，则无法查看任何数据；如工厂不为空，则显示“工厂”为此工厂的计薪量申报数据。
             */
            Long staffSid = ApiThreadLocalUtil.get().getSysUser().getStaffSid();
            if (staffSid != null){
                BasStaff staff = basStaffService.selectBasStaffById(staffSid);
                if (staff.getDefaultPlantSid() != null){
                    payProductProcessStepItem.setPlantSid(staff.getDefaultPlantSid());
                    List<PayProductProcessStepItem> list = productProcessStepItemService.selectPayProductProcessStepItemForm(payProductProcessStepItem);
                    long total = list.size();
                    rspData.setTotal(total);
                    if (total > 0){
                        if (payProductProcessStepItem.getPageSize() != null && payProductProcessStepItem.getPageNum() != null
                                && payProductProcessStepItem.getPageSize() > 0 && (payProductProcessStepItem.getPageSize()*(payProductProcessStepItem.getPageNum()-1) >= 0)){
                            list = list.stream().skip(payProductProcessStepItem.getPageSize()*(payProductProcessStepItem.getPageNum()-1)).limit(payProductProcessStepItem.getPageSize()).collect(Collectors.toList());
                        }
                        rspData.setRows(list);
                    }
                    return rspData;
                }
            }
            return getDataTable(new ArrayList<>());
        }
        return rspData;
    }

    /**
     * 导出查询商品道序明细报表
     */
    @PreAuthorize(hasPermi = "ems:product:process:step:item:form")
    @PostMapping("/item/form/export")
    @ApiOperation(value = "导出查询商品道序明细报表", notes = "导出查询商品道序明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProductProcessStepItem.class))
    public void export(HttpServletResponse response, PayProductProcessStepItem payProductProcessStepItem) throws IOException {
        Long[] roleIds = null;
        List<SysRole> roleList = ApiThreadLocalUtil.get().getSysUser().getRoles();
        if (CollectionUtil.isNotEmpty(roleList)){
            roleIds = roleList.stream().map(SysRole::getRoleId).toArray(Long[]::new);
        }
        SysRoleMenu roleMenu = new SysRoleMenu();
        roleMenu.setRoleIds(roleIds);
        roleMenu.setPerms("ems:plant:all");
        boolean isAll = true;
        if (!"10000".equals(ApiThreadLocalUtil.get().getClientId())){
            isAll = remoteSystemService.isHavePerms(roleMenu).getData();
        }
        if (!ConstantsEms.CLIENT_ID_10000.equals(ApiThreadLocalUtil.get().getClientId())){
            payProductProcessStepItem.setClientId(ApiThreadLocalUtil.get().getClientId());
        }
        List<PayProductProcessStepItem> list = new ArrayList<>();
        if (isAll){
            list = productProcessStepItemService.selectPayProductProcessStepItemForm(payProductProcessStepItem);
        }
        else {
            /*
             * 获取此账号档案的“员工sid”，在通过“员工sid”，在员工档案获取此员工所属的工厂；
             * 如工厂为空，则无法查看任何数据；如工厂不为空，则显示“工厂”为此工厂的计薪量申报数据。
             */
            Long staffSid = ApiThreadLocalUtil.get().getSysUser().getStaffSid();
            if (staffSid != null){
                BasStaff staff = basStaffService.selectBasStaffById(staffSid);
                if (staff.getDefaultPlantSid() != null){
                    payProductProcessStepItem.setPlantSid(staff.getDefaultPlantSid());
                    list = productProcessStepItemService.selectPayProductProcessStepItemForm(payProductProcessStepItem);
                }
            }
        }
        List<PayProductProcessStepItemFormExport> exportList = BeanCopyUtils.copyListProperties(list, PayProductProcessStepItemFormExport::new);
        if (CollectionUtil.isNotEmpty(exportList)){
            DecimalFormat df2 = new DecimalFormat("####.##");
            DecimalFormat df3 = new DecimalFormat("########.###");
            exportList.forEach(item->{
                item.setSortToString(item.getSort() == null ? null : df2.format(item.getSort()));
                item.setPriceRateToString(item.getPriceRate() == null ? null : df3.format(item.getPriceRate()));
                item.setProductPriceRateToString(item.getProductPriceRate() == null ? null : df3.format(item.getProductPriceRate()));
            });
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PayProductProcessStepItemFormExport> util = new ExcelUtil<>(PayProductProcessStepItemFormExport.class, dataMap);
        util.exportExcel(response, exportList, "商品道序明细报表");
    }

}
