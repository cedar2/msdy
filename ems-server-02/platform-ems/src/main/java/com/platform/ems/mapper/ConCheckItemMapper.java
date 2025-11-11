package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ConCheckItem;

/**
 * 检测项目Mapper接口
 * 
 * @author qhq
 * @date 2021-11-01
 */
public interface ConCheckItemMapper  extends BaseMapper<ConCheckItem> {


    ConCheckItem selectConCheckItemById (Long sid);

    List<ConCheckItem> selectConCheckItemList (ConCheckItem conCheckItem);

    /**
     * 添加多个
     * @param list List ConCheckItem
     * @return int
     */
    int inserts (@Param("list") List<ConCheckItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConCheckItem
    * @return int
    */
    int updateAllById (ConCheckItem entity);

    /**
     * 更新多个
     * @param list List ConCheckItem
     * @return int
     */
    int updatesAllById (@Param("list") List<ConCheckItem> list);


}
