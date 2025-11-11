package com.platform.ems.plug.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.plug.domain.ConSaleChannel;

/**
 * 销售渠道Mapper接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface ConSaleChannelMapper  extends BaseMapper<ConSaleChannel> {


    ConSaleChannel selectConSaleChannelById(Long sid);

    List<ConSaleChannel> selectConSaleChannelList(ConSaleChannel conSaleChannel);

    /**
     * 添加多个
     * @param list List ConSaleChannel
     * @return int
     */
    int inserts(@Param("list") List<ConSaleChannel> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ConSaleChannel
    * @return int
    */
    int updateAllById(ConSaleChannel entity);

    /**
     * 更新多个
     * @param list List ConSaleChannel
     * @return int
     */
    int updatesAllById(@Param("list") List<ConSaleChannel> list);

    /** 获取下拉列表 */
    List<ConSaleChannel> getConSaleChannelList();
}
