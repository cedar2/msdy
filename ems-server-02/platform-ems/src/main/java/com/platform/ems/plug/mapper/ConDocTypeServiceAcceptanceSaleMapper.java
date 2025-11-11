package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeServiceAcceptanceSale;

/**
 * 单据类型_服务销售验收单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeServiceAcceptanceSaleMapper  extends BaseMapper<ConDocTypeServiceAcceptanceSale> {


    ConDocTypeServiceAcceptanceSale selectConDocTypeServiceAcceptanceSaleById(Long sid);

    List<ConDocTypeServiceAcceptanceSale> selectConDocTypeServiceAcceptanceSaleList(ConDocTypeServiceAcceptanceSale conDocTypeServiceAcceptanceSale);

    /**
     * 添加多个
     * @param list List ConDocTypeServiceAcceptanceSale
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeServiceAcceptanceSale> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeServiceAcceptanceSale
    * @return int
    */
    int updateAllById(ConDocTypeServiceAcceptanceSale entity);

    /**
     * 更新多个
     * @param list List ConDocTypeServiceAcceptanceSale
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeServiceAcceptanceSale> list);


}
