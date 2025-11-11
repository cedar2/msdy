package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsAuthorize;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsPdm;
import com.platform.ems.domain.BasSku;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.service.IBasSkuService;
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
import com.platform.ems.domain.FrmSampleReview;
import com.platform.ems.service.IFrmSampleReviewService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 样品评审单Controller
 *
 * @author chenkw
 * @date 2022-12-12
 */
@RestController
@RequestMapping("/frm/sample/review")
@Api(tags = "样品评审单")
public class FrmSampleReviewController extends BaseController {

    @Autowired
    private IFrmSampleReviewService frmSampleReviewService;
    @Autowired
    private IBasSkuService basSkuService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private RemoteSystemService remoteSystemService;

    /**
     * 查询样品评审单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询样品评审单列表", notes = "查询样品评审单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmSampleReview.class))
    public TableDataInfo list(@RequestBody FrmSampleReview frmSampleReview) {
        // 创建人数据权限匹配
        dataScope(frmSampleReview);
        startPage(frmSampleReview);
        List<FrmSampleReview> list = frmSampleReviewService.selectFrmSampleReviewList(frmSampleReview);
        TableDataInfo rspData = getDataTable(list);
        setSku1Name(list);
        return rspData;
    }

    /**
     * 创建人权限匹配
     * @param frmSampleReview
     */
    private void dataScope(FrmSampleReview frmSampleReview) {
        String perms = null;
        if (ConstantsPdm.REVIEW_STAGE_YPCS.equals(frmSampleReview.getReviewPhase())) {
            perms = ConstantsAuthorize.PDM_SAMPLE_REVIEW_CS_ALL;
        }
        else if (ConstantsPdm.REVIEW_STAGE_YPZS.equals(frmSampleReview.getReviewPhase())) {
            perms = ConstantsAuthorize.PDM_SAMPLE_REVIEW_ZS_ALL;
        }
        if (perms != null) {
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
                    frmSampleReview.setCreatorAccount(creatorAccount);
                }
            }
        }
    }

    /**
     * 导出样品评审单列表
     */
    @Log(title = "样品评审单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出样品评审单列表", notes = "导出样品评审单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FrmSampleReview frmSampleReview) throws IOException {
        // 创建人数据权限匹配
        dataScope(frmSampleReview);
        List<FrmSampleReview> list = frmSampleReviewService.selectFrmSampleReviewList(frmSampleReview);
        setSku1Name(list);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FrmSampleReview> util = new ExcelUtil<>(FrmSampleReview.class, dataMap);
        util.exportExcel(response, list, "样品评审单");
    }

    /**
     * 根据SKU1code 分号字段获取对应的sku名称返回
     */
    private void setSku1Name(List<FrmSampleReview> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(item->{
                if (StrUtil.isNotBlank(item.getSku1Code())) {
                    String[] sku1Code = item.getSku1Code().split(";");
                    List<BasSku> skuList = basSkuService.selectBasSkuList(new BasSku().setSkuCodeList(sku1Code)
                            .setHandleStatus(ConstantsEms.CHECK_STATUS).setStatus(ConstantsEms.ENABLE_STATUS));
                    if (ArrayUtil.isNotEmpty(skuList)) {
                        String sku1Name = "";
                        for (int i = 0; i < skuList.size(); i++) {
                            sku1Name = sku1Name + skuList.get(i).getSkuName() + ";";
                        }
                        item.setSku1Name(sku1Name);
                    }
                }
            });
        }
    }


    /**
     * 获取样品评审单详细信息
     */
    @ApiOperation(value = "获取样品评审单详细信息", notes = "获取样品评审单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmSampleReview.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sampleReviewSid) {
        if (sampleReviewSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(frmSampleReviewService.selectFrmSampleReviewById(sampleReviewSid));
    }

    /**
     * 新增样品评审单
     */
    @ApiOperation(value = "新增样品评审单", notes = "新增样品评审单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "样品评审单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid FrmSampleReview frmSampleReview) {
        int row = frmSampleReviewService.insertFrmSampleReview(frmSampleReview);
        if (row > 0) {
            return AjaxResult.success(frmSampleReviewService.selectFrmSampleReviewById(frmSampleReview.getSampleReviewSid()));
        } else {
            return toAjax(row);
        }
    }

    @ApiOperation(value = "修改样品评审单", notes = "修改样品评审单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "样品评审单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid FrmSampleReview frmSampleReview) {
        return toAjax(frmSampleReviewService.updateFrmSampleReview(frmSampleReview));
    }

    /**
     * 变更样品评审单
     */
    @ApiOperation(value = "变更样品评审单", notes = "变更样品评审单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "样品评审单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FrmSampleReview frmSampleReview) {
        return toAjax(frmSampleReviewService.changeFrmSampleReview(frmSampleReview));
    }

    /**
     * 删除样品评审单
     */
    @ApiOperation(value = "删除样品评审单", notes = "删除样品评审单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "样品评审单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sampleReviewSids) {
        if (CollectionUtils.isEmpty(sampleReviewSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(frmSampleReviewService.deleteFrmSampleReviewByIds(sampleReviewSids));
    }

    /**
     * 提交前的校验
     */
    @ApiOperation(value = "提交前的校验", notes = "提交前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submitVerify(@RequestBody FrmSampleReview frmSampleReview) {
        frmSampleReview.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
        return AjaxResult.success(frmSampleReviewService.submitVerify(frmSampleReview));
    }

    /**
     * 提交前的校验查询或者详情页面通过sid
     */
    @ApiOperation(value = "提交前的校验查询或者详情页面通过sid", notes = "提交前的校验查询或者详情页面通过sid")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify/byid")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submitVerify(Long sampleReviewSid) {
        FrmSampleReview frmSampleReview = frmSampleReviewService.selectFrmSampleReviewById(sampleReviewSid);
        if (frmSampleReview != null) {
            frmSampleReview.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
            return AjaxResult.success(frmSampleReviewService.submitVerify(frmSampleReview));
        }
        return AjaxResult.success(EmsResultEntity.success());
    }

    /**
     * 修改样品评审单处理状态（确认）
     */
    @ApiOperation(value = "查询页面点提交/审批通过/审批驳回/确认", notes = "查询页面点提交/审批通过/审批驳回/确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "样品评审单", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody FrmSampleReview frmSampleReview) {
        if (ArrayUtil.isEmpty(frmSampleReview.getSampleReviewSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(frmSampleReview.getHandleStatus())
                && StrUtil.isBlank(frmSampleReview.getBusinessType())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(frmSampleReviewService.check(frmSampleReview));
    }

}
