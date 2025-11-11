package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeRequireDocument;

/**
 * 业务类型_需求单Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeRequireDocumentMapper  extends BaseMapper<ConBuTypeRequireDocument> {


    ConBuTypeRequireDocument selectConBuTypeRequireDocumentById(Long sid);

    List<ConBuTypeRequireDocument> selectConBuTypeRequireDocumentList(ConBuTypeRequireDocument conBuTypeRequireDocument);

    /**
     * 添加多个
     * @param list List ConBuTypeRequireDocument
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeRequireDocument> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeRequireDocument
    * @return int
    */
    int updateAllById(ConBuTypeRequireDocument entity);

    /**
     * 更新多个
     * @param list List ConBuTypeRequireDocument
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeRequireDocument> list);


}
