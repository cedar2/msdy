package com.platform.ems.plug.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.util.MongodbDeal;
import io.swagger.annotations.Api;
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
import com.platform.ems.plug.mapper.ConDocBuTypeGroupSoMapper;
import com.platform.ems.plug.domain.ConDocBuTypeGroupSo;
import com.platform.ems.plug.service.IConDocBuTypeGroupSoService;

/**
 * 销售订单单据类型与业务类型组合关系Service业务层处理
 *
 * @author chenkw
 * @date 2021-12-24
 */
@Service
@SuppressWarnings("all")
public class ConDocBuTypeGroupSoServiceImpl extends ServiceImpl<ConDocBuTypeGroupSoMapper,ConDocBuTypeGroupSo>  implements IConDocBuTypeGroupSoService {
    @Autowired
    private ConDocBuTypeGroupSoMapper conDocBuTypeGroupSoMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "销售订单单据类型与业务类型组合关系";
    /**
     * 查询销售订单单据类型与业务类型组合关系
     *
     * @param sid 销售订单单据类型与业务类型组合关系ID
     * @return 销售订单单据类型与业务类型组合关系
     */
    @Override
    public ConDocBuTypeGroupSo selectConDocBuTypeGroupSoById(Long sid) {
        ConDocBuTypeGroupSo conDocBuTypeGroupSo = conDocBuTypeGroupSoMapper.selectConDocBuTypeGroupSoById(sid);
        MongodbUtil.find(conDocBuTypeGroupSo);
        return  conDocBuTypeGroupSo;
    }

    /**
     * 查询销售订单单据类型与业务类型组合关系列表
     *
     * @param conDocBuTypeGroupSo 销售订单单据类型与业务类型组合关系
     * @return 销售订单单据类型与业务类型组合关系
     */
    @Override
    public List<ConDocBuTypeGroupSo> selectConDocBuTypeGroupSoList(ConDocBuTypeGroupSo conDocBuTypeGroupSo) {
        return conDocBuTypeGroupSoMapper.selectConDocBuTypeGroupSoList(conDocBuTypeGroupSo);
    }

