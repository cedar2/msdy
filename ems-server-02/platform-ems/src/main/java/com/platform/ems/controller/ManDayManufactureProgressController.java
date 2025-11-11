package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.platform.common.constant.HttpStatus;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ManDayManufactureProgress;
import com.platform.ems.domain.ManDayManufactureProgressItem;
import com.platform.ems.domain.ManManufactureOrder;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.mapper.ManDayManufactureProgressMapper;
import com.platform.ems.service.IManDayManufactureKuanProgressService;
import com.platform.ems.service.IManDayManufactureProgressService;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 生产进度日报Controller
 * test
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@RestController
@RequestMapping("/mam/day/manufacture/progress")
@Api(tags = "生产进度日报")
public class ManDayManufactureProgressController extends BaseController {

    @Autowired
    private IManDayManufactureProgressService manDayManufactureProgressService;
    @Autowired
    private ManDayManufactureProgressMapper manDayManufactureProgressMapper;
    @Autowired
    private IManDayManufactureKuanProgressService manDayManufactureKuanProgressService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    /**
     * 查询生产进度日报列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询生产进度日报列表", notes = "查询生产进度日报列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManDayManufactureProgress.class))
    public TableDataInfo list(@RequestBody ManDayManufactureProgress manDayManufactureProgress) {
        TableDataInfo rspData = new TableDataInfo();
        int row = manDayManufactureProgressMapper.selectManDayManufactureProgressCount(manDayManufactureProgress);
        rspData.setTotal(row);
        rspData.setRows(new ArrayList<>());
        if (row > 0) {
            List<ManDayManufactureProgress> list = manDayManufactureProgressService.selectManDayManufactureProgressList(manDayManufactureProgress);
            if (list != null) {
                rspData.setRows(list);
            }
        }
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        return rspData;
    }

    /**
     * 导出生产进度日报列表
     */
    @Log(title = "生产进度日报", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出生产进度日报列表", notes = "导出生产进度日报列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ManDayManufactureProgress manDayManufactureProgress) throws IOException {
        List<ManDayManufactureProgress> list = manDayManufactureProgressService.selectManDayManufactureProgressList(manDayManufactureProgress);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ManDayManufactureProgress> util = new ExcelUtil<>(ManDayManufactureProgress.class, dataMap);
        util.exportExcel(response, list, "班组生产日报");
    }

    /**
     * 获取生产进度日报详细信息
     */
    @ApiOperation(value = "获取生产进度日报详细信息", notes = "获取生产进度日报详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManDayManufactureProgress.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long dayManufactureProgressSid) {
        if (dayManufactureProgressSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(manDayManufactureProgressService.selectManDayManufactureProgressById(dayManufactureProgressSid));
    }

    /**
     * 新增生产进度日报
     */
    @ApiOperation(value = "新增生产进度日报", notes = "新增生产进度日报")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产进度日报", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ManDayManufactureProgress manDayManufactureProgress) {
        if (judge(manDayManufactureProgress)) {
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            msgList.add(new CommonErrMsgResponse().setMsg("“当天完成量”必须要大于0"));
            return AjaxResult.success(EmsResultEntity.warning(msgList, null));
        }
        return AjaxResult.success(manDayManufactureProgressService.insertManDayManufactureProgress(manDayManufactureProgress));
    }

    /**
     * 新增/编辑直接提交生产进度日报
     */
    @Log(title = "新增/编辑直接提交生产进度日报", businessType = BusinessType.SUBMIT)
    @ApiOperation(value = "新增/编辑直接提交生产进度日报", notes = "新增/编辑直接提交生产进度日报")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/submit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult submit(@RequestBody @Valid ManDayManufactureProgress manDayManufactureProgress) {
        if (judge(manDayManufactureProgress)) {
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            msgList.add(new CommonErrMsgResponse().setMsg("“当天完成量”必须要大于0"));
            return AjaxResult.success(EmsResultEntity.warning(msgList, null));
        }
        return manDayManufactureProgressService.submit(manDayManufactureProgress);
    }

