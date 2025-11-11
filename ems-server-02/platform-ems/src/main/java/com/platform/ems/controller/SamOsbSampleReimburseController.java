package com.platform.ems.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.SamOsbSampleReimburse;
import com.platform.ems.domain.dto.request.SamOsbSampleReimburseReportRequert;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.service.ISamOsbSampleReimburseService;
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
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 外采样报销单-主Controller
 *
 * @author qhq
 * @date 2021-12-28
 */
@RestController
@RequestMapping("/sample/reimburse")
@Api(tags = "外采样报销单-主")
public class SamOsbSampleReimburseController extends BaseController {

	@Autowired
	private ISamOsbSampleReimburseService samOsbSampleReimburseService;
	@Autowired
	private ISystemDictDataService sysDictDataService;

	/**
	 * 查询外采样报销单-主列表
	 */
	@PostMapping("/list")
	@ApiOperation(value = "查询外采样报销单-主列表", notes = "查询外采样报销单-主列表")
	@ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SamOsbSampleReimburse.class))
	public TableDataInfo list (@RequestBody SamOsbSampleReimburse samOsbSampleReimburse) {
		startPage(samOsbSampleReimburse);
		List<SamOsbSampleReimburse> list = samOsbSampleReimburseService.selectSamOsbSampleReimburseList(samOsbSampleReimburse);
		return getDataTable(list);
	}

	/**
	 * 导出外采样报销单-主列表
	 */
	@Log(title = "外采样报销单-主", businessType = BusinessType.EXPORT)
	@ApiOperation(value = "导出外采样报销单-主列表", notes = "导出外采样报销单-主列表")
	@ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
	@PostMapping("/export")
	public void export (HttpServletResponse response, SamOsbSampleReimburse samOsbSampleReimburse) throws IOException {
		List<SamOsbSampleReimburse> list = samOsbSampleReimburseService.selectSamOsbSampleReimburseList(samOsbSampleReimburse);
		Map<String, Object> dataMap = sysDictDataService.getDictDataList();
		ExcelUtil<SamOsbSampleReimburse> util = new ExcelUtil<SamOsbSampleReimburse>(SamOsbSampleReimburse.class, dataMap);
		util.exportExcel(response, list, "外采样报销单-主" + DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
	}


	/**
	 * 获取外采样报销单-主详细信息
	 */
	@ApiOperation(value = "获取外采样报销单-主详细信息", notes = "获取外采样报销单-主详细信息")
	@ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SamOsbSampleReimburse.class))
	@PostMapping("/getInfo")
	public AjaxResult getInfo (Long reimburseSid) {
		if (reimburseSid == null) {
			throw new CheckedException("参数缺失");
		}
		return AjaxResult.success(samOsbSampleReimburseService.selectSamOsbSampleReimburseById(reimburseSid));
	}

	/**
	 * 新增外采样报销单-主
	 */
	@ApiOperation(value = "新增外采样报销单-主", notes = "新增外采样报销单-主")
	@ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@Log(title = "外采样报销单-主", businessType = BusinessType.INSERT)
	@PostMapping("/add")
	public AjaxResult add (@RequestBody @Valid SamOsbSampleReimburse samOsbSampleReimburse) {
		return toAjax(samOsbSampleReimburseService.insertSamOsbSampleReimburse(samOsbSampleReimburse));
	}

	/**
	 * 修改外采样报销单-主
	 */
	@ApiOperation(value = "修改外采样报销单-主", notes = "修改外采样报销单-主")
	@ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@Log(title = "外采样报销单-主", businessType = BusinessType.UPDATE)
	@PostMapping("/edit")
	public AjaxResult edit (@RequestBody SamOsbSampleReimburse samOsbSampleReimburse) {
		return toAjax(samOsbSampleReimburseService.updateSamOsbSampleReimburse(samOsbSampleReimburse));
	}

	/**
	 * 变更外采样报销单-主
	 */
	@ApiOperation(value = "变更外采样报销单-主", notes = "变更外采样报销单-主")
	@ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@Log(title = "外采样报销单-主", businessType = BusinessType.CHANGE)
	@PostMapping("/change")
	public AjaxResult change (@RequestBody SamOsbSampleReimburse samOsbSampleReimburse) {
		return toAjax(samOsbSampleReimburseService.changeSamOsbSampleReimburse(samOsbSampleReimburse));
	}

	/**
	 * 删除外采样报销单-主
	 */
	@ApiOperation(value = "删除外采样报销单-主", notes = "删除外采样报销单-主")
	@ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@Log(title = "外采样报销单-主", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
	public AjaxResult remove (@RequestBody List<Long> reimburseSids) {
		if (CollectionUtils.isEmpty(reimburseSids)) {
			throw new CheckedException("参数缺失");
		}
		return toAjax(samOsbSampleReimburseService.deleteSamOsbSampleReimburseByIds(reimburseSids));
	}


	@ApiOperation(value = "确认", notes = "确认")
	@ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@Log(title = "外采样报销单-主", businessType = BusinessType.CHECK)
	@PostMapping("/check")
	public AjaxResult check (@RequestBody SamOsbSampleReimburse samOsbSampleReimburse) {
		return toAjax(samOsbSampleReimburseService.check(samOsbSampleReimburse));
	}

	@ApiOperation(value = "外采样报销单明细报表", notes = "外采样报销单明细报表")
	@ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@PostMapping("/report")
	public TableDataInfo selectReport (@RequestBody SamOsbSampleReimburseReportRequert requert) {
		startPage(requert);
		List<SamOsbSampleReimburseReportRequert> list = samOsbSampleReimburseService.selectReport(requert);
		return getDataTable(list);
	}

	@ApiOperation(value = "外采样报销单明细报表导出", notes = "外采样报销单明细报表导出")
	@ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
	@PostMapping("/report/export")
	public void reportExport (HttpServletResponse response, SamOsbSampleReimburseReportRequert requert) throws IOException {
		List<SamOsbSampleReimburseReportRequert> list = samOsbSampleReimburseService.selectReport(requert);
		list.forEach(o->{
			BigDecimal quantity = new BigDecimal(o.getQuantity());
			o.setMoney(o.getPurchasePrice().multiply(quantity));
		});
		Map<String, Object> dataMap = sysDictDataService.getDictDataList();
		ExcelUtil<SamOsbSampleReimburseReportRequert> util = new ExcelUtil<>(SamOsbSampleReimburseReportRequert.class, dataMap);
		util.exportExcel(response, BeanCopyUtils.copyListProperties(list, SamOsbSampleReimburseReportRequert::new), "外采样报销单明细报表" + DateUtil.format(new Date(), "yyyyMMddHHmmss"));
	}

	@PostMapping("/isCreate")
	public AjaxResult isCreate(@RequestBody List<Long> materialSidList){
		if (CollUtil.isEmpty(materialSidList)) {
			throw new BaseException("参数错误");
		}
		return AjaxResult.success(samOsbSampleReimburseService.isCreate(materialSidList));
	}
}
