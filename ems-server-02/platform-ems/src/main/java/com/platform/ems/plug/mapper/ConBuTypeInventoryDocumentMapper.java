package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeInventoryDocument;

/**
 * 业务类型_库存凭证Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeInventoryDocumentMapper  extends BaseMapper<ConBuTypeInventoryDocument> {


    ConBuTypeInventoryDocument selectConBuTypeInventoryDocumentById(Long sid);

    List<ConBuTypeInventoryDocument> selectConBuTypeInventoryDocumentList(ConBuTypeInventoryDocument conBuTypeInventoryDocument);

    /**
     * 添加多个
     * @param list List ConBuTypeInventoryDocument
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeInventoryDocument> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeInventoryDocument
    * @return int
    */
    int updateAllById(ConBuTypeInventoryDocument entity);

    /**
     * 更新多个
     * @param list List ConBuTypeInventoryDocument
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeInventoryDocument> list);


}
