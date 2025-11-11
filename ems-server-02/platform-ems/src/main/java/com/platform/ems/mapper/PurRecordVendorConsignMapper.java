package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurRecordVendorConsign;

/**
 * s_pur_record_vendor_consignMapper接口
 * 
 * @author linhongwei
 * @date 2021-06-23
 */
public interface PurRecordVendorConsignMapper  extends BaseMapper<PurRecordVendorConsign> {


    PurRecordVendorConsign selectPurRecordVendorConsignById(Long recordVendorConsignSid);

    List<PurRecordVendorConsign> selectPurRecordVendorConsignList(PurRecordVendorConsign purRecordVendorConsign);

    /**
     * 添加多个
     * @param list List PurRecordVendorConsign
     * @return int
     */
    int inserts(@Param("list") List<PurRecordVendorConsign> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurRecordVendorConsign
    * @return int
    */
    int updateAllById(PurRecordVendorConsign entity);

    /**
     * 更新多个
     * @param list List PurRecordVendorConsign
     * @return int
     */
    int updatesAllById(@Param("list") List<PurRecordVendorConsign> list);


}
