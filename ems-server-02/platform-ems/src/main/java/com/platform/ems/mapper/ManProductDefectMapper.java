package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.domain.dto.request.ManProductDefectRequest;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManProductDefect;

/**
 * 生产产品缺陷登记Mapper接口
 * 
 * @author zhuangyz
 * @date 2022-08-04
 */
public interface ManProductDefectMapper  extends BaseMapper<ManProductDefect> {


    ManProductDefect selectManProductDefectById(Long productDefectSid);

    List<ManProductDefect> selectManProductDefectList(ManProductDefect manProductDefect);

    /**
     * 添加多个
     * @param list List ManProductDefect
     * @return int
     */
    int inserts(@Param("list") List<ManProductDefect> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManProductDefect
    * @return int
    */
    int updateAllById(ManProductDefect entity);

    int updateStatus(ManProductDefectRequest entity);

    /**
     * 更新多个
     * @param list List ManProductDefect
     * @return int
     */
    int updatesAllById(@Param("list") List<ManProductDefect> list);


}
