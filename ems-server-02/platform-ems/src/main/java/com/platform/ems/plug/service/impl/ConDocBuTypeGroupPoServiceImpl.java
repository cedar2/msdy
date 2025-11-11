package com.platform.ems.plug.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.plug.domain.ConDocBuTypeGroupSo;
import com.platform.ems.util.MongodbDeal;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.plug.mapper.ConDocBuTypeGroupPoMapper;
import com.platform.ems.plug.domain.ConDocBuTypeGroupPo;
import com.platform.ems.plug.service.IConDocBuTypeGroupPoService;

/**
 * 采购订单单据类型与业务类型组合关系Service业务层处理
 *
 * @author chenkw
 * @date 2021-12-24
 */
@Service
@SuppressWarnings("all")
public class ConDocBuTypeGroupPoServiceImpl extends ServiceImpl<ConDocBuTypeGroupPoMapper,ConDocBuTypeGroupPo>  implements IConDocBuTypeGroupPoService {
    @Autowired
    private ConDocBuTypeGroupPoMapper conDocBuTypeGroupPoMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "采购订单单据类型与业务类型组合关系";
    /**
     * 查询采购订单单据类型与业务类型组合关系
     *
     * @param sid 采购订单单据类型与业务类型组合关系ID
     * @return 采购订单单据类型与业务类型组合关系
     */
    @Override
    public ConDocBuTypeGroupPo selectConDocBuTypeGroupPoById(Long sid) {
        ConDocBuTypeGroupPo conDocBuTypeGroupPo = conDocBuTypeGroupPoMapper.selectConDocBuTypeGroupPoById(sid);
        MongodbUtil.find(conDocBuTypeGroupPo);
        return  conDocBuTypeGroupPo;
    }

    /**
     * 查询采购订单单据类型与业务类型组合关系列表
     *
     * @param conDocBuTypeGroupPo 采购订单单据类型与业务类型组合关系
     * @return 采购订单单据类型与业务类型组合关系
     */
    @Override
    public List<ConDocBuTypeGroupPo> selectConDocBuTypeGroupPoList(ConDocBuTypeGroupPo conDocBuTypeGroupPo) {
        return conDocBuTypeGroupPoMapper.selectConDocBuTypeGroupPoList(conDocBuTypeGroupPo);
    }

