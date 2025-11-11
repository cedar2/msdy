package com.platform.ems.service;

import com.platform.ems.domain.SBasProductSeason;
import com.platform.ems.domain.dto.request.EditHandleStatusRequest;
import com.platform.ems.domain.dto.request.ListSeasonRequest;
import com.platform.ems.domain.dto.response.ExportSeasonResponse;
import com.platform.ems.domain.dto.response.ListSeasonResponse;

import java.util.List;


/**
 * 产品季档案Service接口
 *
 * @author ruoyi
 * @date 2021-01-21
 */
public interface ISBasProductSeasonService
{
    /**
     * 查询产品季档案
     *
     * @param productSeasonSid 产品季档案ID
     * @return 产品季档案
     */
    public ListSeasonResponse selectSBasProductSeasonById(Long productSeasonSid);

    /**
     * 查询产品季档案列表
     *
     * @param listSeasonRequest 产品季档案
     * @return 产品季档案集合
     */
    public List<ListSeasonResponse> selectSBasProductSeasonList(ListSeasonRequest listSeasonRequest);

    /**
     * 新增产品季档案
     *
     * @param sBasProductSeason 产品季档案
     * @return 结果
     */
    public int insertSBasProductSeason(SBasProductSeason sBasProductSeason);

    /**
     * 修改产品季档案
     *
     * @param sBasProductSeason 产品季档案
     * @return 结果
     */
    public int updateSBasProductSeason(SBasProductSeason sBasProductSeason);

    /**
     * 批量删除产品季档案
     *
     * @param productSeasonSids 需要删除的产品季档案ID
     * @return 结果
     */
    public int deleteSBasProductSeasonByIds(String productSeasonSids);

    /**
     * 删除产品季档案信息
     *
     * @param productSeasonSids 产品季档案ID
     * @return 结果
     */
    public int deleteSBasProductSeasonById(String productSeasonSids);


    /**
     * 删除产品季档案信息
     *
     * @param editHandleStatusRequest 更新产品季档案处理状态
     * @return 结果
     */
    int updateHandleStatus(EditHandleStatusRequest editHandleStatusRequest);

    /**
     * 删除产品季档案信息
     *
     * @param productSeasonSid 产品季sid
     * @param validStatus  启用/停用状态
     * @return 结果
     */
    int updateValidStatus(String productSeasonSid, String validStatus);

    List<ExportSeasonResponse> getAllList();

    List<ListSeasonResponse> seasonExport(ListSeasonRequest listSeasonRequest, String productSeasonSid);

    int changeSBasProductSeason(SBasProductSeason sBasProductSeason);

    /**
     * 产品季档案下拉框列表
     */
    List<SBasProductSeason> getProductSeasonList();
}
