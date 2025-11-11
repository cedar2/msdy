package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConReceivePartnerType;

/**
 * 收货方类型Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConReceivePartnerTypeMapper  extends BaseMapper<ConReceivePartnerType> {


    ConReceivePartnerType selectConReceivePartnerTypeById(Long sid);

    List<ConReceivePartnerType> getList();

    List<ConReceivePartnerType> selectConReceivePartnerTypeList(ConReceivePartnerType conReceivePartnerType);

    /**
     * 添加多个
     * @param list List ConReceivePartnerType
     * @return int
     */
    int inserts(@Param("list") List<ConReceivePartnerType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConReceivePartnerType
    * @return int
    */
    int updateAllById(ConReceivePartnerType entity);

    /**
     * 更新多个
     * @param list List ConReceivePartnerType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConReceivePartnerType> list);


}
