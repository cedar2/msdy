package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasCustomerTag;

import java.util.List;

/**
 * 客户标签(分组)Service接口
 *
 * @author c
 * @date 2022-03-30
 */
public interface IBasCustomerTagService extends IService<BasCustomerTag> {
    /**
     * 查询客户标签(分组)
     *
     * @param customerTagSid 客户标签(分组)ID
     * @return 客户标签(分组)
     */
    public BasCustomerTag selectBasCustomerTagById(Long customerTagSid);

    /**
     * 查询客户标签(分组)列表
     *
     * @param basCustomerTag 客户标签(分组)
     * @return 客户标签(分组)集合
     */
    public List<BasCustomerTag> selectBasCustomerTagList(BasCustomerTag basCustomerTag);

    /**
     * 新增客户标签(分组)
     *
     * @param basCustomerTag 客户标签(分组)
     * @return 结果
     */
    public int insertBasCustomerTag(BasCustomerTag basCustomerTag);

    /**
     * 修改客户标签(分组)
     *
     * @param basCustomerTag 客户标签(分组)
     * @return 结果
     */
    public int updateBasCustomerTag(BasCustomerTag basCustomerTag);

    /**
     * 变更客户标签(分组)
     *
     * @param basCustomerTag 客户标签(分组)
     * @return 结果
     */
    public int changeBasCustomerTag(BasCustomerTag basCustomerTag);

    /**
     * 批量删除客户标签(分组)
     *
     * @param customerTagSids 需要删除的客户标签(分组)ID
     * @return 结果
     */
    public int deleteBasCustomerTagByIds(List<Long> customerTagSids);

    /**
     * 启用/停用
     *
     * @param basCustomerTag
     * @return
     */
    int changeStatus(BasCustomerTag basCustomerTag);

    /**
     * 更改确认状态
     *
     * @param basCustomerTag
     * @return
     */
    int check(BasCustomerTag basCustomerTag);

}
