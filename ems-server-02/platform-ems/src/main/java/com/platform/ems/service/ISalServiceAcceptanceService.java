package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.SalServiceAcceptance;

import java.util.List;

/**
 * 服务销售验收单Service接口
 * 
 * @author linhongwei
 * @date 2021-04-06
 */
public interface ISalServiceAcceptanceService extends IService<SalServiceAcceptance>{
    /**
     * 查询服务销售验收单
     * 
     * @param serviceAcceptanceSid 服务销售验收单ID
     * @return 服务销售验收单
     */
    public SalServiceAcceptance selectSalServiceAcceptanceById(Long serviceAcceptanceSid);

    /**
     * 查询服务销售验收单列表
     * 
     * @param salServiceAcceptance 服务销售验收单
     * @return 服务销售验收单集合
     */
    public List<SalServiceAcceptance> selectSalServiceAcceptanceList(SalServiceAcceptance salServiceAcceptance);

    /**
     * 新增服务销售验收单
     * 
     * @param salServiceAcceptance 服务销售验收单
     * @return 结果
     */
    public int insertSalServiceAcceptance(SalServiceAcceptance salServiceAcceptance);

    /**
     * 修改服务销售验收单
     * 
     * @param salServiceAcceptance 服务销售验收单
     * @return 结果
     */
    public int updateSalServiceAcceptance(SalServiceAcceptance salServiceAcceptance);

    /**
     * 批量删除服务销售验收单
     * 
     * @param serviceAcceptanceSids 需要删除的服务销售验收单ID
     * @return 结果
     */
    public int deleteSalServiceAcceptanceByIds(Long[] serviceAcceptanceSids);

    /**
     * 服务销售验收单确认
     */
    int confirm(SalServiceAcceptance salServiceAcceptance);

    /**
     * 服务销售验收单变更
     */
    int change(SalServiceAcceptance salServiceAcceptance);
}
