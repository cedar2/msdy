package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ConCheckStandard;

/**
 * 检测标准Mapper接口
 * 
 * @author qhq
 * @date 2021-11-01
 */
public interface ConCheckStandardMapper  extends BaseMapper<ConCheckStandard> {


    ConCheckStandard selectConCheckStandardById (Long sid);

    List<ConCheckStandard> selectConCheckStandardList (ConCheckStandard conCheckStandard);

    /**
     * 添加多个
     * @param list List ConCheckStandard
     * @return int
     */
    int inserts (@Param("list") List<ConCheckStandard> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConCheckStandard
    * @return int
    */
    int updateAllById (ConCheckStandard entity);

    /**
     * 更新多个
     * @param list List ConCheckStandard
     * @return int
     */
    int updatesAllById (@Param("list") List<ConCheckStandard> list);


}
