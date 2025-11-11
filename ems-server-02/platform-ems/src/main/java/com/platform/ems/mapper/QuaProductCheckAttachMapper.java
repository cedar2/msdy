package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.QuaProductCheckAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 成衣检测单-附件Mapper接口
 *
 * @author linhongwei
 * @date 2022-04-13
 */
public interface QuaProductCheckAttachMapper extends BaseMapper<QuaProductCheckAttach> {


    QuaProductCheckAttach selectQuaProductCheckAttachById(Long attachmentSid);

    List<QuaProductCheckAttach> selectQuaProductCheckAttachList(QuaProductCheckAttach quaProductCheckAttach);

    /**
     * 添加多个
     *
     * @param list List QuaProductCheckAttach
     * @return int
     */
    int inserts(@Param("list") List<QuaProductCheckAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity QuaProductCheckAttach
     * @return int
     */
    int updateAllById(QuaProductCheckAttach entity);

    /**
     * 更新多个
     *
     * @param list List QuaProductCheckAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<QuaProductCheckAttach> list);


}