    /**
     * 修改生产进度日报
     */
    @ApiOperation(value = "修改生产进度日报", notes = "修改生产进度日报")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产进度日报", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ManDayManufactureProgress manDayManufactureProgress) {
        if (judge(manDayManufactureProgress)) {
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            msgList.add(new CommonErrMsgResponse().setMsg("“当天完成量”必须要大于0"));
            return AjaxResult.success(EmsResultEntity.warning(msgList, null));
        }
        return toAjax(manDayManufactureProgressService.updateManDayManufactureProgress(manDayManufactureProgress));
    }

    public static boolean judge(@RequestBody @Valid ManDayManufactureProgress manDayManufactureProgress) {
        if (CollectionUtil.isNotEmpty(manDayManufactureProgress.getDayManufactureProgressItemList()) && !ConstantsEms.YES.equals(manDayManufactureProgress.getContinueAtleast())) {
            for (ManDayManufactureProgressItem item : manDayManufactureProgress.getDayManufactureProgressItemList()) {
                if (BigDecimal.ZERO.compareTo(item.getQuantity()==null?BigDecimal.ZERO:item.getQuantity()) >= 0){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 变更生产进度日报
     */


    @ApiOperation(value = "变更生产进度日报", notes = "变更生产进度日报")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产进度日报", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ManDayManufactureProgress manDayManufactureProgress) {
        if (judge(manDayManufactureProgress)) {
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            msgList.add(new CommonErrMsgResponse().setMsg("“当天完成量”必须要大于0"));
            return AjaxResult.success(EmsResultEntity.warning(msgList, null));
        }
        return toAjax(manDayManufactureProgressService.changeManDayManufactureProgress(manDayManufactureProgress));
    }

    /**
     * 删除生产进度日报
     */
    @ApiOperation(value = "删除生产进度日报", notes = "删除生产进度日报")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产进度日报", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> dayManufactureProgressSids) {
        if (CollectionUtils.isEmpty(dayManufactureProgressSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(manDayManufactureProgressService.deleteManDayManufactureProgressByIds(dayManufactureProgressSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "生产进度日报", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ManDayManufactureProgress manDayManufactureProgress) {
        return toAjax(manDayManufactureProgressService.check(manDayManufactureProgress));
    }

    /**
     * 提交前校验-生产进度日报
     */
    @ApiOperation(value = "提交前校验-生产进度日报", notes = "提交前校验-生产进度日报")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/verify")
    public AjaxResult verify(Long dayManufactureProgressSid, String handleStatus) {
        if (dayManufactureProgressSid == null || StrUtil.isEmpty(handleStatus)) {
            throw new BaseException("参数缺失");
        }
        manDayManufactureProgressService.verify(dayManufactureProgressSid, handleStatus);
        // 校验完直接确认
        Long[] dayManufactureProgressSidList = new Long[1];
        dayManufactureProgressSidList[0] = dayManufactureProgressSid;
        return toAjax(manDayManufactureProgressService.check(new ManDayManufactureProgress()
                .setDayManufactureProgressSidList(dayManufactureProgressSidList).setHandleStatus(ConstantsEms.CHECK_STATUS)));
    }

    /**
     * 更新款生产进度
     */
    @PostMapping("/kuan/listBy")
    @ApiOperation(value = "更新款生产进度", notes = "更新款生产进度")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ManManufactureOrder.class))
    public AjaxResult kuanList(@RequestBody ManDayManufactureProgress manDayManufactureProgress) {
        if (CollectionUtil.isEmpty(manDayManufactureProgress.getDayManufactureProgressItemList())) {
            return AjaxResult.success(new ArrayList<>());
        }
        return AjaxResult.success(manDayManufactureKuanProgressService.selectManDayManufactureProgressKuanList(manDayManufactureProgress));
    }

}
