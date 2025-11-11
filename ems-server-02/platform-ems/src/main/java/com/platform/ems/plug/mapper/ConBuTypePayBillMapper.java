package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBuTypePayBill;

/**
 * 业务类型_付款单Mapper接口
 *
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConBuTypePayBillMapper  extends BaseMapper<ConBuTypePayBill> {


    ConBuTypePayBill selectConBuTypePayBillById(Long sid);

    List<ConBuTypePayBill> selectConBuTypePayBillList(ConBuTypePayBill conBuTypePayBill);

    /**
     * 添加多个
     * @param list List ConBuTypePayBill
     * @return int
     */
    int inserts(@Param("list") List<ConBuTypePayBill> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBuTypePayBill
    * @return int
    */
    int updateAllById(ConBuTypePayBill entity);

    /**
     * 更新多个
     * @param list List ConBuTypePayBill
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBuTypePayBill> list);

    /** 获取下拉列表 */
    List<ConBuTypePayBill> getConBuTypePayBillList();

}
