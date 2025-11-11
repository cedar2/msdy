package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDataobjectCodeRule;

/**
 * 数据对象类别编码规则Mapper接口
 *
 * @author chenkw
 * @date 2021-11-25
 */
public interface ConDataobjectCodeRuleMapper  extends BaseMapper<ConDataobjectCodeRule> {


    ConDataobjectCodeRule selectConDataobjectCodeRuleById(Long sid);

    List<ConDataobjectCodeRule> selectConDataobjectCodeRuleList(ConDataobjectCodeRule conDataobjectCodeRule);

    /**
     * 添加多个
     * @param list List ConDataobjectCodeRule
     * @return int
     */
    int inserts(@Param("list") List<ConDataobjectCodeRule> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity ConDataobjectCodeRule
     * @return int
     */
    int updateAllById(ConDataobjectCodeRule entity);

    /**
     * 更新多个
     * @param list List ConDataobjectCodeRule
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDataobjectCodeRule> list);

    /**
     * 根据条件查询当前编码值
     * @param entity ConDataobjectCodeRule
     * @return String
     */
    ConDataobjectCodeRule selectCurrentNumberByRule(ConDataobjectCodeRule entity);

    /**
     * 新增当前编码
     *
     * @param entity ConDataobjectCodeRule
     * @return int
     */
    int addCurrentNumber(ConDataobjectCodeRule entity);


}