package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SalServiceAcceptance;

/**
 * 服务销售验收单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-06
 */
public interface SalServiceAcceptanceMapper  extends BaseMapper<SalServiceAcceptance> {


    SalServiceAcceptance selectSalServiceAcceptanceById(Long serviceAcceptanceSid);

    List<SalServiceAcceptance> selectSalServiceAcceptanceList(SalServiceAcceptance salServiceAcceptance);

    /**
     * 添加多个
     * @param list List SalServiceAcceptance
     * @return int
     */
    int inserts(@Param("list") List<SalServiceAcceptance> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SalServiceAcceptance
    * @return int
    */
    int updateAllById(SalServiceAcceptance entity);

    /**
     * 更新多个
     * @param list List SalServiceAcceptance
     * @return int
     */
    int updatesAllById(@Param("list") List<SalServiceAcceptance> list);


    int countByDomain(SalServiceAcceptance params);

    int deleteServiceAcceptanceByIds(@Param("array")Long[] serviceAcceptanceSids);

    int confirm(SalServiceAcceptance salServiceAcceptance);
}
