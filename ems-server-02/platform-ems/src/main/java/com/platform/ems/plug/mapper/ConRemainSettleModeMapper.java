package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConBuTypePayBill;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConRemainSettleMode;

/**
 * 尾款结算方式Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConRemainSettleModeMapper  extends BaseMapper<ConRemainSettleMode> {


    ConRemainSettleMode selectConRemainSettleModeById(Long sid);

    List<ConRemainSettleMode> selectConRemainSettleModeList(ConRemainSettleMode conRemainSettleMode);

    /**
     * 添加多个
     * @param list List ConRemainSettleMode
     * @return int
     */
    int inserts(@Param("list") List<ConRemainSettleMode> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConRemainSettleMode
    * @return int
    */
    int updateAllById(ConRemainSettleMode entity);

    /**
     * 更新多个
     * @param list List ConRemainSettleMode
     * @return int
     */
    int updatesAllById(@Param("list") List<ConRemainSettleMode> list);

    /** 获取下拉列表 */
    List<ConRemainSettleMode> getConRemainSettleModeList();

}
