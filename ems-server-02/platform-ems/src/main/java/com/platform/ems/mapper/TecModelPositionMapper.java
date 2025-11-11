package com.platform.ems.mapper;

import java.util.List;

import com.platform.ems.domain.TecModelPosition;
import org.apache.ibatis.annotations.Param;

/**
 * 版型部位档案Mapper接口
 *
 * @author ChenPinzhen
 * @date 2021-01-25
 */
public interface TecModelPositionMapper {
    /**selectTecModelPositionById
     * 查询版型部位
     *
     * @param modelPositionSid 版型部位SID
     * @return 版型部位
     */
    public TecModelPosition selectTecModelPositionById(Long modelPositionSid);

    /**
     * 查询版型部位
     *
     * @param code 版型部位code
     * @return 版型部位
     */
    public List<TecModelPosition> selectTecModelPositionByCode(String code,String clientId);


    /**
     * 查询版型部位
     *
     * @param tecModelPosition 可查询版型部位信息
     * @return 版型部位
     */
    public List<TecModelPosition> selectTecModelPositionList(TecModelPosition tecModelPosition);


    /**
     * 新增版型部位
     *
     * @param tecModelPosition 版型部位
     * @return 结果
     */
    public int insertTecModelPosition(TecModelPosition tecModelPosition);

    /**
     * 添加多个
     * @param list List TecModelPosition
     * @return int
     */
    int inserts(@Param("list") List<TecModelPosition> list);

    /**
     * 修改版型部位
     *
     * @param tecModelPosition 版型部位
     * @return 结果
     */
    public int updateTecModelPosition(TecModelPosition tecModelPosition);


    /**
     * 批量删除版型部位
     *
     * @param modelPositionSids 需要删除的数据ID
     * @return 结果
     */
    public int deleteTecModelPositionByIds(String[] modelPositionSids);

    /**
     * 验证版型部位编码是否已存在
     */
    int checkCodeUnique(String modelPositionCode);

    /**
     * 验证版型部位名称是否已存在
     */
    int checkNameUnique(String modelPositionName);

    /**
     * 统计保存状态数
     */
    int countByDomain(TecModelPosition params1);

    /**
     * 批量确认或启用停用
     */
    int confirm(TecModelPosition tecModelPosition);

    /**
     * 版型部位下拉框列表
     */
    List<TecModelPosition> getModelPositionList();
}
