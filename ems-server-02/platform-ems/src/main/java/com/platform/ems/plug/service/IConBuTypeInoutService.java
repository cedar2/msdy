package com.platform.ems.plug.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeInout;

import java.util.List;

/**
 * 业务类型-出入库Service接口
 * 
 * @author linhongwei
 * @date 2022-10-09
 */
public interface IConBuTypeInoutService extends IService<ConBuTypeInout>{
    /**
     * 查询业务类型-出入库
     * 
     * @param sid 业务类型-出入库ID
     * @return 业务类型-出入库
     */
    public ConBuTypeInout selectConBuTypeInoutById(Long sid);

    /**
     * 查询业务类型-出入库列表
     * 
     * @param conBuTypeInout 业务类型-出入库
     * @return 业务类型-出入库集合
     */
    public List<ConBuTypeInout> selectConBuTypeInoutList(ConBuTypeInout conBuTypeInout);

    /**
     * 查询业务类型-出入库列表  下拉框接口
     *
     * @param conBuTypeInout 业务类型-出入库
     * @return 业务类型-出入库集合
     */
    public List<ConBuTypeInout> getConBuTypeInoutList(ConBuTypeInout conBuTypeInout);

    /**
     * 新增业务类型-出入库
     * 
     * @param conBuTypeInout 业务类型-出入库
     * @return 结果
     */
    public int insertConBuTypeInout(ConBuTypeInout conBuTypeInout);

    /**
     * 修改业务类型-出入库
     * 
     * @param conBuTypeInout 业务类型-出入库
     * @return 结果
     */
    public int updateConBuTypeInout(ConBuTypeInout conBuTypeInout);

    /**
     * 变更业务类型-出入库
     *
     * @param conBuTypeInout 业务类型-出入库
     * @return 结果
     */
    public int changeConBuTypeInout(ConBuTypeInout conBuTypeInout);

    /**
     * 批量删除业务类型-出入库
     * 
     * @param sids 需要删除的业务类型-出入库ID
     * @return 结果
     */
    public int deleteConBuTypeInoutByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeInout
    * @return
    */
    int changeStatus(ConBuTypeInout conBuTypeInout);

    /**
     * 更改确认状态
     * @param conBuTypeInout
     * @return
     */
    int check(ConBuTypeInout conBuTypeInout);

}
