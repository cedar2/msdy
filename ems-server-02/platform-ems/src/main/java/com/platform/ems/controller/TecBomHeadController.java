package com.platform.ems.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.hutool.core.date.DateTime;
import com.platform.common.annotation.Excel;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.annotation.Idempotent;
import com.platform.ems.config.MinioConfig;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.*;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.service.*;
import com.platform.ems.util.ExcelStyleUtil;
import com.platform.api.service.RemoteFileService;
import com.platform.flowable.service.ISysDeployFormService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Maps;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.api.service.RemoteFlowableService;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.multipart.MultipartFile;

/**
 * 物料清单（BOM）主Controller
 *
 * @author qhq
 * @date 2021-03-15
 */
@RestController
@RequestMapping("/bom/head")
@Api(tags = "物料清单（BOM）主")
public class TecBomHeadController extends BaseController {

    @Autowired
    private ITecBomHeadService tecBomHeadService;

    @Autowired
    private IBasMaterialService basMaterialService;

    @Autowired
	private ISysDeployFormService sysDeployService;

    @Autowired
    private RemoteFlowableService flowableService;

    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private MinioConfig minioConfig;
    @Autowired
    private RemoteFileService remoteFileService;

    @Autowired
    private MinioClient client;
    private static final String FILLE_PATH = "/template";


    /**
     * 查询物料清单（BOM）主列表
     *
     * @PreAuthorize(hasPermi = "ems:head:list")
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询物料清单（BOM）主列表", notes = "查询物料清单（BOM）主列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomHead.class))
    public TableDataInfo list(@RequestBody @Valid TecBomHead tecBomHead) {
        startPage(tecBomHead);
        List<TecBomHead> list = tecBomHeadService.selectTecBomHeadListNew(tecBomHead);
        return getDataTable(list);
    }

    /**
     * 查询物料清单（BOM）主列表
     *
     * @PreAuthorize(hasPermi = "ems:head:list")
     */
    @PostMapping("/report")
    @ApiOperation(value = "bom明细报表", notes = "bom明细报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomHead.class))
    public TableDataInfo report(@RequestBody  TecBomHeadReportRequest tecBomHead) {
        startPage(tecBomHead);
        List<TecBomHeadReportResponse> list = tecBomHeadService.report(tecBomHead);
        return getDataTable(list);
    }

    @PostMapping("/sort/item")
    @ApiOperation(value = "bom明细行排序", notes = "bom明细行排序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomHead.class))
    public AjaxResult sortItem(@RequestBody  List<TecBomItem> list) {
        return AjaxResult.success(tecBomHeadService.sortItem(list));
    }
    @PostMapping("/sort/item/insert")
    @ApiOperation(value = "bom明细行排序-新建页面", notes = "bom明细行排序")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomHead.class))
    public AjaxResult sortItemAdd(@RequestBody  List<BomSortResponse> list) {
        return AjaxResult.success(tecBomHeadService.sortItemAdd(list));
    }

    @PostMapping("/report/material")
    @ApiOperation(value = "BOM物料报表", notes = "BOM物料报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomHead.class))
    public TableDataInfo reportMaterial(@RequestBody  TecBomHeadReportRequest tecBomHead) {
        startPage(tecBomHead);
        List<TecBomHeadMaterialReportResponse> list = tecBomHeadService.reportMaterial(tecBomHead);
        return getDataTable(list);
    }

    @PostMapping("/test")
    @ApiOperation(value = "初始化行号测试", notes = "初始化行号测试")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomHead.class))
    public AjaxResult test(@RequestBody  List<Long> sids) {
       return AjaxResult.success(tecBomHeadService.test(sids));
    }

    @PostMapping("/exchange")
    @ApiOperation(value = "物料替换", notes = "物料替换")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomHead.class))
    public AjaxResult exchange(@RequestBody TecBomHeadExchangeRequest request) {
      return   AjaxResult.success(tecBomHeadService.exChange(request));
    }
    @PostMapping("/exchange/judge")
    @ApiOperation(value = "物料颜色替换校验", notes = "物料替换")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomHead.class))
    public AjaxResult exchangeJudge(@RequestBody List<TecBomHeadReportExSidRequest> sidList) {
        return   AjaxResult.success(tecBomHeadService.changeJudge(sidList));
    }

    @PostMapping("/judge")
    @ApiOperation(value = "物料替换校验-提醒", notes = "物料替换校验-提醒")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomHead.class))
    public AjaxResult Judge(@RequestBody TecBomHeadExChangeJudgeRequest request) {
        return  tecBomHeadService.Judge(request);
    }
    /**
     * 导出采购价信息主列表
     */
