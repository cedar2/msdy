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
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsAuthorize;
import com.platform.ems.domain.DevMarketSurvey;
import com.platform.ems.service.IDevMarketSurveyService;
import com.platform.ems.service.ISystemDictDataService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 市场调研Controller
 *
 * @author chenkw
 * @date 2022-12-08
 */
@RestController
@RequestMapping("/dev/market/survey")
@Api(tags = "市场调研")
public class DevMarketSurveyController extends BaseController {

    @Autowired
    private IDevMarketSurveyService devMarketSurveyService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private MinioClient client;
    @Autowired
    private MinioConfig minioConfig;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询市场调研列表
     */
    @PostMapping("/list")
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_MARKET_SURVY_ALL)
    @ApiOperation(value = "查询市场调研列表", notes = "查询市场调研列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevMarketSurvey.class))
    public TableDataInfo list(@RequestBody DevMarketSurvey devMarketSurvey) {
        startPage(devMarketSurvey);
        List<DevMarketSurvey> list = devMarketSurveyService.selectDevMarketSurveyListOrderByDesc(devMarketSurvey);
        return getDataTable(list);
    }

    /**
     * 导出市场调研列表
     */
    @Log(title = "市场调研", businessType = BusinessType.EXPORT)
    @CreatorScope(fieldName = "creatorAccount", perms = ConstantsAuthorize.PDM_MARKET_SURVY_ALL, loc = 1)
    @ApiOperation(value = "导出市场调研列表", notes = "导出市场调研列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, DevMarketSurvey devMarketSurvey) throws IOException {
        List<DevMarketSurvey> list = devMarketSurveyService.selectDevMarketSurveyListOrderByDesc(devMarketSurvey);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<DevMarketSurvey> util = new ExcelUtil<>(DevMarketSurvey.class, dataMap);
        util.exportExcel(response, list, "市场调研");
    }

    /**
     * 获取市场调研详细信息
     */
    @ApiOperation(value = "获取市场调研详细信息", notes = "获取市场调研详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevMarketSurvey.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long marketSurveySid) {
        if (marketSurveySid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(devMarketSurveyService.selectDevMarketSurveyById(marketSurveySid));
    }

    /**
     * 复制市场调研详细信息
     */
    @ApiOperation(value = "复制市场调研详细信息", notes = "复制市场调研详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = DevMarketSurvey.class))
    @PostMapping("/copy")
    public AjaxResult copy(Long marketSurveySid) {
        if (marketSurveySid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(devMarketSurveyService.copyDevMarketSurveyById(marketSurveySid));
    }

    /**
     * 新增市场调研
     */
    @ApiOperation(value = "新增市场调研", notes = "新增市场调研")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "市场调研", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid DevMarketSurvey devMarketSurvey) {
        int row = devMarketSurveyService.insertDevMarketSurvey(devMarketSurvey);
        if (row > 0) {
            return AjaxResult.success(devMarketSurveyService.selectDevMarketSurveyById(devMarketSurvey.getMarketSurveySid()));
        } else {
            return toAjax(row);
        }
    }

    @ApiOperation(value = "修改市场调研", notes = "修改市场调研")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "市场调研", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮", interval = 3000)
    public AjaxResult edit(@RequestBody @Valid DevMarketSurvey devMarketSurvey) {
        return toAjax(devMarketSurveyService.updateDevMarketSurvey(devMarketSurvey));
    }

    /**
     * 变更市场调研
     */
    @ApiOperation(value = "变更市场调研", notes = "变更市场调研")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "市场调研", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid DevMarketSurvey devMarketSurvey) {
        return toAjax(devMarketSurveyService.changeDevMarketSurvey(devMarketSurvey));
    }

    /**
     * 删除市场调研
     */
    @ApiOperation(value = "删除市场调研", notes = "删除市场调研")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "市场调研", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> marketSurveySids) {
        if (CollectionUtils.isEmpty(marketSurveySids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(devMarketSurveyService.deleteDevMarketSurveyByIds(marketSurveySids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "市场调研", businessType = BusinessType.ENBLEORDISABLE)
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody DevMarketSurvey devMarketSurvey) {
        if (ArrayUtil.isEmpty(devMarketSurvey.getMarketSurveySidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(devMarketSurvey.getStatus())) {
            throw new CheckedException("参数缺失" );
        }
        return AjaxResult.success(devMarketSurveyService.changeStatus(devMarketSurvey));
    }

    /**
     * 修改市场调研处理状态（确认）
     */
    @ApiOperation(value = "修改市场调研处理状态（确认）", notes = "修改市场调研处理状态（确认）")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "市场调研", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult check(@RequestBody DevMarketSurvey devMarketSurvey) {
        if (ArrayUtil.isEmpty(devMarketSurvey.getMarketSurveySidList())) {
            throw new CheckedException("请勾选行");
        }
        if (StrUtil.isBlank(devMarketSurvey.getHandleStatus())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(devMarketSurveyService.check(devMarketSurvey));
    }

    /**
     * 导入
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入", notes = "导入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(devMarketSurveyService.importData(file));
    }

    /**
     * 下载导入模板
     */
    @ApiOperation(value = "下载导入模板", notes = "下载导入模板")
    @PostMapping("/downloadTemplate")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/PDM_市场调研导入模板_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_导入模板_物料(辅料)_V0.1.xlsx", "UTF-8"));
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
}
