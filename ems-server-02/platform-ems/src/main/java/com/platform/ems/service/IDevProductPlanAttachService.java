package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DevProductPlanAttach;

/**
 * 品类规划信息-附件Service接口
 * 
 * @author qhq
 * @date 2021-11-08
 */
public interface IDevProductPlanAttachService extends IService<DevProductPlanAttach> {
    /**
     * 查询品类规划信息-附件
     * 
     * @param attachmentSid 品类规划信息-附件ID
     * @return 品类规划信息-附件
     */
    public DevProductPlanAttach selectDevProductPlanAttachById (Long attachmentSid);

    /**
     * 查询品类规划信息-附件列表
     * 
     * @param devProductPlanAttach 品类规划信息-附件
     * @return 品类规划信息-附件集合
     */
    public List<DevProductPlanAttach> selectDevProductPlanAttachList (DevProductPlanAttach devProductPlanAttach);

    /**
     * 新增品类规划信息-附件
     * 
     * @param devProductPlanAttach 品类规划信息-附件
     * @return 结果
     */
    public int insertDevProductPlanAttach (DevProductPlanAttach devProductPlanAttach);

    /**
     * 修改品类规划信息-附件
     * 
     * @param devProductPlanAttach 品类规划信息-附件
     * @return 结果
     */
    public int updateDevProductPlanAttach (DevProductPlanAttach devProductPlanAttach);

    /**
     * 变更品类规划信息-附件
     *
     * @param devProductPlanAttach 品类规划信息-附件
     * @return 结果
     */
    public int changeDevProductPlanAttach (DevProductPlanAttach devProductPlanAttach);

    /**
     * 批量删除品类规划信息-附件
     * 
     * @param attachmentSids 需要删除的品类规划信息-附件ID
     * @return 结果
     */
    public int deleteDevProductPlanAttachByIds (List<Long> attachmentSids);

}
