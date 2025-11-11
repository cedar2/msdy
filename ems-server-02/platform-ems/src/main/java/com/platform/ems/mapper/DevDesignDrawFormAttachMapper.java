package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.DevDesignDrawFormAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图稿批复单-附件Mapper接口
 *
 * @author qhq
 * @date 2021-11-05
 */
public interface DevDesignDrawFormAttachMapper extends BaseMapper<DevDesignDrawFormAttach> {


    DevDesignDrawFormAttach selectDevDesignDrawFormAttachById(Long attachmentSid);

    List<DevDesignDrawFormAttach> selectDevDesignDrawFormAttachList(DevDesignDrawFormAttach devDesignDrawFormAttach);

    /**
     * 添加多个
     *
     * @param list List DevDesignDrawFormAttach
     * @return int
     */
    int inserts(@Param("list") List<DevDesignDrawFormAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity DevDesignDrawFormAttach
     * @return int
     */
    int updateAllById(DevDesignDrawFormAttach entity);

    /**
     * 更新多个
     *
     * @param list List DevDesignDrawFormAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<DevDesignDrawFormAttach> list);


}
