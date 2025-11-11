package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvRecordGoodsArrival;

/**
 * 采购到货台账Mapper接口
 * 
 * @author linhongwei
 * @date 2022-06-27
 */
public interface InvRecordGoodsArrivalMapper  extends BaseMapper<InvRecordGoodsArrival> {


    InvRecordGoodsArrival selectInvRecordGoodsArrivalById(Long goodsArrivalSid);

    List<InvRecordGoodsArrival> selectInvRecordGoodsArrivalList(InvRecordGoodsArrival invRecordGoodsArrival);

    /**
     * 添加多个
     * @param list List InvRecordGoodsArrival
     * @return int
     */
    int inserts(@Param("list") List<InvRecordGoodsArrival> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvRecordGoodsArrival
    * @return int
    */
    int updateAllById(InvRecordGoodsArrival entity);

    /**
     * 更新多个
     * @param list List InvRecordGoodsArrival
     * @return int
     */
    int updatesAllById(@Param("list") List<InvRecordGoodsArrival> list);


}
