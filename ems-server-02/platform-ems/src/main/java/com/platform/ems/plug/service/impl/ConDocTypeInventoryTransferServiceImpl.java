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
import com.platform.ems.plug.domain.ConDocTypeInventoryTransfer;
import com.platform.ems.plug.mapper.ConDocTypeInventoryTransferMapper;
import com.platform.ems.plug.service.IConDocTypeInventoryTransferService;
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
 * 单据类型_调拨单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeInventoryTransferServiceImpl extends ServiceImpl<ConDocTypeInventoryTransferMapper,ConDocTypeInventoryTransfer>  implements IConDocTypeInventoryTransferService {
    @Autowired
    private ConDocTypeInventoryTransferMapper conDocTypeInventoryTransferMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类型_调拨单";
    /**
     * 查询单据类型_调拨单
     *
     * @param sid 单据类型_调拨单ID
     * @return 单据类型_调拨单
     */
    @Override
    public ConDocTypeInventoryTransfer selectConDocTypeInventoryTransferById(Long sid) {
        ConDocTypeInventoryTransfer conDocTypeInventoryTransfer = conDocTypeInventoryTransferMapper.selectConDocTypeInventoryTransferById(sid);
        MongodbUtil.find(conDocTypeInventoryTransfer);
        return  conDocTypeInventoryTransfer;
    }

    /**
     * 查询单据类型_调拨单列表
     *
     * @param conDocTypeInventoryTransfer 单据类型_调拨单
     * @return 单据类型_调拨单
     */
    @Override
    public List<ConDocTypeInventoryTransfer> selectConDocTypeInventoryTransferList(ConDocTypeInventoryTransfer conDocTypeInventoryTransfer) {
        return conDocTypeInventoryTransferMapper.selectConDocTypeInventoryTransferList(conDocTypeInventoryTransfer);
    }

    /**
     * 新增单据类型_调拨单
     * 需要注意编码重复校验
     * @param conDocTypeInventoryTransfer 单据类型_调拨单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeInventoryTransfer(ConDocTypeInventoryTransfer conDocTypeInventoryTransfer) {
        List<ConDocTypeInventoryTransfer> codeList = conDocTypeInventoryTransferMapper.selectList(new QueryWrapper<ConDocTypeInventoryTransfer>().lambda()
                .eq(ConDocTypeInventoryTransfer::getCode, conDocTypeInventoryTransfer.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeInventoryTransfer> nameList = conDocTypeInventoryTransferMapper.selectList(new QueryWrapper<ConDocTypeInventoryTransfer>().lambda()
                .eq(ConDocTypeInventoryTransfer::getName, conDocTypeInventoryTransfer.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDocTypeInventoryTransferMapper.insert(conDocTypeInventoryTransfer);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeInventoryTransfer.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改单据类型_调拨单
     *
     * @param conDocTypeInventoryTransfer 单据类型_调拨单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeInventoryTransfer(ConDocTypeInventoryTransfer conDocTypeInventoryTransfer) {
        ConDocTypeInventoryTransfer response = conDocTypeInventoryTransferMapper.selectConDocTypeInventoryTransferById(conDocTypeInventoryTransfer.getSid());
        int row=conDocTypeInventoryTransferMapper.updateById(conDocTypeInventoryTransfer);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeInventoryTransfer.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypeInventoryTransfer,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_调拨单
     *
     * @param conDocTypeInventoryTransfer 单据类型_调拨单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeInventoryTransfer(ConDocTypeInventoryTransfer conDocTypeInventoryTransfer) {
        List<ConDocTypeInventoryTransfer> nameList = conDocTypeInventoryTransferMapper.selectList(new QueryWrapper<ConDocTypeInventoryTransfer>().lambda()
                .eq(ConDocTypeInventoryTransfer::getName, conDocTypeInventoryTransfer.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeInventoryTransfer.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeInventoryTransfer.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypeInventoryTransfer response = conDocTypeInventoryTransferMapper.selectConDocTypeInventoryTransferById(conDocTypeInventoryTransfer.getSid());
        int row = conDocTypeInventoryTransferMapper.updateAllById(conDocTypeInventoryTransfer);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeInventoryTransfer.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeInventoryTransfer, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_调拨单
     *
     * @param sids 需要删除的单据类型_调拨单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeInventoryTransferByIds(List<Long> sids) {
        return conDocTypeInventoryTransferMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypeInventoryTransfer
    * @return
    */
    @Override
    public int changeStatus(ConDocTypeInventoryTransfer conDocTypeInventoryTransfer){
        int row=0;
        Long[] sids=conDocTypeInventoryTransfer.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeInventoryTransfer.setSid(id);
                row=conDocTypeInventoryTransferMapper.updateById( conDocTypeInventoryTransfer);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeInventoryTransfer.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeInventoryTransfer.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeInventoryTransfer
     * @return
     */
    @Override
    public int check(ConDocTypeInventoryTransfer conDocTypeInventoryTransfer){
        int row=0;
        Long[] sids=conDocTypeInventoryTransfer.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeInventoryTransfer.setSid(id);
                row=conDocTypeInventoryTransferMapper.updateById( conDocTypeInventoryTransfer);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeInventoryTransfer.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
