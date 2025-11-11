package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ReqPurchaseRequireItem;

/**
 * 申请单-明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-06
 */
public interface ReqPurchaseRequireItemMapper extends BaseMapper<ReqPurchaseRequireItem> {

    ReqPurchaseRequireItem selectReqPurchaseRequireItemById(Long purchaseRequireItemSid);

    List<ReqPurchaseRequireItem> selectReqPurchaseRequireItemList(ReqPurchaseRequireItem reqPurchaseRequireItem);

    /**
     * 添加多个
     *
     * @param list List ReqPurchaseRequireItem
     * @return int
     */
    int inserts(@Param("list") List<ReqPurchaseRequireItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ReqPurchaseRequireItem
     * @return int
     */
    int updateAllById(ReqPurchaseRequireItem entity);

    /**
     * 更新多个
     *
     * @param list List ReqPurchaseRequireItem
     * @return int
     */
    int updatesAllById(@Param("list") List<ReqPurchaseRequireItem> list);


    void deleteReqPurchaseRequireItemByIds(@Param("array") Long[] purchaseRequireSids);

    List<ReqPurchaseRequireItem> getItemList(ReqPurchaseRequireItem reqPurchaseRequireItem);
}
