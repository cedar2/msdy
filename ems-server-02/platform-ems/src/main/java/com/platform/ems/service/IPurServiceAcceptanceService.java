package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurServiceAcceptance;

/**
 * 服务采购验收单Service接口
 * 
 * @author linhongwei
 * @date 2021-04-07
 */
public interface IPurServiceAcceptanceService extends IService<PurServiceAcceptance>{
    /**
     * 查询服务采购验收单
     * 
     * @param serviceAcceptanceSid 服务采购验收单ID
     * @return 服务采购验收单
     */
    public PurServiceAcceptance selectPurServiceAcceptanceById(Long serviceAcceptanceSid);

    /**
     * 查询服务采购验收单列表
     * 
     * @param purServiceAcceptance 服务采购验收单
     * @return 服务采购验收单集合
     */
    public List<PurServiceAcceptance> selectPurServiceAcceptanceList(PurServiceAcceptance purServiceAcceptance);

    /**
     * 新增服务采购验收单
     * 
     * @param purServiceAcceptance 服务采购验收单
     * @return 结果
     */
    public int insertPurServiceAcceptance(PurServiceAcceptance purServiceAcceptance);

    /**
     * 修改服务采购验收单
     * 
     * @param purServiceAcceptance 服务采购验收单
     * @return 结果
     */
    public int updatePurServiceAcceptance(PurServiceAcceptance purServiceAcceptance);

    /**
     * 批量删除服务采购验收单
     * 
     * @param serviceAcceptanceSids 需要删除的服务采购验收单ID
     * @return 结果
     */
    public int deletePurServiceAcceptanceByIds(Long[] serviceAcceptanceSids);

    /**
     * 服务采购验收单确认
     */
    int confirm(PurServiceAcceptance purServiceAcceptance);

    /**
     * 服务采购验收单变更
     */
    int change(PurServiceAcceptance purServiceAcceptance);

}
