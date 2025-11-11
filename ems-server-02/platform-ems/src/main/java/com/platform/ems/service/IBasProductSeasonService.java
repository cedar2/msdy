package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasProductSeason;
import com.platform.ems.domain.BasSkuGroup;

/**
 * 产品季档案Service接口
 * 
 * @author linhongwei
 * @date 2021-03-22
 */
public interface IBasProductSeasonService extends IService<BasProductSeason>{
    /**
     * 查询产品季档案
     * 
     * @param productSeasonSid 产品季档案ID
     * @return 产品季档案
     */
    public BasProductSeason selectBasProductSeasonById(Long productSeasonSid);

    /**
     * 查询产品季档案列表
     * 
     * @param basProductSeason 产品季档案
     * @return 产品季档案集合
     */
    public List<BasProductSeason> selectBasProductSeasonList(BasProductSeason basProductSeason);

    /**
     * 下拉框
     * @param basProductSeason 过滤条件
     * @return 下拉列表
     */
    List<BasProductSeason> getList(BasProductSeason basProductSeason);

    /**
     * 新增产品季档案
     * 
     * @param basProductSeason 产品季档案
     * @return 结果
     */
    public int insertBasProductSeason(BasProductSeason basProductSeason);

    /**
     * 修改产品季档案
     * 
     * @param basProductSeason 产品季档案
     * @return 结果
     */
    public int updateBasProductSeason(BasProductSeason basProductSeason);

    public int changeBasProductSeason(BasProductSeason basProductSeason);

    /**
     * 批量删除产品季档案
     * 
     * @param clientIds 需要删除的产品季档案ID
     * @return 结果
     */
    public int deleteBasProductSeasonByIds(List<String> clientIds);

    int changeStatus(BasProductSeason basProductSeason);

    int check(BasProductSeason basProductSeason);

}
