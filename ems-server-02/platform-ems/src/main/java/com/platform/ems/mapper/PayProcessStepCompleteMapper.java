package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PayProcessStepComplete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 计薪量申报-主Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-08
 */
public interface PayProcessStepCompleteMapper extends BaseMapper<PayProcessStepComplete> {

    PayProcessStepComplete selectPayProcessStepCompleteById(Long stepCompleteSid);

    List<PayProcessStepComplete> selectPayProcessStepCompleteList(PayProcessStepComplete payProcessStepComplete);

    /**
     * 添加多个
     *
     * @param list List PayProcessStepComplete
     * @return int
     */
    int inserts(@Param("list") List<PayProcessStepComplete> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PayProcessStepComplete
     * @return int
     */
    int updateAllById(PayProcessStepComplete entity);

    /**
     * 更新多个
     *
     * @param list List PayProcessStepComplete
     * @return int
     */
    int updatesAllById(@Param("list") List<PayProcessStepComplete> list);


}
