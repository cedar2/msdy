package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConStorageTradeType;

/**
 * 交易类型Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConStorageTradeTypeMapper  extends BaseMapper<ConStorageTradeType> {


    ConStorageTradeType selectConStorageTradeTypeById(Long sid);

    List<ConStorageTradeType> selectConStorageTradeTypeList(ConStorageTradeType conStorageTradeType);

    /**
     * 添加多个
     * @param list List ConStorageTradeType
     * @return int
     */
    int inserts(@Param("list") List<ConStorageTradeType> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConStorageTradeType
    * @return int
    */
    int updateAllById(ConStorageTradeType entity);

    /**
     * 更新多个
     * @param list List ConStorageTradeType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConStorageTradeType> list);


}
