package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConMaterialType;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConPaymentMethod;

/**
 * 支付方式Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-20
 */
public interface ConPaymentMethodMapper  extends BaseMapper<ConPaymentMethod> {


    ConPaymentMethod selectConPaymentMethodById(Long sid);

    List<ConPaymentMethod> selectConPaymentMethodList(ConPaymentMethod conPaymentMethod);

    /**
     * 添加多个
     * @param list List ConPaymentMethod
     * @return int
     */
    int inserts(@Param("list") List<ConPaymentMethod> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConPaymentMethod
    * @return int
    */
    int updateAllById(ConPaymentMethod entity);

    /**
     * 更新多个
     * @param list List ConPaymentMethod
     * @return int
     */
    int updatesAllById(@Param("list") List<ConPaymentMethod> list);

    /** 获取下拉列表 */
    List<ConPaymentMethod> getConPaymentMethodList(ConPaymentMethod conPaymentMethod);

}
