package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConProductTechniqueType;

/**
 * 生产工艺方法(编织方法)Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConProductTechniqueTypeMapper  extends BaseMapper<ConProductTechniqueType> {


    ConProductTechniqueType selectConProductTechniqueTypeById(Long sid);

    List<ConProductTechniqueType> selectConProductTechniqueTypeList(ConProductTechniqueType conProductTechniqueType);

    /**
     * 添加多个
     * @param list List ConProductTechniqueType
     * @return int
     */
    int inserts(@Param("list") List<ConProductTechniqueType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConProductTechniqueType
    * @return int
    */
    int updateAllById(ConProductTechniqueType entity);

    /**
     * 更新多个
     * @param list List ConProductTechniqueType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConProductTechniqueType> list);

    /** 获取下拉列表 */
    List<ConProductTechniqueType> getConProductTechniqueTypeList();
}
