package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.TecMaterialPosInforDown;

/**
 * 商品尺寸-部位（套装的下装）Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-27
 */
public interface TecMaterialPosInforDownMapper  extends BaseMapper<TecMaterialPosInforDown> {


    TecMaterialPosInforDown selectTecMaterialPosInforDownById(String clientId);

    List<TecMaterialPosInforDown> selectTecMaterialPosInforDownList(TecMaterialPosInforDown tecMaterialPosInforDown);

    /**
     * 添加多个
     * @param list List TecMaterialPosInforDown
     * @return int
     */
    int inserts(@Param("list") List<TecMaterialPosInforDown> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity TecMaterialPosInforDown
    * @return int
    */
    int updateAllById(TecMaterialPosInforDown entity);

    /**
     * 更新多个
     * @param list List TecMaterialPosInforDown
     * @return int
     */
    int updatesAllById(@Param("list") List<TecMaterialPosInforDown> list);


}
