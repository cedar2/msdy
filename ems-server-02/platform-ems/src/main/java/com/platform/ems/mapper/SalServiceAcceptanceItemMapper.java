package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalServiceAcceptanceItem;

/**
 * 服务销售验收单-明细Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-06
 */
public interface SalServiceAcceptanceItemMapper  extends BaseMapper<SalServiceAcceptanceItem> {


    SalServiceAcceptanceItem selectSalServiceAcceptanceItemById(Long serviceAcceptanceItemSid);

    List<SalServiceAcceptanceItem> selectSalServiceAcceptanceItemList(SalServiceAcceptanceItem salServiceAcceptanceItem);

    /**
     * 添加多个
     * @param list List SalServiceAcceptanceItem
     * @return int
     */
    int inserts(@Param("list") List<SalServiceAcceptanceItem> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SalServiceAcceptanceItem
    * @return int
    */
    int updateAllById(SalServiceAcceptanceItem entity);

    /**
     * 更新多个
     * @param list List SalServiceAcceptanceItem
     * @return int
     */
    int updatesAllById(@Param("list") List<SalServiceAcceptanceItem> list);


    void deleteServiceAcceptanceItemByIds(@Param("array")Long[] serviceAcceptanceSids);
}
