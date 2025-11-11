package com.platform.ems.controller;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.domain.BasPosition;
import com.platform.ems.service.IBasPositionService;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 岗位Controller
 *
 * @author qhq
 * @date 2021-03-18
 */
@RestController
@RequestMapping("/position")
@Api(tags = "岗位档案")
public class BasPositionController extends BaseController {

    @Autowired
    private IBasPositionService basPositionService;

    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询岗位列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询岗位列表", notes = "查询岗位列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPosition.class))
    public TableDataInfo list(@RequestBody @Valid BasPosition basPosition) {
        startPage(basPosition);
        List<BasPosition> list = basPositionService.selectBasPositionList(basPosition);
        return getDataTable(list);
    }

    /**
     * 导出岗位列表
     */
    @ApiOperation(value = "导出岗位列表", notes = "导出岗位列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasPosition basPosition) throws IOException {
        List<BasPosition> list = basPositionService.selectBasPositionList(basPosition);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<BasPosition> util = new ExcelUtil<>(BasPosition.class , dataMap);
        util.exportExcel(response, list, "岗位");
    }

    /**
     * 获取岗位详细信息
     */
    @ApiOperation(value = "获取岗位详细信息", notes = "获取岗位详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPosition.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long positionSid) {
        return AjaxResult.success(basPositionService.selectBasPositionById(positionSid));
    }

    /**
     * 新增岗位
     */
    @ApiOperation(value = "新增岗位", notes = "新增岗位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid BasPosition basPosition) {
        int row = basPositionService.insertBasPosition(basPosition);
        return AjaxResult.success(basPosition);
    }

    /**
     * 修改岗位
     */
    @ApiOperation(value = "修改岗位", notes = "修改岗位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid BasPosition basPosition) {
        return toAjax(basPositionService.updateBasPosition(basPosition));
    }

    /**
     * 删除岗位
     */
    @ApiOperation(value = "删除岗位", notes = "删除岗位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> positionSids) {
        return toAjax(basPositionService.deleteBasPositionByIds(positionSids));
    }

    /**
     * 启停用岗位
     *
     * @param basPosition
     * @return
     */
    @PostMapping("/status")
    @ApiOperation(value = "启停用岗位", notes = "启停用岗位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult status(@RequestBody BasPosition basPosition) {
        return toAjax(basPositionService.status(basPosition));
    }

    /**
     * 确认
     *
     * @param basPosition
     * @return
     */
    @PostMapping("/handleStatus")
    @ApiOperation(value = "确认岗位", notes = "确认岗位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult handleStatus(@RequestBody BasPosition basPosition) {
        return toAjax(basPositionService.handleStatus(basPosition));
    }

    /**
     * 获取公司所属的岗位
     */
    @PostMapping("/getCompanyPosition")
    @ApiOperation(value = "获取公司所属的岗位", notes = "获取公司所属的岗位")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPosition.class))
    public AjaxResult getCompanyPosition(Long companySid) {
        return AjaxResult.success(basPositionService.getCompanyPosition(companySid));
    }

    /**
     * 岗位，下拉值为状态为确认且启用、当前操作用户所属员工的所属公司下的岗位档案的数据
     */
    @PostMapping("/getSelfPosition")
    @ApiOperation(value = "岗位，下拉值为状态为确认且启用、当前操作用户所属员工的所属公司下的岗位档案的数据",
            notes = "岗位，下拉值为状态为确认且启用、当前操作用户所属员工的所属公司下的岗位档案的数据")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = BasPosition.class))
    public AjaxResult getSelfPosition(@RequestBody BasPosition basPosition) {
        return AjaxResult.success(basPositionService.getSelfPosition(basPosition));
    }

}
