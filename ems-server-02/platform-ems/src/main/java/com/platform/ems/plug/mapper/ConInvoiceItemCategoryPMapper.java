package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConInvoiceItemCategoryP;

/**
 * 类别_采购发票行项目Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConInvoiceItemCategoryPMapper  extends BaseMapper<ConInvoiceItemCategoryP> {


    ConInvoiceItemCategoryP selectConInvoiceItemCategoryPById(Long sid);

    List<ConInvoiceItemCategoryP> selectConInvoiceItemCategoryPList(ConInvoiceItemCategoryP conInvoiceItemCategoryP);

    /**
     * 添加多个
     * @param list List ConInvoiceItemCategoryP
     * @return int
     */
    int inserts(@Param("list") List<ConInvoiceItemCategoryP> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConInvoiceItemCategoryP
    * @return int
    */
    int updateAllById(ConInvoiceItemCategoryP entity);

    /**
     * 更新多个
     * @param list List ConInvoiceItemCategoryP
     * @return int
     */
    int updatesAllById(@Param("list") List<ConInvoiceItemCategoryP> list);


}
