package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConDepositArea;

/**
 * 投料区域Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConDepositAreaMapper  extends BaseMapper<ConDepositArea> {


    ConDepositArea selectConDepositAreaById(Long sid);

    List<ConDepositArea> selectConDepositAreaList(ConDepositArea conDepositArea);

    /**
     * 添加多个
     * @param list List ConDepositArea
     * @return int
     */
    int inserts(@Param("list") List<ConDepositArea> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConDepositArea
    * @return int
    */
    int updateAllById(ConDepositArea entity);

    /**
     * 更新多个
     * @param list List ConDepositArea
     * @return int
     */
    int updatesAllById(@Param("list") List<ConDepositArea> list);


}
