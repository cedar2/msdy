package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasLaboratory;

import java.util.List;

/**
 * 实验室档案Service接口
 *
 * @author c
 * @date 2022-03-31
 */
public interface IBasLaboratoryService extends IService<BasLaboratory> {
    /**
     * 查询实验室档案
     *
     * @param laboratorySid 实验室档案ID
     * @return 实验室档案
     */
    public BasLaboratory selectBasLaboratoryById(Long laboratorySid);

    /**
     * 查询实验室档案列表
     *
     * @param basLaboratory 实验室档案
     * @return 实验室档案集合
     */
    public List<BasLaboratory> selectBasLaboratoryList(BasLaboratory basLaboratory);

    /**
     * 新增实验室档案
     *
     * @param basLaboratory 实验室档案
     * @return 结果
     */
    public int insertBasLaboratory(BasLaboratory basLaboratory);

    /**
     * 修改实验室档案
     *
     * @param basLaboratory 实验室档案
     * @return 结果
     */
    public int updateBasLaboratory(BasLaboratory basLaboratory);

    /**
     * 变更实验室档案
     *
     * @param basLaboratory 实验室档案
     * @return 结果
     */
    public int changeBasLaboratory(BasLaboratory basLaboratory);

    /**
     * 批量删除实验室档案
     *
     * @param laboratorySids 需要删除的实验室档案ID
     * @return 结果
     */
    public int deleteBasLaboratoryByIds(List<Long> laboratorySids);

    /**
     * 启用/停用
     *
     * @param basLaboratory
     * @return
     */
    int changeStatus(BasLaboratory basLaboratory);

    /**
     * 更改确认状态
     *
     * @param basLaboratory
     * @return
     */
    int check(BasLaboratory basLaboratory);

    /**
     * 下拉框
     *
     * @param basLaboratory
     * @return
     */
    List<BasLaboratory> getList(BasLaboratory basLaboratory);

}
