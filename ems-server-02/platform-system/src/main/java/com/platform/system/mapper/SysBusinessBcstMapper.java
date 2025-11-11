package com.platform.system.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.system.domain.SysBusinessBcst;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 业务动态列Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-30
 */
public interface SysBusinessBcstMapper  extends BaseMapper<SysBusinessBcst> {


    SysBusinessBcst selectSysBusinessBcstById(Long businessBcstSid);

    List<SysBusinessBcst> selectSysBusinessBcstList(SysBusinessBcst sysBusinessBcst);

    /**
     * 添加多个
     * @param list List SysBusinessBcst
     * @return int
     */
    int inserts(@Param("list") List<SysBusinessBcst> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity SysBusinessBcst
     * @return int
     */
    int updateAllById(SysBusinessBcst entity);

    /**
     * 更新多个
     * @param list List SysBusinessBcst
     * @return int
     */
    int updatesAllById(@Param("list") List<SysBusinessBcst> list);

    @InterceptorIgnore(tenantLine = "true")
    @Select("select * from s_sys_business_bcst")
    List<SysBusinessBcst> selectAll();

    /**
     * 自动定时任务 不分租户查询全部
     */
    @InterceptorIgnore(tenantLine = "true")
    @Delete({
            "<script>",
            "delete from s_sys_business_bcst ",
            "where business_bcst_sid in ",
            "<foreach collection='businessBcstSidList' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            ";",
            "</script>"
    })
    int deleteAll(SysBusinessBcst entity);

    /**
     * 自动定时任务 不分租户查询全部
     */
    @InterceptorIgnore(tenantLine = "true")
    @Delete({
            "<script>",
            "delete from s_sys_business_bcst ",
            "where title like concat('%', #{title}, '%') ",
            ";",
            "</script>"
    })
    int deleteAllByTitle(SysBusinessBcst entity);
}
