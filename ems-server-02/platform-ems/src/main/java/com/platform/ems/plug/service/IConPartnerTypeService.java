package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConPartnerType;

/**
 * 类型_业务合作伙伴Service接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConPartnerTypeService extends IService<ConPartnerType>{
    /**
     * 查询类型_业务合作伙伴
     * 
     * @param sid 类型_业务合作伙伴ID
     * @return 类型_业务合作伙伴
     */
    public ConPartnerType selectConPartnerTypeById(Long sid);

    /**
     * 查询类型_业务合作伙伴列表
     * 
     * @param conPartnerType 类型_业务合作伙伴
     * @return 类型_业务合作伙伴集合
     */
    public List<ConPartnerType> selectConPartnerTypeList(ConPartnerType conPartnerType);

    /**
     * 新增类型_业务合作伙伴
     * 
     * @param conPartnerType 类型_业务合作伙伴
     * @return 结果
     */
    public int insertConPartnerType(ConPartnerType conPartnerType);

    /**
     * 修改类型_业务合作伙伴
     * 
     * @param conPartnerType 类型_业务合作伙伴
     * @return 结果
     */
    public int updateConPartnerType(ConPartnerType conPartnerType);

    /**
     * 变更类型_业务合作伙伴
     *
     * @param conPartnerType 类型_业务合作伙伴
     * @return 结果
     */
    public int changeConPartnerType(ConPartnerType conPartnerType);

    /**
     * 批量删除类型_业务合作伙伴
     * 
     * @param sids 需要删除的类型_业务合作伙伴ID
     * @return 结果
     */
    public int deleteConPartnerTypeByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conPartnerType
    * @return
    */
    int changeStatus(ConPartnerType conPartnerType);

    /**
     * 更改确认状态
     * @param conPartnerType
     * @return
     */
    int check(ConPartnerType conPartnerType);

}
