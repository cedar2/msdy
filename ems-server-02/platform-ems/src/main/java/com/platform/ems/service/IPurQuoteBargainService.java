package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurQuoteBargain;
import com.platform.ems.domain.PurQuoteBargainItem;
import com.platform.ems.domain.dto.request.PurQuoteBargainRequest;
import com.platform.ems.domain.dto.response.PurQuoteBargainReportResponse;
import com.platform.ems.domain.dto.response.PurQuoteBargainResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 报议价单主(报价/核价/议价)Service接口
 *
 * @author linhongwei
 * @date 2021-04-26
 */
public interface IPurQuoteBargainService extends IService<PurQuoteBargain> {
    /**
     * 查询报议价单主(报价/核价/议价)
     *
     * @param requestQuotationSid 报议价单主(报价/核价/议价)ID
     * @return 报议价单主( 报价 / 核价 / 议价)
     */
    public PurQuoteBargain selectPurRequestQuotationById(Long requestQuotationSid);

    /**
     * 查询
     * 采购报核议价报表
     *
     * @param purQuoteBargainReportResponse
     * @return 报议价单主( 报价 / 核价 / 议价)
     */
    public List<PurQuoteBargainReportResponse> report(PurQuoteBargainReportResponse purQuoteBargainReportResponse);

    public List<PurQuoteBargainResponse> getReport(PurQuoteBargainRequest request);

    /**
     * 查询报议价单主(报价/核价/议价)列表
     *
     * @param purQuoteBargain 报议价单主(报价/核价/议价)
     * @return 报议价单主( 报价 / 核价 / 议价)集合
     */
    public List<PurQuoteBargain> selectPurRequestQuotationList(PurQuoteBargain purQuoteBargain);

    /**
     * 新增报议价单主(报价/核价/议价)
     *
     * @param purQuoteBargain 报议价单主(报价/核价/议价)
     * @return 结果
     */
    public int insertPurRequestQuotation(PurQuoteBargain purQuoteBargain);

    /**
     * 修改报议价单主(报价/核价/议价)
     *
     * @param purQuoteBargain 报议价单主(报价/核价/议价)
     * @return 结果
     */
    public int updatePurRequestQuotation(PurQuoteBargain purQuoteBargain);

    /**
     * 批量删除报议价单主(报价/核价/议价)
     *
     * @param requestQuotationSids 需要删除的报议价单主(报价/核价/议价)ID
     * @return 结果
     */
    public int deletePurRequestQuotationByIds(List<Long> requestQuotationSids);

    /**
     * 导入
     *
     * @param file
     * @return 结果
     */
    public int importDataPur(MultipartFile file);

    /**
     * 检查是否在议价单已经存在交集
     * @author chenkw
     * @param purQuoteBargain 报议价单主(报价/核价/议价)
     * @return 结果
     */
    public void checkDateRange(PurQuoteBargain purQuoteBargain);

    /**
     * 驳回
     * @author chenkw
     * @param purQuoteBargainItem
     * @return 结果
     */
    public int rejected(PurQuoteBargainItem purQuoteBargainItem);

    /**
     * 提交报价单到核价
     * @author chenkw
     * @param purQuoteBargainItem
     * @return 结果
     */
    public int submit(PurQuoteBargainItem purQuoteBargainItem);

    /**
     * 复制报价/核价
     * @author chenkw
     * @param purQuoteBargainItem
     * purQuoteBargainItemSid,stage
     * @return 结果
     */
    public PurQuoteBargain copy(PurQuoteBargainItem purQuoteBargainItem);

    /**
     * 批量删除采购议价明细行
     * @author chenkw
     * @param ids 需要删除的采购价信息主ID
     * @return 结果
     */
    public int deleteItem(List<Long> ids);

    /**
     * 检查是否已存在于采购价和采购成本核算流程中
     * @author chenkw
     * @param purQuoteBargain 报议价单主(报价/核价/议价)
     * @return 结果
     */
    public void checkUnique(PurQuoteBargain purQuoteBargain);
}
