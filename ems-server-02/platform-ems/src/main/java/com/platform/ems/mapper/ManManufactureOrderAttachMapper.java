package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ManManufactureOrderAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 生产订单-附件Mapper接口
 *
 * @author linhongwei
 * @date 2021-10-20
 */
public interface ManManufactureOrderAttachMapper extends BaseMapper<ManManufactureOrderAttach> {


    ManManufactureOrderAttach selectManManufactureOrderAttachById(Long attachmentSid);

    List<ManManufactureOrderAttach> selectManManufactureOrderAttachList(ManManufactureOrderAttach manManufactureOrderAttach);

    /**
     * 添加多个
     *
     * @param list List ManManufactureOrderAttach
     * @return int
     */
    int inserts(@Param("list") List<ManManufactureOrderAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ManManufactureOrderAttach
     * @return int
     */
    int updateAllById(ManManufactureOrderAttach entity);

    /**
     * 更新多个
     *
     * @param list List ManManufactureOrderAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<ManManufactureOrderAttach> list);


}
