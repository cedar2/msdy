package com.platform.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.system.domain.SysProcessTaskPropertiesConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 流程节点属性配置Mapper接口
 *
 * @author qhq
 * @date 2021-10-11
 */
public interface SysProcessTaskPropertiesConfigMapper  extends BaseMapper<SysProcessTaskPropertiesConfig> {

    /**
     * byId
     * @param id
     * @return
     */
    SysProcessTaskPropertiesConfig selectSysProcessTaskPropertiesConfigById (Long id);

    /**
     * list
     * @param sysProcessTaskPropertiesConfig
     * @return
     */
    List<SysProcessTaskPropertiesConfig> selectSysProcessTaskPropertiesConfigList (SysProcessTaskPropertiesConfig sysProcessTaskPropertiesConfig);

    /**
     * 添加多个
     * @param list List SysProcessTaskPropertiesConfig
     * @return int
     */
    int inserts (@Param("list") List<SysProcessTaskPropertiesConfig> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity SysProcessTaskPropertiesConfig
    * @return int
    */
    int updateAllById (SysProcessTaskPropertiesConfig entity);

    /**
     * 更新多个
     * @param list List SysProcessTaskPropertiesConfig
     * @return int
     */
    int updatesAllById (@Param("list") List<SysProcessTaskPropertiesConfig> list);


}
