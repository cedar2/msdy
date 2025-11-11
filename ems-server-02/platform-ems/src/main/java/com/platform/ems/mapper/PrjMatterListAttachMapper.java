package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import com.platform.ems.domain.PrjMatterListAttach;
import org.apache.ibatis.annotations.Param;

/**
 * 事项清单-附件Mapper接口
 *
 * @author platform
 * @date 2023-11-22
 */
public interface PrjMatterListAttachMapper extends BaseMapper<PrjMatterListAttach> {

    /**
     * 查询详情
     *
     * @param matterListAttachSid 单据sid
     * @return PrjMatterListAttach
     */
    PrjMatterListAttach selectPrjMatterListAttachById(Long matterListAttachSid);

    /**
     * 查询列表
     *
     * @param prjMatterListAttach PrjMatterListAttach
     * @return List
     */
    List<PrjMatterListAttach> selectPrjMatterListAttachList(PrjMatterListAttach prjMatterListAttach);

    /**
     * 添加多个
     *
     * @param list List PrjMatterListAttach
     * @return int
     */
    int inserts(@Param("list") List<PrjMatterListAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PrjMatterListAttach
     * @return int
     */
    int updateAllById(PrjMatterListAttach entity);

    /**
     * 更新多个
     *
     * @param list List PrjMatterListAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<PrjMatterListAttach> list);

}
