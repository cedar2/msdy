package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PayProductProcessStepAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品道序-附件Mapper接口
 *
 * @author c
 * @date 2021-09-08
 */
public interface PayProductProcessStepAttachMapper extends BaseMapper<PayProductProcessStepAttach> {


    PayProductProcessStepAttach selectPayProductProcessStepAttachById(Long attachmentSid);

    List<PayProductProcessStepAttach> selectPayProductProcessStepAttachList(PayProductProcessStepAttach payProductProcessStepAttach);

    /**
     * 添加多个
     *
     * @param list List PayProductProcessStepAttach
     * @return int
     */
    int inserts(@Param("list") List<PayProductProcessStepAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PayProductProcessStepAttach
     * @return int
     */
    int updateAllById(PayProductProcessStepAttach entity);

    /**
     * 更新多个
     *
     * @param list List PayProductProcessStepAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<PayProductProcessStepAttach> list);


}
