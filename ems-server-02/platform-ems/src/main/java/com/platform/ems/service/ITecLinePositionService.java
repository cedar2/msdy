package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecLinePosition;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 线部位档案Service接口
 *
 * @author hjj
 * @date 2021-08-19
 */
public interface ITecLinePositionService extends IService<TecLinePosition> {
    /**
     * 查询线部位档案
     *
     * @param linePositionSid 线部位档案ID
     * @return 线部位档案
     */
    public TecLinePosition selectTecLinePositionById(Long linePositionSid);

    /**
     * 查询线部位档案列表
     *
     * @param tecLinePosition 线部位档案
     * @return 线部位档案集合
     */
    public List<TecLinePosition> selectTecLinePositionList(TecLinePosition tecLinePosition);

    /**
     * 新增线部位档案
     *
     * @param tecLinePosition 线部位档案
     * @return 结果
     */
    public int insertTecLinePosition(TecLinePosition tecLinePosition);

    /**
     * 修改线部位档案
     *
     * @param tecLinePosition 线部位档案
     * @return 结果
     */
    public int updateTecLinePosition(TecLinePosition tecLinePosition);

    /**
     * 变更线部位档案
     *
     * @param tecLinePosition 线部位档案
     * @return 结果
     */
    public int changeTecLinePosition(TecLinePosition tecLinePosition);

    /**
     * 批量删除线部位档案
     *
     * @param linePositionSids 需要删除的线部位档案ID
     * @return 结果
     */
    public int deleteTecLinePositionByIds(List<Long> linePositionSids);

    /**
     * 启用/停用
     *
     * @param tecLinePosition
     * @return
     */
    int changeStatus(TecLinePosition tecLinePosition);

    /**
     * 更改确认状态
     *
     * @param tecLinePosition
     * @return
     */
    int check(TecLinePosition tecLinePosition);

    /**
     * 导入线部位档案
     */
    int importData(MultipartFile file);
}
