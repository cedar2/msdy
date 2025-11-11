package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.DevMakeSampleFormAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 打样准许单-附件Mapper接口
 *
 * @author linhongwei
 * @date 2022-03-24
 */
public interface DevMakeSampleFormAttachMapper extends BaseMapper<DevMakeSampleFormAttach> {


    DevMakeSampleFormAttach selectDevMakeSampleFormAttachById(Long attachmentSid);

    List<DevMakeSampleFormAttach> selectDevMakeSampleFormAttachList(DevMakeSampleFormAttach devMakeSampleFormAttach);

    /**
     * 添加多个
     *
     * @param list List DevMakeSampleFormAttach
     * @return int
     */
    int inserts(@Param("list") List<DevMakeSampleFormAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity DevMakeSampleFormAttach
     * @return int
     */
    int updateAllById(DevMakeSampleFormAttach entity);

    /**
     * 更新多个
     *
     * @param list List DevMakeSampleFormAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<DevMakeSampleFormAttach> list);


}
