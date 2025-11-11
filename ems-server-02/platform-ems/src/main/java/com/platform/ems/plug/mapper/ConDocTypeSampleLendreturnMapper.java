package com.platform.ems.plug.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConDocTypeSampleLendreturn;
import org.apache.ibatis.annotations.Param;


/**
 * 单据类型_样品借还单Mapper接口
 * 
 * @author linhongwei
 * @date 2022-01-24
 */
public interface ConDocTypeSampleLendreturnMapper  extends BaseMapper<ConDocTypeSampleLendreturn> {


    ConDocTypeSampleLendreturn selectConDocTypeSampleLendreturnById(Long sid);

    List<ConDocTypeSampleLendreturn> selectConDocTypeSampleLendreturnList(ConDocTypeSampleLendreturn conDocTypeSampleLendreturn);

    /**
     * 添加多个
     * @param list List ConDocTypeSampleLendreturn
     * @return int
     */
    int inserts(@Param("list") List<ConDocTypeSampleLendreturn> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDocTypeSampleLendreturn
    * @return int
    */
    int updateAllById(ConDocTypeSampleLendreturn entity);

    /**
     * 更新多个
     * @param list List ConDocTypeSampleLendreturn
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDocTypeSampleLendreturn> list);


}
