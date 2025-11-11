package com.platform.ems.plug.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConManufactureMilestoneList;

/**
 * 生产里程碑清单Service接口
 *
 * @author platform
 * @date 2024-03-14
 */
public interface IConManufactureMilestoneListService extends IService<ConManufactureMilestoneList> {

    /**
     * 查询生产里程碑清单
     *
     * @param manufactureMilestoneListSid 生产里程碑清单ID
     * @return 生产里程碑清单
     */
    public ConManufactureMilestoneList selectConManufactureMilestoneListById(Long manufactureMilestoneListSid);

    /**
     * 查询生产里程碑清单列表
     *
     * @param conManufactureMilestoneList 生产里程碑清单
     * @return 生产里程碑清单集合
     */
    public List<ConManufactureMilestoneList> selectConManufactureMilestoneListList(ConManufactureMilestoneList conManufactureMilestoneList);

    /**
     * 新增生产里程碑清单
     *
     * @param conManufactureMilestoneList 生产里程碑清单
     * @return 结果
     */
    public int insertConManufactureMilestoneList(ConManufactureMilestoneList conManufactureMilestoneList);

    /**
     * 修改生产里程碑清单
     *
     * @param conManufactureMilestoneList 生产里程碑清单
     * @return 结果
     */
    public int updateConManufactureMilestoneList(ConManufactureMilestoneList conManufactureMilestoneList);

    /**
     * 变更生产里程碑清单
     *
     * @param conManufactureMilestoneList 生产里程碑清单
     * @return 结果
     */
    public int changeConManufactureMilestoneList(ConManufactureMilestoneList conManufactureMilestoneList);

    /**
     * 批量删除生产里程碑清单
     *
     * @param manufactureMilestoneListSids 需要删除的生产里程碑清单ID
     * @return 结果
     */
    public int deleteConManufactureMilestoneListByIds(List<Long> manufactureMilestoneListSids);

    /**
     * 更改确认状态
     *
     * @param conManufactureMilestoneList 请求参数
     * @return
     */
    int check(ConManufactureMilestoneList conManufactureMilestoneList);

}
