package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.BasMaterialAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物料&商品-附件Mapper接口
 *
 * @author linhongwei
 * @date 2021-03-12
 */
public interface BasMaterialAttachmentMapper extends BaseMapper<BasMaterialAttachment> {


    BasMaterialAttachment selectBasMaterialAttachmentById(Long materialAttachmentSid);

    List<BasMaterialAttachment> selectBasMaterialAttachmentList(BasMaterialAttachment basMaterialAttachment);

    /**
     * 添加多个
     *
     * @param list List BasMaterialAttachment
     * @return int
     */
    int inserts(@Param("list") List<BasMaterialAttachment> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity BasMaterialAttachment
     * @return int
     */
    int updateAllById(BasMaterialAttachment entity);

    /**
     * 更新多个
     *
     * @param list List BasMaterialAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<BasMaterialAttachment> list);


    void deleteBasMaterialAttachmentByIds(@Param("array")Long[] materialSids);
}
