package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConPartnerType;

/**
 * 类型_业务合作伙伴Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConPartnerTypeMapper  extends BaseMapper<ConPartnerType> {


    ConPartnerType selectConPartnerTypeById(Long sid);

    List<ConPartnerType> selectConPartnerTypeList(ConPartnerType conPartnerType);

    /**
     * 添加多个
     * @param list List ConPartnerType
     * @return int
     */
    int inserts(@Param("list") List<ConPartnerType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConPartnerType
    * @return int
    */
    int updateAllById(ConPartnerType entity);

    /**
     * 更新多个
     * @param list List ConPartnerType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConPartnerType> list);


}
