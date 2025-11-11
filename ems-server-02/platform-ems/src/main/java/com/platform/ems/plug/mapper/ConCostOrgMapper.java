package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConCostOrg;

/**
 * 成本组织Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConCostOrgMapper  extends BaseMapper<ConCostOrg> {


    ConCostOrg selectConCostOrgById(Long sid);

    List<ConCostOrg> selectConCostOrgList(ConCostOrg conCostOrg);

    List<ConCostOrg> getCostOrgList(ConCostOrg conCostOrg);

    /**
     * 添加多个
     * @param list List ConCostOrg
     * @return int
     */
    int inserts(@Param("list") List<ConCostOrg> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConCostOrg
    * @return int
    */
    int updateAllById(ConCostOrg entity);

    /**
     * 更新多个
     * @param list List ConCostOrg
     * @return int
     */
    int updatesAllById(@Param("list") List<ConCostOrg> list);


}
