package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SamSampleLendreturnAttach;

/**
 * 样品借还单-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-12-20
 */
public interface SamSampleLendreturnAttachMapper  extends BaseMapper<SamSampleLendreturnAttach> {


    List<SamSampleLendreturnAttach> selectSamSampleLendreturnAttachById(Long lendreturnSid);

    List<SamSampleLendreturnAttach> selectSamSampleLendreturnAttachList(SamSampleLendreturnAttach samSampleLendreturnAttach);

    /**
     * 添加多个
     * @param list List SamSampleLendreturnAttach
     * @return int
     */
    int inserts(@Param("list") List<SamSampleLendreturnAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SamSampleLendreturnAttach
    * @return int
    */
    int updateAllById(SamSampleLendreturnAttach entity);

    /**
     * 更新多个
     * @param list List SamSampleLendreturnAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<SamSampleLendreturnAttach> list);


}
