package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConDocTypeInventoryDocument;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 单据类型_库存凭证Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-17
 */
public interface ConDocTypeInventoryDocumentMapper extends BaseMapper<ConDocTypeInventoryDocument> {


    ConDocTypeInventoryDocument selectConDocTypeInventoryDocumentById(Long sid);

    List<ConDocTypeInventoryDocument> selectConDocTypeInventoryDocumentList(ConDocTypeInventoryDocument conDocTypeInventoryDocument);

    /**
     * 添加多个
     *
     * @param list List ConDocTypeInventoryDocument
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeInventoryDocument> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConDocTypeInventoryDocument
     * @return int
     */
    int updateAllById(ConDocTypeInventoryDocument entity);

    /**
     * 更新多个
     *
     * @param list List ConDocTypeInventoryDocument
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeInventoryDocument> list);


}
