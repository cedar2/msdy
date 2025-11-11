package com.platform.ems.mapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.response.form.ManOutsourceSettleStatistics;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManManufactureOutsourceSettleItem;

/**
 * 外发加工费结算单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-10
 */
public interface ManManufactureOutsourceSettleItemMapper  extends BaseMapper<ManManufactureOutsourceSettleItem> {


    ManManufactureOutsourceSettleItem selectManManufactureOutsourceSettleItemById(Long manufactureOutsourceSettleItemSid);

    @InterceptorIgnore(tenantLine = "true")
    List<ManManufactureOutsourceSettleItem> selectManManufactureOutsourceSettleItemList(ManManufactureOutsourceSettleItem manManufactureOutsourceSettleItem);

    @InterceptorIgnore(tenantLine = "true")
    List<ManManufactureOutsourceSettleItem> selectManManufactureOutsourceSettleItemForm(ManManufactureOutsourceSettleItem manManufactureOutsourceSettleItem);

    /**
     * 查询商品外加工费统计报表
     *
     * @param request 条件
     * @return 外发加工费结算单-明细集合
     */
    List<ManOutsourceSettleStatistics> selectManManufactureOutsourceSettleStatistics(ManOutsourceSettleStatistics request);

    /**
     * 添加多个
     * @param list List ManManufactureOutsourceSettleItem
     * @return int
     */
    int inserts(@Param("list") List<ManManufactureOutsourceSettleItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManManufactureOutsourceSettleItem
    * @return int
    */
    int updateAllById(ManManufactureOutsourceSettleItem entity);

    /**
     * 更新多个
     * @param list List ManManufactureOutsourceSettleItem
     * @return int
     */
    int updatesAllById(@Param("list") List<ManManufactureOutsourceSettleItem> list);

}
