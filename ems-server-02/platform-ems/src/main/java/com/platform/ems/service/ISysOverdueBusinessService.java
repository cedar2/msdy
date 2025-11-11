package com.platform.ems.service;

import java.util.List;
import com.platform.common.core.page.TableDataInfo;
import com.platform.system.domain.SysOverdueBusiness;

/**
 * 已逾期警示列Service接口
 *
 * @author linhongwei
 * @date 2021-06-29
 */
public interface ISysOverdueBusinessService {
    /**
     * 查询已逾期警示列
     *
     * @param id 已逾期警示列ID
     * @return 已逾期警示列
     */
    public SysOverdueBusiness selectSysOverdueBusinessById(String id);

    /**
     * 查询已逾期警示列列表（用户工作台）
     *
     * @param sysOverdueBusiness 已逾期警示列
     * @return 已逾期警示列集合
     */
    public List<SysOverdueBusiness> selectSysOverdueBusinessList(SysOverdueBusiness sysOverdueBusiness);

    /**
     * 查询已逾期警示列报表
     *
     * @param sysOverdueBusiness 已逾期警示列
     * @return 已逾期警示列集合
     */
    public List<SysOverdueBusiness> selectSysOverdueBusinessReport(SysOverdueBusiness sysOverdueBusiness);

    public TableDataInfo selectSysOverdueBusinessTable(SysOverdueBusiness sysOverdueBusiness);

    /**
     * 新增已逾期警示列
     *
     * @param sysOverdueBusiness 已逾期警示列
     * @return 结果
     */
    public int insertSysOverdueBusiness(SysOverdueBusiness sysOverdueBusiness);

    /**
     * 修改已逾期警示列
     *
     * @param sysOverdueBusiness 已逾期警示列
     * @return 结果
     */
    public int updateSysOverdueBusiness(SysOverdueBusiness sysOverdueBusiness);


    /**
     * 批量删除已逾期警示列
     *
     * @param ids 需要删除的已逾期警示列ID
     * @return 结果
     */
    public int deleteSysOverdueBusinessByIds(List<String> ids);


}
