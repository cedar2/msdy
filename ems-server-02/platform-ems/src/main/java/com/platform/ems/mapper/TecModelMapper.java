package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecModel;
import com.platform.ems.domain.TecModelPosInfor;
import com.platform.ems.domain.TecModelPosInforDown;
import com.platform.ems.domain.dto.response.ModelSystemListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版型档案Mapper接口
 *
 * @author olive
 * @date 2021-01-30
 */
public interface TecModelMapper extends BaseMapper<TecModel> {
    /**
     * 查询版型档案
     *
     * @param modelSid 版型档案ID
     * @return 版型档案
     */
    public TecModel selectTecModelById(Long modelSid);

    /**
     * 查询版型档案列表
     *
     * @param tecModel 版型档案
     * @return 版型档案集合
     */
    public List<TecModel> selectTecModelList(TecModel tecModel);

    List<ModelSystemListResponse> getList(TecModel tecModel);

    List<TecModelPosInfor> getDetail(Long modelSid);

    List<TecModelPosInforDown> getDownDetail(Long modelSid);

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
     * @param tecModel 版型档案
     * @return 结果
     */
    public int updateTecModel(TecModel tecModel);

    /**
     * 删除版型档案
     *
     * @param clientId 版型档案ID
     * @return 结果
     */
    public int deleteTecModelById(Long clientId);

    /**
     * 批量删除版型档案
     *
     * @param modelSidList 需要删除的数据ID
     * @return 结果
     */
    public int deleteTecModelByIds(@Param("modelSidList") Long[] modelSidList);

    String getHandleStatus(@Param("sId") Long sId);

    String putHandleStatus(@Param("sId") Long sId, @Param("handleStatus") String handleStatus);

    String getStatus(@Param("sId") Long sId);

    String putStatus(@Param("sId") Long sId, @Param("status") String status);

    int countByDomain(TecModel params);

    int checkNameUnique(String modelName);

    int checkCodeUnique(String modelCode);


    /**
     * 添加多个
     * @param list List TecModel
     * @return int
     */
    int inserts(@Param("list") List<TecModel> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity TecModel
     * @return int
     */
    int updateAllById(TecModel entity);

    /**
     * 更新多个
     * @param list List TecModel
     * @return int
     */
    int updatesAllById(@Param("list") List<TecModel> list);
}
