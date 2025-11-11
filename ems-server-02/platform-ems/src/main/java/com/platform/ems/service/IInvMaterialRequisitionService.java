package com.platform.ems.service;

import java.util.List;

import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.dto.request.InvMaterialRequisitionReportRequest;
import com.platform.ems.domain.dto.response.InvMaterialRequisitionReportResponse;

/**
 * 领退料单Service接口
 *
 * @author linhongwei
 * @date 2021-04-08
 */
public interface IInvMaterialRequisitionService extends IService<InvMaterialRequisition>{
    /**
     * 查询领退料单
     *
     * @param materialRequisitionSid 领退料单ID
     * @return 领退料单
     */
    public InvMaterialRequisition selectInvMaterialRequisitionById(Long materialRequisitionSid);

    public List<InvMaterialRequisitionItem> sort(List<InvMaterialRequisitionItem> items, String type);
    /**
     * 查询领退料单明细报表
     *
     * @param request 领退料单
     * @return 领退料单集合
     */
    public List<InvMaterialRequisitionReportResponse> reportInvMaterialRequisition(InvMaterialRequisitionReportRequest request);

    /**
     * 新增领退料单
     *
     * @param invMaterialRequisition 领退料单
     * @return 结果
     */
    public int insertInvMaterialRequisition(InvMaterialRequisition invMaterialRequisition);

    /**
     * 修改领退料单
     *
     * @param invMaterialRequisition 领退料单
     * @return 结果
     */
    public int updateInvMaterialRequisition(InvMaterialRequisition invMaterialRequisition);

    /**
     * 批量删除领退料单
     *
     * @param materialRequisitionSids 需要删除的领退料单ID
     * @return 结果
     */
    public int deleteInvMaterialRequisitionByIds(Long[] materialRequisitionSids);
    /**
     * 关闭领退料单
     */
    public int close(Long[] materialRequisitionSids);

    /**
     * 领退料单确认
     */
    int confirm(InvMaterialRequisition invMaterialRequisition);

    /**
     * 领退料单变更
     */
    int change(InvMaterialRequisition invMaterialRequisition);

    /**
     * 提交时校验
     */
    public OrderErrRequest processCheck(OrderErrRequest request);

    //明细报表生成库存预留
    public int create(List<Long> sids);

    //明细报表释放预留库存
    public int reportFreeInv(List<Long> sids);
    //冲销
    public int invCx(InvInventoryDocument invInventoryDocument, List<InvInventoryDocumentItem> list);
    /**
     * 导入领退料单
     */
    AjaxResult importData(MultipartFile file);

	/**
	 * 查询领退料单列表
	 *
	 * @param invMaterialRequisition 领退料单
	 * @return 领退料单
	 */
	List<InvMaterialRequisition> selectInvMaterialRequisitionList(InvMaterialRequisition invMaterialRequisition);
    /**
     * 复制
     */
    public InvMaterialRequisition getCopy(Long sid);
    /**
     * 物料需求测算-创建领料单
     */
    public InvMaterialRequisition getMaterialRequisition(List<TecBomItemReport> order);

	/**
	 * 生成PDF
	 * @Author qhq
	 * @param invMaterialRequisition
	 * @return
	 */
	public AjaxResult generatePDF(InvMaterialRequisition invMaterialRequisition);
}
