package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConSampleRequisitionUsage;

/**
 * 样品出库用途Mapper接口
 * 
 * @author yangqz
 * @date 2022-04-24
 */
public interface ConSampleRequisitionUsageMapper  extends BaseMapper<ConSampleRequisitionUsage> {


    ConSampleRequisitionUsage selectConSampleRequisitionUsageById(Long sid);

    List<ConSampleRequisitionUsage> selectConSampleRequisitionUsageList(ConSampleRequisitionUsage conSampleRequisitionUsage);

    List<ConSampleRequisitionUsage> getList(ConSampleRequisitionUsage conSampleRequisitionUsage);

    /**
     * 添加多个
     * @param list List ConSampleRequisitionUsage
     * @return int
     */
    int inserts(@Param("list") List<ConSampleRequisitionUsage> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConSampleRequisitionUsage
    * @return int
    */
    int updateAllById(ConSampleRequisitionUsage entity);

    /**
     * 更新多个
     * @param list List ConSampleRequisitionUsage
     * @return int
     */
    int updatesAllById(@Param("list") List<ConSampleRequisitionUsage> list);


}
