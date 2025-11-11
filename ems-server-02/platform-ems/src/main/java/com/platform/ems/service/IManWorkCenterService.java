package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.ManManufactureOrderProcess;
import com.platform.ems.domain.ManWorkCenter;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.ManWorkCenterActionRequest;
import com.platform.ems.domain.dto.request.ManWorkCenterReportRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 工作中心/班组Service接口
 *
 * @author linhongwei
 * @date 2021-03-26
 */
public interface IManWorkCenterService extends IService<ManWorkCenter> {
    /**
     * 查询工作中心/班组
     *
     * @param workCenterSid 工作中心/班组ID
     * @return 工作中心/班组
     */
    public ManWorkCenter selectManWorkCenterById(Long workCenterSid);

    /**
     * 查询工作中心/班组的编码和名称
     *
     * @param workCenterSid 工作中心/班组ID
     * @return 工作中心/班组
     */
    ManWorkCenter selectCodeNameById(Long workCenterSid);

    /**
     * 查询工作中心/班组列表
     *
     * @param manWorkCenter 工作中心/班组
     * @return 工作中心/班组集合
     */
    public List<ManWorkCenter> selectManWorkCenterList(ManWorkCenter manWorkCenter);


    /**
     * 查询工作中心/班组忙闲报表
     *
     * @param reportRequest
     * @return
     */
    public List<ManManufactureOrderProcess> selectManWorkCenterReportList(ManWorkCenterReportRequest reportRequest);

    /**
     * 新增工作中心/班组
     *
     * @param manWorkCenter 工作中心/班组
     * @return 结果
     */
    public int insertManWorkCenter(ManWorkCenter manWorkCenter);

    /**
     * 修改工作中心/班组
     *
     * @param manWorkCenter 工作中心/班组
     * @return 结果
     */
    public int updateManWorkCenter(ManWorkCenter manWorkCenter);

    /**
     * 变更工作中心/班组
     *
     * @param manWorkCenter 工作中心/班组
     * @return
     */
    public int change(ManWorkCenter manWorkCenter);

    /**
     * 批量删除工作中心/班组
     *
     * @param workCenterSids 需要删除的工作中心/班组ID
     * @return 结果
     */

    public int deleteManWorkCenterByIds(List<Long> workCenterSids);

    /**
     * 批量确认工作中心/班组
     *
     * @param
     * @return 结果
     */

    public int confirm(ManWorkCenterActionRequest action);

    /**
     * 批量启用/停用 工作中心/班组
     *
     * @param
     * @return 结果
     */
    public int status(ManWorkCenterActionRequest action);

    public List<ManWorkCenter> getList();

    List<ManWorkCenter> getWorkCenterList(ManWorkCenter manWorkCenter);

    /**
     * 获取工厂+部门下启用&确认班组
     */
    List<ManWorkCenter> getCoDeptList(ManWorkCenter manWorkCenter);
    /**
     * 班组 导入
     */
    public EmsResultEntity importDataPur(MultipartFile file);
}


