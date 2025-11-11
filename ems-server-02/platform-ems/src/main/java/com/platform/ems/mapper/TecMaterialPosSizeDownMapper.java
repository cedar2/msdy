package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.TecMaterialPosSizeDown;

/**
 * 商品尺寸-部位-尺码-尺寸（下装）Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-27
 */
public interface TecMaterialPosSizeDownMapper  extends BaseMapper<TecMaterialPosSizeDown> {


    TecMaterialPosSizeDown selectTecMaterialPosSizeDownById(Long materialPosSizeSid);

    List<TecMaterialPosSizeDown> selectTecMaterialPosSizeDownList(TecMaterialPosSizeDown tecMaterialPosSizeDown);

    /**
     * 添加多个
     * @param list List TecMaterialPosSizeDown
     * @return int
     */
    int inserts(@Param("list") List<TecMaterialPosSizeDown> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity TecMaterialPosSizeDown
    * @return int
    */
    int updateAllById(TecMaterialPosSizeDown entity);

    /**
     * 更新多个
     * @param list List TecMaterialPosSizeDown
     * @return int
     */
    int updatesAllById(@Param("list") List<TecMaterialPosSizeDown> list);


}
