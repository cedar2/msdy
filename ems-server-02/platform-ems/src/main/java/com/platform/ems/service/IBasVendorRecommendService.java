package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorRecommend;

/**
 * 供应商推荐-基础Service接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface IBasVendorRecommendService extends IService<BasVendorRecommend>{
    /**
     * 查询供应商推荐-基础
     * 
     * @param vendorRecommendSid 供应商推荐-基础ID
     * @return 供应商推荐-基础
     */
    public BasVendorRecommend selectBasVendorRecommendById(Long vendorRecommendSid);

    /**
     * 查询供应商推荐-基础列表
     * 
     * @param basVendorRecommend 供应商推荐-基础
     * @return 供应商推荐-基础集合
     */
    public List<BasVendorRecommend> selectBasVendorRecommendList(BasVendorRecommend basVendorRecommend);

    /**
     * 新增供应商推荐-基础
     * 
     * @param basVendorRecommend 供应商推荐-基础
     * @return 结果
     */
    public int insertBasVendorRecommend(BasVendorRecommend basVendorRecommend);

    /**
     * 修改供应商推荐-基础
     * 
     * @param basVendorRecommend 供应商推荐-基础
     * @return 结果
     */
    public int updateBasVendorRecommend(BasVendorRecommend basVendorRecommend);

    /**
     * 变更供应商推荐-基础
     *
     * @param basVendorRecommend 供应商推荐-基础
     * @return 结果
     */
    public int changeBasVendorRecommend(BasVendorRecommend basVendorRecommend);

    /**
     * 批量删除供应商推荐-基础
     * 
     * @param vendorRecommendSids 需要删除的供应商推荐-基础ID
     * @return 结果
     */
    public int deleteBasVendorRecommendByIds(List<Long>  vendorRecommendSids);

    /**
     * 更改确认状态
     * @param basVendorRecommend
     * @return
     */
    int check(BasVendorRecommend basVendorRecommend);

}
