package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.HrLaborContract;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 劳动合同Mapper接口
 *
 * @author xfzz
 * @date 2024/5/7
 */
public interface HrLaborContractMapper extends BaseMapper<HrLaborContract> {


    HrLaborContract selectHrLaborContractById(Long laborContractSid);

    List<HrLaborContract> selectHrLaborContractList(HrLaborContract hrLaborContract);

    /**
     * 添加多个
     *
     * @param list List HrLaborContract
     * @return int
     */
    int inserts(@Param("list") List<HrLaborContract> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity HrLaborContract
     * @return int
     */
    int updateAllById(HrLaborContract entity);

    /**
     * 更新多个
     *
     * @param list List HrLaborContract
     * @return int
     */
    int updatesAllById(@Param("list") List<HrLaborContract> list);

    @InterceptorIgnore(tenantLine = "true")
    @Select("select t.* from s_hr_labor_contract t " +
            "where lvyue_status = #{lvyueStatus} AND t.handle_status = #{handleStatus}")
    List<HrLaborContract> selectAll(HrLaborContract hrLaborContract);
}
