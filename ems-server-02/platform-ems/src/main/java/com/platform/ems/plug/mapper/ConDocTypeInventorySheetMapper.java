package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeInventorySheet;

/**
 * 单据类型(盘点单)Mapper接口
 *
 * @author chenkw
 * @date 2021-08-11
 */
public interface ConDocTypeInventorySheetMapper  extends BaseMapper<ConDocTypeInventorySheet> {


    ConDocTypeInventorySheet selectConDocTypeInventorySheetById(Long sid);

    List<ConDocTypeInventorySheet> selectConDocTypeInventorySheetList(ConDocTypeInventorySheet conDocTypeInventorySheet);

    /**
     * 添加多个
     * @param list List ConDocTypeInventorySheet
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeInventorySheet> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity ConDocTypeInventorySheet
     * @return int
     */
    int updateAllById(ConDocTypeInventorySheet entity);

    /**
     * 更新多个
     * @param list List ConDocTypeInventorySheet
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeInventorySheet> list);


}
