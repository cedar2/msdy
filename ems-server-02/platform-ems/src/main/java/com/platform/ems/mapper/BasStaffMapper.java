package com.platform.ems.mapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.HrLaborContract;
import com.platform.ems.domain.dto.response.form.BasStaffConditionForm;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasStaff;
import org.apache.ibatis.annotations.Select;

/**
 * 员工档案Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-17
 */
public interface BasStaffMapper  extends BaseMapper<BasStaff> {


    List<BasStaff> getStaffList(BasStaff basStaff);

    BasStaff selectBasStaffById(Long staffSid);

    List<BasStaff> selectBasStaffList(BasStaff basStaff);

    /**
     * 添加多个
     * @param list List BasStaff
     * @return int
     */
    int inserts(@Param("list") List<BasStaff> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasStaff
    * @return int
    */
    int updateAllById(BasStaff entity);

    /**
     * 更新多个
     * @param list List BasStaff
     * @return int
     */
    int updatesAllById(@Param("list") List<BasStaff> list);


    List<BasStaff> getMemberList(BasStaff basStaff);

    /**
     * 员工工作状况报表
     *
     * @param basStaff 员工档案
     * @return 员工档案集合
     */
    List<BasStaffConditionForm> conditionBasStaffList(BasStaffConditionForm basStaff);

    /**
     * 通过员工sid 或者 所属岗位查询员工
     *
     * @param basStaff 员工档案
     * @return 员工档案集合
     */
    @InterceptorIgnore(tenantLine = "true")
    List<BasStaff> getStaffListBySidOrPosition(BasStaff basStaff);

    @InterceptorIgnore(tenantLine = "true")
    @Select("select t.* from s_bas_staff t " +
            "where t.staff_status = #{staffStatus} AND t.handle_status = #{handleStatus} " +
            "AND t.is_on_job = #{isOnJob} AND t.status = #{status}")
    List<BasStaff> selectAll(BasStaff basStaff);
}
