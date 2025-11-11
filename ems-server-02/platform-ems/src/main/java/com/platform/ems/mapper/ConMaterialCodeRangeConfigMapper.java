package com.platform.ems.mapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ConMaterialCodeRangeConfig;

/**
 * 物料/商品/服务编码号码段配置Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-03
 */
public interface ConMaterialCodeRangeConfigMapper  extends BaseMapper<ConMaterialCodeRangeConfig> {


    ConMaterialCodeRangeConfig selectConMaterialCodeRangeConfigById(Long rangeConfigSid);

    List<ConMaterialCodeRangeConfig> selectConMaterialCodeRangeConfigList(ConMaterialCodeRangeConfig conMaterialCodeRangeConfig);

    @InterceptorIgnore(tenantLine = "true")
    int updateCode(@Param("materialCategory")String materialCategory,@Param("clientId")String clientId);

    /**
     * 添加多个
     * @param list List ConMaterialCodeRangeConfig
     * @return int
     */
    int inserts(@Param("list") List<ConMaterialCodeRangeConfig> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConMaterialCodeRangeConfig
    * @return int
    */
    int updateAllById(ConMaterialCodeRangeConfig entity);

    /**
     * 更新多个
     * @param list List ConMaterialCodeRangeConfig
     * @return int
     */
    int updatesAllById(@Param("list") List<ConMaterialCodeRangeConfig> list);


}
