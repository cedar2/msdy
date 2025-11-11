package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConAccountMethodGroupMethod;

/**
 * 收付款方式组合-支付方式Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-10
 */
public interface ConAccountMethodGroupMethodMapper  extends BaseMapper<ConAccountMethodGroupMethod> {


    ConAccountMethodGroupMethod selectConAccountMethodGroupMethodById(Long sid);

    List<ConAccountMethodGroupMethod> selectConAccountMethodGroupMethodList(ConAccountMethodGroupMethod conAccountMethodGroupMethod);

    /**
     * 添加多个
     * @param list List ConAccountMethodGroupMethod
     * @return int
     */
    int inserts(@Param("list") List<ConAccountMethodGroupMethod> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConAccountMethodGroupMethod
    * @return int
    */
    int updateAllById(ConAccountMethodGroupMethod entity);

    /**
     * 更新多个
     * @param list List ConAccountMethodGroupMethod
     * @return int
     */
    int updatesAllById(@Param("list") List<ConAccountMethodGroupMethod> list);


}
