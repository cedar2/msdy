package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasSkuGroupItem;

/**
 * SKU组明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-22
 */
public interface BasSkuGroupItemMapper  extends BaseMapper<BasSkuGroupItem> {


    BasSkuGroupItem selectBasSkuGroupItemById(String clientId);

    List<BasSkuGroupItem> selectBasSkuGroupItemList(BasSkuGroupItem basSkuGroupItem);

    /**
     * 详情要按名称排序
     * @param basSkuGroupItem List BasSkuGroupItem
     * @return int
     */
    List<BasSkuGroupItem> selectBasSkuGroupItemListByNameSort(BasSkuGroupItem basSkuGroupItem);


    List<BasSkuGroupItem> getDetail(Long skuGroupSid);
    /**
     * 添加多个
     * @param list List BasSkuGroupItem
     * @return int
     */
    int inserts(@Param("list") List<BasSkuGroupItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasSkuGroupItem
    * @return int
    */
    int updateAllById(BasSkuGroupItem entity);

    /**
     * 更新多个
     * @param list List BasSkuGroupItem
     * @return int
     */
    int updatesAllById(@Param("list") List<BasSkuGroupItem> list);


}
