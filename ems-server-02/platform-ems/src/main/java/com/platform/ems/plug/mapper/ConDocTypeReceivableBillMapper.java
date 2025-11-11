package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDocTypeReceivableBill;

/**
 * 单据类型_收款单Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConDocTypeReceivableBillMapper  extends BaseMapper<ConDocTypeReceivableBill> {

    ConDocTypeReceivableBill selectConDocTypeReceivableBillById(Long sid);

    List<ConDocTypeReceivableBill> selectConDocTypeReceivableBillList(ConDocTypeReceivableBill conDocTypeReceivableBill);

    /**
     * 添加多个
     */
    int inserts(@Param("list") List<ConDocTypeReceivableBill> list);

    /**
     * 全量更新
     */
    int updateAllById(ConDocTypeReceivableBill entity);

    /**
     * 更新多个
     */
    int updatesAllById(@Param("list") List<ConDocTypeReceivableBill> list);

    /**
     * 获取下拉列表
     */
    List<ConDocTypeReceivableBill> getConDocTypeReceivableBillList(ConDocTypeReceivableBill conDocType);
}