    /**
     * 新增采购订单单据类型与业务类型组合关系
     * 需要注意编码重复校验
     * @param conDocBuTypeGroupPo 采购订单单据类型与业务类型组合关系
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocBuTypeGroupPo(ConDocBuTypeGroupPo conDocBuTypeGroupPo) {
        //校验
        setConfirm(conDocBuTypeGroupPo);
        int row= conDocBuTypeGroupPoMapper.insert(conDocBuTypeGroupPo);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbDeal.insert(conDocBuTypeGroupPo.getSid(), conDocBuTypeGroupPo.getHandleStatus(), msgList,TITLE,null);
        }
        return row;
    }

    /**
     * 修改采购订单单据类型与业务类型组合关系
     *
     * @param conDocBuTypeGroupPo 采购订单单据类型与业务类型组合关系
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocBuTypeGroupPo(ConDocBuTypeGroupPo conDocBuTypeGroupPo) {
        ConDocBuTypeGroupPo response = conDocBuTypeGroupPoMapper.selectConDocBuTypeGroupPoById(conDocBuTypeGroupPo.getSid());
        //校验
        setConfirm(conDocBuTypeGroupPo);
        int row=conDocBuTypeGroupPoMapper.updateById(conDocBuTypeGroupPo);
        if(row>0){
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(conDocBuTypeGroupPo, response);
            MongodbDeal.update(conDocBuTypeGroupPo.getSid(), response.getHandleStatus(), conDocBuTypeGroupPo.getHandleStatus(), msgList, TITLE,null);
        }
        return row;
    }

    /**
     * 变更采购订单单据类型与业务类型组合关系
     *
     * @param conDocBuTypeGroupPo 采购订单单据类型与业务类型组合关系
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocBuTypeGroupPo(ConDocBuTypeGroupPo conDocBuTypeGroupPo) {
        ConDocBuTypeGroupPo response = conDocBuTypeGroupPoMapper.selectConDocBuTypeGroupPoById(conDocBuTypeGroupPo.getSid());
        //校验
        setConfirm(conDocBuTypeGroupPo);
        int row=conDocBuTypeGroupPoMapper.updateAllById(conDocBuTypeGroupPo);
        if(row>0){
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(conDocBuTypeGroupPo, response);
            MongodbDeal.update(conDocBuTypeGroupPo.getSid(), conDocBuTypeGroupPo.getHandleStatus(),response.getHandleStatus(), msgList, TITLE,null);
        }
        return row;
    }

    /**
     * 批量删除采购订单单据类型与业务类型组合关系
     *
     * @param sids 需要删除的采购订单单据类型与业务类型组合关系ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocBuTypeGroupPoByIds(List<Long> sids) {
        int row = conDocBuTypeGroupPoMapper.deleteBatchIds(sids);
        if (row > 0){
            sids.forEach(sid->{
                ConDocBuTypeGroupPo item = conDocBuTypeGroupPoMapper.selectById(sid);
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
//                msgList = BeanUtils.eq(item, item);
                MongodbUtil.insertUserLog(sid,BusinessType.DELETE.getValue(),msgList,TITLE);
            });
        }
        return row;
    }

    /**
    * 启用/停用
    * @param conDocBuTypeGroupPo
    * @return
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ConDocBuTypeGroupPo conDocBuTypeGroupPo){
        int row=0;
        Long[] sids=conDocBuTypeGroupPo.getSidList();
        if(sids!=null&&sids.length>0){
            row=conDocBuTypeGroupPoMapper.update(null, new UpdateWrapper<ConDocBuTypeGroupPo>().lambda().set(ConDocBuTypeGroupPo::getStatus ,conDocBuTypeGroupPo.getStatus() )
                    .in(ConDocBuTypeGroupPo::getSid,sids));
            for(Long id:sids){
                conDocBuTypeGroupPo.setSid(id);
                row=conDocBuTypeGroupPoMapper.updateById( conDocBuTypeGroupPo);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.status(conDocBuTypeGroupPo.getSid(), conDocBuTypeGroupPo.getStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocBuTypeGroupPo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ConDocBuTypeGroupPo conDocBuTypeGroupPo){
        int row=0;
        Long[] sids=conDocBuTypeGroupPo.getSidList();
        if(sids!=null&&sids.length>0){
            //校验
            for (Long sid : sids) {
                ConDocBuTypeGroupPo item = conDocBuTypeGroupPoMapper.selectById(sid);
                setConfirm(item);
            }
            row=conDocBuTypeGroupPoMapper.update(null,new UpdateWrapper<ConDocBuTypeGroupPo>().lambda().set(ConDocBuTypeGroupPo::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConDocBuTypeGroupPo::getSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbDeal.check(conDocBuTypeGroupPo.getSid(), conDocBuTypeGroupPo.getHandleStatus(), msgList,TITLE, null);
            }
        }
        return row;
    }

    /**
     * 校验
     * @param conDocBuTypeGroupSo
     * @return
     */
    private void setConfirm(ConDocBuTypeGroupPo conDocBuTypeGroupPo){
        List<ConDocBuTypeGroupPo> list = new ArrayList<>();
        if (conDocBuTypeGroupPo.getSid() == null){
            list = conDocBuTypeGroupPoMapper.selectList(new QueryWrapper<ConDocBuTypeGroupPo>().lambda()
                    .eq(ConDocBuTypeGroupPo::getDocTypeCode,conDocBuTypeGroupPo.getDocTypeCode())
                    .eq(ConDocBuTypeGroupPo::getBuTypeCode,conDocBuTypeGroupPo.getBuTypeCode()));
        }else {
            list = conDocBuTypeGroupPoMapper.selectList(new QueryWrapper<ConDocBuTypeGroupPo>().lambda()
                    .eq(ConDocBuTypeGroupPo::getDocTypeCode,conDocBuTypeGroupPo.getDocTypeCode())
                    .eq(ConDocBuTypeGroupPo::getBuTypeCode,conDocBuTypeGroupPo.getBuTypeCode())
                    .ne(ConDocBuTypeGroupPo::getSid,conDocBuTypeGroupPo.getSid()));
        }
        if (CollectionUtils.isNotEmpty(list)){
            throw new CustomException("已存在该单据类型和业务类型组合");
        }
        if (ConstantsEms.CHECK_STATUS.equals(conDocBuTypeGroupPo.getHandleStatus())){
            conDocBuTypeGroupPo.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
    }


}
