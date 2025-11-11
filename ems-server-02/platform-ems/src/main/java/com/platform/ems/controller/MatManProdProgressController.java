package com.platform.ems.controller;

import com.platform.common.core.controller.BaseController;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.annotation.PreAuthorize;
import com.platform.ems.domain.ManDayManufactureProgress;
import com.platform.ems.domain.MatManProdProgress;
import com.platform.ems.service.IMatManProdProgressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品生产进度报表Controller
 * @author wangp
 * @date 2022-10-20
 */

@RestController
@RequestMapping("/mat/man/prod/progress")
@Api(tags = "商品生产进度报表")
public class MatManProdProgressController extends BaseController {

    @Autowired
    private IMatManProdProgressService matManProdProgressService;

    /**
     * 查询生产进度日报列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询商品生产进度报表", notes = "查询商品生产进度报表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = MatManProdProgress.class))
    public TableDataInfo list(@RequestBody MatManProdProgress matManProdProgress) {
        startPage(matManProdProgress);
        List<MatManProdProgress> list =  matManProdProgressService.selectMatManProgressList(matManProdProgress);
        return getDataTable(list);
    }
}
