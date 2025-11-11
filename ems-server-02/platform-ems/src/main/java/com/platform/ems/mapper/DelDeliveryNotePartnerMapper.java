package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DelDeliveryNotePartner;

/**
 * 交货单-合作伙伴Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-21
 */
public interface DelDeliveryNotePartnerMapper  extends BaseMapper<DelDeliveryNotePartner> {


    DelDeliveryNotePartner selectDelDeliveryNotePartnerById(Long deliveryNotePartnerSid);

    List<DelDeliveryNotePartner> selectDelDeliveryNotePartnerList(DelDeliveryNotePartner delDeliveryNotePartner);

    /**
     * 添加多个
     * @param list List DelDeliveryNotePartner
     * @return int
     */
    int inserts(@Param("list") List<DelDeliveryNotePartner> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DelDeliveryNotePartner
    * @return int
    */
    int updateAllById(DelDeliveryNotePartner entity);

    /**
     * 更新多个
     * @param list List DelDeliveryNotePartner
     * @return int
     */
    int updatesAllById(@Param("list") List<DelDeliveryNotePartner> list);


    void deleteDelDeliveryNotePartnerByIds(@Param("array") Long[] deliveryNoteSids);
}
