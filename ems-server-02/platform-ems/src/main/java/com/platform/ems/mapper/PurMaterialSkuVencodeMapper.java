package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurMaterialSkuVencode;

/**
 * 采购货源供方SKU编码Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-29
 */
public interface PurMaterialSkuVencodeMapper  extends BaseMapper<PurMaterialSkuVencode> {


    PurMaterialSkuVencode selectPurMaterialSkuVencodeById(Long materialVendorSkuSid);

    List<PurMaterialSkuVencode> selectPurMaterialSkuVencodeList(PurMaterialSkuVencode purMaterialSkuVencode);

    /**
     * 添加多个
     * @param list List PurMaterialSkuVencode
     * @return int
     */
    int inserts(@Param("list") List<PurMaterialSkuVencode> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurMaterialSkuVencode
    * @return int
    */
    int updateAllById(PurMaterialSkuVencode entity);

    /**
     * 更新多个
     * @param list List PurMaterialSkuVencode
     * @return int
     */
    int updatesAllById(@Param("list") List<PurMaterialSkuVencode> list);


    void deletePurMaterialSkuVencodeByIds(@Param("array")Long[] purchaseSourceSids);
}
