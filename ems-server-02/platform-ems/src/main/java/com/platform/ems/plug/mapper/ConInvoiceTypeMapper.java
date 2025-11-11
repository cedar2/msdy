package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConInvoiceType;

/**
 * 发票类型Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConInvoiceTypeMapper extends BaseMapper<ConInvoiceType> {

    ConInvoiceType selectConInvoiceTypeById(Long sid);

    List<ConInvoiceType> selectConInvoiceTypeList(ConInvoiceType conInvoiceType);

    /**
     * 添加多个
     * @param list List ConInvoiceType
     * @return int
     */
    int inserts(@Param("list") List<ConInvoiceType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConInvoiceType
    * @return int
    */
    int updateAllById(ConInvoiceType entity);

    /** 获取下拉列表 */
    List<ConInvoiceType> getConInvoiceTypeList(ConInvoiceType conInvoiceType);
}
