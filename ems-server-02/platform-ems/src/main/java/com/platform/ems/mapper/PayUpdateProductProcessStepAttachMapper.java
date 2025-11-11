package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PayUpdateProductProcessStepAttach;

/**
 * 商品道序变更-附件Mapper接口
 *
 * @author chenkw
 * @date 2022-11-08
 */
public interface PayUpdateProductProcessStepAttachMapper extends BaseMapper<PayUpdateProductProcessStepAttach> {


    PayUpdateProductProcessStepAttach selectPayUpdateProductProcessStepAttachById(Long attachmentSid);

    List<PayUpdateProductProcessStepAttach> selectPayUpdateProductProcessStepAttachList(PayUpdateProductProcessStepAttach payUpdateProductProcessStepAttach);

    /**
     * 添加多个
     *
     * @param list List PayUpdateProductProcessStepAttach
     * @return int
     */
    int inserts(@Param("list") List<PayUpdateProductProcessStepAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PayUpdateProductProcessStepAttach
     * @return int
     */
    int updateAllById(PayUpdateProductProcessStepAttach entity);

    /**
     * 更新多个
     *
     * @param list List PayUpdateProductProcessStepAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<PayUpdateProductProcessStepAttach> list);


}
