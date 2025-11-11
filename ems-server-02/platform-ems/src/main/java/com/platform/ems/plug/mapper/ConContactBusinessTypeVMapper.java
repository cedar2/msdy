package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConContactBusinessTypeV;

/**
 * 对接业务类型_供应商Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConContactBusinessTypeVMapper  extends BaseMapper<ConContactBusinessTypeV> {


    ConContactBusinessTypeV selectConContactBusinessTypeVById(Long sid);

    List<ConContactBusinessTypeV> selectConContactBusinessTypeVList(ConContactBusinessTypeV conContactBusinessTypeV);

    /**
     * 添加多个
     * @param list List ConContactBusinessTypeV
     * @return int
     */
    int inserts(@Param("list") List<ConContactBusinessTypeV> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConContactBusinessTypeV
    * @return int
    */
    int updateAllById(ConContactBusinessTypeV entity);

    /**
     * 更新多个
     * @param list List ConContactBusinessTypeV
     * @return int
     */
    int updatesAllById(@Param("list") List<ConContactBusinessTypeV> list);


}
