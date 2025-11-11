package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeServiceAcceptanceSale;

/**
 * 业务类型_服务销售验收单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeServiceAcceptanceSaleMapper  extends BaseMapper<ConBuTypeServiceAcceptanceSale> {


    ConBuTypeServiceAcceptanceSale selectConBuTypeServiceAcceptanceSaleById(Long sid);

    List<ConBuTypeServiceAcceptanceSale> selectConBuTypeServiceAcceptanceSaleList(ConBuTypeServiceAcceptanceSale conBuTypeServiceAcceptanceSale);

    /**
     * 添加多个
     * @param list List ConBuTypeServiceAcceptanceSale
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeServiceAcceptanceSale> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeServiceAcceptanceSale
    * @return int
    */
    int updateAllById(ConBuTypeServiceAcceptanceSale entity);

    /**
     * 更新多个
     * @param list List ConBuTypeServiceAcceptanceSale
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeServiceAcceptanceSale> list);


}
