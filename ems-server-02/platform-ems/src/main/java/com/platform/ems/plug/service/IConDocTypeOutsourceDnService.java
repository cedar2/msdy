package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeOutsourceDn;
import com.platform.ems.plug.domain.ConDocTypeOutsourcePo;

/**
 * 单据类型_外发加工交货单Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeOutsourceDnService extends IService<ConDocTypeOutsourceDn>{
    /**
     * 查询单据类型_外发加工交货单
     *
     * @param sid 单据类型_外发加工交货单ID
     * @return 单据类型_外发加工交货单
     */
    public ConDocTypeOutsourceDn selectConDocTypeOutsourceDnById(Long sid);

    /**
     * 查询单据类型_外发加工交货单列表
     *
     * @param conDocTypeOutsourceDn 单据类型_外发加工交货单
     * @return 单据类型_外发加工交货单集合
     */
    public List<ConDocTypeOutsourceDn> selectConDocTypeOutsourceDnList(ConDocTypeOutsourceDn conDocTypeOutsourceDn);

    /**
     * 新增单据类型_外发加工交货单
     *
     * @param conDocTypeOutsourceDn 单据类型_外发加工交货单
     * @return 结果
     */
    public int insertConDocTypeOutsourceDn(ConDocTypeOutsourceDn conDocTypeOutsourceDn);

    /**
     * 修改单据类型_外发加工交货单
     *
     * @param conDocTypeOutsourceDn 单据类型_外发加工交货单
     * @return 结果
     */
    public int updateConDocTypeOutsourceDn(ConDocTypeOutsourceDn conDocTypeOutsourceDn);

    /**
     * 变更单据类型_外发加工交货单
     *
     * @param conDocTypeOutsourceDn 单据类型_外发加工交货单
     * @return 结果
     */
    public int changeConDocTypeOutsourceDn(ConDocTypeOutsourceDn conDocTypeOutsourceDn);

    /**
     * 批量删除单据类型_外发加工交货单
     *
     * @param sids 需要删除的单据类型_外发加工交货单ID
     * @return 结果
     */
    public int deleteConDocTypeOutsourceDnByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeOutsourceDn
    * @return
    */
    int changeStatus(ConDocTypeOutsourceDn conDocTypeOutsourceDn);

    /**
     * 更改确认状态
     * @param conDocTypeOutsourceDn
     * @return
     */
    int check(ConDocTypeOutsourceDn conDocTypeOutsourceDn);

    /**  获取下拉列表 */
    List<ConDocTypeOutsourceDn> getConDocTypeOutsourceDnList();
}
