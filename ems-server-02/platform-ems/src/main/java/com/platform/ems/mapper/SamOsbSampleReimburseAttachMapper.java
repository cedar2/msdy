package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.SamOsbSampleReimburseAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 外采样报销单-附件Mapper接口
 *
 * @author qhq
 * @date 2021-12-28
 */
public interface SamOsbSampleReimburseAttachMapper  extends BaseMapper<SamOsbSampleReimburseAttach> {


    SamOsbSampleReimburseAttach selectSamOsbSampleReimburseAttachById (Long attachmentSid);

    List<SamOsbSampleReimburseAttach> selectSamOsbSampleReimburseAttachList (SamOsbSampleReimburseAttach samOsbSampleReimburseAttach);

    /**
     * 添加多个
     * @param list List SamOsbSampleReimburseAttach
     * @return int
     */
    int inserts (@Param("list") List<SamOsbSampleReimburseAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SamOsbSampleReimburseAttach
    * @return int
    */
    int updateAllById (SamOsbSampleReimburseAttach entity);

    /**
     * 更新多个
     * @param list List SamOsbSampleReimburseAttach
     * @return int
     */
    int updatesAllById (@Param("list") List<SamOsbSampleReimburseAttach> list);


}
