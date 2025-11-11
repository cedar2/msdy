package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvRecordGoodsArrivalDetail;

/**
 * 采购到货台账-缸号明细Mapper接口
 * 
 * @author linhongwei
 * @date 2022-06-27
 */
public interface InvRecordGoodsArrivalDetailMapper  extends BaseMapper<InvRecordGoodsArrivalDetail> {


    List<InvRecordGoodsArrivalDetail> selectInvRecordGoodsArrivalDetailById(Long goodsArrivalItemSid);

    List<InvRecordGoodsArrivalDetail> selectInvRecordGoodsArrivalDetailList(InvRecordGoodsArrivalDetail invRecordGoodsArrivalDetail);

    /**
     * 添加多个
     * @param list List InvRecordGoodsArrivalDetail
     * @return int
     */
    int inserts(@Param("list") List<InvRecordGoodsArrivalDetail> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvRecordGoodsArrivalDetail
    * @return int
    */
    int updateAllById(InvRecordGoodsArrivalDetail entity);

    /**
     * 更新多个
     * @param list List InvRecordGoodsArrivalDetail
     * @return int
     */
    int updatesAllById(@Param("list") List<InvRecordGoodsArrivalDetail> list);


}
