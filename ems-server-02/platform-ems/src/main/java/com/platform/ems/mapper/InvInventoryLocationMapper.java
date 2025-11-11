package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.RepInventoryStatus;
import com.platform.ems.domain.dto.response.form.InvInventoryLocationBarcodeStatisticsForm;
import com.platform.ems.domain.dto.response.form.InvInventoryLocationStoreStatisticsForm;
import com.platform.ems.domain.dto.response.form.InvInventorySpecialBarcodeStatisticsForm;
import com.platform.ems.domain.dto.response.form.InvInventorySpecialStoreStatisticsForm;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvInventoryLocation;

/**
 * 仓库库位库存Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-20
 */
public interface InvInventoryLocationMapper  extends BaseMapper<InvInventoryLocation> {

    List<InvInventoryLocation> getInvInventoryLocation(InvInventoryLocation invInventoryLocation);
    InvInventoryLocation selectInvInventoryLocationById(Long locationStockSid);
    List<RepInventoryStatus> selectRepInventoryStatusList();

    InvInventoryLocation getLocationAble(InvInventoryLocation invInventoryLocation);

    List<InvInventoryLocation> selectInvInventoryLocationList(InvInventoryLocation invInventoryLocation);

    /**
     * 添加多个
     * @param list List InvInventoryLocation
     * @return int
     */
    int inserts(@Param("list") List<InvInventoryLocation> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvInventoryLocation
    * @return int
    */
    int updateAllById(InvInventoryLocation entity);

    /**
     * 更新多个
     * @param list List InvInventoryLocation
     * @return int
     */
    int updatesAllById(@Param("list") List<InvInventoryLocation> list);

    /**
     * 库存统计报表 按SKU
     *
     * @param request InvInventoryLocationBarcodeStatisticsForm 仓库库位库存
     */
    List<InvInventoryLocationBarcodeStatisticsForm> selectInvInventoryLocationBarcodeStatisticsForm(InvInventoryLocationBarcodeStatisticsForm request);

    /**
     * 库存统计报表 按仓库
     *
     * @param request InvInventoryLocationStoreStatisticsForm 仓库库位库存
     */
    List<InvInventoryLocationStoreStatisticsForm> selectInvInventoryLocationStoreStatisticsForm(InvInventoryLocationStoreStatisticsForm request);

    /**
     * 特殊库存统计报表 按SKU
     *
     * @param request InvInventorySpecialBarcodeStatisticsForm 仓库库位库存
     */
    List<InvInventorySpecialBarcodeStatisticsForm> selectInvInventorySpecialBarcodeStatisticsForm(InvInventorySpecialBarcodeStatisticsForm request);

    /**
     * 特殊库存统计报表 按仓库
     *
     * @param request InvInventorySpecialStoreStatisticsForm 仓库库位库存
     */
    List<InvInventorySpecialStoreStatisticsForm> selectInvInventorySpecialStoreStatisticsForm(InvInventorySpecialStoreStatisticsForm request);

    /**
     * 移动端库存报表
     */
    List<InvInventoryLocation> selectMobInvLocFormList(InvInventoryLocation inventory);

}
