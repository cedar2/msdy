package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasVendorTag;

import java.util.List;

/**
 * 供应商标签(分组)Service接口
 *
 * @author c
 * @date 2022-03-30
 */
public interface IBasVendorTagService extends IService<BasVendorTag> {
    /**
     * 查询供应商标签(分组)
     *
     * @param vendorTagSid 供应商标签(分组)ID
     * @return 供应商标签(分组)
     */
    public BasVendorTag selectBasVendorTagById(Long vendorTagSid);

    /**
     * 查询供应商标签(分组)列表
     *
     * @param basVendorTag 供应商标签(分组)
     * @return 供应商标签(分组)集合
     */
    public List<BasVendorTag> selectBasVendorTagList(BasVendorTag basVendorTag);

    /**
     * 新增供应商标签(分组)
     *
     * @param basVendorTag 供应商标签(分组)
     * @return 结果
     */
    public int insertBasVendorTag(BasVendorTag basVendorTag);

    /**
     * 修改供应商标签(分组)
     *
     * @param basVendorTag 供应商标签(分组)
     * @return 结果
     */
    public int updateBasVendorTag(BasVendorTag basVendorTag);

    /**
     * 变更供应商标签(分组)
     *
     * @param basVendorTag 供应商标签(分组)
     * @return 结果
     */
    public int changeBasVendorTag(BasVendorTag basVendorTag);

    /**
     * 批量删除供应商标签(分组)
     *
     * @param vendorTagSids 需要删除的供应商标签(分组)ID
     * @return 结果
     */
    public int deleteBasVendorTagByIds(List<Long> vendorTagSids);

    /**
     * 启用/停用
     *
     * @param basVendorTag
     * @return
     */
    int changeStatus(BasVendorTag basVendorTag);

    /**
     * 更改确认状态
     *
     * @param basVendorTag
     * @return
     */
    int check(BasVendorTag basVendorTag);

}
