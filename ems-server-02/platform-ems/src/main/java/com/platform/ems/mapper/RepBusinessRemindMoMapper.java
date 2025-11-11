package com.platform.ems.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepBusinessRemindMo;

/**
 * 已逾期/即将到期-生产订单Mapper接口
 *
 * @author chenkw
 * @date 2022-04-26
 */
public interface RepBusinessRemindMoMapper extends BaseMapper<RepBusinessRemindMo> {


    RepBusinessRemindMo selectRepBusinessRemindMoById(Long dataRecordSid);

    List<RepBusinessRemindMo> selectRepBusinessRemindMoList(RepBusinessRemindMo repBusinessRemindMo);

    /**
     * 添加多个
     *
     * @param list List RepBusinessRemindMo
     * @return int
     */
    int inserts(@Param("list") List<RepBusinessRemindMo> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity RepBusinessRemindMo
     * @return int
     */
    int updateAllById(RepBusinessRemindMo entity);

    /**
     * 更新多个
     *
     * @param list List RepBusinessRemindMo
     * @return int
     */
    int updatesAllById(@Param("list") List<RepBusinessRemindMo> list);

    /**
     * 自动定时任务
     *
     * 得到已逾期/即将到期生产订单报表
     *
     * @param repBusinessRemindMo 已逾期/即将到期生产订单报表
     * @return 已逾期/即将到期生产订单
     */
    @InterceptorIgnore(tenantLine = "true")
    List<RepBusinessRemindMo> getFormData(RepBusinessRemindMo repBusinessRemindMo);

    /**
     * 自动定时任务
     *
     * 批量删除
     *
     * @param entity RepBusinessRemindMo
     * @return int
     */
    @InterceptorIgnore(tenantLine = "true")
    int delete(RepBusinessRemindMo entity);

    /**
     * 自动定时任务
     *
     * 添加多个
     *
     * @param list List RepBusinessRemindMo
     * @return int
     */
    @InterceptorIgnore(tenantLine = "true")
    int insertAll(@Param("list") List<RepBusinessRemindMo> list);

    /**
     * 查询已逾期/即将到期生产订单报表
     *
     * @param repBusinessRemindMo 已逾期/即将到期生产订单报表
     * @return 已逾期/即将到期生产订单
     */
    List<RepBusinessRemindMo> getCountForm(RepBusinessRemindMo repBusinessRemindMo);
}
