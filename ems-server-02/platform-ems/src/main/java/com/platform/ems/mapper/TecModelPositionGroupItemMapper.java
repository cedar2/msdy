package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.TecModelPositionGroupItem;

/**
 * 版型部位组明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-02
 */
public interface TecModelPositionGroupItemMapper  extends BaseMapper<TecModelPositionGroupItem> {


    TecModelPositionGroupItem selectTecModelPositionGroupItemById(Long groupItemSid);

    List<TecModelPositionGroupItem> selectTecModelPositionGroupItemList(TecModelPositionGroupItem tecModelPositionGroupItem);

    /**
     * 添加多个
     * @param list List TecModelPositionGroupItem
     * @return int
     */
    int inserts(@Param("list") List<TecModelPositionGroupItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity TecModelPositionGroupItem
    * @return int
    */
    int updateAllById(TecModelPositionGroupItem entity);

    /**
     * 更新多个
     * @param list List TecModelPositionGroupItem
     * @return int
     */
    int updatesAllById(@Param("list") List<TecModelPositionGroupItem> list);


}
