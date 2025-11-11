package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecModelPosSizeDown;

/**
 * 版型-部位-尺码-尺寸（下装）Service接口
 * 
 * @author linhongwei
 * @date 2021-04-25
 */
public interface ITecModelPosSizeDownService extends IService<TecModelPosSizeDown>{
    /**
     * 查询版型-部位-尺码-尺寸（下装）
     * 
     * @param modelPositionSizeSid 版型-部位-尺码-尺寸（下装）ID
     * @return 版型-部位-尺码-尺寸（下装）
     */
    public TecModelPosSizeDown selectTecModelPosSizeDownById(Long modelPositionSizeSid);

    /**
     * 查询版型-部位-尺码-尺寸（下装）列表
     * 
     * @param tecModelPosSizeDown 版型-部位-尺码-尺寸（下装）
     * @return 版型-部位-尺码-尺寸（下装）集合
     */
    public List<TecModelPosSizeDown> selectTecModelPosSizeDownList(TecModelPosSizeDown tecModelPosSizeDown);

    /**
     * 新增版型-部位-尺码-尺寸（下装）
     * 
     * @param tecModelPosSizeDown 版型-部位-尺码-尺寸（下装）
     * @return 结果
     */
    public int insertTecModelPosSizeDown(TecModelPosSizeDown tecModelPosSizeDown);

    /**
     * 修改版型-部位-尺码-尺寸（下装）
     * 
     * @param tecModelPosSizeDown 版型-部位-尺码-尺寸（下装）
     * @return 结果
     */
    public int updateTecModelPosSizeDown(TecModelPosSizeDown tecModelPosSizeDown);

    /**
     * 批量删除版型-部位-尺码-尺寸（下装）
     * 
     * @param modelPositionSizeSids 需要删除的版型-部位-尺码-尺寸（下装）ID
     * @return 结果
     */
    public int deleteTecModelPosSizeDownByIds(List<Long> modelPositionSizeSids);

}
