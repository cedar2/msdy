package com.platform.ems.plug.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.plug.domain.ConBcstUserConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知用户配置Mapper接口
 *
 * @author linhongwei
 * @date 2021-10-12
 */
public interface ConBcstUserConfigMapper extends BaseMapper<ConBcstUserConfig> {


    ConBcstUserConfig selectConBcstUserConfigById(Long sid);

    List<ConBcstUserConfig> selectConBcstUserConfigList(ConBcstUserConfig conBcstUserConfig);

    /**
     * 添加多个
     *
     * @param list List ConBcstUserConfig
     * @return int
     */
    int inserts(@Param("list") List<ConBcstUserConfig> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ConBcstUserConfig
     * @return int
     */
    int updateAllById(ConBcstUserConfig entity);

    /**
     * 更新多个
     *
     * @param list List ConBcstUserConfig
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBcstUserConfig> list);


}
