package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.CosRecordProductActualCost;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品实际成本台账表Service接口
 *
 * @author chenkw
 * @date 2023-04-27
 */
public interface ICosRecordProductActualCostService extends IService<CosRecordProductActualCost> {
    /**
     * 查询商品实际成本台账表
     *
     * @param recordCostSid 商品实际成本台账表ID
     * @return 商品实际成本台账表
     */
    public CosRecordProductActualCost selectCosRecordProductActualCostById(Long recordCostSid);

    /**
     * 查询商品实际成本台账表列表
     *
     * @param cosRecordProductActualCost 商品实际成本台账表
     * @return 商品实际成本台账表集合
     */
    public List<CosRecordProductActualCost> selectCosRecordProductActualCostList(CosRecordProductActualCost cosRecordProductActualCost);

    /**
     * 新增商品实际成本台账表
     *
     * @param cosRecordProductActualCost 商品实际成本台账表
     * @return 结果
     */
    public int insertCosRecordProductActualCost(CosRecordProductActualCost cosRecordProductActualCost);

    /**
     * 变更商品实际成本台账表
     *
     * @param cosRecordProductActualCost 商品实际成本台账表
     * @return 结果
     */
    public int changeCosRecordProductActualCost(CosRecordProductActualCost cosRecordProductActualCost);

    /**
     * 批量删除商品实际成本台账表
     *
     * @param recordCostSids 需要删除的商品实际成本台账表ID
     * @return 结果
     */
    public int deleteCosRecordProductActualCostByIds(List<Long> recordCostSids);

    /**
     * 导入
     * @param file 文件
     * @return 返回
     */
    EmsResultEntity importActualCost(MultipartFile file);

}
