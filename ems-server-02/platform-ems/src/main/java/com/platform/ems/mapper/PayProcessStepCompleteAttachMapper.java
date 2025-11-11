package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PayProcessStepCompleteAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 计薪量申报-附件Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-08
 */
public interface PayProcessStepCompleteAttachMapper extends BaseMapper<PayProcessStepCompleteAttach> {


    PayProcessStepCompleteAttach selectPayProcessStepCompleteAttachById(Long attachmentSid);

    List<PayProcessStepCompleteAttach> selectPayProcessStepCompleteAttachList(PayProcessStepCompleteAttach payProcessStepCompleteAttach);

    /**
     * 添加多个
     *
     * @param list List PayProcessStepCompleteAttach
     * @return int
     */
    int inserts(@Param("list") List<PayProcessStepCompleteAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PayProcessStepCompleteAttach
     * @return int
     */
    int updateAllById(PayProcessStepCompleteAttach entity);

    /**
     * 更新多个
     *
     * @param list List PayProcessStepCompleteAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<PayProcessStepCompleteAttach> list);


}
