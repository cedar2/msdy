package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeMaterialRequisition;

/**
 * 单据类型_领退料单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeMaterialRequisitionMapper  extends BaseMapper<ConDocTypeMaterialRequisition> {


    ConDocTypeMaterialRequisition selectConDocTypeMaterialRequisitionById(Long sid);

    List<ConDocTypeMaterialRequisition> selectConDocTypeMaterialRequisitionList(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition);

    List<ConDocTypeMaterialRequisition> getList(ConDocTypeMaterialRequisition conDocTypeMaterialRequisition);

    /**
     * 添加多个
     * @param list List ConDocTypeMaterialRequisition
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeMaterialRequisition> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeMaterialRequisition
    * @return int
    */
    int updateAllById(ConDocTypeMaterialRequisition entity);

    /**
     * 更新多个
     * @param list List ConDocTypeMaterialRequisition
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeMaterialRequisition> list);


}
