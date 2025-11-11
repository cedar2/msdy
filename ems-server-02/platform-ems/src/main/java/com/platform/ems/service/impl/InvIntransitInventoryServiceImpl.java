package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.UserOperLog;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.platform.ems.mapper.InvIntransitInventoryMapper;
import com.platform.ems.domain.InvIntransitInventory;
import com.platform.ems.service.IInvIntransitInventoryService;

/**
 * 调拨在途库存Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-04
 */
@Service
@SuppressWarnings("all")
public class InvIntransitInventoryServiceImpl extends ServiceImpl<InvIntransitInventoryMapper,InvIntransitInventory>  implements IInvIntransitInventoryService {
    @Autowired
    private InvIntransitInventoryMapper invIntransitInventoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "调拨在途库存";
    /**
     * 查询调拨在途库存
     *
     * @param intransitStockSid 调拨在途库存ID
     * @return 调拨在途库存
     */
    @Override
    public InvIntransitInventory selectInvIntransitInventoryById(Long intransitStockSid) {
        InvIntransitInventory invIntransitInventory = invIntransitInventoryMapper.selectInvIntransitInventoryById(intransitStockSid);
        MongodbUtil.find(invIntransitInventory);
        return  invIntransitInventory;
    }

    /**
     * 查询调拨在途库存列表
     *
     * @param invIntransitInventory 调拨在途库存
     * @return 调拨在途库存
     */
    @Override
    public List<InvIntransitInventory> selectInvIntransitInventoryList(InvIntransitInventory invIntransitInventory) {
        return invIntransitInventoryMapper.selectInvIntransitInventoryList(invIntransitInventory);
    }

    /**
     * 新增调拨在途库存
     * 需要注意编码重复校验
     * @param invIntransitInventory 调拨在途库存
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvIntransitInventory(InvIntransitInventory invIntransitInventory) {
        int row= invIntransitInventoryMapper.insert(invIntransitInventory);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(invIntransitInventory.getIntransitStockSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改调拨在途库存
     *
     * @param invIntransitInventory 调拨在途库存
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvIntransitInventory(InvIntransitInventory invIntransitInventory) {
        InvIntransitInventory response = invIntransitInventoryMapper.selectInvIntransitInventoryById(invIntransitInventory.getIntransitStockSid());
        int row=invIntransitInventoryMapper.updateById(invIntransitInventory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invIntransitInventory.getIntransitStockSid(), BusinessType.UPDATE.ordinal(), response,invIntransitInventory,TITLE);
        }
        return row;
    }

    /**
     * 变更调拨在途库存
     *
     * @param invIntransitInventory 调拨在途库存
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeInvIntransitInventory(InvIntransitInventory invIntransitInventory) {
        InvIntransitInventory response = invIntransitInventoryMapper.selectInvIntransitInventoryById(invIntransitInventory.getIntransitStockSid());
                                                                                                                    int row=invIntransitInventoryMapper.updateAllById(invIntransitInventory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invIntransitInventory.getIntransitStockSid(), BusinessType.CHANGE.ordinal(), response,invIntransitInventory,TITLE);
        }
        return row;
    }

    /**
     * 批量删除调拨在途库存
     *
     * @param intransitStockSids 需要删除的调拨在途库存ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvIntransitInventoryByIds(List<Long> intransitStockSids) {
        return invIntransitInventoryMapper.deleteBatchIds(intransitStockSids);
    }

    /**
    * 启用/停用
    * @param invIntransitInventory
    * @return
    */
    @Override
    public int changeStatus(InvIntransitInventory invIntransitInventory){
        int row=0;
        Long[] sids=invIntransitInventory.getIntransitStockSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                invIntransitInventory.setIntransitStockSid(id);
                row=invIntransitInventoryMapper.updateById( invIntransitInventory);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(invIntransitInventory.getIntransitStockSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,null);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param invIntransitInventory
     * @return
     */
    @Override
    public int check(InvIntransitInventory invIntransitInventory){
        int row=0;
        Long[] sids=invIntransitInventory.getIntransitStockSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                invIntransitInventory.setIntransitStockSid(id);
                row=invIntransitInventoryMapper.updateById( invIntransitInventory);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(invIntransitInventory.getIntransitStockSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
