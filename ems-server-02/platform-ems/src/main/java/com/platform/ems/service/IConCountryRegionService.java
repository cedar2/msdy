package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.entity.ConCountryRegion;

/**
 * 国家区域Service接口
 *
 * @author qhq
 * @date 2021-03-26
 */
public interface IConCountryRegionService extends IService<ConCountryRegion>{
    /**
     * 查询国家区域
     *
     * @param countryRegionSid 国家区域ID
     * @return 国家区域
     */
    public ConCountryRegion selectConCountryRegionById(Long countryRegionSid);

    /**
     * 查询国家区域列表
     *
     * @param conCountryRegion 国家区域
     * @return 国家区域集合
     */
    public List<ConCountryRegion> selectConCountryRegionList(ConCountryRegion conCountryRegion);

    /**
     * 新增国家区域
     *
     * @param conCountryRegion 国家区域
     * @return 结果
     */
    public int insertConCountryRegion(ConCountryRegion conCountryRegion);

    /**
     * 修改国家区域
     *
     * @param conCountryRegion 国家区域
     * @return 结果
     */
    public int updateConCountryRegion(ConCountryRegion conCountryRegion);

    /**
     * 变更国家区域
     *
     * @param conCountryRegion 国家区域
     * @return 结果
     */
    public int changeConCountryRegion(ConCountryRegion conCountryRegion);

    /**
     * 批量删除国家区域
     *
     * @param countryRegionSids 需要删除的国家区域ID
     * @return 结果
     */
    public int deleteConCountryRegionByIds(List<Long>  countryRegionSids);

    /**
     * 启用/停用
     * @param conCountryRegion
     * @return
     */
    int changeStatus(ConCountryRegion conCountryRegion);

    /**
     * 更改确认状态
     * @param conCountryRegion
     * @return
     */
    int check(ConCountryRegion conCountryRegion);
}
