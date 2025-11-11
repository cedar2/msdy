package com.platform.ems.plug.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConMaterialRequisitionBusinessFlag;

/**
 * 业务标识_领退料Service接口
 *
 * @author platform
 * @date 2024-11-10
 */
public interface IConMaterialRequisitionBusinessFlagService extends IService<ConMaterialRequisitionBusinessFlag>{

    /**
     * 查询业务标识_领退料
     *
     * @param sid 业务标识_领退料ID
     * @return 业务标识_领退料
     */
    public ConMaterialRequisitionBusinessFlag selectConMaterialRequisitionBusinessFlagById(Long sid);

    /**
     * 查询业务标识_领退料列表
     *
     * @param conMaterialRequisitionBusinessFlag 业务标识_领退料
     * @return 业务标识_领退料集合
     */
    public List<ConMaterialRequisitionBusinessFlag> selectConMaterialRequisitionBusinessFlagList(ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag);

    /**
     * 新增业务标识_领退料
     *
     * @param conMaterialRequisitionBusinessFlag 业务标识_领退料
     * @return 结果
     */
    public int insertConMaterialRequisitionBusinessFlag(ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag);

    /**
     * 修改业务标识_领退料
     *
     * @param conMaterialRequisitionBusinessFlag 业务标识_领退料
     * @return 结果
     */
    public int updateConMaterialRequisitionBusinessFlag(ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag);

    /**
     * 变更业务标识_领退料
     *
     * @param conMaterialRequisitionBusinessFlag 业务标识_领退料
     * @return 结果
     */
    public int changeConMaterialRequisitionBusinessFlag(ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag);

    /**
     * 批量删除业务标识_领退料
     *
     * @param sids 需要删除的业务标识_领退料ID
     * @return 结果
     */
    public int deleteConMaterialRequisitionBusinessFlagByIds(List<Long>  sids);

    /**
     * 启用/停用
     * @param conMaterialRequisitionBusinessFlag 请求参数
     * @return
     */
    int changeStatus(ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag);

    /**
     * 更改确认状态
     * @param conMaterialRequisitionBusinessFlag 请求参数
     * @return
     */
    int check(ConMaterialRequisitionBusinessFlag conMaterialRequisitionBusinessFlag);

}
