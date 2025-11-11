package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBusinessChannel;

/**
 * 销售渠道/业务渠道Service接口
 *
 * @author linhongwei
 * @date 2021-06-30
 */
public interface IConBusinessChannelService extends IService<ConBusinessChannel>{
    /**
     * 查询销售渠道/业务渠道
     *
     * @param sid 销售渠道/业务渠道ID
     * @return 销售渠道/业务渠道
     */
    public ConBusinessChannel selectConBusinessChannelById(Long sid);

    /**
     * 查询销售渠道/业务渠道列表
     *
     * @param conBusinessChannel 销售渠道/业务渠道
     * @return 销售渠道/业务渠道集合
     */
    public List<ConBusinessChannel> selectConBusinessChannelList(ConBusinessChannel conBusinessChannel);

    /**
     * 新增销售渠道/业务渠道
     *
     * @param conBusinessChannel 销售渠道/业务渠道
     * @return 结果
     */
    public int insertConBusinessChannel(ConBusinessChannel conBusinessChannel);

    /**
     * 修改销售渠道/业务渠道
     *
     * @param conBusinessChannel 销售渠道/业务渠道
     * @return 结果
     */
    public int updateConBusinessChannel(ConBusinessChannel conBusinessChannel);

    /**
     * 变更销售渠道/业务渠道
     *
     * @param conBusinessChannel 销售渠道/业务渠道
     * @return 结果
     */
    public int changeConBusinessChannel(ConBusinessChannel conBusinessChannel);

    /**
     * 批量删除销售渠道/业务渠道
     *
     * @param sids 需要删除的销售渠道/业务渠道ID
     * @return 结果
     */
    public int deleteConBusinessChannelByIds(List<Long>  sids);

    /**
     * 启用/停用
     * @param conBusinessChannel
     * @return
     */
    int changeStatus(ConBusinessChannel conBusinessChannel);

    /**
     * 更改确认状态
     * @param conBusinessChannel
     * @return
     */
    int check(ConBusinessChannel conBusinessChannel);

    /**
     * 下拉框列表
     */
    List<ConBusinessChannel> getConBusinessChannelList(ConBusinessChannel conBusinessChannel);
}
