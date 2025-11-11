package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DevDesignDrawFormAttach;

/**
 * 图稿批复单-附件Service接口
 * 
 * @author qhq
 * @date 2021-11-05
 */
public interface IDevDesignDrawFormAttachService extends IService<DevDesignDrawFormAttach>{
    /**
     * 查询图稿批复单-附件
     * 
     * @param attachmentSid 图稿批复单-附件ID
     * @return 图稿批复单-附件
     */
    public DevDesignDrawFormAttach selectDevDesignDrawFormAttachById (Long attachmentSid);

    /**
     * 查询图稿批复单-附件列表
     * 
     * @param devDesignDrawFormAttach 图稿批复单-附件
     * @return 图稿批复单-附件集合
     */
    public List<DevDesignDrawFormAttach> selectDevDesignDrawFormAttachList (DevDesignDrawFormAttach devDesignDrawFormAttach);

    /**
     * 新增图稿批复单-附件
     * 
     * @param devDesignDrawFormAttach 图稿批复单-附件
     * @return 结果
     */
    public int insertDevDesignDrawFormAttach (DevDesignDrawFormAttach devDesignDrawFormAttach);

    /**
     * 修改图稿批复单-附件
     * 
     * @param devDesignDrawFormAttach 图稿批复单-附件
     * @return 结果
     */
    public int updateDevDesignDrawFormAttach (DevDesignDrawFormAttach devDesignDrawFormAttach);

    /**
     * 变更图稿批复单-附件
     *
     * @param devDesignDrawFormAttach 图稿批复单-附件
     * @return 结果
     */
    public int changeDevDesignDrawFormAttach (DevDesignDrawFormAttach devDesignDrawFormAttach);

    /**
     * 批量删除图稿批复单-附件
     * 
     * @param attachmentSids 需要删除的图稿批复单-附件ID
     * @return 结果
     */
    public int deleteDevDesignDrawFormAttachByIds (List<Long> attachmentSids);

}
