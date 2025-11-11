package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConStorageBinType;

/**
 * 仓位存储类型Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConStorageBinTypeMapper  extends BaseMapper<ConStorageBinType> {


    ConStorageBinType selectConStorageBinTypeById(Long sid);

    List<ConStorageBinType> selectConStorageBinTypeList(ConStorageBinType conStorageBinType);

    /**
     * 添加多个
     * @param list List ConStorageBinType
     * @return int
     */
    int inserts(@Param("list") List<ConStorageBinType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConStorageBinType
    * @return int
    */
    int updateAllById(ConStorageBinType entity);

    /**
     * 更新多个
     * @param list List ConStorageBinType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConStorageBinType> list);


}
