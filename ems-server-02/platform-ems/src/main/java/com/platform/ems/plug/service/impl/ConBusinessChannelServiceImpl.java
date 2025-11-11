package com.platform.ems.plug.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.plug.mapper.ConBusinessChannelMapper;
import com.platform.ems.plug.domain.ConBusinessChannel;
import com.platform.ems.plug.service.IConBusinessChannelService;

/**
 * 销售渠道/业务渠道Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-30
 */
@Service
@SuppressWarnings("all")
public class ConBusinessChannelServiceImpl extends ServiceImpl<ConBusinessChannelMapper,ConBusinessChannel>  implements IConBusinessChannelService {
    @Autowired
    private ConBusinessChannelMapper conBusinessChannelMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "销售渠道/业务渠道";
    /**
     * 查询销售渠道/业务渠道
     *
     * @param sid 销售渠道/业务渠道ID
     * @return 销售渠道/业务渠道
     */
    @Override
    public ConBusinessChannel selectConBusinessChannelById(Long sid) {
        ConBusinessChannel conBusinessChannel = conBusinessChannelMapper.selectConBusinessChannelById(sid);
        MongodbUtil.find(conBusinessChannel);
        return  conBusinessChannel;
    }

    /**
     * 查询销售渠道/业务渠道列表
     *
     * @param conBusinessChannel 销售渠道/业务渠道
     * @return 销售渠道/业务渠道
     */
    @Override
    public List<ConBusinessChannel> selectConBusinessChannelList(ConBusinessChannel conBusinessChannel) {
        return conBusinessChannelMapper.selectConBusinessChannelList(conBusinessChannel);
    }

    /**
     * 新增销售渠道/业务渠道
     * 需要注意编码重复校验
     * @param conBusinessChannel 销售渠道/业务渠道
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBusinessChannel(ConBusinessChannel conBusinessChannel) {
        List<ConBusinessChannel> codeList = conBusinessChannelMapper.selectList(new QueryWrapper<ConBusinessChannel>().lambda()
                .eq(ConBusinessChannel::getCode, conBusinessChannel.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBusinessChannel> nameList = conBusinessChannelMapper.selectList(new QueryWrapper<ConBusinessChannel>().lambda()
                .eq(ConBusinessChannel::getName, conBusinessChannel.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBusinessChannelMapper.insert(conBusinessChannel);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBusinessChannel.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改销售渠道/业务渠道
     *
     * @param conBusinessChannel 销售渠道/业务渠道
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBusinessChannel(ConBusinessChannel conBusinessChannel) {
        ConBusinessChannel response = conBusinessChannelMapper.selectConBusinessChannelById(conBusinessChannel.getSid());
        int row=conBusinessChannelMapper.updateById(conBusinessChannel);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBusinessChannel.getSid(), BusinessType.UPDATE.getValue(), response,conBusinessChannel,TITLE);
        }
        return row;
    }

    /**
     * 变更销售渠道/业务渠道
     *
     * @param conBusinessChannel 销售渠道/业务渠道
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBusinessChannel(ConBusinessChannel conBusinessChannel) {
        List<ConBusinessChannel> nameList = conBusinessChannelMapper.selectList(new QueryWrapper<ConBusinessChannel>().lambda()
                .eq(ConBusinessChannel::getName, conBusinessChannel.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBusinessChannel.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBusinessChannel.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBusinessChannel response = conBusinessChannelMapper.selectConBusinessChannelById(conBusinessChannel.getSid());
        int row=conBusinessChannelMapper.updateAllById(conBusinessChannel);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBusinessChannel.getSid(), BusinessType.CHANGE.getValue(), response,conBusinessChannel,TITLE);
        }
        return row;
    }

    /**
     * 批量删除销售渠道/业务渠道
     *
     * @param sids 需要删除的销售渠道/业务渠道ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBusinessChannelByIds(List<Long> sids) {
        return conBusinessChannelMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     * @param conBusinessChannel
     * @return
     */
    @Override
    public int changeStatus(ConBusinessChannel conBusinessChannel){
        int row=0;
        Long[] sids=conBusinessChannel.getSidList();
        if(sids!=null&&sids.length>0){
            row=conBusinessChannelMapper.update(null, new UpdateWrapper<ConBusinessChannel>().lambda().set(ConBusinessChannel::getStatus ,conBusinessChannel.getStatus() )
                    .in(ConBusinessChannel::getSid,sids));
            for(Long id:sids){
                conBusinessChannel.setSid(id);
                row=conBusinessChannelMapper.updateById( conBusinessChannel);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBusinessChannel.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBusinessChannel.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBusinessChannel
     * @return
     */
    @Override
    public int check(ConBusinessChannel conBusinessChannel){
        int row=0;
        Long[] sids=conBusinessChannel.getSidList();
        if(sids!=null&&sids.length>0){
            row=conBusinessChannelMapper.update(null,new UpdateWrapper<ConBusinessChannel>().lambda().set(ConBusinessChannel::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConBusinessChannel::getSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 下拉框列表
     */
    @Override
    public List<ConBusinessChannel> getConBusinessChannelList(ConBusinessChannel conBusinessChannel) {
        return conBusinessChannelMapper.getConBusinessChannelList(conBusinessChannel);
    }
}
