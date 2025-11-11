package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasSku;

/**
 * SKU档案Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-22
 */
public interface BasSkuMapper  extends BaseMapper<BasSku> {


    BasSku selectBasSkuById(Long skuSid);

    List<BasSku> selectBasSkuList(BasSku basSku);

    List<BasSku> getList(String skuType);

    /**
     * 添加多个
     * @param list List BasSku
     * @return int
     */
    int inserts(@Param("list") List<BasSku> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasSku
    * @return int
    */
    int updateAllById(BasSku entity);

    /**
     * 更新多个
     * @param list List BasSku
     * @return int
     */
    int updatesAllById(@Param("list") List<BasSku> list);


}
