package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypePayBill;

/**
 * 单据类型_付款单Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypePayBillMapper extends BaseMapper<ConDocTypePayBill> {

    ConDocTypePayBill selectConDocTypePayBillById(Long sid);

    List<ConDocTypePayBill> selectConDocTypePayBillList(ConDocTypePayBill conDocTypePayBill);

    /**
     * 添加多个
     */
    int inserts(@Param("list") List<ConDocTypePayBill> list);

    /**
     * 全量更新
     */
    int updateAllById(ConDocTypePayBill entity);

    /**
     * 更新多个
     */
    int updatesAllById(@Param("list") List<ConDocTypePayBill> list);

    /**
     * 获取下拉列表
     */
    List<ConDocTypePayBill> getConDocTypePayBillList(ConDocTypePayBill conDocType);
}
