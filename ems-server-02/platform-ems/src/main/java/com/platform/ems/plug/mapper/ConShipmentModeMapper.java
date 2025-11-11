package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConShipmentMode;

/**
 * 配送方式Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConShipmentModeMapper  extends BaseMapper<ConShipmentMode> {


    ConShipmentMode selectConShipmentModeById(Long sid);

    List<ConShipmentMode> selectConShipmentModeList(ConShipmentMode conShipmentMode);

    /**
     * 添加多个
     * @param list List ConShipmentMode
     * @return int
     */
    int inserts(@Param("list") List<ConShipmentMode> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConShipmentMode
    * @return int
    */
    int updateAllById(ConShipmentMode entity);

    /**
     * 更新多个
     * @param list List ConShipmentMode
     * @return int
     */
    int updatesAllById(@Param("list") List<ConShipmentMode> list);


    /**
     * 配送方式下拉框
     */
    List<ConShipmentMode> getList();
}
