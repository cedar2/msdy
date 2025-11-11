package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepBusinessRemindPo;

/**
 * 已逾期/即将到期-采购订单Mapper接口
 *
 * @author linhongwei
 * @date 2022-02-24
 */
public interface RepBusinessRemindPoMapper extends BaseMapper<RepBusinessRemindPo> {

    RepBusinessRemindPo selectRepBusinessRemindPoById(Long dataRecordSid);

    List<RepBusinessRemindPo> selectRepBusinessRemindPoList(RepBusinessRemindPo repBusinessRemindPo);

    /** 获取已逾期/即将到期 报表*/
    @SqlParser(filter=true)
    List<RepBusinessRemindPo>  getYyq(RepBusinessRemindPo repBusinessRemindPo);

    @SqlParser(filter=true)
    Integer  getYyqCount(RepBusinessRemindPo repBusinessRemindPo);

    /** 获取已逾期报表/即将到期 报表*/
    List<RepBusinessRemindPo>  getYyqItem(RepBusinessRemindPo repBusinessRemindPo);
    /**
     * 添加多个
     *
     * @param list List RepBusinessRemindPo
     * @return int
     */
    int inserts(@Param("list") List<RepBusinessRemindPo> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepBusinessRemindPo
     * @return int
     */
    int updateAllById(RepBusinessRemindPo entity);

    /**
     * 更新多个
     *
     * @param list List RepBusinessRemindPo
     * @return int
     */
    int updatesAllById(@Param("list") List<RepBusinessRemindPo> list);

    /**
     * 自动定时任务
     *
     * 批量删除
     *
     * @param entity RepBusinessRemindSo
     * @return int
     */
    @InterceptorIgnore(tenantLine = "true")
    int delete(RepBusinessRemindPo entity);

    /**
     * 添加多个
     *
     * @param list List RepBusinessRemindSo
     * @return int
     */
    @InterceptorIgnore(tenantLine = "true")
    int insertAll(@Param("list") List<RepBusinessRemindPo> list);

}
