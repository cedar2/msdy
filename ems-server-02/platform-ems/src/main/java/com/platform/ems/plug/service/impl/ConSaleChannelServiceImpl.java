package com.platform.ems.plug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConSaleChannel;
import com.platform.ems.plug.mapper.ConSaleChannelMapper;
import com.platform.ems.plug.service.IConSaleChannelService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 销售渠道Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConSaleChannelServiceImpl extends ServiceImpl<ConSaleChannelMapper,ConSaleChannel>  implements IConSaleChannelService {
    @Autowired
    private ConSaleChannelMapper conSaleChannelMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "销售渠道";
    /**
     * 查询销售渠道
     *
     * @param sid 销售渠道ID
     * @return 销售渠道
     */
    @Override
    public ConSaleChannel selectConSaleChannelById(Long sid) {
        ConSaleChannel conSaleChannel = conSaleChannelMapper.selectConSaleChannelById(sid);
        MongodbUtil.find(conSaleChannel);
        return  conSaleChannel;
    }

    /**
     * 查询销售渠道列表
     *
     * @param conSaleChannel 销售渠道
     * @return 销售渠道
     */
    @Override
    public List<ConSaleChannel> selectConSaleChannelList(ConSaleChannel conSaleChannel) {
        return conSaleChannelMapper.selectConSaleChannelList(conSaleChannel);
    }

    /**
     * 新增销售渠道
     * 需要注意编码重复校验
     * @param conSaleChannel 销售渠道
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConSaleChannel(ConSaleChannel conSaleChannel) {
        String name = conSaleChannel.getName();
        String code = conSaleChannel.getCode();
        List<ConSaleChannel> list = conSaleChannelMapper.selectList(new QueryWrapper<ConSaleChannel>().lambda()
                .or().eq(ConSaleChannel::getName, name)
                .or().eq(ConSaleChannel::getCode, code)
        );
        if(CollectionUtils.isNotEmpty(list)){
            throw new CustomException("配置档案已存在相同的名称或编码，不允许重复");
        }
        int row= conSaleChannelMapper.insert(conSaleChannel);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conSaleChannel.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改销售渠道
     *
     * @param conSaleChannel 销售渠道
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConSaleChannel(ConSaleChannel conSaleChannel) {
        ConSaleChannel response = conSaleChannelMapper.selectConSaleChannelById(conSaleChannel.getSid());
        int row=conSaleChannelMapper.updateById(conSaleChannel);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conSaleChannel.getSid(), BusinessType.UPDATE.ordinal(), response,conSaleChannel,TITLE);
        }
        return row;
    }

    /**
     * 变更销售渠道
     *
     * @param conSaleChannel 销售渠道
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConSaleChannel(ConSaleChannel conSaleChannel) {
        String name = conSaleChannel.getName();
        ConSaleChannel item = conSaleChannelMapper.selectOne(new QueryWrapper<ConSaleChannel>().lambda()
                .eq(ConSaleChannel::getName, name)
        );
        if (item != null && !item.getSid().equals(conSaleChannel.getSid())) {
            throw new CustomException("配置档案已存在相同的名称，不允许重复");
        }
        ConSaleChannel response = conSaleChannelMapper.selectConSaleChannelById(conSaleChannel.getSid());
        int row = conSaleChannelMapper.updateAllById(conSaleChannel);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conSaleChannel.getSid(), BusinessType.CHANGE.ordinal(), response, conSaleChannel, TITLE);
        }
        return row;
    }

    /**
     * 批量删除销售渠道
     *
     * @param sids 需要删除的销售渠道ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConSaleChannelByIds(List<Long> sids) {
        return conSaleChannelMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conSaleChannel
    * @return
    */
    @Override
    public int changeStatus(ConSaleChannel conSaleChannel){
        int row=0;
        Long[] sids=conSaleChannel.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conSaleChannel.setSid(id);
                row=conSaleChannelMapper.updateById( conSaleChannel);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conSaleChannel.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conSaleChannel.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conSaleChannel
     * @return
     */
    @Override
    public int check(ConSaleChannel conSaleChannel){
        int row=0;
        Long[] sids=conSaleChannel.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conSaleChannel.setSid(id);
                row=conSaleChannelMapper.updateById( conSaleChannel);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conSaleChannel.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConSaleChannel> getConSaleChannelList() {
        return conSaleChannelMapper.getConSaleChannelList();
    }
}
