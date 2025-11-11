package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConContactBusinessTypeC;

/**
 * 对接业务类型_客户Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConContactBusinessTypeCService extends IService<ConContactBusinessTypeC>{
    /**
     * 查询对接业务类型_客户
     * 
     * @param sid 对接业务类型_客户ID
     * @return 对接业务类型_客户
     */
    public ConContactBusinessTypeC selectConContactBusinessTypeCById(Long sid);

    /**
     * 查询对接业务类型_客户列表
     * 
     * @param conContactBusinessTypeC 对接业务类型_客户
     * @return 对接业务类型_客户集合
     */
    public List<ConContactBusinessTypeC> selectConContactBusinessTypeCList(ConContactBusinessTypeC conContactBusinessTypeC);

    /**
     * 新增对接业务类型_客户
     * 
     * @param conContactBusinessTypeC 对接业务类型_客户
     * @return 结果
     */
    public int insertConContactBusinessTypeC(ConContactBusinessTypeC conContactBusinessTypeC);

    /**
     * 修改对接业务类型_客户
     * 
     * @param conContactBusinessTypeC 对接业务类型_客户
     * @return 结果
     */
    public int updateConContactBusinessTypeC(ConContactBusinessTypeC conContactBusinessTypeC);

    /**
     * 变更对接业务类型_客户
     *
     * @param conContactBusinessTypeC 对接业务类型_客户
     * @return 结果
     */
    public int changeConContactBusinessTypeC(ConContactBusinessTypeC conContactBusinessTypeC);

    /**
     * 批量删除对接业务类型_客户
     * 
     * @param sids 需要删除的对接业务类型_客户ID
     * @return 结果
     */
    public int deleteConContactBusinessTypeCByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conContactBusinessTypeC
    * @return
    */
    int changeStatus(ConContactBusinessTypeC conContactBusinessTypeC);

    /**
     * 更改确认状态
     * @param conContactBusinessTypeC
     * @return
     */
    int check(ConContactBusinessTypeC conContactBusinessTypeC);

}
