package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.PurOutsourceQuoteBargain;
import com.platform.ems.domain.PurOutsourceQuoteBargainItem;
import com.platform.ems.domain.dto.request.PurOutsourceQuotationRequest;
import com.platform.ems.domain.dto.request.PurOutsourceQuoteBargainRequest;
import com.platform.ems.domain.dto.response.PurOutsourceQuoteBargainReportResponse;
import com.platform.ems.domain.dto.response.PurOutsourceQuoteBargainResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 加工询报议价单主(询价/报价/核价/议价)Service接口
 * 
 * @author linhongwei
 * @date 2021-05-10
 */
public interface IPurOutsourceQuoteBargainService extends IService<PurOutsourceQuoteBargain>{
    /**
     * 查询加工询报议价单主(询价/报价/核价/议价)
     * 
     * @param outsourceRequestQuotationSid 加工询报议价单主(询价/报价/核价/议价)ID
     * @return 加工询报议价单主(询价/报价/核价/议价)
     */
    public PurOutsourceQuoteBargain selectPurOutsourceRequestQuotationById(Long outsourceRequestQuotationSid);

    /**
     * 查询加工询报议价单明细(询价/报价/核价/议价)
     *
     * @param outsourceRequestQuotationItemSid 加工询报议价单明细(询价/报价/核价/议价)ID
     * @return 加工询报议价单明细(询价/报价/核价/议价)
     */
    public PurOutsourceQuoteBargainItem selectPurOutsourceRequestQuotationByItemId(Long outsourceRequestQuotationItemSid);

    /**
     * 查询加工询报议价单主 ----查询页面
     *
     * @param request 加工询报议价单主(询价/报价/核价/议价)
     * @return 加工询报议价单主(询价/报价/核价/议价)
     */
    public List<PurOutsourceQuoteBargainReportResponse> report(PurOutsourceQuotationRequest request);

    /**
     * 查询加工询报议价单主 ----明细报表页面
     *
     * @param request 加工询报议价单主(询价/报价/核价/议价)
     * @return 加工询报议价单主(询价/报价/核价/议价)
     */
    public List<PurOutsourceQuoteBargainResponse> getReport(PurOutsourceQuoteBargainRequest request);

    /**
     * 查询加工询报议价单主(询价/报价/核价/议价)列表
     * 
     * @param purOutsourceQuoteBargain 加工询报议价单主(询价/报价/核价/议价)
     * @return 加工询报议价单主(询价/报价/核价/议价)集合
     */
    public List<PurOutsourceQuoteBargain> selectPurOutsourceRequestQuotationList(PurOutsourceQuoteBargain purOutsourceQuoteBargain);

    /**
     * 新增加工询报议价单主(询价/报价/核价/议价)
     * 
     * @param purOutsourceQuoteBargain 加工询报议价单主(询价/报价/核价/议价)
     * @return 结果
     */
    public int insertPurOutsourceRequestQuotation(PurOutsourceQuoteBargain purOutsourceQuoteBargain);

    /**
     * 批量删除采购议价明细
     *
     * @param ids 批量删除采购议价明细
     * @return 结果
     */
    public int deleteItem(List<Long> ids);
    /**
     * 修改加工询报议价单主(询价/报价/核价/议价)
     * 
     * @param purOutsourceQuoteBargain 加工询报议价单主(询价/报价/核价/议价)
     * @return 结果
     */
    public int updatePurOutsourceRequestQuotation(PurOutsourceQuoteBargain purOutsourceQuoteBargain);

    /**
     * 批量删除加工询报议价单主(询价/报价/核价/议价)
     * 
     * @param outsourceRequestQuotationSids 需要删除的加工询报议价单主(询价/报价/核价/议价)ID
     * @return 结果
     */
    public int deletePurOutsourceRequestQuotationByIds(List<Long> outsourceRequestQuotationSids);

    /**
     * 导入
     *
     * @param file
     * @return 结果
     */
    public int importDataPur(MultipartFile file);

    /**
     * 检查是否已存在于加工采购价和采购成本核算流程中
     * @author chenkw
     * @param purOutsourceQuoteBargain 加工报议价单主(报价/核价/议价)
     * @return 结果
     */
    public void checkUnique(PurOutsourceQuoteBargain purOutsourceQuoteBargain);

    /**
     * 检查是否在加工议价单已经存在交集
     * @author chenkw
     * @param purOutsourceQuoteBargain 加工报议价单主(报价/核价/议价)
     * @return 结果
     */
    public void checkDateRange(PurOutsourceQuoteBargain purOutsourceQuoteBargain);

    /**
     * 明细提交时校验
     * @author chenkw
     * @param purOutsourceQuoteBargainItem
     * @return 结果
     */
    public void checkPrice(PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem);

    /**
     * 驳回
     * @author chenkw
     * @param purOutsourceQuoteBargainItem
     * @return 结果
     */
    public int rejected(PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem);

    /**
     * 提交报价单到核价
     * 核价单到议价
     * 议价审批
     * @author chenkw
     * @param purOutsourceQuoteBargainItem
     * @return 结果
     */
    public int submit(PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem);

    /**
     * 复制加工报价/核价
     * @author chenkw
     * @param purOutsourceQuoteBargainItem
     * purOutsourceQuoteBargainItemSid,stage
     * @return 结果
     */
    public PurOutsourceQuoteBargain copy(PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem);

    /**
     * 修改询报议价单明细(询价/报价/核价/议价) 提交、流转
     * @author chenkw
     * @param purOutsourceQuoteBargainItem 询报议价单明细(询价/报价/核价/议价)
     * @return 结果
     */
    public int updatePurOutsourceRequestQuotationItem(PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem);

}
