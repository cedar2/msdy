package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import com.platform.ems.plug.domain.ConAccountCategory;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBookType;

/**
 * 流水类型_财务Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-29
 */
public interface ConBookTypeMapper  extends BaseMapper<ConBookType> {


    ConBookType selectConBookTypeById(Long sid);

    List<ConBookType> selectConBookTypeList(ConBookType conBookType);

    /**
     * 添加多个
     * @param list List ConBookType
     * @return int
     */
    int inserts(@Param("list") List<ConBookType> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity ConBookType
     * @return int
     */
    int updateAllById(ConBookType entity);

    /**
     * 更新多个
     * @param list List ConBookType
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBookType> list);

    /**
     * 流水类型财务下拉框列表
     */
    List<ConBookType> getConBookTypeList(ConBookType conBookType);
}
