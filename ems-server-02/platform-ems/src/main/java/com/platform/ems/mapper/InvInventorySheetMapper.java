package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.InvInventoryAdjust;
import com.platform.ems.domain.InvInventorySheet;

/**
 * 盘点单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-20
 */
public interface InvInventorySheetMapper  extends BaseMapper<InvInventorySheet> {


    InvInventorySheet selectInvInventorySheetById(Long inventorySheetSid);

    List<InvInventorySheet> selectInvInventorySheetList(InvInventorySheet invInventorySheet);

    /**
     * 添加多个
     * @param list List InvInventorySheet
     * @return int
     */
    int inserts(@Param("list") List<InvInventorySheet> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvInventorySheet
    * @return int
    */
    int updateAllById(InvInventorySheet entity);

    /**
     * 更新多个
     * @param list List InvInventorySheet
     * @return int
     */
    int updatesAllById(@Param("list") List<InvInventorySheet> list);


    int countByDomain(InvInventorySheet params);

    int deleteInvInventorySheetByIds(@Param("array") Long[] inventorySheetSids);

    int confirm(InvInventorySheet invInventorySheet);
    
    InvInventorySheet getName(InvInventorySheet invInventorySheet);
}