    /**
     * 新增销售订单单据类型与业务类型组合关系
     * 需要注意编码重复校验
     * @param conDocBuTypeGroupSo 销售订单单据类型与业务类型组合关系
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocBuTypeGroupSo(ConDocBuTypeGroupSo conDocBuTypeGroupSo) {
        //校验
        setConfirm(conDocBuTypeGroupSo);
        int row= conDocBuTypeGroupSoMapper.insert(conDocBuTypeGroupSo);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbDeal.insert(conDocBuTypeGroupSo.getSid(), conDocBuTypeGroupSo.getHandleStatus(), msgList,TITLE,null);
        }
        return row;
    }

    /**
     * 修改销售订单单据类型与业务类型组合关系
     *
     * @param conDocBuTypeGroupSo 销售订单单据类型与业务类型组合关系
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocBuTypeGroupSo(ConDocBuTypeGroupSo conDocBuTypeGroupSo) {
        //校验
        setConfirm(conDocBuTypeGroupSo);
        ConDocBuTypeGroupSo response = conDocBuTypeGroupSoMapper.selectConDocBuTypeGroupSoById(conDocBuTypeGroupSo.getSid());
        int row=conDocBuTypeGroupSoMapper.updateById(conDocBuTypeGroupSo);
        if(row>0){
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(conDocBuTypeGroupSo, response);
            MongodbDeal.update(conDocBuTypeGroupSo.getSid(), response.getHandleStatus(), conDocBuTypeGroupSo.getHandleStatus(), msgList, TITLE,null);
        }
        return row;
    }

    /**
     * 变更销售订单单据类型与业务类型组合关系
     *
     * @param conDocBuTypeGroupSo 销售订单单据类型与业务类型组合关系
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocBuTypeGroupSo(ConDocBuTypeGroupSo conDocBuTypeGroupSo) {
        ConDocBuTypeGroupSo response = conDocBuTypeGroupSoMapper.selectConDocBuTypeGroupSoById(conDocBuTypeGroupSo.getSid());
        //校验
        setConfirm(conDocBuTypeGroupSo);
        int row=conDocBuTypeGroupSoMapper.updateAllById(conDocBuTypeGroupSo);
        if(row>0){
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(conDocBuTypeGroupSo, response);
            MongodbDeal.update(conDocBuTypeGroupSo.getSid(), conDocBuTypeGroupSo.getHandleStatus(),response.getHandleStatus(), msgList, TITLE,null);
        }
        return row;
    }

    /**
     * 批量删除销售订单单据类型与业务类型组合关系
     *
     * @param sids 需要删除的销售订单单据类型与业务类型组合关系ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocBuTypeGroupSoByIds(List<Long> sids) {
        int row = conDocBuTypeGroupSoMapper.deleteBatchIds(sids);
        if (row > 0){
            sids.forEach(sid->{
                ConDocBuTypeGroupSo item = conDocBuTypeGroupSoMapper.selectById(sid);
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
    * @param conDocBuTypeGroupSo
    * @return
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ConDocBuTypeGroupSo conDocBuTypeGroupSo){
        int row=0;
        Long[] sids=conDocBuTypeGroupSo.getSidList();
        if(sids!=null&&sids.length>0){
            row=conDocBuTypeGroupSoMapper.update(null, new UpdateWrapper<ConDocBuTypeGroupSo>().lambda().set(ConDocBuTypeGroupSo::getStatus ,conDocBuTypeGroupSo.getStatus() )
                    .in(ConDocBuTypeGroupSo::getSid,sids));
            for(Long id:sids){
                conDocBuTypeGroupSo.setSid(id);
                row=conDocBuTypeGroupSoMapper.updateById( conDocBuTypeGroupSo);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.status(conDocBuTypeGroupSo.getSid(), conDocBuTypeGroupSo.getStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocBuTypeGroupSo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ConDocBuTypeGroupSo conDocBuTypeGroupSo){
        int row=0;
        Long[] sids=conDocBuTypeGroupSo.getSidList();
        if(sids!=null&&sids.length>0){
            //校验
            for (Long sid : sids) {
                ConDocBuTypeGroupSo item = conDocBuTypeGroupSoMapper.selectById(sid);
                setConfirm(item);
            }
            row=conDocBuTypeGroupSoMapper.update(null,new UpdateWrapper<ConDocBuTypeGroupSo>().lambda().set(ConDocBuTypeGroupSo::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConDocBuTypeGroupSo::getSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbDeal.check(conDocBuTypeGroupSo.getSid(), conDocBuTypeGroupSo.getHandleStatus(), msgList,TITLE, null);
            }
        }
        return row;
    }

    /**
     * 校验
     * @param conDocBuTypeGroupSo
     * @return
     */
    private void setConfirm(ConDocBuTypeGroupSo conDocBuTypeGroupSo){
        List<ConDocBuTypeGroupSo> list = new ArrayList<>();
        if (conDocBuTypeGroupSo.getSid() == null){
            list = conDocBuTypeGroupSoMapper.selectList(new QueryWrapper<ConDocBuTypeGroupSo>().lambda()
                    .eq(ConDocBuTypeGroupSo::getDocTypeCode,conDocBuTypeGroupSo.getDocTypeCode())
                    .eq(ConDocBuTypeGroupSo::getBuTypeCode,conDocBuTypeGroupSo.getBuTypeCode()));
        }else {
            list = conDocBuTypeGroupSoMapper.selectList(new QueryWrapper<ConDocBuTypeGroupSo>().lambda()
                    .eq(ConDocBuTypeGroupSo::getDocTypeCode,conDocBuTypeGroupSo.getDocTypeCode())
                    .eq(ConDocBuTypeGroupSo::getBuTypeCode,conDocBuTypeGroupSo.getBuTypeCode())
                    .ne(ConDocBuTypeGroupSo::getSid,conDocBuTypeGroupSo.getSid()));
        }
        if (CollectionUtils.isNotEmpty(list)){
            throw new CustomException("已存在该单据类型和业务类型组合");
        }
        if (ConstantsEms.CHECK_STATUS.equals(conDocBuTypeGroupSo.getHandleStatus())){
            conDocBuTypeGroupSo.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
    }

}
