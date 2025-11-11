package com.platform.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.system.domain.SysProcessTaskConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 流程任务节点个性化配置参数Mapper接口
 *
 * @author qhq
 * @date 2021-10-11
 */
public interface SysProcessTaskConfigMapper  extends BaseMapper<SysProcessTaskConfig> {

    /**
     * byId
     * @param id
     * @return
     */
    SysProcessTaskConfig selectSysProcessTaskConfigById (Long id);

    /**
     * list
     * @param sysProcessTaskConfig
     * @return
     */
    List<SysProcessTaskConfig> selectSysProcessTaskConfigList (SysProcessTaskConfig sysProcessTaskConfig);

    /**
     * 添加多个
     * @param list List SysProcessTaskConfig
     * @return int
     */
    int inserts (@Param("list") List<SysProcessTaskConfig> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SysProcessTaskConfig
    * @return int
    */
    int updateAllById (SysProcessTaskConfig entity);

    /**
     * 更新多个
     * @param list List SysProcessTaskConfig
     * @return int
     */
    int updatesAllById (@Param("list") List<SysProcessTaskConfig> list);


}
