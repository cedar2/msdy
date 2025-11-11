package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManManufactureOrderConcernTaskAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 生产订单关注事项-附件Mapper接口
 *
 * @author linhongwei
 * @date 2021-10-20
 */
public interface ManManufactureOrderConcernTaskAttachMapper extends BaseMapper<ManManufactureOrderConcernTaskAttach> {


    ManManufactureOrderConcernTaskAttach selectManManufactureOrderConcernTaskAttachById(Long attachmentSid);

    List<ManManufactureOrderConcernTaskAttach> selectManManufactureOrderConcernTaskAttachList(ManManufactureOrderConcernTaskAttach ManManufactureOrderConcernTaskAttach);

    /**
     * 添加多个
     *
     * @param list List ManManufactureOrderConcernTaskAttach
     * @return int
     */
    int inserts(@Param("list") List<ManManufactureOrderConcernTaskAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManManufactureOrderConcernTaskAttach
     * @return int
     */
    int updateAllById(ManManufactureOrderConcernTaskAttach entity);

    /**
     * 更新多个
     *
     * @param list List ManManufactureOrderConcernTaskAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<ManManufactureOrderConcernTaskAttach> list);


}
