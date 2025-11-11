package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConDiscountType;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConInvoiceCategory;

/**
 * 发票类别Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConInvoiceCategoryMapper  extends BaseMapper<ConInvoiceCategory> {


    ConInvoiceCategory selectConInvoiceCategoryById(Long sid);

    List<ConInvoiceCategory> selectConInvoiceCategoryList(ConInvoiceCategory conInvoiceCategory);

    /**
     * 添加多个
     * @param list List ConInvoiceCategory
     * @return int
     */
    int inserts(@Param("list") List<ConInvoiceCategory> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConInvoiceCategory
    * @return int
    */
    int updateAllById(ConInvoiceCategory entity);

    /**
     * 更新多个
     * @param list List ConInvoiceCategory
     * @return int
     */
    int updatesAllById(@Param("list") List<ConInvoiceCategory> list);


    /** 获取下拉列表 */
    List<ConInvoiceCategory> getConInvoiceCategoryList();

}
