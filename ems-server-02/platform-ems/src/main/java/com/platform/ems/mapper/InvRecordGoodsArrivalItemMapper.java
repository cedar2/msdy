package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.request.InvRecordGoodsArrivalRequest;
import com.platform.ems.domain.dto.response.InvRecordGoodsArrivalResponse;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvRecordGoodsArrivalItem;

/**
 * 采购到货台账-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2022-06-27
 */
public interface InvRecordGoodsArrivalItemMapper  extends BaseMapper<InvRecordGoodsArrivalItem> {


    List<InvRecordGoodsArrivalItem> selectInvRecordGoodsArrivalItemById(Long goodsArrivalSid);

    List<InvRecordGoodsArrivalItem> selectInvRecordGoodsArrivalItemList(InvRecordGoodsArrivalItem invRecordGoodsArrivalItem);
    List<InvRecordGoodsArrivalResponse> getReport(InvRecordGoodsArrivalRequest invRecordGoodsArrivalRequest);
    /**
     * 添加多个
     * @param list List InvRecordGoodsArrivalItem
     * @return int
     */
    int inserts(@Param("list") List<InvRecordGoodsArrivalItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvRecordGoodsArrivalItem
    * @return int
    */
    int updateAllById(InvRecordGoodsArrivalItem entity);

    /**
     * 更新多个
     * @param list List InvRecordGoodsArrivalItem
     * @return int
     */
    int updatesAllById(@Param("list") List<InvRecordGoodsArrivalItem> list);


}
