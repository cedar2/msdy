package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.InvOwnerMaterialSettle;
import com.platform.ems.domain.InvOwnerMaterialSettleItem;
import com.platform.ems.domain.dto.request.InvOwnerMaterialSettleRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.InvOwnerMaterialSettleReportResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 甲供料结算单Service接口
 * 
 * @author c
 * @date 2021-09-13
 */
public interface IInvOwnerMaterialSettleService extends IService<InvOwnerMaterialSettle>{
    /**
     * 查询甲供料结算单
     * 
     * @param settleSid 甲供料结算单ID
     * @return 甲供料结算单
     */
    public InvOwnerMaterialSettle selectInvOwnerMaterialSettleById(Long settleSid);

    public List<InvOwnerMaterialSettleItem> sort(List<InvOwnerMaterialSettleItem> items, String type);
    /**
     * 查询甲供料结算单列表
     * 
     * @param invOwnerMaterialSettle 甲供料结算单
     * @return 甲供料结算单集合
     */
    public List<InvOwnerMaterialSettle> selectInvOwnerMaterialSettleList(InvOwnerMaterialSettle invOwnerMaterialSettle);

    /**
     * 新增甲供料结算单
     * 
     * @param invOwnerMaterialSettle 甲供料结算单
     * @return 结果
     */
    public int insertInvOwnerMaterialSettle(InvOwnerMaterialSettle invOwnerMaterialSettle);

    /**
     * 修改甲供料结算单
     * 
     * @param invOwnerMaterialSettle 甲供料结算单
     * @return 结果
     */
    public int updateInvOwnerMaterialSettle(InvOwnerMaterialSettle invOwnerMaterialSettle);

    /**
     * 变更甲供料结算单
     *
     * @param invOwnerMaterialSettle 甲供料结算单
     * @return 结果
     */
    public int changeInvOwnerMaterialSettle(InvOwnerMaterialSettle invOwnerMaterialSettle);

    public List<InvOwnerMaterialSettleReportResponse> getReport(InvOwnerMaterialSettleRequest InvOwnerMaterialSettleRequest);

    /**
     * 批量删除甲供料结算单
     * 
     * @param settleSids 需要删除的甲供料结算单ID
     * @return 结果
     */
    public int deleteInvOwnerMaterialSettleByIds(List<Long> settleSids);

    /**
     * 作废
     *
     */
    public int disuse(List<Long>  settleSids);
    /**
     * 更改确认状态
     * @param invOwnerMaterialSettle
     * @return
     */
    int check(InvOwnerMaterialSettle invOwnerMaterialSettle);

    /**
     * 提交时校验
     */
    public OrderErrRequest processCheck(OrderErrRequest request);
    /**
     * 甲供料结算单
     */
    public AjaxResult importDataInv(MultipartFile file);
}
