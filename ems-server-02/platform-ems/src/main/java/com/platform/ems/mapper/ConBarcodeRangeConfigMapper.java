package com.platform.ems.mapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ConBarcodeRangeConfig;

/**
 * 物料/商品/服务条码号码段配置Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-03
 */
public interface ConBarcodeRangeConfigMapper  extends BaseMapper<ConBarcodeRangeConfig> {


    ConBarcodeRangeConfig selectConBarcodeRangeConfigById(Long rangeConfigSid);

    List<ConBarcodeRangeConfig> selectConBarcodeRangeConfigList(ConBarcodeRangeConfig conBarcodeRangeConfig);

    @InterceptorIgnore(tenantLine = "true")
    int updateCode(@Param("clientId")String clientId);

    /**
     * 添加多个
     * @param list List ConBarcodeRangeConfig
     * @return int
     */
    int inserts(@Param("list") List<ConBarcodeRangeConfig> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConBarcodeRangeConfig
    * @return int
    */
    int updateAllById(ConBarcodeRangeConfig entity);

    /**
     * 更新多个
     * @param list List ConBarcodeRangeConfig
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBarcodeRangeConfig> list);


}
