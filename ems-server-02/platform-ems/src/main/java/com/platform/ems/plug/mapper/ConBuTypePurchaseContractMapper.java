package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypePurchaseContract;

/**
 * 业务类型_采购合同信息Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypePurchaseContractMapper  extends BaseMapper<ConBuTypePurchaseContract> {


    ConBuTypePurchaseContract selectConBuTypePurchaseContractById(Long sid);

    List<ConBuTypePurchaseContract> selectConBuTypePurchaseContractList(ConBuTypePurchaseContract conBuTypePurchaseContract);

    /**
     * 添加多个
     * @param list List ConBuTypePurchaseContract
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypePurchaseContract> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypePurchaseContract
    * @return int
    */
    int updateAllById(ConBuTypePurchaseContract entity);

    /**
     * 更新多个
     * @param list List ConBuTypePurchaseContract
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypePurchaseContract> list);


}
