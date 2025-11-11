package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeInventoryAdjust;

/**
 * 单据类型_库存调整单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeInventoryAdjustMapper  extends BaseMapper<ConDocTypeInventoryAdjust> {


    ConDocTypeInventoryAdjust selectConDocTypeInventoryAdjustById(Long sid);

    List<ConDocTypeInventoryAdjust> selectConDocTypeInventoryAdjustList(ConDocTypeInventoryAdjust conDocTypeInventoryAdjust);

    /**
     * 添加多个
     * @param list List ConDocTypeInventoryAdjust
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeInventoryAdjust> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeInventoryAdjust
    * @return int
    */
    int updateAllById(ConDocTypeInventoryAdjust entity);

    /**
     * 更新多个
     * @param list List ConDocTypeInventoryAdjust
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeInventoryAdjust> list);


}
