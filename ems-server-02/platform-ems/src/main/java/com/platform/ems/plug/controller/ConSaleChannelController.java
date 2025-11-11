package com.platform.ems.plug.controller;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.platform.common.exception.CheckedException;
import com.platform.common.annotation.Log;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.enums.HandleStatus;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import javax.validation.Valid;
import com.platform.ems.plug.domain.ConSaleChannel;
import com.platform.ems.plug.service.IConSaleChannelService;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.page.TableDataInfo;

/**
 * 销售渠道Controller
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@RestController
@RequestMapping("/sale/channel")
@Api(tags = "销售渠道")
public class ConSaleChannelController extends BaseController {

    @Autowired
    private IConSaleChannelService conSaleChannelService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static Integer i;
    /**
     * 查询销售渠道列表
     */
    @PreAuthorize(hasPermi = "ems:channel:list")
    @PostMapping("/list")
    @ApiOperation(value = "查询销售渠道列表", notes = "查询销售渠道列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSaleChannel.class))
    public TableDataInfo list(@RequestBody ConSaleChannel conSaleChannel) {
        startPage(conSaleChannel);
        List<ConSaleChannel> list = conSaleChannelService.selectConSaleChannelList(conSaleChannel);
        return getDataTable(list);
    }

    /**
     * 导出销售渠道列表
     */
    @PreAuthorize(hasPermi = "ems:channel:export")
    @Log(title = "销售渠道", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出销售渠道列表", notes = "导出销售渠道列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, ConSaleChannel conSaleChannel) throws IOException {
        List<ConSaleChannel> list = conSaleChannelService.selectConSaleChannelList(conSaleChannel);
        Map<String,Object> dataMap=sysDictDataService.getDictDataList();
        ExcelUtil<ConSaleChannel> util = new ExcelUtil<ConSaleChannel>(ConSaleChannel.class,dataMap);
        util.exportExcel(response, list, "销售渠道"+ DateUtil.format(new DateTime(), "yyyyMMddHHmmss"));
    }

    /**
     * 导入销售渠道
     */
    @PostMapping("/import")
    @ApiOperation(value = "导入销售渠道", notes = "导入销售渠道")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ConSaleChannel> util = new ExcelUtil<ConSaleChannel>(ConSaleChannel.class);
        List<ConSaleChannel> list = util.importExcel(file.getInputStream());
        Integer listSize=list.size();
        Integer lose=0;
        String msg="";
        try{
            list.stream().forEach(conSaleChannel ->{
                conSaleChannelService.insertConSaleChannel(conSaleChannel);
                i++;
            });
        }catch (Exception e){
            lose=listSize-i;
            msg=StrUtil.format("前{}条数据导入成功，失败{}条,导入成功的数据请勿重复导入",i,lose);
        }
        if(StrUtil.isEmpty(msg)){
            msg="导入成功";
        }
        return AjaxResult.success(msg);
    }


    @ApiOperation(value = "下载销售渠道导入模板", notes = "下载销售渠道导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<ConSaleChannel> util = new ExcelUtil<ConSaleChannel>(ConSaleChannel.class);
        util.importTemplateExcel(response, "销售渠道导入模板");
    }


    /**
     * 获取销售渠道详细信息
     */
    @ApiOperation(value = "获取销售渠道详细信息", notes = "获取销售渠道详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSaleChannel.class))
    @PreAuthorize(hasPermi = "ems:channel:query")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(Long sid) {
                    if(sid==null){
                throw new CheckedException("参数缺失");
            }
                return AjaxResult.success(conSaleChannelService.selectConSaleChannelById(sid));
    }

    /**
     * 新增销售渠道
     */
    @ApiOperation(value = "新增销售渠道", notes = "新增销售渠道")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:channel:add")
    @Log(title = "销售渠道", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid ConSaleChannel conSaleChannel) {
        return toAjax(conSaleChannelService.insertConSaleChannel(conSaleChannel));
    }

    /**
     * 修改销售渠道
     */
    @ApiOperation(value = "修改销售渠道", notes = "修改销售渠道")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:channel:edit")
    @Log(title = "销售渠道", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody ConSaleChannel conSaleChannel) {
        return toAjax(conSaleChannelService.updateConSaleChannel(conSaleChannel));
    }

    /**
     * 变更销售渠道
     */
    @ApiOperation(value = "变更销售渠道", notes = "变更销售渠道")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response =AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:channel:change")
    @Log(title = "销售渠道", businessType = BusinessType.CHANGE)
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid ConSaleChannel conSaleChannel) {
        return toAjax(conSaleChannelService.changeConSaleChannel(conSaleChannel));
    }

    /**
     * 删除销售渠道
     */
    @ApiOperation(value = "删除销售渠道", notes = "删除销售渠道")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PreAuthorize(hasPermi = "ems:channel:remove")
    @Log(title = "销售渠道", businessType = BusinessType.DELETE)
	@PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<Long>  sids) {
        if(ArrayUtil.isEmpty( sids)){
            throw new CheckedException("参数缺失");
        }
        return toAjax(conSaleChannelService.deleteConSaleChannelByIds(sids));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售渠道", businessType = BusinessType.UPDATE)
    @PreAuthorize(hasPermi = "ems:channel:edit")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConSaleChannel conSaleChannel) {
        return AjaxResult.success(conSaleChannelService.changeStatus(conSaleChannel));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @PreAuthorize(hasPermi = "ems:channel:edit")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @Log(title = "销售渠道", businessType = BusinessType.CHECK)
    @PostMapping("/check")
    public AjaxResult check(@RequestBody ConSaleChannel conSaleChannel) {
        conSaleChannel.setConfirmDate(new Date());
        conSaleChannel.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        conSaleChannel.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(conSaleChannelService.check(conSaleChannel));
    }

    @PostMapping("/getConSaleChannelList")
    @ApiOperation(value = "销售渠道下拉列表", notes = "销售渠道下拉框列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = ConSaleChannel.class))
    public AjaxResult getConSaleChannelList(){
        return AjaxResult.success(conSaleChannelService.getConSaleChannelList());
    }
}
