package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinRecordAdvancePaymentItem;

/**
 * 供应商业务台账-明细-预付Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-29
 */
public interface FinRecordAdvancePaymentItemMapper  extends BaseMapper<FinRecordAdvancePaymentItem> {


    FinRecordAdvancePaymentItem selectFinRecordAdvancePaymentItemById(Long recordAdvancePaymentItemSid);

    List<FinRecordAdvancePaymentItem> selectFinRecordAdvancePaymentItemList(FinRecordAdvancePaymentItem finRecordAdvancePaymentItem);

    /**
     * 添加多个
     * @param list List FinRecordAdvancePaymentItem
     * @return int
     */
    int inserts(@Param("list") List<FinRecordAdvancePaymentItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinRecordAdvancePaymentItem
    * @return int
    */
    int updateAllById(FinRecordAdvancePaymentItem entity);

    /**
     * 更新多个
     * @param list List FinRecordAdvancePaymentItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinRecordAdvancePaymentItem> list);


}
