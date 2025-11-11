package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.FinRecordAdvanceReceiptItem;

/**
 * 客户业务台账-明细-预收Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-16
 */
public interface FinRecordAdvanceReceiptItemMapper  extends BaseMapper<FinRecordAdvanceReceiptItem> {


    FinRecordAdvanceReceiptItem selectFinRecordAdvanceReceiptItemById(Long recordAdvanceReceiptItemSid);

    List<FinRecordAdvanceReceiptItem> selectFinRecordAdvanceReceiptItemList(FinRecordAdvanceReceiptItem finRecordAdvanceReceiptItem);

    /**
     * 添加多个
     * @param list List FinRecordAdvanceReceiptItem
     * @return int
     */
    int inserts(@Param("list") List<FinRecordAdvanceReceiptItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinRecordAdvanceReceiptItem
    * @return int
    */
    int updateAllById(FinRecordAdvanceReceiptItem entity);

    /**
     * 更新多个
     * @param list List FinRecordAdvanceReceiptItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinRecordAdvanceReceiptItem> list);


}
