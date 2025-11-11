package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorRecommendAttach;

/**
 * 供应商推荐-附件Service接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface IBasVendorRecommendAttachService extends IService<BasVendorRecommendAttach>{
    /**
     * 查询供应商推荐-附件
     * 
     * @param vendorRecommendAttachSid 供应商推荐-附件ID
     * @return 供应商推荐-附件
     */
    public BasVendorRecommendAttach selectBasVendorRecommendAttachById(Long vendorRecommendAttachSid);

    /**
     * 查询供应商推荐-附件列表
     * 
     * @param basVendorRecommendAttach 供应商推荐-附件
     * @return 供应商推荐-附件集合
     */
    public List<BasVendorRecommendAttach> selectBasVendorRecommendAttachList(BasVendorRecommendAttach basVendorRecommendAttach);

    /**
     * 新增供应商推荐-附件
     * 
     * @param basVendorRecommendAttach 供应商推荐-附件
     * @return 结果
     */
    public int insertBasVendorRecommendAttach(BasVendorRecommendAttach basVendorRecommendAttach);

    /**
     * 修改供应商推荐-附件
     * 
     * @param basVendorRecommendAttach 供应商推荐-附件
     * @return 结果
     */
    public int updateBasVendorRecommendAttach(BasVendorRecommendAttach basVendorRecommendAttach);

    /**
     * 变更供应商推荐-附件
     *
     * @param basVendorRecommendAttach 供应商推荐-附件
     * @return 结果
     */
    public int changeBasVendorRecommendAttach(BasVendorRecommendAttach basVendorRecommendAttach);

    /**
     * 批量删除供应商推荐-附件
     * 
     * @param vendorRecommendAttachSids 需要删除的供应商推荐-附件ID
     * @return 结果
     */
    public int deleteBasVendorRecommendAttachByIds(List<Long>  vendorRecommendAttachSids);


    /**
     * 查询主表下的附件清单信息
     *
     * @param vendorRecommendSid 供应商推荐ID
     * @return 供应商推荐-附件
     */
    public List<BasVendorRecommendAttach> selectBasVendorRecommendAttachListById(Long vendorRecommendSid);

    /**
     * 由主表批量新增供应商推荐-附件
     *
     * @param basVendorRecommendAttachList List 供应商推荐-附件
     * @return 结果
     */
    public int insertBasVendorRecommendAttach(List<BasVendorRecommendAttach> basVendorRecommendAttachList,Long vendorRecommendSid);

    /**
     * 批量修改供应商推荐-附件
     *
     * @param basVendorRecommendAttachList 供应商推荐-附件
     * @return 结果
     */
    public int updateBasVendorRecommendAttach(List<BasVendorRecommendAttach> basVendorRecommendAttachList);

    /**
     * 批量修改（删除/新建/更改）供应商推荐-附件
     *
     * @param list 供应商推荐-附件 (原先的)
     * @param request 供应商推荐-附件 (更新后)
     * @return 结果
     */
    public int updateBasVendorRecommendAttach(List<BasVendorRecommendAttach> list, List<BasVendorRecommendAttach> request,Long vendorRecommendSid);
}
