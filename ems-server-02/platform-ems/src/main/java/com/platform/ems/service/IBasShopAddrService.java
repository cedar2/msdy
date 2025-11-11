package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasShopAddr;

import java.util.List;

/**
 * 店铺-联系方式信息Service接口
 *
 * @author c
 * @date 2022-03-31
 */
public interface IBasShopAddrService extends IService<BasShopAddr> {
    /**
     * 查询店铺-联系方式信息
     *
     * @param shopContactSid 店铺-联系方式信息ID
     * @return 店铺-联系方式信息
     */
    public BasShopAddr selectBasShopAddrById(Long shopContactSid);

    /**
     * 查询店铺-联系方式信息列表
     *
     * @param basShopAddr 店铺-联系方式信息
     * @return 店铺-联系方式信息集合
     */
    public List<BasShopAddr> selectBasShopAddrList(BasShopAddr basShopAddr);

    /**
     * 新增店铺-联系方式信息
     *
     * @param basShopAddr 店铺-联系方式信息
     * @return 结果
     */
    public int insertBasShopAddr(BasShopAddr basShopAddr);

    /**
     * 修改店铺-联系方式信息
     *
     * @param basShopAddr 店铺-联系方式信息
     * @return 结果
     */
    public int updateBasShopAddr(BasShopAddr basShopAddr);

    /**
     * 变更店铺-联系方式信息
     *
     * @param basShopAddr 店铺-联系方式信息
     * @return 结果
     */
    public int changeBasShopAddr(BasShopAddr basShopAddr);

    /**
     * 批量删除店铺-联系方式信息
     *
     * @param shopContactSids 需要删除的店铺-联系方式信息ID
     * @return 结果
     */
    public int deleteBasShopAddrByIds(List<Long> shopContactSids);

}
