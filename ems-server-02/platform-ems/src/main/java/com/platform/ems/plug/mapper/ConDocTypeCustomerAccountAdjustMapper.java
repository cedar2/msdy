package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeCustomerAccountAdjust;

/**
 * 单据类型_客户调账单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeCustomerAccountAdjustMapper  extends BaseMapper<ConDocTypeCustomerAccountAdjust> {


    ConDocTypeCustomerAccountAdjust selectConDocTypeCustomerAccountAdjustById(Long sid);

    List<ConDocTypeCustomerAccountAdjust> selectConDocTypeCustomerAccountAdjustList(ConDocTypeCustomerAccountAdjust conDocTypeCustomerAccountAdjust);

    /**
     * 添加多个
     * @param list List ConDocTypeCustomerAccountAdjust
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeCustomerAccountAdjust> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeCustomerAccountAdjust
    * @return int
    */
    int updateAllById(ConDocTypeCustomerAccountAdjust entity);

    /**
     * 更新多个
     * @param list List ConDocTypeCustomerAccountAdjust
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeCustomerAccountAdjust> list);


}
