package com.platform.ems.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.ems.annotation.FieldScope;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.PayProcessStepComplete;
import com.platform.ems.domain.PayProcessStepCompleteItem;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.PayProcessStepCompleteTableRequest;
import com.platform.ems.domain.dto.request.PayProcessStepCompleteVerifyRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.PayProcessStepCompleteTableResponse;
import com.platform.ems.domain.dto.response.export.PayProcessStepCompleteItemFormExport;
import com.platform.ems.domain.dto.response.form.ProductProcessCompleteStatisticsSalary;
import com.platform.ems.mapper.PayProcessStepCompleteItemMapper;
import com.platform.ems.service.IBasStaffService;
import com.platform.ems.service.IPayProcessStepCompleteItemService;
import com.platform.ems.service.IPayProcessStepCompleteService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.api.service.RemoteSystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 计薪量申报-主Controller
 *
 * @author linhongwei
 * @date 2021-09-08
 */
@RestController
@RequestMapping("/process/step/complete")
@Api(tags = "计薪量申报-主")
public class PayProcessStepCompleteController extends BaseController {

    @Autowired
    private IPayProcessStepCompleteService payProcessStepCompleteService;
    @Autowired
    private PayProcessStepCompleteItemMapper payProcessStepCompleteItemMapper;
    @Autowired
    private IPayProcessStepCompleteItemService payProcessStepCompleteItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private IBasStaffService basStaffService;
    @Autowired
    private RemoteSystemService remoteSystemService;

