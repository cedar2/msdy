package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeRequireDocument;

/**
 * 单据类型_需求单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeRequireDocumentMapper  extends BaseMapper<ConDocTypeRequireDocument> {


    ConDocTypeRequireDocument selectConDocTypeRequireDocumentById(Long sid);

    List<ConDocTypeRequireDocument> selectConDocTypeRequireDocumentList(ConDocTypeRequireDocument conDocTypeRequireDocument);

    /**
     * 添加多个
     * @param list List ConDocTypeRequireDocument
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeRequireDocument> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeRequireDocument
    * @return int
    */
    int updateAllById(ConDocTypeRequireDocument entity);

    /**
     * 更新多个
     * @param list List ConDocTypeRequireDocument
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeRequireDocument> list);


}
