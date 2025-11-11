package com.platform.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.validation.Validator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.model.SysRoleDataAuthFieldValue;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.exception.UtilException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.user.CaptchaException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.security.utils.dingtalk.GetDingtalkCode;
import com.platform.common.security.utils.wx.GetWeiXinCode;
import com.platform.common.utils.LoginType;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.system.mapper.*;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.platform.common.constant.UserConstants;
import com.platform.common.core.domain.entity.SysRole;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.exception.ServiceException;
import com.platform.common.utils.SecurityUtils;
import com.platform.common.utils.StringUtils;
import com.platform.common.utils.bean.BeanValidators;
import com.platform.common.utils.spring.SpringUtils;
import com.platform.system.domain.SysPost;
import com.platform.system.domain.SysUserPost;
import com.platform.common.core.domain.entity.SysUserRole;
import com.platform.system.service.ISysConfigService;
import com.platform.system.service.ISysUserService;

/**
 * 用户 业务层处理
 *
 * @author platform
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService
{
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Resource
    private SysUserMapper userMapper;

    @Resource
    private SystemUserMapper systemUserMapper;

    @Resource
    private SysClientMapper sysClientMapper;

    @Resource
    private SysRoleMapper roleMapper;

    @Resource
    private SysPostMapper postMapper;

    @Resource
    private SysUserRoleMapper userRoleMapper;

    @Resource
    private SysUserPostMapper userPostMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    protected Validator validator;

    @Autowired
    private RedisCache redisService;

    private static final String KEY_HEAD = "forget_";
    private static final Long KEY_TIME = 60L;
    private static final TimeUnit TIME_TYPE = TimeUnit.MINUTES;

    /**
     * 根据条件分页查询用户列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUserList(SysUser user)
    {
        return userMapper.selectUserList(user);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectAllocatedList(SysUser user)
    {
        return userMapper.selectAllocatedList(user);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    public List<SysUser> selectUnallocatedList(SysUser user)
    {
        return userMapper.selectUnallocatedList(user);
    }

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName)
    {
        return userMapper.selectUserByUserName(userName);
    }

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId)
    {
        return userMapper.selectUserById(userId);
    }

    /**
     * 查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName)
    {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list))
        {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName)
    {
        List<SysPost> list = postMapper.selectPostsByUserName(userName);
        if (CollectionUtils.isEmpty(list))
        {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean checkUserNameUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkUserNameUnique(user.getUserName());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public boolean checkPhoneUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public boolean checkEmailUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user)
    {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin())
        {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId)
    {
        if (!SysUser.isAdmin(SecurityUtils.getUserId()))
        {
            SysUser user = new SysUser();
            user.setUserId(userId);
            List<SysUser> users = SpringUtils.getAopProxy(this).selectUserList(user);
            if (StringUtils.isEmpty(users))
            {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
    }

    /**
     * 新增保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertUser(SysUser user)
    {
        // 新增用户信息
        int rows = userMapper.insertUser(user);
        // 新增用户岗位关联
        insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    /**
     *
     */
    @Override
    @Transactional
    public int autoRegister(SysUser user) {
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return insertUser(user);
    }

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean registerUser(SysUser user)
    {
        return userMapper.insertUser(user) > 0;
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateUser(SysUser user)
    {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPostByUserId(userId);
        // 新增用户与岗位管理
        insertUserPost(user);
        return userMapper.updateUser(user);
    }

    /**
     * 用户授权角色
     *
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds)
    {
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user)
    {
        int updateCount = 0;
        if(user.getStatus().equals("0")){
            List<SysUser> userList = userMapper.selectList(new QueryWrapper<SysUser>()
                    .lambda().in(SysUser::getUserId ,user.getUserIdList()));
            for (SysUser item:userList) {
                // 判断当前租户ID的状态为”正常“的用户数是否大于等于所属租户ID的租户信息的“授权账号数”
                SysClient sysClient = sysClientMapper.selectOne(new QueryWrapper<SysClient>().lambda()
                        .eq(SysClient::getClientId,item.getClientId()));
                if (sysClient != null && sysClient.getAccountNumLimitType() != null && sysClient.getSystemFeeType() != null) {
                    if (sysClient.getAccountNumLimitType().equals("XZ") && sysClient.getSystemFeeType().equals("ZCZHS")) {
                        List<SysUser> zcUserList = userMapper.selectList(new QueryWrapper<SysUser>().lambda().eq(SysUser::getStatus,"0")
                                .eq(SysUser::getClientId,item.getClientId()));
                        if (zcUserList.size()>=sysClient.getAuthorizeAccountNum()){
                            throw new BaseException("正常账号数超出授权数，无法创建！");
                        }
                    }
                }
            }

        }
        List<Long> userIdList = Arrays.asList(user.getUserIdList());
        for (Long userId : userIdList) {
            SysUser sysUser = new SysUser();
            int i = userMapper.updateUserStatus(userId, user.getStatus());
            updateCount += i;
        }

        return updateCount;
    }

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user)
    {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户头像
     *
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar)
    {
        return userMapper.updateUserAvatar(userName, avatar) > 0;
    }

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user)
    {
        return userMapper.updateUser(user);
    }

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String userName, String password)
    {
        return userMapper.resetUserPwd(userName, password);
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user)
    {
        this.insertUserRole(user.getUserId(), user.getRoleIds());
    }

    /**
     * 新增用户岗位信息
     *
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user)
    {
        Long[] posts = user.getPostIds();
        if (StringUtils.isNotEmpty(posts))
        {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<SysUserPost>(posts.length);
            for (Long postId : posts)
            {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            userPostMapper.batchUserPost(list);
        }
    }

    /**
     * 新增用户角色信息
     *
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds)
    {
        if (StringUtils.isNotEmpty(roleIds))
        {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>(roleIds.length);
            for (Long roleId : roleIds)
            {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            userRoleMapper.batchUserRole(list);
        }
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserById(Long userId)
    {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位表
        userPostMapper.deleteUserPostByUserId(userId);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds)
    {
        for (Long userId : userIds)
        {
            checkUserAllowed(new SysUser(userId));
            checkUserDataScope(userId);
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPost(userIds);
        return userMapper.deleteUserByIds(userIds);
    }

    /**
     * 导入用户数据
     *
     * @param userList 用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(userList) || userList.size() == 0)
        {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String password = configService.selectConfigByKey("sys.user.initPassword");
        for (SysUser user : userList)
        {
            try
            {
                // 验证是否存在这个用户
                SysUser u = userMapper.selectUserByUserName(user.getUserName());
                if (StringUtils.isNull(u))
                {
                    BeanValidators.validateWithException(validator, user);
                    user.setPassword(SecurityUtils.encryptPassword(password));
                    user.setCreateBy(operName);
                    userMapper.insertUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    BeanValidators.validateWithException(validator, user);
                    checkUserAllowed(u);
                    checkUserDataScope(u.getUserId());
                    user.setUserId(u.getUserId());
                    user.setUpdateBy(operName);
                    userMapper.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、账号 " + user.getUserName() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    @Override
    public SysUser selectUserByNameAndId(String userName,String clientId) {
        return userMapper.selectUserByNameAndId(userName,clientId);
    }

    /**
     * 通过用户ID查询用户数据角色权限字段信息
     * @param userId 用户ID
     * @return 用户数据角色权限字段信息
     */
    @Override
    public List<SysRoleDataAuthFieldValue> selectRoleDataAuthFiledValueList(Long userId) {
        return userMapper.selectRoleDataAuthFiledValueList(userId);
    }

    @Override
    public SysUser selectUserByOpenid(String openid){
        return userMapper.selectUserByOpenid(openid);
    }

    @Override
    public SysUser selectUserByqyUserId(String workWechatOpenid){
        SysUser user = null;
        List<SysUser> userList = userMapper.selectUserByqyUserId(workWechatOpenid);
        if (CollectionUtil.isNotEmpty(userList)) {
            if (userList.size() > 1) {
                throw new BaseException("找到多个绑定账号，请重新登录");
            }
            user = userList.get(0);
        }
        return user;
    }

    @Override
    public SysUser selectUserByDdUserId(String dingtalkOpenid) {
        SysUser user = null;
        List<SysUser> userList = userMapper.selectUserBydingtalk(dingtalkOpenid);
        if (CollectionUtil.isNotEmpty(userList)) {
            if (userList.size() > 1) {
                throw new BaseException("找到多个绑定账号，请重新登录");
            }
            user = userList.get(0);
        }
        return user;
    }

    @Override
    public SysUser selectUserByGzhOpenId(String gzhOpenId) {
        return userMapper.selectUserByGzhOpenId(gzhOpenId);
    }

    /**
     * 用户类型是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkUserTypeUnique(SysUser user) {
        if(UserConstants.USER_TYPE_ADMIN.equals(user.getUserType())){
            Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
            SysUser info = userMapper.checkUserTypeUnique(user);
            if (info!=null
                    &&info.getClientId().equals(user.getClientId())
                    &&info.getUserId().longValue()!=userId.longValue()) {
                return UserConstants.NOT_UNIQUE_NUM;
            }
        }
        return UserConstants.UNIQUE_NUM;
    }

    @Override
    public int cnacel(SysUser user) {
        if ("1".equals(user.getType())){	//钉钉
            user.setDingtalkFlag("0");
            user.setDingtalkOpenid(null);
        }else if("2".equals(user.getType())){	//企业微信
            user.setWorkWechatFlag("0");
            user.setWorkWechatOpenid(null);
        }else if("3".equals(user.getType())){	//公众号
            user.setWxGzhFlag("0");
            user.setWxGzhOpenid(null);
        }
        user.setUserId(ApiThreadLocalUtil.get().getUserid());
        return userMapper.cnacel(user);
    }

    @Override
    public boolean updateUserOpenId(SysUser user) {
        int affectRows = userMapper.updateUserOpenId(user);
        return affectRows == 1;
    }

    @Override
    public String selectUserClientId(Long userId) {
        return userMapper.selectUserClientId(userId);
    }

    @Override
    public int authLogin(SysUser user) {
        SysUser data = new SysUser();
        SysClient sysClient = new SysClient();
        SysUser userInfo = userMapper.selectUserByUserName(user.getUserName());
        if (userInfo != null) {
            BeanCopyUtils.copyProperties(userInfo,data);
            sysClient = sysClientMapper.selectSysClientById(userInfo.getClientId());
        }
        int row = 1;
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        //钉钉
        if (LoginType.DD.equals(user.getType())) {
            if (StrUtil.isBlank(sysClient.getDingtalkAppkey()) || StrUtil.isBlank(sysClient.getDingtalkAppsecret())) {
                throw new BaseException("租户未配置，请联系管理员");
            }
            user.setDingtalkAppkey(sysClient.getDingtalkAppkey());
            user.setDingtalkAppsecret(sysClient.getDingtalkAppsecret());
            JSONObject result = GetDingtalkCode.getDdUserInfo(user);
            String dingtalkOpenid = verify(result, "userid");
//            SysUser sysUser = userMapper.selectUserBydingtalk(dingtalkOpenid);
//            if (sysUser != null && !user.getUserName().equals(sysUser.getUserName())) {
//                throw new BaseException("该钉钉已绑定其它账号，请取消授权后操作");
//            }
            updateWrapper.in(SysUser::getDingtalkOpenid, dingtalkOpenid)
                    .set(SysUser::getDingtalkFlag, "1");
            row = systemUserMapper.update(null, updateWrapper);
        }
        //企微
        if (LoginType.QW.equals(user.getType())) {
            if (StrUtil.isBlank(sysClient.getWorkWechatAppkey()) || StrUtil.isBlank(sysClient.getWorkWechatAppsecret())) {
                throw new BaseException("租户未配置，请联系管理员");
            }
            user.setWorkWechatAppkey(sysClient.getWorkWechatAppkey());
            user.setWorkWechatAppsecret(sysClient.getWorkWechatAppsecret());
            JSONObject result = GetWeiXinCode.getQyUserInfo(user);
            String workWechatOpenid = verify(result, "UserId");
//            SysUser sysUser = userMapper.selectUserByqyUserId(workWechatOpenid);
//            if (sysUser != null && !user.getUserName().equals(sysUser.getUserName())) {
//                throw new BaseException("该企业微信已绑定其它账号，请取消授权后操作");
//            }
            updateWrapper.in(SysUser::getWorkWechatOpenid, workWechatOpenid)
                    .set(SysUser::getWorkWechatFlag, "1");
            row = systemUserMapper.update(null, updateWrapper);

        }
        //公众号
        if (LoginType.GZH.equals(user.getType())) {
            if (StrUtil.isBlank(sysClient.getWxGzhAppkey()) || StrUtil.isBlank(sysClient.getWxGzhAppsecret())) {
                throw new BaseException("租户未配置，请联系管理员");
            }
            user.setWxGzhAppkey(sysClient.getWxGzhAppkey());
            user.setWxGzhAppsecret(sysClient.getWxGzhAppsecret());
            System.out.println("wx code : " + user.getCode());
            JSONObject result = GetWeiXinCode.getOpenId(user);
            String wxGzhOpenid = result.get("openid").toString();
            if (StrUtil.isBlank(wxGzhOpenid)) {
                throw new BaseException("读取用户失败,请联系管理员");
            }
            SysUser sysUser = userMapper.selectUserByGzhOpenId(wxGzhOpenid);
            if (sysUser != null && !user.getUserName().equals(sysUser.getUserName())) {
                throw new BaseException("该公众号已绑定其它账号，请取消授权后操作");
            }
            updateWrapper.in(SysUser::getWxGzhOpenid, wxGzhOpenid)
                    .set(SysUser::getWxGzhFlag, "1");
            row = systemUserMapper.update(null, updateWrapper);
        }
        return row;
    }

    /**
     * 发送验证码
     */
    @Override
    public SysUser verifyEmail(SysUser user) {
        String userName = user.getUserName();
        String clientId = user.getClientId();
        SysUser sysUser = userMapper.selectUserByNameAndId(userName, clientId);
        if (sysUser == null){
            throw new BaseException("账号或ID有误，请确认");
        }
        //通过用户邮箱发送
        String userEmail = sysUser.getEmail();
        if (StrUtil.isBlank(userEmail)) {
            throw new BaseException("该账号未绑定邮箱，请联系管理员");
        } else {
            String key = KEY_HEAD + userEmail;
            String code = getCode(key);
            try {
                MailUtil.send(CollUtil.newArrayList(userEmail), "重置密码", "Verification Code: <font size=\"6px\"  color=\"red\" >" + code + "</font> ", true);
            } catch (Exception e) {
                e.getMessage();
                throw new UtilException("邮件发送失败");
            }
        }
        SysUser sysuser = new SysUser();
        sysuser.setUserId(sysUser.getUserId());
        sysuser.setEmail(userEmail);
        return sysuser;
    }

    /**
     * 匹配验证码
     */
    @Override
    public void verifyCode(SysUser sysUser) {
        String email = sysUser.getEmail();
        String code = sysUser.getCode();
        Long userId = sysUser.getUserId();
        String key = KEY_HEAD + email;
        String cacheCode = redisService.getCacheObject(key);
        if (StrUtil.isEmpty(cacheCode)) {
            throw new CaptchaException("验证码已过期");
        }
        if (!cacheCode.equals(code)) {
            throw new CaptchaException("验证码错误");
        }
        redisService.deleteObject(key);
        redisService.setCacheObject(KEY_HEAD + userId, 1, 10L, TimeUnit.MINUTES);
    }

    /**
     * 生成随机验证码
     */
    private String getCode(String key) {
        String code = RandomUtil.randomNumbers(6);
        redisService.setCacheObject(key, code, KEY_TIME, TIME_TYPE);
        return code;
    }

    private String verify(JSONObject result, String userid) {
        String openid = result.getString(userid);
        if (StrUtil.isBlank(openid)) {
            openid = result.getString("OpenId");
        }
        if (StrUtil.isBlank(openid)) {
            throw new BaseException("读取用户失败,请联系管理员");
        }
        return openid;
    }

}
