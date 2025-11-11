package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConBusinessChannel;

/**
 * 销售渠道/业务渠道Mapper接口
 *
 * @author linhongwei
 * @date 2021-06-30
 */
public interface ConBusinessChannelMapper  extends BaseMapper<ConBusinessChannel> {


    ConBusinessChannel selectConBusinessChannelById(Long sid);

    List<ConBusinessChannel> selectConBusinessChannelList(ConBusinessChannel conBusinessChannel);

    /**
     * 添加多个
     * @param list List ConBusinessChannel
     * @return int
     */
    int inserts(@Param("list") List<ConBusinessChannel> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity ConBusinessChannel
     * @return int
     */
    int updateAllById(ConBusinessChannel entity);

    /**
     * 更新多个
     * @param list List ConBusinessChannel
     * @return int
     */
    int updatesAllById(@Param("list") List<ConBusinessChannel> list);

    /**
     * 下拉框列表
     */
    List<ConBusinessChannel> getConBusinessChannelList(ConBusinessChannel conBusinessChannel);
}
