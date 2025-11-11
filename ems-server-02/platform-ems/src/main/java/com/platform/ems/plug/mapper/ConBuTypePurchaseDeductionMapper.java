package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypePurchaseDeduction;

/**
 * 业务类型_采购扣款单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypePurchaseDeductionMapper  extends BaseMapper<ConBuTypePurchaseDeduction> {


    ConBuTypePurchaseDeduction selectConBuTypePurchaseDeductionById(Long sid);

    List<ConBuTypePurchaseDeduction> selectConBuTypePurchaseDeductionList(ConBuTypePurchaseDeduction conBuTypePurchaseDeduction);

    /**
     * 添加多个
     * @param list List ConBuTypePurchaseDeduction
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypePurchaseDeduction> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypePurchaseDeduction
    * @return int
    */
    int updateAllById(ConBuTypePurchaseDeduction entity);

    /**
     * 更新多个
     * @param list List ConBuTypePurchaseDeduction
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypePurchaseDeduction> list);


}
