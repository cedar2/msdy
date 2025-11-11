package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.SamSampleLendreturn;

/**
 * 样品借还单-主Mapper接口
 * 
 * @author linhongwei
 * @date 2021-12-20
 */
public interface SamSampleLendreturnMapper  extends BaseMapper<SamSampleLendreturn> {


    SamSampleLendreturn selectSamSampleLendreturnById(Long lendreturnSid);

    List<SamSampleLendreturn> selectSamSampleLendreturnList(SamSampleLendreturn samSampleLendreturn);

    /**
     * 添加多个
     * @param list List SamSampleLendreturn
     * @return int
     */
    int inserts(@Param("list") List<SamSampleLendreturn> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SamSampleLendreturn
    * @return int
    */
    int updateAllById(SamSampleLendreturn entity);

    /**
     * 更新多个
     * @param list List SamSampleLendreturn
     * @return int
     */
    int updatesAllById(@Param("list") List<SamSampleLendreturn> list);


}
