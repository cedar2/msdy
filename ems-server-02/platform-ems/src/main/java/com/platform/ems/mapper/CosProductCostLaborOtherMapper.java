package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.CosProductCostLaborOther;

/**
 * 商品成本核算-工价成本明细Mapper接口
 * 
 * @author c
 * @date 2021-07-06
 */
public interface CosProductCostLaborOtherMapper  extends BaseMapper<CosProductCostLaborOther> {


    List<CosProductCostLaborOther> selectCosProductCostLaborOtherById(int serialNum);

    List<CosProductCostLaborOther> selectCosProductCostLaborOtherList(CosProductCostLaborOther cosProductCostLaborOther);

    /**
     * 根据 productCostSid 查询对应的工价成本明细-其他
     * @param productCostLaborSid
     * @return 工价成本明细-其他集合
     */
    List<CosProductCostLaborOther> getByProductCostSid(Long productCostLaborSid);
    /**
     * 添加多个
     * @param list List CosProductCostLaborOther
     * @return int
     */
    int inserts(@Param("list") List<CosProductCostLaborOther> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity CosProductCostLaborOther
    * @return int
    */
    int updateAllById(CosProductCostLaborOther entity);

    /**
     * 更新多个
     * @param list List CosProductCostLaborOther
     * @return int
     */
    int updatesAllById(@Param("list") List<CosProductCostLaborOther> list);


}
