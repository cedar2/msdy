package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecProductZipper;
import com.platform.ems.domain.dto.request.TecProductZipperAddListRequest;
import com.platform.ems.domain.dto.response.TecProductZipperInfoResponse;

/**
 * 商品所用拉链Service接口
 * 
 * @author c
 * @date 2021-08-03
 */
public interface ITecProductZipperService extends IService<TecProductZipper>{
    /**
     * 查询商品所用拉链
     * 
     * @param bomMaterialCode, materialSids 商品所用拉链ID
     * @return 商品所用拉链
     */
    public TecProductZipper selectTecProductZipperById(String bomMaterialCode, List<Long> materialSids);

    /**
     * 查询商品所用拉链列表
     * 
     * @param tecProductZipper 商品所用拉链
     * @return 商品所用拉链集合
     */
    public List<TecProductZipper> selectTecProductZipperList(TecProductZipper tecProductZipper);

    /**
     * 新增商品所用拉链
     * 
     * @param request 商品所用拉链
     * @return 结果
     */
    public int insertTecProductZipper(TecProductZipper request);

    /**
     * 修改商品所用拉链
     * 
     * @param tecProductZipper 商品所用拉链
     * @return 结果
     */
    public int updateTecProductZipper(TecProductZipper tecProductZipper);

    /**
     * 变更商品所用拉链
     *
     * @param tecProductZipper 商品所用拉链
     * @return 结果
     */
    public int changeTecProductZipper(TecProductZipper tecProductZipper);

    /**
     * 批量删除商品所用拉链
     * 
     * @param productZipperSids 需要删除的商品所用拉链ID
     * @return 结果
     */
    public int deleteTecProductZipperByIds(List<Long> productZipperSids);

    /**
     * 更改确认状态
     * @param tecProductZipper
     * @return
     */
    int check(TecProductZipper tecProductZipper);

}