    /**
     * 查询计薪量申报-主列表
     */
    @PostMapping("/list")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all")
    @ApiOperation(value = "查询计薪量申报-主列表", notes = "查询计薪量申报-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStepComplete.class))
    public TableDataInfo list(@RequestBody PayProcessStepComplete payProcessStepComplete) {
        startPage(payProcessStepComplete);
        List<PayProcessStepComplete> list = payProcessStepCompleteService.selectPayProcessStepCompleteList(payProcessStepComplete);
        return getDataTable(list);
    }

    /**
     * 导出计薪量申报-主列表
     */
    @PostMapping("/export")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all", loc = 1)
    @ApiOperation(value = "导出计薪量申报-主列表", notes = "导出计薪量申报-主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void export(HttpServletResponse response, PayProcessStepComplete payProcessStepComplete) throws IOException {
        List<PayProcessStepComplete> list = payProcessStepCompleteService.selectPayProcessStepCompleteList(payProcessStepComplete);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PayProcessStepComplete> util = new ExcelUtil<>(PayProcessStepComplete.class, dataMap);
        util.exportExcel(response, list, "计薪量申报");
    }


    /**
     * 获取计薪量申报-主详细信息
     */
    @ApiOperation(value = "获取计薪量申报-主详细信息", notes = "获取计薪量申报-主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStepComplete.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long stepCompleteSid) {
        if (stepCompleteSid == null) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(payProcessStepCompleteService.selectPayProcessStepCompleteById(stepCompleteSid));
    }

    /**
     * 新增计薪量申报-主
     */
    @ApiOperation(value = "新增计薪量申报-主", notes = "新增计薪量申报-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid PayProcessStepComplete payProcessStepComplete) {
        return AjaxResult.success(null, payProcessStepCompleteService.insertPayProcessStepComplete(payProcessStepComplete));
    }

    /**
     * 修改计薪量申报-主
     */
    @ApiOperation(value = "修改计薪量申报-主", notes = "修改计薪量申报-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid PayProcessStepComplete payProcessStepComplete) {
        return toAjax(payProcessStepCompleteService.updatePayProcessStepComplete(payProcessStepComplete));
    }

    /**
     * 变更计薪量申报-主
     */
    @ApiOperation(value = "变更计薪量申报-主", notes = "变更计薪量申报-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid PayProcessStepComplete payProcessStepComplete) {
        return toAjax(payProcessStepCompleteService.changePayProcessStepComplete(payProcessStepComplete));
    }

    /**
     * 删除计薪量申报-主
     */
    @ApiOperation(value = "删除计薪量申报-主", notes = "删除计薪量申报-主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> stepCompleteSids) {
        if (CollectionUtils.isEmpty(stepCompleteSids)) {
            throw new CheckedException("参数缺失");
        }
        return toAjax(payProcessStepCompleteService.deletePayProcessStepCompleteByIds(stepCompleteSids));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @PostMapping("/check")
    public AjaxResult check(@RequestBody PayProcessStepComplete payProcessStepComplete) {
        Long[] sids = payProcessStepComplete.getStepCompleteSidList();
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        for (Long sid : sids) {
            PayProcessStepComplete resquest = new PayProcessStepComplete();
            BeanCopyUtils.copyProperties(payProcessStepComplete, resquest);
            resquest.setStepCompleteSidList(new Long[]{sid});
            EmsResultEntity result = payProcessStepCompleteService.confirm(resquest);
            if (EmsResultEntity.ERROR_TAG.equals(result.getTag())) {
                errMsgList.addAll(result.getMsgList());
            }
        }
        if (CollectionUtil.isNotEmpty(errMsgList)) {
            return AjaxResult.success(EmsResultEntity.error(errMsgList));
        }
        return AjaxResult.success(EmsResultEntity.success());
    }

    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "新建编辑的确认前校验", notes = "新建编辑的确认前校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/verify")
    public AjaxResult verify(@RequestBody PayProcessStepComplete payProcessStepComplete) {
        String sid = payProcessStepCompleteService.verifyUnique(payProcessStepComplete);
        if (sid != null){
            throw new BaseException("工厂(工序)+班组+操作部门+商品工价类型+计薪完工类型+所属年月”组合已存在计薪量申报单");
        }
        if (ConstantsEms.CHECK_STATUS.equals(payProcessStepComplete.getHandleStatus())) {
            List<PayProcessStepCompleteItem> itemList = payProcessStepComplete.getPayProcessStepCompleteItemList();
            if (CollectionUtil.isEmpty(itemList)) {
                throw new BaseException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }
            return AjaxResult.success(payProcessStepCompleteService.checkVerify(payProcessStepComplete));
        }
        return AjaxResult.success();
    }

    /**
     * 唯一性校验新建编辑页面点暂存调用这一个接口直接会报错已存在
     */
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "新建编辑的暂存前校验", notes = "新建编辑的暂存前校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/verifyQuantity")
    public AjaxResult verifyQuantity(@RequestBody PayProcessStepComplete payProcessStepComplete) {
        String sid = payProcessStepCompleteService.verifyUnique(payProcessStepComplete);
        if (sid != null){
            throw new BaseException("工厂(工序)+班组+操作部门+商品工价类型+计薪完工类型+所属年月+商品编码(款号)+录入维度”组合已存在计薪量申报单");
        }
        EmsResultEntity entity = payProcessStepCompleteService.checkVerify(payProcessStepComplete);
        return AjaxResult.success(entity);
    }

    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "明细弹出框添加明细计件结算的校验", notes = "明细弹出框添加明细计件结算的校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/verifyJijina")
    public AjaxResult verifyJijina(@RequestBody PayProcessStepCompleteVerifyRequest request) {
        List<PayProcessStepCompleteItem> newList = request.getNewList();
        PayProcessStepComplete payProcessStepComplete = request.getPayProcessStepComplete();
        if (CollectionUtil.isEmpty(newList)) {
            return AjaxResult.success();
        }
        if (payProcessStepComplete.getStepCompleteSid() == null) {
            payProcessStepComplete.setHandleStatus(ConstantsEms.SAVA_STATUS);
        }
        // 前端为了避免加进明细行后校验失败了但写入了明细行的情况 ，所以后端在这个接口 要 把新的明细行写进总的明细行里去。
        payProcessStepComplete.getPayProcessStepCompleteItemList().addAll(newList);
        EmsResultEntity entity = payProcessStepCompleteItemService.checkPayProductJijianSettleInforBySelect(newList, payProcessStepComplete);
        return AjaxResult.success(entity);
    }

    /**
     * 累计计薪量申报、生产订单量
     */
    @ApiOperation(value = "累计计薪量申报、生产订单量", notes = "累计计薪量申报、生产订单量")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStepCompleteItem.class))
    @PostMapping("/getQuantity")
    public AjaxResult getQuantity(@RequestBody PayProcessStepCompleteItem payProcessStepCompleteItem) {
        return AjaxResult.success(payProcessStepCompleteService.getQuantity(payProcessStepCompleteItem));
    }

    /**
     * 唯一性校验
     * 新建页面点添加明细时调用这个接口，弹出提示并可以选择是否跳转页面
     * 而新建编辑页面点暂存则调用另一个接口直接会报错已存在
     */
    @PostMapping("/verifyUnique")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    @ApiOperation(value = "唯一性校验", notes = "唯一性校验")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStepComplete.class))
    public AjaxResult verifyUnique(@RequestBody PayProcessStepComplete payProcessStepComplete) {
        String sid = payProcessStepCompleteService.verifyUnique(payProcessStepComplete);
        if (sid != null){
            return AjaxResult.success("工厂(工序)+班组+操作部门+商品工价类型+计薪完工类型+所属年月+商品编码(款号)+录入维度”组合已存在计薪量申报单，是否跳转页面", sid);
        }
        return AjaxResult.success(sid);
    }

    /**
     * 查询员工的计件量明细
     */
    @PostMapping("/item/getWageItem")
    @ApiOperation(value = "查询员工的计件量明细", notes = "查询员工的计件量明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStepCompleteItem.class))
    public TableDataInfo getProcessStepCompleteWageItem(@RequestBody PayProcessStepCompleteItem payProcessStepCompleteItem) {
        startPage(payProcessStepCompleteItem);
        List<PayProcessStepCompleteItem> list = payProcessStepCompleteItemService.getProcessStepCompleteWageItem(payProcessStepCompleteItem);
        return getDataTable(list);
    }

    /**
     * 计薪量明细刷新排序
     */
    @PostMapping("/item/sort")
    @ApiOperation(value = "计薪量明细刷新排序", notes = "计薪量明细刷新排序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStepCompleteItem.class))
    public AjaxResult itemSort(@RequestBody List<PayProcessStepCompleteItem> payProcessStepCompleteItemList) {
        return AjaxResult.success(payProcessStepCompleteItemService.sort(payProcessStepCompleteItemList));
    }

    /**
     * 更新道序工价 / 工价倍率
     */
    @PostMapping("/item/price")
    @ApiOperation(value = "更新道序工价/工价倍率", notes = "更新道序工价/工价倍率")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStepCompleteItem.class))
    public AjaxResult updatePrice(@RequestBody PayProcessStepComplete payProcessStepComplete) {
        if (CollectionUtil.isEmpty(payProcessStepComplete.getPayProcessStepCompleteItemList())){
            throw new BaseException("请选择明细行");
        }
        return AjaxResult.success(payProcessStepCompleteItemService.updatePrice(payProcessStepComplete));
    }

    /**
     * 更新道序工价 / 工价倍率
     */
    @PostMapping("/item/updatePrice")
    @ApiOperation(value = "更新道序工价/工价倍率", notes = "更新道序工价/工价倍率")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStepCompleteItem.class))
    public AjaxResult updatePrice(@RequestBody List<PayProcessStepCompleteItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)){
            throw new BaseException("请选择明细行");
        }
        return AjaxResult.success(payProcessStepCompleteItemService.updateItemPrice(itemList));
    }

    /**
     * 计薪明细预警
     * 点击此按钮，进行结算量校验，如超量，则预警列显示红灯，否则预警列默认为：空
     */
    @PostMapping("/item/warning")
    @ApiOperation(value = "计薪明细预警", notes = "计薪明细预警")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStepCompleteItem.class))
    public AjaxResult itemWarning(@RequestBody PayProcessStepComplete payProcessStepComplete) {
        if (CollectionUtil.isEmpty(payProcessStepComplete.getPayProcessStepCompleteItemList())){
            throw new BaseException("请选择明细行");
        }
        if (payProcessStepComplete.getStepCompleteSid() == null) {
            payProcessStepComplete.setHandleStatus(ConstantsEms.SAVA_STATUS);
        }
        return AjaxResult.success(payProcessStepCompleteItemService.itemWarningBySelect(payProcessStepComplete.getPayProcessStepCompleteItemList(), payProcessStepComplete));
    }

    /**
     * 计薪明细按款显示
     */
    @PostMapping("/item/table")
    @ApiOperation(value = "计薪明细按款显示", notes = "计薪明细按款显示")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStepCompleteItem.class))
    public AjaxResult itemTable(@RequestBody PayProcessStepCompleteTableRequest request) {
        if (CollectionUtil.isEmpty(request.getPayProcessStepCompleteItemList())) {
            throw new BaseException("获取不到该计薪量申报的明细数据，请联系管理员");
        }
        if (request.getProductSid() == null){
            throw new BaseException("请选择明细行");
        }
        return AjaxResult.success(payProcessStepCompleteItemService.itemTable(request));
    }

    @PostMapping("/item/add/toTable")
    @ApiOperation(value = "计薪明细按款录入的显示", notes = "计薪明细按款录入的显示")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStepCompleteTableResponse.class))
    public AjaxResult itemAddToTable(@RequestBody PayProcessStepComplete request) {
        return AjaxResult.success(payProcessStepCompleteItemService.itemAddToTable(request));
    }

    @PostMapping("/item/add/byTable")
    @ApiOperation(value = "计薪明细按款录入点确认", notes = "计薪明细按款录入点确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStepCompleteItem.class))
    public AjaxResult itemAddByTable(@RequestBody PayProcessStepCompleteTableResponse request) {
        return AjaxResult.success(payProcessStepCompleteItemService.itemAddByTable(request));
    }

    /**
     * 道序计薪量统计报表
     */
    @PostMapping("/item/form")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all")
    @ApiOperation(value = "道序计薪量统计报表", notes = "道序计薪量统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PayProcessStepCompleteItem.class))
    public TableDataInfo selectProcessStepCompleteItemForm(@RequestBody PayProcessStepComplete payProcessStepComplete) {
        Integer total = payProcessStepCompleteItemMapper.countProcessStepCompleteItemForm(payProcessStepComplete);
        List<PayProcessStepCompleteItem> list = payProcessStepCompleteItemService.selectProcessStepCompleteItemForm(payProcessStepComplete);
        return getDataTable(list,total);
    }

    @PostMapping("/item/form/export")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all", loc = 1)
    @ApiOperation(value = "导出道序计薪量统计报表", notes = "导出道序计薪量统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void exportProcessStepCompleteItemForm(HttpServletResponse response, PayProcessStepComplete payProcessStepComplete) throws IOException {
        if (payProcessStepComplete.getPlantSid() == null || payProcessStepComplete.getYearmonthBegin() == null || payProcessStepComplete.getYearmonthEnd() == null) {
            throw new IOException("请选择工厂和所属年月区间");
        }
        payProcessStepComplete.setPageBegin(null);
        payProcessStepComplete.setPageNum(null);
        payProcessStepComplete.setPageSize(null);
        List<PayProcessStepCompleteItem> list = payProcessStepCompleteItemService.selectProcessStepCompleteItemForm(payProcessStepComplete);
        List<PayProcessStepCompleteItemFormExport> exportList = BeanCopyUtils.copyListProperties(list, PayProcessStepCompleteItemFormExport::new);
        if (CollectionUtil.isNotEmpty(exportList)) {
            DecimalFormat df4 = new DecimalFormat("###########.####");
            DecimalFormat df3 = new DecimalFormat("###########.###");
            DecimalFormat df2 = new DecimalFormat("###########.##");
            exportList.forEach(item->{
                if (item.getPrice() != null) {
                    item.setPriceString(df4.format(item.getPrice()));
                }
                if (item.getPriceRate() != null) {
                    item.setPriceRateString(df3.format(item.getPriceRate()));
                }
                if (item.getWangongPriceRate() != null) {
                    item.setWangongPriceRateString(df3.format(item.getWangongPriceRate()));
                }
                if (item.getCompleteQuantity() != null) {
                    item.setCompleteQuantityString(df3.format(item.getCompleteQuantity()));
                }
                if (item.getShicaiQuantity() != null) {
                    item.setShicaiQuantityString(df3.format(item.getShicaiQuantity()));
                }
                if (item.getMoney() != null) {
                    item.setMoneyString(df2.format(item.getMoney()));
                }
            });
        }
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<PayProcessStepCompleteItemFormExport> util = new ExcelUtil<>(PayProcessStepCompleteItemFormExport.class, dataMap);
        util.exportExcel(response, exportList, "道序计薪量统计报表");
    }

    /**
     * 商品计件工资统计报表
     */
    @PostMapping("/statistics/salary")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all")
    @ApiOperation(value = "商品计件工资统计报表", notes = "商品计件工资统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ProductProcessCompleteStatisticsSalary.class))
    public TableDataInfo statisticsSalary(@RequestBody ProductProcessCompleteStatisticsSalary completeStatisticsSalary) {
        int pageNum = completeStatisticsSalary.getPageNum();
        completeStatisticsSalary.setPageNum(null);
        List<ProductProcessCompleteStatisticsSalary> total = payProcessStepCompleteItemService.selectCompleteStatisticsSalaryList(completeStatisticsSalary);
        if (CollectionUtil.isNotEmpty(total)) {
            completeStatisticsSalary.setPageNum(pageNum);
            List<ProductProcessCompleteStatisticsSalary> list = payProcessStepCompleteItemService.selectCompleteStatisticsSalaryList(completeStatisticsSalary);
            return getDataTable(list, total.get(0).getPageSize());
        }
        return getDataTable(total, total.size());
    }

    /**
     * 导出计薪量申报-主列表
     */
    @PostMapping("/statistics/salary/export")
    @FieldScope(fieldName = "plantSid", perms = "ems:plant:all", loc = 1)
    @ApiOperation(value = "导出商品计件工资统计报表", notes = "导出商品计件工资统计报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    public void exportStatisticsSalary(HttpServletResponse response, ProductProcessCompleteStatisticsSalary completeStatisticsSalary) throws IOException {
        completeStatisticsSalary.setPageNum(1).setPageSize(10000);
        List<ProductProcessCompleteStatisticsSalary> list = payProcessStepCompleteItemService.selectCompleteStatisticsSalaryList(completeStatisticsSalary);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<ProductProcessCompleteStatisticsSalary> util = new ExcelUtil<>(ProductProcessCompleteStatisticsSalary.class, dataMap);
        util.exportExcel(response, list, "商品计件工资统计报表");
    }

}
