package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConPurchaseGroup;
import com.platform.ems.plug.mapper.ConPurchaseGroupMapper;
import com.platform.ems.plug.service.IConPurchaseGroupService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 采购组Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConPurchaseGroupServiceImpl extends ServiceImpl<ConPurchaseGroupMapper,ConPurchaseGroup>  implements IConPurchaseGroupService {
    @Autowired
    private ConPurchaseGroupMapper conPurchaseGroupMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "采购组";
    /**
     * 查询采购组
     *
     * @param sid 采购组ID
     * @return 采购组
     */
    @Override
    public ConPurchaseGroup selectConPurchaseGroupById(Long sid) {
        ConPurchaseGroup conPurchaseGroup = conPurchaseGroupMapper.selectConPurchaseGroupById(sid);
        MongodbUtil.find(conPurchaseGroup);
        return  conPurchaseGroup;
    }

    /**
     * 查询采购组列表
     *
     * @param conPurchaseGroup 采购组
     * @return 采购组
     */
    @Override
    public List<ConPurchaseGroup> selectConPurchaseGroupList(ConPurchaseGroup conPurchaseGroup) {
        return conPurchaseGroupMapper.selectConPurchaseGroupList(conPurchaseGroup);
    }

    /**
     * 新增采购组
     * 需要注意编码重复校验
     * @param conPurchaseGroup 采购组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConPurchaseGroup(ConPurchaseGroup conPurchaseGroup) {
        List<ConPurchaseGroup> codeList = conPurchaseGroupMapper.selectList(new QueryWrapper<ConPurchaseGroup>().lambda()
                .eq(ConPurchaseGroup::getCode, conPurchaseGroup.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConPurchaseGroup> nameList = conPurchaseGroupMapper.selectList(new QueryWrapper<ConPurchaseGroup>().lambda()
                .eq(ConPurchaseGroup::getName, conPurchaseGroup.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conPurchaseGroupMapper.insert(conPurchaseGroup);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conPurchaseGroup.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改采购组
     *
     * @param conPurchaseGroup 采购组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConPurchaseGroup(ConPurchaseGroup conPurchaseGroup) {
        ConPurchaseGroup response = conPurchaseGroupMapper.selectConPurchaseGroupById(conPurchaseGroup.getSid());
        int row=conPurchaseGroupMapper.updateById(conPurchaseGroup);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conPurchaseGroup.getSid(), BusinessType.UPDATE.getValue(), response,conPurchaseGroup,TITLE);
        }
        return row;
    }

    /**
     * 变更采购组
     *
     * @param conPurchaseGroup 采购组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConPurchaseGroup(ConPurchaseGroup conPurchaseGroup) {
        List<ConPurchaseGroup> nameList = conPurchaseGroupMapper.selectList(new QueryWrapper<ConPurchaseGroup>().lambda()
                .eq(ConPurchaseGroup::getName, conPurchaseGroup.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conPurchaseGroup.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conPurchaseGroup.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConPurchaseGroup response = conPurchaseGroupMapper.selectConPurchaseGroupById(conPurchaseGroup.getSid());
        int row = conPurchaseGroupMapper.updateAllById(conPurchaseGroup);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conPurchaseGroup.getSid(), BusinessType.CHANGE.getValue(), response, conPurchaseGroup, TITLE);
        }
        return row;
    }

    /**
     * 批量删除采购组
     *
     * @param sids 需要删除的采购组ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConPurchaseGroupByIds(List<Long> sids) {
        return conPurchaseGroupMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conPurchaseGroup
    * @return
    */
    @Override
    public int changeStatus(ConPurchaseGroup conPurchaseGroup){
        int row=0;
        Long[] sids=conPurchaseGroup.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conPurchaseGroup.setSid(id);
                row=conPurchaseGroupMapper.updateById( conPurchaseGroup);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conPurchaseGroup.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conPurchaseGroup.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conPurchaseGroup
     * @return
     */
    @Override
    public int check(ConPurchaseGroup conPurchaseGroup){
        int row=0;
        Long[] sids=conPurchaseGroup.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conPurchaseGroup.setSid(id);
                row=conPurchaseGroupMapper.updateById( conPurchaseGroup);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conPurchaseGroup.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 采购组下拉框
     */
    @Override
    public List<ConPurchaseGroup> getList() {
        return conPurchaseGroupMapper.getPurchaseGroupList(new ConPurchaseGroup().setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS));
    }

    /**
     * 采购组下拉框
     */
    @Override
    public List<ConPurchaseGroup> getPurchaseGroupList(ConPurchaseGroup conPurchaseGroup) {
        conPurchaseGroup.setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS);
        return conPurchaseGroupMapper.getPurchaseGroupList(conPurchaseGroup);
    }
}
