package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConInvoiceDimensionPurcahse;

/**
 * 发票维度_采购Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConInvoiceDimensionPurcahseMapper  extends BaseMapper<ConInvoiceDimensionPurcahse> {


    ConInvoiceDimensionPurcahse selectConInvoiceDimensionPurcahseById(Long sid);

    List<ConInvoiceDimensionPurcahse> selectConInvoiceDimensionPurcahseList(ConInvoiceDimensionPurcahse conInvoiceDimensionPurcahse);

    /**
     * 添加多个
     * @param list List ConInvoiceDimensionPurcahse
     * @return int
     */
    int inserts(@Param("list") List<ConInvoiceDimensionPurcahse> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConInvoiceDimensionPurcahse
    * @return int
    */
    int updateAllById(ConInvoiceDimensionPurcahse entity);

    /**
     * 更新多个
     * @param list List ConInvoiceDimensionPurcahse
     * @return int
     */
    int updatesAllById(@Param("list") List<ConInvoiceDimensionPurcahse> list);


}
