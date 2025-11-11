package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConInvoiceItemCategoryS;

/**
 * 类别_销售发票行项目Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConInvoiceItemCategorySMapper  extends BaseMapper<ConInvoiceItemCategoryS> {


    ConInvoiceItemCategoryS selectConInvoiceItemCategorySById(Long sid);

    List<ConInvoiceItemCategoryS> selectConInvoiceItemCategorySList(ConInvoiceItemCategoryS conInvoiceItemCategoryS);

    /**
     * 添加多个
     * @param list List ConInvoiceItemCategoryS
     * @return int
     */
    int inserts(@Param("list") List<ConInvoiceItemCategoryS> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConInvoiceItemCategoryS
    * @return int
    */
    int updateAllById(ConInvoiceItemCategoryS entity);

    /**
     * 更新多个
     * @param list List ConInvoiceItemCategoryS
     * @return int
     */
    int updatesAllById(@Param("list") List<ConInvoiceItemCategoryS> list);


}
