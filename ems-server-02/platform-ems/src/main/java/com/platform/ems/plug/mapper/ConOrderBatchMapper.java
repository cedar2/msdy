package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConOrderBatch;

/**
 * 下单批次Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConOrderBatchMapper  extends BaseMapper<ConOrderBatch> {


    ConOrderBatch selectConOrderBatchById(Long sid);

    List<ConOrderBatch> selectConOrderBatchList(ConOrderBatch conOrderBatch);

    /**
     * 添加多个
     * @param list List ConOrderBatch
     * @return int
     */
    int inserts(@Param("list") List<ConOrderBatch> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConOrderBatch
    * @return int
    */
    int updateAllById(ConOrderBatch entity);

    /**
     * 更新多个
     * @param list List ConOrderBatch
     * @return int
     */
    int updatesAllById(@Param("list") List<ConOrderBatch> list);


    /**
     * 下单批次下拉框
     */
    List<ConOrderBatch> getList();
}
