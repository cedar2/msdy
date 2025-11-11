package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasPlant;

/**
 * 工厂档案Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-15
 */
public interface BasPlantMapper  extends BaseMapper<BasPlant> {


    BasPlant selectBasPlantById(Long plantSid);

    List<BasPlant> selectBasPlantList(BasPlant basPlant);

    /**
     * 添加多个
     * @param list List BasPlant
     * @return int
     */
    int inserts(@Param("list") List<BasPlant> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasPlant
    * @return int
    */
    int updateAllById(BasPlant entity);

    /**
     * 更新多个
     * @param list List BasPlant
     * @return int
     */
    int updatesAllById(@Param("list") List<BasPlant> list);

    /**
     * 验证工厂名称是否重复
     * @param plantName 工厂名称
     * @return int
     */
    int checkNameUnique(String plantName);

    /**
     * 批量删除工厂档案
     *
     * @param plantSids 需要删除的工厂档案ID
     * @return 结果
     */
    int deleteBasPlantByIds(@Param("array")String[] plantSids);

    /**
     * 验证是否保存状态
     *
     * @param params 工厂档案ID、处理状态
     * @return 结果
     */
    int countByDomain(BasPlant params);

    int insertBasPlant(BasPlant basPlant);

    /**
     * 验证工厂编码是否重复
     * @param plantCode 工厂编码
     * @return int
     */
    int checkCodeUnique(String plantCode);

    /**
     * 批量确认或启用/停用工厂档案
     *
     * @param basPlant 工厂档案IDS、确认状态、启用/停用状态
     * @return 结果
     */
    int confirm(BasPlant basPlant);

    /**
     * 工厂档案下拉框列表
     */
    List<BasPlant> getPlantList(BasPlant basPlant);
}
