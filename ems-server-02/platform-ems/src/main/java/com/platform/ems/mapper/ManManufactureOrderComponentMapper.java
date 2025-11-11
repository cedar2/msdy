package com.platform.ems.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManManufactureOrderComponent;

/**
 * 生产订单-组件Mapper接口
 * 
 * @author qhq
 * @date 2021-04-13
 */
public interface ManManufactureOrderComponentMapper  extends BaseMapper<ManManufactureOrderComponent> {


    ManManufactureOrderComponent selectManManufactureOrderComponentById(String manufactureOrderComponentSid);

    List<ManManufactureOrderComponent> selectManManufactureOrderComponentList(ManManufactureOrderComponent manManufactureOrderComponent);

    /**
     * 添加多个
     * @param list List ManManufactureOrderComponent
     * @return int
     */
    int inserts(@Param("list") List<ManManufactureOrderComponent> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManManufactureOrderComponent
    * @return int
    */
    int updateAllById(ManManufactureOrderComponent entity);

    /**
     * 更新多个
     * @param list List ManManufactureOrderComponent
     * @return int
     */
    int updatesAllById(@Param("list") List<ManManufactureOrderComponent> list);


}
