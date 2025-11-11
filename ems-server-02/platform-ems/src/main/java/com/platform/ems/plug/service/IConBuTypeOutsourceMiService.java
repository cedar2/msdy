package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeOutsourceMi;

/**
 * 业务类型_外发加工发料单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConBuTypeOutsourceMiService extends IService<ConBuTypeOutsourceMi>{
    /**
     * 查询业务类型_外发加工发料单
     * 
     * @param sid 业务类型_外发加工发料单ID
     * @return 业务类型_外发加工发料单
     */
    public ConBuTypeOutsourceMi selectConBuTypeOutsourceMiById(Long sid);

    /**
     * 查询业务类型_外发加工发料单列表
     * 
     * @param conBuTypeOutsourceMi 业务类型_外发加工发料单
     * @return 业务类型_外发加工发料单集合
     */
    public List<ConBuTypeOutsourceMi> selectConBuTypeOutsourceMiList(ConBuTypeOutsourceMi conBuTypeOutsourceMi);

    /**
     * 新增业务类型_外发加工发料单
     * 
     * @param conBuTypeOutsourceMi 业务类型_外发加工发料单
     * @return 结果
     */
    public int insertConBuTypeOutsourceMi(ConBuTypeOutsourceMi conBuTypeOutsourceMi);

    /**
     * 修改业务类型_外发加工发料单
     * 
     * @param conBuTypeOutsourceMi 业务类型_外发加工发料单
     * @return 结果
     */
    public int updateConBuTypeOutsourceMi(ConBuTypeOutsourceMi conBuTypeOutsourceMi);

    /**
     * 变更业务类型_外发加工发料单
     *
     * @param conBuTypeOutsourceMi 业务类型_外发加工发料单
     * @return 结果
     */
    public int changeConBuTypeOutsourceMi(ConBuTypeOutsourceMi conBuTypeOutsourceMi);

    /**
     * 批量删除业务类型_外发加工发料单
     * 
     * @param sids 需要删除的业务类型_外发加工发料单ID
     * @return 结果
     */
    public int deleteConBuTypeOutsourceMiByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conBuTypeOutsourceMi
    * @return
    */
    int changeStatus(ConBuTypeOutsourceMi conBuTypeOutsourceMi);

    /**
     * 更改确认状态
     * @param conBuTypeOutsourceMi
     * @return
     */
    int check(ConBuTypeOutsourceMi conBuTypeOutsourceMi);

}
