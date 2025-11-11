package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SysUserAgency;

/**
 * 账号代办设置Service接口
 *
 * @author qhq
 * @date 2021-10-18
 */
public interface ISysUserAgencyService extends IService<SysUserAgency>{
    /**
     * 查询账号代办设置
     *
     * @param userAgencySid 账号代办设置ID
     * @return 账号代办设置
     */
    public SysUserAgency selectSysUserAgencyById (Long userAgencySid);

    /**
     * 查询账号代办设置列表
     *
     * @param sysUserAgency 账号代办设置
     * @return 账号代办设置集合
     */
    public List<SysUserAgency> selectSysUserAgencyList (SysUserAgency sysUserAgency);

    /**
     * 新增账号代办设置
     *
     * @param sysUserAgency 账号代办设置
     * @return 结果
     */
    public int insertSysUserAgency (SysUserAgency sysUserAgency);

    /**
     * 修改账号代办设置
     *
     * @param sysUserAgency 账号代办设置
     * @return 结果
     */
    public int updateSysUserAgency (SysUserAgency sysUserAgency);

    /**
     * 变更账号代办设置
     *
     * @param sysUserAgency 账号代办设置
     * @return 结果
     */
    public int changeSysUserAgency (SysUserAgency sysUserAgency);

    /**
     * 批量删除账号代办设置
     *
     * @param userAgencySids 需要删除的账号代办设置ID
     * @return 结果
     */
    public int deleteSysUserAgencyByIds (List<Long> userAgencySids);

    /**
     * 启用/停用
     * @param sysUserAgency
     * @return
     */
    int changeStatus (SysUserAgency sysUserAgency);

    /**
     * 更改确认状态
     * @param sysUserAgency
     * @return
     */
    int check (SysUserAgency sysUserAgency);

}
