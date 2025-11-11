package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasSeasonVendor;

/**
 * 季度供应商Service接口
 *
 * @author chenkw
 * @date 2023-04-13
 */
public interface IBasSeasonVendorService extends IService<BasSeasonVendor> {
    /**
     * 查询季度供应商
     *
     * @param seasonVendorSid 季度供应商ID
     * @return 季度供应商
     */
    public BasSeasonVendor selectBasSeasonVendorById(Long seasonVendorSid);

    /**
     * 查询季度供应商列表
     *
     * @param basSeasonVendor 季度供应商
     * @return 季度供应商集合
     */
    public List<BasSeasonVendor> selectBasSeasonVendorList(BasSeasonVendor basSeasonVendor);

    /**
     * 新增季度供应商
     *
     * @param basSeasonVendor 季度供应商
     * @return 结果
     */
    public int insertBasSeasonVendor(BasSeasonVendor basSeasonVendor);

    /**
     * 修改季度供应商
     *
     * @param basSeasonVendor 季度供应商
     * @return 结果
     */
    public int updateBasSeasonVendor(BasSeasonVendor basSeasonVendor);

    /**
     * 变更季度供应商
     *
     * @param basSeasonVendor 季度供应商
     * @return 结果
     */
    public int changeBasSeasonVendor(BasSeasonVendor basSeasonVendor);

    /**
     * 批量删除季度供应商
     *
     * @param seasonVendorSids 需要删除的季度供应商ID
     * @return 结果
     */
    public int deleteBasSeasonVendorByIds(List<Long> seasonVendorSids);

    /**
     * 启用/停用
     *
     * @param basSeasonVendor
     * @return
     */
    int changeStatus(BasSeasonVendor basSeasonVendor);

    /**
     * 更改确认状态
     *
     * @param basSeasonVendor
     * @return
     */
    int check(BasSeasonVendor basSeasonVendor);

    /**
     * 设置是否快反供应商
     *
     * @param basSeasonVendor
     * @return
     */
    int setKuaiFan(BasSeasonVendor basSeasonVendor);

}
