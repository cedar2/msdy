package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConSaleOrg;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConSaleGroup;

/**
 * 销售组Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConSaleGroupMapper  extends BaseMapper<ConSaleGroup> {


    ConSaleGroup selectConSaleGroupById(Long sid);

    List<ConSaleGroup> selectConSaleGroupList(ConSaleGroup conSaleGroup);

    /**
     * 添加多个
     * @param list List ConSaleGroup
     * @return int
     */
    int inserts(@Param("list") List<ConSaleGroup> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConSaleGroup
    * @return int
    */
    int updateAllById(ConSaleGroup entity);

    /**
     * 更新多个
     * @param list List ConSaleGroup
     * @return int
     */
    int updatesAllById(@Param("list") List<ConSaleGroup> list);

    /** 获取下拉列表 */
    List<ConSaleGroup> getConSaleGroupList();
}
