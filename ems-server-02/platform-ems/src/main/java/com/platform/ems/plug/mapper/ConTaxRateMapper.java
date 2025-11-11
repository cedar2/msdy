package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConTaxRate;

/**
 * 税率配置Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConTaxRateMapper  extends BaseMapper<ConTaxRate> {

    ConTaxRate selectConTaxRateById(Long taxRateSid);

    List<ConTaxRate> selectConTaxRateList(ConTaxRate conTaxRate);

    /**
     * 添加多个
     * @param list List ConTaxRate
     * @return int
     */
    int inserts(@Param("list") List<ConTaxRate> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConTaxRate
    * @return int
    */
    int updateAllById(ConTaxRate entity);

    /** 获取下拉列表 */
    List<ConTaxRate> getConTaxRateList();

    /** 获取下拉列表 */
    List<ConTaxRate> getList(ConTaxRate entity);
}
