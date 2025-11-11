package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookAccountReceivable;
import com.platform.ems.domain.FinBookAccountReceivableItem;

/**
 * 财务流水账-明细-应收Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-11
 */
public interface FinBookAccountReceivableItemMapper  extends BaseMapper<FinBookAccountReceivableItem> {


    FinBookAccountReceivableItem selectFinBookAccountReceivableItemById(Long bookAccountReceivableItemSid);

    List<FinBookAccountReceivableItem> selectFinBookAccountReceivableItemList(FinBookAccountReceivableItem finBookAccountReceivableItem);

    /**
     * 添加多个
     * @param list List FinBookAccountReceivableItem
     * @return int
     */
    int inserts(@Param("list") List<FinBookAccountReceivableItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookAccountReceivableItem
    * @return int
    */
    int updateAllById(FinBookAccountReceivableItem entity);

    /**
     * 更新多个
     * @param list List FinBookAccountReceivableItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookAccountReceivableItem> list);
    
    /**
     * 
     */
    List<FinBookAccountReceivableItem> getItemList(FinBookAccountReceivable entity);

}
