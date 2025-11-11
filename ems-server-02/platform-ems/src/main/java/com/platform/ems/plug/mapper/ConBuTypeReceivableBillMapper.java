package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConBuTypePayBill;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypeReceivableBill;

/**
 * 业务类型_收款单Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypeReceivableBillMapper  extends BaseMapper<ConBuTypeReceivableBill> {


    ConBuTypeReceivableBill selectConBuTypeReceivableBillById(Long sid);

    List<ConBuTypeReceivableBill> selectConBuTypeReceivableBillList(ConBuTypeReceivableBill conBuTypeReceivableBill);

    /**
     * 添加多个
     * @param list List ConBuTypeReceivableBill
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypeReceivableBill> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypeReceivableBill
    * @return int
    */
    int updateAllById(ConBuTypeReceivableBill entity);

    /**
     * 更新多个
     * @param list List ConBuTypeReceivableBill
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypeReceivableBill> list);

    /** 获取下拉列表 */
    List<ConBuTypeReceivableBill> getConBuTypeReceivableBillList();
}
