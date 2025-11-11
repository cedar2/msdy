package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeVendorAccountAdjust;

/**
 * 单据类型_供应商调账单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeVendorAccountAdjustMapper  extends BaseMapper<ConDocTypeVendorAccountAdjust> {


    ConDocTypeVendorAccountAdjust selectConDocTypeVendorAccountAdjustById(Long sid);

    List<ConDocTypeVendorAccountAdjust> selectConDocTypeVendorAccountAdjustList(ConDocTypeVendorAccountAdjust conDocTypeVendorAccountAdjust);

    /**
     * 添加多个
     * @param list List ConDocTypeVendorAccountAdjust
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeVendorAccountAdjust> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeVendorAccountAdjust
    * @return int
    */
    int updateAllById(ConDocTypeVendorAccountAdjust entity);

    /**
     * 更新多个
     * @param list List ConDocTypeVendorAccountAdjust
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeVendorAccountAdjust> list);


}
