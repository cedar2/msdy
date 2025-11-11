package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConCreditType;

/**
 * 信用类型Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConCreditTypeMapper  extends BaseMapper<ConCreditType> {


    ConCreditType selectConCreditTypeById(Long sid);

    List<ConCreditType> selectConCreditTypeList(ConCreditType conCreditType);

    /**
     * 添加多个
     * @param list List ConCreditType
     * @return int
     */
    int inserts(@Param("list") List<ConCreditType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConCreditType
    * @return int
    */
    int updateAllById(ConCreditType entity);

    /**
     * 更新多个
     * @param list List ConCreditType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConCreditType> list);


}
