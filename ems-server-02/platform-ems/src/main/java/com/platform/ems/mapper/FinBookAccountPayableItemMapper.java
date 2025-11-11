package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.FinBookAccountPayable;
import com.platform.ems.domain.FinBookAccountPayableItem;

/**
 * 财务流水账-明细-应付Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-03
 */
public interface FinBookAccountPayableItemMapper  extends BaseMapper<FinBookAccountPayableItem> {


    FinBookAccountPayableItem selectFinBookAccountPayableItemById(Long bookAccountPayableItemSid);

    List<FinBookAccountPayableItem> selectFinBookAccountPayableItemList(FinBookAccountPayableItem finBookAccountPayableItem);

    /**
     * 添加多个
     * @param list List FinBookAccountPayableItem
     * @return int
     */
    int inserts(@Param("list") List<FinBookAccountPayableItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity FinBookAccountPayableItem
    * @return int
    */
    int updateAllById(FinBookAccountPayableItem entity);

    /**
     * 更新多个
     * @param list List FinBookAccountPayableItem
     * @return int
     */
    int updatesAllById(@Param("list") List<FinBookAccountPayableItem> list);

    List<FinBookAccountPayableItem> getItemList(FinBookAccountPayable entity);
}
