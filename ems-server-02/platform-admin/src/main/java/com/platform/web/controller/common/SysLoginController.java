package com.platform.web.controller.common;

import cn.hutool.core.util.StrUtil;
import com.platform.api.service.RemoteUserService;
import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.StringUtils;
import com.platform.common.core.service.TokenService;
import com.platform.system.mapper.SysClientMapper;
import com.platform.web.domain.OpenIdRequest;
import com.platform.web.domain.PlatformLoginForm;
import com.platform.web.enums.PlatformType;
import com.platform.web.service.PlatformOpenIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.platform.common.core.domain.model.LoginBody;
import com.platform.framework.web.service.SysLoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 登录验证
 *
 * @author platform
 */
@RestController
public class SysLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/auth/login")
    public R<?> login(@RequestBody LoginBody loginBody)
    {
        // 用户登录
        if (StrUtil.isEmpty(loginBody.getUsername()) || StrUtil.isEmpty(loginBody.getPassword())) {
            throw new CheckedException("账号和密码不能为空！");
        }
        // 生成令牌
        LoginUser loginUser = loginService.login(loginBody.getClientId(), loginBody.getUsername(), loginBody.getPassword(),
                loginBody.getCode(), loginBody.getUuid());
        return R.ok(tokenService.createToken(loginUser));
    }


    /**
     * 退出
     *
     * @param request
     * @return
     */
    @DeleteMapping("/auth/logout")
    public R<?> logout(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
        }
        return R.ok();
    }

    @Autowired
    SysLoginService sysLoginService;
    @Resource
    SysClientMapper clientMapper;
    @Autowired
    PlatformOpenIdService openIdService;

    /**
     * 1. 正常登录
     * 2. 通过code获取open_id存数据库
     */
    private R<?> platformLogin(PlatformLoginForm form, PlatformType platform) {
        // 1.正常登录
        LoginUser loginUser = sysLoginService.login(form.getClientId(), form.getUsername(), form.getPassword(), form.getCode(), form.getUuid());
        // 2.通过code获取open_id存数据库
        // 当 code 不为空的情况下
        PlatformLoginForm.PlatformInfo platformInfo = form.getPlatform();
        String code;
        if (platformInfo != null && StrUtil.isNotEmpty((code = platformInfo.getCode()))) {
            SysClient sysClient = clientMapper.selectSysClientById(form.getClientId());
            OpenIdRequest openIdRequest = OpenIdRequest.of(loginUser, sysClient, code);
            this.openIdService.tryGetOpenIdAndSaveDB(openIdRequest, platform);
        }
        return R.ok(tokenService.createToken(loginUser));
    }

    /**
     * 飞书：【登录】+【存openId】
     */
    @PostMapping("/auth/feishuLogin")
    public R<?> feishuLogin(@RequestBody PlatformLoginForm form) {
        return platformLogin(form, PlatformType.Feishu);
    }


    /**
     * 微信公众号：【登录】+【存openId】
     */
    @PostMapping("/auth/wechatGzh")
    public R<?> wechatGzhLogin(@RequestBody PlatformLoginForm form) {
        return platformLogin(form, PlatformType.WechatGzh);
    }


    /**
     * 钉钉：【登录】+【存openId】
     */
    @PostMapping("/auth/dingTalkLogin")
    public R<?> dingTalkLogin(@RequestBody PlatformLoginForm form) {
        return platformLogin(form, PlatformType.DingTalk);
    }


    /**
     * 企业微信：【登录】+【存openId】
     */
    @PostMapping("/auth/qiyeLogin")
    public R<?> qiyeLogin(@RequestBody PlatformLoginForm form) {
        return platformLogin(form, PlatformType.WorkWechat);
    }



}
