package com.platform.ems.plug.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.plug.domain.ConBuTypeSalesOrder;
import com.platform.ems.plug.domain.ConSaleChannel;

/**
 * 销售渠道Service接口
 *
 * @author linhongwei
 * @date 2021-05-19
 */
public interface IConSaleChannelService extends IService<ConSaleChannel>{
    /**
     * 查询销售渠道
     *
     * @param sid 销售渠道ID
     * @return 销售渠道
     */
    public ConSaleChannel selectConSaleChannelById(Long sid);

    /**
     * 查询销售渠道列表
     *
     * @param conSaleChannel 销售渠道
     * @return 销售渠道集合
     */
    public List<ConSaleChannel> selectConSaleChannelList(ConSaleChannel conSaleChannel);

    /**
     * 新增销售渠道
     *
     * @param conSaleChannel 销售渠道
     * @return 结果
     */
    public int insertConSaleChannel(ConSaleChannel conSaleChannel);

    /**
     * 修改销售渠道
     *
     * @param conSaleChannel 销售渠道
     * @return 结果
     */
    public int updateConSaleChannel(ConSaleChannel conSaleChannel);

    /**
     * 变更销售渠道
     *
     * @param conSaleChannel 销售渠道
     * @return 结果
     */
    public int changeConSaleChannel(ConSaleChannel conSaleChannel);

    /**
     * 批量删除销售渠道
     *
     * @param sids 需要删除的销售渠道ID
     * @return 结果
     */
    public int deleteConSaleChannelByIds(List<Long> sids);

    /**
    * 启用/停用
    * @param conSaleChannel
    * @return
    */
    int changeStatus(ConSaleChannel conSaleChannel);

    /**
     * 更改确认状态
     * @param conSaleChannel
     * @return
     */
    int check(ConSaleChannel conSaleChannel);

    /**  获取下拉列表 */
    List<ConSaleChannel> getConSaleChannelList();
}
