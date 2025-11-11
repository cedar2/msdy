package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConFileType;

/**
 * 附件类型Mapper接口
 *
 * @author chenkw
 * @date 2021-07-05
 */
@SuppressWarnings("all")
public interface ConFileTypeMapper extends BaseMapper<ConFileType> {


    ConFileType selectConFileTypeById(Long sid);

    List<ConFileType> selectConFileTypeList(ConFileType conFileType);

    /**
     * 添加多个
     *
     * @param list List ConFileType
     * @return int
     */
    int inserts(@Param("list") List<ConFileType> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConFileType
     * @return int
     */
    int updateAllById(ConFileType entity);

    /**
     * 更新多个
     *
     * @param list List ConFileType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConFileType> list);

    /**
     * 文件类型下拉框列表
     */
    List<ConFileType> getConFileTypeList();
}
