package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvStorehouseMaterial;

/**
 * 仓库物料信息Mapper接口
 * 
 * @author linhongwei
 * @date 2022-02-12
 */
public interface InvStorehouseMaterialMapper  extends BaseMapper<InvStorehouseMaterial> {


    InvStorehouseMaterial selectInvStorehouseMaterialById(Long storehouseMaterialSid);

    List<InvStorehouseMaterial> selectInvStorehouseMaterialList(InvStorehouseMaterial invStorehouseMaterial);

    /**
     * 添加多个
     * @param list List InvStorehouseMaterial
     * @return int
     */
    int inserts(@Param("list") List<InvStorehouseMaterial> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvStorehouseMaterial
    * @return int
    */
    int updateAllById(InvStorehouseMaterial entity);

    /**
     * 更新多个
     * @param list List InvStorehouseMaterial
     * @return int
     */
    int updatesAllById(@Param("list") List<InvStorehouseMaterial> list);

    /**
     * 更新多个使用频率
     * @param list List InvStorehouseMaterial
     * @return int
     */
    int updatesUsageFrequencyFlag(@Param("list") List<InvStorehouseMaterial> list);
}
