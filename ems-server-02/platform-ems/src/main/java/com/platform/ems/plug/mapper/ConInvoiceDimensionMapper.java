package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConAccountCategory;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConInvoiceDimension;

/**
 * 发票维度Mapper接口
 *
 * @author chenkw
 * @date 2021-08-11
 */
public interface ConInvoiceDimensionMapper  extends BaseMapper<ConInvoiceDimension> {


    ConInvoiceDimension selectConInvoiceDimensionById(Long sid);

    List<ConInvoiceDimension> selectConInvoiceDimensionList(ConInvoiceDimension conInvoiceDimension);

    /**
     * 添加多个
     * @param list List ConInvoiceDimension
     * @return int
     */
    int inserts(@Param("list") List<ConInvoiceDimension> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity ConInvoiceDimension
     * @return int
     */
    int updateAllById(ConInvoiceDimension entity);

    /**
     * 更新多个
     * @param list List ConInvoiceDimension
     * @return int
     */
    int updatesAllById(@Param("list") List<ConInvoiceDimension> list);

    /**
     * 开票维度下拉框列表
     */
    List<ConInvoiceDimension> getInvoiceDimensionList();

}
