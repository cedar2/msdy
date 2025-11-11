package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeCustomerAccountAdjust;

/**
 * 业务类型_客户调账单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeCustomerAccountAdjustMapper  extends BaseMapper<ConBuTypeCustomerAccountAdjust> {


    ConBuTypeCustomerAccountAdjust selectConBuTypeCustomerAccountAdjustById(Long sid);

    List<ConBuTypeCustomerAccountAdjust> selectConBuTypeCustomerAccountAdjustList(ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust);

    /**
     * 添加多个
     * @param list List ConBuTypeCustomerAccountAdjust
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeCustomerAccountAdjust> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeCustomerAccountAdjust
    * @return int
    */
    int updateAllById(ConBuTypeCustomerAccountAdjust entity);

    /**
     * 更新多个
     * @param list List ConBuTypeCustomerAccountAdjust
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeCustomerAccountAdjust> list);


}
