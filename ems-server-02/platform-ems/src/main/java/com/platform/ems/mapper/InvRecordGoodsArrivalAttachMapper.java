package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvRecordGoodsArrivalAttach;

/**
 * 采购到货台账-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2022-06-27
 */
public interface InvRecordGoodsArrivalAttachMapper  extends BaseMapper<InvRecordGoodsArrivalAttach> {


    List<InvRecordGoodsArrivalAttach> selectInvRecordGoodsArrivalAttachById(Long goodsArrivalSid);

    List<InvRecordGoodsArrivalAttach> selectInvRecordGoodsArrivalAttachList(InvRecordGoodsArrivalAttach invRecordGoodsArrivalAttach);

    /**
     * 添加多个
     * @param list List InvRecordGoodsArrivalAttach
     * @return int
     */
    int inserts(@Param("list") List<InvRecordGoodsArrivalAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvRecordGoodsArrivalAttach
    * @return int
    */
    int updateAllById(InvRecordGoodsArrivalAttach entity);

    /**
     * 更新多个
     * @param list List InvRecordGoodsArrivalAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<InvRecordGoodsArrivalAttach> list);


}
