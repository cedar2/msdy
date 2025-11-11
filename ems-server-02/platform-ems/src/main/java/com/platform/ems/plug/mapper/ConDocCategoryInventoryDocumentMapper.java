package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocCategoryInventoryDocument;

/**
 * 单据类别_库存凭证Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocCategoryInventoryDocumentMapper  extends BaseMapper<ConDocCategoryInventoryDocument> {


    ConDocCategoryInventoryDocument selectConDocCategoryInventoryDocumentById(Long sid);

    List<ConDocCategoryInventoryDocument> selectConDocCategoryInventoryDocumentList(ConDocCategoryInventoryDocument conDocCategoryInventoryDocument);
    List<ConDocCategoryInventoryDocument> getList();
    /**
     * 添加多个
     * @param list List ConDocCategoryInventoryDocument
     * @return int
     */
    int inserts(@Param("list") List<ConDocCategoryInventoryDocument> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocCategoryInventoryDocument
    * @return int
    */
    int updateAllById(ConDocCategoryInventoryDocument entity);

    /**
     * 更新多个
     * @param list List ConDocCategoryInventoryDocument
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocCategoryInventoryDocument> list);


}
