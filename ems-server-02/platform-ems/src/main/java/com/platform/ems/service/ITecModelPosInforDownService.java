package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecModelPosInforDown;

/**
 * 版型-部位信息（下装）Service接口
 * 
 * @author linhongwei
 * @date 2021-04-25
 */
public interface ITecModelPosInforDownService extends IService<TecModelPosInforDown>{
    /**
     * 查询版型-部位信息（下装）
     * 
     * @param modelPositionInforSid 版型-部位信息（下装）ID
     * @return 版型-部位信息（下装）
     */
    public TecModelPosInforDown selectTecModelPosInforDownById(Long modelPositionInforSid);

    /**
     * 查询版型-部位信息（下装）列表
     * 
     * @param tecModelPosInforDown 版型-部位信息（下装）
     * @return 版型-部位信息（下装）集合
     */
    public List<TecModelPosInforDown> selectTecModelPosInforDownList(TecModelPosInforDown tecModelPosInforDown);

    /**
     * 新增版型-部位信息（下装）
     * 
     * @param tecModelPosInforDown 版型-部位信息（下装）
     * @return 结果
     */
    public int insertTecModelPosInforDown(TecModelPosInforDown tecModelPosInforDown);

    /**
     * 修改版型-部位信息（下装）
     * 
     * @param tecModelPosInforDown 版型-部位信息（下装）
     * @return 结果
     */
    public int updateTecModelPosInforDown(TecModelPosInforDown tecModelPosInforDown);

    /**
     * 批量删除版型-部位信息（下装）
     * 
     * @param modelPositionInforSids 需要删除的版型-部位信息（下装）ID
     * @return 结果
     */
    public int deleteTecModelPosInforDownByIds(List<Long> modelPositionInforSids);

}
