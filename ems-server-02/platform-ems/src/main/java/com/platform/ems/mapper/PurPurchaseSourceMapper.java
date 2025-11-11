package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurPurchaseSource;

/**
 * 采购货源清单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-10
 */
public interface PurPurchaseSourceMapper  extends BaseMapper<PurPurchaseSource> {


    PurPurchaseSource selectPurPurchaseSourceById(Long purchaseSourceSid);

    List<PurPurchaseSource> selectPurPurchaseSourceList(PurPurchaseSource purPurchaseSource);

    /**
     * 添加多个
     * @param list List PurPurchaseSource
     * @return int
     */
    int inserts(@Param("list") List<PurPurchaseSource> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurPurchaseSource
    * @return int
    */
    int updateAllById(PurPurchaseSource entity);

    /**
     * 更新多个
     * @param list List PurPurchaseSource
     * @return int
     */
    int updatesAllById(@Param("list") List<PurPurchaseSource> list);

    /**
     * 验证供应商编码和商品/物料/服务编码组合是否重复
     * @param purPurchaseSource 供应商sid、物料sid
     * @return int
     */
    int checkVendorAndMaterial(PurPurchaseSource purPurchaseSource);

    int countByDomain(PurPurchaseSource params);

    int deletePurPurchaseSourceByIds(@Param("array")Long[] purchaseSourceSids);

    int confirm(PurPurchaseSource purPurchaseSource);
}
