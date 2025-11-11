package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConInventoryDocumentCategory;
import org.apache.ibatis.annotations.Param;


/**
 * 库存凭证类别Mapper接口
 * 
 * @author c
 * @date 2021-07-29
 */
public interface ConInventoryDocumentCategoryMapper  extends BaseMapper<ConInventoryDocumentCategory> {


    ConInventoryDocumentCategory selectConInventoryDocumentCategoryById(Long sid);

    List<ConInventoryDocumentCategory> selectConInventoryDocumentCategoryList(ConInventoryDocumentCategory conInventoryDocumentCategory);

    List<ConInventoryDocumentCategory> getList();

    /**
     * 添加多个
     * @param list List ConInventoryDocumentCategory
     * @return int
     */
    int inserts(@Param("list") List<ConInventoryDocumentCategory> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConInventoryDocumentCategory
    * @return int
    */
    int updateAllById(ConInventoryDocumentCategory entity);

    /**
     * 更新多个
     * @param list List ConInventoryDocumentCategory
     * @return int
     */
    int updatesAllById(@Param("list") List<ConInventoryDocumentCategory> list);


}
