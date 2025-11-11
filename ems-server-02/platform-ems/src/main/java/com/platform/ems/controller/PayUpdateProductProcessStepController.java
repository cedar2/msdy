package com.platform.ems.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.annotation.FieldScope;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.domain.BasStaff;
import com.platform.ems.domain.PayProductProcessStep;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.service.IBasStaffService;
import com.platform.api.service.RemoteSystemService;
import com.platform.common.core.domain.entity.SysRole;
import com.platform.system.domain.SysRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import com.platform.ems.domain.PayUpdateProductProcessStep;
import com.platform.ems.service.IPayUpdateProductProcessStepService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 商品道序变更-主Controller
 *
 * @author chenkw
 * @date 2022-11-08
 */
@RestController
@RequestMapping("/pay/update/product/process/step")
@Api(tags = "商品道序变更-主")
public class PayUpdateProductProcessStepController extends BaseController {

    @Autowired
    private IPayUpdateProductProcessStepService payUpdateProductProcessStepService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private RemoteSystemService remoteSystemService;
    @Autowired
    private IBasStaffService basStaffService;

    /**
     * 查询商品道序变更-主列表
     */
    @PostMapping("/list")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all")
    @ApiOperation(value = "查询商品道序变更-主列表", notes = "查询商品道序变更-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayUpdateProductProcessStep.class))
    public TableDataInfo list(@RequestBody PayUpdateProductProcessStep payUpdateProductProcessStep) {
        startPage(payUpdateProductProcessStep);
        List<PayUpdateProductProcessStep> list = payUpdateProductProcessStepService.selectPayUpdateProductProcessStepList(payUpdateProductProcessStep);
        return getDataTable(list);
    }

    /**
     * 导出商品道序变更-主列表
     */
//    @Log(title = "商品道序变更-主", businessType = BusinessType.EXPORT)
//    @ApiOperation(value = "导出商品道序变更-主列表", notes = "导出商品道序变更-主列表")
//    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, PayUpdateProductProcessStep payUpdateProductProcessStep) throws IOException {
//        List<PayUpdateProductProcessStep> list = payUpdateProductProcessStepService.selectPayUpdateProductProcessStepList(payUpdateProductProcessStep);
//        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
//        ExcelUtil<PayUpdateProductProcessStep> util = new ExcelUtil<>(PayUpdateProductProcessStep.class, dataMap);
//        util.exportExcel(response, list, "商品道序变更-主");
//    }


    /**
     * 获取商品道序变更-主详细信息
     */
    @ApiOperation(value = "获取商品道序变更-主详细信息", notes = "获取商品道序变更-主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayUpdateProductProcessStep.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long updateProductProcessStepSid) {
        if (updateProductProcessStepSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(payUpdateProductProcessStepService.selectPayUpdateProductProcessStepById(updateProductProcessStepSid));
    }

    /**
     * 新增商品道序变更-主
     */
    @ApiOperation(value = "新增商品道序变更-主", notes = "新增商品道序变更-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品道序变更-主", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid PayUpdateProductProcessStep payUpdateProductProcessStep) {
        int row = payUpdateProductProcessStepService.insertPayUpdateProductProcessStep(payUpdateProductProcessStep);
        if (row > 0){
            return AjaxResult.success("操作成功", payUpdateProductProcessStepService
                    .selectPayUpdateProductProcessStepById(payUpdateProductProcessStep.getUpdateProductProcessStepSid()));
        }
        return toAjax(row);
    }

    @PostMapping("/add/verify")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "商品道序变更-主前工价不一致的校验", notes = "商品道序变更-主前工价不一致的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = EmsResultEntity.class))
    public AjaxResult addVerify(@RequestBody @Valid PayUpdateProductProcessStep payUpdateProductProcessStep) {
        if (payUpdateProductProcessStep.getProductCode() == null){
            if (payUpdateProductProcessStep.getSampleCodeSelf() == null){
                throw new BaseException("商品编码/我司样衣号不能为空");
            }
        }
        return AjaxResult.success(payUpdateProductProcessStepService.checkPrice(payUpdateProductProcessStep));
    }

    /**
     * 确认校验明细工价是否大于商品工价上限
     */
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "确认校验明细工价是否大于商品工价上限", notes = "确认校验明细工价是否大于商品工价上限")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "确认校验明细工价是否大于商品工价上限", businessType = BusinessType.DELETE)
    @PostMapping("/verifyPrice")
    public AjaxResult verifyPrice(@RequestBody PayUpdateProductProcessStep payUpdateProductProcessStep) {
        return AjaxResult.success(payUpdateProductProcessStepService.verifyPrice(payUpdateProductProcessStep));
    }


    @ApiOperation(value = "修改商品道序变更-主", notes = "修改商品道序变更-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品道序变更-主", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody PayUpdateProductProcessStep payUpdateProductProcessStep) {
        return toAjax(payUpdateProductProcessStepService.updatePayUpdateProductProcessStep(payUpdateProductProcessStep));
    }

    @ApiOperation(value = "商品道序变更提交", notes = "商品道序变更提交")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品道序变更提交", businessType = BusinessType.UPDATE)
    @PostMapping("/updateStatus")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult updateStatus(@RequestBody PayUpdateProductProcessStep payUpdateProductProcessStep) {
        return toAjax(payUpdateProductProcessStepService.updateStatus(payUpdateProductProcessStep));
    }

    /**
     * 导出商品道序变更-主列表
     */
    @Log(title = "商品道序变更", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出商品道序变更-主列表", notes = "导出商品道序变更-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, PayUpdateProductProcessStep payUpdateProductProcessStep) throws IOException {
        List<PayUpdateProductProcessStep> list = new ArrayList<>();
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
            list = payUpdateProductProcessStepService.selectPayUpdateProductProcessStepList(payUpdateProductProcessStep);
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
                    payUpdateProductProcessStep.setPlantSid(staff.getDefaultPlantSid());
                    list = payUpdateProductProcessStepService.selectPayUpdateProductProcessStepList(payUpdateProductProcessStep);
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
        ExcelUtil<PayUpdateProductProcessStep> util = new ExcelUtil<>(PayUpdateProductProcessStep.class, dataMap);
        util.exportExcel(response, list, "商品道序变更");
    }


    /**
     * 变更商品道序变更-主
     */
    @ApiOperation(value = "变更商品道序变更-主", notes = "变更商品道序变更-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品道序变更-主", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid PayUpdateProductProcessStep payUpdateProductProcessStep) {
        return toAjax(payUpdateProductProcessStepService.changePayUpdateProductProcessStep(payUpdateProductProcessStep));
    }

    /**
     * 删除商品道序变更-主
     */
    @ApiOperation(value = "删除商品道序变更-主", notes = "删除商品道序变更-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品道序变更-主", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> updateProductProcessStepSids) {
        if (CollectionUtils.isEmpty(updateProductProcessStepSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(payUpdateProductProcessStepService.deletePayUpdateProductProcessStepByIds(updateProductProcessStepSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "商品道序变更-主", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody PayUpdateProductProcessStep payUpdateProductProcessStep) {
        return toAjax(payUpdateProductProcessStepService.check(payUpdateProductProcessStep));
    }

}
