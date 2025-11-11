package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.MatterTraceTableVo;
import com.platform.ems.domain.PrjMatterList;
import com.platform.ems.domain.PrjProjectQuery;
import com.platform.ems.domain.TargetVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 事项清单Mapper接口
 *
 * @author platform
 * @date 2023-11-20
 */
public interface PrjMatterListMapper extends BaseMapper<PrjMatterList> {

    /**
     * 查询详情
     *
     * @param matterListSid 单据sid
     * @return PrjMatterList
     */
    PrjMatterList selectPrjMatterListById(Long matterListSid);

    /**
     * 查询列表
     *
     * @param prjMatterList PrjMatterList
     * @return List
     */
    List<PrjMatterList> selectPrjMatterListList(PrjMatterList prjMatterList);

    /**
     * 添加多个
     *
     * @param list List PrjMatterList
     * @return int
     */
    int inserts(@Param("list") List<PrjMatterList> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PrjMatterList
     * @return int
     */
    int updateAllById(PrjMatterList entity);

    /**
     * 更新多个
     *
     * @param list List PrjMatterList
     * @return int
     */
    int updatesAllById(@Param("list") List<PrjMatterList> list);

    /**
     * 事项所属状态为未开始，且当前日期+事项的待办提醒天数>=事项计划完成日期
     */
    List<PrjMatterList> getNotYetStartMatterList(PrjMatterList entity);

    /**
     * 查询即将到期
     */
    List<PrjMatterList> getToexpireBusiness(PrjMatterList entity);

    /**
     * 查询已逾期
     */
    List<PrjMatterList> getOverdueBusiness(PrjMatterList entity);

    /**
     * 事项进度跟踪报表
     *
     * @param query
     * @return
     */
    @Select("<script>" +
            "SELECT\n" +
            "IF((spml.matter_status = 'JXZ') AND (spml.plan_end_date &gt;= DATE_FORMAT(NOW(),'%Y-%m-%d')) \n" +
            "AND (DATE_SUB(spml.plan_end_date,INTERVAL spml.toexpire_days_matter DAY) &lt;= DATE_FORMAT(NOW(),'%Y-%m-%d')),'2',\n" +
            "IF((spml.matter_status = 'JXZ') AND (spml.plan_end_date &lt; DATE_FORMAT(NOW(),'%Y-%m-%d')),'0','1')) AS warning,\n" +
            "spml.matter_name,\n" +
            "spts.dict_label AS matter_status,\n" +
            "spp.project_sid,\n" +
            "spp.project_code,\n" +
            "sbs.store_code,\n" +
            "sbs.store_name,\n" +
            "spt.dict_label AS project_type,\n" +
            "sua.nick_name AS matter_handler,\n" +
            "sub.nick_name AS matter_manager,\n" +
            "spml.plan_start_date,\n" +
            "spml.plan_end_date,\n" +
            "stb.dict_label AS matter_business,\n" +
            "sbs.store_addr_detail,\n" +
            "scsr.node_name as saleRegionName,\n" +
            "som.dict_label AS operate_mode,\n" +
            "sbc.customer_code,\n" +
            "sbc.customer_name\n" +
            "FROM s_prj_matter_list spml \n" +
            "LEFT JOIN s_prj_project spp ON spml.project_code = spp.project_code\n" +
            "LEFT JOIN s_bas_store sbs ON sbs.store_code = spp.store_code\n" +
            "LEFT JOIN s_bas_customer sbc ON spp.customer_code = sbc.customer_code\n" +
            "LEFT JOIN sys_user sua ON spml.matter_handler = sua.user_name\n" +
            "LEFT JOIN sys_user sub ON spml.matter_manager = sub.user_name\n" +
            "LEFT JOIN s_con_sale_region scsr ON sbs.sale_region_code = scsr.node_code\n" +
            "LEFT JOIN sys_dict_data spts ON spts.dict_value = spml.matter_status AND spts.dict_type = 's_project_task_status'\n" +
            "LEFT JOIN sys_dict_data spt ON spt.dict_value = spp.project_type AND spt.dict_type = 's_project_type'\n" +
            "LEFT JOIN sys_dict_data som ON som.dict_value = spp.operate_mode AND som.dict_type = 's_operate_mode'\n" +
            "LEFT JOIN sys_dict_data stb ON stb.dict_value = spml.matter_business AND stb.dict_type = 's_task_business'\n" +
            "WHERE spml.matter_status IN ('JXZ','WKS','ZT')\n" +
            "<if test=\"v.saleRegionCode != null and v.saleRegionCode!=''\"> \n" +
            "AND sbs.sale_region_code = #{v.saleRegionCode}\n" +
            "</if> \n" +
            "<if test=\"v.matterHandler != null and v.matterHandler!=''\"> \n" +
            "AND spml.matter_handler = #{v.matterHandler}\n" +
            "</if> \n" +
            "<if test=\"v.matterBusiness != null and v.matterBusiness.size >0 \"> \n" +
            "<foreach collection=\"v.matterBusiness\" item=\"item\" open=\"AND spml.matter_business IN (\" close=\")\" separator=\",\">" +
            "  #{item} " +
            "</foreach>" +
            "</if> \n" +
            "<if test=\"v.pageNum != null and v.pageSize!=null\"> \n" +
            "limit #{v.pageNum},#{v.pageSize}\n" +
            "</if>" +
            "</script>")
    List<MatterTraceTableVo> matterTraceTable(@Param("v") PrjProjectQuery query);

    @Select("<script>" +
            "SELECT\n" +
            "count(1)\n" +
            "FROM s_prj_matter_list spml \n" +
            "LEFT JOIN s_prj_project spp ON spml.project_code = spp.project_code\n" +
            "LEFT JOIN s_bas_store sbs ON sbs.store_code = spp.store_code\n" +
            "LEFT JOIN s_bas_customer sbc ON spp.customer_code = sbc.customer_code\n" +
            "LEFT JOIN sys_user sua ON spml.matter_handler = sua.user_name\n" +
            "LEFT JOIN sys_user sub ON spml.matter_manager = sub.user_name\n" +
            "LEFT JOIN s_con_sale_region scsr ON sbs.sale_region_code = scsr.node_code\n" +
            "LEFT JOIN sys_dict_data spts ON spts.dict_value = spml.matter_status AND spts.dict_type = 's_project_task_status'\n" +
            "LEFT JOIN sys_dict_data spt ON spt.dict_value = spp.project_type AND spt.dict_type = 's_project_type'\n" +
            "LEFT JOIN sys_dict_data som ON som.dict_value = spp.operate_mode AND som.dict_type = 's_operate_mode'\n" +
            "LEFT JOIN sys_dict_data stb ON stb.dict_value = spml.matter_business AND stb.dict_type = 's_task_business'\n" +
            "WHERE spml.matter_status IN ('JXZ','WKS','ZT')\n" +
            "<if test=\"v.saleRegionCode != null and v.saleRegionCode!=''\"> \n" +
            "AND sbs.sale_region_code = #{v.saleRegionCode}\n" +
            "</if> \n" +
            "<if test=\"v.matterHandler != null and v.matterHandler!=''\"> \n" +
            "AND spml.matter_handler = #{v.matterHandler}\n" +
            "</if> \n" +
            "<if test=\"v.matterBusiness != null and v.matterBusiness.size >0 \"> \n" +
            "<foreach collection=\"v.matterBusiness\" item=\"item\" open=\"AND spml.matter_business IN (\" close=\")\" separator=\",\">" +
            "  #{item} " +
            "</foreach>" +
            "</if> \n" +
            "</script>")
    int matterTraceTableCount(@Param("v") PrjProjectQuery query);

    @Select("<script>" +
            "SELECT\n" +
            "count(spml.matter_status IN ('JXZ','ZT','WKS') OR NULL) AS unfinishedNum,\n" +
            "count((spml.matter_status = 'JXZ' OR NULL) AND (spml.plan_end_date &gt;= DATE_FORMAT(NOW(),'%Y-%m-%d') OR NULL) \n" +
            "AND (DATE_SUB(spml.plan_end_date,INTERVAL spml.toexpire_days_matter DAY) &lt;= DATE_FORMAT(NOW(),'%Y-%m-%d') OR NULL)) AS aboutToExpireNum,\n" +
            "count((spml.matter_status = 'JXZ' OR NULL) AND (spml.plan_end_date &lt; DATE_FORMAT(NOW(),'%Y-%m-%d') OR NULL)) AS overdueNum\n" +
            "FROM s_prj_matter_list spml \n" +
            "LEFT JOIN s_prj_project spp ON spml.project_code = spp.project_code\n" +
            "LEFT JOIN s_bas_store sbs ON sbs.store_code = spp.store_code\n" +
            "LEFT JOIN s_con_sale_region scsr ON sbs.sale_region_code = scsr.node_code\n" +
            "WHERE spml.matter_status IN ('JXZ','WKS','ZT')\n" +
            "<if test=\"v.saleRegionCode != null and v.saleRegionCode!=''\"> \n" +
            "AND sbs.sale_region_code = #{v.saleRegionCode}\n" +
            "</if> \n" +
            "<if test=\"v.matterHandler != null and v.matterHandler!=''\"> \n" +
            "AND spml.matter_handler = #{v.matterHandler}\n" +
            "</if> \n" +
            "<if test=\"v.matterBusiness != null and v.matterBusiness.size >0 \"> \n" +
            "<foreach collection=\"v.matterBusiness\" item=\"item\" open=\"AND spml.matter_business IN (\" close=\")\" separator=\",\">" +
            "  #{item} " +
            "</foreach>" +
            "</if> \n" +
            "<if test=\"v.projectTypes != null and v.projectTypes.size >0 \"> \n" +
            "<foreach collection=\"v.projectTypes\" item=\"item\" open=\"AND spp.project_type IN (\" close=\")\" separator=\",\">" +
            "  #{item} " +
            "</foreach>" +
            "</if> \n" +
            "</script>")
    TargetVo matterTraceTarget(@Param("v") PrjProjectQuery query);
}
