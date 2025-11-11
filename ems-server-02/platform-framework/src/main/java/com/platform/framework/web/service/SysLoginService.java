package com.platform.framework.web.service;

import javax.annotation.Resource;

import com.platform.common.constant.ConstantsEms;

import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.model.SysRoleDataAuthFieldValue;

import com.platform.common.enums.UserStatus;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.DateUtils;
import com.platform.common.utils.SecurityUtils;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.system.domain.SysUserOnline;
import com.platform.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;
import com.platform.common.constant.CacheConstants;
import com.platform.common.constant.Constants;
import com.platform.common.constant.UserConstants;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.exception.ServiceException;
import com.platform.common.exception.user.BlackListException;
import com.platform.common.exception.user.CaptchaException;
import com.platform.common.exception.user.CaptchaExpireException;
import com.platform.common.exception.user.UserNotExistsException;
import com.platform.common.exception.user.UserPasswordNotMatchException;
import com.platform.common.utils.MessageUtils;
import com.platform.common.utils.StringUtils;
import com.platform.common.utils.ip.IpUtils;
import com.platform.framework.manager.AsyncManager;
import com.platform.framework.manager.factory.AsyncFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * 登录校验方法
 *
 * @author platform
 */
@Slf4j
@Component
public class SysLoginService
{

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysClientService clientService;

    @Autowired
    private ISysPermissionService permissionService;

    @Autowired
    private ISysConfigService configService;

    private static final String IS_VEIW_PRICE_SALE = "ems:sales:order:saleprice";

    private static final String IS_VEIW_PRICE_PUR = "ems:purchase:order:saleprice";

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public LoginUser login(String clientId, String username, String password, String code, String uuid)
    {
        // 验证码校验
        validateCaptcha(username, code, uuid);
        // 登录前置校验
        loginPreCheck(username, password);

        SysClient sysClient = clientService.selectSysClientById(clientId);

        // 租户为空 || 租户处理状态不为确认 || 租户状态不为启用
        if (sysClient == null || !ConstantsEms.CHECK_STATUS.equals(sysClient.getHandleStatus())) {
            throw new ServiceException("租户ID" + clientId + "不存在！");
        }
        else if (!ConstantsEms.ENABLE_STATUS.equals(sysClient.getStatus())) {
            throw new ServiceException("租户ID" + clientId + "已停用！");
        }
        // 校验租户
        judgeClient(sysClient, username, clientId);

        SysUser sysUser = userService.selectUserByNameAndId(username, clientId);
        // 没有该用户 || 该用户的状态不为启用状态
        if (sysUser == null || !sysUser.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)) {
            throw new BaseException("登录用户：" + username + " 不存在");
        }

        if (UserStatus.DELETED.getCode().equals(sysUser.getDelFlag())) {
            throw new BaseException("对不起，您的账号：" + username + " 已被删除");
        }
        if (UserStatus.DISABLE.getCode().equals(sysUser.getStatus())) {
            throw new BaseException("对不起，您的账号：" + username + " 已停用");
        }
        if (!SecurityUtils.matchesPassword(password, sysUser.getPassword())) {
            throw new BaseException("密码错误");
        }

