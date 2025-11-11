package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManManufactureOutsourceSettle;

/**
 * 外发加工费结算单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-10
 */
public interface ManManufactureOutsourceSettleMapper  extends BaseMapper<ManManufactureOutsourceSettle> {


    ManManufactureOutsourceSettle selectManManufactureOutsourceSettleById(Long manufactureOutsourceSettleSid);

    List<ManManufactureOutsourceSettle> selectManManufactureOutsourceSettleList(ManManufactureOutsourceSettle manManufactureOutsourceSettle);

    /**
     * 添加多个
     * @param list List ManManufactureOutsourceSettle
     * @return int
     */
    int inserts(@Param("list") List<ManManufactureOutsourceSettle> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManManufactureOutsourceSettle
    * @return int
    */
    int updateAllById(ManManufactureOutsourceSettle entity);

    /**
     * 更新多个
     * @param list List ManManufactureOutsourceSettle
     * @return int
     */
    int updatesAllById(@Param("list") List<ManManufactureOutsourceSettle> list);

}
