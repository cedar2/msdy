package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasDepartment;
import com.platform.ems.domain.BasPlant;

/**
 * 工厂档案Service接口
 *
 * @author linhongwei
 * @date 2021-03-15
 */
public interface IBasPlantService extends IService<BasPlant> {
    /**
     * 查询工厂档案
     *
     * @param plantSid 工厂档案ID
     * @return 工厂档案
     */
    public BasPlant selectBasPlantById(Long plantSid);

    /**
     * 查询工厂档案的编码和名称
     *
     * @param plantSid 工厂档案ID
     * @return 工厂档案
     */
    BasPlant selectCodeNameById(Long plantSid);

    /**
     * 查询工厂档案列表
     *
     * @param basPlant 工厂档案
     * @return 工厂档案集合
     */
    public List<BasPlant> selectBasPlantList(BasPlant basPlant);

    /**
     * 新增工厂档案
     *
     * @param basPlant 工厂档案
     * @return 结果
     */
    public int insertBasPlant(BasPlant basPlant);

    /**
     * 修改工厂档案
     *
     * @param basPlant 工厂档案
     * @return 结果
     */
    public int updateBasPlant(BasPlant basPlant);

    /**
     * 批量删除工厂档案
     *
     * @param plantSids 需要删除的工厂档案ID
     * @return 结果
     */
    public int deleteBasPlantByIds(String[] plantSids);

    /**
     * 批量确认工厂档案
     *
     * @param basPlant 工厂档案IDS、确认状态
     * @return 结果
     */
    int confirm(BasPlant basPlant);

    /**
     * 变更工厂档案
     *
     * @param basPlant 工厂档案
     * @return 结果
     */
    int change(BasPlant basPlant);

    /**
     * 批量启用/停用工厂档案
     *
     * @param basPlant 工厂档案IDS、启用/停用状态
     * @return 结果
     */
    int status(BasPlant basPlant);

    /**
     * 工厂档案下拉框列表
     * @return 结果
     */
    List<BasPlant> getPlantList(BasPlant basPlant);

    List<BasDepartment> getDepartmentList(BasPlant basPlant);
}
