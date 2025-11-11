package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasShopAttach;

import java.util.List;

/**
 * 店铺-附件Service接口
 *
 * @author c
 * @date 2022-03-31
 */
public interface IBasShopAttachService extends IService<BasShopAttach> {
    /**
     * 查询店铺-附件
     *
     * @param attachmentSid 店铺-附件ID
     * @return 店铺-附件
     */
    public BasShopAttach selectBasShopAttachById(Long attachmentSid);

    /**
     * 查询店铺-附件列表
     *
     * @param basShopAttach 店铺-附件
     * @return 店铺-附件集合
     */
    public List<BasShopAttach> selectBasShopAttachList(BasShopAttach basShopAttach);

    /**
     * 新增店铺-附件
     *
     * @param basShopAttach 店铺-附件
     * @return 结果
     */
    public int insertBasShopAttach(BasShopAttach basShopAttach);

    /**
     * 修改店铺-附件
     *
     * @param basShopAttach 店铺-附件
     * @return 结果
     */
    public int updateBasShopAttach(BasShopAttach basShopAttach);

    /**
     * 变更店铺-附件
     *
     * @param basShopAttach 店铺-附件
     * @return 结果
     */
    public int changeBasShopAttach(BasShopAttach basShopAttach);

    /**
     * 批量删除店铺-附件
     *
     * @param attachmentSids 需要删除的店铺-附件ID
     * @return 结果
     */
    public int deleteBasShopAttachByIds(List<Long> attachmentSids);

}
