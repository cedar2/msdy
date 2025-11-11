package com.platform.common.core.controller;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Validator;
import com.alibaba.fastjson.JSONObject;
import com.platform.common.utils.bean.BeanCopyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.platform.common.constant.HttpStatus;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.common.core.page.PageDomain;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.core.page.TableSupport;
import com.platform.common.utils.DateUtils;
import com.platform.common.utils.SecurityUtils;
import com.platform.common.utils.StringUtils;
import com.platform.common.utils.sql.SqlUtil;

/**
 * web层通用数据处理
 *
 * @author platform
 */
public class BaseController
{
    protected final Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * body 获取对象分页
     *
     * @param obj
     */
    protected void startPage(Object obj) {
        JSONObject jsonObject = (JSONObject)JSONObject.toJSON(obj);
        Integer pageNum = (Integer)jsonObject.get(TableSupport.PAGE_NUM);
        Integer pageSize = (Integer)jsonObject.get(TableSupport.PAGE_SIZE);
        startPage(pageNum, pageSize);
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 分页查询  默认传空
     *
     * @param pageNum
     * @param pageSize
     */
    protected void startPage(Integer pageNum, Integer pageSize) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        if (Validator.isEmpty(pageNum)) {
            pageNum = pageDomain.getPageNum();
        }
        if (Validator.isEmpty(pageSize)) {
            pageSize = pageDomain.getPageSize();
        }
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = pageDomain.getOrderBy();
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings("all")
    protected TableDataInfo getDataTable(List<?> list, Supplier<?> target) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(BeanCopyUtils.copyListProperties(list,target));
        rspData.setMsg("查询成功");
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    @SuppressWarnings("all")
    protected TableDataInfo getDataTable(List<?> list, Supplier<?> target,Integer pageNum,Integer pageSize) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(BeanCopyUtils.copyListProperties(list,target).stream().skip((pageNum-1)*pageSize).limit(pageSize).collect(Collectors.toList()));
        rspData.setMsg("查询成功");
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }


    @SuppressWarnings("all")
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 返回成功
     */
    public AjaxResult success()
    {
        return AjaxResult.success();
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error()
    {
        return AjaxResult.error();
    }

    /**
     * 返回成功消息
     */
    public AjaxResult success(String message)
    {
        return AjaxResult.success(message);
    }

    /**
     * 返回成功消息
     */
    public AjaxResult success(Object data)
    {
        return AjaxResult.success(data);
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error(String message)
    {
        return AjaxResult.error(message);
    }

    @SuppressWarnings("all")
    protected TableDataInfo getDataTable(List<?> list, long total) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        rspData.setTotal(total);
        return rspData;
    }

    @SuppressWarnings("all")
    protected TableDataInfo getDataTable() {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(new ArrayList<>());
        rspData.setMsg("查询成功");
        rspData.setTotal(0);
        return rspData;
    }

    /**
     * 返回警告消息
     */
    public AjaxResult warn(String message)
    {
        return AjaxResult.warn(message);
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? success(rows) : error();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected AjaxResult toAjax(boolean result)
    {
        return result ? success() : error();
    }

    /**
     * 页面跳转
     */
    public String redirect(String url)
    {
        return StringUtils.format("redirect:{}", url);
    }

    /**
     * 获取用户缓存信息
     */
    public LoginUser getLoginUser()
    {
        return SecurityUtils.getLoginUser();
    }

    /**
     * 获取登录用户id
     */
    public Long getUserId()
    {
        return getLoginUser().getUserid();
    }

    /**
     * 获取登录部门id
     */
    public Long getDeptId()
    {
        return getLoginUser().getDeptId();
    }

    /**
     * 获取登录用户名
     */
    public String getUsername()
    {
        return getLoginUser().getUsername();
    }
}
