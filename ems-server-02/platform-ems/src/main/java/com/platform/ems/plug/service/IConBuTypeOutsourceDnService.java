package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeOutsourceDn;
import com.platform.ems.plug.domain.ConBuTypeOutsourcePo;

/**
 * 业务类型_外发加工交货单Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeOutsourceDnService extends IService<ConBuTypeOutsourceDn>{
    /**
     * 查询业务类型_外发加工交货单
     *
     * @param sid 业务类型_外发加工交货单ID
     * @return 业务类型_外发加工交货单
     */
    public ConBuTypeOutsourceDn selectConBuTypeOutsourceDnById(Long sid);

    /**
     * 查询业务类型_外发加工交货单列表
     *
     * @param conBuTypeOutsourceDn 业务类型_外发加工交货单
     * @return 业务类型_外发加工交货单集合
     */
    public List<ConBuTypeOutsourceDn> selectConBuTypeOutsourceDnList(ConBuTypeOutsourceDn conBuTypeOutsourceDn);

    /**
     * 新增业务类型_外发加工交货单
     *
     * @param conBuTypeOutsourceDn 业务类型_外发加工交货单
     * @return 结果
     */
    public int insertConBuTypeOutsourceDn(ConBuTypeOutsourceDn conBuTypeOutsourceDn);

    /**
     * 修改业务类型_外发加工交货单
     *
     * @param conBuTypeOutsourceDn 业务类型_外发加工交货单
     * @return 结果
     */
    public int updateConBuTypeOutsourceDn(ConBuTypeOutsourceDn conBuTypeOutsourceDn);

    /**
     * 变更业务类型_外发加工交货单
     *
     * @param conBuTypeOutsourceDn 业务类型_外发加工交货单
     * @return 结果
     */
    public int changeConBuTypeOutsourceDn(ConBuTypeOutsourceDn conBuTypeOutsourceDn);

    /**
     * 批量删除业务类型_外发加工交货单
     *
     * @param sids 需要删除的业务类型_外发加工交货单ID
     * @return 结果
     */
    public int deleteConBuTypeOutsourceDnByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeOutsourceDn
    * @return
    */
    int changeStatus(ConBuTypeOutsourceDn conBuTypeOutsourceDn);

    /**
     * 更改确认状态
     * @param conBuTypeOutsourceDn
     * @return
     */
    int check(ConBuTypeOutsourceDn conBuTypeOutsourceDn);

    /**  获取下拉列表 */
    List<ConBuTypeOutsourceDn> getConBuTypeOutsourceDnList();
}
