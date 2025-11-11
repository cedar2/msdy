package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManProductDefect;
import com.platform.ems.domain.dto.request.ManProductDefectRequest;

/**
 * 生产产品缺陷登记Service接口
 * 
 * @author zhuangyz
 * @date 2022-08-04
 */
public interface IManProductDefectService extends IService<ManProductDefect>{
    /**
     * 查询生产产品缺陷登记
     * 
     * @param productDefectSid 生产产品缺陷登记ID
     * @return 生产产品缺陷登记
     */
    public ManProductDefect selectManProductDefectById(Long productDefectSid);

    /**
     * 查询生产产品缺陷登记列表
     * 
     * @param manProductDefect 生产产品缺陷登记
     * @return 生产产品缺陷登记集合
     */
    public List<ManProductDefect> selectManProductDefectList(ManProductDefect manProductDefect);
    //设置值
    public int updateStatus(ManProductDefectRequest request);
    /**
     * 新增生产产品缺陷登记
     * 
     * @param manProductDefect 生产产品缺陷登记
     * @return 结果
     */
    public int insertManProductDefect(ManProductDefect manProductDefect);

    /**
     * 修改生产产品缺陷登记
     * 
     * @param manProductDefect 生产产品缺陷登记
     * @return 结果
     */
    public int updateManProductDefect(ManProductDefect manProductDefect);

    /**
     * 变更生产产品缺陷登记
     *
     * @param manProductDefect 生产产品缺陷登记
     * @return 结果
     */
    public int changeManProductDefect(ManProductDefect manProductDefect);

    /**
     * 批量删除生产产品缺陷登记
     * 
     * @param productDefectSids 需要删除的生产产品缺陷登记ID
     * @return 结果
     */
    public int deleteManProductDefectByIds(List<Long> productDefectSids);

    /**
    * 启用/停用
    * @param manProductDefect
    * @return
    */
    int changeStatus(ManProductDefect manProductDefect);

    /**
     * 更改确认状态
     * @param manProductDefect
     * @return
     */
    int check(ManProductDefect manProductDefect);

}
