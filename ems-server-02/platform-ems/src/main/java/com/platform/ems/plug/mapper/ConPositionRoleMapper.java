package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConPositionRole;

/**
 * 工作角色Mapper接口
 * 
 * @author chenkw
 * @date 2021-05-20
 */
public interface ConPositionRoleMapper  extends BaseMapper<ConPositionRole> {


    ConPositionRole selectConPositionRoleById(Long sid);

    List<ConPositionRole> selectConPositionRoleList(ConPositionRole conPositionRole);

    /**
     * 添加多个
     * @param list List ConPositionRole
     * @return int
     */
    int inserts(@Param("list") List<ConPositionRole> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConPositionRole
    * @return int
    */
    int updateAllById(ConPositionRole entity);

    /**
     * 更新多个
     * @param list List ConPositionRole
     * @return int
     */
    int updatesAllById(@Param("list") List<ConPositionRole> list);


}