        // 数据角色
        List<SysRoleDataAuthFieldValue> fieldValueList = userService.selectRoleDataAuthFiledValueList(sysUser.getUserId());
        if (sysUser != null) {
            sysUser.setFieldValueList(fieldValueList);
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser.getUserId());
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser.getUserId());
        boolean isView = permissions.stream().anyMatch(item -> item.equals(IS_VEIW_PRICE_SALE));
        if (isView) {
            sysUser.setIsViewPrice("Y");
        } else {
            sysUser.setIsViewPrice("N");
        }
        boolean isViewPur = permissions.stream().anyMatch(item -> item.equals(IS_VEIW_PRICE_PUR));
        if (isViewPur) {
            sysUser.setIsViewPricePur("Y");
        } else {
            sysUser.setIsViewPricePur("N");
        }
        LoginUser sysUserVo = new LoginUser();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        //注意这条需要获取 loginUser，但是此时还没写入缓存
        //  recordLoginInfo(sysUser.getUserId());
        // 生成token
        return sysUserVo;
    }

    @Autowired
    private ISysUserOnlineService onlineService;

    /**
     * 校验租户
     */
    public void judgeClient(SysClient sysClient, String username, String clientId) {
        // 若当前登录账号的“租户ID”所属租户信息的“账号数限制方式“是“限制”且“收费类型”是“在线并发”，则执行下一校验
        // 判断当前登录账号的“租户ID”所属租户信息的“租户有效期(至)”不为空且小于当前日期，则提示错误信息：账号已到期，无法登录，请联系系统管理员续费！
        LocalDate today = LocalDate.now();
        if (sysClient.getEndDate() != null) {
            LocalDate endDate = sysClient.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (endDate.compareTo(today) < 0) {
                throw new BaseException("账号已到期，无法登录，请联系系统管理员续费！");
            }
        }
//        // 判断是否已经登录
//        List<String> isLogin = new ArrayList<>();
//
//        List<SysUserOnline> rows = oneLineList(null, null);
//        int num = sysClient.getAuthorizeAccountNum() == null ? 0 : sysClient.getAuthorizeAccountNum(), i = 0;
//        for (SysUserOnline user : rows) {
//            if (clientId.equals(user.getClientId()) && !username.equals(user.getUserName())) {
//                i += 1;
//            }
//            // 》判断当前已在线的账号数是否大于等于当前登录账号的“租户ID“所属租户信息的“授权账号数”，如是，则提示错误信息：在线账号数超出授权数，请稍后再登录！
//            if (i>=num) {
//                if ("XZ".equals(sysClient.getAccountNumLimitType()) && "ZXBF".equals(sysClient.getSystemFeeType())) {
//                    throw new BaseException("在线账号数超出授权数，请稍后再登录！");
//                }
//            }
//            // 登录时校验此账号是否已登录，如已登录，则强制退出系统目前已登录的，在进行当前的登录。
//            if (username.equals(user.getUserName())) {
//                isLogin.add(user.getTokenId());
//            }
//        }
//        if (isLogin.size() != 0) {
//            for (String tokenid : isLogin) {
//                // 登录时校验此账号是否已登录，如已登录，则强制退出系统目前已登录的，在进行当前的登录。
//                redisCache.deleteObject(CacheConstants.LOGIN_TOKEN_KEY + tokenid);
//            }
//        }
    }

    public List<SysUserOnline> oneLineList(String ipaddr, String userName)
    {
        Collection<String> keys = redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys)
        {
            com.platform.system.api.model.LoginUser modelUser = redisCache.getCacheObject(key);
            if (modelUser == null) {
                continue;
            }
            LoginUser user = new LoginUser();
            BeanCopyUtils.copyProperties(modelUser, user);

            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName))
            {
                userOnlineList.add(onlineService.selectOnlineByInfo(ipaddr, userName, user));
            }
            else if (StringUtils.isNotEmpty(ipaddr))
            {
                userOnlineList.add(onlineService.selectOnlineByIpaddr(ipaddr, user));
            }
            else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(user.getSysUser()))
            {
                userOnlineList.add(onlineService.selectOnlineByUserName(userName, user));
            }
            else
            {
                userOnlineList.add(onlineService.loginUserToUserOnline(user));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return userOnlineList;
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
            String captcha = redisCache.getCacheObject(verifyKey);
            redisCache.deleteObject(verifyKey);
            if (captcha == null)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
                throw new CaptchaExpireException();
            }
            if (!code.equalsIgnoreCase(captcha))
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
                throw new CaptchaException();
            }
        }
    }

    /**
     * 登录前置校验
     * @param username 用户名
     * @param password 用户密码
     */
    public void loginPreCheck(String username, String password)
    {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null")));
            throw new UserNotExistsException();
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // IP黑名单校验
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked")));
            throw new BlackListException();
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId)
    {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr());
        sysUser.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(sysUser);
    }
}
