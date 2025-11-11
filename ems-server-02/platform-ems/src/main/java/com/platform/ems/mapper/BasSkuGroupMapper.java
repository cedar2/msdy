package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasSkuGroup;

/**
 * SKU组档案Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-22
 */
public interface BasSkuGroupMapper  extends BaseMapper<BasSkuGroup> {


    BasSkuGroup selectBasSkuGroupById(Long clientId);



    List<BasSkuGroup> selectBasSkuGroupList(BasSkuGroup basSkuGroup);

    List<BasSkuGroup> getList(BasSkuGroup basSkuGroup);
    /**
     * 添加多个
     * @param list List BasSkuGroup
     * @return int
     */
    int inserts(@Param("list") List<BasSkuGroup> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasSkuGroup
    * @return int
    */
    int updateAllById(BasSkuGroup entity);

    /**
     * 更新多个
     * @param list List BasSkuGroup
     * @return int
     */
    int updatesAllById(@Param("list") List<BasSkuGroup> list);


}
