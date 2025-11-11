package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConInOutStockDocCategory;
import org.apache.ibatis.annotations.Param;


/**
 * 出入库对应的单据类别Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-15
 */
public interface ConInOutStockDocCategoryMapper  extends BaseMapper<ConInOutStockDocCategory> {


    ConInOutStockDocCategory selectConInOutStockDocCategoryById(Long sid);

    List<ConInOutStockDocCategory> selectConInOutStockDocCategoryList(ConInOutStockDocCategory conInOutStockDocCategory);

    /**
     * 添加多个
     * @param list List ConInOutStockDocCategory
     * @return int
     */
    int inserts(@Param("list") List<ConInOutStockDocCategory> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConInOutStockDocCategory
    * @return int
    */
    int updateAllById(ConInOutStockDocCategory entity);

    /**
     * 更新多个
     * @param list List ConInOutStockDocCategory
     * @return int
     */
    int updatesAllById(@Param("list") List<ConInOutStockDocCategory> list);

    List<ConInOutStockDocCategory>  getList();
    List<ConInOutStockDocCategory>  getListCategory(@Param("movementTypeCode") String movementTypeCode);
}
