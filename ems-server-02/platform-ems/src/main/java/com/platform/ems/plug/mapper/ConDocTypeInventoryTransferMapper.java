package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConBuTypeMaterialRequisition;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeInventoryTransfer;

/**
 * 单据类型_调拨单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeInventoryTransferMapper  extends BaseMapper<ConDocTypeInventoryTransfer> {
    ConDocTypeInventoryTransfer selectConDocTypeInventoryTransferById(Long sid);
    List<ConDocTypeInventoryTransfer> selectConDocTypeInventoryTransferList(ConDocTypeInventoryTransfer conDocTypeInventoryTransfer);
    List<ConDocTypeInventoryTransfer> getList();
    /**
     * 添加多个
     * @param list List ConDocTypeInventoryTransfer
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeInventoryTransfer> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeInventoryTransfer
    * @return int
    */
    int updateAllById(ConDocTypeInventoryTransfer entity);

    /**
     * 更新多个
     * @param list List ConDocTypeInventoryTransfer
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeInventoryTransfer> list);


}
