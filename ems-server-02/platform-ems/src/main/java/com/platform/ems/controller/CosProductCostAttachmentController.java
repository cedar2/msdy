package com.platform.ems.controller;

import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.Log;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.CosProductCostAttachment;
import com.platform.ems.service.ICosProductCostAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 商品成本核算-附件Controller
 *
 * @author linhongwei
 * @date 2021-02-26
 */
@RestController
@RequestMapping("/cost/attachment")
public class CosProductCostAttachmentController extends BaseController {

    @Autowired
    private ICosProductCostAttachmentService cosProductCostAttachmentService;

    /**
     * 查询商品成本核算-附件列表
     */
    //@PreAuthorize(hasPermi = "ems:attachment:list")
    @PostMapping("/list")
    public TableDataInfo list(@RequestBody CosProductCostAttachment cosProductCostAttachment) {
        startPage();
        List<CosProductCostAttachment> list = cosProductCostAttachmentService.selectCosProductCostAttachmentList(cosProductCostAttachment);
        return getDataTable(list);
    }

    /**
     * 导出商品成本核算-附件列表
     */
    //@PreAuthorize(hasPermi = "ems:attachment:export")
    @Log(title = "商品成本核算-附件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CosProductCostAttachment cosProductCostAttachment) throws IOException {
        List<CosProductCostAttachment> list = cosProductCostAttachmentService.selectCosProductCostAttachmentList(cosProductCostAttachment);
        ExcelUtil<CosProductCostAttachment> util = new ExcelUtil<CosProductCostAttachment>(CosProductCostAttachment.class);
        util.exportExcel(response, list, "attachment");
    }

    /**
     * 获取商品成本核算-附件详细信息
     */
    //@PreAuthorize(hasPermi = "ems:attachment:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String productCostAttachmentSid) {
        return AjaxResult.success(cosProductCostAttachmentService.selectCosProductCostAttachmentById(productCostAttachmentSid));
    }

    /**
     * 新增商品成本核算-附件
     */
    //@PreAuthorize(hasPermi = "ems:attachment:add")
    @Log(title = "商品成本核算-附件", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody CosProductCostAttachment cosProductCostAttachment) {
        cosProductCostAttachmentService.insertCosProductCostAttachment(cosProductCostAttachment);
        return AjaxResult.success();
    }

    /**
     * 修改商品成本核算-附件
     */
    //@PreAuthorize(hasPermi = "ems:attachment:edit")
    @Log(title = "商品成本核算-附件", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody CosProductCostAttachment cosProductCostAttachment) {
        cosProductCostAttachmentService.updateCosProductCostAttachment(cosProductCostAttachment);
        return AjaxResult.success();
    }

    /**
     * 删除商品成本核算-附件
     */
    //@PreAuthorize(hasPermi = "ems:attachment:remove")
    @Log(title = "商品成本核算-附件", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    public AjaxResult remove(String[] productCostAttachmentSids) {
        cosProductCostAttachmentService.deleteCosProductCostAttachmentByIds(productCostAttachmentSids);
        return AjaxResult.success();
    }
}
