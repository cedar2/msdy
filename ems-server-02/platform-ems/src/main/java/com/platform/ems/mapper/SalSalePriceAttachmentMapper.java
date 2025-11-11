package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.SalSalePriceAttachment;
import com.platform.ems.domain.SalSalePriceItem;

import java.util.List;

/**
 * undefinedMapper接口
 * 
 * @author linhongwei
 * @date 2021-03-05
 */
public interface SalSalePriceAttachmentMapper  extends BaseMapper<SalSalePriceAttachment> {
    /**
     * 查询undefined
     * 
     * @param salePriceAttachmentSid undefinedID
     * @return undefined
     */
    public SalSalePriceAttachment selectSalSalePriceAttachmentById(Long salePriceAttachmentSid);

    /**
     * 查询undefined列表
     * 
     * @param salSalePriceAttachment undefined
     * @return undefined集合
     */
    public List<SalSalePriceAttachment> selectSalSalePriceAttachmentList(SalSalePriceAttachment salSalePriceAttachment);

    /**
     * 新增undefined
     * 
     * @param salSalePriceAttachment undefined
     * @return 结果
     */
    public int insertSalSalePriceAttachment(SalSalePriceAttachment salSalePriceAttachment);

    /**
     * 修改undefined
     * 
     * @param salSalePriceAttachment undefined
     * @return 结果
     */
    public int updateSalSalePriceAttachment(SalSalePriceAttachment salSalePriceAttachment);

    /**
     * 删除undefined
     * 
     * @param salePriceAttachmentSid undefinedID
     * @return 结果
     */
    public int deleteSalSalePriceAttachmentById(Long salePriceAttachmentSid);

    /**
     * 批量删除undefined
     * 
     * @param salePriceAttachmentSids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSalSalePriceAttachmentByIds(String[] salePriceAttachmentSids);
}
