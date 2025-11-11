package com.platform.ems.service;

import com.platform.common.core.page.TableDataInfo;
import com.platform.system.domain.SysToexpireBusiness;

import java.util.List;

/**
 * 即将到期预警Service接口
 *
 * @author linhongwei
 * @date 2021-06-29
 */
public interface ISysToexpireBusinessService {
    /**
     * 查询即将到期预警
     *
     * @param id 即将到期预警ID
     * @return 即将到期预警
     */
    public SysToexpireBusiness selectSysToexpireBusinessById(String id);

    /**
     * 查询即将到期预警列表 (用户工作台)
     *
     * @param sysToexpireBusiness 即将到期预警
     * @return 即将到期预警集合
     */
    List<SysToexpireBusiness> selectSysToexpireBusinessList(SysToexpireBusiness sysToexpireBusiness);

    /**
     * 查询即将到期预警报表
     *
     * @param sysToexpireBusiness 即将到期预警
     * @return 即将到期预警集合
     */
    List<SysToexpireBusiness> selectSysToexpireBusinessReport(SysToexpireBusiness sysToexpireBusiness);

    TableDataInfo selectSysToexpireBusinessTable(SysToexpireBusiness sysToexpireBusiness);

    /**
     * 新增即将到期预警
     *
     * @param sysToexpireBusiness 即将到期预警
     * @return 结果
     */
    public int insertSysToexpireBusiness(SysToexpireBusiness sysToexpireBusiness);

    /**
     * 修改即将到期预警
     *
     * @param sysToexpireBusiness 即将到期预警
     * @return 结果
     */
    public int updateSysToexpireBusiness(SysToexpireBusiness sysToexpireBusiness);

    /**
     * 批量删除即将到期预警
     *
     * @param ids 需要删除的即将到期预警ID
     * @return 结果
     */
    public int deleteSysToexpireBusinessByIds(List<String> ids);


}
