package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConDiscountType;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConPurchaseOrg;

/**
 * 采购组织Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConPurchaseOrgMapper  extends BaseMapper<ConPurchaseOrg> {


    ConPurchaseOrg selectConPurchaseOrgById(Long sid);

    List<ConPurchaseOrg> selectConPurchaseOrgList(ConPurchaseOrg conPurchaseOrg);

    /**
     * 添加多个
     * @param list List ConPurchaseOrg
     * @return int
     */
    int inserts(@Param("list") List<ConPurchaseOrg> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConPurchaseOrg
    * @return int
    */
    int updateAllById(ConPurchaseOrg entity);

    /**
     * 更新多个
     * @param list List ConPurchaseOrg
     * @return int
     */
    int updatesAllById(@Param("list") List<ConPurchaseOrg> list);

    /** 获取下拉列表 */
    List<ConPurchaseOrg> getConPurchaseOrgList();
}
