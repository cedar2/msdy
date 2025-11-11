package com.platform.ems.service;

import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.BasSku;
import com.platform.ems.domain.TecModel;
import com.platform.ems.domain.dto.response.ModelSystemDetailResponse;
import com.platform.ems.domain.dto.response.ModelSystemListResponse;

import java.util.List;

/**
 * 版型档案Service接口
 *
 * @author olive
 * @date 2021-01-30
 */
public interface ITecModelService {
    /**
     * 查询版型档案
     *
     * @param modelSid 版型档案ID
     * @return 版型档案
     */
    public TecModel selectTecModelById(Long modelSid);

    /**
     * 获取下拉框列表
     * @return
     */
    List<ModelSystemListResponse> getList();

    /**
     * 获取下拉框列表
     * @return
     */
    List<ModelSystemListResponse> getList(TecModel tecModel);

    /**
     * 获取详细尺码表
     * @return
     */
    TecModel getDetail(Long modelSid);
    /**
     * 查询版型档案列表
     *
     * @param tecModel 版型档案
     * @return 版型档案集合
     */
    public List<TecModel> selectTecModelList(TecModel tecModel);

    /**
     * 新增版型档案
     *
     * @param tecModel 版型档案
     * @return 结果
     */
    public int insertTecModel(TecModel tecModel);

    /**
     * 修改版型档案
     *
     * @param request 版型档案
     * @return 结果
     */
    public int updateTecModel(TecModel request);

    public int changeTecModel(TecModel request);

    /**
     * 批量删除版型档案
     *
     * @param tecModel 需要删除的版型档案ID
     * @return 结果
     */
    public int deleteTecModelByIds(TecModel tecModel);

    /**
     * 删除版型档案信息
     *
     * @param clientId 版型档案ID
     * @return 结果
     */
    public int deleteTecModelById(Long clientId);

    String getHandleStatus(Long sId);

    String putHandleStatus(Long sId, String handleStatus);

    String getStatus(Long sId);

    String putStatus(Long sId, String validStatus);

    int checkCodeUnique(String modelCode);

    int checkNameUnique(String modelName);

    int changeStatus(TecModel tecModel);

    int check(TecModel tecModel);

    /**
     * 版型档案确认操作前校验相应附件是否上传
     *
     * @param tecModel 版型档案
     * @return 结果
     */
    AjaxResult checkAttach(TecModel tecModel);
}
