package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.TecLinePositionGroupItem;

/**
 * 线部位组明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-08-19
 */
public interface TecLinePositionGroupItemMapper  extends BaseMapper<TecLinePositionGroupItem> {


    TecLinePositionGroupItem selectTecLinePositionGroupItemById(Long groupItemSid);

    List<TecLinePositionGroupItem> selectTecLinePositionGroupItemList(TecLinePositionGroupItem tecLinePositionGroupItem);

    /**
     * 添加多个
     * @param list List TecLinePositionGroupItem
     * @return int
     */
    int inserts(@Param("list") List<TecLinePositionGroupItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity TecLinePositionGroupItem
    * @return int
    */
    int updateAllById(TecLinePositionGroupItem entity);

    /**
     * 更新多个
     * @param list List TecLinePositionGroupItem
     * @return int
     */
    int updatesAllById(@Param("list") List<TecLinePositionGroupItem> list);


}
