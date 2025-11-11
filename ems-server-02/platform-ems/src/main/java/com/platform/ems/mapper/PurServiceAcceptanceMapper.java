package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PurServiceAcceptance;

/**
 * 服务采购验收单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-04-07
 */
public interface PurServiceAcceptanceMapper  extends BaseMapper<PurServiceAcceptance> {


    PurServiceAcceptance selectPurServiceAcceptanceById(Long serviceAcceptanceSid);

    List<PurServiceAcceptance> selectPurServiceAcceptanceList(PurServiceAcceptance purServiceAcceptance);

    /**
     * 添加多个
     * @param list List PurServiceAcceptance
     * @return int
     */
    int inserts(@Param("list") List<PurServiceAcceptance> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PurServiceAcceptance
    * @return int
    */
    int updateAllById(PurServiceAcceptance entity);

    /**
     * 更新多个
     * @param list List PurServiceAcceptance
     * @return int
     */
    int updatesAllById(@Param("list") List<PurServiceAcceptance> list);


    int countByDomain(PurServiceAcceptance params);

    int deletePurServiceAcceptanceByIds(@Param("array")Long[] serviceAcceptanceSids);

    int confirm(PurServiceAcceptance purServiceAcceptance);
}
