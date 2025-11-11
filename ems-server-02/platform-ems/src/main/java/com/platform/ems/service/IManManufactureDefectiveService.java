package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManManufactureDefective;

import java.util.List;

/**
 * 生产次品台账Service接口
 *
 * @author c
 * @date 2022-03-02
 */
public interface IManManufactureDefectiveService extends IService<ManManufactureDefective> {
    /**
     * 查询生产次品台账
     *
     * @param manufactureDefectiveSid 生产次品台账ID
     * @return 生产次品台账
     */
    public ManManufactureDefective selectManManufactureDefectiveById(Long manufactureDefectiveSid);

    /**
     * 查询生产次品台账列表
     *
     * @param manManufactureDefective 生产次品台账
     * @return 生产次品台账集合
     */
    public List<ManManufactureDefective> selectManManufactureDefectiveList(ManManufactureDefective manManufactureDefective);

    /**
     * 新增生产次品台账
     *
     * @param manManufactureDefective 生产次品台账
     * @return 结果
     */
    public int insertManManufactureDefective(ManManufactureDefective manManufactureDefective);

    /**
     * 修改生产次品台账
     *
     * @param manManufactureDefective 生产次品台账
     * @return 结果
     */
    public int updateManManufactureDefective(ManManufactureDefective manManufactureDefective);

    /**
     * 变更生产次品台账
     *
     * @param manManufactureDefective 生产次品台账
     * @return 结果
     */
    public int changeManManufactureDefective(ManManufactureDefective manManufactureDefective);

    /**
     * 批量删除生产次品台账
     *
     * @param manufactureDefectiveSids 需要删除的生产次品台账ID
     * @return 结果
     */
    public int deleteManManufactureDefectiveByIds(List<Long> manufactureDefectiveSids);

    /**
     * 更改确认状态
     *
     * @param manManufactureDefective
     * @return
     */
    int check(ManManufactureDefective manManufactureDefective);

}
