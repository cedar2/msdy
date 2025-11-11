package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConBuTypeMaterialRequisition;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConSpecialStock;

/**
 * 特殊库存Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConSpecialStockMapper  extends BaseMapper<ConSpecialStock> {


    ConSpecialStock selectConSpecialStockById(Long sid);

    List<ConSpecialStock> selectConSpecialStockList(ConSpecialStock conSpecialStock);

    /**
     * 添加多个
     * @param list List ConSpecialStock
     * @return int
     */
    int inserts(@Param("list") List<ConSpecialStock> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConSpecialStock
    * @return int
    */
    int updateAllById(ConSpecialStock entity);

    /**
     * 更新多个
     * @param list List ConSpecialStock
     * @return int
     */
    int updatesAllById(@Param("list") List<ConSpecialStock> list);
    List<ConSpecialStock> getList();

}
