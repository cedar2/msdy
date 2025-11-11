package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeOutsourceDn;
import com.platform.ems.plug.domain.ConDocTypeOutsourcePo;

/**
 * 单据类型_外发加工单Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeOutsourcePoService extends IService<ConDocTypeOutsourcePo>{
    /**
     * 查询单据类型_外发加工单
     *
     * @param sid 单据类型_外发加工单ID
     * @return 单据类型_外发加工单
     */
    public ConDocTypeOutsourcePo selectConDocTypeOutsourcePoById(Long sid);

    /**
     * 查询单据类型_外发加工单列表
     *
     * @param conDocTypeOutsourcePo 单据类型_外发加工单
     * @return 单据类型_外发加工单集合
     */
    public List<ConDocTypeOutsourcePo> selectConDocTypeOutsourcePoList(ConDocTypeOutsourcePo conDocTypeOutsourcePo);

    /**
     * 新增单据类型_外发加工单
     *
     * @param conDocTypeOutsourcePo 单据类型_外发加工单
     * @return 结果
     */
    public int insertConDocTypeOutsourcePo(ConDocTypeOutsourcePo conDocTypeOutsourcePo);

    /**
     * 修改单据类型_外发加工单
     *
     * @param conDocTypeOutsourcePo 单据类型_外发加工单
     * @return 结果
     */
    public int updateConDocTypeOutsourcePo(ConDocTypeOutsourcePo conDocTypeOutsourcePo);

    /**
     * 变更单据类型_外发加工单
     *
     * @param conDocTypeOutsourcePo 单据类型_外发加工单
     * @return 结果
     */
    public int changeConDocTypeOutsourcePo(ConDocTypeOutsourcePo conDocTypeOutsourcePo);

    /**
     * 批量删除单据类型_外发加工单
     *
     * @param sids 需要删除的单据类型_外发加工单ID
     * @return 结果
     */
    public int deleteConDocTypeOutsourcePoByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeOutsourcePo
    * @return
    */
    int changeStatus(ConDocTypeOutsourcePo conDocTypeOutsourcePo);

    /**
     * 更改确认状态
     * @param conDocTypeOutsourcePo
     * @return
     */
    int check(ConDocTypeOutsourcePo conDocTypeOutsourcePo);

    /**  获取下拉列表 */
    List<ConDocTypeOutsourcePo> getConDocTypeOutsourcePoList();
}
