package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManProcessRoute;
import com.platform.ems.domain.ManProcessRouteItem;
import com.platform.ems.domain.dto.request.ManProcessRouteActionRequest;

import java.util.List;

/**
 * 工艺路线Service接口
 *
 * @author linhongwei
 * @date 2021-03-26
 */
public interface IManProcessRouteService extends IService<ManProcessRoute> {
    /**
     * 查询工艺路线
     *
     * @param processRouteSid 工艺路线ID
     * @return 工艺路线
     */
    public ManProcessRoute selectManProcessRouteById(Long processRouteSid);

    public  List<ManProcessRouteItem> monthGetManProcess(Long processRouteSid);

    /**
     * 查询工艺路线列表
     *
     * @param manProcessRoute 工艺路线
     * @return 工艺路线集合
     */
    public List<ManProcessRoute> selectManProcessRouteList(ManProcessRoute manProcessRoute);

    /**
     * 新增工艺路线
     *
     * @param manProcessRoute 工艺路线
     * @return 结果
     */
    public int insertManProcessRoute(ManProcessRoute manProcessRoute);

    /**
     * 修改工艺路线
     *
     * @param manProcessRoute 工艺路线
     * @return 结果
     */
    public int updateManProcessRoute(ManProcessRoute manProcessRoute);

    /**
     * 批量删除工艺路线
     *
     * @param processRouteSids 需要删除的工艺路线ID
     * @return 结果
     */
    public int deleteManProcessRouteByIds(List<Long> processRouteSids);

    /**
     * 变更工艺路线
     *
     * @param manProcessRoute 工艺路线
     * @return 结果
     */
    public int change(ManProcessRoute manProcessRoute);

    /**
     * 批量确认工艺路线
     *
     * @param
     * @return 结果
     */
    public int confirm(ManProcessRouteActionRequest action);

    /**
     * 启用/停用 工艺路线
     *
     * @param
     * @return 结果
     */
    public int status(ManProcessRouteActionRequest action);


    /**
     * 款项类别下拉框列表
     */
    List<ManProcessRoute> getManProcessRouteList(ManProcessRoute manProcessRoute);
}
