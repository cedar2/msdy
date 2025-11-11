package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ConTaskTemplateCompare;

/**
 * 任务模版对照关系ControllerService接口
 *
 * @author platform
 * @date 2023-11-03
 */
public interface IConTaskTemplateCompareService extends IService<ConTaskTemplateCompare>{

    /**
     * 查询任务模版对照关系Controller
     *
     * @param taskTemplateCompareSid 任务模版对照关系ControllerID
     * @return 任务模版对照关系Controller
     */
    public ConTaskTemplateCompare selectConTaskTemplateCompareById(Long taskTemplateCompareSid);

    /**
     * 查询任务模版对照关系Controller列表
     *
     * @param conTaskTemplateCompare 任务模版对照关系Controller
     * @return 任务模版对照关系Controller集合
     */
    public List<ConTaskTemplateCompare> selectConTaskTemplateCompareList(ConTaskTemplateCompare conTaskTemplateCompare);

    /**
     * 新增任务模版对照关系Controller
     *
     * @param conTaskTemplateCompare 任务模版对照关系Controller
     * @return 结果
     */
    public int insertConTaskTemplateCompare(ConTaskTemplateCompare conTaskTemplateCompare);

    /**
     * 修改任务模版对照关系Controller
     *
     * @param conTaskTemplateCompare 任务模版对照关系Controller
     * @return 结果
     */
    public int updateConTaskTemplateCompare(ConTaskTemplateCompare conTaskTemplateCompare);

    /**
     * 变更任务模版对照关系Controller
     *
     * @param conTaskTemplateCompare 任务模版对照关系Controller
     * @return 结果
     */
    public int changeConTaskTemplateCompare(ConTaskTemplateCompare conTaskTemplateCompare);

    /**
     * 批量删除任务模版对照关系Controller
     *
     * @param taskTemplateCompareSids 需要删除的任务模版对照关系ControllerID
     * @return 结果
     */
    public int deleteConTaskTemplateCompareByIds(List<Long>  taskTemplateCompareSids);

    /**
     * 启用/停用
     * @param conTaskTemplateCompare 请求参数
     * @return
     */
    int changeStatus(ConTaskTemplateCompare conTaskTemplateCompare);

    /**
     * 更改确认状态
     * @param conTaskTemplateCompare 请求参数
     * @return
     */
    int check(ConTaskTemplateCompare conTaskTemplateCompare);

}

