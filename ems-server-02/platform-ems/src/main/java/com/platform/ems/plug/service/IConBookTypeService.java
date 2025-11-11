package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConAccountCategory;
import com.platform.ems.plug.domain.ConBookType;

/**
 * 流水类型_财务Service接口
 *
 * @author linhongwei
 * @date 2021-06-29
 */
public interface IConBookTypeService extends IService<ConBookType>{
    /**
     * 查询流水类型_财务
     *
     * @param sid 流水类型_财务ID
     * @return 流水类型_财务
     */
    public ConBookType selectConBookTypeById(Long sid);

    /**
     * 查询流水类型_财务列表
     *
     * @param conBookType 流水类型_财务
     * @return 流水类型_财务集合
     */
    public List<ConBookType> selectConBookTypeList(ConBookType conBookType);

    /**
     * 新增流水类型_财务
     *
     * @param conBookType 流水类型_财务
     * @return 结果
     */
    public int insertConBookType(ConBookType conBookType);

    /**
     * 修改流水类型_财务
     *
     * @param conBookType 流水类型_财务
     * @return 结果
     */
    public int updateConBookType(ConBookType conBookType);

    /**
     * 变更流水类型_财务
     *
     * @param conBookType 流水类型_财务
     * @return 结果
     */
    public int changeConBookType(ConBookType conBookType);

    /**
     * 批量删除流水类型_财务
     *
     * @param sids 需要删除的流水类型_财务ID
     * @return 结果
     */
    public int deleteConBookTypeByIds(List<Long>  sids);

    /**
     * 启用/停用
     * @param conBookType
     * @return
     */
    int changeStatus(ConBookType conBookType);

    /**
     * 更改确认状态
     * @param conBookType
     * @return
     */
    int check(ConBookType conBookType);

    /**
     * 流水类型财务下拉框列表
     */
    List<ConBookType> getConBookTypeList(ConBookType conBookType);
}
