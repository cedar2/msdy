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
import com.platform.ems.plug.domain.ConProductTechniqueType;
import com.platform.ems.plug.service.IConProductTechniqueTypeService;
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
 * 生产工艺方法(编织方法)Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/product/technique/type")
@Api(tags = "生产工艺方法(编织方法)")
public class ConProductTechniqueTypeController extends BaseController {

    @Autowired
    private IConProductTechniqueTypeService conProductTechniqueTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;

    /**
     * 查询生产工艺方法(编织方法)列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询生产工艺方法(编织方法)列表", notes = "查询生产工艺方法(编织方法)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConProductTechniqueType.class))
    public TableDataInfo list(@RequestBody ConProductTechniqueType conProductTechniqueType) {
        startPage(conProductTechniqueType);
        List<ConProductTechniqueType> list = conProductTechniqueTypeService.selectConProductTechniqueTypeList(conProductTechniqueType);
        return getDataTable(list);
    }

    /**
     * 导出生产工艺方法(编织方法)列表
     */
    @Log(title = "生产工艺方法(编织方法)", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产工艺方法(编织方法)列表", notes = "导出生产工艺方法(编织方法)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConProductTechniqueType conProductTechniqueType) throws IOException {
        List<ConProductTechniqueType> list = conProductTechniqueTypeService.selectConProductTechniqueTypeList(conProductTechniqueType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConProductTechniqueType> util = new ExcelUtil<>(ConProductTechniqueType.class, dataMap);
        util.exportExcel(response, list, "生产工艺方法(编织方法)");
    }

    /**
     * 导入生产工艺方法(编织方法)
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入生产工艺方法(编织方法)", notes = "导入生产工艺方法(编织方法)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        ExcelUtil<ConProductTechniqueType> util = new ExcelUtil<>(ConProductTechniqueType.class);
        List<ConProductTechniqueType> list = util.importExcel(file.getInputStream());
        Integer listSize = list.size();
        Integer lose = 0;
        String msg = "";
        try {
            list.stream().forEach(conProductTechniqueType -> {
                conProductTechniqueTypeService.insertConProductTechniqueType(conProductTechniqueType);
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


    @ApiOperation(value = "下载生产工艺方法(编织方法)导入模板", notes = "下载生产工艺方法(编织方法)导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConProductTechniqueType> util = new ExcelUtil<>(ConProductTechniqueType.class);
        util.importTemplateExcel(response, "生产工艺方法(编织方法)导入模板");
    }


    /**
     * 获取生产工艺方法(编织方法)详细信息
     */
    @ApiOperation(value = "获取生产工艺方法(编织方法)详细信息", notes = "获取生产工艺方法(编织方法)详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConProductTechniqueType.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conProductTechniqueTypeService.selectConProductTechniqueTypeById(sid));
    }

    /**
     * 新增生产工艺方法(编织方法)
     */
    @ApiOperation(value = "新增生产工艺方法(编织方法)", notes = "新增生产工艺方法(编织方法)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产工艺方法(编织方法)", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConProductTechniqueType conProductTechniqueType) {
        return toAjax(conProductTechniqueTypeService.insertConProductTechniqueType(conProductTechniqueType));
    }

    /**
     * 修改生产工艺方法(编织方法)
     */
    @ApiOperation(value = "修改生产工艺方法(编织方法)", notes = "修改生产工艺方法(编织方法)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产工艺方法(编织方法)", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConProductTechniqueType conProductTechniqueType) {
        return toAjax(conProductTechniqueTypeService.updateConProductTechniqueType(conProductTechniqueType));
    }

    /**
     * 变更生产工艺方法(编织方法)
     */
    @ApiOperation(value = "变更生产工艺方法(编织方法)", notes = "变更生产工艺方法(编织方法)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产工艺方法(编织方法)", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConProductTechniqueType conProductTechniqueType) {
        return toAjax(conProductTechniqueTypeService.changeConProductTechniqueType(conProductTechniqueType));
    }

    /**
     * 删除生产工艺方法(编织方法)
     */
    @ApiOperation(value = "删除生产工艺方法(编织方法)", notes = "删除生产工艺方法(编织方法)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产工艺方法(编织方法)", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conProductTechniqueTypeService.deleteConProductTechniqueTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产工艺方法(编织方法)", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConProductTechniqueType conProductTechniqueType) {
        return AjaxResult.success(conProductTechniqueTypeService.changeStatus(conProductTechniqueType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产工艺方法(编织方法)", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConProductTechniqueType conProductTechniqueType) {
        conProductTechniqueType.setConfirmDate(new Date());
        conProductTechniqueType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conProductTechniqueType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conProductTechniqueTypeService.check(conProductTechniqueType));
    }

    @PostMapping("/getConProductTechniqueTypeList")
    @ApiOperation(value = "生产工艺类型下拉列表", notes = "生产工艺类型下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConProductTechniqueType.class))
    public AjaxResult getConProductTechniqueTypeList() {
        return AjaxResult.success(conProductTechniqueTypeService.getConProductTechniqueTypeList());
    }

}
