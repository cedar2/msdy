package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.page.TableDataInfo;
import com.platform.system.domain.SysBusinessBcst;

/**
 * 业务动态列Service接口
 *
 * @author linhongwei
 * @date 2021-06-30
 */
public interface ISysBusinessBcstService extends IService<SysBusinessBcst> {
    /**
     * 查询业务动态列
     *
     * @return 业务动态列
     */
    public SysBusinessBcst selectSysBusinessBcstById(Long businessBcstSid);

    /**
     * 查询业务动态列列表（用户工作台）
     *
     * @param sysBusinessBcst 业务动态列
     * @return 业务动态列集合
     */
    public List<SysBusinessBcst> selectSysBusinessBcstList(SysBusinessBcst sysBusinessBcst);

    /**
     * 查询业务动态列报表
     *
     * @param sysBusinessBcst 业务动态列
     * @return 业务动态列集合
     */
    public List<SysBusinessBcst> selectSysBusinessBcstReport(SysBusinessBcst sysBusinessBcst);

    TableDataInfo selectSysBusinessBcstTable(SysBusinessBcst sysBusinessBcst);

    /**
     * 新增业务动态列
     *
     * @param sysBusinessBcst 业务动态列
     * @return 结果
     */
    public int insertSysBusinessBcst(SysBusinessBcst sysBusinessBcst);


    /**
     * 批量删除业务动态列
     *
     * @param ids 需要删除的业务动态列ID
     * @return 结果
     */
    public int deleteSysBusinessBcstByIds(List<String> ids);


}
