package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManProductDefectAttach;

/**
 * 生产产品缺陷登记-附件Mapper接口
 * 
 * @author zhuangyz
 * @date 2022-08-04
 */
public interface ManProductDefectAttachMapper  extends BaseMapper<ManProductDefectAttach> {


    ManProductDefectAttach selectManProductDefectAttachById(Long attachSid);

    List<ManProductDefectAttach> selectManProductDefectAttachList(ManProductDefectAttach manProductDefectAttach);

    /**
     * 添加多个
     * @param list List ManProductDefectAttach
     * @return int
     */
    int inserts(@Param("list") List<ManProductDefectAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManProductDefectAttach
    * @return int
    */
    int updateAllById(ManProductDefectAttach entity);

    /**
     * 更新多个
     * @param list List ManProductDefectAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<ManProductDefectAttach> list);


}
