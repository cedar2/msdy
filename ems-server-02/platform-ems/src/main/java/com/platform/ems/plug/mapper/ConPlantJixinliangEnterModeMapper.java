package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConPlantJixinliangEnterMode;

/**
 * 工厂计薪量录入方式Mapper接口
 * 
 * @author zhuangyz
 * @date 2022-07-14
 */
public interface ConPlantJixinliangEnterModeMapper  extends BaseMapper<ConPlantJixinliangEnterMode> {


    ConPlantJixinliangEnterMode selectConPlantJixinliangEnterModeById(Long sid);

    List<ConPlantJixinliangEnterMode> selectConPlantJixinliangEnterModeList(ConPlantJixinliangEnterMode conPlantJixinliangEnterMode);

    /**
     * 添加多个
     * @param list List ConPlantJixinliangEnterMode
     * @return int
     */
    int inserts(@Param("list") List<ConPlantJixinliangEnterMode> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConPlantJixinliangEnterMode
    * @return int
    */
    int updateAllById(ConPlantJixinliangEnterMode entity);

    /**
     * 更新多个
     * @param list List ConPlantJixinliangEnterMode
     * @return int
     */
    int updatesAllById(@Param("list") List<ConPlantJixinliangEnterMode> list);


}
