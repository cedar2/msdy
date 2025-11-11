package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasShop;

import java.util.List;

/**
 * 店铺档案Service接口
 *
 * @author c
 * @date 2022-03-31
 */
public interface IBasShopService extends IService<BasShop> {
    /**
     * 查询店铺档案
     *
     * @param shopSid 店铺档案ID
     * @return 店铺档案
     */
    public BasShop selectBasShopById(Long shopSid);

    /**
     * 查询店铺档案列表
     *
     * @param basShop 店铺档案
     * @return 店铺档案集合
     */
    public List<BasShop> selectBasShopList(BasShop basShop);

    /**
     * 新增店铺档案
     *
     * @param basShop 店铺档案
     * @return 结果
     */
    public int insertBasShop(BasShop basShop);

    /**
     * 修改店铺档案
     *
     * @param basShop 店铺档案
     * @return 结果
     */
    public int updateBasShop(BasShop basShop);

    /**
     * 变更店铺档案
     *
     * @param basShop 店铺档案
     * @return 结果
     */
    public int changeBasShop(BasShop basShop);

    /**
     * 批量删除店铺档案
     *
     * @param shopSids 需要删除的店铺档案ID
     * @return 结果
     */
    public int deleteBasShopByIds(List<Long> shopSids);

    /**
     * 启用/停用
     *
     * @param basShop
     * @return
     */
    int changeStatus(BasShop basShop);

    /**
     * 更改确认状态
     *
     * @param basShop
     * @return
     */
    int check(BasShop basShop);

}
