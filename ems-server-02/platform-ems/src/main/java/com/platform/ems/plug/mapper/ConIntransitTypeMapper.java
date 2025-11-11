package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConIntransitType;

/**
 * 在途类型Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-20
 */
public interface ConIntransitTypeMapper  extends BaseMapper<ConIntransitType> {


    ConIntransitType selectConIntransitTypeById(Long sid);

    List<ConIntransitType> selectConIntransitTypeList(ConIntransitType conIntransitType);

    /**
     * 添加多个
     * @param list List ConIntransitType
     * @return int
     */
    int inserts(@Param("list") List<ConIntransitType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConIntransitType
    * @return int
    */
    int updateAllById(ConIntransitType entity);

    /**
     * 更新多个
     * @param list List ConIntransitType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConIntransitType> list);


}
