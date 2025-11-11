package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysClient;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.entity.SysUserDataRole;
import com.platform.common.core.domain.entity.SysUserRole;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.user.CaptchaException;
import com.platform.common.exception.UtilException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasStaff;
import com.platform.ems.mapper.BasStaffMapper;
import com.platform.system.mapper.SysClientMapper;
import com.platform.system.mapper.SysUserDataRoleMapper;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.mapper.SysUserRoleMapper;
import com.platform.system.mapper.SystemUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.platform.ems.controller.SysClientController.valPass;

/**
 * 用户信息Service业务层处理
 *
 * @author qhq
 * @date 2021-09-13
 */
@Service
@SuppressWarnings("all")
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SysUser> implements ISystemUserService {
    @Autowired
    private SystemUserMapper sysUserMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysUserDataRoleMapper sysUserDataRoleMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SysClientMapper sysClientMapper;
    @Autowired
    private RedisCache redisService;

    @Autowired
    private BasStaffMapper basStaffMapper;

    private static final String KEY_HEAD = "forget_";
    private static final Long KEY_TIME = 60L;
    private static final TimeUnit TIME_TYPE = TimeUnit.MINUTES;


    private static final String TITLE = "用户信息";

    /**
     * 查询用户信息
     *
     * @param userId 用户信息ID
     * @return 用户信息
     */
    @Override
    public SysUser selectSysUserById(Long userId) {
        SysUser sysUser = sysUserMapper.selectSysUserById(userId);
        MongodbUtil.find(sysUser, sysUser.getUserId());
        return sysUser;
    }

    /**
     * 查询用户信息
     *
     * @param userId 用户信息ID
     * @return 用户信息
     */
    @Override
    public SysUser selectSysUserByName(String userName) {
        SysUser sysUser = sysUserMapper.selectSysUserByName(userName);
        return sysUser;
    }

    /**
     * 查询用户信息列表
     *
     * @param SysUser 用户信息
     * @return 用户信息
     */
    @Override
    public List<SysUser> selectSysUserList(SysUser SysUser) {
        return sysUserMapper.selectSysUserList(SysUser);
    }

    /**
     * 新增用户
     * @param sysUser 用户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysUser(SysUser user) {
        int row = 0;
        // 判断员工是否已存在
        if (user.getStaffSid() != null && !ConstantsEms.YES.equals(user.getStaffVerifyContinue())) {
            List<SysUser> userList = sysUserMapper.selectSysUserListAll(
                    new SysUser().setStaffSid(user.getStaffSid())
                    .setAccountType(user.getAccountType()));
            if (CollectionUtil.isNotEmpty(userList)) {
                throw new BaseException(null, "101", "该员工已创建账号，是否继续创建？");
            }
        }

        // 账号已存在
        List<SysUser> account = sysUserMapper.selectSysUserListAll(new SysUser().setUserName(user.getUserName())
                .setClientId(user.getClientId()));
        if (CollectionUtil.isNotEmpty(account)) {
            throw new BaseException("用户账号已存在");
        }
        // 手机号码已存在
        if (StrUtil.isNotBlank(user.getPhonenumber())) {
            if (!JudgeFormat.isValidChineseMobileNumber(user.getPhonenumber())) {
                throw new BaseException("手机号码格式不正确");
            }
            List<SysUser> phont = sysUserMapper.selectSysUserListAll(new SysUser()
                    .setPhonenumber(user.getPhonenumber()));
            if (CollectionUtil.isNotEmpty(phont)) {
                throw new BaseException("手机号码已存在");
            }
        }
        // 邮箱账号已存在
        if (StrUtil.isNotBlank(user.getEmail())) {
            if (!JudgeFormat.isValidEmail(user.getEmail())) {
                throw new BaseException("邮箱格式不正确");
            }
            List<SysUser> email = sysUserMapper.selectSysUserListAll(new SysUser().setEmail(user.getEmail()));
            if (CollectionUtil.isNotEmpty(email)) {
                throw new BaseException("邮箱已存在");
            }
        }

        // 该租户已存在租户管理员账号
        if ("ZHGLY".equals(user.getUserType())) {
            List<SysUser> clientAccount = sysUserMapper.selectSysUserListAll(new SysUser()
                    .setClientId(user.getClientId())
                    .setUserType(user.getUserType()));
            if (CollectionUtil.isNotEmpty(clientAccount)) {
                throw new BaseException("该租户已存在租户管理员账号");
            }
        }

        // 判断当前租户ID的状态为”正常“的用户数是否大于等于所属租户ID的租户信息的“授权账号数”
        SysClient sysClient = sysClientMapper.selectOne(new QueryWrapper<SysClient>().lambda()
                .eq(SysClient::getClientId,user.getClientId()));
        if ("XZ".equals(sysClient.getAccountNumLimitType()) && "ZCZHS".equals(sysClient.getSystemFeeType())) {
            List<SysUser> userList = sysUserMapper.selectList(new QueryWrapper<SysUser>().lambda().eq(SysUser::getStatus,"0")
                    .eq(SysUser::getClientId,user.getClientId()));
            if (userList.size()>=sysClient.getAuthorizeAccountNum()){
                throw new BaseException("正常账号数超出授权数，无法创建！");
            }
        }

        valPass(user.getPassword());
        user.setCreateBy(ApiThreadLocalUtil.get().getUsername()).setCreateTime(new Date());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));

        //获取员工的手机号、邮箱
        if("YG".equals(user.getAccountType())){
            BasStaff basStaff = basStaffMapper.selectOne(new QueryWrapper<BasStaff>().lambda()
                    .eq(BasStaff::getStaffSid, user.getStaffSid()));
            if((user.getPhonenumber() == null || user.getPhonenumber().isEmpty()) && basStaff.getMobphone() != null){
                user.setPhonenumber(basStaff.getMobphone());
            }
            if((user.getEmail() == null || user.getEmail().isEmpty()) && basStaff.getEmailPersonal() != null){
                user.setEmail(basStaff.getEmailPersonal());
            }
        }
        row = sysUserMapper.insertInfomation(user);
        List<SysUser> sysUserList = sysUserMapper.selectSysUserListAll(new SysUser().setUserName(user.getUserName()));
        SysUser sysUser = sysUserList.stream().filter(o->o.getClientId().equals(user.getClientId())).collect(Collectors.toList()).get(0);
        // 操作角色
        if (CollectionUtil.isNotEmpty(user.getUserRoleList())) {
            List<SysUserRole> userRoleList = user.getUserRoleList();
            userRoleList.forEach(item->{
                item.setClientId(user.getClientId()).setUserId(sysUser.getUserId())
                        .setCreateTime(new Date()).setCreateBy(user.getCreateBy());
            });
            sysUserRoleMapper.inserts(userRoleList);
        }
        // 数据角色
        if (CollectionUtil.isNotEmpty(user.getUserDataRoleList())) {
            List<SysUserDataRole> userDataRoleList = user.getUserDataRoleList();
            userDataRoleList.forEach(item->{
                item.setClientId(user.getClientId()).setUserId(sysUser.getUserId()).setUserName(user.getUserName())
                        .setCreateDate(new Date()).setCreatorAccount(user.getCreateBy()).setUpdateDate(null).setUpdaterAccount(null);
            });
            sysUserDataRoleMapper.inserts(userDataRoleList);
        }
        MongodbUtil.insertUserLog(sysUser.getUserId(), BusinessType.INSERT.getValue(), null, "用户管理", null);
        return row;
    }

    /**
     * 根据用户查数据角色对象
     * @param userId 用户id
     * @return 结果
     */
    @Override
    public List<SysUserDataRole> selectSysUserDataRoleByUserId(Long userId) {
        return sysUserDataRoleMapper.selectSysUserDataRoleList(new SysUserDataRole().setUserId(userId));
    }

    /**
     * 根据用户查操作角色对象
     * @param userId 用户id
     * @return 结果
     */
    @Override
    public List<SysUserRole> selectSysUserRoleByUserId(Long userId) {
        return sysUserRoleMapper.selectSysUserRoleList(new SysUserRole().setUserId(userId));
    }

    /**
     * 变更用户信息
     * @param sysUser 用户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSysUser(SysUser user) {
        int row = 0;
        // 判断员工是否已存在
        if (user.getStaffSid() != null && !ConstantsEms.YES.equals(user.getStaffVerifyContinue())) {
            List<SysUser> userList = sysUserMapper.selectList(
                    new QueryWrapper<SysUser>().lambda().eq(SysUser::getStaffSid, user.getStaffSid())
                            .eq(SysUser::getAccountType, user.getAccountType())
                            .ne(SysUser::getUserId, user.getUserId()));
            if (CollectionUtil.isNotEmpty(userList)) {
                throw new BaseException(null, "101", "该员工已创建账号，是否继续？");
            }
        }

        // 账号已存在
        List<SysUser> account = sysUserMapper.selectSysUserListAll(new SysUser().setUserName(user.getUserName())
                .setClientId(user.getClientId()));
        if (CollectionUtil.isNotEmpty(account)) {
            account = account.stream().filter(o->!o.getUserId().equals(user.getUserId())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(account)) {
                throw new BaseException("用户账号已存在");
            }
        }
        // 手机号码已存在
        if (StrUtil.isNotBlank(user.getPhonenumber())) {
            if (!JudgeFormat.isValidChineseMobileNumber(user.getPhonenumber())) {
                throw new BaseException("手机号码格式不正确");
            }
            List<SysUser> phone = sysUserMapper.selectSysUserListAll(new SysUser().setPhonenumber(user.getPhonenumber())
                    .setClientId(user.getClientId()));
            if (CollectionUtil.isNotEmpty(phone)) {
                phone = phone.stream().filter(o->!o.getUserId().equals(user.getUserId())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(phone)) {
                    throw new BaseException("手机号码已存在");
                }
            }
        }

        // 邮箱账号已存在
        if (StrUtil.isNotBlank(user.getEmail())) {
            if (!JudgeFormat.isValidEmail(user.getEmail())) {
                throw new BaseException("邮箱格式不正确");
            }
            List<SysUser> email = sysUserMapper.selectSysUserListAll(new SysUser().setEmail(user.getEmail())
                    .setClientId(user.getClientId()));
            if (CollectionUtil.isNotEmpty(email)) {
                email = email.stream().filter(o->!o.getUserId().equals(user.getUserId())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(email)) {
                    throw new BaseException("邮箱已存在");
                }
            }
        }

        // 该租户已存在租户管理员账号
        if ("ZHGLY".equals(user.getUserType())) {
            List<SysUser> clientAccount = sysUserMapper.selectSysUserListAll(new SysUser().setClientId(user.getClientId())
                    .setUserType(user.getUserType()));
            if (CollectionUtil.isNotEmpty(clientAccount)) {
                clientAccount = clientAccount.stream().filter(o->!o.getUserId().equals(user.getUserId())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(clientAccount)) {
                    throw new BaseException("该租户已存在租户管理员账号");
                }
            }
        }
        //获取员工的手机号、邮箱
        if(user.getAccountType().equals("YG")){
            BasStaff basStaff = basStaffMapper.selectOne(new QueryWrapper<BasStaff>().lambda()
                    .eq(BasStaff::getStaffSid, user.getStaffSid()));
            if((user.getPhonenumber() == null || user.getPhonenumber().isEmpty()) && basStaff.getMobphone() != null){
                user.setPhonenumber(basStaff.getMobphone());
            }
            if((user.getEmail() == null || user.getEmail().isEmpty()) && basStaff.getEmailPersonal() != null){
                user.setEmail(basStaff.getEmailPersonal());
            }
        }
        user.setUpdateBy(ApiThreadLocalUtil.get().getUsername()).setUpdateTime(new Date());
        row = sysUserMapper.updateByInfoId(user);

        // 操作角色
        sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, user.getUserId()));
        if (CollectionUtil.isNotEmpty(user.getUserRoleList())) {
            List<SysUserRole> userRoleList = user.getUserRoleList();
            userRoleList.forEach(item->{
                if (item.getUserId() == null) {
                    item.setClientId(user.getClientId()).setUserId(user.getUserId())
                            .setCreateTime(new Date()).setCreateBy(user.getCreateBy());
                }
            });
            sysUserRoleMapper.inserts(userRoleList);
        }
        // 数据角色
        sysUserDataRoleMapper.delete(new QueryWrapper<SysUserDataRole>().lambda().eq(SysUserDataRole::getUserId, user.getUserId()));
        if (CollectionUtil.isNotEmpty(user.getUserDataRoleList())) {
            List<SysUserDataRole> userDataRoleList = user.getUserDataRoleList();
            userDataRoleList.forEach(item->{
                if (item.getUserDataRoleSid() == null) {
                    item.setClientId(user.getClientId()).setUserId(user.getUserId()).setUserName(user.getUserName())
                            .setCreateDate(new Date()).setCreatorAccount(user.getCreateBy());
                }
            });
            sysUserDataRoleMapper.inserts(userDataRoleList);
        }
        MongodbUtil.insertUserLog(user.getUserId(), BusinessType.CHANGE.getValue(), null, "用户管理", null);
        return row;
    }

    /**
     * 发送验证码
     */
    @Override
    public SysUser verifyEmail(SysUser user) {
        String userName = user.getUserName();
        String clientId = user.getClientId();
        SysUser sysUser = sysUserMapper.selectUserByNameAndId(userName, clientId);
        if (sysUser == null) {
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
     * 生成随机验证码
     */
    private String getCode(String key) {
        String code = RandomUtil.randomNumbers(6);
        redisService.setCacheObject(key, code, KEY_TIME, TIME_TYPE);
        return code;
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

    @Override
    public int resetPwd(SysUser user) {
        return sysUserMapper.updateById(user);
    }

    @Override
    public int setOpenid(SysUser user) {
        SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().lambda()
                .eq(SysUser::getUserId, user.getUserId()));
        if (user.getUserId() != null && sysUser != null) {
            sysUserMapper.update(null, new UpdateWrapper<SysUser>().lambda().eq(SysUser::getUserId, sysUser.getUserId())
                    .set(SysUser::getWorkWechatOpenid,user.getWorkWechatOpenid())
                    .set(SysUser::getDingtalkOpenid,user.getDingtalkOpenid())
                    .set(SysUser::getFeishuOpenId,user.getFeishuOpenId())
                    .set(SysUser::getWxGzhOpenid,user.getWxGzhOpenid()));
            return 1;
        }
        return 0;
    }
}
