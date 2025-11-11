package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDocTypeOutsourceMi;

/**
 * 单据类型_外发加工发料单Service接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface IConDocTypeOutsourceMiService extends IService<ConDocTypeOutsourceMi>{
    /**
     * 查询单据类型_外发加工发料单
     * 
     * @param sid 单据类型_外发加工发料单ID
     * @return 单据类型_外发加工发料单
     */
    public ConDocTypeOutsourceMi selectConDocTypeOutsourceMiById(Long sid);

    /**
     * 查询单据类型_外发加工发料单列表
     * 
     * @param conDocTypeOutsourceMi 单据类型_外发加工发料单
     * @return 单据类型_外发加工发料单集合
     */
    public List<ConDocTypeOutsourceMi> selectConDocTypeOutsourceMiList(ConDocTypeOutsourceMi conDocTypeOutsourceMi);

    /**
     * 新增单据类型_外发加工发料单
     * 
     * @param conDocTypeOutsourceMi 单据类型_外发加工发料单
     * @return 结果
     */
    public int insertConDocTypeOutsourceMi(ConDocTypeOutsourceMi conDocTypeOutsourceMi);

    /**
     * 修改单据类型_外发加工发料单
     * 
     * @param conDocTypeOutsourceMi 单据类型_外发加工发料单
     * @return 结果
     */
    public int updateConDocTypeOutsourceMi(ConDocTypeOutsourceMi conDocTypeOutsourceMi);

    /**
     * 变更单据类型_外发加工发料单
     *
     * @param conDocTypeOutsourceMi 单据类型_外发加工发料单
     * @return 结果
     */
    public int changeConDocTypeOutsourceMi(ConDocTypeOutsourceMi conDocTypeOutsourceMi);

    /**
     * 批量删除单据类型_外发加工发料单
     * 
     * @param sids 需要删除的单据类型_外发加工发料单ID
     * @return 结果
     */
    public int deleteConDocTypeOutsourceMiByIds(List<Long>  sids);

    /**
    * 启用/停用
    * @param conDocTypeOutsourceMi
    * @return
    */
    int changeStatus(ConDocTypeOutsourceMi conDocTypeOutsourceMi);

    /**
     * 更改确认状态
     * @param conDocTypeOutsourceMi
     * @return
     */
    int check(ConDocTypeOutsourceMi conDocTypeOutsourceMi);

}
