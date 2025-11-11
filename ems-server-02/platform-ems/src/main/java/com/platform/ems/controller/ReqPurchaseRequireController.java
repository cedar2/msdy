package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.annotation.Idempotent;
import com.platform.common.annotation.Log;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.entity.SysDefaultSettingClient;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ReqPurchaseRequire;
import com.platform.ems.domain.ReqPurchaseRequireItem;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.service.IReqPurchaseRequireItemService;
import com.platform.ems.service.IReqPurchaseRequireService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.system.mapper.SysDefaultSettingClientMapper;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 采购申请单Controller
 *
 * @author linhongwei
 * @date 2021-04-06
 */
@RestController
@RequestMapping("/require")
@Api(tags = "采购申请单")
public class ReqPurchaseRequireController extends BaseController {

    @Autowired
    private IReqPurchaseRequireService reqPurchaseRequireService;
    @Autowired
    private IReqPurchaseRequireItemService reqPurchaseRequireItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    @Autowired
    private SysDefaultSettingClientMapper settingClientMapper;

    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private MinioClient client;

    private static final String FILLE_PATH = "/template";

    /**
     * 查询采购申请单列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询采购申请单列表", notes = "查询采购申请单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ReqPurchaseRequire.class))
    public TableDataInfo list(@RequestBody ReqPurchaseRequire reqPurchaseRequire) {
        startPage(reqPurchaseRequire);
        List<ReqPurchaseRequire> list = reqPurchaseRequireService.selectReqPurchaseRequireList(reqPurchaseRequire);
        return getDataTable(list);
    }

    /**
     * 导出采购申请单列表
     */
    @Log(title = "采购申请单", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购申请单列表", notes = "导出采购申请单列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ReqPurchaseRequire reqPurchaseRequire) throws IOException {
        List<ReqPurchaseRequire> list = reqPurchaseRequireService.selectReqPurchaseRequireList(reqPurchaseRequire);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ReqPurchaseRequire> util = new ExcelUtil<>(ReqPurchaseRequire.class,dataMap);
        util.exportExcel(response, list, "采购申请单");
    }

    /**
     * 获取采购申请单详细信息
     */
    @ApiOperation(value = "获取采购申请单详细信息", notes = "获取采购申请单详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ReqPurchaseRequire.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long purchaseRequireSid) {
        if (purchaseRequireSid == null){
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(reqPurchaseRequireService.selectReqPurchaseRequireById(purchaseRequireSid));
    }

    /**
     * 新增采购申请单
     */
    @ApiOperation(value = "新增采购申请单", notes = "新增采购申请单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购申请单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ReqPurchaseRequire reqPurchaseRequire) {
        reqPurchaseRequireService.insertReqPurchaseRequire(reqPurchaseRequire);
        return AjaxResult.success("操作成功", String.valueOf(reqPurchaseRequire.getPurchaseRequireSid()));
    }

    /**
     * 修改采购申请单
     */
    @ApiOperation(value = "修改采购申请单", notes = "修改采购申请单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @Log(title = "采购申请单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid ReqPurchaseRequire reqPurchaseRequire) {
        return toAjax(reqPurchaseRequireService.updateReqPurchaseRequire(reqPurchaseRequire));
    }

    /**
     * 删除采购申请单
     */
    @ApiOperation(value = "删除采购申请单", notes = "删除采购申请单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "采购申请单", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody Long[] purchaseRequireSids) {
        if (ArrayUtil.isEmpty( purchaseRequireSids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(reqPurchaseRequireService.deleteReqPurchaseRequireByIds(purchaseRequireSids));
    }

    /**
     * 作废
     */
    @ApiOperation(value = "作废", notes = "作废")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "作废", businessType = BusinessType.CANCEL)
    @PostMapping("/cancellation")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult cancellation(@RequestBody ReqPurchaseRequire reqPurchaseRequire) {
        if (ArrayUtil.isEmpty(reqPurchaseRequire.getPurchaseRequireSidList())) {
            throw new CheckedException("请选择行");
        }
        if (StrUtil.isBlank(reqPurchaseRequire.getCancelRemark())) {
            throw new BaseException("请填写作废说明再操作");
        }
        return toAjax(reqPurchaseRequireService.cancellationByIds(reqPurchaseRequire));
    }

    /**
     * 关闭
     */
    @ApiOperation(value = "关闭", notes = "关闭")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "关闭", businessType = BusinessType.CLOSE)
    @PostMapping("/close")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult close(@RequestBody ReqPurchaseRequire reqPurchaseRequire) {
        if (ArrayUtil.isEmpty(reqPurchaseRequire.getPurchaseRequireSidList())) {
            throw new CheckedException("请选择行");
        }
        return toAjax(reqPurchaseRequireService.closeByIds(reqPurchaseRequire));
    }

    /**
     * 采购申请单确认
     */
    @Log(title = "采购申请单", businessType = BusinessType.HANDLE)
    @PostMapping("/check")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "采购申请单确认", notes = "采购申请单确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult confirm(@RequestBody ReqPurchaseRequire reqPurchaseRequire) {
        if (ArrayUtil.isEmpty(reqPurchaseRequire.getPurchaseRequireSidList())) {
            throw new CheckedException("请选择行");
        }
        if (StrUtil.isBlank(reqPurchaseRequire.getHandleStatus())
                && StrUtil.isBlank(reqPurchaseRequire.getBusinessType())) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(reqPurchaseRequireService.check(reqPurchaseRequire));
    }