//    @PreAuthorize(hasPermi = "archive:price:export")
    @ApiOperation(value = "bom明细报表导出", notes = "bom明细报表导出")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchasePrice.class))
    @Log(title = "导出bom信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export/report")
    public void exportReport(HttpServletResponse response, TecBomHeadReportRequest tecBomHead) throws IOException {
        List<TecBomHeadReportResponse> list = tecBomHeadService.report(tecBomHead);
        tecBomHeadService.setIsM(list);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<TecBomHeadReportResponse> util = new ExcelUtil<>(TecBomHeadReportResponse.class,dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, TecBomHeadReportResponse::new), "BOM明细报表");
    }

    @PostMapping("/import/item")
    @ApiOperation(value = "bom明细导入-编辑页面", notes = "bom明细导入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataGCK(@RequestParam MultipartFile file,  String materialCode, String sampleCodeSelf) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return tecBomHeadService.importBOM(file,materialCode,sampleCodeSelf);
    }
    @PostMapping("/import/item/add")
    @ApiOperation(value = "bom明细导入-新建页面", notes = "bom明细导入")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importDataAdd(@RequestParam MultipartFile file,  String materialCode, String sampleCodeSelf) throws Exception {
        if (file.isEmpty()) {
            throw new BaseException("请选择文件上传");
        }
        return tecBomHeadService.importBOMAdd(file,materialCode,sampleCodeSelf);
    }

    @ApiOperation(value = "BOM物料报表导出", notes = "BOM物料报表导出")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchasePrice.class))
    @Log(title = "导出bom信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export/report/material")
    public void exportMaterialReport(HttpServletResponse response, TecBomHeadReportRequest tecBomHead) throws IOException {
        List<TecBomHeadMaterialReportResponse> list = tecBomHeadService.reportMaterial(tecBomHead);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<TecBomHeadMaterialReportResponse> util = new ExcelUtil<>(TecBomHeadMaterialReportResponse.class,dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, TecBomHeadMaterialReportResponse::new), "BOM物料报表");
    }

    @ApiOperation(value = "下载BOM明细导入导入模板", notes = "下载BOM明细导入导入模板")
    @PostMapping("/importTemplate/bom/item")
    public void importTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException {
        InputStream inputStream = null;
        String fileName = FILLE_PATH + "/EMS软件_BOM明细导入_V0.1.xlsx";
        try {
            GetObjectArgs args = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build();
            inputStream = client.getObject(args);
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("EMS软件_BOM明细导入_V0.1.xlsx", "UTF-8"));
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
    /**
     * 条件查询BOM
     *
     * @param request
     * @return
     */
    @PostMapping("/MaterialList")
    @ApiOperation(value = "查询商品列表", notes = "查询商品列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomHead.class))
    public TableDataInfo list(@RequestBody @Valid BasMaterial request) {
        List<BasMaterial> back = new ArrayList<BasMaterial>();
        List<BasMaterial> back2 = new ArrayList<BasMaterial>();
        TecBomHead bomhead = new TecBomHead();
        bomhead.setBeginTime(request.getBeginTime());
        bomhead.setEndTime(request.getEndTime());
        request.setBeginTime(null);
        request.setEndTime(null);
        List<TecBomHead> headLists = tecBomHeadService.selectTecBomHeadList(bomhead);
        if (CollectionUtil.isNotEmpty(headLists)) {
            List<BasMaterial> materialList = basMaterialService.selectBasMaterialList(request);
            headLists.forEach(head -> {
                for (BasMaterial material : materialList) {
                    if (head.getMaterialSid() != null && head.getMaterialSid()==material.getMaterialSid()) {
                        back.add(material);
                        break;
                    }
                }
            });
        }
        List<Long> materialSids = new ArrayList<>();
        for (BasMaterial ma : back) {
            if (CollectionUtil.isEmpty(materialSids)) {
                back2.add(ma);
                materialSids.add(ma.getMaterialSid());
            } else {
                if (!materialSids.contains(ma.getMaterialSid())) {
                    back2.add(ma);
                    materialSids.add(ma.getMaterialSid());
                }
            }
        }
        //由于返回的是组合成一条的记录,这边需要后台自己实现分页...
        int num = request.getPageNum();
        int size = request.getPageSize();
        if (num == 1) {
            if (back2.size() < size) {
                return getDataTable(back2);
            } else {
                return getDataTable(back2.subList(0, num));
            }
        } else {
            int count = num * size;
            if (back2.size() > count) {
                return getDataTable(back2.subList(count - size, count));
            } else {
                return getDataTable(back2.subList(count - size, back2.size()));
            }
        }
    }


    /**
     * 导出物料清单（BOM）主列表
     * @PreAuthorize(hasPermi = "ems:head:export")
     */
    @Log(title = "物料清单（BOM）主", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出物料清单（BOM）明细", notes = "导出物料清单（BOM）明细")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export/item")
    public void exportItem(HttpServletResponse response, TecBomHead tecBomHead) throws IOException {
        tecBomHeadService.export(response,tecBomHead);
    }


    /**
     * 导出采购价信息主列表
     */
//    @PreAuthorize(hasPermi = "archive:price:export")
    @ApiOperation(value = "导出bom信息", notes = "导出bom信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = PurPurchasePrice.class))
    @Log(title = "导出bom信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TecBomHead tecBomHead) throws IOException {
        List<TecBomHead> list = tecBomHeadService.selectTecBomHeadListNew(tecBomHead);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<TecBomHeadExResponse> util = new ExcelUtil<>(TecBomHeadExResponse.class,dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, TecBomHeadExResponse::new), "bom");
    }

    /**
     * 获取物料清单（BOM）主详细信息-新
     *
     * @PreAuthorize(hasPermi = "ems:head:query")
     */
    @ApiOperation(value = "获取物料清单（BOM）主详细信息", notes = "获取物料清单（BOM）主详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomHead.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(@RequestBody TecBomHead tecBomHead) {
        TecBomHead tecBomHeads = tecBomHeadService.getBom(tecBomHead);
        return AjaxResult.success(tecBomHeads);
    }

    @ApiOperation(value = "更改采购类型", notes = "更改采购类型")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = TecBomHead.class))
    @PostMapping("/change/purchase")
    public AjaxResult changePurchaseType(@RequestBody TecBomHeadReportPurchaseRequest request) {
        return AjaxResult.success( tecBomHeadService.changePurchaseType(request));
    }

    /**
     * 新增物料清单（BOM）主
     *
     * @PreAuthorize(hasPermi = "ems:head:add")
     */
    @ApiOperation(value = "新增物料清单（BOM）主", notes = "新增物料清单（BOM）主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料清单（BOM）主", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult add(@RequestBody @Valid List<TecBomHead> bomList) {
        if(bomList.size()<=0){
            throw new CustomException("请求信息，不允许为空请联系管理员");
        }
        return AjaxResult.success(tecBomHeadService.insertTecBomHead(bomList));
    }


    /**
     * 修改物料清单（BOM）主-新
     *
     * @PreAuthorize(hasPermi = "ems:head:edit")
     */
    @ApiOperation(value = "修改物料清单（BOM）主", notes = "修改物料清单（BOM）主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料清单（BOM）主", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Idempotent(message = "系统处理中，请勿重复点击按钮")
    public AjaxResult edit(@RequestBody @Valid TecBomHead head) {
        return toAjax(tecBomHeadService.editTecBomHead(head));
    }


    /**
     * 删除物料清单（BOM）主
     *
     * @PreAuthorize(hasPermi = "ems:head:remove")
     */
    @ApiOperation(value = "删除物料清单（BOM）主", notes = "删除物料清单（BOM）主")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料清单（BOM）主", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long> materialSids) {
        return toAjax(tecBomHeadService.deleteTecBomHeadByIds(materialSids));
    }

    /**
     * 修改BOM清单处理状态
     *
     * @PreAuthorize(hasPermi = "archive:material:edit")
     */
    @ApiOperation(value = "修改BOM清单处理状态", notes = "修改BOM清单处理状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料清单（BOM）", businessType = BusinessType.UPDATE)
    @PostMapping("/handle")
    public AjaxResult handle(@RequestBody TecBomHead bom) {
        return toAjax(tecBomHeadService.editHandleStatus(bom));
    }

    /**
     * 修改BOM清单处理状态
     *
     * @PreAuthorize(hasPermi = "archive:material:edit")
     */
    @ApiOperation(value = "bom复制", notes = "bom复制")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "bom复制（BOM）", businessType = BusinessType.UPDATE)
    @PostMapping("/copy")
    public AjaxResult bomCopy(@RequestBody BasBomCopyRequest request) {
        TecBomHead tecBomHead = new TecBomHead();
        BeanCopyUtils.copyProperties(request,tecBomHead);
        return AjaxResult.success(tecBomHeadService.getBomItemM(tecBomHead));
    }

    /**
     * 修改BOM清单启用/停用状态
     *
     * @PreAuthorize(hasPermi = "archive:material:edit")
     */
    @ApiOperation(value = "修改BOM清单启用/停用状态", notes = "修改BOM清单启用/停用状态")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料清单（BOM）", businessType = BusinessType.UPDATE)
    @PostMapping("/status")
    public AjaxResult status(@RequestBody TecBomHead bom) {
        return toAjax(tecBomHeadService.editStatus(bom));
    }

    /**
     * 修改BOM Status
     *
     * @PreAuthorize(hasPermi = "archive:material:edit")
     */
    @ApiOperation(value = "修改BOM Status", notes = "修改BOM Status")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "物料清单（BOM）", businessType = BusinessType.UPDATE)
    @PostMapping("/bomStatus")
    public AjaxResult bomStatus(@RequestBody TecBomHead tecBomHead) {
        return toAjax(tecBomHeadService.updateBomStatus(tecBomHead));
    }

    /**
     * 组合拉链物料 插入
     *
     */
    @ApiOperation(value = "新建组合拉链", notes = "新建组合拉链")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = int.class))
    @Log(title = "组合拉链（BOM）", businessType = BusinessType.UPDATE)
    @PostMapping("/insert/zipper")
    public AjaxResult  insertZipper(@RequestBody TecBomHead tecBomHead) {
        return toAjax(tecBomHeadService.insertZipper(tecBomHead));
    }

    /**
     * 查看组合拉链
     *
     */
    @ApiOperation(value = "查看组合拉链", notes = "新建组合拉链")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = int.class))
    @Log(title = "组合拉链", businessType = BusinessType.UPDATE)
    @PostMapping("/getInfo/zipper")
    public TecBomHead  getZipper(Long materialSid) {
        return tecBomHeadService.getZipper(materialSid);
    }

    /**
     * 修改组合拉链
     *
     */
    @ApiOperation(value = "修改组合拉链", notes = "修改组合拉链")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "组合拉链", businessType = BusinessType.UPDATE)
    @PostMapping("/edit/zipper")
    public AjaxResult  editZipper(@RequestBody TecBomHead tecBomHead) {
        return toAjax(tecBomHeadService.editZipper(tecBomHead));
    }

    @PostMapping("/processCheck")
    public AjaxResult processCheck(@RequestBody TecBomHead tecBomHead){
        return AjaxResult.success(tecBomHeadService.processCheck(tecBomHead));
    }
}
