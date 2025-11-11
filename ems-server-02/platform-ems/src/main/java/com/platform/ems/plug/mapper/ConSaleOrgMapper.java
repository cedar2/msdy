package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConPurchaseOrg;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConSaleOrg;

/**
 * 销售组织Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConSaleOrgMapper  extends BaseMapper<ConSaleOrg> {


    ConSaleOrg selectConSaleOrgById(Long sid);

    List<ConSaleOrg> selectConSaleOrgList(ConSaleOrg conSaleOrg);

    /**
     * 添加多个
     * @param list List ConSaleOrg
     * @return int
     */
    int inserts(@Param("list") List<ConSaleOrg> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConSaleOrg
    * @return int
    */
    int updateAllById(ConSaleOrg entity);

    /**
     * 更新多个
     * @param list List ConSaleOrg
     * @return int
     */
    int updatesAllById(@Param("list") List<ConSaleOrg> list);

    /** 获取下拉列表 */
    List<ConSaleOrg> getConSaleOrgList();
}
