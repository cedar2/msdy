package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.InvInventoryDocument;
import com.platform.ems.domain.InvInventoryLocation;
import com.platform.ems.domain.InvStorehouseMaterial;
import com.platform.ems.domain.dto.request.InvInventoryLocationMaterialRequest;
import com.platform.ems.domain.dto.request.InvReserveInventoryRequest;
import com.platform.ems.domain.dto.response.InvReserveInventoryResponse;
import com.platform.ems.domain.dto.response.form.InvInventoryLocationBarcodeStatisticsForm;
import com.platform.ems.domain.dto.response.form.InvInventoryLocationStoreStatisticsForm;
import com.platform.ems.domain.dto.response.form.InvInventorySpecialBarcodeStatisticsForm;
import com.platform.ems.domain.dto.response.form.InvInventorySpecialStoreStatisticsForm;
import org.springframework.web.multipart.MultipartFile;

/**
 * 仓库库位库存Service接口
 *
 * @author linhongwei
 * @date 2021-06-16
 */
public interface IInvInventoryLocationService extends IService<InvInventoryLocation>{
    /**
     * 查询仓库库位库存
     *
     * @param locationStockSid 仓库库位库存ID
     * @return 仓库库位库存
     */
    public InvInventoryLocation selectInvInventoryLocationById(Long locationStockSid);
    /**
     * 添加样品获取库存数量和库存价
     *
     */
    public List<BasMaterial> getLocationMaterial(InvInventoryLocationMaterialRequest locationMaterial);
    /**
     * 添加盘点获取数量
     *
     */
    public List<BasMaterial> getLocationMaterialQu(InvInventoryLocationMaterialRequest locationMaterial);
    /**
     * 查询仓库库位库存列表
     *
     * @param invInventoryLocation 仓库库位库存
     * @return 仓库库位库存集合
     */
    public List<InvInventoryLocation> selectInvInventoryLocationList(InvInventoryLocation invInventoryLocation);
    /**
     * 库存预留报表
     *
     */
    public List<InvReserveInventoryResponse> report(InvReserveInventoryRequest request);
    /**
     * 新增仓库库位库存
     *
     * @param invInventoryLocation 仓库库位库存
     * @return 结果
     */
    public int insertInvInventoryLocation(InvInventoryLocation invInventoryLocation);

    public int getMaterialLocation(List<InvInventoryLocation> list);

    /**
     * 修改仓库库位库存
     *
     * @param invInventoryLocation 仓库库位库存
     * @return 结果
     */
    public int updateInvInventoryLocation(InvInventoryLocation invInventoryLocation);

    /**
     * 变更仓库库位库存
     *
     * @param invInventoryLocation 仓库库位库存
     * @return 结果
     */
    public int changeInvInventoryLocation(InvInventoryLocation invInventoryLocation);

    /**
     * 批量删除仓库库位库存
     *
     * @param locationStockSids 需要删除的仓库库位库存ID
     * @return 结果
     */
    public int deleteInvInventoryLocationByIds(List<Long> locationStockSids);

    /**
     * 释放库存预留
     *
     */
    public int deleteInvReserve(List<Long> locationStockSids);

    /**
    * 启用/停用
    * @param invInventoryLocation
    * @return
    */
    int changeStatus(InvInventoryLocation invInventoryLocation);

    /**
     * 更改确认状态
     * @param invInventoryLocation
     * @return
     */
    int check(InvInventoryLocation invInventoryLocation);

    public AjaxResult importDataInv(MultipartFile file);

    /**
     * 库存出库初始化
     */
    public AjaxResult importDataInvCHK(MultipartFile file);

    public InvInventoryDocument importDataInvOld(MultipartFile file);


    /**
     * 导入甲供料结算单
     */
    int importData(MultipartFile file);

    /**
     * 库存统计报表 按SKU
     *
     * @param request InvInventoryLocationBarcodeStatisticsForm 仓库库位库存
     */
    public List<InvInventoryLocationBarcodeStatisticsForm> selectInvInventoryLocationStatisticsForm(InvInventoryLocationBarcodeStatisticsForm request);

    /**
     * 库存统计报表 按仓库
     *
     * @param request InvInventoryLocationStoreStatisticsForm 仓库库位库存

     */
    public List<InvInventoryLocationStoreStatisticsForm> selectInvInventoryLocationStatisticsForm(InvInventoryLocationStoreStatisticsForm request);

    /**
     * 库存统计报表 按仓库 更新日期
     *
     * @param request InvStorehouseMaterial 仓库物料信息对象

     */
    AjaxResult updateInvStorehouseMaterial(InvStorehouseMaterial request);

    /**
     * 特殊库存统计报表 按SKU
     *
     * @param request InvInventoryLocationBarcodeStatisticsForm 仓库库位库存
     */
    public List<InvInventorySpecialBarcodeStatisticsForm> selectInvInventorySpecialStatisticsForm(InvInventorySpecialBarcodeStatisticsForm request);

    /**
     * 特殊库存统计报表 按仓库
     *
     * @param request InvInventoryLocationStoreStatisticsForm 仓库库位库存
     */
    public List<InvInventorySpecialStoreStatisticsForm> selectInvInventorySpecialStatisticsForm(InvInventorySpecialStoreStatisticsForm request);

    /**
     * 移动端库存报表
     */
    List<InvInventoryLocation> selectMobInvLocFormList(InvInventoryLocation inventory);

}
