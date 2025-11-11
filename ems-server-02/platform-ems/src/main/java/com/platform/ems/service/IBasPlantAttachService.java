package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasPlantAttach;

/**
 * 工厂档案-附件Service接口
 *
 * @author chenkw
 * @date 2021-09-15
 */
public interface IBasPlantAttachService extends IService<BasPlantAttach>{
    /**
     * 查询工厂档案-附件
     *
     * @param attachmentSid 工厂档案-附件ID
     * @return 工厂档案-附件
     */
    public BasPlantAttach selectBasPlantAttachById(Long attachmentSid);

    /**
     * 查询工厂档案-附件列表
     *
     * @param basPlantAttach 工厂档案-附件
     * @return 工厂档案-附件集合
     */
    public List<BasPlantAttach> selectBasPlantAttachList(BasPlantAttach basPlantAttach);

    /**
     * 新增工厂档案-附件
     *
     * @param basPlantAttach 工厂档案-附件
     * @return 结果
     */
    public int insertBasPlantAttach(BasPlantAttach basPlantAttach);

    /**
     * 修改工厂档案-附件
     *
     * @param basPlantAttach 工厂档案-附件
     * @return 结果
     */
    public int updateBasPlantAttach(BasPlantAttach basPlantAttach);

    /**
     * 变更工厂档案-附件
     *
     * @param basPlantAttach 工厂档案-附件
     * @return 结果
     */
    public int changeBasPlantAttach(BasPlantAttach basPlantAttach);

    /**
     * 批量删除工厂档案-附件
     *
     * @param attachmentSids 需要删除的工厂档案-附件ID
     * @return 结果
     */
    public int deleteBasPlantAttachByIds(List<Long>  attachmentSids);

}