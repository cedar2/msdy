package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecProductSizeZipperLength;

/**
 * 商品尺码拉链长度明细Service接口
 * 
 * @author c
 * @date 2021-08-03
 */
public interface ITecProductSizeZipperLengthService extends IService<TecProductSizeZipperLength>{
    /**
     * 查询商品尺码拉链长度明细
     * 
     * @param productZipperSid 商品尺码拉链长度明细ID
     * @return 商品尺码拉链长度明细
     */
    public TecProductSizeZipperLength selectTecProductSizeZipperLengthById(Long productZipperSid);

    /**
     * 查询商品尺码拉链长度明细列表
     * 
     * @param tecProductSizeZipperLength 商品尺码拉链长度明细
     * @return 商品尺码拉链长度明细集合
     */
    public List<TecProductSizeZipperLength> selectTecProductSizeZipperLengthList(TecProductSizeZipperLength tecProductSizeZipperLength);

    /**
     * 新增商品尺码拉链长度明细
     * 
     * @param tecProductSizeZipperLength 商品尺码拉链长度明细
     * @return 结果
     */
    public int insertTecProductSizeZipperLength(TecProductSizeZipperLength tecProductSizeZipperLength);

    /**
     * 修改商品尺码拉链长度明细
     * 
     * @param tecProductSizeZipperLength 商品尺码拉链长度明细
     * @return 结果
     */
    public int updateTecProductSizeZipperLength(TecProductSizeZipperLength tecProductSizeZipperLength);

    /**
     * 变更商品尺码拉链长度明细
     *
     * @param tecProductSizeZipperLength 商品尺码拉链长度明细
     * @return 结果
     */
    public int changeTecProductSizeZipperLength(TecProductSizeZipperLength tecProductSizeZipperLength);

    /**
     * 批量删除商品尺码拉链长度明细
     * 
     * @param productZipperSids 需要删除的商品尺码拉链长度明细ID
     * @return 结果
     */
    public int deleteTecProductSizeZipperLengthByIds(List<Long> productZipperSids);

    /**
     * 更改确认状态
     * @param tecProductSizeZipperLength
     * @return
     */
    int check(TecProductSizeZipperLength tecProductSizeZipperLength);

}
