package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.response.form.InvInventoryProductUserMaterial;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvInventoryDocument;

/**
 * 库存凭证Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-16
 */
public interface InvInventoryDocumentMapper  extends BaseMapper<InvInventoryDocument> {


    List<Long> selectInvInventoryDocumentSids(InvInventoryDocument invInventoryDocument);

    InvInventoryDocument selectInvInventoryDocumentById(Long inventoryDocumentSid);

    /**
     * 查询页面更新信息按钮获取信息
     *
     * @param inventoryDocumentSid 库存凭证ID
     * @return 库存凭证
     */
    InvInventoryDocument selectInvInventoryDocumentDetailById(Long inventoryDocumentSid);

    /**
     * 查询页面更新信息按钮修改信息
     *
     * @param invInventoryDocument 库存凭证ID
     * @return 库存凭证
     */
    int updateInvInventoryDocumentDetailById(InvInventoryDocument invInventoryDocument);

    List<InvInventoryDocument> selectInvInventoryDocumentList(InvInventoryDocument invInventoryDocument);

    /**
     * 添加多个
     * @param list List InvInventoryDocument
     * @return int
     */
    int inserts(@Param("list") List<InvInventoryDocument> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvInventoryDocument
    * @return int
    */
    int updateAllById(InvInventoryDocument entity);

    /**
     * 更新多个
     * @param list List InvInventoryDocument
     * @return int
     */
    int updatesAllById(@Param("list") List<InvInventoryDocument> list);

    int countByDomain(InvInventoryDocument params);

    int deleteInvInventoryDocumentByIds(@Param("array") Long[] inventoryDocumentSids);

    int confirm(InvInventoryDocument invInventoryDocument);

    /**
     * 查询商品领用物料统计报表
     *
     * @param request
     * @return
     */
    List<InvInventoryProductUserMaterial> productUserMaterialStatistics(InvInventoryProductUserMaterial request);
}
