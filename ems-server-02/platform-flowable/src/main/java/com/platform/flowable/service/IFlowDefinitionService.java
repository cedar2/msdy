package com.platform.flowable.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.common.core.domain.AjaxResult;
import com.platform.system.domain.dto.FlowProcDefDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author c
 */
public interface IFlowDefinitionService {

    boolean exist(String processDefinitionKey);


    /**
     * 流程定义列表
     *
     * @param map pageNum当前页码
     * @param map pageSize每页条数
     * @return 流程定义分页列表数据
     */
    Page<FlowProcDefDto> list(Map<String, String> map);

    /**
     * 导入流程文件
     *
     * @param name
     * @param category
     * @param in
     * @return
     */
    AjaxResult importFile(String name, String category, InputStream in);

    /**
     * 读取xml
     * @param deployId
     * @return
     */
    AjaxResult readXml(String deployId) throws IOException;

    /**
     * 激活或挂起流程定义
     *
     * @param state    状态
     * @param deployId 流程部署ID
     */
    void updateState(Integer state, String deployId);


    /**
     * 删除流程定义
     *
     * @param deployId 流程部署ID act_ge_bytearray 表中 deployment_id值
     */
    void delete(String deployId);


    /**
     * 读取图片文件
     * @param deployId
     * @return
     */
    InputStream readImage(String deployId);

    public void getProcessDefitionByKey(String defKey,String businessKey);

    /**
     * 获取流程所有节点信息
     * @param flowTaskVo
     */
    public List<Map<String,String>> getAllUserTask(String definitionId);
}
