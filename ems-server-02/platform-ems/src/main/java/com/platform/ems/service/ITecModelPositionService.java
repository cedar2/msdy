package com.platform.ems.service;

import java.util.List;

import com.platform.ems.domain.TecModelPosition;
import org.springframework.web.multipart.MultipartFile;

/**
 * 版型部位档案Service接口
 *
 * @author ChenPinzhen
 * @date 2021-01-25
 */
public interface ITecModelPositionService {
    /**selectTecModelPositionById
     * 查询版型部位
     *
     * @param modelPositionSid 版型部位SID
     * @return 版型部位
     */
    public TecModelPosition selectTecModelPositionById(Long modelPositionSid);

    /**
     * 查询版型部位列表
     *
     * @param tecModelPosition 版型部位
     * @return 版型部位集合
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
     * 修改版型部位
     *
     * @param tecModelPosition 版型部位
     * @return 结果
     */
    public int updateTecModelPosition(TecModelPosition tecModelPosition);

    /**
     * 批量删除版型部位
     *
     * @param modelPositionSids 需要删除的版型部位ID
     * @return 结果
     */
    public int deleteTecModelPositionByIds(String[] modelPositionSids);

    /**
     * 版型部位确认
     *
     * @param tecModelPosition 版型部位确认
     * @return 结果
     */
    int confirm(TecModelPosition tecModelPosition);

    /**
     * 版型部位变更
     *
     * @param tecModelPosition 版型部位变更
     * @return 结果
     */
    int change(TecModelPosition tecModelPosition);

    /**
     * 版型部位启用/停用
     *
     * @param tecModelPosition 版型部位启用/停用
     * @return 结果
     */
    int status(TecModelPosition tecModelPosition);

    /**
     * 版型部位下拉框列表
     * @return 结果
     */
    List<TecModelPosition> getModelPositionList();

    /**
     * 导入版型部位
     */
    int importData(MultipartFile file);
}
