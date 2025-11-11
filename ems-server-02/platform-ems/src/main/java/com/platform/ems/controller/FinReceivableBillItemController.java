package com.platform.ems.controller;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.platform.ems.domain.dto.request.form.FinReceivableBillItemFormRequest;
import com.platform.ems.domain.dto.response.form.FinReceivableBillItemFormResponse;
import com.platform.common.utils.bean.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import com.platform.ems.domain.FinReceivableBillItem;
import com.platform.ems.service.IFinReceivableBillItemService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.common.core.page.TableDataInfo;

/**
 * 收款单明细报表Controller
 *
 * @author linhongwei
 * @date 2021-06-22
 */
@RestController
@RequestMapping("/fin/receivable/bill/item")
@Api(tags = "收款单明细报表")
public class FinReceivableBillItemController extends BaseController {

    @Autowired
    private IFinReceivableBillItemService finReceivableBillItemService;
    @Autowired
    private ISystemDictDataService sysDictDataService;


}
