package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConContactBusinessTypeV;

/**
 * 对接业务类型_供应商Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConContactBusinessTypeVService extends IService<ConContactBusinessTypeV>{
    /**
     * 查询对接业务类型_供应商
     * 
     * @param sid 对接业务类型_供应商ID
     * @return 对接业务类型_供应商
     */
    public ConContactBusinessTypeV selectConContactBusinessTypeVById(Long sid);

    /**
     * 查询对接业务类型_供应商列表
     * 
     * @param conContactBusinessTypeV 对接业务类型_供应商
     * @return 对接业务类型_供应商集合
     */
    public List<ConContactBusinessTypeV> selectConContactBusinessTypeVList(ConContactBusinessTypeV conContactBusinessTypeV);

    /**
     * 新增对接业务类型_供应商
     * 
     * @param conContactBusinessTypeV 对接业务类型_供应商
     * @return 结果
     */
    public int insertConContactBusinessTypeV(ConContactBusinessTypeV conContactBusinessTypeV);

    /**
     * 修改对接业务类型_供应商
     * 
     * @param conContactBusinessTypeV 对接业务类型_供应商
     * @return 结果
     */
    public int updateConContactBusinessTypeV(ConContactBusinessTypeV conContactBusinessTypeV);

    /**
     * 变更对接业务类型_供应商
     *
     * @param conContactBusinessTypeV 对接业务类型_供应商
     * @return 结果
     */
    public int changeConContactBusinessTypeV(ConContactBusinessTypeV conContactBusinessTypeV);

    /**
     * 批量删除对接业务类型_供应商
     * 
     * @param sids 需要删除的对接业务类型_供应商ID
     * @return 结果
     */
    public int deleteConContactBusinessTypeVByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conContactBusinessTypeV
    * @return
    */
    int changeStatus(ConContactBusinessTypeV conContactBusinessTypeV);

    /**
     * 更改确认状态
     * @param conContactBusinessTypeV
     * @return
     */
    int check(ConContactBusinessTypeV conContactBusinessTypeV);

}
