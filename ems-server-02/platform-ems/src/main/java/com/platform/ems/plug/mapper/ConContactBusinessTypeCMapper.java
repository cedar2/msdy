package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConContactBusinessTypeC;

/**
 * 对接业务类型_客户Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConContactBusinessTypeCMapper  extends BaseMapper<ConContactBusinessTypeC> {


    ConContactBusinessTypeC selectConContactBusinessTypeCById(Long sid);

    List<ConContactBusinessTypeC> selectConContactBusinessTypeCList(ConContactBusinessTypeC conContactBusinessTypeC);

    /**
     * 添加多个
     * @param list List ConContactBusinessTypeC
     * @return int
     */
    int inserts(@Param("list") List<ConContactBusinessTypeC> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConContactBusinessTypeC
    * @return int
    */
    int updateAllById(ConContactBusinessTypeC entity);

    /**
     * 更新多个
     * @param list List ConContactBusinessTypeC
     * @return int
     */
    int updatesAllById(@Param("list") List<ConContactBusinessTypeC> list);


}
