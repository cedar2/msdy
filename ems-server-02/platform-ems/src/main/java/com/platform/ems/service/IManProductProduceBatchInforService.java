package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ManProductProduceBatchInfor;

/**
 * 商品生产批次信息Service接口
 * 
 * @author chenkw
 * @date 2022-09-30
 */
public interface IManProductProduceBatchInforService extends IService<ManProductProduceBatchInfor>{
    /**
     * 查询商品生产批次信息
     * 
     * @param produceBatchInforSid 商品生产批次信息ID
     * @return 商品生产批次信息
     */
    ManProductProduceBatchInfor selectManProductProduceBatchInforById(Long produceBatchInforSid);

    /**
     * 查询商品生产批次信息列表
     * 
     * @param manProductProduceBatchInfor 商品生产批次信息
     * @return 商品生产批次信息集合
     */
    List<ManProductProduceBatchInfor> selectManProductProduceBatchInforList(ManProductProduceBatchInfor manProductProduceBatchInfor);

    /**
     * 新增商品生产批次信息
     * 
     * @param manProductProduceBatchInfor 商品生产批次信息
     * @return 结果
     */
    int insertManProductProduceBatchInfor(ManProductProduceBatchInfor manProductProduceBatchInfor);

    /**
     * 修改商品生产批次信息
     * 
     * @param manProductProduceBatchInfor 商品生产批次信息
     * @return 结果
     */
    int updateManProductProduceBatchInfor(ManProductProduceBatchInfor manProductProduceBatchInfor);

    /**
     * 变更商品生产批次信息
     *
     * @param manProductProduceBatchInfor 商品生产批次信息
     * @return 结果
     */
    int changeManProductProduceBatchInfor(ManProductProduceBatchInfor manProductProduceBatchInfor);

    /**
     * 批量删除商品生产批次信息
     * 
     * @param produceBatchInforSids 需要删除的商品生产批次信息ID
     * @return 结果
     */
    int deleteManProductProduceBatchInforByIds(List<Long>  produceBatchInforSids);

    /**
     * 更改确认状态
     * @param manProductProduceBatchInfor
     * @return
     */
    int check(ManProductProduceBatchInfor manProductProduceBatchInfor);

    /**
     * 维护实裁数
     * @param manProductProduceBatchInfor
     * @return
     */
    int preserveShicai(ManProductProduceBatchInfor manProductProduceBatchInfor);

}
