package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecProductLine;
import com.platform.ems.domain.TecProductLineposMat;
import com.platform.ems.domain.dto.request.EstimateLineReportRequest;
import com.platform.ems.domain.dto.response.EstimateLineReportResponse;

import java.util.List;

/**
 * 商品线Service接口
 *
 * @author linhongwei
 * @date 2021-10-21
 */
public interface ITecProductLineService extends IService<TecProductLine> {
    /**
     * 查询商品线
     *
     * @param materialSid 商品ID
     * @return 商品线
     */
    public TecProductLine selectTecProductLineById(Long materialSid);

    /**
     * 查询商品线列表
     *
     * @param tecProductLine 商品线
     * @return 商品线集合
     */
    public List<TecProductLine> selectTecProductLineList(TecProductLine tecProductLine);

    /**
     * 新增商品线
     *
     * @param tecProductLine 商品线
     * @return 结果
     */
    public int insertTecProductLine(TecProductLine tecProductLine);

    /**
     * 修改商品线
     *
     * @param tecProductLine 商品线
     * @return 结果
     */
    public int updateTecProductLine(TecProductLine tecProductLine);

    /**
     * 变更商品线
     *
     * @param tecProductLine 商品线
     * @return 结果
     */
    public int changeTecProductLine(TecProductLine tecProductLine);

    /**
     * 批量删除商品线
     *
     * @param productLineSids 需要删除的商品线ID
     * @return 结果
     */
    public int deleteTecProductLineByIds(List<Long> productLineSids);

    /**
     * 更改确认状态
     *
     * @param tecProductLine
     * @return
     */
    int check(TecProductLine tecProductLine);
    /**
     * 物料需求报表 线用量
     */
    public List<EstimateLineReportResponse> getEstLine(List<EstimateLineReportRequest> requestList);
    /**
     * 添加线部位时校验名称是否重复
     */
    TecProductLineposMat verifyPosition(TecProductLineposMat tecProductLineposMat);
}
