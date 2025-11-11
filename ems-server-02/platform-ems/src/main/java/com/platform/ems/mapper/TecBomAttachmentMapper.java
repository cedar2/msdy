package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.TecBomAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * BOM附件Mapper接口
 *
 * @author qhq
 * @date 2021-03-15
 */
public interface TecBomAttachmentMapper  extends BaseMapper<TecBomAttachment> {


    TecBomAttachment selectTecBomAttachmentById(String bomAttachmentSid);

    List<TecBomAttachment> selectTecBomAttachmentList(TecBomAttachment tecBomAttachment);

    /**
     * 添加多个
     * @param list List TecBomAttachment
     * @return int
     */
    int inserts(@Param("list") List<TecBomAttachment> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity TecBomAttachment
     * @return int
     */
    int updateAllById(TecBomAttachment entity);

    /**
     * 更新多个
     * @param list List TecBomAttachment
     * @return int
     */
    int updatesAllById(@Param("list") List<TecBomAttachment> list);

    /**
     * 根据bomSid查询相关数据
     * @param bomSid
     * @return
     */
    List<TecBomAttachment> selectAttachmentByBomSid(Long bomSid);

    /**
     * 根据bomid删除相关数据
     */
    int deleteAttachmentByBomId(Long bomId);

}
