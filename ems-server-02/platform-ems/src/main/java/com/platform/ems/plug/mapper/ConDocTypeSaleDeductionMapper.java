package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeSaleDeduction;

/**
 * 单据类型_销售扣款单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeSaleDeductionMapper  extends BaseMapper<ConDocTypeSaleDeduction> {


    ConDocTypeSaleDeduction selectConDocTypeSaleDeductionById(Long sid);

    List<ConDocTypeSaleDeduction> selectConDocTypeSaleDeductionList(ConDocTypeSaleDeduction conDocTypeSaleDeduction);

    /**
     * 添加多个
     * @param list List ConDocTypeSaleDeduction
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeSaleDeduction> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeSaleDeduction
    * @return int
    */
    int updateAllById(ConDocTypeSaleDeduction entity);

    /**
     * 更新多个
     * @param list List ConDocTypeSaleDeduction
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeSaleDeduction> list);


}
