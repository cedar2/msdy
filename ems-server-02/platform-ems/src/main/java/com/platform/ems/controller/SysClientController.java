package com.platform.ems.controller;

import cn.hutool.core.util.StrUtil;
import com.platform.common.core.domain.R;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.user.CaptchaException;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.poi.ExcelUtil;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.enums.HandleStatus;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.system.service.ISysClientService;
import com.platform.ems.service.ISystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 租户信息Controller
 *
 * @author linhongwei
 * @date 2021-09-30
 */
@RestController
@RequestMapping("/client")
@Api(tags = "租户信息")
public class SysClientController extends BaseController {

    @Autowired
    private ISysClientService sysClientService;
    @Resource
    private ISystemUserService systemUserService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String KEY_HEAD = "forget_";

    @Autowired
    private RedisCache redisService;

    /**
     * 查询租户信息列表
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询租户信息列表", notes = "查询租户信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysClient.class))
    public TableDataInfo list(@RequestBody SysClient sysClient) {
        startPage(sysClient);
        List<SysClient> list = sysClientService.selectSysClientList(sysClient);
        return getDataTable(list);
    }

    /**
     * 导出租户信息列表
     */
    @ApiOperation(value = "导出租户信息列表", notes = "导出租户信息列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = void.class))
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysClient sysClient) throws IOException {
        List<SysClient> list = sysClientService.selectSysClientList(sysClient);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysClient> util = new ExcelUtil<>(SysClient.class, dataMap);
        util.exportExcel(response, list, "租户信息");
    }

    /**
     * 获取租户信息详细信息
     */
    @ApiOperation(value = "获取租户信息详细信息", notes = "获取租户信息详细信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysClient.class))
    @PostMapping("/getInfo")
    public AjaxResult getInfo(String clientId) {
        if (StrUtil.isEmpty(clientId)) {
            throw new CheckedException("参数缺失");
        }
        return AjaxResult.success(sysClientService.selectSysClientById(clientId));
    }

    /**
     * 新增租户信息
     */
    @ApiOperation(value = "新增租户信息", notes = "新增租户信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/add")
    public AjaxResult add(@RequestBody @Valid SysClient sysClient) {
        LoginUser loginUser = ApiThreadLocalUtil.get();
        if (loginUser.getUserid() != null && !loginUser.getUserid().equals(1L)) {
            throw new CustomException("非系统管理员禁止添加租户");
        }
        String msg;
        if (sysClientService.insertSysClient(sysClient) == 0) {
            return AjaxResult.error("创建租户账户失败,请联系管理员");
        }
        msg = StrUtil.format("新增租户成功,租户管理员账号:[{}],初始登录密码:[{}]", sysClient.getClientName(), "123456");
        return AjaxResult.success(msg);
    }

    /**
     * 修改租户信息
     */
    @ApiOperation(value = "修改租户信息", notes = "修改租户信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody @Valid SysClient sysClient) {
        LoginUser loginUser = ApiThreadLocalUtil.get();
        if (loginUser.getUserid() != null && !loginUser.getUserid().equals(1L)) {
            throw new CustomException("非系统管理员禁止修改租户");
        }
        return toAjax(sysClientService.updateSysClient(sysClient));
    }

    /**
     * 变更租户信息
     */
    @ApiOperation(value = "变更租户信息", notes = "变更租户信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/change")
    public AjaxResult change(@RequestBody @Valid SysClient sysClient) {
        return toAjax(sysClientService.changeSysClient(sysClient));
    }

    /**
     * 设置电签数
     */
    @ApiOperation(value = "设置电签数", notes = "设置电签数")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/setDianqian")
    public AjaxResult setDianqian(@RequestBody SysClient sysClient) {
        return toAjax(sysClientService.setDianqian(sysClient));
    }

    /**
     * 删除租户信息
     */
    @ApiOperation(value = "删除租户信息", notes = "删除租户信息")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/delete")
    public AjaxResult remove(@RequestBody List<String> clientIds) {
        if (CollectionUtils.isEmpty(clientIds)) {
            throw new CheckedException("参数缺失");
        }
        LoginUser loginUser = ApiThreadLocalUtil.get();
        if (loginUser.getUserid() != null && !loginUser.getUserid().equals(1L)) {
            throw new CustomException("非系统管理员禁止删除租户");
        }
        return toAjax(sysClientService.deleteSysClientByIds(clientIds));
    }

    @ApiOperation(value = "启用停用接口", notes = "启用停用接口")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysClient sysClient) {
        return AjaxResult.success(sysClientService.changeStatus(sysClient));
    }

    @ApiOperation(value = "确认", notes = "确认")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = AjaxResult.class))
    @PostMapping("/check")
    public AjaxResult check(@RequestBody SysClient sysClient) {
        sysClient.setConfirmDate(new Date());
        sysClient.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        sysClient.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        return toAjax(sysClientService.check(sysClient));
    }

    /**
     * 租户档案下拉列表
     */
    @ApiOperation(value = "租户档案下拉列表", notes = "租户档案下拉列表")
    @ApiResponses(@ApiResponse(code = 200, message = "请求成功", response = SysClient.class))
    @PostMapping("/getList")
    public AjaxResult getList(@RequestBody SysClient sysClient) {
        return AjaxResult.success(sysClientService.getList(sysClient));
    }

    /**
     * 发送验证码
     *
     * @param user
     * @return
     */
    @PostMapping("/sendCode")
    public R<?> resetPassword(@RequestBody SysUser user) {
        if (StrUtil.isEmpty(user.getUserName()) || StrUtil.isEmpty(user.getClientId())) {
            return R.fail("请输入账号及ID");
        }
        SysUser sysUser = systemUserService.verifyEmail(user);

        return R.ok(sysUser, "操作成功");
    }

    /**
     * 匹配验证码
     *
     * @param sysUser
     * @return
     */
    @PostMapping("/verifyemailCode")
    public R<?> verifyemailCode(@RequestBody SysUser sysUser) {
        systemUserService.verifyCode(sysUser);
        return R.ok("操作成功");
    }

    @PostMapping("/api/resetPwd")
    public R apiresetPwd(@RequestBody SysUser user) {
        String key = KEY_HEAD + user.getUserId();
        String cacheCode = redisService.getCacheObject(key).toString();
        if (StrUtil.isEmpty(cacheCode)) {
            throw new CaptchaException("重置密码超时，请重试");
        }
//        userService.checkUserAllowed(user);
        valPass(user.getPassword());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        int row = systemUserService.resetPwd(user);
        if (row <= 0) {
            return R.fail("重置密码失败");
        }
        redisService.deleteObject(cacheCode);
        return R.ok("操作成功");
    }

    public static boolean valPass(Object value) {
        String errorMsg = "密码必须是6位以上字符，并且包含字母和数字";
        if (null == value) {
            throw new BaseException(errorMsg);
        } else {
            String passWord = (String) value;
            passWord = passWord.toLowerCase();
            if (passWord.matches("\\w+")) {
                String regex = "^(?=.*[0-9])(?=.*[a-z]).*$";
                boolean result = (passWord.length() >= 6 && passWord.matches(regex)) ? true : false;
                if (!result) {
                    throw new BaseException(errorMsg);
                }
            } else {
                throw new BaseException(errorMsg);
            }
        }
        return true;
    }
}
