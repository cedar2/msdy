package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeMaterialRequisition;

/**
 * 业务类型_领退料单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeMaterialRequisitionMapper  extends BaseMapper<ConBuTypeMaterialRequisition> {


    ConBuTypeMaterialRequisition selectConBuTypeMaterialRequisitionById(Long sid);

    List<ConBuTypeMaterialRequisition> selectConBuTypeMaterialRequisitionList(ConBuTypeMaterialRequisition conBuTypeMaterialRequisition);
    List<ConBuTypeMaterialRequisition> getList();
    /**
     * 添加多个
     * @param list List ConBuTypeMaterialRequisition
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeMaterialRequisition> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeMaterialRequisition
    * @return int
    */
    int updateAllById(ConBuTypeMaterialRequisition entity);

    /**
     * 更新多个
     * @param list List ConBuTypeMaterialRequisition
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeMaterialRequisition> list);


}
