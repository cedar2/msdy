package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PrjTask;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 任务节点Service接口
 *
 * @author chenkw
 * @date 2022-12-07
 */
public interface IPrjTaskService extends IService<PrjTask> {
    /**
     * 查询任务节点
     *
     * @param taskSid 任务节点ID
     * @return 任务节点
     */
    public PrjTask selectPrjTaskById(Long taskSid);

    /**
     * 复制任务节点
     *
     * @param taskSid 任务节点ID
     * @return 任务节点
     */
    public PrjTask copyPrjTaskById(Long taskSid);

    /**
     * 查询任务节点列表
     *
     * @param prjTask 任务节点
     * @return 任务节点集合
     */
    public List<PrjTask> selectPrjTaskList(PrjTask prjTask);

    /**
     * 新增任务节点
     *
     * @param prjTask 任务节点
     * @return 结果
     */
    public int insertPrjTask(PrjTask prjTask);

    /**
     * 修改任务节点
     *
     * @param prjTask 任务节点
     * @return 结果
     */
    public int updatePrjTask(PrjTask prjTask);

    /**
     * 变更任务节点
     *
     * @param prjTask 任务节点
     * @return 结果
     */
    public int changePrjTask(PrjTask prjTask);

    /**
     * 批量删除任务节点
     *
     * @param taskSids 需要删除的任务节点ID
     * @return 结果
     */
    public int deletePrjTaskByIds(List<Long> taskSids);

    /**
     * 启用/停用
     *
     * @param prjTask
     * @return
     */
    int changeStatus(PrjTask prjTask);

    /**
     * 更改确认状态
     *
     * @param prjTask
     * @return
     */
    int check(PrjTask prjTask);

    /**
     * 导入
     * @param file
     * @return
     */
    EmsResultEntity importData(MultipartFile file);
}
