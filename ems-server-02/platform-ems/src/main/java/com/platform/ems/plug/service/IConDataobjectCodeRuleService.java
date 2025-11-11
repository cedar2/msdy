package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConDataobjectCodeRule;

/**
 * 数据对象类别编码规则Service接口
 *
 * @author chenkw
 * @date 2021-11-25
 */
public interface IConDataobjectCodeRuleService extends IService<ConDataobjectCodeRule>{
    /**
     * 查询数据对象类别编码规则
     *
     * @param sid 数据对象类别编码规则ID
     * @return 数据对象类别编码规则
     */
    public ConDataobjectCodeRule selectConDataobjectCodeRuleById(Long sid);

    /**
     * 查询数据对象类别编码规则列表
     *
     * @param conDataobjectCodeRule 数据对象类别编码规则
     * @return 数据对象类别编码规则集合
     */
    public List<ConDataobjectCodeRule> selectConDataobjectCodeRuleList(ConDataobjectCodeRule conDataobjectCodeRule);

    /**
     * 新增数据对象类别编码规则
     *
     * @param conDataobjectCodeRule 数据对象类别编码规则
     * @return 结果
     */
    public int insertConDataobjectCodeRule(ConDataobjectCodeRule conDataobjectCodeRule);

    /**
     * 修改数据对象类别编码规则
     *
     * @param conDataobjectCodeRule 数据对象类别编码规则
     * @return 结果
     */
    public int updateConDataobjectCodeRule(ConDataobjectCodeRule conDataobjectCodeRule);

    /**
     * 变更数据对象类别编码规则
     *
     * @param conDataobjectCodeRule 数据对象类别编码规则
     * @return 结果
     */
    public int changeConDataobjectCodeRule(ConDataobjectCodeRule conDataobjectCodeRule);

    /**
     * 批量删除数据对象类别编码规则
     *
     * @param sids 需要删除的数据对象类别编码规则ID
     * @return 结果
     */
    public int deleteConDataobjectCodeRuleByIds(List<Long>  sids);

    /**
     * 启用/停用
     * @param conDataobjectCodeRule
     * @return
     */
    int changeStatus(ConDataobjectCodeRule conDataobjectCodeRule);

    /**
     * 更改确认状态
     * @param conDataobjectCodeRule
     * @return
     */
    int check(ConDataobjectCodeRule conDataobjectCodeRule);

    /**
     * 根据条件查询当前编码值
     * @param conDataobjectCodeRule ConDataobjectCodeRule
     * @return Integer
     */
    ConDataobjectCodeRule selectCurrentNumberByRule(ConDataobjectCodeRule conDataobjectCodeRule);

    /**
     * 修改当前编码值
     * @param entity ConDataobjectCodeRule
     * @return Integer
     */
    int addCurrentNumber(ConDataobjectCodeRule entity);
}