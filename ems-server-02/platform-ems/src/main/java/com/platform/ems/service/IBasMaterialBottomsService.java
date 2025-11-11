package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.BasMaterialBottoms;
import com.platform.ems.domain.dto.request.MaterialBottomsActionRequest;
import com.platform.ems.domain.dto.request.MaterialBottomsListRequest;
import com.platform.ems.domain.dto.response.MaterialBottomsListResponse;

import java.util.List;

/**
 * 商品-上下装尺码对照Service接口
 *
 * @author linhongwei
 * @date 2021-03-14
 */
public interface IBasMaterialBottomsService extends IService<BasMaterialBottoms>{
    /**
     * 查询商品-上下装尺码对照
     *
     * @param bottomsSkuSid 商品-上下装尺码对照ID
     * @return 商品-上下装尺码对照
     */
    public BasMaterialBottoms selectBasMaterialBottomsById(Long bottomsSkuSid);

    /**
     * 查询商品-上下装尺码对照列表
     *
     * @param masMaterialBottoms 商品-上下装尺码对照
     * @return 商品-上下装尺码对照集合
     */
    public List<BasMaterialBottoms> selectBasMaterialBottomsList(BasMaterialBottoms masMaterialBottoms);

    /**
     * 新增商品-上下装尺码对照
     *
     * @param basMaterialBottoms 商品-上下装尺码对照
     * @return 结果
     */
    public int insertBasMaterialBottoms(BasMaterialBottoms basMaterialBottoms);

    /**
     * 修改商品-上下装尺码对照
     *
     * @param basMaterialBottoms 商品-上下装尺码对照
     * @return 结果
     */
    public AjaxResult updateBasMaterialBottoms(BasMaterialBottoms basMaterialBottoms);

    /**
     * 修改商品-上下装尺码对照
     *
     * @param basMaterialBottoms 商品-上下装尺码对照
     * @return 结果
     */
    public AjaxResult changeBasMaterialBottoms(BasMaterialBottoms basMaterialBottoms);
    /**
     * 批量删除商品-上下装尺码对照
     *
     * @param bottomsSkuSids 需要删除的商品-上下装尺码对照ID
     * @return 结果
     */
    public AjaxResult deleteBasMaterialBottomsByIds(List<Long> bottomsSkuSids);

    /**
     * 启用/停用-上下装尺码对照
     *
     * @param materialBottomsActionRequest
     * @return 结果
     */

    public AjaxResult updateValidStatus(MaterialBottomsActionRequest materialBottomsActionRequest);

    /**
     *确认-上下装尺码对照
     *
     * @param materialBottomsActionRequest
     * @return 结果
     */
    public AjaxResult confirm(MaterialBottomsActionRequest materialBottomsActionRequest);

}
