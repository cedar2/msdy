package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConInvoiceDimensionSale;

/**
 * 发票维度_销售Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConInvoiceDimensionSaleMapper  extends BaseMapper<ConInvoiceDimensionSale> {


    ConInvoiceDimensionSale selectConInvoiceDimensionSaleById(Long sid);

    List<ConInvoiceDimensionSale> selectConInvoiceDimensionSaleList(ConInvoiceDimensionSale conInvoiceDimensionSale);

    /**
     * 添加多个
     * @param list List ConInvoiceDimensionSale
     * @return int
     */
    int inserts(@Param("list") List<ConInvoiceDimensionSale> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConInvoiceDimensionSale
    * @return int
    */
    int updateAllById(ConInvoiceDimensionSale entity);

    /**
     * 更新多个
     * @param list List ConInvoiceDimensionSale
     * @return int
     */
    int updatesAllById(@Param("list") List<ConInvoiceDimensionSale> list);


}
