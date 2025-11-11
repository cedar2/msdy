package com.platform.system.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.common.core.service.TokenService;
import com.platform.common.enums.UserStatus;
import com.platform.common.exception.CheckedException;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.exception.base.BaseException;
import com.platform.common.security.utils.wx.GetWeiXinCode;
import com.platform.system.dingding.service.DingdingServer;
import com.platform.system.qywx.dto.UserInfoDTO;
import com.platform.system.qywx.service.QywxServer;
import com.platform.system.qywx.vo.UserInfoVo;
import com.platform.system.service.ISysUserService;
import com.platform.system.service.impl.SysPermissionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Set;

@Slf4j
@Api(tags = "微信接口")
@RestController
@RequestMapping("/login")
public class AuthLoginController {

    @Resource(name = "qywxServer")
    private QywxServer qywxServer;

    @Resource(name = "dingdingServer")
    private DingdingServer dingdingServer;

    @Resource
    private ISysUserService userService;

    @Resource
    private SysPermissionServiceImpl permissionService;

    @Resource
    private TokenService tokenService;

    private final String undefined = "undefined";

    /**
     * 企业微信登陆接口
     */
    @ApiOperation(value = "登陆接口", notes = "登陆接口")
    @GetMapping("/qywx/userInfo")
    public AjaxResult qywx(String code) {
        log.info("进入到控制器userInfo中code:{}", code);
        if (StrUtil.isBlank(code) && undefined.equals(code)) {
            return AjaxResult.error("未获取到code！");
        }
        UserInfoDTO userInfo = qywxServer.getUserInfo(new UserInfoVo(null, code));
        log.info("WxCpController.userInfo.qywxServer.getUserInfo，结果：{}，参数：{}", userInfo, code);
        String workWechatOpenid = userInfo.getUserId();
        if (StrUtil.isBlank(workWechatOpenid)) {
            throw new CheckedException("workWechatOpenid为空！");
        }
        SysUser user = userService.selectUserByqyUserId(workWechatOpenid);
        if (user != null) {
            if (!user.getStatus().equals("0")) {
                throw new BaseException("账号已停用，请联系管理员");
            }
            if (!user.getDelFlag().equals("0")) {
                throw new BaseException("账号已被删除，请联系管理员");
            }
        }
        else {
            throw new BaseException("请先登录");
        }
        LoginUser loginUser = setUserDataResponse(user, workWechatOpenid);
        //  生成token并保存
        tokenService.createToken(loginUser);
        return AjaxResult.success(loginUser);
    }

    /**
     * 公众号登陆接口
     */
    @ApiOperation(value = "登陆接口", notes = "登陆接口")
    @GetMapping("/gzh/userInfo")
    public AjaxResult gzh(String code) {
        log.info("进入到控制器userInfo中code:{}", code);
        if (StrUtil.isBlank(code) && undefined.equals(code)) {
            return AjaxResult.error("未获取到code！");
        }
        JSONObject result = GetWeiXinCode.getOpenId(code);
        String userid = result.getString("openid");
        log.info("WxCpController.userInfo.qywxServer.getUserInfo，结果：{}，参数：{}", userid, code);
        if (StrUtil.isBlank(userid)) {
            throw new CheckedException("userid为空！");
        }
        SysUser user = userService.selectUserByGzhOpenId(userid);
        if (user != null) {
            user = userService.selectUserByNameAndId(String.valueOf(user.getUserName()), user.getClientId());
        }
        LoginUser loginUser = setUserDataResponse(user, userid);
        //  生成token并保存
        tokenService.createToken(loginUser);
        return AjaxResult.success(loginUser);
    }

    /**
     * 得到登录返回的 data
     */
    public LoginUser setUserDataResponse(SysUser user, String userid) {
        log.info("WxCpController.userInfo.userService.userInfo，Result结果：{}，参数：{}", user, userid);
        if (user == null) {
            log.error("登录用户：{} 不存在.", userid);
            throw new CheckedException("登录用户：" + userid + " 不存在");
        } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            log.error("登录用户：{} 已被删除.", user.getUserName());
            throw new CheckedException("对不起，您的账号：" + user.getUserName() + " 已被删除");
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.error("登录用户：{} 已被停用.", user.getUserName());
            throw new CheckedException("对不起，您的账号：" + user.getUserName() + " 已停用");
        }
        //  获取角色
        Set<String> roles = permissionService.getRolePermission(user.getUserId());
        log.info("WxCpController.userInfo.permissionService.getRolePermission，Result结果roles：{}，参数user：{}", roles, user);
        if (roles.isEmpty()) {
            log.error("登录用户：{} 未设置角色信息，请联系管理员.", user.getUserName());
            throw new CheckedException("对不起，您的账号：" + user.getUserName() + " 未设置角色，请联系管理员！");
        }

        //  获取权限 移动端菜单类型 ： 1
        String mobileType = "1";
        Set<String> permissions = permissionService.getMenuPermissionPType(user, mobileType);
        log.info("WxCpController.userInfo.permissionService.getMenuPermissionPType，Result结果permissions：{}，参数user：{},mobileType:{}", permissions, user, mobileType);
        if (permissions.isEmpty()) {
            log.error("登录用户：{} 未设置权限信息，请联系管理员.", user.getUserName());
            throw new CheckedException("对不起，您的账号：" + user.getUserName() + " 未设置权限，请联系管理员！");
        }
        user.setPassword(null);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserid(user.getUserId());
        loginUser.setClientId(user.getClientId());
        loginUser.setSysUser(user);
        loginUser.setRoles(roles);
        loginUser.setPermissions(permissions);
        log.info("WxCpController.userInfo.loginUser.用户登录信息：{}", loginUser);
        return loginUser;
    }

    /**
     * 钉钉登录接口
     */
    @ApiOperation(value = "登陆接口", notes = "登陆接口")
    @GetMapping("/dingding/userInfo")
    public AjaxResult dingding(String code) {
        log.info("进入到控制器userInfo中code:{}", code);
        if (StrUtil.isBlank(code) && undefined.equals(code)) {
            return AjaxResult.error("未获取到code！");
        }
        // 获取access_token，注意正式代码要有异常流处理
        String accessToken = dingdingServer.getToken();
        String userid = dingdingServer.getUserId(code,accessToken);
        if (StrUtil.isBlank(userid)) {
            throw new CheckedException("userid为空！");
        }
        SysUser user = userService.selectUserByDdUserId(userid);
        if (user != null) {
            if (!user.getStatus().equals("0")) {
                throw new BaseException("账号已停用，请联系管理员");
            }
            if (!user.getDelFlag().equals("0")) {
                throw new BaseException("账号已被删除，请联系管理员");
            }
        }
        else {
            throw new BaseException("请先登录");
        }
        LoginUser loginUser = setUserDataResponse(user, userid);
        //  生成token并保存
        tokenService.createToken(loginUser);
        return AjaxResult.success(loginUser);
    }

}
