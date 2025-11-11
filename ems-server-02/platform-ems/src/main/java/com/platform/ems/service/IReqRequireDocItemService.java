package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ReqRequireDocItem;

/**
 * 需求单明细Service接口
 * 
 * @author linhongwei
 * @date 2021-04-02
 */
public interface IReqRequireDocItemService extends IService<ReqRequireDocItem>{
    /**
     * 查询需求单明细
     * 
     * @param requireDocItemSid 需求单明细ID
     * @return 需求单明细
     */
    public ReqRequireDocItem selectReqRequireDocItemById(Long requireDocItemSid);

    /**
     * 查询需求单明细列表
     * 
     * @param reqRequireDocItem 需求单明细
     * @return 需求单明细集合
     */
    public List<ReqRequireDocItem> selectReqRequireDocItemList(ReqRequireDocItem reqRequireDocItem);

    /**
     * 新增需求单明细
     * 
     * @param reqRequireDocItem 需求单明细
     * @return 结果
     */
    public int insertReqRequireDocItem(ReqRequireDocItem reqRequireDocItem);

    /**
     * 修改需求单明细
     * 
     * @param reqRequireDocItem 需求单明细
     * @return 结果
     */
    public int updateReqRequireDocItem(ReqRequireDocItem reqRequireDocItem);

    /**
     * 批量删除需求单明细
     * 
     * @param requireDocItemSids 需要删除的需求单明细ID
     * @return 结果
     */
    public int deleteReqRequireDocItemByIds(List<Long> requireDocItemSids);

}
