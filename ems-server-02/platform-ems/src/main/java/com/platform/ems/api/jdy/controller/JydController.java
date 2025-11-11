package com.platform.ems.api.jdy.controller;

import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.api.jdy.api.jdy.FormDataApiClient;
import com.platform.ems.api.jdy.model.form.FormDataQueryParam;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import com.platform.ems.api.jdy.constants.HttpConstant;

import static com.platform.ems.api.jdy.constants.HttpConstant.APP_ID;
import static com.platform.ems.api.jdy.constants.HttpConstant.ENTRY_ID;

@RestController
@RequestMapping("/jdy")
@Api(tags = "简道云接口")
public class JydController {

    private static final FormDataApiClient formDataApiClient = new FormDataApiClient(HttpConstant.API_KEY, HttpConstant.HOST);

    private static final String NUM_WIDGET = "_widget_1669106585318";
    private static final String TEXT_WIDGET = "_widget_1669106585317";
    private static final String ADDRESS_WIDGET = "_widget_1669106585320";
    private static final String DATA_WIDGET = "_widget_1669106585319";

    // 查询多条数据
    @GetMapping("/batchData")
    private AjaxResult batchDataQuery() throws Exception {
        FormDataQueryParam param = new FormDataQueryParam(APP_ID, ENTRY_ID);
        param.setLimit(10);
        // 只查这两个字段，不传为查全部字段
        param.setFieldList(Arrays.asList(NUM_WIDGET, TEXT_WIDGET));
        // 按条件查询表单数据
        List<Map<String, Object>> condList = new ArrayList<>();
        // 字段 _widget_1654848548482 的值 等于  单行文本 字符串
        condList.add(new HashMap<String, Object>() {
            {
                put("field", TEXT_WIDGET);
                put("type", "text");
                put("method", "eq");
                put("value", Collections.singletonList("单行文本1"));
            }
        });
        Map<String, Object> filter = new HashMap<String, Object>() {
            {
                // 关系是 and
                put("rel", "and");
                put("cond", condList);
            }
        };
        param.setFilter(filter);
        Map<String, Object> result = formDataApiClient.batchDataQuery(param, null);
        return AjaxResult.success(result);
    }

}
