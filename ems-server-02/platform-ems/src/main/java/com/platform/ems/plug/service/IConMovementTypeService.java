package com.platform.ems.plug.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConMovementType;

/**
 * 作业类型(移动类型)Service接口
 *
 * @author linhongwei
 * @date 2021-05-20
 */
public interface IConMovementTypeService extends IService<ConMovementType> {
    /**
     * 查询作业类型(移动类型)
     *
     * @param sid 作业类型(移动类型)ID
     * @return 作业类型(移动类型)
     */
    public ConMovementType selectConMovementTypeById(Long sid);

    List<ConMovementType> getList(ConMovementType movementType);

    /**
     * 查询作业类型(移动类型)列表
     *
     * @param conMovementType 作业类型(移动类型)
     * @return 作业类型(移动类型)集合
     */
    public List<ConMovementType> selectConMovementTypeList(ConMovementType conMovementType);

    /**
     * 新增作业类型(移动类型)
     *
     * @param conMovementType 作业类型(移动类型)
     * @return 结果
     */
    public int insertConMovementType(ConMovementType conMovementType);

    /**
     * 修改作业类型(移动类型)
     *
     * @param conMovementType 作业类型(移动类型)
     * @return 结果
     */
    public int updateConMovementType(ConMovementType conMovementType);

    /**
     * 变更作业类型(移动类型)
     *
     * @param conMovementType 作业类型(移动类型)
     * @return 结果
     */
    public int changeConMovementType(ConMovementType conMovementType);

    /**
     * 批量删除作业类型(移动类型)
     *
     * @param sids 需要删除的作业类型(移动类型)ID
     * @return 结果
     */
    public int deleteConMovementTypeByIds(List<Long> sids);

    /**
     * 启用/停用
     *
     * @param conMovementType
     * @return
     */
    int changeStatus(ConMovementType conMovementType);

    /**
     * 更改确认状态
     *
     * @param conMovementType
     * @return
     */
    int check(ConMovementType conMovementType);

    /**
     * 查询作业类型(移动类型)列表
     *
     * @param conMovementType 作业类型(移动类型)
     * @return 作业类型(移动类型)集合
     */
    List<ConMovementType> conMovementTypeList(ConMovementType conMovementType);

    ConMovementType conMovementTypeById(Long sid);

    /**
     * 下拉框列表
     */
    List<ConMovementType> getConMovementTypeList();

    List<ConMovementType> getMovementList(ConMovementType conMovementType);
}
