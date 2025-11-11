package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ReqPurchaseRequire;

/**
 * 申请单Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-06
 */
public interface ReqPurchaseRequireMapper extends BaseMapper<ReqPurchaseRequire> {

    ReqPurchaseRequire selectReqPurchaseRequireById(Long purchaseRequireSid);

    List<ReqPurchaseRequire> selectReqPurchaseRequireList(ReqPurchaseRequire reqPurchaseRequire);

    /**
     * 添加多个
     *
     * @param list List ReqPurchaseRequire
     * @return int
     */
    int inserts(@Param("list") List<ReqPurchaseRequire> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ReqPurchaseRequire
     * @return int
     */
    int updateAllById(ReqPurchaseRequire entity);

    /**
     * 更新多个
     *
     * @param list List ReqPurchaseRequire
     * @return int
     */
    int updatesAllById(@Param("list") List<ReqPurchaseRequire> list);


    int countByDomain(ReqPurchaseRequire params);

    int deleteReqPurchaseRequireByIds(@Param("array") Long[] purchaseRequireSids);

    int confirm(ReqPurchaseRequire reqPurchaseRequire);
}
