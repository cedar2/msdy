package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.TecModelPosInforDown;

/**
 * 版型-部位信息（下装）Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-25
 */
public interface TecModelPosInforDownMapper  extends BaseMapper<TecModelPosInforDown> {


    TecModelPosInforDown selectTecModelPosInforDownById(Long modelPositionInforSid);

    List<TecModelPosInforDown> selectTecModelPosInforDownList(TecModelPosInforDown tecModelPosInforDown);

    /**
     * 添加多个
     * @param list List TecModelPosInforDown
     * @return int
     */
    int inserts(@Param("list") List<TecModelPosInforDown> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity TecModelPosInforDown
    * @return int
    */
    int updateAllById(TecModelPosInforDown entity);

    /**
     * 更新多个
     * @param list List TecModelPosInforDown
     * @return int
     */
    int updatesAllById(@Param("list") List<TecModelPosInforDown> list);


    int deleteTecModelPosInforDownByModelSid(Long modelSid);

}
