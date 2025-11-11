package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConAdvanceSettleMode;

/**
 * 预收款/预付款付款方式Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-20
 */
public interface ConAdvanceSettleModeMapper  extends BaseMapper<ConAdvanceSettleMode> {


    ConAdvanceSettleMode selectConAdvanceSettleModeById(Long sid);

    List<ConAdvanceSettleMode> selectConAdvanceSettleModeList(ConAdvanceSettleMode conAdvanceSettleMode);

    /**
     * 添加多个
     * @param list List ConAdvanceSettleMode
     * @return int
     */
    int inserts(@Param("list") List<ConAdvanceSettleMode> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConAdvanceSettleMode
    * @return int
    */
    int updateAllById(ConAdvanceSettleMode entity);

    /**
     * 更新多个
     * @param list List ConAdvanceSettleMode
     * @return int
     */
    int updatesAllById(@Param("list") List<ConAdvanceSettleMode> list);

    /** 获取下拉列表 */
    List<ConAdvanceSettleMode> getConAdvanceSettleModeList();

}
