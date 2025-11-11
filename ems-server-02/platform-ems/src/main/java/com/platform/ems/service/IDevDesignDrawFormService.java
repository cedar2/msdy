package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.DevDesignDrawForm;

import java.util.List;

/**
 * 图稿批复单Service接口
 *
 * @author qhq
 * @date 2021-11-05
 */
public interface IDevDesignDrawFormService extends IService<DevDesignDrawForm> {
    /**
     * 查询图稿批复单
     *
     * @param designDrawFormSid 图稿批复单ID
     * @return 图稿批复单
     */
    public DevDesignDrawForm selectDevDesignDrawFormById(Long designDrawFormSid);

    /**
     * 查询图稿批复单列表
     *
     * @param devDesignDrawForm 图稿批复单
     * @return 图稿批复单集合
     */
    public List<DevDesignDrawForm> selectDevDesignDrawFormList(DevDesignDrawForm devDesignDrawForm);

    /**
     * 新增图稿批复单
     *
     * @param devDesignDrawForm 图稿批复单
     * @return 结果
     */
    public AjaxResult insertDevDesignDrawForm(DevDesignDrawForm devDesignDrawForm);

    /**
     * 修改图稿批复单
     *
     * @param devDesignDrawForm 图稿批复单
     * @return 结果
     */
    public int updateDevDesignDrawForm(DevDesignDrawForm devDesignDrawForm);

    /**
     * 变更图稿批复单
     *
     * @param devDesignDrawForm 图稿批复单
     * @return 结果
     */
    public int changeDevDesignDrawForm(DevDesignDrawForm devDesignDrawForm);

    /**
     * 批量删除图稿批复单
     *
     * @param designDrawFormSids 需要删除的图稿批复单ID
     * @return 结果
     */
    public int deleteDevDesignDrawFormByIds(List<Long> designDrawFormSids);

    /**
     * 更改确认状态
     *
     * @param devDesignDrawForm
     * @return
     */
    int check(DevDesignDrawForm devDesignDrawForm);

    public boolean attachmentIsExist(Long sid);

    public int updateStatus(DevDesignDrawForm devDesignDrawForm);

    /**
     * 是否已创建图稿批复
     */
    DevDesignDrawForm verify(Long productSid);
}
