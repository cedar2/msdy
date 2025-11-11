package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManProductDefectAttach;

/**
 * 生产产品缺陷登记-附件Service接口
 * 
 * @author zhuangyz
 * @date 2022-08-04
 */
public interface IManProductDefectAttachService extends IService<ManProductDefectAttach>{
    /**
     * 查询生产产品缺陷登记-附件
     * 
     * @param attachSid 生产产品缺陷登记-附件ID
     * @return 生产产品缺陷登记-附件
     */
    public ManProductDefectAttach selectManProductDefectAttachById(Long attachSid);

    /**
     * 查询生产产品缺陷登记-附件列表
     * 
     * @param manProductDefectAttach 生产产品缺陷登记-附件
     * @return 生产产品缺陷登记-附件集合
     */
    public List<ManProductDefectAttach> selectManProductDefectAttachList(ManProductDefectAttach manProductDefectAttach);

    /**
     * 新增生产产品缺陷登记-附件
     * 
     * @param manProductDefectAttach 生产产品缺陷登记-附件
     * @return 结果
     */
    public int insertManProductDefectAttach(ManProductDefectAttach manProductDefectAttach);

    /**
     * 修改生产产品缺陷登记-附件
     * 
     * @param manProductDefectAttach 生产产品缺陷登记-附件
     * @return 结果
     */
    public int updateManProductDefectAttach(ManProductDefectAttach manProductDefectAttach);

    /**
     * 变更生产产品缺陷登记-附件
     *
     * @param manProductDefectAttach 生产产品缺陷登记-附件
     * @return 结果
     */
    public int changeManProductDefectAttach(ManProductDefectAttach manProductDefectAttach);

    /**
     * 批量删除生产产品缺陷登记-附件
     * 
     * @param attachSids 需要删除的生产产品缺陷登记-附件ID
     * @return 结果
     */
    public int deleteManProductDefectAttachByIds(List<Long> attachSids);

    /**
    * 启用/停用
    * @param manProductDefectAttach
    * @return
    */
    int changeStatus(ManProductDefectAttach manProductDefectAttach);

    /**
     * 更改确认状态
     * @param manProductDefectAttach
     * @return
     */
    int check(ManProductDefectAttach manProductDefectAttach);

}
