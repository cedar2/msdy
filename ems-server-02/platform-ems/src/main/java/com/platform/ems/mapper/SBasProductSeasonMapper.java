package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.SBasProductSeason;
import com.platform.ems.domain.dto.request.EditHandleStatusRequest;
import com.platform.ems.domain.dto.request.ListSeasonRequest;
import com.platform.ems.domain.dto.response.ExportSeasonResponse;
import com.platform.ems.domain.dto.response.ListSeasonResponse;
import org.apache.ibatis.annotations.Param;

import java.util.HashSet;
import java.util.List;


/**
 * 产品季档案Mapper接口
 *
 * @author shakeflags
 * @date 2021-01-21
 */
public interface SBasProductSeasonMapper extends BaseMapper<SBasProductSeason>
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
     * 删除产品季档案
     *
     * @param productSeasonSid 产品季档案ID
     * @return 结果
     */
//    public int deleteSBasProductSeasonById(String productSeasonSid);

    /**
     * 批量删除产品季档案
     *
     * @param productSeasonSid 需要删除的数据ID
     * @return 结果
     */
    public int deleteSBasProductSeasonByIds(String productSeasonSid);

    /**
     * 更新产品季档案处理状态
     *
     * @param editHandleStatusRequest 需要删除的数据ID
     * @return 结果
     */
    int updateHandleStatus(EditHandleStatusRequest editHandleStatusRequest);

    /**
     * 更新产品季档案启用/停用状态
     *
     * @param productSeasonSid 需要删除的数据ID
     * @param status            启用/停用状态
     * @return 结果
     */
    int updateValidStatus(@Param("productSeasonSid") String productSeasonSid, @Param("status") String status);

    /**
     * 获取表中所有数据
     * @return 结果
     */
    List<ExportSeasonResponse> getAllList();

    /**
     * 查询季档案启用/停用状态
     * @param productSeasonSid 产品季档案SID
     * @return 结果
     */
    HashSet<String> selectHandleStatusById(String productSeasonSid);

    List<ListSeasonResponse> selectSBasProductSeasonByIds(String productSeasonSid);

    /**
     * 产品季档案下拉框列表
     */
    List<SBasProductSeason> getProductSeasonList();
}
