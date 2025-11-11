package com.platform.ems.plug.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConPurchaseGroup;
import com.platform.ems.plug.service.IConPurchaseGroupService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 采购组Controller
 *
 * @author chenkw
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/purchase/group")
@Api(tags = "采购组")
public class ConPurchaseGroupController extends BaseController {

    @Autowired
    private IConPurchaseGroupService conPurchaseGroupService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询采购组列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询采购组列表", notes = "查询采购组列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPurchaseGroup.class))
    public TableDataInfo list(@RequestBody ConPurchaseGroup conPurchaseGroup) {
        startPage(conPurchaseGroup);
        List<ConPurchaseGroup> list = conPurchaseGroupService.selectConPurchaseGroupList(conPurchaseGroup);
        return getDataTable(list);
    }

    /**
     * 导出采购组列表
     */
    @Log(title = "采购组", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购组列表", notes = "导出采购组列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConPurchaseGroup conPurchaseGroup) throws IOException {
        List<ConPurchaseGroup> list = conPurchaseGroupService.selectConPurchaseGroupList(conPurchaseGroup);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConPurchaseGroup> util = new ExcelUtil<>(ConPurchaseGroup.class, dataMap);
        util.exportExcel(response, list, "采购组");
    }

    /**
     * 导入采购组
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入采购组", notes = "导入采购组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConPurchaseGroup> util = new ExcelUtil<ConPurchaseGroup>(ConPurchaseGroup.class);
        List<ConPurchaseGroup> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conPurchaseGroup -> {
                conPurchaseGroupService.insertConPurchaseGroup(conPurchaseGroup);
                i++;
            });
        } catch (Exception e) {
            lose = listSize - i;
            msg = StrUtil.format("前{}条数据导入成功，失败{}条,导入成功的数据请勿重复导入", i, lose);
        }
        if (StrUtil.isEmpty(msg)) {
            msg = "导入成功";
        }
        return AjaxResult.success(msg);
    }


    @ApiOperation(value = "下载采购组导入模板", notes = "下载采购组导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConPurchaseGroup> util = new ExcelUtil<ConPurchaseGroup>(ConPurchaseGroup.class);
        util.importTemplateExcel(response, "采购组导入模板");
    }


    /**
     * 获取采购组详细信息
     */
    @ApiOperation(value = "获取采购组详细信息", notes = "获取采购组详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPurchaseGroup.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conPurchaseGroupService.selectConPurchaseGroupById(sid));
    }

    /**
     * 新增采购组
     */
    @ApiOperation(value = "新增采购组", notes = "新增采购组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购组", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConPurchaseGroup conPurchaseGroup) {
        return toAjax(conPurchaseGroupService.insertConPurchaseGroup(conPurchaseGroup));
    }

    /**
     * 修改采购组
     */
    @ApiOperation(value = "修改采购组", notes = "修改采购组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购组", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConPurchaseGroup conPurchaseGroup) {
        return toAjax(conPurchaseGroupService.updateConPurchaseGroup(conPurchaseGroup));
    }

    /**
     * 变更采购组
     */
    @ApiOperation(value = "变更采购组", notes = "变更采购组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购组", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConPurchaseGroup conPurchaseGroup) {
        return toAjax(conPurchaseGroupService.changeConPurchaseGroup(conPurchaseGroup));
    }

    /**
     * 删除采购组
     */
    @ApiOperation(value = "删除采购组", notes = "删除采购组")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购组", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conPurchaseGroupService.deleteConPurchaseGroupByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购组", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConPurchaseGroup conPurchaseGroup) {
        return AjaxResult.success(conPurchaseGroupService.changeStatus(conPurchaseGroup));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购组", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConPurchaseGroup conPurchaseGroup) {
        conPurchaseGroup.setConfirmDate(new Date());
        conPurchaseGroup.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conPurchaseGroup.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conPurchaseGroupService.check(conPurchaseGroup));
    }

    /**
     * 采购组下拉框
     */
    @PostMapping("/getList")
    @ApiOperation(value = "采购组下拉框", notes = "采购组下拉框")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPurchaseGroup.class))
    public AjaxResult getList() {
        return AjaxResult.success(conPurchaseGroupService.getList());
    }

    /**
     * 采购组下拉框（带参数）
     */
    @PostMapping("/getPurchaseGroupList")
    @ApiOperation(value = "采购组下拉框（带参数）", notes = "采购组下拉框（带参数）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConPurchaseGroup.class))
    public AjaxResult getPurchaseGroupList(@RequestBody ConPurchaseGroup conPurchaseGroup) {
        return AjaxResult.success(conPurchaseGroupService.getPurchaseGroupList(conPurchaseGroup));
    }
}
