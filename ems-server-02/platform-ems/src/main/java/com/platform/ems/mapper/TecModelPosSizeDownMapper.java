package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.TecModelPosSizeDown;

/**
 * 版型-部位-尺码-尺寸（下装）Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-25
 */
public interface TecModelPosSizeDownMapper  extends BaseMapper<TecModelPosSizeDown> {


    TecModelPosSizeDown selectTecModelPosSizeDownById(Long modelPositionSizeSid);

    List<TecModelPosSizeDown> selectTecModelPosSizeDownList(TecModelPosSizeDown tecModelPosSizeDown);

    /**
     * 添加多个
     * @param list List TecModelPosSizeDown
     * @return int
     */
    int inserts(@Param("list") List<TecModelPosSizeDown> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity TecModelPosSizeDown
    * @return int
    */
    int updateAllById(TecModelPosSizeDown entity);

    /**
     * 更新多个
     * @param list List TecModelPosSizeDown
     * @return int
     */
    int updatesAllById(@Param("list") List<TecModelPosSizeDown> list);

    int deleteTecModPosSizeDownByModPosInfSid(Long modelPositionInforSid);
}
