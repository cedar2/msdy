package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasStorehouseAttach;

/**
 * 仓库档案-附件Service接口
 *
 * @author chenkw
 * @date 2021-09-15
 */
public interface IBasStorehouseAttachService extends IService<BasStorehouseAttach>{
    /**
     * 查询仓库档案-附件
     *
     * @param attachmentSid 仓库档案-附件ID
     * @return 仓库档案-附件
     */
    public BasStorehouseAttach selectBasStorehouseAttachById(Long attachmentSid);

    /**
     * 查询仓库档案-附件列表
     *
     * @param basStorehouseAttach 仓库档案-附件
     * @return 仓库档案-附件集合
     */
    public List<BasStorehouseAttach> selectBasStorehouseAttachList(BasStorehouseAttach basStorehouseAttach);

    /**
     * 新增仓库档案-附件
     *
     * @param basStorehouseAttach 仓库档案-附件
     * @return 结果
     */
    public int insertBasStorehouseAttach(BasStorehouseAttach basStorehouseAttach);

    /**
     * 修改仓库档案-附件
     *
     * @param basStorehouseAttach 仓库档案-附件
     * @return 结果
     */
    public int updateBasStorehouseAttach(BasStorehouseAttach basStorehouseAttach);

    /**
     * 变更仓库档案-附件
     *
     * @param basStorehouseAttach 仓库档案-附件
     * @return 结果
     */
    public int changeBasStorehouseAttach(BasStorehouseAttach basStorehouseAttach);

    /**
     * 批量删除仓库档案-附件
     *
     * @param attachmentSids 需要删除的仓库档案-附件ID
     * @return 结果
     */
    public int deleteBasStorehouseAttachByIds(List<Long>  attachmentSids);

}