package com.platform.system.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.platform.common.constant.UserConstants;
import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.common.core.domain.model.SysRoleDataAuthFieldValue;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.user.CaptchaException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.system.api.model.SysUserExport;
import com.platform.system.domain.SysPost;
import com.platform.system.mapper.SysUserMapper;
import com.platform.system.service.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.platform.common.core.controller.BaseController;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.entity.SysRole;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.page.TableDataInfo;
import com.platform.common.utils.SecurityUtils;
import com.platform.common.utils.StringUtils;
import com.platform.common.utils.poi.ExcelUtil;

/**
 * 用户信息
 *
 * @author platform
 */
@RestController
@RequestMapping("/user")
public class SysUserController extends BaseController
{
    /**
     * 用户账号类型租户管理员
     **/
    private static final String USER_TYPE_CLIENT_ADMIN = "ZHGLY";
    private static final String SUPPER_CLIENT_ID = "10000";
    private static final String KEY_HEAD = "forget_";
    private static final String IS_VEIW_PRICE_SALE = "ems:sales:order:saleprice";
    private static final String IS_VEIW_PRICE_PUR = "ems:purchase:order:saleprice";
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysRoleService roleService;
    @Autowired
    private ISysPostService postService;
    @Autowired
    private ISysPermissionService permissionService;
    /**
     * 租户服务
     */
    @Autowired
    private ISysClientService sysClientService;
    @Autowired
    private RedisCache redisService;

    @Autowired
    private ISysDictDataService sysDictDataService;

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

