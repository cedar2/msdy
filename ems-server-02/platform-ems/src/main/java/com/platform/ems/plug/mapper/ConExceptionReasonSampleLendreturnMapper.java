package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConExceptionReasonSampleLendreturn;

/**
 * 异常明细配置Mapper接口
 * 
 * @author yangqz
 * @date 2022-04-25
 */
public interface ConExceptionReasonSampleLendreturnMapper  extends BaseMapper<ConExceptionReasonSampleLendreturn> {


    ConExceptionReasonSampleLendreturn selectConExceptionReasonSampleLendreturnById(Long sid);

    List<ConExceptionReasonSampleLendreturn> getList(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn);
    List<ConExceptionReasonSampleLendreturn> selectConExceptionReasonSampleLendreturnList(ConExceptionReasonSampleLendreturn conExceptionReasonSampleLendreturn);

    /**
     * 添加多个
     * @param list List ConExceptionReasonSampleLendreturn
     * @return int
     */
    int inserts(@Param("list") List<ConExceptionReasonSampleLendreturn> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConExceptionReasonSampleLendreturn
    * @return int
    */
    int updateAllById(ConExceptionReasonSampleLendreturn entity);

    /**
     * 更新多个
     * @param list List ConExceptionReasonSampleLendreturn
     * @return int
     */
    int updatesAllById(@Param("list") List<ConExceptionReasonSampleLendreturn> list);


}
