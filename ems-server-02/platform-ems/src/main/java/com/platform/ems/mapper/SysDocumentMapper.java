package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SysDocument;

/**
 * 文档管理Mapper接口
 *
 * @author chenkw
 * @date 2023-02-13
 */
public interface SysDocumentMapper extends BaseMapper<SysDocument> {

    SysDocument selectSysDocumentById(Long documentSid);

    List<SysDocument> selectSysDocumentList(SysDocument sysDocument);

    /**
     * 添加多个
     *
     * @param list List SysDocument
     * @return int
     */
    int inserts(@Param("list") List<SysDocument> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity SysDocument
     * @return int
     */
    int updateAllById(SysDocument entity);

    /**
     * 更新多个
     *
     * @param list List SysDocument
     * @return int
     */
    int updatesAllById(@Param("list") List<SysDocument> list);

}