    /**
     * 获取系统用户列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取系统用户列表",
            notes = "获取系统用户列表")
    @ApiResponses(@ApiResponse(code = 200,
            message = "请求成功",
            response = SysUser.class))
    public TableDataInfo list(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        List<DictData> accountTypeList = (List<DictData>) dataMap.get("s_user_account_type");
        if (ObjectUtil.isNotEmpty(accountTypeList)) {
            list.forEach((item) -> {
                accountTypeList.forEach((accountTypeItem) -> {
                    if (StrUtil.equals(accountTypeItem.getDictValue(), item.getAccountType())) {
                        item.setAccountTypeName(accountTypeItem.getDictLabel());
                    }
                });

            });
        }
        return getDataTable(list);
    }

    @PostMapping("/getList")
    public List<SysUser> getList() {
        SysUser user = new SysUser();
        LoginUser loginUser = ApiThreadLocalUtil.get();
        if (loginUser != null && loginUser.getUserid() != null && loginUser.getUserid() != 1L) {
            user.setClientId(loginUser.getClientId());
        }
        user.setStatus("0");
        List<SysUser> list = userService.selectUserList(user);
        return list;
    }

    @PostMapping("/getUserList")
    public List<SysUser> getUserList(@RequestBody SysUser user) {
        LoginUser loginUser = ApiThreadLocalUtil.get();
        if (loginUser != null && loginUser.getUserid() != null && loginUser.getUserid() != 1L) {
            user.setClientId(loginUser.getClientId());
        }
        List<SysUser> list = userService.selectUserList(user);
        return list;
    }

    @PostMapping("/dowmList")
    public R<?> dowmList(@RequestBody SysUser user) {
        LoginUser loginUser = ApiThreadLocalUtil.get();
        if (loginUser != null && loginUser.getUserid() != null && loginUser.getUserid() != 1L) {
            user.setClientId(loginUser.getClientId());
        }
        List<SysUser> list = userService.selectUserList(user);
        return R.ok(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUserExport user) throws IOException {
        SysUser sysUser = new SysUser();
        BeanCopyUtils.copyProperties(user, sysUser);
        List<SysUser> list = userService.selectUserList(sysUser);
        Map<String, Object> dataMap = sysDictDataService.getDictDataList();
        ExcelUtil<SysUserExport> util = new ExcelUtil<>(SysUserExport.class, dataMap);
        util.exportExcel(response, BeanCopyUtils.copyListProperties(list, SysUserExport::new), "用户数据");
    }

    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = SecurityUtils.getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info/{username}")
    public R<LoginUser> info(@PathVariable("username") String username) {
        SysUser sysUser = userService.selectUserByUserName(username);
        if (StringUtils.isNull(sysUser)) {
            return R.fail("用户名或密码错误");
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
        return R.ok(sysUserVo);
    }

    @PostMapping("/info/{openId}")
    public R<LoginUser> infoByopenid(@PathVariable("openId") String openId) {
        SysUser sysUser = userService.selectUserByOpenid(openId);
        if (StringUtils.isNull(sysUser)) {
            return R.fail("用户不存在");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser.getUserId());
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser.getUserId());
        LoginUser sysUserVo = new LoginUser();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        return R.ok(sysUserVo);
    }

    @PostMapping("/info/qy/{userId}")
    public R<LoginUser> infoByqyUserId(@PathVariable("userId") String userId) {
        SysUser sysUser = userService.selectUserByqyUserId(userId);
        if (StringUtils.isNull(sysUser)) {
            return R.fail("用户不存在");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser.getUserId());
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser.getUserId());
        LoginUser sysUserVo = new LoginUser();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        return R.ok(sysUserVo);
    }

    @GetMapping("/qiyeBingding")
    public R<Boolean> qiyeBingding(@RequestParam("userId") Long userId,
                                   @RequestParam("qiyeUserId") String qiyeUserId,
                                   @RequestParam("type") String type) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        if ("1".equals(type)) {
            sysUser.setDingtalkOpenid(qiyeUserId);
        } else if ("2".equals(type)) {
            sysUser.setWorkWechatOpenid(qiyeUserId);
        } else if ("3".equals(type)) {
            sysUser.setWxGzhOpenid(qiyeUserId);
        }
        int row = userService.updateUserStatus(sysUser);
        if (row < 1) {
            return R.fail("绑定失败");
        }
        return R.ok();
    }

    @PostMapping("/info/dd/{userId}")
    public R<LoginUser> getInfoByDdUserId(@PathVariable("userId") String userId) {
        SysUser sysUser = userService.selectUserByDdUserId(userId);
        if (StringUtils.isNull(sysUser)) {
            return R.fail("用户不存在");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser.getUserId());
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser.getUserId());
        LoginUser sysUserVo = new LoginUser();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        return R.ok(sysUserVo);
    }

    @PostMapping("/info/wxgzh/{gzhOpenId}")
    public R<LoginUser> getInfoByGzhOpenId(@PathVariable("gzhOpenId") String gzhOpenId) {
        SysUser sysUser = userService.selectUserByGzhOpenId(gzhOpenId);
        if (StringUtils.isNull(sysUser)) {
            return R.fail("用户不存在");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser.getUserId());
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser.getUserId());
        LoginUser sysUserVo = new LoginUser();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        return R.ok(sysUserVo);
    }

    /**
     * 根据租户号获取当前用户信息
     */
    @CrossOrigin
    @GetMapping("/getInfoByClientId")
    public R<LoginUser> getInfoByClientId(String username, String clientId) {
        // 2022-10-8 wp 功能优化
        SysClient sysClient = sysClientService.selectSysClientById(clientId);

        // 租户为空 || 租户处理状态不为确认 || 租户状态不为启用
        if (sysClient == null || !"5".equals(sysClient.getHandleStatus())) {
            return R.fail("租户ID" + clientId + "不存在！");
        }
        else if (!"1".equals(sysClient.getStatus())) {
            return R.fail("租户ID" + clientId + "已停用！");
        }

        //----------------------------
        SysUser sysUser = userService.selectUserByNameAndId(username, clientId);
        // 没有该用户 || 该用户的状态不为启用状态
        if (sysUser == null || !sysUser.getStatus().equals("0")) {
            return R.fail("账号错误");
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
        return R.ok(sysUserVo);
    }

    @PostMapping("/query")
    public R<SysUser> query(@RequestBody SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        if (list.size() != 1) {
            return R.fail("用户信息异常");
        }
        return R.ok(list.get(0));
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        LoginUser loginUser = ApiThreadLocalUtil.get();
        Long userId = ApiThreadLocalUtil.get().getUserid();
        SysUser sysUser = userService.selectUserById(userId);
        // 数据角色
        List<SysRoleDataAuthFieldValue> fieldValueList = userService.selectRoleDataAuthFiledValueList(userId);
        if (sysUser != null) {
            sysUser.setFieldValueList(fieldValueList);
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(userId);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(userId);
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
        // 租户
        SysClient client = sysClientService.selectSysClientById(sysUser.getClientId());

        AjaxResult ajax = AjaxResult.success();
        ajax.put("client", client);
        ajax.put("user", sysUser);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;

    }

    /**
     * 根据用户编号获取详细信息
     */
    @ApiOperation(value = "根据用户编号获取详细信息",
            notes = "根据用户编号获取详细信息")
    @ApiResponses(@ApiResponse(code = 200,
            message = "请求成功",
            response = SysUser.class))
    @GetMapping(value = {"/", "/{userId}"})
    public AjaxResult getInfo(@PathVariable(value = "userId",
            required = false) String userId) {
        Long loginId = ApiThreadLocalUtil.get().getUserid();
        AjaxResult ajax = AjaxResult.success();
        List<SysRole> roles;
        List<SysPost> posts;
        if (loginId != null && loginId == 1L) {
            roles = roleService.selectRoleAll(null);
            posts = postService.selectPostAll(null);
            ajax.put("roles", roles);
        } else {
            String clientId = ApiThreadLocalUtil.get().getSysUser().getClientId();
            roles = roleService.selectRoleAll(clientId);
            posts = postService.selectPostAll(clientId);
            if (!USER_TYPE_CLIENT_ADMIN.equals(ApiThreadLocalUtil.get().getSysUser().getUserType())) {
                ajax.put("roles",
                        roles.stream().filter(r -> r.getCreateBy().equals(ApiThreadLocalUtil.get().getUsername())).collect(
                                Collectors.toList()));
            } else {
                ajax.put("roles", roles);
            }
        }
        ajax.put("posts", posts);
        if (StringUtils.isNotNull(userId)) {
            ajax.put(AjaxResult.DATA_TAG, userService.selectUserById(Long.valueOf(userId)));
            ajax.put("postIds", postService.selectPostListByUserId(Long.valueOf(userId)));
            ajax.put("roleIds", roleService.selectRoleListByUserId(Long.valueOf(userId)));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        LoginUser loginUser = ApiThreadLocalUtil.get();
        if (StrUtil.isEmpty(user.getClientId())) {
            user.setClientId(loginUser.getSysUser().getClientId());
        }
        if (UserConstants.NOT_UNIQUE_NUM.equals(userService.checkUserNameUnique(user))) {
            return AjaxResult.error("账号已存在");
        } else if (UserConstants.NOT_UNIQUE_NUM.equals(userService.checkPhoneUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (UserConstants.NOT_UNIQUE_NUM.equals(userService.checkEmailUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        } else if (UserConstants.NOT_UNIQUE_NUM.equals(userService.checkUserTypeUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，该租户已存在租户管理员账号，操作失败！");
        }
        valPass(user.getPassword());
        user.setCreateBy(loginUser.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setWorkWechatFlag("0");
        user.setWxGzhFlag("0");
        user.setWxXcxFlag("0");
        int row = 0;
        try {
            row = userService.insertUser(user);
        } catch (BaseException e) {
            // 员工已存在用户档案中，是否继续
            if ("101".equals(e.getCode())) {
                return AjaxResult.success(e.getDefaultMessage(), null);
            }
            throw e;
        }
        return toAjax(row);
    }

    /**
     * 租户管理员自动注册
     */
    @PostMapping("/autoRegister")
    public R autoRegister(@Validated @RequestBody SysUser user) {
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return R.ok(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        SysUser verifyUser = new SysUser();
        BeanCopyUtils.copyProperties(user, verifyUser);
        verifyUser.setClientId(userService.selectUserClientId(user.getUserId()));
        if (UserConstants.NOT_UNIQUE_NUM.equals(userService.checkUserNameUnique(user))) {
            return AjaxResult.error("账号已存在");
        }
        if (UserConstants.NOT_UNIQUE_NUM.equals(userService.checkPhoneUnique(verifyUser))) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (UserConstants.NOT_UNIQUE_NUM.equals(userService.checkEmailUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        } else if (UserConstants.NOT_UNIQUE_NUM.equals(userService.checkUserTypeUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，该租户已存在租户管理员账号，操作失败！");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        int row = 0;
        try {
            row = userService.updateUser(user);
        } catch (BaseException e) {
            // 员工已存在用户档案中，是否继续
            if ("101".equals(e.getCode())) {
                return AjaxResult.success(e.getDefaultMessage(), null);
            }
            throw e;
        }
        return toAjax(row);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        valPass(user.getPassword());
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.resetPwd(user));
    }

    @PutMapping("/editPwd")
    public AjaxResult editPwd(@RequestBody SysUser user) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        userService.checkUserAllowed(user);
        if (loginUser.getSysUser().getPassword().equals(SecurityUtils.encryptPassword(user.getPassword()))) {
            throw new CustomException("旧密码验证错误");
        }
        valPass(user.getPassword());
        user.setPassword(SecurityUtils.encryptPassword(user.getNewpassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.resetPwd(user));
    }

    @CrossOrigin
    @PostMapping("/api/resetPwd")
    public R apiresetPwd(@RequestBody SysUser user) {
        String key = KEY_HEAD + user.getUserId();
        String cacheCode = redisService.getCacheObject(key).toString();
        if (StrUtil.isEmpty(cacheCode)) {
            throw new CaptchaException("重置密码超时，请重试");
        }
        userService.checkUserAllowed(user);
        valPass(user.getPassword());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(ApiThreadLocalUtil.get().getUsername());
        int row = userService.resetPwd(user);
        if (row <= 0) {
            return R.fail("重置密码失败");
        }
        redisService.deleteObject(cacheCode);
        return R.ok();
    }

    /**
     * 授权登录
     */
    @PostMapping("/auth/login")
    public R<?> authLogin(@RequestBody SysUser user) {
        // code:临时授权码 type:(1-钉钉 2-企微 3-公众号)
        if (StrUtil.isBlank(user.getCode()) || StrUtil.isBlank(user.getType()) || StrUtil.isBlank(user.getUserName())) {
            throw new BaseException("参数错误");
        }
        return R.ok(userService.authLogin(user));
    }

    /**
     * 取消授权
     */
    @PostMapping("/cnacel/authorization")
    public AjaxResult cnacel(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        return toAjax(userService.cnacel(user));
    }

    /**
     * 状态修改
     */
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 发送验证码
     *
     * @return
     */
    @PostMapping("/sendCode")
    public R<?> resetPassword(@RequestBody SysUser user) {
        if (StrUtil.isEmpty(user.getUserName()) || StrUtil.isEmpty(user.getClientId())) {
            return R.fail("请输入账号及ID");
        }
        SysUser sysUser = userService.verifyEmail(user);

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
        userService.verifyCode(sysUser);
        return R.ok(null, "操作成功");
    }

    @Autowired
    private SysUserMapper userMapper;

    /**
     * 保存用户的feishu的openid
     */
    @PostMapping("/platform/save/feishu")
    public R<?> platformSaveFeishuOpenId(@RequestParam("userId") Long userId,
                                         @RequestParam("feishuOpenId") String feishuOpenId) {
        userMapper.update(null, new LambdaUpdateWrapper<SysUser>().set(SysUser::getFeishuOpenId, null)
                .eq(SysUser::getFeishuOpenId, feishuOpenId));
        return platformSaveUser(userId,
                                user -> user.feishuOpenId = feishuOpenId
        );
    }

    /**
     * 保存钉钉的userId
     */
    @PostMapping("/platform/save/dingTalk")
    public R<?> platformSaveDingTalkUserId(@RequestParam("userId") Long userId,
                                           @RequestParam("dingTalkUserId") String dingTalkUserId) {
        userMapper.update(null, new LambdaUpdateWrapper<SysUser>().set(SysUser::getDingtalkOpenid, null)
                .eq(SysUser::getDingtalkOpenid, dingTalkUserId));
        return platformSaveUser(userId,
                                user -> user.setDingtalkOpenid(dingTalkUserId)
        );
    }


    /**
     * 保存企业微信的userId
     */
    @PostMapping("/platform/save/workWechat")
    public R<?> platformSaveWorkWechatUserId(@RequestParam("userId") Long userId,
                                             @RequestParam("workWechatOpenId") String workWechatOpenId) {
        userMapper.update(null, new LambdaUpdateWrapper<SysUser>().set(SysUser::getWorkWechatOpenid, null)
                .eq(SysUser::getWorkWechatOpenid, workWechatOpenId));
        return platformSaveUser(userId,
                                user -> user.setWorkWechatOpenid(workWechatOpenId)
        );
    }

    /**
     * 保存微信公众号的userId
     */
    @PostMapping("/platform/save/wechatGzh")
    public R<?> platformSaveWechatGzhUserId(@RequestParam("userId") Long userId,
                                            @RequestParam("wechatGzhOpenId") String wechatGzhOpenId) {
        userMapper.update(null, new LambdaUpdateWrapper<SysUser>().set(SysUser::getWxGzhOpenid, null)
                .eq(SysUser::getWxGzhOpenid, wechatGzhOpenId));
        return platformSaveUser(userId,
                                user -> user.setWxGzhOpenid(wechatGzhOpenId)
        );
    }


    private R<Object> platformSaveUser(Long userId, Consumer<SysUser> consumer) {
        SysUser user = userService.selectUserById(userId);
        if (user == null) {
            return R.ok(null, "用户不存在");
        }
        consumer.accept(user);
        boolean updateSuccess = userService.updateUserOpenId(user);
        return updateSuccess ? R.ok() : R.fail();
    }
}
