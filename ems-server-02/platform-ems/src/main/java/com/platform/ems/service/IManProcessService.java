package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.ManProcess;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.ManProcessActionRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 工序Service接口
 *
 * @author linhongwei
 * @date 2021-03-26
 */
public interface IManProcessService extends IService<ManProcess>{
    /**
     * 查询工序
     *
     * @param processSid 工序ID
     * @return 工序
     */
    public ManProcess selectManProcessById(Long processSid);

    /**
     * 查询工序列表
     *
     * @param manProcess 工序
     * @return 工序集合
     */
    public List<ManProcess> selectManProcessList(ManProcess manProcess);

    /**
     * 新增工序
     *
     * @param manProcess 工序
     * @return 结果
     */
    public int insertManProcess(ManProcess manProcess);

    /**
     * 修改工序
     *
     * @param manProcess 工序
     * @return 结果
     */
    public int updateManProcess(ManProcess manProcess);

    /**
     * 批量删除工序
     *
     * @param processSids 需要删除的工序ID
     * @return 结果
     */
    public int deleteManProcessByIds(List<Long> processSids);
    /**
     * 批量确认工序
     *
     * @param manProcessActionRequest
     * @return 结果
     */
    public int confirm(ManProcessActionRequest manProcessActionRequest);
    /**
     * 变更工序
     *
     * @param manProcess
     * @return 结果
     */
    public int change(ManProcess manProcess);
    /**
     * 启用/停用 工序
     *
     * @param manProcessActionRequest
     * @return 结果
     */
    public int status(ManProcessActionRequest manProcessActionRequest);

    /**
     * 工序档案列表
     *
     * @param
     * @return 结果
     */
    public List<ManProcess>  getList(ManProcess manProcess);
    /**
     * 工序 导入
     */
    public EmsResultEntity importDataPur(MultipartFile file);
}
