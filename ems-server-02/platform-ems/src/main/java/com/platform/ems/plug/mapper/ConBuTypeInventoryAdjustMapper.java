package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeInventoryAdjust;

/**
 * 业务类型_库存调整单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeInventoryAdjustMapper  extends BaseMapper<ConBuTypeInventoryAdjust> {


    ConBuTypeInventoryAdjust selectConBuTypeInventoryAdjustById(Long sid);

    List<ConBuTypeInventoryAdjust> selectConBuTypeInventoryAdjustList(ConBuTypeInventoryAdjust conBuTypeInventoryAdjust);
    List<ConBuTypeInventoryAdjust> getList();
    /**
     * 添加多个
     * @param list List ConBuTypeInventoryAdjust
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeInventoryAdjust> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeInventoryAdjust
    * @return int
    */
    int updateAllById(ConBuTypeInventoryAdjust entity);

    /**
     * 更新多个
     * @param list List ConBuTypeInventoryAdjust
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeInventoryAdjust> list);


}
