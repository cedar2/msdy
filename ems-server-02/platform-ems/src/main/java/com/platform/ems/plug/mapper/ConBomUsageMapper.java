package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBomUsage;

/**
 * BOM用途Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBomUsageMapper  extends BaseMapper<ConBomUsage> {


    ConBomUsage selectConBomUsageById(Long sid);

    List<ConBomUsage> selectConBomUsageList(ConBomUsage conBomUsage);

    /**
     * 添加多个
     * @param list List ConBomUsage
     * @return int
     */
    int inserts(@Param("list") List<ConBomUsage> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBomUsage
    * @return int
    */
    int updateAllById(ConBomUsage entity);

    /**
     * 更新多个
     * @param list List ConBomUsage
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBomUsage> list);


}
