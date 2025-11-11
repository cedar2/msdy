package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConShelfType;

/**
 * 货架类型Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConShelfTypeMapper  extends BaseMapper<ConShelfType> {


    ConShelfType selectConShelfTypeById(Long sid);

    List<ConShelfType> selectConShelfTypeList(ConShelfType conShelfType);

    /**
     * 添加多个
     * @param list List ConShelfType
     * @return int
     */
    int inserts(@Param("list") List<ConShelfType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConShelfType
    * @return int
    */
    int updateAllById(ConShelfType entity);

    /**
     * 更新多个
     * @param list List ConShelfType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConShelfType> list);


}
