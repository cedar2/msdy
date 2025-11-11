package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepBusinessRemindSo;

/**
 * 已逾期/即将到期-销售订单Mapper接口
 *
 * @author linhongwei
 * @date 2022-02-24
 */
public interface RepBusinessRemindSoMapper extends BaseMapper<RepBusinessRemindSo> {


    RepBusinessRemindSo selectRepBusinessRemindSoById(Long dataRecordSid);

    List<RepBusinessRemindSo> selectRepBusinessRemindSoList(RepBusinessRemindSo repBusinessRemindSo);

    /** 获取已逾期报表 */
    @SqlParser(filter=true)
    List<RepBusinessRemindSo>  getYyq(RepBusinessRemindSo repBusinessRemindSo);

    @SqlParser(filter=true)
    Integer getYyqCount(RepBusinessRemindSo repBusinessRemindSo);

    //获取已逾期报表
    List<RepBusinessRemindSo>  getYyqItem(RepBusinessRemindSo repBusinessRemindSo);
    /**
     * 添加多个
     *
     * @param list List RepBusinessRemindSo
     * @return int
     */
    int inserts(@Param("list") List<RepBusinessRemindSo> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepBusinessRemindSo
     * @return int
     */
    int updateAllById(RepBusinessRemindSo entity);

    /**
     * 更新多个
     *
     * @param list List RepBusinessRemindSo
     * @return int
     */
    int updatesAllById(@Param("list") List<RepBusinessRemindSo> list);

    /**
     * 自动定时任务
     *
     * 批量删除
     *
     * @param entity RepBusinessRemindSo
     * @return int
     */
    @InterceptorIgnore(tenantLine = "true")
    int delete(RepBusinessRemindSo entity);

    /**
     * 添加多个
     *
     * @param list List RepBusinessRemindSo
     * @return int
     */
    @InterceptorIgnore(tenantLine = "true")
    int insertAll(@Param("list") List<RepBusinessRemindSo> list);
}
