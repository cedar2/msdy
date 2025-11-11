package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConMeasureUnit;

/**
 * 计量单位Service接口
 *
 * @author linhongwei
 * @date 2021-05-20
 */
public interface IConMeasureUnitService extends IService<ConMeasureUnit>{
    /**
     * 查询计量单位
     *
     * @param sid 计量单位ID
     * @return 计量单位
     */
    public ConMeasureUnit selectConMeasureUnitById(Long sid);

    /**
     * 查询计量单位列表
     *
     * @param conMeasureUnit 计量单位
     * @return 计量单位集合
     */
    public List<ConMeasureUnit> selectConMeasureUnitList(ConMeasureUnit conMeasureUnit);

    /**
     * 新增计量单位
     *
     * @param conMeasureUnit 计量单位
     * @return 结果
     */
    public int insertConMeasureUnit(ConMeasureUnit conMeasureUnit);

    /**
     * 修改计量单位
     *
     * @param conMeasureUnit 计量单位
     * @return 结果
     */
    public int updateConMeasureUnit(ConMeasureUnit conMeasureUnit);

    /**
     * 变更计量单位
     *
     * @param conMeasureUnit 计量单位
     * @return 结果
     */
    public int changeConMeasureUnit(ConMeasureUnit conMeasureUnit);

    /**
     * 批量删除计量单位
     *
     * @param sids 需要删除的计量单位ID
     * @return 结果
     */
    public int deleteConMeasureUnitByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conMeasureUnit
    * @return
    */
    int changeStatus(ConMeasureUnit conMeasureUnit);

    /**
     * 更改确认状态
     * @param conMeasureUnit
     * @return
     */
    int check(ConMeasureUnit conMeasureUnit);

    /**  获取下拉列表 */
    List<ConMeasureUnit> getConMeasureUnitList();
}
