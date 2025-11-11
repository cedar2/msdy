package com.platform.ems.plug.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.SysClientMovementType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.plug.domain.ConMovementType;
import com.platform.ems.plug.service.IConMovementTypeService;
import com.platform.ems.service.ISysClientMovementTypeService;
import com.platform.ems.service.ISystemDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 作业类型(移动类型)Controller
 *
 * @author linhongwei
 * @date 2021-05-20
 */
@RestController
@RequestMapping("/movement/type")
@Api(tags = "作业类型(移动类型)")
public class ConMovementTypeController extends BaseController {

    @Autowired
    private IConMovementTypeService conMovementTypeService;
    @Autowired
    private ISysClientMovementTypeService conClientMovementTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询作业类型(移动类型)列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询作业类型(移动类型)列表", notes = "查询作业类型(移动类型)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMovementType.class))
    public TableDataInfo list(@RequestBody ConMovementType conMovementType) {
        startPage(conMovementType);
        List<ConMovementType> list = conMovementTypeService.conMovementTypeList(conMovementType);
        return getDataTable(list);
    }

    /**
     * 导出作业类型(移动类型)列表
     */
    @Log(title = "作业类型(移动类型)", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出作业类型(移动类型)列表", notes = "导出作业类型(移动类型)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConMovementType conMovementType) throws IOException {
        List<ConMovementType> list = conMovementTypeService.conMovementTypeList(conMovementType);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ConMovementType> util = new ExcelUtil<>(ConMovementType.class, dataMap);
        util.exportExcel(response, list, "作业类型(移动类型)");
    }

    /**
     * 获取作业类型(移动类型)详细信息
     */
    @ApiOperation(value = "获取作业类型(移动类型)详细信息", notes = "获取作业类型(移动类型)详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMovementType.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
        if (sid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(conMovementTypeService.conMovementTypeById(sid));
    }

    /**
     * 新增作业类型(移动类型)
     */
    @ApiOperation(value = "新增作业类型(移动类型)", notes = "新增作业类型(移动类型)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "作业类型(移动类型)", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConMovementType conMovementType) {
        return toAjax(conMovementTypeService.insertConMovementType(conMovementType));
    }

    /**
     * 修改作业类型(移动类型)
     */
    @ApiOperation(value = "修改作业类型(移动类型)", notes = "修改作业类型(移动类型)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "作业类型(移动类型)", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ConMovementType conMovementType) {
        return toAjax(conMovementTypeService.updateConMovementType(conMovementType));
    }

    /**
     * 变更作业类型(移动类型)
     */
    @ApiOperation(value = "变更作业类型(移动类型)", notes = "变更作业类型(移动类型)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "作业类型(移动类型)", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConMovementType conMovementType) {
        return toAjax(conMovementTypeService.changeConMovementType(conMovementType));
    }

    /**
     * 删除作业类型(移动类型)
     */
    @ApiOperation(value = "删除作业类型(移动类型)", notes = "删除作业类型(移动类型)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "作业类型(移动类型)", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> sids) {
        if (ArrayUtil.isEmpty(sids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(conMovementTypeService.deleteConMovementTypeByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "作业类型(移动类型)", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConMovementType conMovementType) {
        return AjaxResult.success(conMovementTypeService.changeStatus(conMovementType));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "作业类型(移动类型)", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConMovementType conMovementType) {
        conMovementType.setConfirmDate(new Date());
        conMovementType.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conMovementType.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conMovementTypeService.check(conMovementType));
    }

    /**
     * 获取(移动类型)列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "查询作业类型(移动类型)列表", notes = "查询作业类型(移动类型)列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMovementType.class))
    public AjaxResult getList(@RequestBody ConMovementType conMovementType) {
        // 获取租户对应作业类型配置
        List<SysClientMovementType> clientList = conClientMovementTypeService.selectSysClientMovementTypeList(new SysClientMovementType()
                .setClientId(ApiThreadLocalUtil.get().getClientId()));
        if (CollectionUtil.isNotEmpty(clientList)){
            Long[] clientMovementSid = clientList.stream().map(SysClientMovementType::getMovementTypeSid).toArray(Long[]::new);
            conMovementType.setSidList(clientMovementSid);
        }
        // 是否一步调拨, 单据类别出入库对应单据类别表,  库存凭证类别编码
        conMovementType.setInvDocCategoryCode(conMovementType.getCode());
        List<ConMovementType> movementTypeList = conMovementTypeService.getList(conMovementType);
        if (CollectionUtil.isNotEmpty(movementTypeList)){
            return AjaxResult.success(movementTypeList);
        }
        else {
            return AjaxResult.success(new AjaxResult());
        }
    }

    /**
     * 下拉框列表
     */
    @PostMapping("/getConMovementTypeList")
    @ApiOperation(value = "作业类型下拉框列表", notes = "作业类型下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMovementType.class))
    public AjaxResult getConMovementTypeList() {
        return AjaxResult.success(conMovementTypeService.getConMovementTypeList());
    }

    /**
     * 下拉框列表
     */
    @PostMapping("/getMovementList")
    @ApiOperation(value = "作业类型下拉(带参)", notes = "作业类型下拉(带参)")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConMovementType.class))
    public AjaxResult getMovementList(@RequestBody ConMovementType conMovementType) {
        return AjaxResult.success(conMovementTypeService.getMovementList(conMovementType));
    }

}
