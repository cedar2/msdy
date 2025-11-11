package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.AssAssetRecord;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 资产台账Service接口
 *
 * @author chenkw
 * @date 2022-03-01
 */
public interface IAssAssetRecordService extends IService<AssAssetRecord> {
    /**
     * 查询资产台账
     *
     * @param assetSid 资产台账ID
     * @return 资产台账
     */
    public AssAssetRecord selectAssAssetRecordById(Long assetSid);

    /**
     * 查询资产台账列表
     *
     * @param assAssetRecord 资产台账
     * @return 资产台账集合
     */
    public List<AssAssetRecord> selectAssAssetStatisticalRecordList(AssAssetRecord assAssetRecord);


    /**
     * 查询资产台统计账明细
     *
     * @param assAssetRecord 资产台账
     * @return 资产台账集合
     */
    public List<AssAssetRecord> selectAssAssetStatisticalRecordListDetail(AssAssetRecord assAssetRecord);


    /**
     * 查询资产统计台账列表
     *
     * @param assAssetRecord 资产台账
     * @return 资产台账集合
     */
    public List<AssAssetRecord> selectAssAssetRecordList(AssAssetRecord assAssetRecord);

    /**
     * 新增资产台账
     *
     * @param assAssetRecord 资产台账
     * @return 结果
     */
    public int insertAssAssetRecord(AssAssetRecord assAssetRecord);

    /**
     * 修改资产台账
     *
     * @param assAssetRecord 资产台账
     * @return 结果
     */
    public int updateAssAssetRecord(AssAssetRecord assAssetRecord);

    /**
     * 变更资产台账
     *
     * @param assAssetRecord 资产台账
     * @return 结果
     */
    public int changeAssAssetRecord(AssAssetRecord assAssetRecord);

    /**
     * 批量删除资产台账
     *
     * @param assetSids 需要删除的资产台账ID
     * @return 结果
     */
    public int deleteAssAssetRecordByIds(List<Long> assetSids);

    /**
     * 更改确认状态
     *
     * @param assAssetRecord
     * @return
     */
    int check(AssAssetRecord assAssetRecord);

    /**
     * 作废
     */
    int cancel(AssAssetRecord assAssetRecord);

    Object importData(MultipartFile file);

    void exportAssetCardList(HttpServletResponse response, AssAssetRecord assAssetRecord);
}
