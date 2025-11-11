package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeInventoryTransfer;

/**
 * 业务类型_调拨单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeInventoryTransferMapper  extends BaseMapper<ConBuTypeInventoryTransfer> {


    ConBuTypeInventoryTransfer selectConBuTypeInventoryTransferById(Long sid);

    List<ConBuTypeInventoryTransfer> selectConBuTypeInventoryTransferList(ConBuTypeInventoryTransfer conBuTypeInventoryTransfer);
    List<ConBuTypeInventoryTransfer> getList();
    /**
     * 添加多个
     * @param list List ConBuTypeInventoryTransfer
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeInventoryTransfer> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeInventoryTransfer
    * @return int
    */
    int updateAllById(ConBuTypeInventoryTransfer entity);

    /**
     * 更新多个
     * @param list List ConBuTypeInventoryTransfer
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeInventoryTransfer> list);


}
