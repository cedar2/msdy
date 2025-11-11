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
import com.platform.ems.plug.domain.ConBuTypeInventoryTransfer;
import com.platform.ems.plug.mapper.ConBuTypeInventoryTransferMapper;
import com.platform.ems.plug.service.IConBuTypeInventoryTransferService;
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
 * 业务类型_调拨单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeInventoryTransferServiceImpl extends ServiceImpl<ConBuTypeInventoryTransferMapper,ConBuTypeInventoryTransfer>  implements IConBuTypeInventoryTransferService {
    @Autowired
    private ConBuTypeInventoryTransferMapper conBuTypeInventoryTransferMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_调拨单";
    /**
     * 查询业务类型_调拨单
     *
     * @param sid 业务类型_调拨单ID
     * @return 业务类型_调拨单
     */
    @Override
    public ConBuTypeInventoryTransfer selectConBuTypeInventoryTransferById(Long sid) {
        ConBuTypeInventoryTransfer conBuTypeInventoryTransfer = conBuTypeInventoryTransferMapper.selectConBuTypeInventoryTransferById(sid);
        MongodbUtil.find(conBuTypeInventoryTransfer);
        return  conBuTypeInventoryTransfer;
    }

    @Override
    public List<ConBuTypeInventoryTransfer> getList() {
    return conBuTypeInventoryTransferMapper.getList();

    }

    /**
     * 查询业务类型_调拨单列表
     *
     * @param conBuTypeInventoryTransfer 业务类型_调拨单
     * @return 业务类型_调拨单
     */
    @Override
    public List<ConBuTypeInventoryTransfer> selectConBuTypeInventoryTransferList(ConBuTypeInventoryTransfer conBuTypeInventoryTransfer) {
        return conBuTypeInventoryTransferMapper.selectConBuTypeInventoryTransferList(conBuTypeInventoryTransfer);
    }

    /**
     * 新增业务类型_调拨单
     * 需要注意编码重复校验
     * @param conBuTypeInventoryTransfer 业务类型_调拨单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeInventoryTransfer(ConBuTypeInventoryTransfer conBuTypeInventoryTransfer) {
        List<ConBuTypeInventoryTransfer> codeList = conBuTypeInventoryTransferMapper.selectList(new QueryWrapper<ConBuTypeInventoryTransfer>().lambda()
                .eq(ConBuTypeInventoryTransfer::getCode, conBuTypeInventoryTransfer.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeInventoryTransfer> nameList = conBuTypeInventoryTransferMapper.selectList(new QueryWrapper<ConBuTypeInventoryTransfer>().lambda()
                .eq(ConBuTypeInventoryTransfer::getName, conBuTypeInventoryTransfer.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeInventoryTransferMapper.insert(conBuTypeInventoryTransfer);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeInventoryTransfer.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_调拨单
     *
     * @param conBuTypeInventoryTransfer 业务类型_调拨单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeInventoryTransfer(ConBuTypeInventoryTransfer conBuTypeInventoryTransfer) {
        ConBuTypeInventoryTransfer response = conBuTypeInventoryTransferMapper.selectConBuTypeInventoryTransferById(conBuTypeInventoryTransfer.getSid());
        int row=conBuTypeInventoryTransferMapper.updateById(conBuTypeInventoryTransfer);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeInventoryTransfer.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeInventoryTransfer,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_调拨单
     *
     * @param conBuTypeInventoryTransfer 业务类型_调拨单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeInventoryTransfer(ConBuTypeInventoryTransfer conBuTypeInventoryTransfer) {
        List<ConBuTypeInventoryTransfer> nameList = conBuTypeInventoryTransferMapper.selectList(new QueryWrapper<ConBuTypeInventoryTransfer>().lambda()
                .eq(ConBuTypeInventoryTransfer::getName, conBuTypeInventoryTransfer.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeInventoryTransfer.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeInventoryTransfer.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeInventoryTransfer response = conBuTypeInventoryTransferMapper.selectConBuTypeInventoryTransferById(conBuTypeInventoryTransfer.getSid());
        int row = conBuTypeInventoryTransferMapper.updateAllById(conBuTypeInventoryTransfer);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeInventoryTransfer.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeInventoryTransfer, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_调拨单
     *
     * @param sids 需要删除的业务类型_调拨单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeInventoryTransferByIds(List<Long> sids) {
        return conBuTypeInventoryTransferMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeInventoryTransfer
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeInventoryTransfer conBuTypeInventoryTransfer){
        int row=0;
        Long[] sids=conBuTypeInventoryTransfer.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeInventoryTransfer.setSid(id);
                row=conBuTypeInventoryTransferMapper.updateById( conBuTypeInventoryTransfer);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeInventoryTransfer.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeInventoryTransfer.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeInventoryTransfer
     * @return
     */
    @Override
    public int check(ConBuTypeInventoryTransfer conBuTypeInventoryTransfer){
        int row=0;
        Long[] sids=conBuTypeInventoryTransfer.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeInventoryTransfer.setSid(id);
                row=conBuTypeInventoryTransferMapper.updateById( conBuTypeInventoryTransfer);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeInventoryTransfer.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
