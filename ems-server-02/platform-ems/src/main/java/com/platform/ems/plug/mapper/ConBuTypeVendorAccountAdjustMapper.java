package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeVendorAccountAdjust;

/**
 * 业务类型_供应商调账单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeVendorAccountAdjustMapper  extends BaseMapper<ConBuTypeVendorAccountAdjust> {


    ConBuTypeVendorAccountAdjust selectConBuTypeVendorAccountAdjustById(Long sid);

    List<ConBuTypeVendorAccountAdjust> selectConBuTypeVendorAccountAdjustList(ConBuTypeVendorAccountAdjust conBuTypeVendorAccountAdjust);

    /**
     * 添加多个
     * @param list List ConBuTypeVendorAccountAdjust
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeVendorAccountAdjust> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeVendorAccountAdjust
    * @return int
    */
    int updateAllById(ConBuTypeVendorAccountAdjust entity);

    /**
     * 更新多个
     * @param list List ConBuTypeVendorAccountAdjust
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeVendorAccountAdjust> list);


}
