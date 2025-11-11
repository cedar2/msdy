package com.platform.ems.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.annotation.CreatorScope;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsAuthorize;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.FrmDraftDesign;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.service.IFrmDraftDesignService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 图稿绘制单Controller
 *
 * @author linhongwei
 * @date 2022-12-12
 */
@RestController
@RequestMapping("/frm/draft/design")
@Api(tags = "图稿绘制单")
public class FrmDraftDesignController extends BaseController {

    @Autowired
    private IFrmDraftDesignService frmDraftDesignService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询图稿绘制单列表
     */
    @PostMapping("/list")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_DRAFT_DESIGN_ALL)
    @ApiOperation(value = "查询图稿绘制单列表", notes = "查询图稿绘制单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmDraftDesign.class))
    public TableDataInfo list(@RequestBody FrmDraftDesign frmDraftDesign) {
        startPage(frmDraftDesign);
        List<FrmDraftDesign> list = frmDraftDesignService.selectFrmDraftDesignListOrderByDesc(frmDraftDesign);
        return getDataTable(list);
    }

    /**
     * 导出图稿绘制单列表
     */
    @Log(title = "图稿绘制单", businessType = BusinessType.EXPORT)
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_DRAFT_DESIGN_ALL, loc = 1)
    @ApiOperation(value = "导出图稿绘制单列表", notes = "导出图稿绘制单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, FrmDraftDesign frmDraftDesign) throws IOException {
        List<FrmDraftDesign> list = frmDraftDesignService.selectFrmDraftDesignListOrderByDesc(frmDraftDesign);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<FrmDraftDesign> util = new ExcelUtil<>(FrmDraftDesign.class, dataMap);
        util.exportExcel(response, list, "图稿绘制单");
    }


    /**
     * 获取图稿绘制单详细信息
     */
    @ApiOperation(value = "获取图稿绘制单详细信息", notes = "获取图稿绘制单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = FrmDraftDesign.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long draftDesignSid) {
        if (draftDesignSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(frmDraftDesignService.selectFrmDraftDesignById(draftDesignSid));
    }

    /**
     * 提交前的校验
     */
    @ApiOperation(value = "提交前的校验", notes = "提交前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submitVerify(@RequestBody FrmDraftDesign frmDraftDesign) {
        frmDraftDesign.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
        return AjaxResult.success(frmDraftDesignService.submitVerify(frmDraftDesign));
    }

    /**
     * 提交前的校验  通过sid
     */
    @ApiOperation(value = "提交前的校验", notes = "提交前的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit/verify/byid")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submitVerify(Long draftDesignSid) {
        FrmDraftDesign draftDesign = frmDraftDesignService.selectFrmDraftDesignById(draftDesignSid);
        if (draftDesign != null) {
            draftDesign.setHandleStatus(ConstantsEms.SUBMIT_STATUS);
            return AjaxResult.success(frmDraftDesignService.submitVerify(draftDesign));
        }
        return AjaxResult.success(EmsResultEntity.success());
    }

    /**
     * 新增图稿绘制单
     */
    @ApiOperation(value = "新增图稿绘制单", notes = "新增图稿绘制单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "图稿绘制单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid FrmDraftDesign frmDraftDesign) {
        int row = frmDraftDesignService.insertFrmDraftDesign(frmDraftDesign);
        if (row > 0) {
            return AjaxResult.success(frmDraftDesignService.selectFrmDraftDesignById(frmDraftDesign.getDraftDesignSid()));
        } else {
            return toAjax(row);
        }
    }

    @ApiOperation(value = "修改图稿绘制单", notes = "修改图稿绘制单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "图稿绘制单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid FrmDraftDesign frmDraftDesign) {
        return toAjax(frmDraftDesignService.updateFrmDraftDesign(frmDraftDesign));
    }

    /**
     * 变更图稿绘制单
     */
    @ApiOperation(value = "变更图稿绘制单", notes = "变更图稿绘制单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "图稿绘制单", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid FrmDraftDesign frmDraftDesign) {
        return toAjax(frmDraftDesignService.changeFrmDraftDesign(frmDraftDesign));
    }

    /**
     * 删除图稿绘制单
     */
    @ApiOperation(value = "删除图稿绘制单", notes = "删除图稿绘制单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "图稿绘制单", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> draftDesignSids) {
        if (CollectionUtils.isEmpty(draftDesignSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(frmDraftDesignService.deleteFrmDraftDesignByIds(draftDesignSids));
    }

    /**
     * 修改项目档案处理状态（确认）
     */
    @ApiOperation(value = "修改项目档案处理状态（确认）", notes = "修改项目档案处理状态（确认）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "图稿绘制单", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody FrmDraftDesign frmDraftDesign) {
        if (ArrayUtil.isEmpty(frmDraftDesign.getDraftDesignSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(frmDraftDesign.getHandleStatus())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(frmDraftDesignService.check(frmDraftDesign));
    }

    /**
     * 审批
     */
    @ApiOperation(value = "审批", notes = "审批")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "图稿绘制单", businessType = BusinessType.HANDLE)
    @PostMapping("/approval")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult approval(@RequestBody FrmDraftDesign frmDraftDesign) {
        if (ArrayUtil.isEmpty(frmDraftDesign.getDraftDesignSidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(frmDraftDesign.getHandleStatus())
                && StrUtil.isBlank(frmDraftDesign.getBusinessType())) {
            throw new BaseException("参数缺失");
        }
        return toAjax(frmDraftDesignService.approval(frmDraftDesign));
    }

}