    /**
     * 采购申请单变更
     */
    @Log(title = "采购申请单", businessType = BusinessType.UPDATE)
    @PostMapping("/change")
    @ApiOperation(value = "采购申请单变更", notes = "采购申请单变更")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult change(@RequestBody @Valid ReqPurchaseRequire reqPurchaseRequire) {
        if (CollectionUtil.isNotEmpty(reqPurchaseRequire.getReqPurchaseRequireItemList()) && !reqPurchaseRequire.isJumpJudgeQuantity()) {
            List<CommonErrMsgResponse> msgList = new ArrayList<>();
            // 租户配置提醒 or 报错 or 跳过
            SysDefaultSettingClient settingClient = settingClientMapper.selectOne(new QueryWrapper<SysDefaultSettingClient>().lambda()
                    .eq(SysDefaultSettingClient::getClientId, ApiThreadLocalUtil.get().getClientId()));
            String type = settingClient == null ? ConstantsEms.S_MESSAGE_DISPLAT_TYPE_BC
                    : StrUtil.isBlank(settingClient.getNoticeTypePurRequireToOrderExcess()) ? ConstantsEms.S_MESSAGE_DISPLAT_TYPE_WU
                    : settingClient.getNoticeTypePurRequireToOrderExcess();
            if (!type.equals(ConstantsEms.S_MESSAGE_DISPLAT_TYPE_WU)) {
                // 遍历
                reqPurchaseRequire.getReqPurchaseRequireItemList().forEach(item->{
                    if (item.getQuantity() != null && item.getHaveReferQuantity() != null &&
                            item.getQuantity().compareTo(item.getHaveReferQuantity()) < 0) {
                        CommonErrMsgResponse msg = new CommonErrMsgResponse();
                        msg.setItemNum(item.getItemNum());
                        String message = type.equals(ConstantsEms.S_MESSAGE_DISPLAT_TYPE_BC) ? "“申请量”不能小于“已转采购量”，请核实！"
                                : "“申请量”小于“已转采购量”，是否继续？";
                        msg.setMsg(message);
                        msgList.add(msg);
                    }
                });
            }
            if (CollectionUtil.isNotEmpty(msgList)) {
                if (ConstantsEms.S_MESSAGE_DISPLAT_TYPE_BC.equals(type)) {
                    return AjaxResult.success(EmsResultEntity.error(msgList));
                }
                else if (ConstantsEms.S_MESSAGE_DISPLAT_TYPE_TS.equals(type)) {
                    return AjaxResult.success(EmsResultEntity.warning(msgList));
                }
            }
        }
        return AjaxResult.success(reqPurchaseRequireService.change(reqPurchaseRequire));
    }

    /**
     * 查询采购申请单-明细列表
     */
    @PostMapping("/getItemList")
    @ApiOperation(value = "查询采购申请单-明细列表", notes = "查询采购申请单-明细列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ReqPurchaseRequireItem.class))
    public TableDataInfo getItemList(@RequestBody ReqPurchaseRequireItem reqPurchaseRequireItem) {
        startPage(reqPurchaseRequireItem);
        List<ReqPurchaseRequireItem> list = reqPurchaseRequireItemService.getItemList(reqPurchaseRequireItem);
        return getDataTable(list);
    }

    /**
     * 导出采购申请单明细报表
     */
    @Log(title = "采购申请单明细报表", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出采购申请单明细报表", notes = "导出采购申请单明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/requireItem/export")
    public void export(HttpServletResponse response, ReqPurchaseRequireItem reqPurchaseRequireItem) throws IOException {
        List<ReqPurchaseRequireItem> list = reqPurchaseRequireItemService.getItemList(reqPurchaseRequireItem);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ReqPurchaseRequireItem> util = new ExcelUtil<>(ReqPurchaseRequireItem.class,dataMap);
        util.exportExcel(response, list, "采购申请单明细报表");
    }

    /**
     * 导入采购申请单
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入采购申请单", notes = "导入采购申请单")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return AjaxResult.success(reqPurchaseRequireService.importData(file));
    }

    @ApiOperation(value = "下载采购申请单导入模板", notes = "下载采购申请单导入模板")
    @PostMapping("/import/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        OutputStream out = null;
        String fileName = FILLE_PATH + "/SCM_导入模板_采购申请单_V1.0.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("SCM_导入模板_采购申请单_V1.0.xlsx", "UTF-8"));
            int len = 0;
            byte[] buffer = new byte[1024];
            out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

        } catch (Exception e) {
            throw new BaseException("读取文件异常:" + e.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
