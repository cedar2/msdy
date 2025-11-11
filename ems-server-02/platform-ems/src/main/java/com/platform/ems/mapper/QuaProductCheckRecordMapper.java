package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.QuaProductCheckRecord;

/**
 * 商品检测问题台账Mapper接口
 *
 * @author admin
 * @date 2024-03-06
 */
public interface QuaProductCheckRecordMapper extends BaseMapper<QuaProductCheckRecord> {

    QuaProductCheckRecord selectQuaProductCheckRecordById(Long productCheckRecordSid);

    List<QuaProductCheckRecord> selectQuaProductCheckRecordList(QuaProductCheckRecord quaProductCheckRecord);

    /**
     * 添加多个
     */
    int inserts(@Param("list") List<QuaProductCheckRecord> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     */
    int updateAllById(QuaProductCheckRecord entity);

    /**
     * 更新多个
     */
    int updatesAllById(@Param("list") List<QuaProductCheckRecord> list);

}
