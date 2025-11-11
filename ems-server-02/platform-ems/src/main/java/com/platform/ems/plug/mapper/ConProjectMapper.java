package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConProject;

/**
 * 项目Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConProjectMapper  extends BaseMapper<ConProject> {


    ConProject selectConProjectById(Long sid);

    List<ConProject> selectConProjectList(ConProject conProject);

    /**
     * 添加多个
     * @param list List ConProject
     * @return int
     */
    int inserts(@Param("list") List<ConProject> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConProject
    * @return int
    */
    int updateAllById(ConProject entity);

    /**
     * 更新多个
     * @param list List ConProject
     * @return int
     */
    int updatesAllById(@Param("list") List<ConProject> list);


}
