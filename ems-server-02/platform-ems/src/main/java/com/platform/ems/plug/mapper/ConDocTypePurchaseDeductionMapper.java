package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypePurchaseDeduction;

/**
 * 单据类型_采购扣款单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypePurchaseDeductionMapper  extends BaseMapper<ConDocTypePurchaseDeduction> {


    ConDocTypePurchaseDeduction selectConDocTypePurchaseDeductionById(Long sid);

    List<ConDocTypePurchaseDeduction> selectConDocTypePurchaseDeductionList(ConDocTypePurchaseDeduction conDocTypePurchaseDeduction);

    /**
     * 添加多个
     * @param list List ConDocTypePurchaseDeduction
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypePurchaseDeduction> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypePurchaseDeduction
    * @return int
    */
    int updateAllById(ConDocTypePurchaseDeduction entity);

    /**
     * 更新多个
     * @param list List ConDocTypePurchaseDeduction
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypePurchaseDeduction> list);


}
