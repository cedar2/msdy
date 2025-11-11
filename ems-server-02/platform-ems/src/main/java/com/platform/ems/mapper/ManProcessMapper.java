package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManProcess;

/**
 * 工序Mapper接口
 * 
 * @author linhongwei
 * @date 2021-03-26
 */
public interface ManProcessMapper  extends BaseMapper<ManProcess> {


    ManProcess selectManProcessById(Long processSid);

    List<ManProcess> selectManProcessList(ManProcess manProcess);

    /**
     * 添加多个
     * @param list List ManProcess
     * @return int
     */
    int inserts(@Param("list") List<ManProcess> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManProcess
    * @return int
    */
    int updateAllById(ManProcess entity);

    /**
     * 更新多个
     * @param list List ManProcess
     * @return int
     */
    int updatesAllById(@Param("list") List<ManProcess> list);

    /**
     * 查询工序列表
     * @param
     * @return
     */
    List<ManProcess>  getList(ManProcess manProcess);
}
