package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeSaleContract;

/**
 * 业务类型_销售合同信息Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeSaleContractMapper  extends BaseMapper<ConBuTypeSaleContract> {


    ConBuTypeSaleContract selectConBuTypeSaleContractById(Long sid);

    List<ConBuTypeSaleContract> selectConBuTypeSaleContractList(ConBuTypeSaleContract conBuTypeSaleContract);

    /**
     * 添加多个
     * @param list List ConBuTypeSaleContract
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeSaleContract> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeSaleContract
    * @return int
    */
    int updateAllById(ConBuTypeSaleContract entity);

    /**
     * 更新多个
     * @param list List ConBuTypeSaleContract
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeSaleContract> list);


}
