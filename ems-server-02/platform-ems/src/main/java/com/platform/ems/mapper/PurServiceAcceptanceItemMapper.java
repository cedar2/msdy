package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurServiceAcceptanceItem;

/**
 * 服务采购验收单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-07
 */
public interface PurServiceAcceptanceItemMapper  extends BaseMapper<PurServiceAcceptanceItem> {


    PurServiceAcceptanceItem selectPurServiceAcceptanceItemById(String clientId);

    List<PurServiceAcceptanceItem> selectPurServiceAcceptanceItemList(PurServiceAcceptanceItem purServiceAcceptanceItem);

    /**
     * 添加多个
     * @param list List PurServiceAcceptanceItem
     * @return int
     */
    int inserts(@Param("list") List<PurServiceAcceptanceItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurServiceAcceptanceItem
    * @return int
    */
    int updateAllById(PurServiceAcceptanceItem entity);

    /**
     * 更新多个
     * @param list List PurServiceAcceptanceItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PurServiceAcceptanceItem> list);


    void deletePurServiceAcceptanceItemByIds(@Param("array")Long[] serviceAcceptanceSids);
}
