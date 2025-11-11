package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConReferenceProject;

/**
 * 业务归属项目Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConReferenceProjectMapper  extends BaseMapper<ConReferenceProject> {


    ConReferenceProject selectConReferenceProjectById(Long sid);

    List<ConReferenceProject> selectConReferenceProjectList(ConReferenceProject conReferenceProject);

    /**
     * 添加多个
     * @param list List ConReferenceProject
     * @return int
     */
    int inserts(@Param("list") List<ConReferenceProject> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConReferenceProject
    * @return int
    */
    int updateAllById(ConReferenceProject entity);

    /**
     * 更新多个
     * @param list List ConReferenceProject
     * @return int
     */
    int updatesAllById(@Param("list") List<ConReferenceProject> list);


}
