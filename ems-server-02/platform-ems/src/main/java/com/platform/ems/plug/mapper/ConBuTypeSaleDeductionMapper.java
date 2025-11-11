package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeSaleDeduction;

/**
 * 业务类型_销售扣款单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeSaleDeductionMapper  extends BaseMapper<ConBuTypeSaleDeduction> {


    ConBuTypeSaleDeduction selectConBuTypeSaleDeductionById(Long sid);

    List<ConBuTypeSaleDeduction> selectConBuTypeSaleDeductionList(ConBuTypeSaleDeduction conBuTypeSaleDeduction);

    /**
     * 添加多个
     * @param list List ConBuTypeSaleDeduction
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeSaleDeduction> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeSaleDeduction
    * @return int
    */
    int updateAllById(ConBuTypeSaleDeduction entity);

    /**
     * 更新多个
     * @param list List ConBuTypeSaleDeduction
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeSaleDeduction> list);


}
