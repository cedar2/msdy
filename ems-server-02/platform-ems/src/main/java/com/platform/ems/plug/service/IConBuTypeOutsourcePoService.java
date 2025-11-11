package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeOutsourcePo;
import com.platform.ems.plug.domain.ConDiscountType;

/**
 * 业务类型_外发加工单Service接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeOutsourcePoService extends IService<ConBuTypeOutsourcePo>{
    /**
     * 查询业务类型_外发加工单
     *
     * @param sid 业务类型_外发加工单ID
     * @return 业务类型_外发加工单
     */
    public ConBuTypeOutsourcePo selectConBuTypeOutsourcePoById(Long sid);

    /**
     * 查询业务类型_外发加工单列表
     *
     * @param conBuTypeOutsourcePo 业务类型_外发加工单
     * @return 业务类型_外发加工单集合
     */
    public List<ConBuTypeOutsourcePo> selectConBuTypeOutsourcePoList(ConBuTypeOutsourcePo conBuTypeOutsourcePo);

    /**
     * 新增业务类型_外发加工单
     *
     * @param conBuTypeOutsourcePo 业务类型_外发加工单
     * @return 结果
     */
    public int insertConBuTypeOutsourcePo(ConBuTypeOutsourcePo conBuTypeOutsourcePo);

    /**
     * 修改业务类型_外发加工单
     *
     * @param conBuTypeOutsourcePo 业务类型_外发加工单
     * @return 结果
     */
    public int updateConBuTypeOutsourcePo(ConBuTypeOutsourcePo conBuTypeOutsourcePo);

    /**
     * 变更业务类型_外发加工单
     *
     * @param conBuTypeOutsourcePo 业务类型_外发加工单
     * @return 结果
     */
    public int changeConBuTypeOutsourcePo(ConBuTypeOutsourcePo conBuTypeOutsourcePo);

    /**
     * 批量删除业务类型_外发加工单
     *
     * @param sids 需要删除的业务类型_外发加工单ID
     * @return 结果
     */
    public int deleteConBuTypeOutsourcePoByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeOutsourcePo
    * @return
    */
    int changeStatus(ConBuTypeOutsourcePo conBuTypeOutsourcePo);

    /**
     * 更改确认状态
     * @param conBuTypeOutsourcePo
     * @return
     */
    int check(ConBuTypeOutsourcePo conBuTypeOutsourcePo);

    /**  获取下拉列表 */
    List<ConBuTypeOutsourcePo> getConBuTypeOutsourcePoList();
}
